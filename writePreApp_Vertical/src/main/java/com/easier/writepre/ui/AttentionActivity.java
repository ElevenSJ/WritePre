package com.easier.writepre.ui;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;

import com.easier.writepre.R;
import com.easier.writepre.adapter.ViewPagerFragmentAdapter;
import com.easier.writepre.entity.YouMengType;
import com.easier.writepre.fragment.AttentionedFragment;
import com.easier.writepre.fragment.BeAttentionFragment;
import com.easier.writepre.utils.SPUtils;
import com.easier.writepre.utils.ToastUtil;
import com.easier.writepre.widget.MainViewPager;
import com.easier.writepre.widget.TabIndicator;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

/**
 * 关注
 *
 * @author zhoulu
 */
public class AttentionActivity extends BaseActivity {
    private static final String[] CONTENT_OTHER = new String[]{"TA关注的",
            "TA的粉丝"};
    private static final String[] CONTENT_MINE = new String[]{"我的关注", "我的粉丝"};
    private static String[] CONTENT;
    private TabIndicator indicator;

    private MainViewPager pager;

    private ViewPagerFragmentAdapter adapter;
    private int selectNo = 0;
    private AttentionedFragment attentionedFragment;
    private BeAttentionFragment beAttentionFragment;
    private String id;
    private String name;

    @Override
    protected void onCreate(Bundle arg0) {
        // TODO Auto-generated method stub
        super.onCreate(arg0);
        setContentView(R.layout.activity_attention);
        selectNo = getIntent().getIntExtra("select_no", 0);
        id = getIntent().getStringExtra("id");
        name = getIntent().getStringExtra("name");
        if (TextUtils.equals(id, SPUtils.instance().getLoginEntity().get_id())) {
            CONTENT = CONTENT_MINE;
        } else {
            CONTENT = CONTENT_OTHER;
        }
        initView();
    }

    private void initView() {
        // 加载控件
        pager = (MainViewPager) findViewById(R.id.main_viewpager);

        indicator = (TabIndicator) findViewById(R.id.main_tab_indicator);

        indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position != selectNo) {
                    umeng(selectNo);
                }
                selectNo = position;

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        adapter = new ViewPagerFragmentAdapter(getSupportFragmentManager(),
                CONTENT);
        attentionedFragment = new AttentionedFragment();
        Bundle bundle1 = new Bundle();
        bundle1.putString("_id", id);
        attentionedFragment.setArguments(bundle1);

        beAttentionFragment = new BeAttentionFragment();
        Bundle bundle2 = new Bundle();
        bundle2.putString("_id", id);
        beAttentionFragment.setArguments(bundle2);

        adapter.addFragment(attentionedFragment);
        adapter.addFragment(beAttentionFragment);

        pager.setAdapter(adapter);
        indicator.setViewPage(pager);
        indicator.setCurrentItem(selectNo);

    }

    @Override
    protected void onPause() {
        super.onPause();
        umeng(selectNo);

    }

    private void umeng(int selectNo) {
        //友盟统计
        List<String> var = new ArrayList<String>();
        var.add(YouMengType.getName(MainActivity.TYPE_FIV));
        if (selectNo == 0) {
            var.add("查看关注");
        } else {
            var.add("查看粉丝");
        }
        YouMengType.onEvent(this, var, getShowTime(), name);
    }
}
