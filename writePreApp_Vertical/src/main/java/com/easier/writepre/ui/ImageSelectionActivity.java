package com.easier.writepre.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.easier.writepre.R;
import com.easier.writepre.entity.ImageEntity;
import com.easier.writepre.utils.BitmapHelp;
import com.easier.writepre.utils.DiskCache;
import com.easier.writepre.utils.DiskCache.ImageCallback;
import com.easier.writepre.utils.FileUtils;
import com.easier.writepre.utils.LogUtils;
import com.lidroid.xutils.bitmap.PauseOnScrollListener;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ImageSelectionActivity extends BaseActivity {

	public static ArrayList<String> data1 = new ArrayList<String>();
	public static final int REQUEST_CODE_ADD = 101;

	private static final int UPDATE_REFRESH = 0;
	private static final int UPDATE_REFRESH_ALL = 1;
	private static final int UPDATE_MENU = 2;
	private static final int SHOW_DIALOG = -1;
	private static final int HIDE_DIALOG = -2;

	private LayoutInflater mInflater;
	private GridView mGridView;
	private ImageAdapter mImageAdapter;
	private FileAdapter mMenuAdapter;
	private View mMenuContainer;
	private ListView mListMenu;
	private TextView mTvFooter;
	private TextView mTvPreView;
	private LinkedHashMap<String, ImageEntity> mFileCache = new LinkedHashMap<String, ImageEntity>();
	private LinkedHashMap<String, ImageEntity> mDirCache = new LinkedHashMap<String, ImageEntity>();
	private ArrayList<ImageEntity> mFiles = new ArrayList<ImageEntity>();
	private int mMaxSize = 6;
	private ArrayList<String> mSelected = new ArrayList<String>();
	private boolean mDestroy = false;
	private boolean mVisible = false;

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (isFinished() || !mVisible) {
				return;
			}

			// 刷新
			int what = msg.what;
			switch (what) {
			case SHOW_DIALOG:
				dlgLoad.loading();
				break;
			case HIDE_DIALOG:
				dlgLoad.dismissDialog();
				break;
			case UPDATE_REFRESH:
				dlgLoad.dismissDialog();
				if (mImageAdapter != null) {
					mImageAdapter.setData(mFiles);
					this.post(new Runnable() {

						@Override
						public void run() {
							mGridView.setSelection(0);
						}
					});
				}
				break;
			case UPDATE_REFRESH_ALL:
				dlgLoad.dismissDialog();
				if (mImageAdapter != null) {
					mImageAdapter.setData(mFiles);
					this.post(new Runnable() {

						@Override
						public void run() {
							mGridView.setSelection(0);
						}
					});
				}
				break;
			case UPDATE_MENU:
				dlgLoad.dismissDialog();
				if (mMenuAdapter != null) {
					mMenuAdapter.setData(mDirCache);
				}
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// LogUtils.v("fc", "ImageSelectionActivity.onCreate");
		mInflater = this.getLayoutInflater();
		setContentView(R.layout.activity_image_selection);
		mMaxSize = this.getIntent().getIntExtra("maxSize", 6);
		if (getIntent().getStringArrayListExtra("data") != null) {
			mSelected.addAll(this.getIntent().getStringArrayListExtra("data"));
		}
		initView();
		onScanImage();
	}

	private void initView() {
		setTopTitle("图片");
		mGridView = (GridView) this.findViewById(R.id.gridview);
		mMenuContainer = this.findViewById(R.id.view_menu_content);
		mListMenu = (ListView) this.findViewById(R.id.list_menu);
		mTvFooter = (TextView) this.findViewById(R.id.tv_footer_name);
		mTvPreView = (TextView) this.findViewById(R.id.tv_preview);

		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		mImageAdapter = new ImageAdapter(dm.widthPixels);
		mGridView.setAdapter(mImageAdapter);
		mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				int viewType = parent.getAdapter().getItemViewType(0);
				int type = ImageDetailActivity.TYPE_VIEW;
				if (0 == viewType) {
					type = ImageDetailActivity.TYPE_CAMERA;
				}
				toImageDetail(type, position);
			}
		});

		mTvPreView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				toImageDetail(ImageDetailActivity.TYPE_PREVIEW, 0);
			}
		});

		mMenuAdapter = new FileAdapter();
		mListMenu.setAdapter(mMenuAdapter);
		mListMenu.setItemChecked(0, true);
		mListMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				mListMenu.setItemChecked(position, true);
				dismissMenu();
				ImageEntity entity = (ImageEntity) parent.getAdapter().getItem(position);
				mTvFooter.setText(String.valueOf(entity.getParentName()));
				onLoadDirImage(entity.getParentPath());
			}
		});

		mTvFooter.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mMenuContainer.getVisibility() == View.GONE) {
					showMenu();
				} else {
					dismissMenu();
				}
			}
		});

		mMenuContainer.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dismissMenu();
			}
		});
		if (!mSelected.isEmpty()) {
			updateState();
		}

		mGridView.setOnScrollListener(new PauseOnScrollListener(BitmapHelp.getBitmapUtils(), false, true));
	}

	@Override
	public void onTopRightTxtClick(View view) {
		Intent intent = new Intent();
		intent.putStringArrayListExtra("data1", mSelected);
		setResult(RESULT_OK, intent);
		ImageSelectionActivity.this.finish();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
	}

	@Override
	public void onBackPressed() {
		onBack();
	}

	private void onBack() {
		this.finish();
	}

	private void updateState() {
		int count = mSelected.size();
		if (count > 0) {
			setTopRightTxt("完成（" + count + "/" + mMaxSize + "）");
			mTvPreView.setEnabled(true);
			mTvPreView.setText("预览（" + count + "）");
		} else {
			setTopRightTxt(null);
			mTvPreView.setEnabled(false);
			mTvPreView.setText("预览");
		}
		if (count > mMaxSize) {
			Toast.makeText(ImageSelectionActivity.this, "你最多只能选择" + mMaxSize + "张照片", Toast.LENGTH_SHORT).show();
			return;
		}
	}

	private void toImageDetail(int type, int position) {
		Intent intent = null;
		data1.clear();
		ArrayList<ImageEntity> dataFile = new ArrayList<ImageEntity>();
		int requestCode = ImageDetailActivity.REQUEST_CODE_VIEW;
		intent = new Intent(ImageSelectionActivity.this, ImageDetailActivity.class);
		switch (type) {
		case ImageDetailActivity.TYPE_VIEW:
			mHandler.sendEmptyMessage(SHOW_DIALOG);
			dataFile = new ArrayList<ImageEntity>(mFiles);
			for (int i = 0; i < dataFile.size(); i++) {
				data1.add(dataFile.get(i).getPath());
			}
			mHandler.sendEmptyMessage(HIDE_DIALOG);
			break;
		case ImageDetailActivity.TYPE_PREVIEW:
			requestCode = ImageDetailActivity.REQUEST_CODE_PREVIEW;
			ImageEntity entity = null;
			for (String key : mSelected) {
				entity = mFileCache.get(key);
				if (entity != null) {
					dataFile.add(entity);
				}
			}
			data1.addAll(mSelected);
			break;
		case ImageDetailActivity.TYPE_CAMERA:
			if (0 == position) {
				requestCode = ImageDetailActivity.REQUEST_CODE_CAMERA;
			} else {
				type = ImageDetailActivity.TYPE_VIEW;
				dataFile = new ArrayList<ImageEntity>(mFiles);
				dataFile.remove(0);
				position = position - 1;
			}
			break;
		}
		intent.putStringArrayListExtra("selected_data", mSelected);
		intent.putExtra("type", type);
		intent.putExtra("MAX_SIZE", mMaxSize);
		intent.putExtra("position", position);
		startActivityForResult(intent, requestCode);
	}

	private void showMenu() {
		if (mMenuContainer.getVisibility() == View.GONE) {
			final Animation in = AnimationUtils.loadAnimation(this, R.anim.in_bottomtop);
			mListMenu.startAnimation(in);
			mMenuContainer.setVisibility(View.VISIBLE);
		}
	}

	private void dismissMenu() {
		if (mMenuContainer.getVisibility() == View.VISIBLE) {
			final Animation out = AnimationUtils.loadAnimation(this, R.anim.out_topbottom);
			mListMenu.startAnimation(out);
			out.setAnimationListener(new AnimationListener() {

				@Override
				public void onAnimationStart(Animation animation) {
				}

				@Override
				public void onAnimationRepeat(Animation animation) {

				}

				@Override
				public void onAnimationEnd(Animation animation) {
					mMenuContainer.setVisibility(View.GONE);
				}
			});
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onRestart() {
		super.onRestart();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		dismissMenu();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		LogUtils.e(
				"ImageSelectionActivity.onActivityResult.requestCode=" + requestCode + ", resultCode=" + resultCode);
		if (resultCode == RESULT_OK) {
			if (requestCode == ImageDetailActivity.REQUEST_CODE_VIEW || requestCode == ImageDetailActivity.TYPE_VIEW) {
				if (data != null) {
					ArrayList<String> results = data.getStringArrayListExtra("data1");
					if (results != null) {
						mSelected.clear();
						mSelected.addAll(results);
						updateState();
						mHandler.sendEmptyMessage(UPDATE_REFRESH);
					}
				}
			} else if (requestCode == 99) {
				if (data != null) {
					Intent i = new Intent();
					i.putExtra("path", data.getStringExtra("path"));
					setResult(RESULT_OK, i);
					finish();
				}
			}
		}
		if (resultCode == RESULT_CANCELED) {

		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// LogUtils.e("fc", "ImageSelectionActivity.onDestroy");
		mDestroy = true;
		if (mFileCache != null) {
			mFileCache.clear();
		}
		if (mFiles != null) {
			mFiles.clear();
		}
		if (mDirCache != null) {
			mDirCache.clear();
		}
		if (data1 != null) {
			data1.clear();
		}
		if (mSelected != null) {
			mSelected.clear();
		}
	}

	private void onLoadDirImage(final String parent) {
		mHandler.sendEmptyMessage(SHOW_DIALOG);
		new Thread() {
			public void run() {
				if ("root/所有图片".equals(parent)) {
					mFiles = toArrayList(mFileCache);
				} else {
					mFiles = new ArrayList<ImageEntity>();
					File dir = new File(parent);
					File[] files = dir.listFiles();
					if (files != null && files.length > 0) {
						String key = null;
						ImageEntity entity = null;
						Arrays.sort(files, new FileUtils(FileUtils.TYPE_SORT_DATE));
						for (File file : files) {
							key = file.getPath();
							entity = mFileCache.get(key);
							if (entity != null) {
								mFiles.add(entity);
							}
						}
					}
				}
				mHandler.sendEmptyMessage(UPDATE_REFRESH_ALL);
			}
		}.start();

	}

	private void onScanImage() {
		mHandler.sendEmptyMessage(SHOW_DIALOG);
		new Thread() {
			private int count;

			private String getParent(String path) {
				int length = path.length(), firstInPath = 0;
				if (File.separatorChar == '\\' && length > 2 && path.charAt(1) == ':') {
					firstInPath = 2;
				}
				int index = path.lastIndexOf(File.separatorChar);
				if (index == -1 && firstInPath > 0) {
					index = 2;
				}
				if (index == -1 || path.charAt(length - 1) == File.separatorChar) {
					return null;
				}
				if (path.indexOf(File.separatorChar) == index && path.charAt(firstInPath) == File.separatorChar) {
					return path.substring(0, index + 1);
				}
				return path.substring(0, index);
			}

			public void run() {
				onScan();
				mFiles = toArrayList(mFileCache);
				mHandler.sendEmptyMessage(UPDATE_REFRESH_ALL);
				mHandler.sendEmptyMessage(UPDATE_MENU);
			}

			private void onScan() {
				mFileCache.clear();
				mDirCache.clear();

				String[] projection = { MediaStore.Images.Media._ID, MediaStore.Images.Media.DISPLAY_NAME,
						MediaStore.Images.Media.DATA, MediaStore.Images.Media.DATE_MODIFIED };
				Cursor cursor = ImageSelectionActivity.this.getContentResolver().query(
						MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, MediaStore.Images.Media.SIZE
								+ " > 0 and " + MediaStore.Images.Media.MIME_TYPE + " like 'image/%'",
						null, MediaStore.Images.Media.DATE_MODIFIED + " DESC");

				if (cursor != null) {
					while (cursor.moveToNext()) {
						if (isFinished()) {
							break;
						}
						String name = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
						String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
						long lastModified = cursor
								.getLong(cursor.getColumnIndex(MediaStore.Images.Media.DATE_MODIFIED));
						String parent = getParent(path);
						mFileCache.put(path, new ImageEntity(name, path, parent, lastModified));
						if (count == 0) {
							mDirCache.put("root", new ImageEntity(true, path, "root/所有图片", 0));
						}
						if (parent != null) {
							ImageEntity dir = mDirCache.get(parent);
							if (dir == null) {
								dir = new ImageEntity(true, path, parent, 0);
							}
							dir.addCount();
							mDirCache.put(parent, dir);
						}

						count++;
					}
					cursor.close();
				}
			}
		}.start();
	}

	protected boolean isFinished() {
		return isFinishing() || mDestroy;
	}

	@Override
	public void onAttachedToWindow() {
		super.onAttachedToWindow();
		mVisible = true;
	}

	@Override
	public void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		mVisible = false;
	}

	private ArrayList<ImageEntity> toArrayList(Map<String, ImageEntity> maps) {
		ArrayList<ImageEntity> entities = new ArrayList<ImageEntity>();
		if (maps != null && !maps.isEmpty()) {
			if (maps != null) {
				Iterator<Entry<String, ImageEntity>> it = maps.entrySet().iterator();
				while (it.hasNext()) {
					Entry<String, ImageEntity> entry = it.next();
					// String key = entry.getKey();
					ImageEntity entity = entry.getValue();
					entities.add(entity);
				}
			}
		}
		return entities;
	}

	class FileAdapter extends BaseAdapter {
		private ArrayList<ImageEntity> mData = new ArrayList<ImageEntity>();

		public void setData(HashMap<String, ImageEntity> mImageDir) {
			mData.clear();
			if (mImageDir != null) {
				Iterator<Entry<String, ImageEntity>> it = mImageDir.entrySet().iterator();
				while (it.hasNext()) {
					Entry<String, ImageEntity> entry = it.next();
					String key = entry.getKey();
					ImageEntity entity = entry.getValue();
					if ("root".equals(key)) {
						mData.add(0, entity);
					} else {
						mData.add(entity);
					}
				}
			}
			this.notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return mData.size();
		}

		@Override
		public Object getItem(int position) {
			return mData.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.view_menu_item, null);
				holder = new ViewHolder();
				holder.ivConver = (ImageView) convertView.findViewById(R.id.image_cover);
				holder.tvName = (TextView) convertView.findViewById(R.id.tv_dir_name);
				holder.tvCount = (TextView) convertView.findViewById(R.id.tv_dir_count);
				holder.ivRight = (ImageView) convertView.findViewById(R.id.image_right);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			final ImageEntity entity = mData.get(position);
			String path = entity.getPath();
			holder.ivConver.setTag(path);
			Bitmap bitmap = DiskCache.getInstance().loadImage(path, 200, 200, new DiskCache.ImageCallback() {

				@Override
				public void imageLoaded(Bitmap bitmap, String path) {
					if (isFinished() || !mVisible) {
						return;
					}
					View imageView = mListMenu.findViewWithTag(path);
					if (imageView != null && imageView.getVisibility() == View.VISIBLE && bitmap != null) {
						((ImageView) imageView).setImageBitmap(bitmap);
					}
				}
			});
			if (bitmap != null) {
				holder.ivConver.setImageBitmap(bitmap);
			} else {
				holder.ivConver.setImageResource(R.drawable.empty_photo);
			}

			holder.tvName.setText(String.valueOf(entity.getParentName()));
			long count = entity.getCount();
			if (count > 0) {
				holder.tvCount.setText(count + "张");
			} else {
				holder.tvCount.setText("");
			}

			int checkedPosition = mListMenu.getCheckedItemPosition();
			holder.ivRight.setVisibility(position == checkedPosition ? View.VISIBLE : View.INVISIBLE);
			return convertView;
		}

		class ViewHolder {
			ImageView ivConver;
			TextView tvName;
			TextView tvCount;
			ImageView ivRight;
		}
	}

	class ImageAdapter extends BaseAdapter {

		private int mWidth = 0;
		private ArrayList<ImageEntity> mData = new ArrayList<ImageEntity>();

		public ImageAdapter(int width) {
			super();
			this.mWidth = (width - 4) / 3;
		}

		public void setData(final ArrayList<ImageEntity> mImageFiles) {
			if (mImageFiles == null) {
				mData.clear();
			} else {
				this.mData = mImageFiles;
			}
			this.notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return mData.size();
		}

		@Override
		public Object getItem(int position) {
			return mData.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public int getItemViewType(int position) {
			if (position < getCount()) {
				ImageEntity entity = mData.get(position);
				if ("camera".equals(entity.getName())) {
					return 0;
				}
			}
			return 1;
		}

		@Override
		public int getViewTypeCount() {
			return 2;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			int type = getItemViewType(position);
			ViewHolder holderImage = null;
			if (type == 0) {
				convertView = mInflater.inflate(R.layout.view_camera_item, null);
				convertView.setLayoutParams(new GridView.LayoutParams(mWidth, mWidth));
				convertView.findViewById(R.id.view_camera).setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						toImageDetail(ImageDetailActivity.TYPE_CAMERA, 0);
					}
				});
			} else {
				if (convertView == null || convertView.getTag() == null) {
					convertView = mInflater.inflate(R.layout.view_image_item, null);
					convertView.setLayoutParams(new GridView.LayoutParams(mWidth, mWidth));
					holderImage = new ViewHolder();
					holderImage.imageView = (ImageView) convertView.findViewById(R.id.image);
					holderImage.check = convertView.findViewById(R.id.view_check);
					holderImage.checkBox = (CheckBox) convertView.findViewById(R.id.checkBox1);
					convertView.setTag(holderImage);
				} else {
					holderImage = (ViewHolder) convertView.getTag();
				}
			}
			if (holderImage != null) {
				final ImageView imageView = holderImage.imageView;
				final ImageEntity entity = mData.get(position);
				String path = entity.getPath();
				Bitmap bitmap = null;
				holderImage.imageView.setTag(path);
				bitmap = DiskCache.getInstance().loadImage(path, mWidth,
						mWidth, new ImageCallback() {

							@Override
							public void imageLoaded(Bitmap bitmap, String path) {
								if (isFinished() || !mVisible) {
									return;
								}
								View imageView = mGridView
										.findViewWithTag(path);
								if (imageView != null
										&& imageView.getVisibility() == View.VISIBLE
										&& bitmap != null) {
									((ImageView) imageView)
											.setImageBitmap(bitmap);
								}
							}
						});
				if (bitmap != null) {
					imageView.setImageBitmap(bitmap);
				} else {
					imageView.setImageResource(R.drawable.empty_photo);
				}
//				BitmapHelp.getBitmapUtils(ImageSelectionActivity.this).display(imageView, path,
//						new BitmapLoadCallBack<View>() {
//
//							@Override
//							public void onLoadCompleted(View arg0, String arg1, Bitmap arg2, BitmapDisplayConfig arg3,
//									BitmapLoadFrom arg4) {
//								Bitmap newBitmap = ThumbnailUtils.extractThumbnail(arg2, mWidth, mWidth);
//								((ImageView) arg0).setImageBitmap(newBitmap);
//								arg2.recycle();
//								System.gc();
//							}
//
//							@Override
//							public void onLoadFailed(View arg0, String arg1, Drawable arg2) {
//								imageView.setImageResource(R.drawable.empty_photo);
//							}
//						});
				final CheckBox checkBox = holderImage.checkBox;
				holderImage.check.setTag(entity);
				holderImage.check.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						int count = mSelected.size();
						ImageEntity entity = (ImageEntity) v.getTag();
						boolean flag = false;
						if (checkBox.isChecked()) {
							imageView.setAlpha(1f);
							checkBox.setChecked(false);
							entity.setChecked(0);
							mSelected.remove(entity.getPath());
						} else if (count < mMaxSize) {
							imageView.setAlpha(0.4f);
							checkBox.setChecked(true);
							entity.setChecked(1);
							mSelected.add(entity.getPath());
						} else {
							flag = true;
						}
						updateState();
					}
				});

				if (mSelected.contains(entity.getPath())) {
					imageView.setAlpha(0.4f);
					checkBox.setChecked(true);
				} else {
					imageView.setAlpha(1f);
					checkBox.setChecked(false);
				}
			}
			return convertView;
		}

		class ViewHolder {
			ImageView imageView;
			View check;
			CheckBox checkBox;
		}
	}
}
