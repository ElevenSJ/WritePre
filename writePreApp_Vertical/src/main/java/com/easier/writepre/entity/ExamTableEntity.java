package com.easier.writepre.entity;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.NoAutoIncrement;
import com.lidroid.xutils.db.annotation.NotNull;
import com.lidroid.xutils.db.annotation.Table;

import java.io.Serializable;

/**
 * 考试表结构体
 * Created by zhoulu on 2017/7/31.
 */
@Table(name = "exam_table")
public class ExamTableEntity implements Serializable {
    @Id
    @NoAutoIncrement
    @NotNull
    @Column(column = "_id")//注释列名
    private String _id;
    @Column(column = "pkg_id")//注释列名
    private String pkg_id;
    @Column(column = "user_select")//注释列名
    private String user_select;
    @Column(column = "sort_num")//注释列名
    private int sort_num;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }


    public String getPkg_id() {
        return pkg_id;
    }

    public void setPkg_id(String pkg_id) {
        this.pkg_id = pkg_id;
    }

    public int getSort_num() {
        return sort_num;
    }

    public void setSort_num(int sort_num) {
        this.sort_num = sort_num;
    }

    public String getUser_select() {
        return user_select;
    }

    public void setUser_select(String user_select) {
        this.user_select = user_select;
    }

    @Override
    public String toString() {
        return "ExamTableEntity{" +
                "_id='" + _id + '\'' +
                ", pkg_id='" + pkg_id + '\'' +
                ", user_select='" + user_select + '\'' +
                ", sort_num=" + sort_num +
                '}';
    }
}
