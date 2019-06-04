package io.renren.common.utils;

import lombok.experimental.UtilityClass;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5加密类（封装jdk自带的md5加密方法）
 *
 * @date 2016年12月2日 下午4:14:22
 */
@UtilityClass
public class MD5Util {

    public static String encrypt(String source) {
        return encodeMd5(source.getBytes());
    }

    public String encrypt(String source, String key) {
        return encrypt(source + Constant.SPLIT_CHAR_COLON + key);
    }

    private static String encodeMd5(byte[] source) {
        try {
            return encodeHex(MessageDigest.getInstance("MD5").digest(source));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    private static String encodeHex(byte[] bytes) {
        StringBuffer buffer = new StringBuffer(bytes.length * 2);
        for (int i = 0; i < bytes.length; i++) {
            if (((int) bytes[i] & 0xff) < 0x10)
                buffer.append("0");
            buffer.append(Long.toString((int) bytes[i] & 0xff, 16));
        }
        return buffer.toString();
    }


    public static String sign( String key, String content, String charset)  {
        String signData = key +  content ;

        try {
            String sign = DigestUtils.md5Hex(signData.getBytes(charset));
            return sign;
        } catch (UnsupportedEncodingException e) {

            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        System.out.println(encrypt("hello 18010450436")); // 5e419a421ff54aba83ed386b1f40a1fc
    }
}
