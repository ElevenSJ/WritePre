package com.easier.writepre.entity;

/**
 * Created by dqt on 2016/10/28.
 */

public class XiaoMiOpenIdInfo {
    private String result="";
    private String description="";
    private String code="";

    private OpenIdData data;

    public class OpenIdData {
        private String openId;

        public String getOpenId() {
            return openId;
        }

        public void setOpenId(String openId) {
            this.openId = openId;
        }
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public OpenIdData getData() {
        return data;
    }

    public void setData(OpenIdData data) {
        this.data = data;
    }
}
