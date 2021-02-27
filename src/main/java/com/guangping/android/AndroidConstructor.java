package com.guangping.android;

import com.guangping.encrypt.DigestAes;

import java.util.Objects;
import java.util.Random;
import java.util.stream.IntStream;

/**
 * <p>
 * 随机创建虚假的安卓手机的SERIAL(序列号)、android_id.
 * <p>
 * android_id为16位的小写字母、数字的混合字符串;
 * SERIAL为13位的大写字母、数字的混合字符串;
 * </p>
 *
 * @author GuangPing Lin
 * @date 2021/2/5 13:37
 */
public abstract class AndroidConstructor {

    /***
     * {@code RANDOM}为随机生成字符串的字符库，
     * 即随机从{@code RANDOM}中取得字符，用以组合成SERIAL、android_id.
     */
    private static final String RANDOM = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnopqrstuvwxyz";


    /***
     * {@code RANDOM_STRING_GENERATOR}是个随机字符串的生成器.
     */
    private static final Random RANDOM_STRING_GENERATOR = new Random();

    /**
     * {@code SERIAL_STRING_SIZE}为生成的SERIAL字符串的总长度.
     */
    private static final int SERIAL_STRING_SIZE = 13;

    /**
     * {@code ANDROID_ID_STRING_SIZE}为生成的android_id字符串的总长度.
     */
    private static final int ANDROID_ID_STRING_SIZE = 16;


    public static String generateRandomSerial() {
        // 选取部分RANDOM的起始下标.
        int offset = 0;
        // 选取部分RANDOM的截止下标+1.
        int end = 36;

        return generateRandomString(SERIAL_STRING_SIZE, offset, end);
    }

    public static String generateRandomAndroidId() {
        int offset = 26;
        int end = RANDOM.length();

        return generateRandomString(ANDROID_ID_STRING_SIZE, offset, end);
    }


    private static String generateRandomString(int stringSize, int offset, int end) {
        // 生成的随机int序列流，该流作为获取RANDOM里的字符的下标.
        IntStream indexStream = RANDOM_STRING_GENERATOR.ints(stringSize, offset, end);

        // 将int序列流转换成int[].
        int[] indexArray = indexStream.toArray();

        // 按照int序列流的int数组，挨个获取RANDOM里的字符，并组合成字符串
        StringBuilder randomString = new StringBuilder(indexArray.length);
        for (int i : indexArray) {
            randomString.append(RANDOM.charAt(i));
        }

        return randomString.toString();
    }

    public static String generateOauthId() {
        String serial = AndroidConstructor.generateRandomSerial();
        String androidId = AndroidConstructor.generateRandomAndroidId();
        byte[] md5Bytes = DigestAes.getMd5Bytes(androidId + serial);
        return DigestAes.bytes2hexString(Objects.requireNonNull(md5Bytes), false);
    }
}
