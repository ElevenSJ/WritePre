package com.easier.writepre.utils;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.easier.writepre.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
//import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.media.MediaFormat;
import android.media.MediaMetadataRetriever;
//import android.media.ThumbnailUtils;
import android.view.View;

@SuppressLint("UseSparseArrays")
public class Bimp {

    public static int max = 0;

    public static Map<String, Boolean> map = new HashMap<String, Boolean>(); // 需要添加蒙版的图片

    public static void picCompress(Context context, ArrayList<String> mCache,
                                   ArrayList<String> compressPath) {
        compressPath.clear();
        LogUtils.e("开始压缩，总共：" + mCache.size() + "个文件");
        // 文件压缩
        int size = mCache.size();
        for (int i = 0; i < size; i++) {
            String path = mCache.get(i);
            String fileName = path.substring(path.lastIndexOf("/") + 1,
                    path.length());
            LogUtils.e("第：" + (i + 1) + "个文件："+fileName);
            if(!MediaFile.isImageFileType(path)){
                LogUtils.e("文件为非图片，无需压缩");
                compressPath.add(path);
                continue;
            }
            LogUtils.e("文件为图片，需压缩");
            if (FileUtils.fileIsExists(FileUtils.SD_COMP_IMAGES_PATH, fileName)) {    // && (map.get(path) != null && !map.get(path))
                LogUtils.e("图片已压缩");
                compressPath.add(FileUtils.SD_COMP_IMAGES_PATH + fileName);
                continue;
            }
            File file = new File(path);
            LogUtils.e(Bimp.class, "图片大小size：" + file.length());
            if (file.length() < 5 * 1024 * 1024f) { // 这样处理方式：不论图片多大都进行压缩了，如果这样
                // 直接把判断逻辑改下即可
                LogUtils.e(Bimp.class, "图片大小小于5M,不压缩"); // 图片小于5M 重新生成蒙版图片
                if (map.get(path) != null && map.get(path)) { // 添加蒙版效果
                    try {
                        compressPath.add(addFilterFile(
                                revitionImageSize(file.getPath(), 2500),
                                context, fileName).getPath());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else
                    compressPath.add(file.getPath()); // 不添加蒙版效果
            } else {
                try {
                    Bitmap bm = revitionImageSize(path, 2500);
                    if (bm == null) {
                        LogUtils.e("图片压缩失败");
                        continue;
                    }
                    LogUtils.e("图片压缩成功");
                    File newfile = saveBitmap(bm, FileUtils.SD_COMP_IMAGES_PATH
                            + fileName);
                    if (map.get(path) != null && map.get(path)) { // 添加蒙版效果
                        compressPath.add(addFilterFile(bm, context, fileName)
                                .getPath());
                    } else
                        compressPath.add(newfile.getPath()); // 不添加蒙版效果
                    bm.recycle();
                    System.gc();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return;
    }

    public static Bitmap revitionImageSize(String path, int size)
            throws IOException {
        File file = new File(path);
        if (!file.exists()) {
            return null;
        }
        Bitmap bitmap = null;
        BufferedInputStream in = new BufferedInputStream(new FileInputStream(
                file));
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        LogUtils.e(Bimp.class, "图片太大,开始压缩");
        BitmapFactory.decodeStream(in, null, options);
        LogUtils.e(Bimp.class, "图片原始大小Height：" + options.outHeight + "Width："
                + options.outWidth);
        in.close();
        int i = 0;
        while (true) {
            if ((options.outWidth >> i <= size)
                    && (options.outHeight >> i <= size)) {
                in = new BufferedInputStream(
                        new FileInputStream(new File(path)));
                options.inSampleSize = (int) Math.pow(2.0D, i);
                options.inJustDecodeBounds = false;
                bitmap = BitmapFactory.decodeStream(in, null, options);
                break;
            }
            i += 1;
        }
        LogUtils.e(Bimp.class, "压缩后图片大小Height：" + options.outHeight + "Width："
                + options.outWidth);
        return bitmap;
    }

    /**
     * 视频缩略图
     *
     * @param filePath
     * @return
     */
    public static Bitmap getVideoThumbnail(String filePath) {
        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(filePath);
            bitmap = retriever.getFrameAtTime();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (RuntimeException e) {
            e.printStackTrace();
        } finally {
            try {
                retriever.release();
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }

    /**
     * 保存方法
     *
     * @return
     */
    public static File saveBitmap(Bitmap bm, String path) {
        LogUtils.e(Bimp.class, "保存图片");
        if (bm == null) {
            LogUtils.e(Bimp.class, "未获取到bitmap数据");
            return null;
        }
        File f = new File(path);
        f.mkdirs();
        if (f.exists()) {
            f.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(f);
            bm.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
            LogUtils.e(Bimp.class, "保存成功");
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            LogUtils.e(Bimp.class, "保存失败");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            LogUtils.e(Bimp.class, "保存失败");
        }
        return f;
    }

    /**
     * 将view生产bitmap对象
     *
     * @param v
     * @return
     */
    public static Bitmap createViewBitmap(View v) {
        Bitmap bitmap = Bitmap.createBitmap(v.getWidth(), v.getHeight(),
                Bitmap.Config.RGB_565); // ARGB_8888
        Canvas canvas = new Canvas(bitmap);
        v.draw(canvas);
        return bitmap;
    }

    /**
     * 添加水印
     *
     * @param src
     * @param watermark
     * @param alpha
     * @return
     */
    public static Bitmap Watermark(Bitmap src, Bitmap watermark, int alpha) {
        if (src == null) {
            return null;
        }
        int w = src.getWidth();
        int h = src.getHeight();
        // int ww = watermark.getWidth();
        // int wh = watermark.getHeight();
        Paint paint = new Paint();
        paint.setAlpha(alpha);
        paint.setAntiAlias(true);
        Bitmap newb = Bitmap.createBitmap(w, h, Config.ARGB_8888);
        Canvas cv = new Canvas(newb);
        cv.drawBitmap(src, 0, 0, null);
        cv.drawBitmap(watermark, 5, h - 80, paint);
        cv.save(Canvas.ALL_SAVE_FLAG);
        cv.restore();
        return newb;
    }

    // 等比缩放图片  解决在有的机型上添加聚光灯效果显示错位
    public static Bitmap zoomImage(Bitmap bitmap, int newWidth, int newHeight) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        // 计算缩放比例
        float scaleW = (float) newWidth / width;
        float sacleH = (float) newHeight / height;
        // 取得缩放Matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleW, sacleH);
        // matrix.postRotate(90);
        Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height,     // 得到新图片
                matrix, true);
        return newBitmap;
    }

    /**
     * 添加图片滤镜效果
     */
    public static Bitmap addFilterBitmap(Bitmap src, Context context) {
        if (src == null) {
            return null;
        }
        int w = src.getWidth();
        int h = src.getHeight();
        Paint paint = new Paint();
        paint.setAlpha(255);
        paint.setAntiAlias(true);
        Bitmap newb = Bitmap.createBitmap(w, h, Config.ARGB_8888);
        Canvas cv = new Canvas(newb);
        cv.drawBitmap(src, 0, 0, null);
        cv.drawBitmap(
                zoomImage(BitmapFactory.decodeResource(context.getResources(),
                        R.drawable.filter_bg), w, h), 0, 0, paint);
        cv.save(Canvas.ALL_SAVE_FLAG);
        cv.restore();
        return newb;
    }

    /**
     * 返回添加蒙版过生成的图片所属路径
     *
     * @param src
     * @return
     */
    private static File addFilterFile(Bitmap src, Context context,
                                      String fileName) {
        return saveBitmap(Bimp.addFilterBitmap(src, context),
                FileUtils.SD_COMP_IMAGES_PATH // 压缩后的图片上添加蒙版进行保存
                        + fileName);
    }

    public static Bitmap getBitmapDrawableFromStream(int resId,
                                                     Context context, int pressSize) {
        // 获取资源图片流
        InputStream is = context.getResources().openRawResource(resId);
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inJustDecodeBounds = true;
        // BitmapFactory.decodeStream(is, null, opt);
        opt.inJustDecodeBounds = false;
        opt.inPreferredConfig = Bitmap.Config.ARGB_8888;
        opt.inSampleSize = pressSize;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        Bitmap bitmap = BitmapFactory.decodeStream(is, null, opt);
        try {
            if (is != null) {
                is.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * 图片旋转
     *
     * @param tmpBitmap
     * @param degrees
     * @return
     */
    public static Bitmap rotateToDegrees(Bitmap tmpBitmap, float degrees) {
        Matrix matrix = new Matrix();
        matrix.reset();
        matrix.setRotate(degrees);
        return tmpBitmap = Bitmap.createBitmap(tmpBitmap, 0, 0,
                tmpBitmap.getWidth(), tmpBitmap.getHeight(), matrix, true);
    }

    /**
     * 读取照片exif信息中的旋转角度
     *
     * @param path 照片路径
     * @return角度
     */
    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
                default:
                    degree = 0;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    public static Bitmap comp(Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        if (baos.toByteArray().length / 1024 > 1024) {// 判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
            baos.reset();// 重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, 50, baos);// 这里压缩50%，把压缩后的数据存放到baos中
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        // 开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        // 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
        float hh = 800f;// 这里设置高度为800f
        float ww = 480f;// 这里设置宽度为480f
        // 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;// be=1表示不缩放
        if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;// 设置缩放比例
        // 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        isBm = new ByteArrayInputStream(baos.toByteArray());
        bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        return bitmap;// 压缩好比例大小后再进行质量压缩
    }

    public static Bitmap compressImage(Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > 100) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();// 重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;// 每次都减少10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
        return bitmap;
    }

    /**
     * 羽化滤镜效果
     *
     * @param bitmap
     * @return
     */
    public static Bitmap changeToEclosion(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int dst[] = new int[width * height];
        bitmap.getPixels(dst, 0, width, 0, 0, width, height);

        int ratio = width > height ? height * 32768 / width : width * 32768
                / height;

        int cx = width >> 1;
        int cy = height >> 1;
        int max = cx * cx + cy * cy;
        int min = (int) (max * (1 - 0.5f));
        int diff = max - min;

        int R, G, B;
        int pos, pixColor;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                pos = y * width + x;
                pixColor = dst[pos];
                R = Color.red(pixColor);
                G = Color.green(pixColor);
                B = Color.blue(pixColor);

                int dx = cx - x;
                int dy = cy - y;
                if (width > height) {
                    dx = (dx * ratio) >> 15;
                } else {
                    dy = (dy * ratio) >> 15;
                }

                int distSq = dx * dx + dy * dy;
                float v = ((float) distSq / diff) * 255;
                R = (int) (R + (v));
                G = (int) (G + (v));
                B = (int) (B + (v));
                R = (R > 255 ? 255 : (R < 0 ? 0 : R));
                G = (G > 255 ? 255 : (G < 0 ? 0 : G));
                B = (B > 255 ? 255 : (B < 0 ? 0 : B));

                dst[pos] = Color.rgb(R, G, B);
            }
        }
        Bitmap processBitmap = Bitmap.createBitmap(width, height,
                Bitmap.Config.RGB_565);
        processBitmap.setPixels(dst, 0, width, 0, 0, width, height);
        return processBitmap;
    }

    public static Bitmap changeToSoftness(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int dst[] = new int[width * height];
        bitmap.getPixels(dst, 0, width, 0, 0, width, height);

        int R, G, B, pixel;
        int pos, pixColor;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                pos = y * width + x;
                pixColor = dst[pos];
                R = Color.red(pixColor); // (color >> 16) & 0xFF
                G = Color.green(pixColor); // (color >> 8) & 0xFF;
                B = Color.blue(pixColor); // color & 0xFF
                pixel = 255 - (255 - R) * (255 - R) / 255;
                if (pixel < 0)
                    pixel = -pixel;
                pixel = pixel * R / 256;
                if (pixel > 255)
                    pixel = 255;
                R = pixel;

                pixel = 255 - (255 - G) * (255 - G) / 255;
                if (pixel < 0)
                    pixel = -pixel;
                pixel = pixel * R / 256;
                if (pixel > 255)
                    pixel = 255;
                G = pixel;

                pixel = 255 - (255 - B) * (255 - B) / 255;
                if (pixel < 0)
                    pixel = -pixel;
                pixel = pixel * G / 256;
                if (pixel > 255)
                    pixel = 255;
                B = pixel;

                dst[pos] = Color.rgb(R, G, B);
            }
        }
        Bitmap processBitmap = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);
        processBitmap.setPixels(dst, 0, width, 0, 0, width, height);

        return processBitmap;
    }

}
