package crosschain.homomorphicEncryption.Benaloh_Bgn_Paillier;
import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * @ClassName: Paillier
 * @Description: This is a sample java source code of Paillier
 * PKE.
 */
public class Paillier {
    /**
     * @ClassName: PublicKey
     * @Description: This is a class for storing the public
     * key (n, g) of Paillier PKE.
     */
    public class PublicKey {
        private BigInteger n, g;
        public PublicKey(BigInteger n, BigInteger g) {
            this.n = n;
            this.g = g;
        }
        public BigInteger getN() {
            return n;
        }
        public BigInteger getG() {
            return g;
        }
    }
    /**
     * @ClassName: PrivateKey
     * @Description: This is a class for storing the private
     * key (lambda, mu) of Paillier PKE.
     */
    public class PrivateKey {
        private BigInteger lambda, mu;
        public PrivateKey(BigInteger lambda, BigInteger mu) {
            this.lambda = lambda;
            this.mu = mu;
        }
        public BigInteger getLambda() {
            return lambda;
        }
        public BigInteger getMu() {
            return mu;
        }
    }
    private final int CERTAINTY = 64;
    private PublicKey pubkey; // The public key of Paillier PKE, (n, g)
    private PrivateKey prikey; // The private key of Paillier PKE, (lambda, mu)
    /**
     * @Title: getPubkey
     * @Description: This function returns the generated
     * public key.
     * @return PublicKey The public key used to encrypt
     * the data.
     */
    public PublicKey getPubkey() {
        return pubkey;
    }
    /**
     * @Title: getPrikey
     * @Description: This function returns the generated
     * private key.
     * @return PrivateKey The private key used to decrypt
     * the data.
     */
    public PrivateKey getPrikey() {
        return prikey;
    }
    /**
     * @Title: keyGeneration
     * @Description: This function is to help generate the
     * public key and
     * private key for encryption and decryption.
     * @param k
     * k is the security parameter, which decides
     * the length of two large primes (p and q).
     * @return void
     */
    public void keyGeneration(int k) {
        BigInteger p_prime, q_prime, p, q;
        do {
            p_prime = new BigInteger(k, CERTAINTY,
                    new SecureRandom());
            p = (p_prime.multiply(BigInteger.valueOf(2)))
                    .add(BigInteger.ONE);
        } while (!p.isProbablePrime(CERTAINTY));
        do {
            do {
                q_prime = new BigInteger(k, CERTAINTY,new SecureRandom());
            } while (p_prime.compareTo(q_prime) == 0);
            q = (q_prime.multiply(BigInteger.valueOf(2)))
                    .add(BigInteger.ONE);
        } while (!q.isProbablePrime(CERTAINTY));
// The following steps are to generate the keys
// n=p*q
        BigInteger n = p.multiply(q);
// nsquare=n^2
        BigInteger nsquare = n.pow(2);
// a generator g=(1+n) in Z*_(n^2)
        BigInteger g = BigInteger.ONE.add(n);
// lambda = lcm(p-1, q-1) = p_prime*q_prime
        BigInteger lambda = BigInteger.valueOf(2)
                .multiply(p_prime)
                .multiply(q_prime);
// mu = (L(g^lambda mod n^2))^{-1} mod n
        BigInteger mu = Lfunction(g.modPow(lambda, nsquare), n)
                .modInverse(n);
        pubkey = new PublicKey(n, g);
        prikey = new PrivateKey(lambda, mu);
    }


