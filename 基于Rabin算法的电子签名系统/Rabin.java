import java.math.BigInteger;//大整数类
import java.util.Scanner;
public class Rabin {
    //0,1,10是BigInteger类的基本常量
    static BigInteger zero = BigInteger.ZERO;static BigInteger one = BigInteger.ONE;
    //一些常用大数的初始化
    static BigInteger two  = new BigInteger("2");static BigInteger three= new BigInteger("3");
    static BigInteger four = new BigInteger("4");static BigInteger five = new BigInteger("5");
    static BigInteger six  = new BigInteger("6");static BigInteger seven= new BigInteger("7");
    static BigInteger eight= new BigInteger("8");static BigInteger fuyi = new BigInteger("-1");
    //初始化定义两个大素数p和q，公钥n，私钥d
    static BigInteger p,q,n,d;
    //当m=-1时，计算Legendre符号值调用此方法
    public static int neg_one(BigInteger m) {   //当m=-1时，Legendre符号值为-1的（m-1）/2次方，由于mod 2=1的数字可以让值为+1或-1，无法准确界定，所以将m mod 4
        if( m.mod(four).compareTo(one) == 0)  //当m mod 4=1时，（m-1）/2的值必为一个偶数，所以返回值为1
            return 1;
        else if( m.mod(four).compareTo(three) == 0)  //当m mod 4=3时，（m-1）/2的值必为一个奇数，所以返回值为-1
            return -1;
        else
            return 0;
    }
    //当m为偶数时，计算Legendre符号值调用此方法
    public static int two(BigInteger m) {
        BigInteger tmp = m.mod(eight);  //当m=2时，Legendre符号值为-1的（m*m-1）/8次方，
        if(tmp.compareTo(one)==0 || tmp.compareTo(seven)==0)  //当m mod 8=1或7时，（m*m-1）/8的值必为一个偶数，所以返回值为1
            return 1;
        else if(tmp.compareTo(three)==0 || tmp.compareTo(five)==0)   //当m mod 8=3或5时，（m*m-1）/8的值必为一个奇数，所以返回值为-1
            return -1;
        else
            return 0;
    }
    //定义二次剩余的Legendre符号
    public static int legendre(BigInteger p,BigInteger m) {
        int mul = 1;BigInteger tmp;
        if(m.mod(p).compareTo(zero) == 0)  //判断m是否能整除p
        {
            System.out.print("错误参数");
            return 0;
        }
        while(true) {
            if(m.compareTo(p) > 0)  //只要m比p大，则将m mod p
                m = m.mod(p);
            while(m.mod(two).compareTo(zero) == 0) {  //只要是偶数就把m除2，直到m变成奇数或2
                m = m.divide(two);
                mul *= two(p);//调用two这个方法，Legendre符号mul初值为1，每次循环乘以1或-1，具体根据看满足two方法中的哪个条件
            }
            if( m.compareTo(one) == 0)
                return mul;  //m=1时，Legendre符号值=1
            else if( m.compareTo(fuyi) == 0) {
                mul *= neg_one(p);  //m=-1时，调用neg_one方法计算Legendre符号的值
                return mul;
            }
            //交换p和m的值
            tmp = p;p = m;m = tmp;
            //二次互反率
            if(p.subtract(one).multiply(m.subtract(one)).mod(eight).compareTo(zero) != 0)
                mul *= -1;
        }
    }
    //定义二次剩余的Jacobi符号
    public static int Jacobi(BigInteger m) {
        return legendre(p,m)*legendre(q,m);//Jacobi符号值为两个互素的大整数分别mod m的Legendre符号值相乘
    }
    //生成密钥
    public static void RabinKey_init(int len) {
        while(true) {
            BigInteger num = BigInteger.ZERO;
            for(int i=1;i<len-2;i++)
                num = num.multiply(two).add(Math.random()>0.5?zero:one);//Math.random()是在0-1中随机取一个double类型的随机值
            num = num.multiply(eight).add(three);
            if(num.isProbablePrime(10)) {//判断这个大整数是否为素数，是素数就把num的值赋给p
                p = num;
                break;  //p得到数值后跳出循环
            }
        }
        while(true) {
            BigInteger num = BigInteger.ZERO;
            for(int i=1;i<len-2;i++)
                num = num.multiply(two).add(Math.random()>0.5?zero:one);
            num = num.multiply(eight).add(seven);
            if(num.isProbablePrime(10)) {//判断这个大整数是否为素数，是素数就把num的值赋给q
                q = num;
                break;  //q得到数值后跳出循环
            }
        }
        //用计算得出的p，q的值计算出公钥和私钥
        n = p.multiply(q);
        d = n.subtract(p).subtract(q).add(five).divide(eight);
    }
    //私钥签名
    public static BigInteger sign(BigInteger m) {
        BigInteger m1 = m.multiply(BigInteger.valueOf(16)).add(BigInteger.valueOf(6));  //当参数m不符合条件时，将其映射成条件范围内的一个值m1
        int j = Jacobi(m1);//计算其雅可比符号值
        BigInteger s = zero;//初始化签名的值
        //根据不同的Jacobi符号值，套用不同的公式进行签名
        if(j == -1)
            s = Momisf.fun(m1.divide(two), d, n);  //调用Momisf类里面的fun函数计算签名的值
        else if(j == 1)
            s = Momisf.fun(m1, d, n);
        return s;
    }
    //公钥验证签名
    public static BigInteger check(BigInteger s) {
        BigInteger m1 = s.multiply(s).mod(n);//签名值平方mod n的剩余
        BigInteger m = zero;
        //根据m1 mod8的不同剩余，计算m的值
        if(m1.mod(eight).compareTo(six) == 0 )
            m = m1;
        else if(m1.mod(eight).compareTo(three) == 0)
            m = m1.multiply(two);
        else if(m1.mod(eight).compareTo(seven) == 0)
            m = n.subtract(m1);
        else if(m1.mod(eight).compareTo(two) == 0)
            m = two.multiply(n.subtract(m1));
        return m.subtract(six).divide(BigInteger.valueOf(16));//返回经冗余函数映射后的值
    }
    public static void main(String args[]) {
        Scanner in =new Scanner(System.in);
        System.out.print("请输入需要的密钥位数：\n");
        RabinKey_init(in.nextInt());
        System.out.println("自动生成公私钥：");
        System.out.println("公钥n为"+n+"\n私钥（p，q)为("+p+","+q+")\n请输入需要的签名的消息(仅支持数字)：");
        BigInteger m = in.nextBigInteger();
        BigInteger s = sign(m);
        System.out.println("私钥签名s为"+s);
        if(m.compareTo(check(s)) == 0)
            System.out.println("公钥校验成功");
        else
            System.out.println("签名校验失败");
        in.close();
    }
}
