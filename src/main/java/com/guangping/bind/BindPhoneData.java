package com.guangping.bind;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author GuangPing Lin
 * @date 2021/2/22 17:19
 */
public class BindPhoneData extends SmsBindPhoneData {
    @JsonProperty("v_code")
    private String vCode;
    private String msgSystemVer;

    public String getVCode() {
        return vCode;
    }

    public void setVCode(String vCode) {
        this.vCode = vCode;
    }

    public String getMsgSystemVer() {
        return msgSystemVer;
    }

    public void setMsgSystemVer(String msgSystemVer) {
        this.msgSystemVer = msgSystemVer;
    }
}
