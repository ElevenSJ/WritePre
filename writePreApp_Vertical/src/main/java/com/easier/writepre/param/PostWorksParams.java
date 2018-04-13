package com.easier.writepre.param;


/**
 * 投寄作品
 */
public class PostWorksParams extends BaseBodyParams {

    private String mail_url;

    private String company;

    private String mail_number;

    private String stu_type;// 报名类别  0小书法师 1专业人才

    public PostWorksParams(String stu_type, String mail_url, String company, String mail_number) {
        this.stu_type = stu_type;
        this.mail_url = mail_url;
        this.company = company;
        this.mail_number = mail_number;
    }

    @Override
    public String getProNo() {
        return "tec_xsf_exam_mail_submit";
    }

    @Override
    public String getUrl() {
        //return (boolean) SPUtils.instance().get(SPUtils.IS_LOGIN, false) ? "app" : "login";
        return "app";
    }
}
