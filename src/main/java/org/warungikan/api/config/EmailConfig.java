package org.warungikan.api.config;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Properties;
import java.util.UUID;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.warungikan.api.utils.Constant;

@Configuration
public class EmailConfig {

	@Value("${mail.username}")
	private String username;
	
	@Value("${mail.password}")
	private String encrypted_password;
	
	@Value("${mail.host}")
	private String host;
	
	@Value("${mail.port}")
	private String port;
	
	@Value("${web.ui.url}")
	private String web_ui;
	
	@Value("${web.ui.url.reg.url}")
	private String web_ui_reg_conf;
	@Bean
	public JavaMailSender getJavaMailSender() throws UnsupportedEncodingException, Exception {
	    JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
	    mailSender.setHost("smtp.gmail.com");
	    mailSender.setPort(587);

	    String password = decrypt(encrypted_password.getBytes("UTF-8"), Constant.ENC_KEY);
	    mailSender.setUsername(username);
	    mailSender.setPassword(password);
	     
	    Properties props = mailSender.getJavaMailProperties();
	    props.put("mail.transport.protocol", "smtp");
	    props.put("mail.smtp.auth", "true");
	    props.put("mail.smtp.starttls.enable", "true");
	    props.put("mail.debug", "true");
	     
	    return mailSender;
	}
	
	public static void main(String[] args) {
		try {
			System.out.println(UUID.randomUUID().toString().replace("-", ""));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static String encrypt(byte[] bs, String encryptionKey) throws Exception {
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding","SunJCE");
		MessageDigest sha = MessageDigest.getInstance("SHA-1");
		byte[] hashedPassword = sha.digest(encryptionKey.getBytes());
		hashedPassword = Arrays.copyOf(hashedPassword, 16);
		SecretKeySpec key = new SecretKeySpec(hashedPassword, "AES");
		cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(Constant.IV.getBytes()));
		return bytesToHex(cipher.doFinal(bs));
	}
	
	public static String decrypt(byte[] bs, String encryptionKey) throws Exception {
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding","SunJCE");
		MessageDigest sha = MessageDigest.getInstance("SHA-1");
		byte[] hashedPassword = sha.digest(encryptionKey.getBytes("UTF-8"));
		hashedPassword = Arrays.copyOf(hashedPassword, 16);
		SecretKeySpec key = new SecretKeySpec(hashedPassword, "AES");
		cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(Constant.IV.getBytes("UTF-8")));
		byte[] toDecrypt = Hex.decodeHex(new String(bs).toCharArray());
		byte[] result = cipher.doFinal(toDecrypt);
		return new String(result, "UTF-8");
	}
	public static String bytesToHex(byte[] bytes) {
		char[] hexArray = "0123456789ABCDEF".toCharArray();
	    char[] hexChars = new char[bytes.length * 2];
	    for ( int j = 0; j < bytes.length; j++ ) {
	        int v = bytes[j] & 0xFF;
	        hexChars[j * 2] = hexArray[v >>> 4];
	        hexChars[j * 2 + 1] = hexArray[v & 0x0F];
	    }
	    return new String(hexChars);
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEncrypted_password() {
		return encrypted_password;
	}

	public void setEncrypted_password(String encrypted_password) {
		this.encrypted_password = encrypted_password;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getWeb_ui() {
		return web_ui;
	}

	public void setWeb_ui(String web_ui) {
		this.web_ui = web_ui;
	}

	public String getWeb_ui_reg_conf() {
		return web_ui_reg_conf;
	}

	public void setWeb_ui_reg_conf(String web_ui_reg_conf) {
		this.web_ui_reg_conf = web_ui_reg_conf;
	}
	
	

}
