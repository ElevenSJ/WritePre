package com.easier.writepre.rongyun;

import android.os.Parcel;
import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import io.rong.common.ParcelUtils;
import io.rong.common.RLog;
import io.rong.imlib.MessageTag;
import io.rong.imlib.model.MessageContent;

/**
 * 视频消息结构体
 * Created by zhoulu on 2016/10/27.
 */
@MessageTag(value = "WP:SvdMsg", flag = MessageTag.ISCOUNTED | MessageTag.ISPERSISTED)
public class WPShortVideoMessage extends MessageContent {
    private String imageContent;
    private String fileUri;
    private String extra;
    /**
     * 默认构造函数。
     */
    protected WPShortVideoMessage() {
    }

    protected WPShortVideoMessage(String imageContent, String fileUri, String extra) {
        this.imageContent = imageContent;
        this.fileUri = fileUri;
        this.extra = extra;
    }

    protected WPShortVideoMessage(String imageContent, String fileUri) {
        this.imageContent = imageContent;
        this.fileUri = fileUri;
    }

    /**
     * 构建一个视频消息实例。
     *
     * @return 视频消息实例。
     */
    public static WPShortVideoMessage obtain(String imageContent, String fileUri, String extra) {
        WPShortVideoMessage model = new WPShortVideoMessage(imageContent, fileUri, extra);
        return model;
    }

    /**
     * 构建一个视频消息实例。
     *
     * @return 视频消息实例。
     */
    public static WPShortVideoMessage obtain(String imageContent, String fileUri) {
        WPShortVideoMessage model = new WPShortVideoMessage(imageContent, fileUri);
        return model;
    }

    public static WPShortVideoMessage obtain() {
        return new WPShortVideoMessage();
    }

    /**
     * 构造函数。
     *
     * @param data 初始化传入的二进制数据。
     */
    public WPShortVideoMessage(byte[] data) {
        String jsonStr = null;

        try {
            jsonStr = new String(data, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            RLog.e("JSONException", e.getMessage());
        }

        try {
            JSONObject jsonObj = new JSONObject(jsonStr);

            if (jsonObj.has("imageContent"))
                setImageContent(jsonObj.optString("imageContent"));
            if (jsonObj.has("fileUri"))
                setFileUri(jsonObj.optString("fileUri"));
            if (jsonObj.has("extra"))
                setExtra(jsonObj.optString("extra"));
        } catch (JSONException e) {
            RLog.e("JSONException", e.getMessage());
        }
    }

    /**
     * 将本地消息对象序列化为消息数据。
     *
     * @return 消息数据。
     */
    @Override
    public byte[] encode() {
        JSONObject jsonObj = new JSONObject();

        try {
            jsonObj.put("imageContent", getImageContent());
            jsonObj.put("fileUri", getFileUri());
            if (!TextUtils.isEmpty(getExtra()))
                jsonObj.put("extra", getExtra());

        } catch (JSONException e) {
            RLog.e("JSONException", e.getMessage());
        }

        try {
            return jsonObj.toString().getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * 构造函数。
     *
     * @param in 初始化传入的 Parcel。
     */
    public WPShortVideoMessage(Parcel in) {
        this.setImageContent(ParcelUtils.readFromParcel(in));
        this.setFileUri(ParcelUtils.readFromParcel(in));
        this.setExtra(ParcelUtils.readFromParcel(in));
    }

    /**
     * 读取接口，目的是要从Parcel中构造一个实现了Parcelable的类的实例处理。
     */
    public static final Creator<WPShortVideoMessage> CREATOR = new Creator<WPShortVideoMessage>() {
        @Override
        public WPShortVideoMessage createFromParcel(Parcel source) {
            return new WPShortVideoMessage(source);
        }

        @Override
        public WPShortVideoMessage[] newArray(int size) {
            return new WPShortVideoMessage[size];
        }
    };

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        ParcelUtils.writeToParcel(parcel, this.getImageContent());
        ParcelUtils.writeToParcel(parcel, this.getFileUri());
        ParcelUtils.writeToParcel(parcel, this.getExtra());
    }

    public String getImageContent() {
        return imageContent;
    }

    public void setImageContent(String imageContent) {
        this.imageContent = imageContent;
    }

    public String getFileUri() {
        return fileUri;
    }

    public void setFileUri(String fileUri) {
        this.fileUri = fileUri;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }
}
