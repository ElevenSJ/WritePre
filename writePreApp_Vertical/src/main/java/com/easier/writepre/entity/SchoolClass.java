package com.easier.writepre.entity;

/**
 * Created by SunJie on 17/3/16.
 */

public class SchoolClass {

    private String value;

    public SchoolClass(String aClass) {
        this.value = aClass;
    }
    //这个用来显示在PickerView上面的字符串,PickerView会通过反射获取getPickerViewText方法显示出来。
    public String getPickerViewText() {
        //这里还可以判断文字超长截断再提供显示
        return value;
    }
}
