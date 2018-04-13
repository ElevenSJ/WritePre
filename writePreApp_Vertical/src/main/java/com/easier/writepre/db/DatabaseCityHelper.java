package com.easier.writepre.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.easier.writepre.utils.LogUtils;

public class DatabaseCityHelper extends SQLiteOpenHelper {
	// 类没有实例化,是不能用作父类构造器的参数,必须声明为静态
	private static final String name = "city"; // 数据库名称
	private static final int version = 1; // 数据库版本

	public DatabaseCityHelper(Context context) {
		super(context, name, null, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		LogUtils.e("info create table");
		db.execSQL("CREATE TABLE IF NOT EXISTS recentcity (id integer primary key autoincrement, name varchar(40), date INTEGER)");
		db.execSQL("CREATE TABLE IF NOT EXISTS pic (id TEXT, title TEXT, face_url TEXT, sub_id TEXT, sub_url TEXT, sub_index TEXT)");
	    //db.execSQL("CREATE TABLE IF NOT EXISTS pic (id varchar(64), title varchar(40), face_url varchar(256), sub_id varchar(64), sub_url varchar(256), sub_index INTEGER)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}
}
