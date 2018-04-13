package com.easier.writepre.fragment;

import java.util.ArrayList;
import java.util.List;

import com.easier.writepre.R;
import com.easier.writepre.adapter.SquareAllEssenceListAdapter;
import com.easier.writepre.adapter.SquareAllEssenceListAdapter.ViewHolder;
import com.easier.writepre.entity.ContentBase;
import com.easier.writepre.entity.ContentInfo;
import com.easier.writepre.http.RequestManager;
import com.easier.writepre.mainview.SocialMainView;
import com.easier.writepre.param.MySquarePostParams;
import com.easier.writepre.param.SquareContentGetParams;
import com.easier.writepre.response.BaseResponse;
import com.easier.writepre.response.SquareContentGetResponse;
import com.easier.writepre.social.refreash.PullToRefreshBase;
import com.easier.writepre.social.refreash.PullToRefreshBase.Mode;
import com.easier.writepre.social.refreash.PullToRefreshBase.OnRefreshListener;
import com.easier.writepre.social.refreash.PullToRefreshBase.OnRefreshListener2;
import com.easier.writepre.social.refreash.PullToRefreshListView;
import com.easier.writepre.ui.BaseActivity;
import com.easier.writepre.ui.NoSwipeBackActivity;
import com.easier.writepre.ui.MyPostActivity.ShuaXinInterface;
import com.easier.writepre.ui.TopicDetailActivity;
import com.easier.writepre.utils.SPUtils;
import com.easier.writepre.utils.ToastUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * 我的广场发帖
 *
 * @author zhaomaohan
 */
public class MySquarePostFragment extends BaseFragment implements
        OnRefreshListener2<ListView> {
    private PullToRefreshListView listView;

    private List<ContentInfo> list;

    private SquareAllEssenceListAdapter adapter;

    private Handler mHandler = new Handler();

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub

    }

    @Override
    public int getContextView() {
        // TODO Auto-generated method stub
        return R.layout.fragment_my_square_post;
    }

    @Override
    protected void init() {
        listView = (PullToRefreshListView) findViewById(R.id.listview);
//		listView.setMode(Mode.PULL_FROM_END);

        // 数据
        list = new ArrayList<ContentInfo>();

        adapter = new SquareAllEssenceListAdapter(getActivity(), listView.getRefreshableView());

        listView.setAdapter(adapter);

        listViewLoadData("9");

        listView.setOnRefreshListener(this);
        listView.getRefreshableView().setOnItemClickListener(
                new OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        if (position < 1) {
                            return;
                        }
                        adapter.setSelectedIndex(position - 1);
                        if (adapter.getItem(position - 1) instanceof ContentInfo) {
                            ContentInfo contentInfo = (ContentInfo) adapter.getItem(position - 1);
                            ViewHolder holder = (ViewHolder) view.getTag();
                            holder.tv_readCount.setTag(position - 1);
                            adapter.requestReadAdd(
                                    contentInfo);
                            Intent intent = new Intent(getActivity(),
                                    TopicDetailActivity.class);
                            intent.putExtra("data", contentInfo);
                            startActivityForResult(intent, TopicDetailActivity.DETAIL_CODE);
                        }
                    }
                });
    }

    /**
     * 首次获取数据
     */
    protected void listViewLoadData(String lastId) {
        RequestManager.request(getActivity(), new MySquarePostParams(lastId,
                "20"), SquareContentGetResponse.class, this, SPUtils.instance()
                .getSocialPropEntity().getApp_socail_server());
    }

    @Override
    public void onResponse(BaseResponse response) {
        if ("0".equals(response.getResCode())) {
            if (response instanceof SquareContentGetResponse) {
                SquareContentGetResponse gscrResult = (SquareContentGetResponse) response;
                if (gscrResult != null) {
                    SquareContentGetResponse.Repbody rBody = gscrResult
                            .getRepBody();
                    if (rBody != null) {
                        if (rBody.getList() != null) {
                            if (rBody.getList().isEmpty()) {
                                BaseActivity.setListLabel(listView, true);
                            } else {
                                list.addAll(rBody.getList());
                            }
                        }
                        adapter.mergeData(list, null);
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        } else {
            ToastUtil.show(response.getResMsg());
        }
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
        // TODO Auto-generated method stub
        loadNews();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
        loadOlds();
    }

    /**
     * 下拉获取数据
     */
    private void loadNews() {
        NoSwipeBackActivity.setListLabel(listView, false);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                listView.onRefreshComplete();
                list.clear();
                listViewLoadData("9");
            }
        }, 300);
    }

    /**
     * 上拉获取数据
     */
    protected void loadOlds() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                listView.onRefreshComplete();
                if (list != null && list.size() > 0) {
                    if (adapter.getItem(adapter.getCount() - 1) instanceof ContentInfo) {
                        listViewLoadData(((ContentInfo) adapter.getItem(adapter.getCount() - 1))
                                .get_id());
                    }
                }
            }
        }, 300);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (TopicDetailActivity.DETAIL_CODE == requestCode) {
            if (getActivity().RESULT_OK == resultCode) {
                adapter.replace(adapter.getSelectedIndex(), (ContentBase) data.getSerializableExtra("data"));
            } else if (getActivity().RESULT_CANCELED == resultCode) {
                adapter.remove(adapter.getSelectedIndex());
            }
        }
    }
}
