package com.easier.writepre.param;

public class TopicPostCommentParams extends BaseBodyParams {

    private String post_id; // 帖子id
    private String reply_to; // 回复填：评论id,新增评论填空串
    private String reply_to_user; // 回复给谁, 新增时填空串
    private String title; // 回复内容
    private String voice_url="";//音频网络url
    private String voice_len="0";//音频时长

    public TopicPostCommentParams(String post_id, String reply_to, String reply_to_user, String title, String voice_url, String voice_len) {
        this.post_id = post_id;
        this.reply_to = reply_to;
        this.reply_to_user = reply_to_user;
        this.title = title;
        this.voice_url = voice_url;
        this.voice_len = voice_len;
    }

    @Override
    public String getProNo() {
        return "sj_square_post_remark";
    }

    @Override
    public String getUrl() {
        return "app";
    }
}
