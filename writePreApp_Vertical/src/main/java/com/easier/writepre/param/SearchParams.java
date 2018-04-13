package com.easier.writepre.param;

import com.easier.writepre.utils.LoginUtil;
import com.easier.writepre.utils.SPUtils;

/**
 * 搜索请求参数
 *
 * @author zhoulu
 */
public class SearchParams extends BaseBodyParams {

    private String start;
    private String count;
    private String key_words = "";
    private String is_teacher = "";
    private String addr = "";

    public SearchParams(String key_words, String is_teacher, String addr, String start, String count) {
        this.key_words = key_words;
        this.is_teacher = is_teacher;
        this.addr = addr;
        this.start = start;
        this.count = count;
    }

    @Override
    public String getProNo() {
        return "sj_user_search";
    }

    @Override
    public String getUrl() {
//        return SPUtils.instance().getLoginEntity() == null ? "login" : "app";
        return "login";
    }
}
