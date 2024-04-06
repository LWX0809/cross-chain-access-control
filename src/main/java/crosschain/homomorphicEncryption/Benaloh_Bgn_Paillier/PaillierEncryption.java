package crosschain.homomorphicEncryption.Benaloh_Bgn_Paillier;

import java.math.BigInteger;
import java.security.SecureRandom;

public class PaillierEncryption {

    private static final int KEY_SIZE = 512;
    private static final BigInteger ONE = BigInteger.ONE;

    private BigInteger n; // 公共模数
    private BigInteger g; // 生成元
    private BigInteger lambda; // λ = lcm(p-1, q-1)

    public PaillierEncryption() {
        // 生成公私钥对
        KeyPair keyPair = generateKeyPair();
        n = keyPair.getN();
        g = keyPair.getG();
        lambda = keyPair.getLambda();
    }

    public BigInteger encrypt(BigInteger plaintext, BigInteger publicKey) {
        // 加密操作：密文 = (g^plaintext * r^n) mod n^2
        BigInteger r = generateRandomR();
        BigInteger ciphertext = g.modPow(plaintext, n.multiply(n)).multiply(r.modPow(n, n.multiply(n))).mod(n.multiply(n));
        return ciphertext;
    }

    public BigInteger decrypt(BigInteger ciphertext, BigInteger privateKey) {
        // 解密操作：明文 = (L(ciphertext^lambda mod n^2) / L(g^lambda mod n^2)) mod n
        BigInteger numerator = L(ciphertext.modPow(lambda, n.multiply(n)));
        BigInteger denominator = L(g.modPow(lambda, n.multiply(n)));
        BigInteger plaintext = numerator.multiply(denominator.modInverse(n)).mod(n);
        return plaintext;
    }

    private BigInteger L(BigInteger x) {
        return x.subtract(ONE).divide(n);
    }

    private KeyPair generateKeyPair() {
        SecureRandom random = new SecureRandom();
        BigInteger p = BigInteger.probablePrime(KEY_SIZE / 2, random);
        BigInteger q = BigInteger.probablePrime(KEY_SIZE / 2, random);
        BigInteger n = p.multiply(q);
        BigInteger lambda = lcm(p.subtract(ONE), q.subtract(ONE));
        BigInteger g = n.add(ONE);
        return new KeyPair(n, g, lambda);
    }

    private BigInteger lcm(BigInteger a, BigInteger b) {
        return a.multiply(b).divide(a.gcd(b));
    }

    private BigInteger generateRandomR() {
        SecureRandom random = new SecureRandom();
        BigInteger r;
        do {
            r = new BigInteger(KEY_SIZE, random);
        } while (r.compareTo(n) >= 0 || r.gcd(n).intValue() != 1);
        return r;
    }

    private class KeyPair {
        private BigInteger n;
        private BigInteger g;
        private BigInteger lambda;

        public KeyPair(BigInteger n, BigInteger g, BigInteger lambda) {
            this.n = n;
            this.g = g;
            this.lambda = lambda;
        }

        public BigInteger getN() {
            return n;
        }

        public BigInteger getG() {
            return g;
        }

        public BigInteger getLambda() {
            return lambda;
        }
    }

    public static void main(String[] args) {
        // 创建加密方A和加密方B
        PaillierEncryption encryptorA = new PaillierEncryption();
        KeyPair keyPairA=encryptorA.generateKeyPair();
        PaillierEncryption encryptorB = new PaillierEncryption();
        KeyPair keyPairB=encryptorB.generateKeyPair();

        // 明文数据
        BigInteger plaintext = new BigInteger("42");

        // 加密方A对明文进行加密
        BigInteger ciphertextA = encryptorA.encrypt(plaintext, keyPairA.getN());

        // 加密方B对明文进行加密
        BigInteger ciphertextB = encryptorB.encrypt(plaintext, keyPairB.getN());

        // 加密方A和加密方B进行同态加法
        BigInteger sumCiphertext = ciphertextA.multiply(ciphertextB).mod(keyPairA.getN().multiply(keyPairA.getN()));

        // 加密方A解密密文结果
        BigInteger decryptedSum = encryptorA.decrypt(sumCiphertext, keyPairA.getLambda());

        System.out.println("解密结果：" + decryptedSum); // 输出结果为明文的两倍，即84
    }
}
