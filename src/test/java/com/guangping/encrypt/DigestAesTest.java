package com.guangping.encrypt;


import org.junit.Test;

import java.nio.charset.StandardCharsets;

import static java.util.Objects.isNull;

public class DigestAesTest {

    @Test
    public void getSha256Bytes() {
        byte[] sha256Bytes = DigestAes.getSha256Bytes("2021-01-12");
        if (!isNull(sha256Bytes)) {
            String s = DigestAes.bytes2hexString(sha256Bytes, false).substring(1, 17);
            System.out.println(s);
        }
    }
}