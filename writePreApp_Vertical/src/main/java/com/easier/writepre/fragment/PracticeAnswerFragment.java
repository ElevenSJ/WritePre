package com.easier.writepre.fragment;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.easier.writepre.R;
import com.easier.writepre.db.DBHelper;
import com.easier.writepre.http.Constant;
import com.easier.writepre.response.ExamResponse;
import com.easier.writepre.ui.PracticeAnswerActivity;
import com.easier.writepre.ui.SquareImageLookActivity;
import com.easier.writepre.utils.BitmapHelp;
import com.easier.writepre.utils.LogUtils;
import com.easier.writepre.utils.StringUtil;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.BitmapLoadCallBack;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;
import com.sj.autolayout.utils.AutoUtils;

/**
 * 题目展示
 * Created by zhongkai on 17/01/13.
 */
public class PracticeAnswerFragment extends Fragment implements RadioGroup.OnCheckedChangeListener, View.OnClickListener {

    private static final String ARG_PARAM1 = "param1";

    private View view;

    private ImageView iv_image;

    private TextView tv_answer;

    private RadioGroup radioGroup;

    private ExamResponse.ExamInfo examBean;

    public static final int GROUP_A = 0;//选项 A
    public static final int GROUP_B = 1;//选项 B
    public static final int GROUP_C = 2;//选项 C
    public static final int GROUP_D = 3;//选项 D

    public PracticeAnswerFragment() {
    }

