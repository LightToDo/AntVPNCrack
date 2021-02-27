package com.guangping.post;

import com.guangping.encrypt.DigestAes;

import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;

import static java.util.Objects.isNull;


/**
 * @author GuangPing Lin
 * @date 2021/2/8 21:57
 */
public class AntRequestPackage {

    private static final String AES_ENCRYPTION_KEY = "fjeldkb4438b1eb36b7e244b37dhg03j";

    private static final int KEY_BYTES_LENGTH = 32;
    private static final byte[] KEY_BYTES;

    static {
        KEY_BYTES = DataHandler.evpBytes(KEY_BYTES_LENGTH, null, AES_ENCRYPTION_KEY.getBytes(), 0)[0];
    }

    public static String encryptData(String data) {
        String encryptData = null;

        try {
            byte[] encryptBytes = DigestAes.encrypt(DigestAes.AesModel.CFB_NO_PADDING, KEY_BYTES, data.getBytes(StandardCharsets.UTF_8), null);
            encryptData = DigestAes.bytes2hexString(encryptBytes, true);
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }

        return encryptData;
    }

    public static String decryptData(String data) {
        byte[] dataJsonStringBytes = DigestAes.hexString2bytes(data);
        byte[] decryptBytes = DigestAes.decrypt(DigestAes.AesModel.CFB_NO_PADDING, KEY_BYTES, dataJsonStringBytes);
        if (isNull(decryptBytes)) {
            return null;
        }
        return new String(decryptBytes, StandardCharsets.UTF_8);
    }

}
