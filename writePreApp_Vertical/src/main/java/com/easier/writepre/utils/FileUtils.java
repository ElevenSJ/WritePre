package com.easier.writepre.utils;

import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;

import com.easier.writepre.widget.VersionUpdateDialog;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.Comparator;

/**
 * @author sunjie 文件管理工具类
 */
public class FileUtils implements Comparator<File> {

    public static final String SYSTEM_VERSION_NAME = "writepre.apk";

    public static final String SD_PATH = Environment.getExternalStorageDirectory().getPath();

    public static final String APP_NAME = "/writepre";
    /**
     * 版本
     */
    public static final String SD_APP_PATH = SD_PATH + APP_NAME + "/app/";
    /**
     * 版本修复path
     */
    public static final String SD_HOT_FIX_PATH = SD_PATH + APP_NAME + "/hot_fix/";
    /**
     * 图片
     */
    public static final String SD_IMAGES_PATH = SD_PATH + APP_NAME + "/images/";
    /**
     * 截图图片
     */
    public static final String SD_CLIP_IMAGES_PATH = SD_PATH + APP_NAME + "/clip_images/";
    /**
     * 压缩图片
     */
    public static final String SD_COMP_IMAGES_PATH = SD_PATH + APP_NAME + "/comp_images/";
    /**
     * 视频
     */
    public static final String SD_MY_VIDEO = SD_PATH + APP_NAME + "/video/";

    /**
     * 视频缓存
     */
    public static final String SD_VIDEO_CACHE = SD_PATH + APP_NAME + "/video_cache/";
    /**
     * 音频
     */
    public static final String SD_MY_VOICE = SD_PATH + APP_NAME + "/voice/";
    /**
     * 本地缓存图片
     */
    public final static String SD_IMAGES_CACHE = SD_PATH + APP_NAME + "/.imagescache/";

    /**
     * 下载
     */
    public static final String SD_DOWN_PATH = SD_PATH + APP_NAME + "/down/";
    /**
     * 本地书法师考试试卷缓存图片
     */
    public final static String SD_IMAGES_EXAM_CACHE = SD_PATH + APP_NAME + "/.examimagescache/";
    public static final int TYPE_SORT_NAME = 0;
    public static final int TYPE_SORT_SIZE = 1;
    public static final int TYPE_SORT_DATE = 2;

    public static final int SIZETYPE_B = 1;// 获取文件大小单位为B的double值
    public static final int SIZETYPE_KB = 2;// 获取文件大小单位为KB的double值
    public static final int SIZETYPE_MB = 3;// 获取文件大小单位为MB的double值
    public static final int SIZETYPE_GB = 4;// 获取文件大小单位为GB的double值

    private int sort_type;

    public FileUtils(int sort_type) {
        this.sort_type = sort_type;
    }

