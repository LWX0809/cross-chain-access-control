package crosschain.attributeAccessControl.contract;

import paillierp.PaillierThreshold;
import paillierp.PartialDecryption;
import paillierp.key.PaillierPrivateThresholdKey;

import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;


/**
 * This script looks for keys in a folder named "Keys" in the project root directory.
 * It then creates at most NUM_SERV PaillierThreshold object, each initialized with a
 * different private key. Finally, it attempts to encrypt, share decrypt and combine 
 * the message MESSAGE.
 * 
 * @author Christian Mouchet
 */
public class Contract_Paillier {

//	private static final int NUM_SERV =5;
//	private static final String MESSAGE = "Hello world !"; // Plain must be smaller than n^2

	private final static Logger logger = Logger.getLogger(Contract_Paillier.class.getName());
	public static BigInteger add(BigInteger c1, BigInteger c2, BigInteger N) {
		BigInteger nsquare = N.pow(2);
		return c1.multiply(c2).mod(nsquare);
	}

	// 密文相除 等于原文相减
	public static BigInteger sub(BigInteger ciphertext1, BigInteger ciphertext2,BigInteger N) {
		//公钥只有一个 所以n是一样的
		BigInteger nsquare = N.pow(2);
		BigInteger ciphertext2Inverse = ciphertext2.modInverse(nsquare);
		return ciphertext1.multiply(ciphertext2Inverse).mod(nsquare);
	}

	//进入密文   数据a b 以及加法因子x 的密文
	public static int compare(BigInteger ciphertext1, BigInteger ciphertext2,BigInteger ciphertext3,BigInteger N) throws Exception {

		//C__=C1*C3/C2  M__=M1+M3-M2   ,M3>M1 ,M3>M2 远大于 加法因子外部不知道
		BigInteger C__ = add(ciphertext1,ciphertext3,N);
		BigInteger ciphertext_ = sub(C__,ciphertext2,N);

		//试试不解密比较大小
//		int comparisonResult = udDM.compareTo(plaintext3);
		logger.info(ciphertext_.toString());
		logger.info(ciphertext3.toString());
		int comparisonResult = ciphertext_.compareTo(ciphertext3);

		if (comparisonResult > 0) {
			logger.info("M1 > M2");
		} else if (comparisonResult < 0) {
			logger.info("M1 < M2");
		} else {
			logger.info("M1 = M2");
		}
		return comparisonResult;
	}

	//进入密文   数据a b 以及加法因子x 的密文  返回差值
	public static String compute(BigInteger ciphertext1, BigInteger ciphertext2,BigInteger ciphertext3,BigInteger N) throws Exception {

		//C__=C1*C3/C2  M__=M1+M3-M2   ,M3>M1 ,M3>M2 远大于 加法因子外部不知道
		BigInteger C__ = add(ciphertext1,ciphertext3,N);
		BigInteger ciphertext_ = sub(C__,ciphertext2,N);

		return ciphertext_.toString();
	}


}
