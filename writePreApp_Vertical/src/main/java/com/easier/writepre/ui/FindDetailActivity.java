package com.easier.writepre.ui;

import java.util.List;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.widget.ImageView;
import com.android.volley.Response.Listener;
import com.easier.writepre.R;
import com.easier.writepre.entity.CategoryInfo;
import com.easier.writepre.entity.ChildCategoryList;
import com.easier.writepre.http.Constant;
import com.easier.writepre.http.RequestManager;
import com.easier.writepre.param.AddFavParams;
import com.easier.writepre.param.DelFavFoundParams;
import com.easier.writepre.param.FavOrNotParams;
import com.easier.writepre.response.BaseResponse;
import com.easier.writepre.response.FavoriteOrNotResponse;
import com.easier.writepre.response.FavoriteOrNotResponse.FavoriteOrNotBody;
import com.easier.writepre.utils.FileUtils;
import com.easier.writepre.utils.SPUtils;
import com.easier.writepre.utils.StringUtil;
import com.easier.writepre.utils.ToastUtil;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

/**
 * 知识库详情
 * 
 * @author chenhong
 * 
 */
public class FindDetailActivity extends BaseActivity implements
		OnClickListener, Listener<BaseResponse> {

	private WebView wv_detail;
	private ImageView iv_nav_left;
	private ImageView iv_nav_right;

	private ImageView iv_fav;
	private ImageView iv_faved;

	private List<ChildCategoryList> mCategoryList;
	private int position;
	private List<CategoryInfo> mChildCategoryList;

	private String ref_repo_id;

	private String _id;// 知识库目录id

	private int flag;// 0:添加收藏 1：取消收藏

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_find_detail);
		init();
	}

	@SuppressWarnings("unchecked")
	private void init() {

		position = getIntent().getIntExtra("from_parent_index", -1);
		if (position >= 0) {
			mCategoryList = (List<ChildCategoryList>) getIntent()
					.getSerializableExtra("from_parent");
		} else {
			position = getIntent().getIntExtra("from_child_index", -1);
			mChildCategoryList = (List<CategoryInfo>) getIntent()
					.getSerializableExtra("from_child");
		}

		wv_detail = (WebView) findViewById(R.id.wv_detail);
		wv_detail.getSettings().setSupportZoom(true);
		wv_detail.getSettings().setBuiltInZoomControls(true);
		wv_detail.getSettings().setUseWideViewPort(true);
//		wv_detail.getSettings().setLayoutAlgorithm(
//				LayoutAlgorithm.SINGLE_COLUMN);
		wv_detail.getSettings().setLoadWithOverviewMode(true);
		iv_nav_left = (ImageView) findViewById(R.id.iv_nav_left);
		iv_nav_right = (ImageView) findViewById(R.id.iv_nav_right);

		findViewById(R.id.iv_back).setOnClickListener(this);
		iv_fav = (ImageView) findViewById(R.id.iv_fav);
		iv_fav.setOnClickListener(this);
		iv_faved = (ImageView) findViewById(R.id.iv_faved);
		iv_faved.setOnClickListener(this);
		findViewById(R.id.iv_share).setOnClickListener(this);
		iv_nav_left.setOnClickListener(this);
		iv_nav_right.setOnClickListener(this);

		if (mCategoryList != null && mCategoryList.size() > 0) {
			if (!TextUtils.isEmpty(mCategoryList.get(position).getFile_url())) {
				dlgLoad.loading();
				wv_detail.loadUrl(StringUtil.getImgeUrl(
						mCategoryList.get(position).getFile_url()));
				wv_detail.setVisibility(View.VISIBLE);
				ref_repo_id = mCategoryList.get(position).getRef_repo_id();
				_id = mCategoryList.get(position).get_id();
			} else {
				wv_detail.setVisibility(View.GONE);
			}
		} else {
			if (!TextUtils.isEmpty(mChildCategoryList.get(position)
					.getFile_url())) {
				String[] tempArr = mChildCategoryList.get(position)
						.getFile_url().split("/");
				final String temp = tempArr[tempArr.length - 1];
				if (FileUtils.fileIsExists(FileUtils.SD_DOWN_PATH, temp)) {
					wv_detail.loadUrl("file:///mnt/sdcard/writepre/down/"
							+ temp);
				} else {
					dlgLoad.loading();
					wv_detail.loadUrl(StringUtil.getImgeUrl(mChildCategoryList.get(position).getFile_url()));
				}
				wv_detail.setVisibility(View.VISIBLE);
				ref_repo_id = mChildCategoryList.get(position).getRef_repo_id();
				_id = mChildCategoryList.get(position).get_id();
			} else {
				wv_detail.setVisibility(View.GONE);
			}
		}

		wv_detail.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageFinished(WebView view, String url) {
				dlgLoad.dismissDialog();
			}
		});

		if (!(boolean) SPUtils.instance().get(SPUtils.IS_LOGIN, false)) {
			iv_fav.setVisibility(View.VISIBLE);
			iv_faved.setVisibility(View.INVISIBLE);
		} else {
			// 判断是否已经收藏
			dlgLoad.loading();
			RequestManager.request(this,new FavOrNotParams(_id),
					FavoriteOrNotResponse.class, this, Constant.URL);
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_back:
			finish();
			break;
		case R.id.iv_fav:// 收藏
			flag = 0;
			dlgLoad.loading();
			RequestManager.request(this,new AddFavParams(_id), BaseResponse.class,
					this, Constant.URL);
			break;
		case R.id.iv_faved:// 取消收藏
			flag = 1;
			dlgLoad.loading();
			RequestManager.request(this,new DelFavFoundParams(_id),
					BaseResponse.class, this, Constant.URL);
			break;
		case R.id.iv_share:// 分享
			if (mCategoryList != null && mCategoryList.size() > 0) {
				if (!TextUtils.isEmpty(mCategoryList.get(position)
						.getFile_url())) {
					sharedContent(
							null,
							StringUtil.getImgeUrl( mCategoryList.get(position).getFile_url()),
									StringUtil.getImgeUrl( mCategoryList.get(position).getFile_url()),
							null, wv_detail);
				}
			} else {
				if (!TextUtils.isEmpty(mChildCategoryList.get(position)
						.getFile_url())) {
					sharedContent(
							null,
							StringUtil.getImgeUrl( mChildCategoryList.get(position)
											.getFile_url()),
							StringUtil.getImgeUrl( mChildCategoryList.get(position)
											.getFile_url()), null, wv_detail);
				}
			}
			break;
		case R.id.iv_nav_left:// 上一个
			position--;
			if (position < 0) {
				ToastUtil.show("已经是第一页");
				position++;
			} else {
				if (mCategoryList != null && mCategoryList.size() > 0) {
					if (!TextUtils.isEmpty(mCategoryList.get(position)
							.getFile_url())) {
						dlgLoad.loading();
						wv_detail.loadUrl(StringUtil.getImgeUrl(mCategoryList.get(position).getFile_url()));
						wv_detail.setVisibility(View.VISIBLE);
						ref_repo_id = mCategoryList.get(position)
								.getRef_repo_id();
						_id = mCategoryList.get(position).get_id();
						if (!(boolean) SPUtils.instance().get(SPUtils.IS_LOGIN,
								false)) {
							iv_fav.setVisibility(View.VISIBLE);
							iv_faved.setVisibility(View.INVISIBLE);
						} else {
							// 判断是否已经收藏
							dlgLoad.loading();
							RequestManager.request(this,new FavOrNotParams(_id),
									FavoriteOrNotResponse.class, this,
									Constant.URL);
						}
					} else {
						ToastUtil.show("已经是第一页");
						position++;
					}
				} else {
					if (!TextUtils.isEmpty(mChildCategoryList.get(position)
							.getFile_url())) {
						String[] tempArr = mChildCategoryList.get(position)
								.getFile_url().split("/");
						final String temp = tempArr[tempArr.length - 1];
						if (FileUtils
								.fileIsExists(FileUtils.SD_DOWN_PATH, temp)) {
							wv_detail
									.loadUrl("file:///mnt/sdcard/writepre/down/"
											+ temp);
						} else {
							dlgLoad.loading();
							wv_detail.loadUrl(StringUtil.getImgeUrl(mChildCategoryList.get(position)
											.getFile_url()));
						}
						wv_detail.setVisibility(View.VISIBLE);
						ref_repo_id = mChildCategoryList.get(position)
								.getRef_repo_id();
						_id = mChildCategoryList.get(position).get_id();
						if (!(boolean) SPUtils.instance().get(SPUtils.IS_LOGIN,
								false)) {
							iv_fav.setVisibility(View.VISIBLE);
							iv_faved.setVisibility(View.INVISIBLE);
						} else {
							// 判断是否已经收藏
							dlgLoad.loading();
							RequestManager.request(this,new FavOrNotParams(_id),
									FavoriteOrNotResponse.class, this,
									Constant.URL);
						}
					} else {
						wv_detail.setVisibility(View.GONE);
					}
				}
			}
			break;
		case R.id.iv_nav_right:// 下一个
			position++;
			if (mCategoryList != null && mCategoryList.size() > 0) {
				if (position >= mCategoryList.size()) {
					ToastUtil.show("已经是最后一页");
					position--;
				} else {
					if (!TextUtils.isEmpty(mCategoryList.get(position)
							.getFile_url())) {
						dlgLoad.loading();
						wv_detail.loadUrl(StringUtil.getImgeUrl(mCategoryList.get(position).getFile_url()));
						wv_detail.setVisibility(View.VISIBLE);
						ref_repo_id = mCategoryList.get(position)
								.getRef_repo_id();
						_id = mCategoryList.get(position).get_id();
						if (!(boolean) SPUtils.instance().get(SPUtils.IS_LOGIN,
								false)) {
							iv_fav.setVisibility(View.VISIBLE);
							iv_faved.setVisibility(View.INVISIBLE);
						} else {
							// 判断是否已经收藏
							dlgLoad.loading();
							RequestManager.request(this,new FavOrNotParams(_id),
									FavoriteOrNotResponse.class, this,
									Constant.URL);
						}
					} else {
						ToastUtil.show("已经是最后一页");
						position--;
					}
				}
			} else {
				if (position >= mChildCategoryList.size()) {
					ToastUtil.show("已经是最后一页");
					position--;
				} else {
					if (!TextUtils.isEmpty(mChildCategoryList.get(position)
							.getFile_url())) {
						String[] tempArr = mChildCategoryList.get(position)
								.getFile_url().split("/");
						final String temp = tempArr[tempArr.length - 1];
						if (FileUtils
								.fileIsExists(FileUtils.SD_DOWN_PATH, temp)) {
							wv_detail
									.loadUrl("file:///mnt/sdcard/writepre/down/"
											+ temp);
						} else {
							dlgLoad.loading();
							wv_detail.loadUrl(StringUtil.getImgeUrl( mChildCategoryList.get(position)
											.getFile_url()));
						}
						wv_detail.setVisibility(View.VISIBLE);
						ref_repo_id = mChildCategoryList.get(position)
								.getRef_repo_id();
						_id = mChildCategoryList.get(position).get_id();
						if (!(boolean) SPUtils.instance().get(SPUtils.IS_LOGIN,
								false)) {
							iv_fav.setVisibility(View.VISIBLE);
							iv_faved.setVisibility(View.INVISIBLE);
						} else {
							// 判断是否已经收藏
							dlgLoad.loading();
							RequestManager.request(this,new FavOrNotParams(_id),
									FavoriteOrNotResponse.class, this,
									Constant.URL);
						}
					} else {
						wv_detail.setVisibility(View.GONE);
					}
				}
			}
			break;

		}
	}

	@Override
	public void onBackPressed() {
		finish();
	}

	@Override
	public void onResponse(BaseResponse response) {

		dlgLoad.dismissDialog();

		if ("0".equals(response.getResCode())) {

			if (response instanceof FavoriteOrNotResponse) {

				FavoriteOrNotResponse mFavoriteOrNotResponse = (FavoriteOrNotResponse) response;
				FavoriteOrNotBody mFavoriteOrNotBody = mFavoriteOrNotResponse
						.getRepBody();
				if (mFavoriteOrNotBody != null) {
					if ("0".equals(mFavoriteOrNotBody.getIs_fav())) {// 未收藏
						iv_fav.setVisibility(View.VISIBLE);
						iv_faved.setVisibility(View.INVISIBLE);
					} else {// 已收藏
						iv_fav.setVisibility(View.INVISIBLE);
						iv_faved.setVisibility(View.VISIBLE);
					}

				}

			} else {
				if (flag == 0) {
					ToastUtil.show("添加收藏成功");
					iv_fav.setVisibility(View.INVISIBLE);
					iv_faved.setVisibility(View.VISIBLE);
				} else {
					ToastUtil.show("取消收藏成功");
					iv_fav.setVisibility(View.VISIBLE);
					iv_faved.setVisibility(View.INVISIBLE);
				}
			}

		} else {
			ToastUtil.show(response.getResMsg());
		}
	}
}
