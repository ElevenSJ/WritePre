package com.easier.writepre.ui;

import com.easier.writepre.R;
import com.easier.writepre.adapter.PracticeQuestionsListAdapter;
import com.easier.writepre.http.RequestManager;
import com.easier.writepre.param.QuestionsListParams;
import com.easier.writepre.param.TheoreticalTestListParams;
import com.easier.writepre.response.BaseResponse;
import com.easier.writepre.response.QuestionsListResponse;
import com.easier.writepre.utils.SPUtils;
import com.easier.writepre.utils.ToastUtil;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * 题库列表
 */
public class PracticeQuestionsListActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    private ListView listViewQuestions;

    private PracticeQuestionsListAdapter adapter;

    private List<QuestionsListResponse.QuestionsInfo> listData;

    private String stu_type;//0:小书法师 1:专业人才
    public int from = 0;
    public static final int FROM_QUESTIONS = 0;//题库
    public static final int FROM_THEORETICAL_TEST = 1;//理论测试
    public static final int FROM_FORMAL_EXAMINATION = 2;//正式考试

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_questions_list);
        from = getIntent().getIntExtra("from", FROM_QUESTIONS);
        initView();
        if (from == FROM_QUESTIONS) {

            initQuestionBankData();
        } else if (from == FROM_THEORETICAL_TEST) {
            stu_type = (String) SPUtils.instance().get("stu_type", "0");
            initTheoreticalTestData();
        }
    }

    private void initView() {
        setTopTitle("试卷");
        listViewQuestions = (ListView) findViewById(R.id.list_questions);
        listViewQuestions.setOnItemClickListener(this);
    }

    /**
     * 获取理论测试试卷列表
     */
    private void initTheoreticalTestData() {
        listData = new ArrayList<QuestionsListResponse.QuestionsInfo>();
        RequestManager.request(this, new TheoreticalTestListParams(stu_type),
                QuestionsListResponse.class, this, SPUtils.instance().getSocialPropEntity().getApp_school_server());
    }

    /**
     * 获取题库列表
     */
    private void initQuestionBankData() {
        listData = new ArrayList<QuestionsListResponse.QuestionsInfo>();
        RequestManager.request(this, new QuestionsListParams(),
                QuestionsListResponse.class, this, SPUtils.instance().getSocialPropEntity().getApp_school_server());
    }

    @Override
    public void onResponse(BaseResponse response) {
        if (response == null) {
            return;
        }
        if ("0".equals(response.getResCode())) {
            if (response instanceof QuestionsListResponse) {
                QuestionsListResponse qLResponse = (QuestionsListResponse) response;
                QuestionsListResponse.QuestionsBody body = qLResponse.getRepBody();
                listData = body.getList();
//                for (int i = 0; i < body.getList().size(); i++) {
//                    listData.add(body.getList().get(i));
//                }
                if (adapter == null) {
                    adapter = new PracticeQuestionsListAdapter(listData, this);
                    listViewQuestions.setAdapter(adapter);
                }
            }
        } else {
            ToastUtil.show(response.getResCode());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if (from == 0) {
            Intent intent = new Intent(PracticeQuestionsListActivity.this, PracticeAnswerActivity.class);
            intent.putExtra("pkg_id", listData.get(i).get_id());
            intent.putExtra("title", listData.get(i).getTitle());
            startActivity(intent);
        } else {
            Intent intent = new Intent(PracticeQuestionsListActivity.this, TheoreticalTestActivity.class);
            intent.putExtra("pkg_id", listData.get(i).get_id());
            intent.putExtra("title", listData.get(i).getTitle());
            intent.putExtra("exam_type", TheoreticalTestActivity.EXAM_TYPE_TEST);
            startActivity(intent);
        }

    }
}
