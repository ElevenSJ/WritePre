package com.easier.writepre.adapter;

import java.util.ArrayList;

import com.alibaba.fastjson.serializer.SerialWriterStringEncoder;
import com.easier.writepre.R;
import com.easier.writepre.app.WritePreApp;
import com.easier.writepre.ui.BaseActivity;
import com.easier.writepre.ui.DiyCourseClassActivity;
import com.easier.writepre.ui.SendTopicActivity;
import com.easier.writepre.utils.Bimp;
import com.easier.writepre.utils.BitmapHelp;
import com.easier.writepre.utils.DiskCache;
import com.easier.writepre.utils.DiskCache.ImageCallback;
import com.easier.writepre.utils.ToastUtil;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.BitmapLoadCallBack;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;
import com.sj.autolayout.utils.AutoUtils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout.LayoutParams;

public class ImageAdapter extends BaseAdapter {

    private int mWidth;
    private ArrayList<String> mData = new ArrayList<String>();
    private ArrayList<String> mSelectedData = new ArrayList<String>();
    private int MAX_SIZE = 0;
    private Context ctx;
    private GridView gridView;
    public static int num = 3;

    private static boolean addItem = true;

    private static boolean canSelected = false;

    private OnItemClickListener listener;

    private int groupIndex = 0;

    private Bitmap imgBitmap = null;

    public ImageAdapter(Context ctx, GridView gridView, int MAX_SIZE) {
        this(ctx, gridView, MAX_SIZE, num, addItem, canSelected);
    }

    public ImageAdapter(Context ctx, GridView gridView, int MAX_SIZE,
                        boolean canSelected) {
        this(ctx, gridView, MAX_SIZE, num, addItem, canSelected);
    }

    public ImageAdapter(Context ctx, GridView gridView, int MAX_SIZE, int num,
                        boolean addItem, boolean canSelected) {
        super();
        this.ctx = ctx;
        this.gridView = gridView;
        this.MAX_SIZE = MAX_SIZE;
        this.addItem = addItem;
        this.canSelected = canSelected;
        this.mWidth = WritePreApp.getApp().getWidth(1f) / num;
    }

    public void setImagMax(int imagMax) {
        this.MAX_SIZE = imagMax;
    }

    public void setData(ArrayList<String> mImageFiles) {
        if (mImageFiles == null) {
            mData.clear();
            if (addItem) {
                mData.add("addItem");
            }
        } else {
            mData.clear();
            mData.addAll(mImageFiles);
            if (addItem) {
                if (mData.size() < MAX_SIZE) {
                    mData.add("addItem");
                }
            }
        }
        this.notifyDataSetChanged();
    }

    public ArrayList<String> getData() {
        return mData;
    }

    public void setSelectedData(ArrayList<String> mImageSelectedFiles) {
        if (mImageSelectedFiles != null && !mImageSelectedFiles.isEmpty()) {
            mSelectedData.clear();
            mSelectedData.addAll(mImageSelectedFiles);
        } else {
            mSelectedData.clear();
        }
        this.notifyDataSetChanged();
    }

