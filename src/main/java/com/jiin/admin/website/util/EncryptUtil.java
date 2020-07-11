package com.jiin.admin.website.util;

import lombok.extern.slf4j.Slf4j;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Slf4j
public class EncryptUtil {
    public static final String MD5 = "MD5";
    public static final String SHA256 = "SHA-256";
    public static final String SHA512 = "SHA-512";

    public static String encrypt(String str, String algorithm) {
        try {
            MessageDigest md = MessageDigest.getInstance(algorithm);
            byte[] bytes = str.getBytes();
            md.reset();
            byte[] digested = md.digest(bytes);
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < digested.length; i++)
                sb.append(Integer.toHexString(0xff & digested[i]));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            log.error("ERROR - " + e.getMessage());
            return null;
        }
    }
}
