package com.easier.writepre.entity;

import android.content.Context;

import com.easier.writepre.BuildConfig;
import com.easier.writepre.utils.SPUtils;
import com.umeng.analytics.MobclickAgent;

import java.util.List;

/**
 * Created by dqt on 2016/11/1.
 * 友盟自定义结构化事件
 */

    public enum YouMengType {
         SHEJIAO("社交模块",1), KECHENG("课程模块",0), FAXIAN("发现模块",2), WOD("个人模块",3),BANNER("广告模块",4);
        // 成员变量
        private String name;
        private int index;

        // 构造方法
        private YouMengType(String name, int index) {
            this.name = name;
            this.index = index;
        }

        // 普通方法
        public static String getName(int index) {
            for (YouMengType c : YouMengType.values()) {
                if (c.getIndex() == index) {
                    return c.name;
                }
            }
            return "";
        }


    public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public static void onEvent(Context var0, List<String> var1, int var2, String var3) {
            if (!(boolean)SPUtils.instance().get("debug",BuildConfig.DEBUG)){
                MobclickAgent.onEvent(var0, var1,var2,var3);
            }
        }

}
