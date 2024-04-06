package crosschain.homomorphicEncryption.BGN;

import java.math.BigInteger;
import java.security.SecureRandom;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.PairingParameters;
import it.unisa.dia.gas.plaf.jpbc.pairing.a1.TypeA1CurveGenerator;
import it.unisa.dia.gas.plaf.jpbc.pairing.a1.TypeA1Pairing;

public class text {

	public static void main(String[] args) {
		
		int bits = 512;
		SecureRandom rng = new SecureRandom();
		TypeA1CurveGenerator a1 = new TypeA1CurveGenerator(rng, 2, bits); // Requires
																			// 2
																			// prime
																			// numbers.
		PairingParameters param = a1.generate();
		TypeA1Pairing pairing = new TypeA1Pairing(param);
		
		BigInteger order = param.getBigInteger("n"); // Must extract the prime numbers for
											// both keys.
		BigInteger r = param.getBigInteger("n0");//�����q2
		BigInteger q = param.getBigInteger("n1");//�����SK
		System.out.println("order:"+order);
		System.out.println("r:"+r);
		System.out.println("q:"+q);
		Field<?> f = pairing.getG1();
		Element P = f.newRandomElement();
		System.out.println(P.getLengthInBytes());
		P = P.mul(param.getBigInteger("l"));
		Element Q = f.newElement();
		Q = Q.set(P);
		Q = Q.mul(r);
		System.out.println(r);
//		System.out.println(P.getLengthInBytes());

	}

}
