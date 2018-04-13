package com.easier.writepre.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import com.easier.writepre.app.WritePreApp;
import com.easier.writepre.entity.LoginEntity;
import com.easier.writepre.entity.OssTokenInfo;
import com.easier.writepre.entity.SocialPropEntity;
import com.easier.writepre.entity.WelcomeAdvInfo;
import com.google.gson.Gson;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;
import io.rong.imkit.RongIM;

/**
 * 本地持久化存儲 管理类
 * <p/>
 * sunjie
 */
public class SPUtils {

    /**
     * instance
     */
    private static SPUtils spUtils = null;

    public static final String FILE_NAME = "message_config";

    private SharedPreferences sp = null;

    // 设备唯一标识
    public static final String DEVICE_ID = "device_uuid";

    // 系统通知的lastid
    public static final String COMM_MESSAGE_LASTID = "comm_message_lastid";

    // 个人通知的lastid
    public static final String USER_MESSAGE_LASTID = "user_message_lastid";

    // 背景音乐默认开启状态设置
    public static final String BG_MUSIC_DEFUALT_OPEN = "bg_music_open";

    // 用户登录状态
    public static final String IS_LOGIN = "is_login";

    // 用户登录名
    public static final String LOGIN_NAME = "login_name";

    // 用户登录密码
    public static final String LOGIN_PWD = "login_pwd";

    // 用户登录成功cooker
    public static final String LOGIN_COOKER = "login_cooker";

    // 用户登录下发数据
    public static final String LOGIN_DATA = "login_data";

    // 登陆社交配置数据
    public static final String SOCIAL_PROP_DATA = "social_prop_data";
    // OSS配置数据
    public static final String OSS_DATA = "oss_data";
    // 装机标识
    public static final String INSTALL_FLAG = "install_flag";
    // 启动广告
    public static final String WELCOME_ADV = "welcome_adv";
    // 城市数据地址
    public static final String CITY_DATA_URL = "city_data_url";
    // 融云Token
    public static final String RONGYUN_TOKEN = "rongyun_token";
    //定位保存的城市
    public static final String CURRENT_CITY = "currnt_city";
    //小米登陆openid
    public static final String MI_OPENID = "mi_openid";
    //小米登陆token
    public static final String MI_TOKEN = "mi_token";
    //碑帖滑动方向（隐藏或展示提示图片）
    public static final String SHOW_HIDE = "show_hide";
    //微信支付appId
    public static final String WECHAT_APPID = "WeChatAppId";
    //小书法师报考页面蒙版
    public static final String MASK_EXAMINATION = "mask_examination";
    //小书法师考试超时后弹窗提示:目前服务端数据库设计缺陷 超时后 无法重置只能由客户端控制 只显示一次
    public static final String EXAMTIMEOUT = "examtimeout";

    /**
     * 返回实例
     *
     * @return
     */
    public static SPUtils instance() {
        if (spUtils == null) {
            spUtils = new SPUtils();
        }

        return spUtils;
    }

    /**
     * init shared Preferences
     *
     * @param context
     */
    public void init(Context context) {
        sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
    }

    /**
     * 保存数据的方法，我们需要拿到保存数据的具体类型，然后根据类型调用不同的保存方法
     *
     * @param key
     * @param object
     */
    public void put(String key, Object object) {

        SharedPreferences.Editor editor = sp.edit();

        if (object instanceof String) {
            editor.putString(key, (String) object);
        } else if (object instanceof Integer) {
            editor.putInt(key, (Integer) object);
        } else if (object instanceof Boolean) {
            editor.putBoolean(key, (Boolean) object);
        } else if (object instanceof Float) {
            editor.putFloat(key, (Float) object);
        } else if (object instanceof Long) {
            editor.putLong(key, (Long) object);
        } else {
            editor.putString(key, object.toString());
        }

        SharedPreferencesCompat.apply(editor);
    }

    /**
     * 得到保存数据的方法，我们根据默认值得到保存的数据的具体类型，然后调用相对于的方法获取值
     *
     * @param key
     * @param defaultObject
     * @return
     */
    public Object get(String key, Object defaultObject) {
        if (defaultObject == null) {
            defaultObject = "";
        }
        if (defaultObject instanceof String) {
            return sp.getString(key, (String) defaultObject);
        } else if (defaultObject instanceof Integer) {
            return sp.getInt(key, (Integer) defaultObject);
        } else if (defaultObject instanceof Boolean) {
            return sp.getBoolean(key, (Boolean) defaultObject);
        } else if (defaultObject instanceof Float) {
            return sp.getFloat(key, (Float) defaultObject);
        } else if (defaultObject instanceof Long) {
            return sp.getLong(key, (Long) defaultObject);
        }

        return null;
    }