    /**
     * @Title: encrypt
     * @Description: This function is to encrypt the message
     * with Paillier’s public key.
     * @param m
     * The message.
     * @param pubkey
     * The public key of Paillier PKE.
     * @return BigInteger The ciphertext.
     * @throws Exception
     * If the message is not in Z*_n, there is
     * an exception.
     */
    public static BigInteger encrypt(BigInteger m,
                                     PublicKey pubkey) throws Exception {
        BigInteger n = pubkey.getN();
        BigInteger nsquare = n.pow(2);
        BigInteger g = pubkey.getG();
        if (!belongToZStarN(m, n)) {
            throw new Exception(
                    "Paillier.encrypt(BigInteger m, PublicKey pubkey): plaintext m is not in Z*_n");
        }
        BigInteger r = randomZStarN(n);
        return (g.modPow(m, nsquare).multiply(r.modPow(n,
                nsquare))).mod(nsquare);
    }/**
     * @Title: decrypt
     * @Description: This function is to decrypt the ciphertext
     * with the public key and the private key.
     * @param c
     * The ciphertext.
     * @param pubkey
     * The public key of Paillier PKE.
     * @param prikey
     * The private key of Paillier PKE.
     * @return BigInteger The plaintext.
     * @throws Exception
     * If the cipher is not in Z*_(n^2), there is
     * an exception.
     */
    public static BigInteger decrypt(BigInteger c, PublicKey
            pubkey, PrivateKey prikey) throws Exception {
        BigInteger n = pubkey.getN();
        BigInteger nsquare = n.pow(2);
        BigInteger lambda = prikey.getLambda();
        BigInteger mu = prikey.getMu();
        if (!belongToZStarNSquare(c, nsquare)) {
            throw new Exception(
                    "Paillier.decrypt(BigInteger c, PrivateKey prikey): ciphertext c is not in Z*_(n^2)");
        }
        return Lfunction(c.modPow(lambda, nsquare), n).
                multiply(mu).mod(n);
    }
    /**
     * @Title: add
     * @Description: The function supports the homomorphic
     * addition with two ciphertext.
     * @param c1
     * The ciphertext.
     * @param c2
     * The ciphertext.
     * @param pubkey
     * The public key of Paillier PKE.
     * @return BigInteger The return value is c1*c2 mod n^2.
     */
    public static BigInteger add(BigInteger c1, BigInteger c2,
                                 PublicKey pubkey) {
             BigInteger nsquare = pubkey.getN().pow(2);
             return c1.multiply(c2).mod(nsquare);
    }

    // 密文相除 等于原文相减
    public static BigInteger sub(BigInteger ciphertext1, BigInteger ciphertext2,PublicKey pubkey) {
        BigInteger nsquare = pubkey.getN().pow(2);
        BigInteger ciphertext2Inverse = ciphertext2.modInverse(nsquare);
        BigInteger divisionResult = ciphertext1.multiply(ciphertext2Inverse).mod(nsquare);
        return divisionResult;
    }
    /**
     * @Title: mul
     * @Description: The function supports the homomorphic
     * multiplication with one ciphertext and one plaintext.
     * @param c
     * The ciphertext.
     * @param m
     * The plaintext.
     * @param pubkey
     * The public key of Paillier PKE.
     * @return BigInteger The return value is c^m mod n^2.
     */
    public static BigInteger mul(BigInteger c, BigInteger m,
                                 PublicKey pubkey) {BigInteger nsquare =
            pubkey.getN().pow(2);
        return c.modPow(m, nsquare);
    }

    // 同态加密指数运算
    public static BigInteger homomorphicExponentiation(BigInteger ciphertext, BigInteger exponent,PublicKey pubkey,PrivateKey prikey) throws Exception {

        BigInteger n = pubkey.getN();
        BigInteger nsquare = n.pow(2);
        BigInteger g = pubkey.getG();
        BigInteger c1 = ciphertext.modPow(exponent, nsquare);
        BigInteger c2 = encrypt(g.modPow(exponent, n),pubkey);
        BigInteger c3 = ciphertext.multiply(c2).mod(nsquare);

        BigInteger c4 = c1.multiply(c3).mod(nsquare);
        BigInteger c5 = decrypt(c4,pubkey,prikey);
        return c5;
    }

