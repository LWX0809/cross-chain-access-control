
package crosschain.homomorphicEncryption.BGN;

import java.math.BigInteger;
import java.security.SecureRandom;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.PairingParameters;
import it.unisa.dia.gas.plaf.jpbc.pairing.a1.TypeA1CurveGenerator;
import it.unisa.dia.gas.plaf.jpbc.pairing.a1.TypeA1Pairing;
import it.unisa.dia.gas.plaf.jpbc.util.math.BigIntegerUtils;

public class BGNEncryption {

	public static final String start = "start";
	public static final String end = "end";
	private PairingParameters param;
	private BigInteger r;
	private BigInteger q; // This is the private key.
	private BigInteger order;
	private SecureRandom rng;

	public PublicKey gen(int bits) {
		rng = new SecureRandom();
		TypeA1CurveGenerator a1 = new TypeA1CurveGenerator(rng, 2, bits); // Requires
																			// 2
																			// prime
																			// numbers.
		param = a1.generate();
		TypeA1Pairing pairing = new TypeA1Pairing(param);
		order = param.getBigInteger("n"); // Must extract the prime numbers for
											// both keys.
		r = param.getBigInteger("n0");
		q = param.getBigInteger("n1");
		Field f = pairing.getG1();
		Element P = f.newRandomElement();
		P = P.mul(param.getBigInteger("l"));
		Element Q = f.newElement();
		Q = Q.set(P);
		Q = Q.mul(r);
		return new PublicKey(pairing, P, Q, order);
	}

	public Element encrypt(PublicKey PK, int msg) {
		BigInteger t = BigIntegerUtils.getRandom(PK.getN());
		int m = msg;
		System.out.println("Hash is " + m);
		Field f = PK.getField();
		Element A = f.newElement();
		Element B = f.newElement();
		Element C = f.newElement();
		A = A.set(PK.getP());
		A = A.mul(BigInteger.valueOf(m));
		B = B.set(PK.getQ());
		B = B.mul(t);
		C = C.set(A);
		C = C.add(B);
		return C;
	}

	public Element add(PublicKey PK, Element A, Element B) {
		BigInteger t = BigIntegerUtils.getRandom(PK.getN());
		Field f = PK.getField();
		Element output = f.newElement();
		Element aux = f.newElement();
		aux.set(PK.getQ());
		aux.mul(t);
		output.set(A);
		output.add(B);
		output.add(aux);
		return output;
	}

	public Element mul(PublicKey PK, Element C, Element D) {
		BigInteger t = BigIntegerUtils.getRandom(PK.getN());

		Element T = PK.doPairing(C, D);

		Element K = PK.doPairing(PK.getQ(), PK.getQ());
		K = K.pow(t);
		return T.mul(K);
	}

	public String decryptMul(PublicKey PK, BigInteger sk, Element C) {
		Element PSK = PK.doPairing(PK.getP(), PK.getP());
		PSK.pow(sk);

		Element CSK = C.duplicate();
		CSK.pow(sk);
		Element aux = PSK.duplicate();

		BigInteger m = new BigInteger("1");
		while (!aux.isEqual(CSK)) {
			aux = aux.mul(PSK);
			m = m.add(BigInteger.valueOf(1));
		}
		return m.toString();
	}

	public String decrypt(PublicKey PK, BigInteger sk, Element C) {
		Field f = PK.getField();
		Element T = f.newElement();
		Element K = f.newElement();
		Element aux = f.newElement();
		T = T.set(PK.getP());
		T = T.mul(sk);
		K = K.set(C);
		K = K.mul(sk);
		aux = aux.set(T);
		BigInteger m = new BigInteger("1");
		while (!aux.isEqual(K)) {
			// This is a brute force implementation of finding the discrete
			// logarithm.
			// Performance may be improved using algorithms such as Pollard's
			// Kangaroo.
			aux = aux.add(T);
			m = m.add(BigInteger.valueOf(1));
		}
		return m.toString();
	}

	public static void main(String[] args) {
		
		BGNEncryption b = new BGNEncryption();
		PublicKey PK = b.gen(512);
		BigInteger f = PK.getN();
		Element P = PK.getP();
		Element Q = PK.getQ();
		BigInteger order = PK.getN();
//		int len1 = P.getLengthInBytes();
//		int len2 = Q.getLengthInBytes();
//		int len3 = order.bitLength();

		Element msg1 = b.encrypt(PK, 10);
//		int len = msg1.getLengthInBytes();
		Element msg2 = b.encrypt(PK, 15);
//		int len = msg2.getLengthInBytes();

		Element add = b.add(PK, msg1, msg2);
		System.out.println("Addition: " + b.decrypt(PK, b.q, add));
//		int len4 = add.getLengthInBytes();

		Element mul = b.mul(PK, msg1, msg2);
		System.out.println("Mul: " + b.decryptMul(PK, b.q, mul));
		
//		int len5 = mul.getLengthInBytes();
//		System.out.println("P�ĳ���:"+len1);
//		System.out.println("Q�ĳ���:"+len2);
//		System.out.println("��Ϊ:"+len3);
//		System.out.println("msg1�ĳ���:"+len);
//		System.out.println(len4);
//		System.out.println(len5);
	}
}