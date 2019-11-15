package com.qunar.qtalk.cricle.camel.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;

/**
 * Created by haoling.wang on 2019/1/14.
 */
public class AESedeUtils {

    private static final Logger log = LoggerFactory.getLogger(AESedeUtils.class);

    public static String encrypt(String content, String password) {
        try {
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            random.setSeed(password.getBytes());
            KeyGenerator kgen = KeyGenerator.getInstance("AES");
            kgen.init(128, random);
            SecretKey secretKey = kgen.generateKey();
            byte[] enCodeFormat = secretKey.getEncoded();
            SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
            Cipher cipher = Cipher.getInstance("AES");// 创建密码器
            byte[] byteContent = content.getBytes("utf-8");
            cipher.init(Cipher.ENCRYPT_MODE, key);// 初始化
            byte[] result = cipher.doFinal(byteContent);
            return parseByte2HexStr(result);
        } catch (Exception e) {
            log.error("AES encrypt occur exception", e);
        }
        return null;
    }

    public static String decrypt(String content, String password) {
        return decrypt(parseHexStr2Byte(content), password);
    }

    public static String decrypt(byte[] content, String password) {
        try {
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            random.setSeed(password.getBytes());
            KeyGenerator kgen = KeyGenerator.getInstance("AES");
            kgen.init(128, random);
            SecretKey secretKey = kgen.generateKey();
            byte[] enCodeFormat = secretKey.getEncoded();
            SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] result = cipher.doFinal(content);
            return new String(result);
        } catch (Exception e) {
            log.error("AES decrypt occur exception", e);
        }
        return null;
    }

    public static String parseByte2HexStr(byte buf[]) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < buf.length; i++) {
            String hex = Integer.toHexString(buf[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex.toUpperCase());
        }
        return sb.toString();
    }

    public static byte[] parseHexStr2Byte(String hexStr) {
        if (hexStr.length() < 1) {
            return null;
        }
        byte[] result = new byte[hexStr.length() / 2];
        for (int i = 0; i < hexStr.length() / 2; i++) {
            int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
            int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
            result[i] = (byte) (high * 16 + low);
        }
        return result;
    }

    public static void main(String[] args) {
//        15F230D4B6C9B46D8CBBF89A06211FF1D3E9B763FBF829B0620483492AED3629
//        lOq9liH:1/0001:XCMvq
//                ?Vje/GGc:a"7TivrXbS[_0<dkvPLXJ14
        long l = System.currentTimeMillis();
        String a = encrypt("lOq9liH:1/0001:XCMvq", "?Vje/GGc:a\"7TivrXbS[_0<dkvPLXJ14");
        String b = decrypt("15F230D4B6C9B46D8CBBF89A06211FF1D3E9B763FBF829B0620483492AED3629", "?Vje/GGc:a\"7TivrXbS[_0<dkvPLXJ14");
        System.out.println(a);
        System.out.println(b);

        System.out.println(System.currentTimeMillis() - l);
    }
}
