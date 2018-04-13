package com.easier.writepre.fragment;

import java.io.File;

import com.easier.writepre.R;
import com.easier.writepre.entity.ActionItem;
import com.easier.writepre.http.Constant;
import com.easier.writepre.utils.Bimp;
import com.easier.writepre.utils.BitmapHelp;
import com.easier.writepre.utils.FileUtils;
import com.easier.writepre.utils.LogUtils;
import com.easier.writepre.utils.StringUtil;
import com.easier.writepre.utils.ToastUtil;
import com.easier.writepre.widget.MyDialog;
import com.easier.writepre.widget.PhotoView;
import com.easier.writepre.widget.PhotoViewAttacher.OnPhotoTapListener;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.BitmapLoadCallBack;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.ProgressBar;

/**
 * @author kai.zhong
 */
public class SquareAllEssenceImageDetailFragment extends BaseFragment {

    private String mImageUrl;

    private PhotoView mImageView;

    private ProgressBar progressBar;

    //	 private PhotoViewAttacher mAttacher;
    private String downloadImageUrl;

    public static SquareAllEssenceImageDetailFragment newInstance(String imageUrl) {
        final SquareAllEssenceImageDetailFragment f = new SquareAllEssenceImageDetailFragment();

        final Bundle args = new Bundle();
        args.putString("url", imageUrl);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mImageUrl = getArguments() != null ? getArguments().getString("url") : null;
    }

    @Override
    public int getContextView() {
        return R.layout.image_detail_fragment;
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

    @Override
    protected void init() {
        mImageView = (PhotoView) findViewById(R.id.image);
        mImageView.setOnPhotoTapListener(new OnPhotoTapListener() {

            @Override
            public void onPhotoTap(View arg0, float arg1, float arg2) {
                if (getActivity() != null) {
                    getActivity().finish();
                }
            }
        });
        mImageView.setOnLongClickListener(new OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                showSaveDialog();
                return false;
            }
        });

