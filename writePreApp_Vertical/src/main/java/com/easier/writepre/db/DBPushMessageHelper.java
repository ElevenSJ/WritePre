package com.easier.writepre.db;

import java.util.ArrayList;
import java.util.List;

import com.easier.writepre.entity.PushMessageEntity;
import com.easier.writepre.utils.SPUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;

public class DBPushMessageHelper {
	public static boolean insert(PushMessageEntity entity) {
		boolean result = false;
		if (entity == null) {
			return result;
		}
		try {
			if (isExit(entity.get_id()) != null) {
				return result;
			} else {
				DBHelper.getExecutor().save(entity);
			}
			result = true;
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return result;
		}
		return result;
	}

	private static PushMessageEntity isExit(String id) {
		PushMessageEntity entity = null;
		try {
			entity = DBHelper.getExecutor().findById(PushMessageEntity.class, id);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return entity;
		}
		return entity;
	}

	public static ArrayList<PushMessageEntity> queryAll() {
		ArrayList<PushMessageEntity> list = new ArrayList<PushMessageEntity>();
		try {
			List<PushMessageEntity> entitys = DBHelper.getExecutor()
					.findAll(Selector.from(PushMessageEntity.class).orderBy("ctime", true));
			list.addAll(entitys);
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e);
			return list;
		}
		return list;
	}

	public static ArrayList<PushMessageEntity> queryAllByUserId(String userId) {
		ArrayList<PushMessageEntity> list = new ArrayList<PushMessageEntity>();
		try {
			List<PushMessageEntity> entitys = DBHelper.getExecutor().findAll(Selector.from(PushMessageEntity.class)
					.where(WhereBuilder.b("user_id", "=", userId)).orderBy("ctime", true));
			list.addAll(entitys);
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e);
			return list;
		}
		return list;
	}

	public static ArrayList<PushMessageEntity> queryAll(String userId) {
		ArrayList<PushMessageEntity> list = new ArrayList<PushMessageEntity>();
		try {
			if ((boolean) SPUtils.instance().get(SPUtils.IS_LOGIN, false)) {
				List<PushMessageEntity> entitys = DBHelper.getExecutor().findAll(Selector.from(PushMessageEntity.class)
						.where(WhereBuilder.b("user_id", "=", userId).or("user_id", "=", "")).orderBy("ctime", true));
				list.addAll(entitys);
			} else {
				List<PushMessageEntity> entitys = DBHelper.getExecutor().findAll(Selector.from(PushMessageEntity.class)
						.where(WhereBuilder.b("user_id", "=", "")).orderBy("ctime", true));
				list.addAll(entitys);
			}
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e);
			return list;
		}
		return list;
	}

	public static int queryAllNoRead(String user_id) {
		int count = 0;
		try {
			if ((boolean) SPUtils.instance().get(SPUtils.IS_LOGIN, false)) {
				count += DBHelper.getExecutor()
						.findAll(Selector.from(PushMessageEntity.class)
								.where(WhereBuilder.b("isRead", "=", "0").and("user_id", "=", user_id)).orderBy("ctime",
										true))
						.size();
				count += DBHelper.getExecutor()
						.findAll(Selector.from(PushMessageEntity.class)
								.where(WhereBuilder.b("isRead", "=", "0").and("user_id", "=", "")).orderBy("ctime",
										true))
						.size();
			} else {
				count += DBHelper.getExecutor()
						.findAll(Selector.from(PushMessageEntity.class)
								.where(WhereBuilder.b("isRead", "=", "0").and("user_id", "=", "")).orderBy("ctime",
										true))
						.size();
			}

		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e);
		}
		return count;
	}

	public static long queryAllCount() {
		long count = 0;
		try {
			count = DBHelper.getExecutor().count(PushMessageEntity.class);
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e);
			return count;
		}
		return count;

	}

	public static PushMessageEntity queryById(int id) {
		PushMessageEntity entity = null;
		try {
			entity = DBHelper.getExecutor()
					.findFirst(Selector.from(PushMessageEntity.class).where(WhereBuilder.b("_id", "=", id)));
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e);
			return entity;
		}
		return entity;

	}

	public static boolean delRes(PushMessageEntity entity) {
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
			DBHelper.getExecutor().deleteAll(PushMessageEntity.class);
			result = true;

		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e);
		}
		return result;
	}

	public static void update(PushMessageEntity entity) {
		try {
			DBHelper.getExecutor().update(entity);
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
