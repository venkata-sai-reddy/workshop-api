package com.clarku.workshop.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.clarku.workshop.exception.GlobalException;

import lombok.extern.log4j.Log4j2;

@Component
@Log4j2
public class Secure {

	public String getEncrypted(String password) throws GlobalException {

		try {
			MessageDigest messageDigest = MessageDigest.getInstance("SHA");
			messageDigest.update(password.getBytes());
			byte[] resultedArray = messageDigest.digest();

			StringBuilder hashPass = new StringBuilder();

			for (byte eachByte : resultedArray) {
				hashPass.append(String.format("%02x", eachByte));
			}

			return hashPass.toString();

		} catch (NoSuchAlgorithmException exp) {
			log.error("Secure :: hashing : Failed to fetch the Algorithm");
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception exp) {
			log.error("Secure :: hashing : " + exp.getMessage());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public String generateTempPass() {
		SecureRandom rand = new SecureRandom();
		String alpha = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		String numeric = "0123456789";
		String special = "!@#$&*-_.?";
		StringBuilder pass = new StringBuilder();
		pass.append(alpha.charAt(rand.nextInt(alpha.length())));
		pass.append(alpha.toLowerCase().charAt(rand.nextInt(alpha.length())));
		pass.append(numeric.charAt(rand.nextInt(numeric.length())));
		pass.append(special.charAt(rand.nextInt(special.length())));
		String allChars = alpha + alpha.toLowerCase() + numeric + special;
		for (int index = 0; index <= rand.nextInt(4, 8); index++) {
			pass.append(allChars.charAt(rand.nextInt(allChars.length())));
		}
		List<String> passChars = Arrays.asList(pass.toString().split(""));
		Collections.shuffle(passChars);
		pass = new StringBuilder("");
		for (String letter : passChars) {
			pass.append(letter);
		}
		return pass.toString();
	}
}
