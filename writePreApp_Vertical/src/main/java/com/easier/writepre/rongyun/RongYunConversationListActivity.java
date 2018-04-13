package com.easier.writepre.rongyun;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.easier.writepre.R;
import com.easier.writepre.entity.YouMengType;
import com.easier.writepre.ui.BaseActivity;
import com.easier.writepre.ui.MainActivity;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import io.rong.imkit.fragment.ConversationListFragment;
import io.rong.imlib.model.Conversation;

/**
 * 会话列表
 *
 * @author zhoulu
 */
@SuppressLint("NewApi")
public class RongYunConversationListActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversationlist);
        setTopTitle("消息");
        setTopRight(R.drawable.icon_contacts);
        enterFragment();

    }

    public void onFriendsClick(View view) {
        super.onTopRightTxtClick(view);
        Intent intent = new Intent(this, RongYunFriendsListActivity.class);
        this.startActivity(intent);
    }

    /**
     * 加载 会话列表 ConversationListFragment
     */
    private void enterFragment() {
        ConversationListFragment fragment = new ConversationListFragment();
        Uri uri = Uri.parse("rong://" + getApplicationInfo().packageName).buildUpon()
                .appendPath("conversationlist")
                .appendQueryParameter(Conversation.ConversationType.PRIVATE.getName(), "false") //设置私聊会话非聚合显示
                .appendQueryParameter(Conversation.ConversationType.GROUP.getName(), "false")//设置群组会话聚合显示
                .appendQueryParameter(Conversation.ConversationType.DISCUSSION.getName(), "true")//设置讨论组会话聚合显示
                .appendQueryParameter(Conversation.ConversationType.SYSTEM.getName(), "true")//设置系统会话聚合显示
                .build();
        fragment.setUri(uri);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        //rong_content 为你要加载的 id
        transaction.add(R.id.conversationlist, fragment);
        transaction.commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        umeng();
    }

    private void umeng() {
        //友盟统计
        List<String> var = new ArrayList<String>();
        var.add(YouMengType.getName(MainActivity.TYPE_FIV));
        var.add("进入聊天");
        YouMengType.onEvent(this, var, getShowTime(), "进入聊天");
    }
}
