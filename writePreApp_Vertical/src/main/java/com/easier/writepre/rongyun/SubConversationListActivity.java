package com.easier.writepre.rongyun;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.easier.writepre.R;
import com.easier.writepre.ui.BaseActivity;

import io.rong.imkit.RongContext;
import io.rong.imkit.fragment.SubConversationListFragment;
import io.rong.imkit.widget.adapter.SubConversationListAdapter;

/**
 * 聚合会话列表
 */
public class SubConversationListActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_subconversationlist);
        SubConversationListFragment fragment = new SubConversationListFragment();
        fragment.setAdapter(new SubConversationListAdapter(RongContext.getInstance()));
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.subconversationlist, fragment);
        transaction.commit();

        Intent intent = getIntent();
        if (intent.getData() == null) {
            return;
        }
        //聚合会话参数
        String type = intent.getData().getQueryParameter("type");

        if (type == null)
            return;

        if (type.equals("group")) {
            setTopTitle("圈组");
        } else if (type.equals("private")) {
            setTopTitle(getResources().getString(R.string.de_actionbar_sub_private));
        } else if (type.equals("discussion")) {
            setTopTitle(getResources().getString(R.string.de_actionbar_sub_discussion));
        } else if (type.equals("system")) {
            setTopTitle(getResources().getString(R.string.de_actionbar_sub_system));
        } else {
            setTopTitle(getResources().getString(R.string.de_actionbar_sub_defult));
        }
    }

    @Override
    protected void onStop() {
        finish();
        super.onStop();
    }
}
