package com.guangping.pojo;

import com.fasterxml.jackson.annotation.JsonMerge;

import java.util.Map;

/**
 * @author GuangPing Lin
 * @date 2021/2/20 18:44
 */
public class Package {
    private String urlString;
    private String method;
    private Map<String, String> header;

    @JsonMerge
    private RequestParameter requestParameter;

    @JsonMerge
    private Data data;

    public String getUrlString() {
        return urlString;
    }

    public void setUrlString(String urlString) {
        this.urlString = urlString;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Map<String, String> getHeader() {
        return header;
    }

    public void setHeader(Map<String, String> header) {
        this.header = header;
    }

    public RequestParameter getRequestParameter() {
        return requestParameter;
    }

    public void setRequestParameter(RequestParameter requestParameter) {
        this.requestParameter = requestParameter;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }
}
