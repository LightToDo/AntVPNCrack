package com.guangping.post;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author GuangPing Lin
 * @date 2021/2/20 19:50
 */
public class AntResponsePackage {
    @JsonProperty("errcode")
    private int errorCode;
    private long timestamp;
    private String data;
    private String sign;

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getData() {
        return data;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }
}