    /**
     * @Title: selfBlind
     * @Description: The function supports the homomorphic
     * self-blinding with one ciphertext and one random number.
     * @param c
     * The ciphertext.
     * @param r
     * A random number in Z*_n.
     * @param pubkey
     * The public key of Paillier PKE.
     * @return BigInteger The return value is c*r^n mod n^2.
     */
    public static BigInteger selfBlind(BigInteger c,
                                       BigInteger r, PublicKey pubkey) {
        BigInteger n = pubkey.getN();
        BigInteger nsquare = n.pow(2);
        return c.multiply(r.modPow(n, nsquare)).mod(nsquare);
    }
/**
 * @Title: Lfunction
 * @Description: This function is the L function which is
 * defined by Paillier PKE, L(mu)=(mu-1)/n.
 * @param mu
 * The input parameter.
 * @param n
 * n=p*q.
 * @return BigInteger The return value is (mu-1)/n.
 */
private static BigInteger Lfunction(BigInteger mu,
                                    BigInteger n) {return
        mu.subtract(BigInteger.ONE).divide(n);
}
    /**
     * @Title: randomZStarN
     * @Description: This function returns a ramdom number in
     * Z*_n.
     * @param n
     * n=p*q.
     * @return BigInteger A random number in Z*_n.
     */
    public static BigInteger randomZStarN(BigInteger n) {
        BigInteger r;
        do {
            r = new BigInteger(n.bitLength(), new
                    SecureRandom());
        } while (r.compareTo(n) >= 0 || r.gcd(n).intValue()
                != 1);
        return r;
    }
    /**
     *
     * @Title: belongToZStarN
     * @Description: This function is to test whether the
     * plaintext is in Z*_n.
     * @param m
     * The plaintext.
     * @param n
     * n=p*q.
     * @return boolean If it is true, the plaintext is Z*_n,
     * otherwise, not.
     */
    private static boolean belongToZStarN(BigInteger m,
                                          BigInteger n) {
        if (m.compareTo(BigInteger.ZERO) < 0 ||
                m.compareTo(n) >= 0
                || m.gcd(n).intValue() != 1) {
            return false;
        }
        return true;
    }
/**
 *
 * @Title: belongToZStarNSquare
 * @Description: This function is to test whether the
 * ciphertext is in
 * Z*_(n^2).
 * @param c
 * The ciphertext.
 * @param nsquare
 * nsquare=n^2.
 * @return boolean If it is true, the ciphertext is
 * Z*_(n^2), otherwise, not.
 */
private static boolean belongToZStarNSquare(BigInteger c,
                                            BigInteger nsquare){
    if (c.compareTo(BigInteger.ZERO) < 0 ||
            c.compareTo(nsquare) >= 0
            || c.gcd(nsquare).intValue() != 1) {
        return false;
    }
    return true;
}