    /**
     * 移除某个key值已经对应的值
     *
     * @param key
     */
    public void remove(String key) {
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(key);
        SharedPreferencesCompat.apply(editor);
    }

    /**
     * 清除所有数据
     */
    public void clear() {
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        SharedPreferencesCompat.apply(editor);
    }

    /**
     * 查询某个key是否已经存在
     *
     * @param key
     * @return
     */
    public boolean contains(String key) {
        return sp.contains(key);
    }

    /**
     * 返回所有的键值对
     *
     * @param context
     * @return
     */
    public Map<String, ?> getAll(Context context) {
        return sp.getAll();
    }

    /**
     * 创建一个解决SharedPreferencesCompat.apply方法的一个兼容类
     *
     * @author jerry
     */
    private static class SharedPreferencesCompat {
        private static final Method sApplyMethod = findApplyMethod();

        /**
         * 反射查找apply的方法
         *
         * @return
         */
        @SuppressWarnings({"unchecked", "rawtypes"})
        private static Method findApplyMethod() {
            try {
                Class clz = SharedPreferences.Editor.class;
                return clz.getMethod("apply");
            } catch (NoSuchMethodException e) {
            }

            return null;
        }

        /**
         * 如果找到则使用apply执行，否则使用commit
         *
         * @param editor
         */
        public static void apply(SharedPreferences.Editor editor) {
            try {
                if (sApplyMethod != null) {
                    sApplyMethod.invoke(editor);
                    return;
                }
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            editor.commit();
        }
    }

    /**
     * 获取保存的融云Token
     *
     * @return token
     */
    public String getToken() {
        String token = (String) SPUtils.instance().get(SPUtils.RONGYUN_TOKEN,
                "");
        return token;
    }

    public LoginEntity getLoginEntity() {
        LoginEntity entity = null;
        String json = (String) SPUtils.instance().get(SPUtils.LOGIN_DATA, "");
        if (!TextUtils.isEmpty(json)) {
            entity = new Gson().fromJson(json, LoginEntity.class);
        }
        if (entity == null) {
            entity = new LoginEntity();
        }
        return entity;
    }

    public SocialPropEntity getSocialPropEntity() {
        SocialPropEntity entity = null;
        String json = (String) SPUtils.instance().get(SPUtils.SOCIAL_PROP_DATA,
                "");
        if (!TextUtils.isEmpty(json)) {
            entity = new Gson().fromJson(json, SocialPropEntity.class);
        }
        if (entity == null) {
            entity = new SocialPropEntity();
        }
        return entity;
    }

    public WelcomeAdvInfo getWelcomeAdvInfo() {
        WelcomeAdvInfo entity = null;
        String json = (String) SPUtils.instance().get(SPUtils.WELCOME_ADV, "");
        if (!TextUtils.isEmpty(json)) {
            entity = new Gson().fromJson(json, WelcomeAdvInfo.class);
        }
        if (entity == null) {
            entity = new WelcomeAdvInfo();
        }
        return entity;
    }

    public OssTokenInfo getOSSTokenInfo() {
        OssTokenInfo entity = null;
        String json = (String) SPUtils.instance().get(SPUtils.OSS_DATA, "");
        if (!TextUtils.isEmpty(json)) {
            entity = new Gson().fromJson(json, OssTokenInfo.class);
        }
        if (entity == null) {
            entity = new OssTokenInfo();
        }
        return entity;
    }

    public void unLogin() {
        remove(SPUtils.IS_LOGIN);
        remove(SPUtils.LOGIN_PWD);
        remove(SPUtils.LOGIN_DATA);
        remove(SPUtils.LOGIN_COOKER);
        remove(SPUtils.RONGYUN_TOKEN);
        remove(SPUtils.EXAMTIMEOUT);
        if (RongIM.getInstance() != null) {
            RongIM.getInstance().logout();
        }
        Platform qq_exit = ShareSDK.getPlatform(WritePreApp.getApp().getApplication(), QQ.NAME);
        if (qq_exit.isValid()) {
            qq_exit.removeAccount();
        }
        Platform sina_exit = ShareSDK.getPlatform(WritePreApp.getApp().getApplication(),
                SinaWeibo.NAME);
        if (sina_exit.isValid()) {
            sina_exit.removeAccount();
        }
        Platform wechat_exit = ShareSDK.getPlatform(WritePreApp.getApp().getApplication(),
                Wechat.NAME);
        if (wechat_exit.isValid()) {
            wechat_exit.removeAccount();
        }
        remove(SPUtils.MI_TOKEN);
        remove(SPUtils.MI_OPENID);
    }

}