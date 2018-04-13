package com.easier.writepre.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.easier.writepre.R;
import com.easier.writepre.entity.CircleApplyMember;
import com.easier.writepre.entity.CircleMember;
import com.easier.writepre.entity.CircleMemberBase;
import com.easier.writepre.http.RequestManager;
import com.easier.writepre.http.WritePreListener;
import com.easier.writepre.param.CircleApplyAcceptParams;
import com.easier.writepre.param.CircleApplyDeleteParams;
import com.easier.writepre.param.CircleDeleteMemberParams;
import com.easier.writepre.param.CircleSetAdminParams;
import com.easier.writepre.response.BaseResponse;
import com.easier.writepre.response.CircleSetAdminResponse;
import com.easier.writepre.ui.CircleMemberActivity;
import com.easier.writepre.ui.CircleMemberEditNameActivity;
import com.easier.writepre.ui.UserInfoActivity;
import com.easier.writepre.utils.SPUtils;
import com.easier.writepre.utils.StringUtil;
import com.easier.writepre.utils.ToastUtil;
import com.easier.writepre.widget.ConfirmHintDialog;
import com.easier.writepre.widget.ConfirmHintDialog.ConfirmHintDialogListener;
import com.easier.writepre.widget.RoundImageView;
import com.sj.autolayout.utils.AutoUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 圈子成员
 */
