package com.guangping.post;

import com.guangping.encrypt.DigestAes;
import com.guangping.pojo.RequestParameter;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static java.util.Objects.isNull;

/**
 * @author GuangPing Lin
 * @date 2021/2/5 14:39
 */
public class DataHandler {

    private static final String ENCRYPTION_PASSWORD_HEAD = "c2fc2f64";
    private static final String ENCRYPTION_PASSWORD_TAIL = "db7bd535";

    private static final String ENCRYPT_DATA_FLAG = "2d5f22520633cfd5c44bacc1634a93f2";

    public static byte[][] evpBytes(int keyLength, byte[] bArr, byte[] aesEncryptionPassword, int updateTimes) {
        byte[] keyBytes = new byte[keyLength];

        int ivLength = DigestAes.IV_BYTES_LENGTH;
        byte[] ivBytes = new byte[DigestAes.IV_BYTES_LENGTH];
        byte[][] bytes = {keyBytes, ivBytes};

        if (aesEncryptionPassword == null) {
            return bytes;
        }

        byte[] temp = null;
        int times = 0;
        int keyBytesIndex = 0;
        int ivBytesIndex = 0;

        String newEncryptionPassword = changeAesEncryptionPassword(new String(aesEncryptionPassword));
        byte[] newEncryptionPasswordBytes = newEncryptionPassword.getBytes(StandardCharsets.UTF_8);

        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");

            while (true) {
                md5.reset();

                if (times > 0) {
                    md5.update(temp);
                }
                md5.update(newEncryptionPasswordBytes);

                if (bArr != null) {
                    md5.update(bArr, 0, 8);
                }

                temp = md5.digest();
                for (int i = 1; i < updateTimes; i++) {
                    md5.reset();
                    md5.update(temp);
                    temp = md5.digest();
                }

                int tempIndex = 0;
                if (keyLength > 0) {
                    while (keyLength != 0 && tempIndex != temp.length) {
                        keyBytes[keyBytesIndex++] = temp[tempIndex++];
                        keyLength--;
                    }
                }

                if (ivLength > 0 && tempIndex != temp.length) {
                    while (ivLength != 0 && tempIndex != temp.length) {
                        ivBytes[ivBytesIndex] = temp[tempIndex];
                        ivLength--;
                        tempIndex++;
                        ivBytesIndex++;
                    }
                }

                if (keyLength == 0 && ivLength == 0) {
                    break;
                }

                times++;
            }

            return bytes;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return new byte[0][0];
    }

    private static String changeAesEncryptionPassword(String encryptionPassword) {
        String substring = encryptionPassword.substring(8, 24);
        return ENCRYPTION_PASSWORD_HEAD + substring + ENCRYPTION_PASSWORD_TAIL;
    }

    public static String sign(RequestParameter requestParameter) {

        String sign = "appId" + "=" + requestParameter.getAppId() +
                "&" +
                "appVersion" + "=" + requestParameter.getAppVersion() +
                "&" +
                "data" + "=" + requestParameter.getData() +
                "&" +
                "timestamp" + "=" + requestParameter.getTimestamp() +
                ENCRYPT_DATA_FLAG;

        byte[] signDigest = DigestAes.getSha256Bytes(sign);

        if (isNull(signDigest)) {
            return null;
        }

        signDigest = DigestAes.getMd5Bytes(DigestAes.bytes2hexString(signDigest, false));

        if (isNull(signDigest)) {
            return null;
        }

        return DigestAes.bytes2hexString(signDigest, false);
    }
}
