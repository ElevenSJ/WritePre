package com.easier.writepre.db;

import com.easier.writepre.entity.ExamTableEntity;
import com.easier.writepre.utils.SPUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;

import java.util.ArrayList;
import java.util.List;

/**
 * 考试数据操作类
 * Created by zhoulu on 2017/7/31.
 */

public class DBExamManager {
    public static boolean inster(ExamTableEntity examTableEntity)
    {
        boolean result = false;
        if (examTableEntity == null) {
            return result;
        }
        try {
            if (isExit(examTableEntity.get_id()) != null) {
                return result;
            } else {
                DBHelper.getExecutor().save(examTableEntity);
            }
            result = true;
        } catch (DbException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return result;
        }
        return result;
    }
    public static ExamTableEntity isExit(String _id) {
        ExamTableEntity entity = null;
        try {
            entity = DBHelper.getExecutor().findById(ExamTableEntity.class, _id);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return entity;
        }
        return entity;
    }
    public static ArrayList<ExamTableEntity> queryAll() {
        ArrayList<ExamTableEntity> list = new ArrayList<ExamTableEntity>();
        try {
            List<ExamTableEntity> entitys = DBHelper.getExecutor()
                    .findAll(Selector.from(ExamTableEntity.class).orderBy("sort_num", true));
            list.addAll(entitys);
        } catch (Exception e) {
            // TODO: handle exception
            System.out.println(e);
            return list;
        }
        return list;
    }

    public static ArrayList<ExamTableEntity> queryAllByPkgId(String pkg_id) {
        ArrayList<ExamTableEntity> list = new ArrayList<ExamTableEntity>();
        try {
            List<ExamTableEntity> entitys = DBHelper.getExecutor().findAll(Selector.from(ExamTableEntity.class)
                    .where(WhereBuilder.b("pkg_id", "=", pkg_id)).orderBy("sort_num", true));
            list.addAll(entitys);
        } catch (Exception e) {
            // TODO: handle exception
            System.out.println(e);
            return list;
        }
        return list;
    }


    public static ExamTableEntity queryById(String _id) {
        ExamTableEntity entity = null;
        try {
            entity = DBHelper.getExecutor()
                    .findFirst(Selector.from(ExamTableEntity.class).where(WhereBuilder.b("_id", "=", _id)));
        } catch (Exception e) {
            // TODO: handle exception
            System.out.println(e);
            return entity;
        }
        return entity;

    }

    public static boolean delRes(ExamTableEntity entity) {
        boolean result = false;
        try {
            DBHelper.getExecutor().delete(entity);
            result = true;

        } catch (Exception e) {
            // TODO: handle exception
            System.out.println(e);
        }
        return result;
    }

    public static boolean delAll() {
        boolean result = false;
        try {
            DBHelper.getExecutor().deleteAll(ExamTableEntity.class);
            result = true;

        } catch (Exception e) {
            // TODO: handle exception
            System.out.println(e);
        }
        return result;
    }

    public static void update(ExamTableEntity entity) {
        try {
            DBHelper.getExecutor().saveOrUpdate(entity);
        } catch (DbException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
