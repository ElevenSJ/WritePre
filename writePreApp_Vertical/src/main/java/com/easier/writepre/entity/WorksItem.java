package com.easier.writepre.entity;


public class WorksItem {
    private String pra_id;
    private String pic_url_1;
    private String pic_url_2;
    private String video_url;

    public WorksItem(String pra_id, String video_url, String pic_url_1, String pic_url_2) {
        this.pra_id = pra_id;
        this.pic_url_1 = pic_url_1;
        this.pic_url_2 = pic_url_2;
        this.video_url = video_url;
    }
}
