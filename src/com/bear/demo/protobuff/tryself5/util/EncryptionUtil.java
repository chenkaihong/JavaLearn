package com.bear.demo.protobuff.tryself5.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.concurrent.CountDownLatch;

import javax.crypto.Cipher;

import org.apache.log4j.Logger;

import com.gzyouai.hummingbird.engine.tryself5.handle.TestProto.MyMsgRsp;
import com.gzyouai.hummingbird.engine.tryself5.handle.TestProto.MyMsgRsp.Builder;

public class EncryptionUtil {
	
	private static Logger log = Logger.getLogger(EncryptionUtil.class);

	/**
	 * 通过RSA算法生成公钥和私钥,并保存在对应的文件中
	 * @param pubkeyfile
	 * @param privatekeyfile
	 * @throws NoSuchAlgorithmException
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static void build(String pubfile, String ppkfile) throws Exception {
		KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
		keyPairGen.initialize(1024);
		KeyPair keyPair = keyPairGen.generateKeyPair();
		RSAPrivateKey ppk = (RSAPrivateKey) keyPair.getPrivate();
		RSAPublicKey pub = (RSAPublicKey) keyPair.getPublic();

		// 写入私钥
		File ppkFile = IOUtil.writeObjectToFile(ppkfile, ppk);
		// 写入公钥
		File pubFile = IOUtil.writeObjectToFile(pubfile, pub);

		log.info("Make pub and ppk...\npubFile: " + pubFile + "\nppkFile: " +  ppkFile);
	}
	
	/**
	 * 通过公钥或私钥解密或加密信息
	 * @param key
	 * @param data
	 * @return
	 * @throws Exception
	 */
	private static byte[] handle(Key key, byte[] data, int mode) throws Exception{
		byte[] result = null;
		if(key == null){
			throw new Exception("Key is null");
		}
		if(data == null){
			throw new Exception("Data is null");
		}
		
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(mode, key);
		result = cipher.doFinal(data);
		return result;
	}
	public static byte[] encode(Key key, byte[] data) throws Exception{
		return handle(key, data, Cipher.ENCRYPT_MODE);
	}
	public static byte[] encode(Key key, String dataString, String charset) throws Exception{
		byte[] data = dataString.getBytes(charset);
		return handle(key, data, Cipher.ENCRYPT_MODE);
	}
	public static byte[] decode(Key key, byte[] data) throws Exception{
		return handle(key, data, Cipher.DECRYPT_MODE);
	}
	

	public static void test(RSAPublicKey pub, RSAPrivateKey ppk, byte[] msg) throws Exception {
//		System.out.println("Resource: " + msg);
		byte[] encodeByte = encode(ppk, msg);
//		System.out.println("Encode: " + new String(encodeByte,"UTF-8"));
		byte[] decodeByte = decode(pub, encodeByte);
//		System.out.println("Decode: " + new String(decodeByte,"UTF-8"));
	}
	public static void main(String[] args) throws Exception {
		String pubfile = "d:/temp/pub.key";
		String ppkfile = "d:/temp/pri.key";
		final RSAPublicKey pub = (RSAPublicKey)IOUtil.readFileToObject(pubfile);
		final RSAPrivateKey ppk = (RSAPrivateKey)IOUtil.readFileToObject(ppkfile);
		
		Builder req = MyMsgRsp.newBuilder();
		req.setContent("HI");
		req.setName("Tom");
		final byte[] msg = req.build().toByteArray();
		
		int threadSize = 10;
		final CountDownLatch latch =  new CountDownLatch(threadSize);
		long beginTime = System.currentTimeMillis();
		for(int i = 0;i < threadSize;i++){
			new Thread(new Runnable() {
				@Override
				public void run() {
					try{
						for(int t = 0;t < 3000;t++){
							try {
								test(pub, ppk, msg);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}finally{
						latch.countDown();
					}
				}
			}).start();
		}
		latch.wait();
		long endTime = System.currentTimeMillis();
		System.out.println("useTime: " + (endTime-beginTime));
	}
}
