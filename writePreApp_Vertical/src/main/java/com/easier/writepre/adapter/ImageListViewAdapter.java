package com.easier.writepre.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.easier.writepre.R;
import com.easier.writepre.app.WritePreApp;
import com.easier.writepre.ui.BaseActivity;
import com.easier.writepre.ui.DiyCourseClassActivity;
import com.easier.writepre.utils.Bimp;
import com.easier.writepre.utils.BitmapHelp;
import com.easier.writepre.utils.DiskCache;
import com.easier.writepre.utils.DiskCache.ImageCallback;
import com.easier.writepre.utils.StringUtil;
import com.easier.writepre.widget.RoundCustomImageView;
import com.facebook.common.util.UriUtil;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.BitmapLoadCallBack;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;
import com.sj.autolayout.utils.AutoUtils;

import java.util.ArrayList;

public class ImageListViewAdapter extends BaseAdapter {

    private int mWidth;
    private ArrayList<String> mData = new ArrayList<String>();
    private ArrayList<String> mSelectedData = new ArrayList<String>();
    private int MAX_SIZE = 0;
    private Context ctx;
    private ListView listView;
    public static int num = 1;

    private static boolean addItem = true;

    private static boolean canSelected = false;

    private OnItemClickListener listener;

    private int groupIndex = 0;

    private Bitmap imgBitmap = null;

    public ImageListViewAdapter(Context ctx, ListView listView, int MAX_SIZE) {
        this(ctx, listView, MAX_SIZE, num, addItem, canSelected);
    }

    public ImageListViewAdapter(Context ctx, ListView listView, int MAX_SIZE,
                                boolean canSelected) {
        this(ctx, listView, MAX_SIZE, num, addItem, canSelected);
    }

    public ImageListViewAdapter(Context ctx, ListView listView, int MAX_SIZE, int num,
                                boolean addItem, boolean canSelected) {
        super();
        this.ctx = ctx;
        this.listView = listView;
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
            convertView = LayoutInflater.from(ctx).inflate(R.layout.list_image_item,
                    parent, false);
            viewHolder.imageView = (SimpleDraweeView) convertView
                    .findViewById(R.id.iv_img);
            viewHolder.iv_del = (ImageView) convertView
                    .findViewById(R.id.iv_del);
            viewHolder.rel_add = (RelativeLayout) convertView
                    .findViewById(R.id.rel_add);
            viewHolder.rel_content = (RelativeLayout) convertView
                    .findViewById(R.id.rel_content);
            convertView.setTag(viewHolder);
            AutoUtils.autoSize(convertView);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final String path = mData.get(position);

        if ("addItem".equals(path)) {
            viewHolder.rel_add.setVisibility(View.VISIBLE);
            viewHolder.rel_content.setVisibility(View.GONE);
        } else {
            viewHolder.rel_add.setVisibility(View.GONE);
            viewHolder.rel_content.setVisibility(View.VISIBLE);
            viewHolder.imageView.setScaleType(ScaleType.CENTER_CROP);
            if (path.startsWith("http")) {
                viewHolder.imageView.setImageURI(Uri.parse(path));
            } else {
                viewHolder.imageView.setTag(path);
//                final int degree = Bimp.readPictureDegree(path);
//                imgBitmap = DiskCache.getInstance().loadImage(path, mWidth,
//                        AutoUtils.getPercentHeightSize(280), new ImageCallback() {
//
//                            @Override
//                            public void imageLoaded(Bitmap bitmap, String path) {
//                                View imageView = listView.findViewWithTag(path);
//                                if (imageView != null
//                                        && imageView.getVisibility() == View.VISIBLE
//                                        && bitmap != null) {
//                                    if (degree == 0) {
//                                        ((SimpleDraweeView) imageView)
//                                                .setImageBitmap(bitmap);
//                                    } else {
//                                        imgBitmap = Bimp.rotateToDegrees(
//                                                bitmap, degree);
//                                        ((SimpleDraweeView) imageView)
//                                                .setImageBitmap(imgBitmap);
//                                    }
//                                }
//                            }
//                        });
//                if (imgBitmap != null) {
//                    viewHolder.imageView.setImageBitmap(imgBitmap);
//                }
//                if (degree == 0) {
//
//                } else {
//                    viewHolder.imageView.setImageURI("file://" + path);
//                    viewHolder.imageView.setRotation(degree);
//                }
                Uri uri = Uri.parse("file://" + path);
                int width = mWidth, height = mWidth / 3;

                ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
                        .setResizeOptions(new ResizeOptions(width, height))
                        .setAutoRotateEnabled(true)
                        .build();
                DraweeController draweeController= viewHolder.imageView.getController();
                PipelineDraweeController controller = (PipelineDraweeController) Fresco.newDraweeControllerBuilder()
                        .setOldController(draweeController)
                        .setImageRequest(request)
                        .build();
                viewHolder.imageView.setController(controller);
            }
        }
        viewHolder.iv_del.bringToFront();
        if (mSelectedData.contains(path)) {
            viewHolder.iv_del.setVisibility(View.VISIBLE);
        } else {
            viewHolder.iv_del.setVisibility(View.GONE);
        }
        if (canSelected) {
            viewHolder.iv_del.setImageResource(R.drawable.check_red);
        } else {
            viewHolder.iv_del
                    .setImageResource(R.drawable.circle_clear_record);
        }
//		if (canSelected) {
        viewHolder.rel_add.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onClick(position, v);
                }
            }
        });
        viewHolder.imageView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onClick(position, v);
                }
                if (canSelected) {
                    if (viewHolder.iv_del.getVisibility() == View.GONE) {
                        viewHolder.iv_del.setVisibility(View.VISIBLE);
                        mSelectedData.add(path);
                    } else {
                        viewHolder.iv_del.setVisibility(View.GONE);
                        mSelectedData.remove(path);
                    }
                }
            }
        });
//		}
        viewHolder.iv_del.setOnClickListener(new OnClickListener() {

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
        LayoutParams layoutParams = new LayoutParams(mWidth, AutoUtils.getPercentHeightSize(280));
        viewHolder.imageView.setLayoutParams(layoutParams);
        return convertView;
    }

    class ViewHolder {
        SimpleDraweeView imageView;
        ImageView iv_del;
        RelativeLayout rel_add, rel_content;
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