    public static void main(String[] args) throws Exception {
        Paillier paillier = new Paillier();
// KeyGeneration
        paillier.keyGeneration(512);
        PublicKey pubkey = paillier.getPubkey();
        PrivateKey prikey = paillier.getPrikey();
// Encryption and Decryption
        BigInteger m = new BigInteger(new String("Hello").getBytes());
        BigInteger c = null;
        BigInteger decrypted_m = null;
        try {
            c = Paillier.encrypt(m, pubkey);
            decrypted_m = Paillier.decrypt(c, pubkey, prikey);
        } catch (Exception e) {
// TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (decrypted_m.compareTo(m) == 0) {
            System.out.println("m:"+m);
            System.out.println("Encryption and Decryption test successfully.");
        }
// Homomorphic Properties
// Addition
        BigInteger m1 = new BigInteger("12345");
        BigInteger m2 = new BigInteger("56789");
        BigInteger m1plusm2 = m1.add(m2);
        try {
            BigInteger c1 = Paillier.encrypt(m1, pubkey);
            BigInteger c2 = Paillier.encrypt(m2, pubkey);
            BigInteger c1mulc2 = Paillier.add(c1, c2, pubkey);
            BigInteger decrypted_c1mulc2 =
                    Paillier.decrypt(c1mulc2, pubkey, prikey);
            if (decrypted_c1mulc2.compareTo(m1plusm2) == 0) {
                System.out.println("c1:"+c1);
                System.out.println("Homomorphic addition tests successfully.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
// Multiplication
        m1 = new BigInteger("12345");
        m2 = new BigInteger("56789");
        BigInteger m1mulm2 = m1.multiply(m2);
        try {
            BigInteger c1 = Paillier.encrypt(m1, pubkey);
            BigInteger c1expm2 = Paillier.mul(c1, m2, pubkey);
            BigInteger decrypted_c1expm2 =
                    Paillier.decrypt(c1expm2, pubkey, prikey);
            if (decrypted_c1expm2.compareTo(m1mulm2) == 0) {
                System.out.println("m1mulm2:"+m1mulm2);
                System.out.println("Homomorphic multiplication tests successfully.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
// Self-Blinding
        m1 = new BigInteger("12345");
        BigInteger r2 = Paillier.randomZStarN(pubkey.getN());
        try {
            BigInteger c1 = Paillier.encrypt(m1, pubkey);
            BigInteger c1mulrn = Paillier.selfBlind(c1, r2,
                    pubkey);
            BigInteger decrypted_c1mulrn =
                    Paillier.decrypt(c1mulrn, pubkey, prikey);
            if (decrypted_c1mulrn.compareTo(m1) == 0) {
                System.out.println("Homomorphic self-blinding tests successfully.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //test ok
        BigInteger plaintext1 = new BigInteger("9");
        BigInteger ciphertext1 = Paillier.encrypt(plaintext1,pubkey);

        Paillier paillier2 = new Paillier();
// KeyGeneration
        paillier2.keyGeneration(512);
        PublicKey pubkey2 = paillier2.getPubkey();
        PrivateKey prikey2 = paillier2.getPrikey();

        BigInteger plaintext2 = new BigInteger("10");
        BigInteger ciphertext2 = Paillier.encrypt(plaintext2,pubkey2);
        BigInteger c3=Paillier.add(ciphertext1,ciphertext2,pubkey);
        BigInteger m3=Paillier.decrypt(c3,pubkey,prikey);
        System.out.println("m3:"+m3);

        System.out.println(paillier2.compare(plaintext1,plaintext2));
//        //对于1
//        BigInteger m2_ = Paillier.decrypt(ciphertext2,pubkey,prikey);
//
//        //对于2
//        BigInteger m1_ = Paillier.decrypt(ciphertext1,pubkey2,prikey2);
//
//        System.out.println("m2_:"+m2_);
//        System.out.println("m1_:"+m1_);
//        BigInteger tesetadd=plaintext1.add(m2_);
//        System.out.println("test:"+tesetadd);
//        BigInteger testm=Paillier.decrypt(tesetadd,pubkey,prikey);
//        System.out.println("testm:"+testm);

}
    public int compare(BigInteger plaintext1, BigInteger plaintext2) throws Exception {

        BigInteger ciphertext1 = Paillier.encrypt(plaintext1,pubkey);
        BigInteger ciphertext2 = Paillier.encrypt(plaintext2,pubkey);

        //C_=C1/C2  M_=m1-m2
        BigInteger ciphertext_ = Paillier.sub(ciphertext1,ciphertext2,pubkey);

        //加法因子
        BigInteger plaintext3 = new BigInteger("15");
        BigInteger ciphertext3 = Paillier.encrypt(plaintext3,pubkey);

        //C__=C1/C2*C3  M__=M1-M2+M3   ,M3>M1 ,M3>M2 远大于 加法因子外部不知道
        BigInteger C__ = Paillier.add(ciphertext_,ciphertext3,pubkey);
        BigInteger M__ = Paillier.decrypt(C__,pubkey,prikey);

        int comparisonResult = M__.compareTo(plaintext3);
        if (comparisonResult > 0) {
            System.out.println("M1 > M2");
        } else if (comparisonResult < 0) {
            System.out.println("M1 < M2");
        } else {
            System.out.println("M1 = M2");
        }
        return comparisonResult;
    }

}