@SuppressLint("InflateParams")
public class CircleMemberAdapter extends BaseAdapter implements
        WritePreListener<BaseResponse> {

    private Context mContext;

    private List<CircleMemberBase> list = new ArrayList<CircleMemberBase>();

    private List<CircleMember> memberList = new ArrayList<CircleMember>();

    private LayoutInflater mInflater;

    private int flag = 0;// 0,申请成员列表，1成员列表

    private boolean isRole = false;
    private PopupWindow popupWindow;
    private int currentPosition;
//    private CircleMember member = null;
    private boolean isCircleMember;//判断是否是圈子成员

    public CircleMemberAdapter(Context context, int flag) {
        mInflater = LayoutInflater.from(context);
        mContext = context;
        this.flag = flag;
    }

    public void setData(List<CircleApplyMember> data) {
        list.clear();
        list.addAll(data);
        notifyDataSetChanged();
    }

    public void setData(int flag, List<CircleMember> data, boolean isRole) {
        this.flag = flag;
        this.isRole = isRole;
        memberList.clear();
        memberList.addAll(data);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return flag == 0 ? list.size() : memberList.size();
    }

    @Override
    public CircleMemberBase getItem(int position) {
        return flag == 0 ? list.get(position) : memberList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.circle_member_item,
                    parent, false);
            holder.ImgIcon = (RoundImageView) convertView
                    .findViewById(R.id.imgHead);
            holder.TvName = (TextView) convertView.findViewById(R.id.tv_name);
            holder.Tvtag = (TextView) convertView.findViewById(R.id.tv_tag);
            holder.bt = (Button) convertView.findViewById(R.id.bt);
            holder.btCancle = (Button) convertView.findViewById(R.id.cancle);
            holder.img_manage = (ImageView) convertView.findViewById(R.id.img_manage);
            convertView.setTag(holder);
            AutoUtils.autoSize(convertView);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.ImgIcon.setImageView(StringUtil.getHeadUrl(getItem(position).getHead_img()));
        holder.ImgIcon.setIconView(getItem(position).getIs_teacher());
        holder.TvName.setText(getItem(position).getUname());
        holder.TvName.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                toUserDetail(getItem(position).getUser_id());
            }
        });
        holder.ImgIcon.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                toUserDetail(getItem(position).getUser_id());
            }
        });
        if (flag == 0) {
            final CircleApplyMember member = (CircleApplyMember) getItem(position);
            holder.Tvtag.setVisibility(View.GONE);
            if(!member.getIs_circle_member()){
                holder.btCancle.setVisibility(View.VISIBLE);
                holder.bt.setVisibility(View.VISIBLE);
                holder.btCancle.setText("忽略");
                holder.bt.setText("通过");
                holder.bt.setTextColor(mContext.getResources().getColor(
                        R.color.social_red));
                holder.btCancle.setTextColor(mContext.getResources().getColor(
                        R.color.social_red));
                holder.bt.setEnabled(true);
                holder.btCancle.setEnabled(true);
            }else{
                holder.bt.setText("已通过");
                holder.bt.setTextColor(mContext.getResources().getColor(
                        R.color.text_gray));
                holder.bt.setEnabled(false);
                holder.btCancle.setVisibility(View.GONE);
            }
            holder.bt.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    holder.bt.setText("已通过");
                    holder.bt.setTextColor(mContext.getResources().getColor(
                            R.color.text_gray));
                    holder.bt.setEnabled(false);
                    RequestManager.request(mContext,
                            new CircleApplyAcceptParams(member.getCircle_id(),
                                    member.get_id()), BaseResponse.class,
                            CircleMemberAdapter.this, SPUtils.instance()
                                    .getSocialPropEntity()
                                    .getApp_socail_server());
                    member.setIs_circle_member(true);
                    holder.btCancle.setVisibility(View.GONE);
                }
            });
            holder.btCancle.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    holder.bt.setVisibility(View.GONE);
                    holder.btCancle.setText("已拒绝");
                    holder.btCancle.setTextColor(mContext.getResources()
                            .getColor(R.color.text_gray));
                    holder.btCancle.setEnabled(false);
                    RequestManager.request(mContext,
                            new CircleApplyDeleteParams(member.get_id()),
                            BaseResponse.class, CircleMemberAdapter.this,
                            SPUtils.instance().getSocialPropEntity()
                                    .getApp_socail_server());
                }
            });

        } else {
            final CircleMember member = (CircleMember) getItem(position);
            if (member.getRole().equals("0")) {
                holder.Tvtag.setVisibility(View.VISIBLE);
                holder.Tvtag.setText("管理员");
            } else {
                holder.Tvtag.setVisibility(View.GONE);
            }
            if (isRole) {
                holder.img_manage.setVisibility(View.VISIBLE);
            } else {
                holder.img_manage.setVisibility(View.GONE);
            }
            holder.btCancle.setVisibility(View.GONE);
            holder.bt.setVisibility(View.GONE);
            holder.img_manage.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    showPopupWindow(v);
                }

                /**
                 * 管理操作popwindow
                 */
                private void showPopupWindow(View v) {

                    View mMenuView = LayoutInflater.from(mContext).inflate(
                            R.layout.popwindow_circle_member_manage, null);
                    Button btn_1 = (Button) mMenuView.findViewById(R.id.btn_1);
                    /**
                     * 判断我是不是创始人
                     *     是，则显示设置管理员按钮，判断当前点击的是不是管理员，
                     *          是，则弹出取消管理员
                     *          不是，则弹出设置管理员
                     *     不是，则隐藏
                     */
                    if(member.getOwner_id().equals(SPUtils.instance().getLoginEntity().get_id())){
                        btn_1.setVisibility(View.VISIBLE);
                        if (member.getRole().equals("0") ) {
                            btn_1.setText("取消管理员");
                        } else {
                            btn_1.setText("设置管理员");
                        }
                    }else{
                        btn_1.setVisibility(View.GONE);
                    }

                    btn_1.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            currentPosition = position;
                            if (member.getRole().equals("0")) {
//                                ToastUtil.show("取消管理员");
                                RequestManager.request(mContext,
                                        new CircleSetAdminParams(member.getCircle_id(),
                                                member.getUser_id(), "clear"), CircleSetAdminResponse.class,
                                        CircleMemberAdapter.this, SPUtils.instance()
                                                .getSocialPropEntity()
                                                .getApp_socail_server());
                            } else {
                                RequestManager.request(mContext,
                                        new CircleSetAdminParams(member.getCircle_id(),
                                                member.getUser_id(), "set"), CircleSetAdminResponse.class,
                                        CircleMemberAdapter.this, SPUtils.instance()
                                                .getSocialPropEntity()
                                                .getApp_socail_server());
                            }
                            popupWindow.dismiss();
                        }
                    });
                    mMenuView.findViewById(R.id.btn_2).setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            popupWindow.dismiss();
                            CircleMemberActivity.LIST_INDEX = position;
                            Intent nameIntent = new Intent(mContext, CircleMemberEditNameActivity.class);
                            nameIntent.putExtra("user_id",member.getUser_id());
                            nameIntent.putExtra("canEmpty", false);
                            nameIntent.putExtra("circle_id", member.getCircle_id());
                            ((CircleMemberActivity)mContext).startActivityForResult(nameIntent,CircleMemberActivity.REQUEST_UPDATE_CODE);
                        }
                    });

                    mMenuView.findViewById(R.id.btn_3).setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            popupWindow.dismiss();
                            showConfirmDialog();
                        }
                    });
                    mMenuView.findViewById(R.id.btn_4).setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            popupWindow.dismiss();
                        }
                    });
                    mMenuView.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            popupWindow.dismiss();
                            return false;
                        }
                    });
                    popupWindow = new PopupWindow(mMenuView,
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            false);
                    popupWindow.setOutsideTouchable(true);
                    popupWindow.showAtLocation(v, Gravity.BOTTOM
                            | Gravity.CENTER_HORIZONTAL, 0, 0);

                }

                /**
                 * 确认删除dialog
                 */
                private void showConfirmDialog() {
                    ConfirmHintDialog dialog = new ConfirmHintDialog(mContext, R.style.loading_dialog, "确定删除", new ConfirmHintDialogListener() {
                        @Override
                        public void OnClick(View view) {
                            switch (view.getId()) {
                                case R.id.tv_confirm:
                                    RequestManager.request(mContext, new CircleDeleteMemberParams(member.getCircle_id(), member.getUser_id()),
                                            BaseResponse.class, CircleMemberAdapter.this, SPUtils.instance().getSocialPropEntity().getApp_socail_server());
                                    memberList.remove(member);
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
                }
            });
        }
        return convertView;
    }

    public static class ViewHolder {
        private RoundImageView ImgIcon;
        private TextView TvName;
        private TextView Tvtag;//创建人标签
        private Button bt;
        private Button btCancle;
        private ImageView img_manage;//圈子成员管理按钮
    }

    @Override
    public void onResponse(BaseResponse response) {
        // TODO Auto-generated method stub

        if ("0".equals(response.getResCode())) {
            if (response instanceof CircleSetAdminResponse) {
                if (((CircleMember) getItem(currentPosition)).getRole().equals("0")) {
                    ((CircleMember) getItem(currentPosition)).setRole("1");
                } else {
                    ((CircleMember) getItem(currentPosition)).setRole("0");
                }
                notifyDataSetChanged();
            }
        } else {
            ToastUtil.show(response.getResMsg());
        }
    }

    /**
     * 查看用户详情
     */
    public void toUserDetail(String userId) {
        Intent intent = new Intent(mContext, UserInfoActivity.class);
        intent.putExtra("user_id", userId);
        mContext.startActivity(intent);
    }

    @Override
    public void onResponse(String tag, BaseResponse response) {
        // TODO Auto-generated method stub

    }
}