    public ArrayList<String> getSelectedData() {
        return mSelectedData;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public String getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(ctx).inflate(R.layout.image_item,
                    parent, false);
            viewHolder.imageView = (ImageView) convertView
                    .findViewById(R.id.image);
            viewHolder.checkSelected = (ImageView) convertView
                    .findViewById(R.id.image_selected);
            viewHolder.selectedLayout = (LinearLayout) convertView
                    .findViewById(R.id.selected_layout);
            convertView.setTag(viewHolder);
            AutoUtils.autoSize(convertView);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final String path = mData.get(position);

        if ("addItem".equals(path)) {
            viewHolder.imageView
                    .setImageResource(R.drawable.icon_addpic_unfocused);
            viewHolder.imageView.setScaleType(ScaleType.FIT_XY);
        } else {
            viewHolder.imageView.setScaleType(ScaleType.CENTER_CROP);
            if (path.startsWith("http")) {
                BitmapHelp.getBitmapUtils().display(viewHolder.imageView, path,
                        new BitmapLoadCallBack<View>() {

                            @Override
                            public void onLoadCompleted(View arg0, String arg1,
                                                        Bitmap arg2, BitmapDisplayConfig arg3,
                                                        BitmapLoadFrom arg4) {
                                viewHolder.imageView.setImageBitmap(arg2);

                            }

                            @Override
                            public void onLoadFailed(View arg0, String arg1,
                                                     Drawable arg2) {
                                viewHolder.imageView
                                        .setImageResource(R.drawable.empty_photo);

                            }
                        });
            } else {
                viewHolder.imageView.setTag(path);
                final int degree = Bimp.readPictureDegree(path);
                imgBitmap = DiskCache.getInstance().loadImage(path, mWidth,
                        mWidth, new ImageCallback() {

                            @Override
                            public void imageLoaded(Bitmap bitmap, String path) {
                                View imageView = gridView.findViewWithTag(path);
                                if (imageView != null
                                        && imageView.getVisibility() == View.VISIBLE
                                        && bitmap != null) {
                                    if (degree == 0) {
                                        ((ImageView) imageView)
                                                .setImageBitmap(bitmap);
                                    } else {
                                        imgBitmap = Bimp.rotateToDegrees(
                                                bitmap, degree);
                                        ((ImageView) imageView)
                                                .setImageBitmap(imgBitmap);
                                    }
                                }
                            }
                        });
                if (imgBitmap != null) {
                    viewHolder.imageView.setImageBitmap(imgBitmap);
                } else {
                    viewHolder.imageView
                            .setImageResource(R.drawable.empty_photo);
                }
            }
        }
        viewHolder.checkSelected.bringToFront();
        if (mSelectedData.contains(path)) {
            viewHolder.selectedLayout.setVisibility(View.VISIBLE);
            viewHolder.checkSelected.setVisibility(View.VISIBLE);
        } else {
            viewHolder.selectedLayout.setVisibility(View.GONE);
            viewHolder.checkSelected.setVisibility(View.GONE);
        }
        if (canSelected) {
            viewHolder.checkSelected.setImageResource(R.drawable.check_red);
        } else {
            viewHolder.checkSelected
                    .setImageResource(R.drawable.delete_rectangle);
        }
//		if (canSelected) {
        viewHolder.imageView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onClick(position, v);
                }
                if (canSelected) {
                    if (viewHolder.checkSelected.getVisibility() == View.GONE) {
                        viewHolder.checkSelected.setVisibility(View.VISIBLE);
                        mSelectedData.add(path);
                    } else {
                        viewHolder.checkSelected.setVisibility(View.GONE);
                        mSelectedData.remove(path);
                    }
                }
            }
        });
//		}
        viewHolder.selectedLayout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mSelectedData.remove(path);
                if (!canSelected) {
                    mData.remove(position);
                    if (listener != null) {
                        listener.onDelete(position, v);
                    }
                    if (addItem) {
                        if (mData.isEmpty()) {
                            mData.add("addItem");
                        } else {
                            if (!"addItem".equals(mData.get(mData.size() - 1))) {
                                mData.add("addItem");
                            }
                        }
                    }
                }
                if (ctx instanceof DiyCourseClassActivity) {
                    ((DiyCourseClassActivity) ctx).updateData(groupIndex,
                            position);
                } else {
                    ((BaseActivity) ctx).updateSelectedData();
                }
                notifyDataSetChanged();

            }
        });
        LayoutParams layoutParams = new LayoutParams(mWidth,mWidth);
        viewHolder.imageView.setLayoutParams(layoutParams);
        return convertView;
    }

    class ViewHolder {
        ImageView imageView;
        ImageView checkSelected;
        LinearLayout selectedLayout;
    }

    public interface OnItemClickListener {
        void onClick(int position, View v);

        void onDelete(int position, View v);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setGroupIndex(int groupPosition) {
        groupIndex = groupPosition;

    }
}
