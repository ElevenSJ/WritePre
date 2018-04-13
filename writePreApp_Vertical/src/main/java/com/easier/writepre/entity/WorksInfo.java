package com.easier.writepre.entity;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.NotNull;
import com.lidroid.xutils.db.annotation.Table;

import java.io.Serializable;

/**
 * 作品表结构体
 * Created by zhongkai on 2017/9/31.
 */
@Table(name = "works_table")
public class WorksInfo implements Serializable {
    @Id
    @NotNull
    @Column(column = "id")//注释列名
    private int id;
    @Column(column = "loc_path")//注释列名
    private String loc_path;
    @Column(column = "oss_path")//注释列名
    private String oss_path;
    @Column(column = "up_state")//注释列名
    private String up_state;
    @Column(column = "sort_num")//注释列名
    private String sort_num;

    public WorksInfo(String loc_path, String oss_path, String up_state, String sort_num) {
        this.loc_path = loc_path;
        this.oss_path = oss_path;
        this.up_state = up_state;
        this.sort_num = sort_num;
    }

    public void setUp_state(String up_state) {
        this.up_state = up_state;
    }

    public String getUp_state() {
        return up_state;
    }


    public void setId(int id) {
        this.id = id;
    }

    public void setLoc_path(String loc_path) {
        this.loc_path = loc_path;
    }

    public void setOss_path(String oss_path) {
        this.oss_path = oss_path;
    }


    public void setSort_num(String sort_num) {
        this.sort_num = sort_num;
    }

    public int getId() {
        return id;
    }

    public String getLoc_path() {
        return loc_path;
    }

    public String getOss_path() {
        return oss_path;
    }

    public String getSort_num() {
        return sort_num;
    }
}
