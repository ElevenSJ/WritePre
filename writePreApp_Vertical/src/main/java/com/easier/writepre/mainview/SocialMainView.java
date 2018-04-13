package com.easier.writepre.mainview;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;

import com.easier.writepre.R;
import com.easier.writepre.adapter.ViewPagerFragmentAdapter;
import com.easier.writepre.entity.YouMengType;
import com.easier.writepre.fragment.CircleFragment;
import com.easier.writepre.fragment.PkNewFragment;
import com.easier.writepre.fragment.SquareFragment;
import com.easier.writepre.fragment.VShowUserFragment;
import com.easier.writepre.response.BaseResponse;
import com.easier.writepre.ui.MainActivity;
import com.easier.writepre.ui.PushMessageActivity;
import com.easier.writepre.utils.LogUtils;
import com.easier.writepre.widget.TabIndicator;
import com.easier.writepre.widget.TabIndicator.OnTabSelectedListener;
import com.sj.autolayout.AutoLayoutActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * 圈子
 *
 * @author dqt
 */
public class SocialMainView extends BaseView {
    public static final String[] CONTENT = new String[]{"广场", "圈子", "微展", "大赛"};

    private ViewPager socialPager;

    private TabIndicator indicator;

    private ViewPagerFragmentAdapter adapter;


    public static final String TAB_INDEX = "index";
    public static final String ITEM_SQUARE_INDEX = "square_item";
    public static final String ITEM_CIRCLE_INDEX = "circle_item";

    public static final int TAB_SQUARE = 0;
    public static final int TAB_PK = 3;
    public static final int TAB_CIRCLE = 1;
    public static final int TAB_MicroExhibition = 2;

    public static final int ITEM_1 = 0;
    public static final int ITEM_2 = 1;

    public static int socialIndex = 0;
    public static int squareItem = 0;
    public static int circleItem = 0;

    public static int index = 0;

    public SocialMainView(Context ctx) {
        super(ctx);
    }

    @Override
    public int getContextView() {
        return R.layout.social_main;
    }

    @Override
    public void initView() {
        socialPager = (ViewPager) findViewById(R.id.social_main_viewpager);
        indicator = (TabIndicator) findViewById(R.id.main_tab_indicator);
        indicator.setOnTabSelectedListener(new OnTabSelectedListener() {

            @Override
            public void onTabSelected(int position, String id, String name) {
                index = position;
                socialPager.setCurrentItem(position);
                if (position != socialIndex) {
                    uMeng(socialIndex);
                }
                setIndex(position);
            }
        });

        adapter = new ViewPagerFragmentAdapter(
                ((AutoLayoutActivity) mCtx).getSupportFragmentManager(), CONTENT);

        indicator.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
                if (arg0 != socialIndex) {
                    uMeng(socialIndex);
                }
                setIndex(arg0);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });
    }

    @Override
    public void showView(Intent intent) {
        if (isShowView) {
            // 重载
            LogUtils.e(SocialMainView.class, "重载");
        } else {
            // 创建
            LogUtils.e(SocialMainView.class, "创建");
            socialIndex = ((MainActivity) mCtx).getIntent().getIntExtra(TAB_INDEX, getIndex());
            squareItem = ((MainActivity) mCtx).getIntent().getIntExtra(ITEM_SQUARE_INDEX, getSquareItem());
            circleItem = ((MainActivity) mCtx).getIntent().getIntExtra(ITEM_CIRCLE_INDEX, getCircleItem());

            adapter.addFragment(new SquareFragment());
            adapter.addFragment(new CircleFragment());
            adapter.addFragment(new VShowUserFragment());
            adapter.addFragment(new PkNewFragment());

            socialPager.setAdapter(adapter);
            indicator.setViewPage(socialPager);
        }
        if (intent != null) {
            if (intent.getIntExtra(TAB_INDEX, -1) != -1) {
                updateInitData(intent);
            }
        }
        socialPager.setCurrentItem(socialIndex);
        super.showView(intent);
    }

    @Override
    public void onNewIntent(Intent intent) {
        // TODO Auto-generated method stub
        // 大赛报名完不提交作品跳到大赛主页面
        if (intent != null) {
            updateInitData(intent);
            socialPager.setCurrentItem(socialIndex);
            //刷新数据
            switch (socialIndex) {
                case TAB_SQUARE:
                    ((SquareFragment) adapter.getItem(socialIndex)).setTabClick(squareItem);
                    break;
                case TAB_CIRCLE:
                    ((CircleFragment) adapter.getItem(socialIndex)).setTabClick(circleItem);
                    break;
                case TAB_MicroExhibition:
                    break;
                case TAB_PK:
                    break;
            }
        }
    }

    private void updateInitData(Intent intent) {
        socialIndex = intent.getIntExtra(TAB_INDEX, getIndex());
        squareItem = intent.getIntExtra(ITEM_SQUARE_INDEX, getSquareItem());
        circleItem = intent.getIntExtra(ITEM_CIRCLE_INDEX, getCircleItem());
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        uMeng(SocialMainView.socialIndex);
    }

    @Override
    public void hideView() {
        super.hideView();
        if (isShowView) {
            uMeng(SocialMainView.socialIndex);
            if (socialIndex == TAB_SQUARE) {
                ((SquareFragment) adapter.getItem(SocialMainView.socialIndex)).uMeng();
            } else if (socialIndex == TAB_CIRCLE) {
                ((CircleFragment) adapter.getItem(SocialMainView.socialIndex)).uMeng();
            }
        }
    }

    public static int getIndex() {
        return socialIndex;
    }

    public static void setIndex(int index) {
        SocialMainView.socialIndex = index;
    }

    private void uMeng(int index) {
        if (index < CONTENT.length) {
            List<String> var = new ArrayList<String>();
            var.add(YouMengType.getName(MainActivity.TYPE_TWO));
            var.add(CONTENT[index]);
            YouMengType.onEvent(mCtx, var, getShowTime(), CONTENT[index]);
        }
    }

    public static int getSquareItem() {
        return squareItem;
    }

    public static void setSquareItem(int squareItem) {
        SocialMainView.squareItem = squareItem;
    }

    public static int getCircleItem() {
        return circleItem;
    }

    public static void setCircleItem(int circleItem) {
        SocialMainView.circleItem = circleItem;
    }

    @Override
    public void onTopRightClick(View view) {
        // TODO Auto-generated method stub
        super.onTopRightClick(view);
        Intent intent = new Intent(mCtx, PushMessageActivity.class);
        mCtx.startActivity(intent);
    }

    @Override
    public void onResponse(BaseResponse response) {
        // TODO Auto-generated method stub
        super.onResponse(response);
    }
}
