package com.easier.writepre.utils;

import com.easier.writepre.R;
import com.easier.writepre.entity.LoginEntity;
import com.easier.writepre.http.Constant;
import com.easier.writepre.http.RequestManager;
import com.easier.writepre.http.WritePreListener;
import com.easier.writepre.param.UpdateUserNickPatams;
import com.easier.writepre.response.BaseResponse;
import com.easier.writepre.ui.LoginActivity;
import com.google.gson.Gson;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

public class LoginUtil implements WritePreListener<BaseResponse> {

	public static boolean checkLogin(Context context) {
		if ((boolean) SPUtils.instance().get(SPUtils.IS_LOGIN, false)) {
			if (SPUtils.instance().getLoginEntity().getUname().trim().equals("")) {
				new LoginUtil().showNickNameDialog(context);
				return false;
			}
			return true;
		} else {
			showLoginDialog(context);
			return false;
		}
	}

	public static void showLoginDialog(final Context context) {
		final Dialog dialog = new Dialog(context, R.style.loading_dialog);
		View view = LayoutInflater.from(context).inflate(R.layout.dialog_islogin, null);
		view.findViewById(R.id.tv_login).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
				Intent intent = new Intent(context, LoginActivity.class);
				context.startActivity(intent);
			}
		});
		view.findViewById(R.id.tv_cancel).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		dialog.dismiss();
		dialog.setContentView(view);
		dialog.setCanceledOnTouchOutside(true);
		dialog.show();
	}

	/**
	 * 
	 * 补全昵称
	 */
	public void showNickNameDialog(final Context context) {
		final Dialog dialog = new Dialog(context, R.style.loading_dialog);
		View view = LayoutInflater.from(context).inflate(R.layout.dialog_nick_name, null);
		final EditText et_nick = (EditText) view.findViewById(R.id.et_nick);
		view.findViewById(R.id.tv_ok).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (et_nick.getText().toString().trim().length() <= 0) {
					ToastUtil.show("请设置昵称");
					return;
				}
				updateInfo(context, et_nick.getText().toString());
				NICK = et_nick.getText().toString();
				dialog.dismiss();

			}
		});
		view.findViewById(R.id.tv_cancel).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		dialog.dismiss();
		dialog.setContentView(view);
		dialog.setCanceledOnTouchOutside(true);
		dialog.show();
	}

	/**
	 * 
	 * 更新用户昵称
	 * 
	 */

	public static String NICK = "";

	public void updateInfo(Context context, String nick) {

		LoginEntity body = SPUtils.instance().getLoginEntity();

		String[] coord = new String[0];

		if (body.getCoord() != null) {
			if (body.getCoord().size() < 2) {
				coord = new String[0];
			} else {
				coord = new String[] { body.getCoord().get(0).toString(), body.getCoord().get(1).toString() };
			}
		}

		RequestManager.request(context,
				new UpdateUserNickPatams(nick, body.getBirth_day(), body.getAge(), body.getSex(), body.getAddr(),
						body.getFav(), body.getInterest(), body.getQq(), body.getBei_tie(), body.getFav_font(),
						body.getStu_time(), body.getSchool(), body.getCompany(), body.getProfession(),
						body.getSignature(), body.getEmail(), body.getReal_name(), coord),
				BaseResponse.class, this, Constant.URL);
	}

	@Override
	public void onResponse(BaseResponse response) {
		if ("0".equals(response.getResCode())) {
			ToastUtil.show("设置昵称成功");
			LoginEntity loginEntity = SPUtils.instance().getLoginEntity();
			loginEntity.setUname(NICK);
			SPUtils.instance().put(SPUtils.LOGIN_DATA, new Gson().toJson(loginEntity));
			this.NICK = NICK;
		} else {
			this.NICK = "";
			ToastUtil.show(response.getResMsg());
		}
	}

	@Override
	public void onResponse(String tag, BaseResponse response) {
		// TODO Auto-generated method stub
		
	}
}
