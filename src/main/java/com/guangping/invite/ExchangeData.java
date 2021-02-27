package com.guangping.invite;

import com.guangping.pojo.Data;

/**
 * @author GuangPing Lin
 * @date 2021/2/21 2:00
 */
public class ExchangeData extends Data {
    private String aff;

    public String getAff() {
        return aff;
    }

    public void setAff(String aff) {
        this.aff = aff;
    }

    @Override
    public String toString() {
        return "ExchangeData{" +
                "aff='" + aff + '\'' +
                '}';
    }
}
