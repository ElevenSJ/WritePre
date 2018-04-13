package com.easier.writepre.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.LruCache;

/**
 * 
 * @author DELL
 * 
 */
public class DiskCache {
	private LruCache<String, SoftReference<Bitmap>> imageCache = null;
	private LruCache<String, SoftReference<Bitmap>> orgImageCache = null;
	private ExecutorService executorService = Executors.newFixedThreadPool(15);

	private static class Holder {
		private static DiskCache instance = new DiskCache();
	}

	public static DiskCache getInstance() {
		return Holder.instance;
	}

	private DiskCache() {
		imageCache = new LruCache<String, SoftReference<Bitmap>>(250);
		orgImageCache = new LruCache<String, SoftReference<Bitmap>>(3);
	}

	public Bitmap loadOriginalImage(final String path,
			final ImageCallback imageCallback) {
		final Handler handler = new Handler() {
			public void handleMessage(Message message) {
				if (message.obj != null) {
					imageCallback.imageLoaded((Bitmap) message.obj, path);
				}
			}
		};

		final Handler mDelayHandler = new Handler() {
			public void handleMessage(Message message) {
				SoftReference<Bitmap> softReference = orgImageCache.get(path);
				if (softReference != null) {
					Bitmap bitmap = softReference.get();
					if (bitmap != null && !bitmap.isRecycled()) {
						handler.sendMessage(handler.obtainMessage(0, bitmap));
						return;
					}
				}
				
				executorService.submit(new Runnable() {
					@Override
					public void run() {
						Bitmap bitmap = loadImageFromLocal(path);
						if (bitmap != null) {
							Message message = handler.obtainMessage(0, bitmap);
							handler.sendMessage(message);
						}
					}
				});
			}
		};

		SoftReference<Bitmap> softReference = imageCache.get(path);
		if (softReference != null) {
			Bitmap bitmap = softReference.get();
			if (bitmap != null && !bitmap.isRecycled()) {
				mDelayHandler.sendEmptyMessageDelayed(0, 200);
				return bitmap;
			}
		}

		executorService.submit(new Runnable() {
			@Override
			public void run() {
				Bitmap bitmap = loadImageFromLocal(path, 200, 200);
				if (bitmap != null) {
					imageCache.put(path, new SoftReference<Bitmap>(bitmap));
					Message message = handler.obtainMessage(0, bitmap);
					handler.sendMessage(message);
				}
				mDelayHandler.sendEmptyMessageDelayed(0, 200);
			}
		});

		return null;
	}

	public Bitmap loadImage(final String path, final int width,
			final int height, final ImageCallback imageCallback) {
		SoftReference<Bitmap> softReference = imageCache.get(path);
		if (softReference != null) {
			Bitmap bitmap = softReference.get();
			if (bitmap != null && !bitmap.isRecycled()) {
				return bitmap;
			}
		}
		final Handler handler = new Handler() {
			public void handleMessage(Message message) {
				if (message.obj != null) {
					imageCallback.imageLoaded((Bitmap) message.obj, path);
				}
			}
		};
		executorService.submit(new Runnable() {
			@Override
			public void run() {
				Bitmap bitmap = loadImageFromLocal(path, width, height);
				if (bitmap != null) {
					imageCache.put(path, new SoftReference<Bitmap>(bitmap));
					Message message = handler.obtainMessage(0, bitmap);
					handler.sendMessage(message);
				}
			}
		});
		return null;
	}

	public interface ImageCallback {
		public void imageLoaded(Bitmap bitmap, String path);
	}

	public int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// 源图片的宽度
		int width = options.outWidth;
		int height = options.outHeight;
		int inSampleSize = 1;
		if (width > reqWidth && height > reqHeight) {
			// 计算出实际宽度和目标宽度的比率
			int widthRatio = Math.round((float) width / (float) reqWidth);
			int heightRatio = Math.round((float) width / (float) reqWidth);
			inSampleSize = Math.max(widthRatio, heightRatio);
		}
		return inSampleSize;
	}

	public Bitmap loadImageFromLocal(String path, final int width,
			final int height) {
		Bitmap bitmap = null;
		try {
			BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(path, opts);
			int sample = calculateInSampleSize(opts, width, height);
			opts.inJustDecodeBounds = false;
			opts.inPreferredConfig = Bitmap.Config.RGB_565;
			opts.inSampleSize = sample;
			opts.inPurgeable = true;
			opts.inInputShareable = true;
			InputStream is = new FileInputStream(new File(path));
			bitmap = BitmapFactory.decodeStream(is, null, opts);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		}

		return bitmap;
	}

	public Bitmap loadImageFromLocal(String path) {
		Bitmap bitmap = null;
		try {
			BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inPreferredConfig = Bitmap.Config.RGB_565;
			opts.inSampleSize = 1;
			opts.inJustDecodeBounds = false;
			opts.inPurgeable = true;
			opts.inInputShareable = true;
			InputStream is = new FileInputStream(new File(path));
			bitmap = BitmapFactory.decodeStream(is, null, opts);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		}

		return bitmap;
	}

	public void clear() {
		if (imageCache != null) {
			imageCache.evictAll();
		}
	}

	public void removeFromContainer(String key) {
		SoftReference<Bitmap> softReference = imageCache.get(key);
		if (softReference != null) {
			Bitmap bitmap = softReference.get();
			if (bitmap != null && !bitmap.isRecycled()) {
				bitmap.recycle();
				softReference.clear();
			}
		}
	}
}