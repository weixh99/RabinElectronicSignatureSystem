import java.math.BigInteger;
import java.util.Scanner;
public class Momisf {
        /*快速模幂算法*/
        static public BigInteger fun(BigInteger x, BigInteger n, BigInteger m) {
            BigInteger d = new BigInteger("1");
            BigInteger nn = new BigInteger("0");
            BigInteger mm = new BigInteger("2");//mm其实就是2对应的大数
            BigInteger aa = new BigInteger("1");//mm其实就是1对应的大数
            /*对算法的实现，非递归*/
            while (n.compareTo(nn) > 0) {
                if (aa.compareTo(n.mod(mm)) == 0) {
                    d = (d.multiply(x)).mod(m);
                    n = (n.subtract(aa)).divide(mm);
                } else {
                    n = n.divide(mm);
                }
                x = (x.multiply(x)).mod(m);
            }
            return d;
    }
}
