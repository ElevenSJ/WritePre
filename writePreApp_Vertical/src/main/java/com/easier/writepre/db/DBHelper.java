package com.easier.writepre.db;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import com.easier.writepre.utils.LogUtils;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.DbUtils.DbUpgradeListener;
import com.lidroid.xutils.exception.DbException;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

public class DBHelper {
    /**
     * 分隔符
     */
    public static final String SLIPT_STORE = "SLIPT;SLIPT";
    // database
    private static final int BUFFER_SIZE = 1024;
    public static final String TABLE_NAME_EXAM = "exam";
    public static final String DB_NAME = "writepre.db";
    public static final String PACKAGE_NAME = "com.easier.writepre";
    public static final String DB_PATH = "/data" + Environment.getDataDirectory().getAbsolutePath() + "/" + PACKAGE_NAME
            + "/databases";
    /**
     * xUtils 提供的数据库操作类
     */
    private static DbUtils db = null;
    /**
     * 数据库版本
     */
    private static final int DB_VERSION = 2;

    private static DBHelper sDBHelper = null;
    private static SQLiteDatabase mWriteDatabase = null;

    public synchronized static DBHelper instance() {
        if (sDBHelper == null) {
            sDBHelper = new DBHelper();
        }

        return sDBHelper;
    }

    private DBHelper() {

    }

    /**
     * 复制数据库文件到本地
     *
     * @param cxt
     * @return
     */
    public static boolean copyDatabaseFile(Context cxt, String fileName) {
        try {
            LogUtils.e("======数据库路径:"+DB_PATH + "/" + DB_NAME);
            if (new File(DB_PATH + "/" + DB_NAME).exists()) {
                return true;
            }

            new File(DB_PATH).mkdir();

            InputStream is = null;
            FileOutputStream fos = new FileOutputStream(new File(DB_PATH + "/" + DB_NAME));

            is = cxt.getAssets().open(fileName);
            byte[] buffer = new byte[BUFFER_SIZE];
            int count = 0;
            while ((count = is.read(buffer)) > 0) {
                fos.write(buffer, 0, count);
            }
            is.close();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public synchronized void open(Context context, String fileName) {
        close();
        copyDatabaseFile(context, fileName);
        mWriteDatabase = context.openOrCreateDatabase(DB_NAME, 0, null);
        if (db == null) {
            db = DbUtils.create(context, DB_NAME, DB_VERSION, new DBUpgrate());
            db.configAllowTransaction(true);
            db.configDebug(true);

        }

    }

    public void insert(String sql) {
        if (mWriteDatabase == null) {
            return;
        }
        mWriteDatabase.execSQL(sql);
    }

    public long insert(String table, ContentValues values) {
        if (mWriteDatabase == null) {
            return -1;
        }
        return mWriteDatabase.insert(table, null, values);
    }

    public int update(String table, ContentValues values, String whereClause, String[] whereArgs) {
        if (mWriteDatabase == null) {
            return -1;
        }
        return mWriteDatabase.update(table, values, whereClause, whereArgs);
    }

    public Cursor query(String table, String[] columns, String selection, String[] selectionArgs, String groupBy,
                        String having, String orderBy) {
        if (mWriteDatabase == null) {
            return null;
        }
        return mWriteDatabase.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
    }

    public Cursor query(String sql) {
        if (mWriteDatabase == null) {
            return null;
        }
        return mWriteDatabase.rawQuery(sql, null);
    }

    public void delete(String sql) {
        if (mWriteDatabase == null) {
            return;
        }
        mWriteDatabase.execSQL(sql);
    }

    public int delete(String table, String whereClause, String[] whereArgs) {
        if (mWriteDatabase == null) {
            return -1;
        }
        return mWriteDatabase.delete(table, whereClause, whereArgs);
    }

    public boolean runTransaction(Runnable runnable) {
        boolean result = false;
        if (mWriteDatabase != null) {

            try {
                // 手动设置开始事务
                mWriteDatabase.beginTransaction();
                // 数据库操作
                runnable.run();
                mWriteDatabase.setTransactionSuccessful(); // 设置事务处理成功，不设置会自动回滚不提交
                result = true;
            } catch (Exception e) {
                System.out.println(e.toString());

            } finally {
                mWriteDatabase.endTransaction(); // 处理完成
            }
        }
        return result;
    }

    public void close() {
        if (mWriteDatabase != null) {
            mWriteDatabase.close();
            mWriteDatabase = null;
        }
    }

    public static DbUtils getExecutor() {
        return db;

    }

    /**
     * 数据库升级
     *
     * @author Tiny
     */
    private static class DBUpgrate implements DbUpgradeListener {

        @Override
        public void onUpgrade(DbUtils db, int oldVersion, int newVersion) {
            LogUtils.e("========================oldVersion="+oldVersion+"***************newVersion="+newVersion);
            if (oldVersion == 1 && newVersion == 2) {
                try {
                    db.execQuery("ALTER TABLE push_message_table ADD COLUMN ext_info TEXT default ''");
                } catch (DbException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                // if (mWriteDatabase == null) {
                // return;
                // }
                // mWriteDatabase.execSQL("ALTER TABLE push_message_table ADD
                // COLUMN ext_info TEXT default ''");
            }
//            if (newVersion >= 2) {
//                try {
//                    db.execQuery("CREATE TABLE IF NOT EXISTS " + TABLE_NAME_EXAM +
//                            "(id integer PRIMARY KEY AUTOINCREMENT NOT NULL, " +
//                            "pkg_id VARCHAR, " +//试卷id
//                            "_id VARCHAR, " +//每道题的id
//                            "key VARCHAR, " +//储存的选项
//                            "sort_num INTEGER)");//试卷中题目的顺序
//                    LogUtils.e("====考试表创建成功!====");
//                } catch (DbException e) {
//                    e.printStackTrace();
//                }
//            }
        }

    }

}
