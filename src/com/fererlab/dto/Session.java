package com.fererlab.dto;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.TreeMap;

/**
 * acm 10/15/12
 */
public class Session extends TreeMap<String, String> {

    private BASE64Encoder base64Encoder = new BASE64Encoder();
    private BASE64Decoder base64Decoder = new BASE64Decoder();
    private String cookieSignSecretKey = "CHANGE_THIS_KEY";
    private String applicationCookieName = "SampleApplication";

    public String toCookie() {
        // (Base64)   Set-Cookie: SampleApplication=a2V5MT12YWx1ZTE7a2V5Mj12YWx1ZTI7
        // (decoded)  Set-Cookie: SampleApplication=key1=value1;key2=value2;
        StringBuilder cookieApplication = new StringBuilder();
        cookieApplication.append("Set-Cookie: ");
        cookieApplication.append(getApplicationCookieName());
        cookieApplication.append("=");

        StringBuilder cookieApplicationSignature = new StringBuilder();
        cookieApplicationSignature.append("Set-Cookie: ");
        cookieApplicationSignature.append(getApplicationCookieName());
        cookieApplicationSignature.append("=");


        try {
            // if there is no cookie value, do not add set cookie
            if (this.size() > 0) {
                // key1=value1;key2=value2;
                StringBuilder applicationCookieValue = new StringBuilder();
                for (String key : this.keySet()) {
                    applicationCookieValue.append(key).append("=").append(get(key)).append(";");
                }
                String keyValues = applicationCookieValue.toString();
                cookieApplication.append(encode(keyValues));
                cookieApplication.append(";\n");

                // sign the cookie
                String signature = sign(keyValues);

                // append the signature
                // (Base64)   Set-Cookie: SampleApplication_fr_ck_sn_ky=YWRhOTI0M2QyZDA5ZmQwYmQ1ZTM4MGE5ODc4Y2M3YTlhZDA2M2E0MA
                // (decoded)  Set-Cookie: SampleApplication_fr_ck_sn_ky=ada9243d2d09fd0bd5e380a9878cc7a9ad063a40;
                cookieApplicationSignature.append("Set-Cookie:  ");
                cookieApplicationSignature.append(applicationCookieName);
                cookieApplicationSignature.append("_");
                cookieApplicationSignature.append(SessionKeys.COOKIE_SIGN_KEY.getValue());
                cookieApplicationSignature.append("=");
                cookieApplicationSignature.append(signature);
                cookieApplicationSignature.append(";\n");

                /*
                Set-Cookie: SampleApplication=a2V5MT12YWx1ZTE7a2V5Mj12YWx1ZTI7;
                Set-Cookie: SampleApplication_fr_ck_sn_ky=YWRhOTI0M2QyZDA5ZmQwYmQ1ZTM4MGE5ODc4Y2M3YTlhZDA2M2E0MA;
                 */
                cookieApplication.append(cookieApplicationSignature);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        /*

        Set-Cookie: SampleApplication=a2V5MT12YWx1ZTE7a2V5Mj12YWx1ZTI7;\nSet-Cookie: SampleApplication_fr_ck_sn_ky=YWRhOTI0M2QyZDA5ZmQwYmQ1ZTM4MGE5ODc4Y2M3YTlhZDA2M2E0MA;\n
         */
        return cookieApplication.toString();
    }

    public String decode(String value) throws IOException {
        return new String(base64Decoder.decodeBuffer(value));
    }

    public String encode(String value) {
        String encoded = base64Encoder.encode(value.getBytes());
        while (encoded.endsWith("=")) {
            encoded = encoded.substring(0, encoded.length() - 1);
        }
        if (encoded.lastIndexOf('=') != -1) {
            encoded = encoded.substring(0, encoded.lastIndexOf('='));
        }
        return encoded;
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

    private String sign(String value) throws Exception {
        String signValue = "";
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            for (byte b : md.digest((value + getCookieSignSecretKey()).getBytes())) {
                signValue += Integer.toString((b & 0xff) + 0x100, 16).substring(1);
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw e;
        }
        return signValue;
    }

    public void putEncoded(String key, String value) {
        put(key, encode(value));
    }

    public void putEncrypt(String secretKey, String key, String value) throws Exception {
        putEncoded(key, encrypt(secretKey, value));
    }

    /*
    setters & getters
     */

    public String getCookieSignSecretKey() {
        return cookieSignSecretKey;
    }

    public void setCookieSignSecretKey(String cookieSignSecretKey) {
        this.cookieSignSecretKey = cookieSignSecretKey;
    }

    public String getApplicationCookieName() {
        return applicationCookieName;
    }

    public void setApplicationCookieName(String applicationCookieName) {
        this.applicationCookieName = applicationCookieName;
    }
}
