package com.easier.writepre.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.easier.writepre.R;
import com.easier.writepre.entity.CourseInfo;
import com.easier.writepre.entity.CourseList;
import com.easier.writepre.entity.YouMengType;
import com.easier.writepre.http.Constant;
import com.easier.writepre.http.RequestManager;
import com.easier.writepre.http.WritePreListener;
import com.easier.writepre.param.AddCourseParams;
import com.easier.writepre.param.DelCourseParams;
import com.easier.writepre.param.DelMyCourseParams;
import com.easier.writepre.response.BaseResponse;
import com.easier.writepre.response.DelCourseResponse;
import com.easier.writepre.ui.CourseCatalogActivity;
import com.easier.writepre.ui.CourseCatalogActivityNew;
import com.easier.writepre.ui.DiyCourseCatalogActivity;
import com.easier.writepre.ui.MainActivity;
import com.easier.writepre.utils.BitmapHelp;
import com.easier.writepre.utils.LoginUtil;
import com.easier.writepre.utils.SPUtils;
import com.easier.writepre.utils.StringUtil;
import com.easier.writepre.utils.ToastUtil;
import com.easier.writepre.widget.ConfirmHintDialog;
import com.easier.writepre.widget.ConfirmHintDialog.ConfirmHintDialogListener;
import com.facebook.drawee.view.SimpleDraweeView;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.BitmapLoadCallBack;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;
import com.sj.autolayout.utils.AutoUtils;
import com.sj.autolayout.utils.DateKit;
import com.umeng.analytics.MobclickAgent;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CourseExpandAdapter extends BaseExpandableListAdapter implements WritePreListener<BaseResponse> {
	private Context mCtx;
	private List<CourseList> groupList;

	private HashMap<String, List<CourseInfo>> childList = new HashMap<>();

	private String typeId = "";
	private String typeTitle = "";

	public CourseExpandAdapter(Context ctx, String typeId,String typeTitle) {
		this.mCtx = ctx;
		this.typeId = typeId;
		this.typeTitle = typeTitle;
	}

	public void setData(List<CourseList> list) {
		if (list != null) {
			groupList = list;
			notifyDataSetChanged();
		}
	}

	public void clearData() {
		groupList.clear();
		childList.clear();
		notifyDataSetChanged();
	}

	public void clearMyData() {
		childList.clear();
//		notifyDataSetChanged();
	}

	public HashMap<String, List<CourseInfo>> getChildData() {
		return childList;
	}

	public void setChildData(List<CourseInfo> data) {
		for (int i = 0; i < data.size(); i++) {
			if (childList.get(data.get(i).getRef_ct_id()) == null) {
				List<CourseInfo> courseList = new ArrayList<CourseInfo>();
				courseList.add(data.get(i));
				childList.put(data.get(i).getRef_ct_id(), courseList);
			} else {
				childList.get(data.get(i).getRef_ct_id()).add(data.get(i));
			}
		}
		notifyDataSetChanged();
	}

	public void setMyChildData(List<CourseInfo> data) {
		childList.clear();
		for (int i = 0; i < data.size(); i++) {
			if (childList.get(data.get(i).getType()) == null) {
				List<CourseInfo> courseList = new ArrayList<CourseInfo>();
				courseList.add(data.get(i));
				childList.put(data.get(i).getType(), courseList);
			} else {
				childList.get(data.get(i).getType()).add(data.get(i));
			}
		}
		notifyDataSetChanged();
	}

	@Override
	public int getGroupCount() {
		return groupList == null ? 0 : groupList.size();
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		// TODO Auto-generated method stub
		return childList.get(groupList.get(groupPosition).get_id()) == null ? 0
				: childList.get(groupList.get(groupPosition).get_id()).size();
	}

	@Override
	public CourseList getGroup(int groupPosition) {
		return groupList.get(groupPosition);
	}

	@Override
	public CourseInfo getChild(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return childList.get(groupList.get(groupPosition).get_id())==null?null:childList.get(groupList.get(groupPosition).get_id()).get(childPosition);
	}

	@Override
	public long getGroupId(int groupPosition) {
		// TODO Auto-generated method stub
		return groupPosition;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return childPosition;
	}

	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		GroupHolder groupHolder;
		if (convertView == null) {
			groupHolder = new GroupHolder();
			convertView = LayoutInflater.from(mCtx).inflate(R.layout.course_list_group_item, parent, false);
			groupHolder.textView = (TextView) convertView.findViewById(R.id.tv_name);
			convertView.setTag(groupHolder);
			AutoUtils.autoSize(convertView);
		} else {
			groupHolder = (GroupHolder) convertView.getTag();
		}

		groupHolder.textView.setText( getGroup(groupPosition).getTitle());
		return convertView;
	}

	@Override
	public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView,
			ViewGroup parent) {
		ChildHolder childHolder;
		if (convertView == null) {
			childHolder = new ChildHolder();
			convertView = LayoutInflater.from(mCtx).inflate(R.layout.course_child_item, parent, false);
			childHolder.icon = (SimpleDraweeView) convertView.findViewById(R.id.img_icon);
			childHolder.store = (ImageView) convertView.findViewById(R.id.tv_store);
			childHolder.name = (TextView) convertView.findViewById(R.id.tv_name);
			childHolder.hotNum = (TextView) convertView.findViewById(R.id.tv_hot_num);
			childHolder.videoTag = (LinearLayout) convertView.findViewById(R.id.tv_tag);
			childHolder.extVideoTag = (LinearLayout) convertView.findViewById(R.id.cs_ext_video_tag);
			childHolder.hotTag = (LinearLayout) convertView.findViewById(R.id.layout_hot_tag);
			convertView.setTag(childHolder);
			AutoUtils.autoSize(convertView);
		} else {
			childHolder = (ChildHolder) convertView.getTag();
		}
		final CourseInfo courseInfo = getChild(groupPosition, childPosition);
		if (courseInfo != null) {
			if (getGroup(groupPosition).getTitle().equals("自选课程")&&TextUtils.isEmpty(courseInfo.getFace_url())) {
				childHolder.icon.setImageResource(R.drawable.diy_cover);
			} else {
				childHolder.icon.setImageURI(StringUtil.getImgeUrl(courseInfo.getFace_url()));
			}
			childHolder.name.setText(courseInfo.getTitle());
			if (getGroup(groupPosition).getTitle().equals("自选课程")) {
				childHolder.hotNum.setText(Html.fromHtml(
						TextUtils.isEmpty(courseInfo.getCtime()) ? "0" : DateKit.timeFormat(courseInfo.getCtime())));
			} else {
				childHolder.hotNum.setText(Html.fromHtml(TextUtils.isEmpty(courseInfo.getHotness()) ? "0"
						: "<font color=gray>热度 </font>" + courseInfo.getHotness() + "℃ "));
			}
			if (TextUtils.isEmpty(courseInfo.getHas_video()) || courseInfo.getHas_video().equals("0")) {
				childHolder.videoTag.setVisibility(View.GONE);
			} else {
				childHolder.videoTag.setVisibility(View.VISIBLE);
			}
			if (TextUtils.isEmpty(courseInfo.getHas_ext_video()) || !courseInfo.getHas_ext_video().equals("1")) {
				childHolder.extVideoTag.setVisibility(View.GONE);
			} else {
				childHolder.extVideoTag.setVisibility(View.VISIBLE);
			}
			if (!typeId.equals("-100") && (boolean) SPUtils.instance().get(SPUtils.IS_LOGIN, false)) {
				childHolder.store.setVisibility(View.VISIBLE);
			} else {
				childHolder.store.setVisibility(View.GONE);
			}
			if (MainActivity.myCourse.get(courseInfo.get_id()) != null) {
				childHolder.store.setImageResource(R.drawable.collection_red);
			} else {
				childHolder.store.setImageResource(R.drawable.collection_gray);
			}
			childHolder.store.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (LoginUtil.checkLogin(mCtx)) {
							MainActivity.isMyCourseUpdate = true;
							if (MainActivity.myCourse.get(courseInfo.get_id()) != null) {
								RequestManager.request(mCtx, new DelCourseParams(courseInfo.get_id(),courseInfo.getType()),
										DelCourseResponse.class, CourseExpandAdapter.this, Constant.URL);
								MainActivity.myCourse.remove(courseInfo.get_id());
								((ImageView) v).setImageResource(R.drawable.collection_gray);
							} else {
								//友盟统计
								List<String> var = new ArrayList<String>();
								var.add(YouMengType.getName(MainActivity.TYPE_ONE));
								var.add("收藏课程");
								YouMengType.onEvent(mCtx,var,1,getGroup(groupPosition).getTitle()+courseInfo.getTitle());
								((ImageView) v).setImageResource(R.drawable.collection_red);
								MainActivity.myCourse.put(courseInfo.get_id(), courseInfo.get_id());
								RequestManager.request(mCtx, new AddCourseParams(courseInfo.get_id(),courseInfo.getType()),
										BaseResponse.class, CourseExpandAdapter.this, Constant.URL);
							}
//						}
					}
				}
			});
			if (typeId.equals("-100")) {
				convertView.setOnLongClickListener(new OnLongClickListener() {

					@Override
					public boolean onLongClick(View v) {
						ConfirmHintDialog dialog = new ConfirmHintDialog(mCtx, R.style.loading_dialog, "是否删除", "删除",
								"取消", new ConfirmHintDialogListener() {

									@Override
									public void OnClick(View view) {
										switch (view.getId()) {
										case R.id.tv_confirm:
											MainActivity.isCourseUpdate = true;
											if (courseInfo.getType().equals("vod")){
												RequestManager.request(mCtx, new DelCourseParams(courseInfo.getRef_lsn_id(),courseInfo.getType()),
														DelCourseResponse.class, CourseExpandAdapter.this, Constant.URL);
											}else{
												RequestManager.request(mCtx, new DelMyCourseParams(courseInfo.get_id()),
														DelCourseResponse.class, CourseExpandAdapter.this, Constant.URL);
											}

											childList.get(getGroup(groupPosition).get_id()).remove(courseInfo);
											MainActivity.myCourse.remove(courseInfo.getRef_lsn_id());
											notifyDataSetChanged();
											break;
										case R.id.tv_cancel:
											break;

										default:
											break;
										}
									}
								});
						dialog.show();
						return false;
					}
				});
			}
			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent();
					intent.putExtra(CourseCatalogActivityNew.COURSE_TYPE, typeTitle);
					intent.putExtra(CourseCatalogActivityNew.COURSE_NAME, courseInfo.getTitle());
					intent.putExtra(CourseCatalogActivityNew.TYPE, courseInfo.getType());
					if (typeId.equals("-100")) {
						intent.putExtra(DiyCourseCatalogActivity.COURSE_GROUP, getGroup(groupPosition).getTitle()+courseInfo.getPub_name());
						if (getGroup(groupPosition).getTitle().equals("自选课程")) {
							intent.setClass(mCtx, DiyCourseCatalogActivity.class);
							intent.putExtra(DiyCourseCatalogActivity.COURSE_ID, courseInfo.getRef_lsn_id());
						}else {
							intent.setClass(mCtx, CourseCatalogActivityNew.class);
							intent.putExtra(CourseCatalogActivityNew.COURSE_ID, courseInfo.getRef_lsn_id());
						}
					} else {
						intent.putExtra(CourseCatalogActivityNew.COURSE_GROUP, getGroup(groupPosition).getTitle());
						intent.putExtra(CourseCatalogActivityNew.COURSE_ID, courseInfo.get_id());
						intent.setClass(mCtx, CourseCatalogActivityNew.class);
					}
					mCtx.startActivity(intent);

				}

			});
		}
		return convertView;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return true;
	}

	public class GroupHolder {
		TextView textView;
	}

	public class ChildHolder {
		SimpleDraweeView icon;
		TextView name;
		TextView hotNum;
		LinearLayout videoTag;
		LinearLayout extVideoTag;
		LinearLayout hotTag;
		ImageView store;
	}

	@Override
	public void onResponse(BaseResponse response) {
		if (response.getResCode().equals("0")) {
		} else {
			ToastUtil.show(response.getResMsg());
		}

	}

	@Override
	public void onResponse(String tag, BaseResponse response) {
		// TODO Auto-generated method stub
		
	}

}
