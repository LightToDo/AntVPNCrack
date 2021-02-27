package com.guangping.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author GuangPing Lin
 * @date 2021/2/20 18:51
 */
public class RequestParameter {
    private String appId;
    private String appVersion;
    private String data;
    private String timestamp;
    private String sign;

    @JsonIgnore
    private Map<String, String> map = new LinkedHashMap<>(5);

    public RequestParameter() {
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
        map.put("appId", appId);
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
        map.put("appVersion", appVersion);
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
        map.put("timestamp", timestamp);
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
        map.put("sign", sign);
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
        map.put("data", data);
    }

    public Map<String, String> getMap() {
        return map;
    }

    public void setMap(Map<String, String> map) {
        this.map = map;
    }
}
