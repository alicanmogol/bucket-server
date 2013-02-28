package com.fererlab.dto;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.TreeMap;

/**
 * acm 10/15/12
 */
public class Session extends TreeMap<String, String> {

    public String COOKIE_SIGN_KEY = "_FR_CSK_";
    private BASE64Encoder base64Encoder = new BASE64Encoder();
    private BASE64Decoder base64Decoder = new BASE64Decoder();

    public String toCookie() {
        StringBuilder cookie = new StringBuilder();
        try {
            // if there is no cookie value, do not add set cookie
            if (this.size() > 0) {
                cookie.append("Set-Cookie:  ");
                for (String key : this.keySet()) {
                    cookie.append(key);
                    cookie.append("=");
                    cookie.append(this.get(key));
                    cookie.append(";");
                }

                // sign the cookie
                sign();

                // append the signiture
                cookie.append(COOKIE_SIGN_KEY);
                cookie.append("=");
                cookie.append(this.get(COOKIE_SIGN_KEY));
                cookie.append(";");

                // line break for output
                cookie.append("\n");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return cookie.toString();
    }

    public String decode(String value) throws IOException {
        return new String(base64Decoder.decodeBuffer(value));
    }

    public String encode(String value) {
        return base64Encoder.encode(value.getBytes());
    }

    public String decrypt(String secretKey, String value) throws Exception {
        Cipher c = Cipher.getInstance("AES");
        SecretKeySpec k = new SecretKeySpec(secretKey.getBytes(), "AES");
        c.init(Cipher.DECRYPT_MODE, k);
        return new String(c.doFinal(value.getBytes()));
    }

    public String encrypt(String secretKey, String value) throws Exception {
        try {
            Cipher c = Cipher.getInstance("AES");
            SecretKeySpec k = new SecretKeySpec(secretKey.getBytes(), "AES");
            c.init(Cipher.ENCRYPT_MODE, k);
            return new String(c.doFinal(value.getBytes()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new Exception("could not encrypt value!");
    }

    private void sign() throws Exception {
        String signValue = "";
        StringBuilder cookieContent = new StringBuilder();
        for (String key : this.keySet()) {
            cookieContent.append(key);
            cookieContent.append("=");
            cookieContent.append(this.get(key));
            cookieContent.append(";");
        }
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            for (byte b : md.digest(cookieContent.toString().getBytes())) {
                signValue += Integer.toString((b & 0xff) + 0x100, 16).substring(1);
            }
            put(COOKIE_SIGN_KEY, signValue);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public void putEncoded(String key, String value) {
        put(key, encode(value));
    }

    public void putEncrypt(String secretKey, String key, String value) throws Exception {
        put(key, encrypt(secretKey, value));
    }

}
