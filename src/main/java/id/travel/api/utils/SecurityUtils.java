package id.travel.api.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import id.travel.api.config.SecurityConstants;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class SecurityUtils {

	public static String md5(String text){
		byte[] bytesOfMessage;
		try {
			bytesOfMessage = text.getBytes("UTF-8");
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] thedigest = md.digest(bytesOfMessage);
			return new String(thedigest);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;

	}
	
	public static String getUsernameByToken(String jwt){
		String username = Jwts.parser()
        .setSigningKey(SecurityConstants.SECRET)
        .parseClaimsJws(jwt.replace(SecurityConstants.TOKEN_PREFIX, ""))
        .getBody()
        .getSubject();
		
		return username;
	}
}
