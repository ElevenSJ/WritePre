package com.easier.writepre.entity;

import java.io.Serializable;

/**
 * Created by SunJie on 16/12/1.
 */

public class ContentBase implements Serializable {
    private String _id;
    private String title;
    private String ctime;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCtime() {
        return ctime;
    }

    public void setCtime(String ctime) {
        this.ctime = ctime;
    }
}
