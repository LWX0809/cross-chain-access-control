package crosschain.attributeAccessControl.gateway_connect;

import com.sun.deploy.cache.BaseLocalApplicationProperties;
import paillierp.PaillierThreshold;
import paillierp.PartialDecryption;
import paillierp.key.PaillierPrivateThresholdKey;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/**
 * This script looks for keys in a folder named "Keys" in the project root directory.
 * It then creates at most NUM_SERV PaillierThreshold object, each initialized with a
 * different private key. Finally, it attempts to encrypt, share decrypt and combine 
 * the message MESSAGE.
 * 
 * @author Christian Mouchet
 */
public class ThreshPaillier {

	private static final int NUM_SERV =5;

	private static Random rand = new SecureRandom();
	private static List<PaillierPrivateThresholdKey> keys = new ArrayList<PaillierPrivateThresholdKey>();
	private static List<PaillierThreshold> decryptionServers =  new ArrayList<PaillierThreshold>();

	public static List<PaillierThreshold> getDecryptionServers() {
		return decryptionServers;
	}

	//先找到key
	public ThreshPaillier() throws IOException {
		Files.walk(Paths.get("keys")).filter(f -> f.toString().endsWith(".privkey")).forEach(filePath -> {
			try {
				if(Files.isRegularFile(filePath)) {
					//System.out.println(filePath.toString());
					keys.add(new PaillierPrivateThresholdKey(Files.readAllBytes(filePath), rand.nextLong()));
				}
			} catch (Exception e) {
				e.printStackTrace();
				//System.exit(1);
			}
		});

		System.out.println("Found "+keys.size()+" keys.");
		System.out.println("---------------------------------------------");

		for(PaillierPrivateThresholdKey key : keys) {
			assert(key.canEncrypt());
			PaillierThreshold decryptionServer = new PaillierThreshold(key);
			if(key.getID() <= NUM_SERV)
				decryptionServers.add(decryptionServer);
		}

		System.out.println("Created "+decryptionServers.size()+" decryption servers");
		System.out.println("---------------------------------------------");
	}

	public static void maintest(String[] args) throws Exception {

//		Random rand = new SecureRandom();
//		List<PaillierPrivateThresholdKey> keys = new ArrayList<PaillierPrivateThresholdKey>();
//		List<PaillierThreshold> decryptionServers =  new ArrayList<PaillierThreshold>();
		
		Files.walk(Paths.get("keys")).filter(f -> f.toString().endsWith(".privkey")).forEach(filePath -> {
		    try {
		    	if(Files.isRegularFile(filePath)) {
		    		//System.out.println(filePath.toString());
		    		keys.add(new PaillierPrivateThresholdKey(Files.readAllBytes(filePath), rand.nextLong()));
		    	}
			} catch (Exception e) {
				e.printStackTrace();
				//System.exit(1);
			}
		});
		
		System.out.println("Found "+keys.size()+" keys.");
		System.out.println("---------------------------------------------");
		
		for(PaillierPrivateThresholdKey key : keys) {
			assert(key.canEncrypt());
			PaillierThreshold decryptionServer = new PaillierThreshold(key);
			if(key.getID() <= NUM_SERV)
				decryptionServers.add(decryptionServer);
		}
		
		System.out.println("Created "+decryptionServers.size()+" decryption servers");
		System.out.println("---------------------------------------------");
		
		BigInteger M = new BigInteger(new String("hello world").getBytes());
		System.out.println("Plain => "+M+"         ("+new String(M.toByteArray())+")");
		System.out.println("---------------------------------------------");
		BigInteger cipher = decryptionServers.get(1).encrypt(M);
		System.out.println("Cipher =>"+cipher);
		System.out.println("---------------------------------------------");
				
		PartialDecryption[] partDec = new PartialDecryption[decryptionServers.size()];
		
		System.out.println("Decryption attempt with "+decryptionServers.size()+" decryption servers...");
		System.out.println();
		for(int i=0; i<decryptionServers.size(); i++){
			PaillierPrivateThresholdKey privateKey = decryptionServers.get(i).getPrivateKey();
			partDec[privateKey.getID()-1] = decryptionServers.get(i).decrypt(cipher);
		}
		
		BigInteger udDM = decryptionServers.get(1).combineShares(partDec);
		System.out.println("Decryption =>"+udDM+"     ("+new String(udDM.toByteArray())+")");


		System.out.println("---------------------------------------------------------------------");

		BigInteger plaintext1 = new BigInteger("12");
		BigInteger plaintext2 = new BigInteger("10");
		BigInteger ciphertext1 =decryptionServers.get(1).encrypt(plaintext1);
		BigInteger ciphertext2 =decryptionServers.get(1).encrypt(plaintext2);
		int a=compare(ciphertext1,ciphertext2,decryptionServers);
	}
	
