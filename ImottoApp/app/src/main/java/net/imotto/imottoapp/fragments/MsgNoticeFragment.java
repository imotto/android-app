package net.imotto.imottoapp.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import net.imotto.imottoapp.ImottoApplication;
import net.imotto.imottoapp.MainActivity;
import net.imotto.imottoapp.R;
import net.imotto.imottoapp.activities.NoticeDetailActivity;
import net.imotto.imottoapp.adapters.NoticesAdapter;
import net.imotto.imottoapp.adapters.RecyclerViewAdapter;
import net.imotto.imottoapp.adapters.RecylcerViewAdapterDataObserver;
import net.imotto.imottoapp.services.ImottoApi;
import net.imotto.imottoapp.services.models.NoticeModel;
import net.imotto.imottoapp.services.models.ReadNoticeResp;
import net.imotto.imottoapp.utils.CommUtils;
import net.imotto.imottoapp.utils.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;


/**
 * Created by sunht on 16/11/8.
 *
 */

public class MsgNoticeFragment extends Fragment implements Observer {
    private static final String TAG="MsgNoticeFragment";
    public static final int REQUEST_CODE_SHOW_NOTICE = 101;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mRefresh;
    private NoticesAdapter mAdapter;
    private TextView noDataHintView;
    private List<NoticeModel> mData = new ArrayList<>();

    private int page=1;
    private int pageSize=10;
    private boolean isLoading = false;

    private ImottoApi.InvokeCompletionHandler<ReadNoticeResp> readNoticeCompletionHandler =
            new ImottoApi.InvokeCompletionHandler<ReadNoticeResp>() {
                @Override
                public void onApiCallCompletion(ReadNoticeResp result) {
                    processReadResult(result);
                }

                @Override
                public Class<ReadNoticeResp> getGenericClass() {
                    return ReadNoticeResp.class;
                }
            };

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i(TAG, "AlbumLovedFragment onCreateView");
        View rootView = inflater.inflate(R.layout.fragment_general, container, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        mRefresh = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh);
        noDataHintView = (TextView) rootView.findViewById(R.id.lbl_no_data);
        boolean isLogin = ImottoApplication.getInstance().isUserLogin();
        noDataHintView.setText(isLogin? R.string.lbl_no_data_notice:R.string.lbl_no_data_notice_gust);

        initView();

        return rootView;

    }

    private void initView(){
        mRefresh.setColorSchemeResources(R.color.colorPrimary);
        mRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mRefresh.setRefreshing(true);
                mAdapter.setEof(false);
                mData.clear();
                mAdapter.notifyDataSetChanged();
                page = 1;
                getData();
            }
        });

        mRecyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getContext())
                .colorResId(R.color.colorRecyclerViewDivider)
                .sizeResId(R.dimen.recycler_view_divider).build());
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new NoticesAdapter(getContext(), mData);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.registerAdapterDataObserver(new RecylcerViewAdapterDataObserver(mAdapter,mRecyclerView, noDataHintView));
        mAdapter.setOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                NoticeModel model = mAdapter.getDataItem(position);
                showNotice(model);
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
                if (lastVisibleItemPosition + 1 == mAdapter.getItemCount()) {
                    Log.d(TAG, "loading executed");

                    boolean isRefreshing = mRefresh.isRefreshing();
                    if (isRefreshing) {
                        return;
                    }
                    if (!isLoading) {
                        if(!mAdapter.getEof()) {
                            isLoading = true;
                            page++;
                            getData();
                        }
                    }
                }
            }
        });

        refreshData();
    }

    private void showNotice(NoticeModel model){
        Intent intent = new Intent(getContext(), NoticeDetailActivity.class);

        String json = CommUtils.toJson(model);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.BUNDLE_JSON_NOTICE, json);
        intent.putExtras(bundle);

        startActivityForResult(intent, REQUEST_CODE_SHOW_NOTICE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == REQUEST_CODE_SHOW_NOTICE){
            if (resultCode == Activity.RESULT_OK){
                long id = data.getLongExtra(Constants.BUNDLE_NOTICE_ID, 0);
                if (id != 0 ){
                    for (NoticeModel m : mData) {
                        if (m.ID == id){
                            m.State = 1; //将提醒状态改为已读
                            break;
                        }
                    }
                    mAdapter.notifyDataSetChanged();
                }

                FragmentActivity activity = getActivity();
                if(activity instanceof MainActivity){
                    ((MainActivity)activity).update(null, null);
                }
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public void refreshData() {
        if(ImottoApplication.getInstance().isUserLogin()) {
            mRefresh.post(new Runnable() {
                @Override
                public void run() {
                    mRefresh.setRefreshing(true);
                    mAdapter.setEof(false);
                    mData.clear();
                    mAdapter.notifyDataSetChanged();
                    page = 1;
                    getData();
                }
            });
        }else{
            mData.clear();
            mAdapter.notifyDataSetChanged();
        }
    }


    private void processReadResult(ReadNoticeResp result){
        boolean refreshing = mRefresh.isRefreshing();
        if(refreshing) {
            mRefresh.setRefreshing(false);
        }

        if(isLoading){
            mAdapter.notifyItemRemoved(mAdapter.getItemCount());
            isLoading = false;
        }

        if(result.isSuccess()){
            if(result.Data!=null && result.Data.size()>0){
                int start = mData.size();

                mData.addAll(result.Data);
                mAdapter.notifyItemRangeInserted(start, result.Data.size());
                if (result.Data.size()<pageSize) {
                    mAdapter.setEof(true);
                }
            }
            else {
                mAdapter.setEof(true);
            }

        }else{
            Toast.makeText(getContext(), result.Msg, Toast.LENGTH_SHORT).show();
            mAdapter.notifyDataSetChanged();
        }
    }


    private void getData() {
        if(ImottoApplication.getInstance().isUserLogin()) {
            ImottoApi.getInstance().readNotices(page, pageSize, readNoticeCompletionHandler);
        }else{
            boolean refreshing = mRefresh.isRefreshing();
            if(refreshing) {
                mRefresh.setRefreshing(false);
            }
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        refreshData();
    }
}