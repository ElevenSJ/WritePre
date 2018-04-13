package com.easier.writepre.entity;

import java.util.ArrayList;

/**
 * Created by SunJie on 17/3/16.
 */

public class LuoYangSchool {

    private ArrayList<LuoYangSchool.CityValue> array;

    public ArrayList<CityValue> getArray() {
        return array;
    }

    public void setArray(ArrayList<CityValue> array) {
        this.array = array;
    }

    public static class CityValue{
        private String city ;
        private ArrayList<String> list;

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public ArrayList<String> getList() {
            return list;
        }

        public void setList(ArrayList<String> list) {
            this.list = list;
        }
    }
    public static class SchoolValue{
        String value;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public SchoolValue(String value) {
            this.value = value;
        }

        //这个用来显示在PickerView上面的字符串,PickerView会通过反射获取getPickerViewText方法显示出来。
        public String getPickerViewText() {
            //这里还可以判断文字超长截断再提供显示
            return value;
        }
    }
}
