package com.guangping.encrypt;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.*;

import static java.util.Objects.isNull;

/**
 * <p>
 * 1、获取数据的16进制形式的MD5字符串;
 * 2、提供AES加密、解密.
 * </p>
 *
 * @author GuangPing Lin
 * @date 2021/2/5 14:46
 */
public abstract class DigestAes {

    public static final int IV_BYTES_LENGTH = 16;

    private static final String AES = "AES";

    public enum AesModel {
        /**
         *
         */
        CFB_NO_PADDING("CFB", "NoPadding");

        private String model;
        private String padding;

        AesModel(String model, String padding) {
            this.model = model;
            this.padding = padding;
        }

        public String getModel() {
            return model;
        }

        public String getPadding() {
            return padding;
        }
    }

    public static byte[] getMd5Bytes(String data) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");

            // 获取数据的UTF-8编码形式.
            byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);

            // 获取数据的MD5摘要
            md5.update(dataBytes);
            return md5.digest();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String bytes2hexString(byte[] bytes, boolean upperCase) {
        int i;
        String s;
        StringBuilder hexString = new StringBuilder();

        for (byte b : bytes) {
            // 只有Integer、Long类有toHexString方法
            // 将byte转换为无符int
            i = Byte.toUnsignedInt(b);
            s = Integer.toHexString(i);

            if (s.length() == 1) {
                hexString.append("0");
            }

            hexString.append(s);
        }

        String hexLowerCase = hexString.toString();
        return upperCase ? hexLowerCase.toUpperCase() : hexLowerCase;
    }

    public static byte[] hexString2bytes(String hexString) {
        int length = hexString.length();
        if (length % 2 != 0) {
            length++;
        }

        byte[] bytes = new byte[length / 2];

        char[] chars = hexString.toCharArray();
        int j = 0;
        int high;
        int low;
        for (int i = 0; i < chars.length; i += 2) {
            high = Character.getNumericValue(chars[i]);
            if (i + 1 < chars.length) {
                low = Character.getNumericValue(chars[i + 1]);
            } else {
                low = Character.getNumericValue(chars[i]);
                high = Character.getNumericValue('0');
            }

            bytes[j++] = (byte) ((high << 4) + low);
        }

        return bytes;
    }

    public static byte[] encrypt(AesModel aesModel, byte[] keyBytes, byte[] dataBytes, byte[] ivParameterBytes) throws InvalidAlgorithmParameterException {
        byte[] encryptBytes = null;

        SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, AES);

        try {
            Cipher cipher = Cipher.getInstance(AES + "/" + aesModel.model + "/" + aesModel.padding);

            IvParameterSpec ivParameterSpec = null;
            if (ivAvailable(ivParameterBytes)) {
                ivParameterSpec = new IvParameterSpec(ivParameterBytes);
            }

            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
            encryptBytes = cipher.doFinal(dataBytes);

            if (ivParameterSpec == null) {
                encryptBytes = bytesMerge(encryptBytes, cipher.getIV());
            }
        } catch (InvalidKeyException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return encryptBytes;
    }

    private static boolean ivAvailable(byte[] ivBytes) {
        return !isNull(ivBytes) && ivBytes.length == IV_BYTES_LENGTH;
    }

    private static byte[] bytesMerge(byte[] encryptBytes, byte[] ivBytes) {
        if (!ivAvailable(ivBytes)) {
            return encryptBytes;
        }

        int encryptBytesLength = encryptBytes.length;
        byte[] mergeBytes = new byte[IV_BYTES_LENGTH + encryptBytesLength];

        System.arraycopy(ivBytes, 0, mergeBytes, 0, IV_BYTES_LENGTH);
        System.arraycopy(encryptBytes, 0, mergeBytes, IV_BYTES_LENGTH, encryptBytes.length);

        return mergeBytes;
    }

    public static byte[] decrypt(AesModel aesModel, byte[] keyBytes, byte[] dataBytes) {
        byte[][] divide = bytesDivide(dataBytes);

        if (!isNull(divide)) {
            byte[] ivBytes = divide[0];
            byte[] decryptBytes = divide[1];

            SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, AES);
            IvParameterSpec ivParameterSpec = null;

            if (ivAvailable(ivBytes)) {
                ivParameterSpec = new IvParameterSpec(ivBytes);
            }

            try {
                Cipher cipher = Cipher.getInstance(AES + "/" + aesModel.model + "/" + aesModel.padding);

                cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
                decryptBytes = cipher.doFinal(decryptBytes);
            } catch (InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException | NoSuchPaddingException | NoSuchAlgorithmException e) {
                e.printStackTrace();
            }

            return decryptBytes;
        }

        return null;
    }

    private static byte[][] bytesDivide(byte[] mergeBytes) {
        if (mergeBytes.length < IV_BYTES_LENGTH) {
            return null;
        }

        byte[] ivBytes = new byte[IV_BYTES_LENGTH];
        System.arraycopy(mergeBytes, 0, ivBytes, 0, IV_BYTES_LENGTH);

        byte[] divideBytes = new byte[mergeBytes.length - IV_BYTES_LENGTH];
        System.arraycopy(mergeBytes, IV_BYTES_LENGTH, divideBytes, 0, divideBytes.length);

        return new byte[][]{ivBytes, divideBytes};
    }

    public static byte[] getSha256Bytes(String data) {
        try {
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");

            byte[] bytes = data.getBytes(StandardCharsets.UTF_8);
            return sha256.digest(bytes);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return null;
    }

}
