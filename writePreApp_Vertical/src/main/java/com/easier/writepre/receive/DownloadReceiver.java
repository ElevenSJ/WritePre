package com.easier.writepre.receive;

import com.easier.writepre.utils.FileUtils;
import com.easier.writepre.utils.ToastUtil;
import com.easier.writepre.widget.VersionUpdateDialog;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.text.TextUtils;

public class DownloadReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
		if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(intent.getAction())) {
			DownloadManager.Query query = new DownloadManager.Query();
			long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
			query.setFilterById(id);
			Cursor c = manager.query(query);
			if (c.moveToFirst()) {
				String filePath = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME));
				int st = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
				switch (st) {
				case DownloadManager.STATUS_SUCCESSFUL:
					FileUtils.installApk(context,filePath);
					break;
				case  DownloadManager.STATUS_FAILED:
					ToastUtil.show("版本下载失败");
					manager.remove(id);
					break;
				}
			}
		} else if (DownloadManager.ACTION_NOTIFICATION_CLICKED.equals(intent.getAction())) {
			// 点击通知栏取消下载
			// long[] ids =
			// intent.getLongArrayExtra(DownloadManager.EXTRA_NOTIFICATION_CLICK_DOWNLOAD_IDS);
			// manager.remove(ids);
			// ToastUtil.show("已经取消下载");
		}
	}

}