    public static boolean init() {
        try {
            if (!new File(SD_PATH).exists()) {
                return false;
            }
            new File(SD_APP_PATH).mkdirs();
            new File(SD_HOT_FIX_PATH).mkdirs();
            new File(SD_IMAGES_PATH).mkdirs();
            new File(SD_CLIP_IMAGES_PATH).mkdirs();
            new File(SD_COMP_IMAGES_PATH).mkdirs();
            new File(SD_MY_VIDEO).mkdirs();
            new File(SD_IMAGES_CACHE).mkdirs();
            new File(SD_MY_VOICE).mkdirs();
            new File(SD_VIDEO_CACHE).mkdirs();
            new File(SD_IMAGES_EXAM_CACHE).mkdirs();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private static DecimalFormat df = new DecimalFormat("#.##");

    public static String getFormatSize(float size) {
        String unit = "B";
        float len = size;
        if (len > 100) {
            len /= 1024f;
            unit = "K";
        }
        if (len > 800) {
            len /= 1024f;
            unit = "M";
        }
        return df.format(len) + unit;
    }

    public int compare(File file1, File file2) {
        if (file1.isDirectory() && !file2.isDirectory()) {
            return -1;
        } else if (!file1.isDirectory() && file2.isDirectory()) {
            return 1;
        } else {
            // 按文件大小排序
            if (sort_type == TYPE_SORT_SIZE) {
                if (file1.length() < file2.length()) {
                    return 1;
                } else if (file1.length() > file2.length()) {
                    return -1;
                } else {
                    return file1.getName().compareToIgnoreCase(file2.getName());
                }
                // 按文件修改时间排序
            } else if (sort_type == TYPE_SORT_DATE) {
                if (file1.lastModified() < file2.lastModified()) {
                    return 1;
                } else if (file1.lastModified() > file2.lastModified()) {
                    return -1;
                } else {
                    return file1.getName().compareToIgnoreCase(file2.getName());
                }
            } else {
                return file1.getName().compareToIgnoreCase(file2.getName());
            }
        }
    }

    public static void deleteFile(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return;
        }
        try {
            File file = new File(filePath);
            if (file.exists() && file.isFile()) {
                file.delete();
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    public static File saveBitmap(String filePath, String fileName, Bitmap mBitmap) {
        if (mBitmap == null) {
            return null;
        }
        File destDir = new File(filePath);
        if (!destDir.exists()) {
            destDir.mkdirs();
        }
        File f = new File(filePath + fileName);
        try {
            f.createNewFile();
        } catch (IOException e) {
        }
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        mBitmap.compress(Bitmap.CompressFormat.PNG, 90, fOut);
        try {
            fOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return f;
    }

    /**
     * 安装apk
     */
    public static void installApk(Context ctx, String apkFilePath) {
        File apkfile = new File(apkFilePath);
        if (!apkfile.exists()) {
            return;
        }
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        i.setDataAndType(Uri.parse("file://" + apkfile.toString()), "application/vnd.android.package-archive");
        ctx.startActivity(i);
    }

    public static void downloadApk(Context ctx, String title, String url, String fileName) {
        if (TextUtils.isEmpty(url)) {
            ToastUtil.show("下载地址错误");
            return;
        }
        ToastUtil.show("后台更新中");
        DownloadManager downloadManager = (DownloadManager) ctx.getSystemService(ctx.DOWNLOAD_SERVICE);
        // 开始下载
        Request request = new Request(Uri.parse(url));
        request.setAllowedNetworkTypes(Request.NETWORK_MOBILE | Request.NETWORK_WIFI);
        request.setAllowedOverRoaming(false);
        // 设置文件类型
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        String mimeString = mimeTypeMap.getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(url));
        request.setMimeType(mimeString);
        // 在通知栏中显示
        request.setVisibleInDownloadsUi(true);
        request.setNotificationVisibility(View.VISIBLE);
        request.setDestinationInExternalPublicDir(APP_NAME + "/app", fileName);
        request.setTitle("写字派最新版本：" + title);
        long id = downloadManager.enqueue(request);
        queryTask(ctx, id);
    }

    /**
     * 查询任务状态
     *
     * @param ctx
     * @param id
     */
    private static void queryTask(final Context ctx, final long id) {
        final DownloadManager manager = (DownloadManager) ctx.getSystemService(Context.DOWNLOAD_SERVICE);
        final DownloadManager.Query query = new DownloadManager.Query();
        new Thread(new Runnable() {
            @Override
            public void run() {
                String downId, title, address, size = "", sizeTotal = "";
                int st = -1;
                boolean running = true;
                query.setFilterById(id);
                while (running) {
                    Cursor cursor = manager.query(query);
                    if (cursor != null) {
                        while (cursor.moveToNext()) {
                            downId = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_ID));
                            title = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_TITLE));
                            address = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                            size = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                            sizeTotal = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                            st = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
                            LogUtils.e("downloadInfo:\ndownId:" + downId + ",\ntitle:" + title + ",\naddress:" + address
                                    + ",\nsize:" + size + ",\nsizeTotal:" + sizeTotal);
                            switch (st) {
                                case DownloadManager.STATUS_FAILED:
                                    Intent failIntent = new Intent(VersionUpdateDialog.DOWNLOAD_FAIL);
                                    ctx.sendBroadcast(failIntent);
                                    running = false;
                                    break;
                                case DownloadManager.STATUS_SUCCESSFUL:
                                    Intent successIntent = new Intent(VersionUpdateDialog.DOWNLOAD_COMPLETE);
                                    ctx.sendBroadcast(successIntent);
                                    running = false;
                                    break;
                                case DownloadManager.STATUS_RUNNING:
                                    Intent progressIntent = new Intent(VersionUpdateDialog.DOWNLOAD_SIZE);
                                    progressIntent.putExtra("size", size);
                                    progressIntent.putExtra("sizeTotal", sizeTotal);
                                    ctx.sendBroadcast(progressIntent);
                                    break;
                            }
                        }
                        cursor.close();
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    } else {
                        Intent failIntent = new Intent(VersionUpdateDialog.DOWNLOAD_FAIL);
                        ctx.sendBroadcast(failIntent);
                        running = false;
                    }
                }

            }
        }).start();

    }

