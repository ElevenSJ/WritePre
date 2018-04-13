package com.easier.writepre.entity;

/**
 * 学院的菜单实体
 * Created by zhaomaohan on 2017/1/12.
 */

public class CollegeMenuInfo {

    private int image;

    private String text;

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public CollegeMenuInfo(int image, String text) {
        this.image = image;
        this.text = text;
    }
}
