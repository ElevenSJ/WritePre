package com.easier.writepre.entity;

/**
 * 学院风采实体
 * Created by zhaomaohan on 2017/1/12.
 */

public class CollegeNewsInfo {

    private int image;

    private String title;

    private String content;

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public CollegeNewsInfo(int image, String title, String content) {
        this.image = image;
        this.title = title;
        this.content = content;
    }
}