    /**
     * 判断某个文件是否存在
     *
     * @param filename
     * @return
     */
    public static boolean fileIsExists(String path, String filename) {
        try {
            File f = new File(path + "/" + filename);
            if (!f.exists()) {
                return false;
            }

        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * 判断某个文件是否存在
     *
     * @param path
     * @return
     */
    public static boolean fileIsExists(String path) {
        try {
            File f = new File(path);
            if (!f.exists()) {
                return false;
            }

        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /*
     * 判断某个目录是否存在
     */
    public static boolean isDirExist(String dir) {// 判断某个目录是否存在
        File file = new File(dir + File.separator);
        if (!file.exists()) {
            return false;
        }
        return true;
    }

    /**
     * 删除空目录
     *
     * @param dir
     */
    public static void deleteDir(String dir) {
        File file = new File(dir + File.separator);
        if (file.exists()) {
            file.delete();
        }
    }

    /**
     * 删除课程文件
     */
    public static void deleteCourseFile(String path, String name) {

        File f = new File(path + File.separator + name);
        if (f.exists()) {
            f.delete();
        }
    }

    public static String listFiles(String dir) {

        File f = new File(dir);
        // 列出所有文件
        File[] files = f.listFiles();
        if (files != null && files.length > 0) {
            return files[0].getName();
        }
        return null;

    }

    /**
     * 检测Sdcard是否存在
     *
     * @return
     */
    public static boolean isExitsSdcard() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
            return true;
        else
            return false;
    }

    /**
     * 保存图片更新至相册
     *
     * @param context
     */
    public static void savePhotoToAlbums(Context context, File file) {
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(file);
        intent.setData(uri);
        context.sendBroadcast(intent);
    }

    /**
     * 根据Uri获取文件路径
     *
     * @param context
     * @param uri
     * @return
     */
    public static String getRealFilePath(final Context context, final Uri uri) {
        if (null == uri) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null)
            data = uri.getPath();
        else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;
    }

    /**
     * 判断SD卡是否挂载
     *
     * @return
     */
    public static boolean isSDCardMounted() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    /**
     * 获取文件指定文件的指定单位的大小
     *
     * @param filePath 文件路径
     * @param sizeType 获取大小的类型1为B、2为KB、3为MB、4为GB
     * @return double值的大小
     */
    public static double getFileOrFilesSize(String filePath, int sizeType) {
        File file = new File(filePath);
        long blockSize = 0;
        try {
            if (file.isDirectory()) {
                blockSize = getFileSizes(file);
            } else {
                blockSize = getFileSize(file);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return FormetFileSize(blockSize, sizeType);
    }

    /**
     * 获取指定文件大小
     *
     * @param file
     * @return
     * @throws Exception
     */
    private static long getFileSize(File file) throws Exception {
        long size = 0;
        if (file.exists()) {
            FileInputStream fis = null;
            fis = new FileInputStream(file);
            size = fis.available();
        } else {
            file.createNewFile();
        }
        return size;
    }

    public static long getFileSize(String path) {
        long size = 0;
        if (fileIsExists(path)) {
            try {
                FileInputStream fis = new FileInputStream(new File(path));
                size = fis.available();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return size;
    }

    /**
     * 获取指定文件夹
     *
     * @param f
     * @return
     * @throws Exception
     */
    private static long getFileSizes(File f) throws Exception {
        long size = 0;
        File flist[] = f.listFiles();
        for (int i = 0; i < flist.length; i++) {
            if (flist[i].isDirectory()) {
                size = size + getFileSizes(flist[i]);
            } else {
                size = size + getFileSize(flist[i]);
            }
        }
        return size;
    }

    /**
     * 转换文件大小,指定转换的类型
     *
     * @param fileS
     * @param sizeType
     * @return
     */
    private static double FormetFileSize(long fileS, int sizeType) {
        DecimalFormat df = new DecimalFormat("#.00");
        double fileSizeLong = 0;
        switch (sizeType) {
            case SIZETYPE_B:
                fileSizeLong = Double.valueOf(df.format((double) fileS));
                break;
            case SIZETYPE_KB:
                fileSizeLong = Double.valueOf(df.format((double) fileS / 1024));
                break;
            case SIZETYPE_MB:
                fileSizeLong = Double.valueOf(df.format((double) fileS / 1048576));
                break;
            case SIZETYPE_GB:
                fileSizeLong = Double.valueOf(df
                        .format((double) fileS / 1073741824));
                break;
            default:
                break;
        }
        return fileSizeLong;
    }

    /**
     * 读取json文本
     *
     * @param path sd卡文本
     * @return
     */
    public static String getJson(String path) {
        String result = "";
        try {
            FileInputStream f = new FileInputStream(path);
            BufferedReader bis = new BufferedReader(new InputStreamReader(f));
            String line = "";
            while ((line = bis.readLine()) != null) {
                result += line;
            }
            LogUtils.e("json=" + result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 读取json文本
     */
    public static String getJson(Context context, String fileName) {
        String result = "";
        try {
            InputStream is = context.getAssets().open(fileName);
            BufferedReader bis = new BufferedReader(new InputStreamReader(is));
            String line = "";
            while ((line = bis.readLine()) != null) {
                result += line;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}