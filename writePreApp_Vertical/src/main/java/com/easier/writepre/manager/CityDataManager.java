package com.easier.writepre.manager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.easier.writepre.db.DBCityHelper;
import com.easier.writepre.entity.CitysList;
import com.easier.writepre.entity.CitysList.CitysBean;
import com.easier.writepre.utils.FileUtils;
import com.easier.writepre.utils.LogUtils;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.text.TextUtils;

public class CityDataManager {

    private static CityDataManager instance;

    private Context ctx;

    private Handler handler;

    public final static int INSERT_OK = 1000010;

    public synchronized static CityDataManager getInstance(Context mCtx, Handler handler) {
        instance = new CityDataManager(mCtx, handler);
        return instance;
    }

    private CityDataManager(Context mCtx, Handler handler) {
        this.ctx = mCtx.getApplicationContext();
        this.handler = handler;
    }

    public void getCityData(final String url) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                if (!TextUtils.isEmpty(url)) {
                    String cityText = FileUtils.SD_DOWN_PATH;
                    File file = new File(cityText);
                    if (!file.exists()) {
                        file.mkdirs();
                    }
                    new HttpUtils().download(url, cityText + "/citys.json", false, true, new RequestCallBack<File>() {

                        @Override
                        public void onSuccess(ResponseInfo<File> arg0) {
                            parseData();
                        }

                        @Override
                        public void onFailure(HttpException arg0, String arg1) {

                        }
                    });
                }
            }
        }).start();
    }

    private void parseData() {

        String jsonText = FileUtils.getJson(FileUtils.SD_DOWN_PATH + "/citys.json");

        Gson gson = new Gson();

        JsonParser parser = new JsonParser();
        JsonArray Jarray = parser.parse(jsonText).getAsJsonArray();

        List<CitysBean> citysList = new ArrayList<CitysBean>();

        ArrayList<CitysList> lcs = new ArrayList<CitysList>();
        for (JsonElement obj : Jarray) {
            CitysList cse = gson.fromJson(obj, CitysList.class);
            lcs.add(cse);
        }

        for (int i = 0; i < lcs.size(); i++) {
            for (int j = 0; j < lcs.get(i).getCitys().size(); j++) {
                citysList.add(lcs.get(i).getCitys().get(j));
            }
        }
        // ToastUtil.show("所有城市size-->" + citysList.size());
        if (citysList.size() > 0) { // 插入数据
            insertCity(citysList);
        } else {
            handler.sendEmptyMessage(INSERT_OK);
        }
    }

    SQLiteDatabase db;

    private void insertCity(final List<CitysBean> citysList) {

        new Thread(new Runnable() {

            @Override
            public void run() {
                DBCityHelper dbHelper = new DBCityHelper(ctx);
                try {
                    dbHelper.createDataBase();
                    db = dbHelper.getWritableDatabase();
                    db.execSQL("delete from city");
                    db.beginTransaction();
                    long time = System.currentTimeMillis();
                    for (int i = 0; i < citysList.size(); i++) {
                        db.execSQL(
                                "insert into city(id,bcode,longitude,latitude,full_spell,min_spell,name,sname) values('"
                                        + (i + 1) + "','" + citysList.get(i).getBcode() + "','"
                                        + citysList.get(i).getCoord().get(0) + "','"
                                        + citysList.get(i).getCoord().get(1) + "','" + citysList.get(i).getFull_spell()
                                        + "','" + citysList.get(i).getMin_spell() + "','" + citysList.get(i).getName()
                                        + "','" + citysList.get(i).getSname() + "')");
                    }
                    db.setTransactionSuccessful();
                    LogUtils.e("耗时--->" + (System.currentTimeMillis() - time));
                    handler.sendEmptyMessage(INSERT_OK);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    db.endTransaction();
                    db.close();
                    dbHelper.close();
                }

            }
        }).start();

    }

}
