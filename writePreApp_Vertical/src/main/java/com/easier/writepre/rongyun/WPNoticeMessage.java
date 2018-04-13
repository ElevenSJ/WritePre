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
 * 公告消息结构体
 * Created by zhoulu on 2016/10/27.
 */
@MessageTag(value = "WP:NtcNtf", flag = MessageTag.ISPERSISTED | MessageTag.ISCOUNTED)
public class WPNoticeMessage extends MessageContent {
    private String message;//公告文字
    private String circleID;//圈子ID
    private String noticeID;//公告id
    private String extra;//其他

    /**
     * 默认构造函数。
     */
    protected WPNoticeMessage() {

    }

    /**
     * 构造函数。
     *
     * @param in 初始化传入的 Parcel。
     */
    public WPNoticeMessage(Parcel in) {
        setExtra(ParcelUtils.readFromParcel(in));
        setMessage(ParcelUtils.readFromParcel(in));
        setCircleID(ParcelUtils.readFromParcel(in));
        setNoticeID(ParcelUtils.readFromParcel(in));
    }

    /**
     * 构造函数。
     *
     * @param data    初始化传入的二进制数据。
     * @param message 此参数代码中并没有调用，后续将废弃。
     */
    public WPNoticeMessage(byte[] data) {
        String jsonStr = null;

        try {
            jsonStr = new String(data, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            RLog.e("JSONException", e.getMessage());
        }

        try {
            JSONObject jsonObj = new JSONObject(jsonStr);

            if (jsonObj.has("message"))
                setMessage(jsonObj.getString("message"));
            if (jsonObj.has("circleID"))
                setCircleID(jsonObj.getString("circleID"));
            if (jsonObj.has("noticeID"))
                setNoticeID(jsonObj.getString("noticeID"));
            if (jsonObj.has("extra"))
                setExtra(jsonObj.getString("extra"));
        } catch (JSONException e) {
            RLog.e("JSONException", e.getMessage());
        }
    }

    /**
     * 构建一个文字消息实例。
     *
     * @return 文字消息实例。
     */
    public static WPNoticeMessage obtain(String msg, String circleId, String noticeId) {
        WPNoticeMessage model = new WPNoticeMessage();
        model.setExtra("");
        model.setMessage(msg);
        model.setCircleID(circleId);
        model.setNoticeID(noticeId);
        return model;
    }

    /**
     * 描述了包含在 Parcelable 对象排列信息中的特殊对象的类型。
     *
     * @return 一个标志位，表明 Parcelable 对象特殊对象类型集合的排列。
     */
    public int describeContents() {
        return 0;
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
            jsonObj.put("message", getMessage());
            jsonObj.put("circleID", getCircleID());
            jsonObj.put("noticeID", getNoticeID());
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

    /**
     * 将类的数据写入外部提供的 Parcel 中。
     *
     * @param dest  对象被写入的 Parcel。
     * @param flags 对象如何被写入的附加标志，可能是 0 或 PARCELABLE_WRITE_RETURN_VALUE。
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        ParcelUtils.writeToParcel(dest, getExtra());
        ParcelUtils.writeToParcel(dest, message);
        ParcelUtils.writeToParcel(dest, circleID);
        ParcelUtils.writeToParcel(dest, noticeID);

    }

    /**
     * 读取接口，目的是要从Parcel中构造一个实现了Parcelable的类的实例处理。
     */
    public static final Creator<WPNoticeMessage> CREATOR = new Creator<WPNoticeMessage>() {
        @Override
        public WPNoticeMessage createFromParcel(Parcel source) {
            return new WPNoticeMessage(source);
        }

        @Override
        public WPNoticeMessage[] newArray(int size) {
            return new WPNoticeMessage[size];
        }
    };

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCircleID() {
        return circleID;
    }

    public void setCircleID(String circleID) {
        this.circleID = circleID;
    }

    public String getNoticeID() {
        return noticeID;
    }

    public void setNoticeID(String noticeID) {
        this.noticeID = noticeID;
    }

    /**
     * 获取文字消息的内容。
     *
     * @return 文字消息的内容。
     */
    public String getExtra() {
        return extra;
    }

    /**
     * 设置消息的附加内容。
     *
     * @param extra 消息的附加内容。
     */
    public void setExtra(String extra) {
        this.extra = extra;
    }
}
