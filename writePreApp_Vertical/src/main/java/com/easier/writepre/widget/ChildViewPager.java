package com.easier.writepre.widget;

import android.content.Context;
import android.content.Intent;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.easier.writepre.adapter.SocialAdvertiseAdapter;
import com.easier.writepre.adapter.XsfTecPracticeExamAdapter;
import com.easier.writepre.entity.BannersInfo;
import com.easier.writepre.entity.YouMengType;
import com.easier.writepre.mainview.SocialMainView;
import com.easier.writepre.response.XsfTecPracticeExamDetailResponse;
import com.easier.writepre.ui.ActiveDetailActivity;
import com.easier.writepre.ui.BannerLinkActivity;
import com.easier.writepre.ui.CircleMsgListActivity;
import com.easier.writepre.ui.MainActivity;
import com.easier.writepre.ui.SquareImageLookActivity;
import com.easier.writepre.utils.LogUtils;
import com.easier.writepre.utils.LoginUtil;
import com.easier.writepre.utils.ToastUtil;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

public class ChildViewPager extends android.support.v4.view.ViewPager {

    private Context context;
    private int count;
    private int currentItem = 0;
    private int delayTime = 4000;
    /**
     * 请求更新显示的View。
     */
    public static final int MSG_UPDATE_IMAGE = 1;
    /**
     * 请求暂停轮播。
     */
    public static final int MSG_KEEP_SILENT = 2;
    /**
     * 请求恢复轮播。
     */
    public static final int MSG_BREAK_SILENT = 3;
    /**
     * 播放的当前页
     */
    public static final int MSG_PAGE_CHANGED = 4;

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            // 检查消息队列并移除未发送的消息，这主要是避免在复杂环境下消息出现重复等问题。
            if (hasMessages(MSG_UPDATE_IMAGE)) {
                removeMessages(MSG_UPDATE_IMAGE);
            }
            switch (msg.what) {
                case MSG_UPDATE_IMAGE:
                    currentItem++;
                    if (currentItem > count) {
                        currentItem = 0;
                    }
                    setCurrentItem(currentItem);
                    // 准备下次播放
                    sendEmptyMessageDelayed(MSG_UPDATE_IMAGE, delayTime);
                    break;
                case MSG_KEEP_SILENT:
                    // 只要不发送消息就暂停了
                    break;
                case MSG_BREAK_SILENT:
                    sendEmptyMessageDelayed(MSG_UPDATE_IMAGE, delayTime);
                    break;
                case MSG_PAGE_CHANGED:
                    // 记录当前的页号，避免播放的时候页面显示不正确。
                    currentItem = msg.arg1;
                    break;
                default:
                    break;
            }
        }

        ;
    };

    PointF downP = new PointF();
    PointF curP = new PointF();
    OnSingleTouchListener onSingleTouchListener;

    public ChildViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        addOnPageChangeListener(new MyOnPageChangeListener());
    }

    public ChildViewPager(Context context) {
        super(context);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent arg0) {
        if (getCurrentItem() != 0 && this instanceof ViewPager) {        // 告诉父容器不要拦截我们的事件，自行处理
            getParent().requestDisallowInterceptTouchEvent(true);
        } else
            getParent().requestDisallowInterceptTouchEvent(false);
        return super.onInterceptTouchEvent(arg0);
    }

    @Override
    public boolean onTouchEvent(MotionEvent arg0) {
        curP.x = arg0.getX();
        curP.y = arg0.getY();

        if (arg0.getAction() == MotionEvent.ACTION_DOWN) {
            downP.x = arg0.getX();
            downP.y = arg0.getY();
            getParent().requestDisallowInterceptTouchEvent(true);
        }

        if (arg0.getAction() == MotionEvent.ACTION_MOVE) {
            float minMove = 120; // 最小滑动距离
            float beginX = downP.x;
            float endX = curP.x;
            float beginY = downP.y;
            float endY = curP.y;
            if (beginX - endX > minMove) { // 左滑
                getParent().requestDisallowInterceptTouchEvent(true);
            } else if (endX - beginX > minMove) { // 右滑
                getParent().requestDisallowInterceptTouchEvent(true);
            } else if (beginY - endY > minMove) { // 上滑
                getParent().requestDisallowInterceptTouchEvent(false);
            } else if (endY - beginY > minMove) { // 下滑
                getParent().requestDisallowInterceptTouchEvent(false);
            }

        }

        if (arg0.getAction() == MotionEvent.ACTION_UP) {
            if (downP.x == curP.x && downP.y == curP.y) {
                onSingleTouch(getCurrentItem());
                return true;
            }
        }

        return super.onTouchEvent(arg0);
    }

    /**
     * 单击
     *
     * @param position
     */
    public void onSingleTouch(int position) {
        if (getAdapter() != null && getAdapter() instanceof SocialAdvertiseAdapter) {

            BannersInfo p = ((SocialAdvertiseAdapter) getAdapter()).getItem(position);
            if (TextUtils.isEmpty(p.getLink_url())) {
                return;
            }
            //友盟统计
            List<String> var = new ArrayList<String>();
            var.add(YouMengType.getName(4));
            var.add(YouMengType.getName(MainActivity.index));
            var.add(p.getTitle());
            YouMengType.onEvent(context, var, 1, p.getTitle());

            if (p.getLink_type().equals("0")) {
                Intent itIn = new Intent(context, BannerLinkActivity.class);
                itIn.putExtra("url", p.getLink_url());
                context.startActivity(itIn);
            } else if (p.getLink_type().equals("circle")) {
                Intent intentC = new Intent(context, CircleMsgListActivity.class);
                intentC.putExtra("circle_id", p.getLink_url()); // 圈子id
                context.startActivity(intentC);
            } else if (p.getLink_type().equals("topic")) {
                Intent intent = new Intent(context, ActiveDetailActivity.class);
                intent.putExtra("id", p.getLink_url());
                context.startActivity(intent);
            } else if (p.getLink_type().equals("vshow")) {
                if (SocialMainView.index == 3) {
                    return;
                }
                Intent intent = new Intent(context,
                        MainActivity.class);
                intent.putExtra(MainActivity.MAIN_TAB_INDEX,
                        MainActivity.TYPE_TWO);
                intent.putExtra(SocialMainView.TAB_INDEX,
                        SocialMainView.TAB_MicroExhibition);
                context.startActivity(intent);
            } else {
                Uri uri = Uri.parse(p.getLink_url());
                Intent itOut = new Intent(Intent.ACTION_VIEW, uri);
                context.startActivity(itOut);
            }
        } else if (getAdapter() != null && getAdapter() instanceof XsfTecPracticeExamAdapter) {
            XsfTecPracticeExamDetailResponse.Items item = ((XsfTecPracticeExamAdapter) getAdapter()).getItem(position);
            if (TextUtils.isEmpty(item.getPic_url())) {
                return;
            }
            ArrayList<XsfTecPracticeExamDetailResponse.Items> list =((XsfTecPracticeExamAdapter) getAdapter()).getData();

            String[] urls = new String[list.size()];
            for(int i = 0; i < list.size();i++){
                urls[i] = list.get(i).getPic_url();
            }

            Intent intent = new Intent(context, SquareImageLookActivity.class);
            intent.putExtra(SquareImageLookActivity.EXTRA_IMAGE_URLS, urls);
            intent.putExtra(SquareImageLookActivity.EXTRA_IMAGE_INDEX, position);
            context.startActivity(intent);

        } else {
            if (onSingleTouchListener != null) {
                onSingleTouchListener.onSingleTouch(position);
            }
        }


    }

    public void setDelayTime(int delayTime) {
        this.delayTime = delayTime;
    }

    public void startPlay() throws Exception {
        count = getAdapter().getCount();
        setFocusable(true);
        setCurrentItem(currentItem);
        if (count > 1) {
            handler.sendEmptyMessageDelayed(MSG_UPDATE_IMAGE, delayTime);
        }
    }

    public void stopPlay() throws Exception {
        handler.sendEmptyMessage(MSG_KEEP_SILENT);
    }

    class MyOnPageChangeListener implements OnPageChangeListener {

        // 配合Adapter的currentItem字段进行设置。
        @Override
        public void onPageSelected(int arg0) {
            handler.sendMessage(Message.obtain(handler, MSG_PAGE_CHANGED, arg0, 0));
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        // 覆写该方法实现轮播效果的暂停和恢复
        @Override
        public void onPageScrollStateChanged(int arg0) {
            switch (arg0) {
                case ViewPager.SCROLL_STATE_DRAGGING:
                    handler.sendEmptyMessage(MSG_KEEP_SILENT);
                    break;
                case ViewPager.SCROLL_STATE_IDLE:
                    handler.sendEmptyMessageDelayed(MSG_UPDATE_IMAGE, delayTime);
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 创建点击事件接口
     *
     * @author wanpg
     */
    public interface OnSingleTouchListener {
        public void onSingleTouch(int position);
    }

    public void setOnSingleTouchListener(OnSingleTouchListener onSingleTouchListener) {
        this.onSingleTouchListener = onSingleTouchListener;
    }

}
