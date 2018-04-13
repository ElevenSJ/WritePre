package com.easier.writepre.fragment;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
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
import com.easier.writepre.db.DBExamManager;
import com.easier.writepre.db.DBHelper;
import com.easier.writepre.entity.ExamTableEntity;
import com.easier.writepre.http.Constant;
import com.easier.writepre.response.ExamResponse;
import com.easier.writepre.ui.ExamImageLookActivity;
import com.easier.writepre.ui.TheoreticalTestActivity;
import com.easier.writepre.ui.SquareImageLookActivity;
import com.easier.writepre.utils.BitmapHelp;
import com.easier.writepre.utils.FileUtils;
import com.easier.writepre.utils.LogUtils;
import com.easier.writepre.utils.StringUtil;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.BitmapLoadCallBack;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.sj.autolayout.utils.AutoUtils;

/**
 * 题目展示
 * Created by zhoulu on 17/07/27.
 */
public class PracticeAnswerTheoryFragment extends Fragment implements RadioGroup.OnCheckedChangeListener, View.OnClickListener {

    private static final String ARG_PARAM1 = "param1";

    private View view;

    private SimpleDraweeView iv_image;

    private TextView tv_answer;

    private RadioGroup radioGroup;

    private ExamResponse.ExamInfo examBean;
    private DbUtils dbUtils = null;
    public static final int GROUP_A = 0;//选项 A
    public static final int GROUP_B = 1;//选项 B
    public static final int GROUP_C = 2;//选项 C
    public static final int GROUP_D = 3;//选项 D

    public PracticeAnswerTheoryFragment() {
    }

    public static PracticeAnswerTheoryFragment newInstance(ExamResponse.ExamInfo examBean) {
        PracticeAnswerTheoryFragment fragment = new PracticeAnswerTheoryFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, examBean);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbUtils = DBHelper.getExecutor();
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
        iv_image = (SimpleDraweeView) view.findViewById(R.id.iv_image);
        tv_answer = (TextView) view.findViewById(R.id.tv_answer);
        radioGroup = (RadioGroup) view.findViewById(R.id.radioGroup);
        //((TextView) view.findViewById(R.id.tv_title)).setText(examBean.getSort_num() + ". " + examBean.getTitle());
        ((TextView) view.findViewById(R.id.tv_title)).setText(examBean.getTitle());
        iv_image.setOnClickListener(this);
        radioGroup.setOnCheckedChangeListener(this);

        if (TextUtils.isEmpty(examBean.getPic_url())) {
            iv_image.setVisibility(View.GONE);
        } else {
            if (checkImageIsExists(examBean.getPic_url())) {
                String imageName = examBean.getPic_url().substring(examBean.getPic_url().lastIndexOf("/"), examBean.getPic_url().length());
                String imagePath = ((TheoreticalTestActivity) getActivity()).getLocalPath() + "/" + imageName;
                examBean.setImagePath(imagePath);
                Uri uri = Uri.parse("file://" + imagePath);
                ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
                        .setAutoRotateEnabled(true)
                        .build();
                DraweeController draweeController = iv_image.getController();
                PipelineDraweeController controller = (PipelineDraweeController) Fresco.newDraweeControllerBuilder()
                        .setOldController(draweeController)
                        .setImageRequest(request)
                        .build();
                iv_image.setController(controller);
            } else {
                Uri uri = Uri.parse(examBean.getPic_url());
                ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
                        .setAutoRotateEnabled(true)
                        .build();
                DraweeController draweeController = iv_image.getController();
                PipelineDraweeController controller = (PipelineDraweeController) Fresco.newDraweeControllerBuilder()
                        .setOldController(draweeController)
                        .setImageRequest(request)
                        .build();
                iv_image.setController(controller);
            }
        }

        for (int i = 0; i < examBean.getItems().size(); i++) {
            RadioButton rb = new RadioButton(getActivity());
            rb.setText(examBean.getItems().get(i).getKey() + "." + examBean.getItems().get(i).getTitle());
            radioGroup.addView(rb);
            rb.setId(i);
            rb.setPadding(20, 20, 20, 20);
            rb.setButtonDrawable(R.drawable.radio_button);  // 自定义radioButton按钮样式
        }
        ExamTableEntity examTableEntity = DBExamManager.queryById(examBean.get_id());
        if (examTableEntity != null)
            if (TextUtils.isEmpty(examBean.getSelect())) {
                ((RadioButton) radioGroup.getChildAt(getChildId(examTableEntity.getUser_select()))).setChecked(true);
                examBean.setSelect(examTableEntity.getUser_select());
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
        examBean.setSelect(key);
        ExamTableEntity examTableEntity = DBExamManager.queryById(examBean.get_id());
        LogUtils.e("key=====" + key);
        if (examTableEntity == null) {
            LogUtils.e("DBExam insert ...");
            examTableEntity = new ExamTableEntity();
            examTableEntity.set_id(examBean.get_id());
            examTableEntity.setPkg_id(examBean.getPkg_id());
            examTableEntity.setSort_num(Integer.parseInt(examBean.getSort_num()));
            examTableEntity.setUser_select(examBean.getSelect());
            DBExamManager.inster(examTableEntity);
            ((TheoreticalTestActivity) getActivity()).haveTodoList.add(examBean.getSort_num());             //已做题目
            LogUtils.e("插入题目序号:=============>"+examBean.getSort_num() + "");
        } else {
            LogUtils.e("DBExam update ...");
            examTableEntity.set_id(examBean.get_id());
            examTableEntity.setPkg_id(examBean.getPkg_id());
            examTableEntity.setSort_num(Integer.parseInt(examBean.getSort_num()));
            examTableEntity.setUser_select(examBean.getSelect());

            DBExamManager.update(examTableEntity);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_image:
                imageBrower(examBean.getImagePath(), examBean.getPic_url());
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
     * @param localPath
     * @param url
     */
    private void imageBrower(String localPath, String url) {
        Intent intent = new Intent(getActivity(), ExamImageLookActivity.class);
        intent.putExtra("url", url);
        intent.putExtra("localPath", localPath);
        getActivity().startActivity(intent);
    }

    private boolean checkImageIsExists(String url) {
        String imageName = url.substring(url.lastIndexOf("/"), url.length());
        if (FileUtils.fileIsExists(((TheoreticalTestActivity) getActivity()).getLocalPath(), imageName)) {
            return true;
        }
        return false;
    }
}
