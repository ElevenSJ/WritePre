package com.easier.writepre.download;

import java.io.File;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;

public interface DownloadInterface {

	public void onLoading(long total, long current, boolean isUploading);

	public void onFailure(String filePath,HttpException error, String msg);

	public void onSuccess(String filePath,ResponseInfo<File> responseInfo);

}
