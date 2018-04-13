package com.easier.writepre.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.easier.writepre.R;
import com.easier.writepre.ui.PracticeAnswerActivity;
import com.sj.autolayout.utils.AutoUtils;

/**
 * 题目适配
 * Created by zhongkai on 17/01/12.
 */
public class AnswerAdapter extends RecyclerView.Adapter<AnswerAdapter.AnswerViewHolder> {

    private Context mContext;

    private final Resources resources;

    private final LayoutInflater inflater;

    public AnswerAdapter(Context mContext) {
        this.mContext = mContext;
        inflater = LayoutInflater.from(mContext);
        resources = mContext.getResources();
    }

    @Override
    public AnswerAdapter.AnswerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_topic, parent, false);
        AutoUtils.autoSize(view);
        return new AnswerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final AnswerAdapter.AnswerViewHolder holder, final int position) {

        holder.tv_id.setText((position + 1) + "");
        holder.tv_id.setTextColor(Color.parseColor("#adadad"));
        holder.tv_id.setBackgroundResource(R.drawable.bg_topic_no);
        if (prePosition == position) {   //未选中
            holder.tv_id.setBackgroundResource(R.drawable.bg_topic_no);
            holder.tv_id.setTextColor(Color.parseColor("#adadad"));
        }
        if (curPosition == position) {  // 选中
            holder.tv_id.setBackgroundResource(R.drawable.bg_topic_select);
            holder.tv_id.setTextColor(Color.parseColor("#9d9f9e"));
        }

        if (((PracticeAnswerActivity) mContext).from == 0) {       // 练习考试
            if (((PracticeAnswerActivity) mContext).errorList.contains((position + 1) + "")) {       //错误的
                holder.tv_id.setTextColor(Color.parseColor("#d88694"));
                holder.tv_id.setBackgroundResource(R.drawable.bg_topic_error);
            }
            if (((PracticeAnswerActivity) mContext).okList.contains((position + 1) + "")) {      // 正确的
                holder.tv_id.setTextColor(Color.parseColor("#5bbaca"));
                holder.tv_id.setBackgroundResource(R.drawable.bg_topic_ok);
            }
        } else {   // 模拟和理论考试
            if (((PracticeAnswerActivity) mContext).haveTodoList.contains((position + 1) + "")) {      // 已做题目
                holder.tv_id.setTextColor(Color.parseColor("#5bbaca"));
                holder.tv_id.setBackgroundResource(R.drawable.bg_topic_ok);
            }
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(holder, position);
            }
        });
    }

    private OnTopicClickListener listener;

    public void setOnTopicClickListener(OnTopicClickListener listener) {
        this.listener = listener;
    }

    private int curPosition;

    public void notifyCurPosition(int curPosition) {
        this.curPosition = curPosition;
        notifyItemChanged(curPosition);
    }

    private int prePosition;

    public void notifyPrePosition(int prePosition) {
        this.prePosition = prePosition;
        notifyItemChanged(prePosition);
    }


    public interface OnTopicClickListener {
        void onClick(AnswerAdapter.AnswerViewHolder holder, int position);
    }

    private int num;

    public void setDataNum(int num) {
        this.num = num;
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return num;
    }


    public static class AnswerViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_id;

        public AnswerViewHolder(View itemView) {
            super(itemView);
            tv_id = (TextView) itemView.findViewById(R.id.tv_id);
        }
    }
}