    public static PracticeAnswerFragment newInstance(ExamResponse.ExamInfo examBean) {
        PracticeAnswerFragment fragment = new PracticeAnswerFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, examBean);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            examBean = (ExamResponse.ExamInfo) getArguments().getSerializable(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_answer, container, false);
        initView();
        AutoUtils.autoSize(view);
        return view;
    }

    private void initView() {
        iv_image = (ImageView) view.findViewById(R.id.iv_image);
        tv_answer = (TextView) view.findViewById(R.id.tv_answer);
        radioGroup = (RadioGroup) view.findViewById(R.id.radioGroup);
        //((TextView) view.findViewById(R.id.tv_title)).setText(examBean.getSort_num() + ". " + examBean.getTitle());
        ((TextView) view.findViewById(R.id.tv_title)).setText(examBean.getTitle());
        iv_image.setOnClickListener(this);
        radioGroup.setOnCheckedChangeListener(this);

        if (TextUtils.isEmpty(examBean.getPic_url())) {
            iv_image.setVisibility(View.GONE);
        } else {
            BitmapHelp.getBitmapUtils().display(iv_image, StringUtil.getImgeUrl(examBean.getPic_url()) + Constant.BIG_IMAGE_SUFFIX, new BitmapLoadCallBack<View>() {

                @Override
                public void onLoadCompleted(View arg0, String arg1,
                                            Bitmap arg2, BitmapDisplayConfig arg3,
                                            BitmapLoadFrom arg4) {
                    ((ImageView) arg0).setImageBitmap(arg2);
                }

                @Override
                public void onLoadFailed(View arg0, String arg1,
                                         Drawable arg2) {

                }
            });
        }

        for (int i = 0; i < examBean.getItems().size(); i++) {
            RadioButton rb = new RadioButton(getActivity());
            rb.setText(examBean.getItems().get(i).getKey() + "." + examBean.getItems().get(i).getTitle());
            radioGroup.addView(rb);
            rb.setId(i);
            rb.setPadding(20, 20, 20, 20);
            rb.setButtonDrawable(R.drawable.radio_button);  // 自定义radioButton按钮样式
        }

        if (((PracticeAnswerActivity) getActivity()).from != 0) {       //模拟和正式考试
            Cursor cursor = DBHelper.instance().query("select key from exam where sort_num = '" + examBean.getSort_num() + "'");
            if (cursor.getCount() == 1 && cursor.moveToFirst()) {
                if (TextUtils.isEmpty(examBean.getSelect())) {
                    examBean.setSelect(cursor.getString(0));
                    ((RadioButton) radioGroup.getChildAt(getChildId(cursor.getString(0)))).setChecked(true);
                }
            }
//            if (cursor.getCount() == 1 && cursor.moveToFirst()) {
//                if (TextUtils.isEmpty(examBean.getSelect())) {
//                    ((RadioButton) radioGroup.getChildAt(getChildId(cursor.getString(0)))).setButtonDrawable(R.drawable.checked);
//                    examBean.setSelect(cursor.getString(0));
//                }
//                for (int j = 0; j < radioGroup.getChildCount(); j++) {
//                    radioGroup.getChildAt(j).setClickable(false);
//                }
//            }
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        String key = "";
        switch (checkedId) {
            case GROUP_A:
                key = "A";
                break;
            case GROUP_B:
                key = "B";
                break;
            case GROUP_C:
                key = "C";
                break;
            case GROUP_D:
                key = "D";
                break;
        }
        if (((PracticeAnswerActivity) getActivity()).from == 0) {       // 练习题库
            if (examBean.getKey().contains(key)) {
                if (!((PracticeAnswerActivity) getActivity()).okList.contains(examBean.getSort_num())) {       //  判断是否添加过
                    ((PracticeAnswerActivity) getActivity()).okList.add(examBean.getSort_num());
                    ((PracticeAnswerActivity) getActivity()).stopOnes();
                    ((PracticeAnswerActivity) getActivity()).changeText();
                }
                showAnswer((RadioButton) group.getChildAt(checkedId), true);      // 选择正确
            } else {       //错误答案
                if (!((PracticeAnswerActivity) getActivity()).errorList.contains(examBean.getSort_num())) {
                    ((PracticeAnswerActivity) getActivity()).errorList.add(examBean.getSort_num());
                    ((PracticeAnswerActivity) getActivity()).changeText();
                }
                showAnswer((RadioButton) group.getChildAt(getChildId(examBean.getKey())), true);
                showAnswer((RadioButton) group.getChildAt(checkedId), false);
                //tv_answer.setText("正确答案：" + examBean.getKey());
            }
            for (int i = 0; i < group.getChildCount(); i++) {           // 选择后不能重新选择
                group.getChildAt(i).setClickable(false);
            }
        } else { // 模拟和正式考试
            if (TextUtils.isEmpty(examBean.getSelect())) {
                LogUtils.e("insert into data...");
                DBHelper.instance().insert("insert into exam(pkg_id, title_id, sort_num, key) values('"
                        + examBean.getPkg_ex_id()
                        + "','"
                        + examBean.get_id()
                        + "','"
                        + examBean.getSort_num()
                        + "','" + key + "')");
                examBean.setSelect(key);
                ((PracticeAnswerActivity) getActivity()).stopOnes();
                ((PracticeAnswerActivity) getActivity()).haveTodoList.add(examBean.getSort_num());             //已做题目
            } else {     // 对象方式的更新  也可用sql语句
                LogUtils.e("update data...");
                ContentValues cv = new ContentValues();
                cv.put("pkg_id", examBean.getPkg_ex_id());
                cv.put("title_id", examBean.get_id());
                cv.put("sort_num", examBean.getSort_num());
                cv.put("key", key);
                String[] args = {String.valueOf(examBean.getSort_num())};
                int code = DBHelper.instance().update("exam", cv, "sort_num=?", args);
                LogUtils.e("update succ..." + code);
                examBean.setSelect(key);
                //((PracticeAnswerActivity) getActivity()).stopOnes();
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_image:
                imageBrower(0, new String[]{examBean.getPic_url()});
                break;
        }
    }

    /**
     * @param key
     * @return
     */
    private int getChildId(String key) {
        if (key.contains("A")) {
            return GROUP_A;
        } else if (key.contains("B")) {
            return GROUP_B;
        } else if (key.contains("C")) {
            return GROUP_C;
        } else if (key.contains("D")) {
            return GROUP_D;
        }
        return 0;
    }

    /**
     * 显示答案小图标(正确、错误选项提示)
     */
    private void showAnswer(RadioButton rb, boolean result) {
        Drawable drawable = getResources().getDrawable(result ? R.drawable.big_yes : R.drawable.big_no);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        rb.setCompoundDrawables(null, null, drawable, null);//设置TextView的drawableleft
        rb.setCompoundDrawablePadding(40);//设置图片和text之间的间距
    }

    /**
     * 预览大图
     *
     * @param position
     * @param urls
     */
    private void imageBrower(int position, String[] urls) {
        Intent intent = new Intent(getActivity(), SquareImageLookActivity.class);
        intent.putExtra(SquareImageLookActivity.EXTRA_IMAGE_URLS, urls);
        intent.putExtra(SquareImageLookActivity.EXTRA_IMAGE_INDEX, position);
        getActivity().startActivity(intent);
    }
}