        progressBar = (ProgressBar) findViewById(R.id.loading);
    }

    protected void showSaveDialog() {
        MyDialog deleteDialog = new MyDialog(getActivity(), R.style.loading_dialog);

        deleteDialog.cleanAction();
        deleteDialog.addAction(new ActionItem(getActivity(), 0, "保存图片"));

        if (!deleteDialog.isShowing()) {
            deleteDialog.show();
            deleteDialog.setItemOnClickListener(new MyDialog.OnItemOnClickListener() {

                @Override
                public void onItemClick(ActionItem item) {
                    String tempfileName = "";
                    if (mImageUrl.startsWith("http://rong")) {
                        tempfileName = StringUtil.getMD5(mImageUrl);
                    } else {
                        tempfileName = mImageUrl.substring(mImageUrl.lastIndexOf("/") + 1);
                    }
                    final String fileName = tempfileName;
                    if (FileUtils.fileIsExists(FileUtils.SD_IMAGES_PATH, fileName)) {
                        ToastUtil.show("已保存");
                    } else {
                        new HttpUtils().download(StringUtil.getImgeUrl(mImageUrl),
                                FileUtils.SD_IMAGES_PATH + fileName + "tmp", true, true, new RequestCallBack<File>() {

                                    @Override
                                    public void onLoading(long total, long current, boolean isUploading) {
                                        // TODO Auto-generated method stub
                                        super.onLoading(total, current, isUploading);
                                    }

                                    @Override
                                    public void onSuccess(ResponseInfo<File> arg0) {
                                        new File(FileUtils.SD_IMAGES_PATH + fileName + "tmp")
                                                .renameTo(new File(FileUtils.SD_IMAGES_PATH + fileName));
                                        insertToDB(FileUtils.SD_IMAGES_PATH, fileName);
                                        loadLocalImag(FileUtils.SD_IMAGES_PATH + fileName);
                                    }

                                    @Override
                                    public void onFailure(HttpException arg0, String arg1) {
                                        String tip = arg1.contains("completely") ? "已存在该文件" : "保存失败";
                                        ToastUtil.show(tip);
                                        LogUtils.e("image-sava-info"+ arg1);
                                        if (!arg1.contains("completely")) {
                                            new File(FileUtils.SD_IMAGES_PATH + fileName + "tmp").delete();
                                        }
                                    }
                                });
                    }
                }
            });
        }
    }

    protected void insertToDB(String filePath, String fileName) {
        try {
            MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), filePath + fileName, fileName,
                    null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 最后通知图库更新
        getActivity().sendBroadcast(
                new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + filePath + fileName)));
        ToastUtil.show("保存成功");
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        String fileName = "";
        if (mImageUrl.startsWith("http://rong")) {
            fileName = StringUtil.getMD5(mImageUrl);
        } else {
            fileName = mImageUrl.substring(mImageUrl.lastIndexOf("/") + 1);
        }
        if (FileUtils.fileIsExists(FileUtils.SD_IMAGES_PATH, fileName)) {
            loadLocalImag(FileUtils.SD_IMAGES_PATH + fileName);
        } else {
            if (mImageUrl.startsWith("group")) {
                loadNetImage(StringUtil.getImgeUrl(mImageUrl));
            } else if (mImageUrl.startsWith("http://rong")) {
                loadNetImage(mImageUrl);
            } else {
                loadNetImage(StringUtil.getImgeUrl(mImageUrl) + Constant.FULL_IMAGE_SUFFIX);
            }
        }

    }

    private void loadNetImage(String path) {
        BitmapHelp.getBitmapUtils().display(mImageView, path, new BitmapLoadCallBack<View>() {

            @Override
            public void onLoadCompleted(View arg0, String arg1, Bitmap arg2, BitmapDisplayConfig arg3,
                                        BitmapLoadFrom arg4) {
                if (isDestory) {
                    return;
                }
                mImageView.setImageBitmap(arg2);
//				mImageView.setImageBitmap(Bimp.addFilter(arg2, ((BitmapDrawable) getResources().getDrawable(
//						R.drawable.filter_bg)).getBitmap(), 255));
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onLoadFailed(View arg0, String arg1, Drawable arg2) {
                if (isDestory) {
                    return;
                }
                String message = "图片无法显示";
                ToastUtil.show(message);
                progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onLoading(View container, String uri, BitmapDisplayConfig config, long total, long current) {
                super.onLoading(container, uri, config, total, current);
                progressBar.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub

    }

    public void loadLocalImag(final String path) {
        // Bitmap bitmap = null;
        mImageView.setTag(path);
        BitmapHelp.getBitmapUtils().display(mImageView, path, new BitmapLoadCallBack<View>() {

            @Override
            public void onLoadCompleted(View arg0, String arg1, Bitmap arg2, BitmapDisplayConfig arg3,
                                        BitmapLoadFrom arg4) {
                if (isDestory) {
                    return;
                }
                if(new File(path).length()>3*1024*1024f)
                {
                    arg2= Bimp.zoomImage(arg2,arg2.getWidth()/2,arg2.getHeight()/2);
                }

                mImageView.setImageBitmap(arg2);
            }

            @Override
            public void onLoadFailed(View arg0, String arg1, Drawable arg2) {
                if (isDestory) {
                    return;
                }
                if (mImageUrl.startsWith("group")) {
                    loadNetImage(StringUtil.getImgeUrl(mImageUrl));
                } else if (mImageUrl.startsWith("http://rong")) {
                    loadNetImage(mImageUrl);
                } else {
                    loadNetImage(StringUtil.getImgeUrl(mImageUrl) + Constant.FULL_IMAGE_SUFFIX);
                }

            }
        });
        // bitmap = DiskCache.getInstance().loadOriginalImage(path, new
        // ImageCallback() {
        //
        // @Override
        // public void imageLoaded(Bitmap bitmap, String path) {
        // if (mImageView != null && mImageView.getVisibility() == View.VISIBLE
        // && bitmap != null) {
        // mImageView.setImageBitmap(bitmap);
        // }
        // }
        // });
        // if (bitmap != null) {
        // try {
        // if (bitmap.getWidth() != 0 && bitmap.getHeight() != 0) {
        // mImageView.setImageBitmap(bitmap);
        // } else {
        // loadNetImage(mImageUrl + Constant.FULL_IMAGE_SUFFIX);
        // }
        // } catch (Exception e) {
        // loadNetImage(mImageUrl + Constant.FULL_IMAGE_SUFFIX);
        // }
        //
        // } else {
        // loadNetImage(mImageUrl + Constant.FULL_IMAGE_SUFFIX);
        // }
    }

}
