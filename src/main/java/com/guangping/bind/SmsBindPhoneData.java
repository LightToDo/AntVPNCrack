package com.guangping.bind;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.guangping.pojo.Data;

/**
 * @author GuangPing Lin
 * @date 2021/2/21 5:12
 */
public class SmsBindPhoneData extends Data {
    private String mobile;
    @JsonProperty("country_code")
    private String countryCode;
    private int type;

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

}