	public static BigInteger lambda(int i, PartialDecryption[] decryptionServers) {
		int numerator = 1;
		int denominator = 1;
		for(PartialDecryption pd: decryptionServers){
			if (pd.getID() != i) {
				numerator *= -pd.getID();
				denominator *= (i-pd.getID());
			}
		}
		return BigInteger.valueOf(numerator/denominator);
	}

	public static BigInteger encrypt(PaillierThreshold pubkey,BigInteger plaintext) throws IOException {
		BigInteger cipher = pubkey.encrypt(plaintext);
		return cipher;
	}

	public static BigInteger decrypt(List<PaillierThreshold> decryptionServers1,BigInteger cipher) throws IOException {

		PartialDecryption[] partDec = new PartialDecryption[decryptionServers1.size()];

		System.out.println("Decryption attempt with "+decryptionServers1.size()+" decryption servers...");
		System.out.println();
		for(int i=0; i<decryptionServers1.size(); i++){
			PaillierPrivateThresholdKey privateKey = decryptionServers1.get(i).getPrivateKey();
			partDec[privateKey.getID()-1] = decryptionServers1.get(i).decrypt(cipher);
		}

		BigInteger plaintext = decryptionServers1.get(1).combineShares(partDec);
		System.out.println("Decryption =>"+plaintext);
		System.out.println("---------------------------------------------------------------------");
		return plaintext;
	}

	public static BigInteger add(BigInteger c1, BigInteger c2,
								 PaillierThreshold pubkey) {
		BigInteger nsquare = pubkey.getPublicThresholdKey().getN().pow(2);
		return c1.multiply(c2).mod(nsquare);
	}

	// 密文相除 等于原文相减
	public static BigInteger sub(BigInteger ciphertext1, BigInteger ciphertext2,PaillierThreshold pubkey) {
		//公钥只有一个 所以n是一样的
		BigInteger nsquare = pubkey.getPublicThresholdKey().getN().pow(2);;
		BigInteger ciphertext2Inverse = ciphertext2.modInverse(nsquare);
		return ciphertext1.multiply(ciphertext2Inverse).mod(nsquare);
	}

	//进入密文
	public static int compare(BigInteger ciphertext1, BigInteger ciphertext2, List<PaillierThreshold> decryptionServers) throws Exception {

		PaillierThreshold pubkey=decryptionServers.get(1);

		//加法因子
		BigInteger plaintext3 = new BigInteger("15");
		BigInteger ciphertext3 =pubkey.encrypt(plaintext3);

		//C__=C1*C3/C2  M__=M1+M3-M2   ,M3>M1 ,M3>M2 远大于 加法因子外部不知道
		BigInteger C__ = add(ciphertext1,ciphertext3,pubkey);
		BigInteger ciphertext_ = sub(C__,ciphertext2,pubkey);

		PartialDecryption[] partDec = new PartialDecryption[decryptionServers.size()];

		for(int i=0; i<decryptionServers.size(); i++){
			PaillierPrivateThresholdKey privateKey = decryptionServers.get(i).getPrivateKey();
			partDec[privateKey.getID()-1] = decryptionServers.get(i).decrypt(ciphertext_);
		}

		BigInteger udDM = decryptionServers.get(1).combineShares(partDec);

		System.out.println("2.Decryption =>  "+udDM);

		//可以不解密直接判断密文大小关系
//		int comparisonResult = udDM.compareTo(plaintext3);
		int comparisonResult = ciphertext_.compareTo(ciphertext3);

		if (comparisonResult > 0) {
			System.out.println("M1 > M2");
		} else if (comparisonResult < 0) {
			System.out.println("M1 < M2");
		} else {
			System.out.println("M1 = M2");
		}
		return comparisonResult;
	}

	public static String getSHA256(String str) {
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("SHA-256"); //获取SHA-256算法
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		md.update(str.getBytes()); //更新摘要
		byte[] bytes = md.digest(); //计算摘要
		StringBuilder sb = new StringBuilder();
		for (byte b : bytes) {
			sb.append(String.format("%02x", b)); //转换成16进制
		}
		return sb.toString();
	}

}
