package com.easier.writepre.entity;

import android.content.Context;
import android.graphics.drawable.Drawable;

public class ActionItem {
	public CharSequence mTitle;
	public int viewId;
	public String msgId;

	public ActionItem(int id, CharSequence title) {
		this.viewId = id;
		this.mTitle = title;
	}

	public ActionItem(Context context, int id) {
		this.viewId = id;
	}
	
	public ActionItem(Context context, int id, int titleId) {
		this.viewId = id;
		this.mTitle = context.getResources().getText(titleId);
	}

	public ActionItem(Context context, String msgId, int id) {
		this.msgId = msgId;
	}

	public ActionItem(Context context, String msgId, int id, CharSequence title) {
		this.msgId = msgId;
		this.mTitle = title;
		this.viewId = id;
	}

	public ActionItem(Context context, int id, CharSequence title) {
		this.viewId = id;
		this.mTitle = title;
	}
}
