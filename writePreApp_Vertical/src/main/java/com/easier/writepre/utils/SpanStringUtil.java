package com.easier.writepre.utils;

import android.content.Context;
import android.content.Intent;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.easier.writepre.R;
import com.easier.writepre.ui.UserInfoActivity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 处理@评论时 TextView换行点击
 */
public class SpanStringUtil {
    /**
     * 关键字高亮变色
     *
     * @param color
     *            变化的色值
     * @param text
     *            文字
     * @param keyword
     *            文字中的关键字
     * @return
     */
    public static SpannableString matcherSearchTitle(int color, String text,
                                                     String keyword) {
        SpannableString s = new SpannableString(text);
        Pattern p = Pattern.compile(keyword);
        Matcher m = p.matcher(s);
        while (m.find()) {
            int start = m.start();
            int end = m.end();
            s.setSpan(new ForegroundColorSpan(color), start, end,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return s;
    }
    /**
     * 关键字首次高亮变色
     *
     * @param color
     *            变化的色值
     * @param text
     *            文字
     * @param keyword
     *            文字中的关键字
     * @return
     */
    public static SpannableString matcherSearchTitleFirst(int color, String text,
                                                     String keyword) {
        SpannableString s = new SpannableString(text);
        Pattern p = Pattern.compile(keyword);
        Matcher m = p.matcher(s);
        while (m.find()) {
            int start = m.start();
            int end = m.end();
            s.setSpan(new ForegroundColorSpan(color), start, end,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            break;
        }
        return s;
    }
    /**
     * 多个关键字高亮变色
     *
     * @param color
     *            变化的色值
     * @param text
     *            文字
     * @param keyword
     *            文字中的关键字数组
     * @return
     */
    public static SpannableString matcherSearchTitle(int color, String text,
                                                     String[] keyword) {
        SpannableString s = new SpannableString(text);
        for (int i = 0; i < keyword.length; i++) {
            Pattern p = Pattern.compile(keyword[i]);
            Matcher m = p.matcher(s);
            while (m.find()) {
                int start = m.start();
                int end = m.end();
                s.setSpan(new ForegroundColorSpan(color), start, end,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        return s;
    }

    @SuppressWarnings("unused")
    public SpannableString getClickableSpan(final Context context,
                                            final String userId, String comment) {
        View.OnClickListener click = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, UserInfoActivity.class);
                intent.putExtra("user_id", userId);
                context.startActivity(intent);
            }
        };
        SpannableString spanableInfo = new SpannableString(comment);
        int start = 3;
        int end = comment.split(":")[0].length();
        spanableInfo.setSpan(new Clickable(click, context), start, end,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spanableInfo;
    }

    public static boolean onTouchClick(View v, MotionEvent event) {
        boolean ret = false;
        CharSequence text = ((TextView) v).getText();
        Spannable stext = Spannable.Factory.getInstance().newSpannable(text);
        TextView widget = (TextView) v;
        int action = event.getAction();
        if (action == MotionEvent.ACTION_UP
                || action == MotionEvent.ACTION_DOWN) {
            int x = (int) event.getX();
            int y = (int) event.getY();
            x -= widget.getTotalPaddingLeft();
            y -= widget.getTotalPaddingTop();
            x += widget.getScrollX();
            y += widget.getScrollY();
            Layout layout = widget.getLayout();
            int line = layout.getLineForVertical(y);
            int off = layout.getOffsetForHorizontal(line, x);
            ClickableSpan[] link = stext
                    .getSpans(off, off, ClickableSpan.class);
            if (link.length != 0) {
                if (action == MotionEvent.ACTION_UP) {
                    link[0].onClick(widget);
                }
                ret = true;
            }
        }
        return ret;
    }

    class Clickable extends ClickableSpan implements OnClickListener {

        private Context context;

        private final View.OnClickListener mListener;

        public Clickable(View.OnClickListener click, Context context) {
            this.mListener = click;
            this.context = context;
        }

        @Override
        public void onClick(View v) {
            mListener.onClick(v);
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            super.updateDrawState(ds);
            ds.setUnderlineText(false);
            ds.setColor(context.getResources().getColor(R.color.blue_low));
        }
    }
}
