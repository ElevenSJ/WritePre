package com.easier.writepre.param;

/**
 * 作品搜索
 *
 * @author kai.zhong
 */
public class PkWorksSearchParams extends BaseBodyParams {

    private String pk_id;

    private String last_id;

    private String city;

    private String key_words;

    private int count;


    public PkWorksSearchParams(String pk_id, String last_id, String city, String key_words,
                               int count) {
        this.pk_id = pk_id;
        this.last_id = last_id;
        this.city = city;
        this.key_words = key_words;
        this.city = city;
        this.count = count;
    }

    @Override
    public String getProNo() {
        return "sj_pk_works_search";
    }

    @Override
    public String getUrl() {
        //return "app";
        return "login";
    }

}
