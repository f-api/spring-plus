package org.example.expert.config;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public abstract class BCrypUtil {

	private static final BCryptPasswordEncoder ENCODER = new BCryptPasswordEncoder();

	public static String encrypt(String rawPassword) {
		return ENCODER.encode(rawPassword);
	}

	public static boolean matches(String rawPassword, String encryptedPassword) {
		return ENCODER.matches(rawPassword, encryptedPassword);
	}
}
