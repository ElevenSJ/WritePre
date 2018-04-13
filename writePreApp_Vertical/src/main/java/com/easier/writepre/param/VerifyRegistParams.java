package com.easier.writepre.param;

public class VerifyRegistParams extends BaseBodyParams
{

    private String tel;
    private String code;
    private String pwd;

    public VerifyRegistParams(String tel, String pwd, String code)
    {
        this.tel = tel;
        this.pwd = pwd;
        this.code = code;
    }

    @Override
    public String getProNo()
    {
        return "app_tel_regis";
    }

    @Override
    public String getUrl()
    {
        return "/writePieWeb/login";
    }

}
