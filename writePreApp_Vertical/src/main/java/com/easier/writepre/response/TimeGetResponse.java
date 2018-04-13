package com.easier.writepre.response;

import android.text.TextUtils;

import java.io.Serializable;

/**
 * 学时获取返回结构体
 *
 * @author zhoulu
 */
public class TimeGetResponse extends BaseResponse {
    private TimeGetResponse.Body repBody;

    public TimeGetResponse.Body getRepBody() {
        return repBody;
    }

    public void setRepBody(TimeGetResponse.Body repBody) {
        this.repBody = repBody;
    }

    public class Body implements Serializable {
        String study_status;

        public String getStudy_status() {
            return study_status;
        }

        public void setStudy_status(String study_status) {
            this.study_status = study_status;
        }
    }
}
