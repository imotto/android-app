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
import net.imotto.imottoapp.activities.ChatActivity;
import net.imotto.imottoapp.activities.UserInfoActivity;
import net.imotto.imottoapp.adapters.OnTalkItemActionListener;
import net.imotto.imottoapp.adapters.RecyclerViewAdapter;
import net.imotto.imottoapp.adapters.RecylcerViewAdapterDataObserver;
import net.imotto.imottoapp.adapters.TalksAdapter;
import net.imotto.imottoapp.services.ImottoApi;
import net.imotto.imottoapp.services.models.ReadRecentTalkResp;
import net.imotto.imottoapp.services.models.RecentTalkModel;
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

public class MsgPrivateFragment extends Fragment implements Observer {
    private static final String TAG="PrivateMsgFrag";
    private static final int REQUEST_CODE_CHAT = 103;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mRefresh;
    private TalksAdapter mAdapter;
    private List<RecentTalkModel> mData = new ArrayList<>();
    private TextView noDataHintView;
    private int page=1;
    private int pageSize=10;
    private boolean isLoading = false;

    private ImottoApi.InvokeCompletionHandler<ReadRecentTalkResp> readRecentTalkCompletionHandler=
            new ImottoApi.InvokeCompletionHandler<ReadRecentTalkResp>() {
                @Override
                public void onApiCallCompletion(ReadRecentTalkResp result) {
                    processReadResult(result);
                }

                @Override
                public Class<ReadRecentTalkResp> getGenericClass() {
                    return ReadRecentTalkResp.class;
                }
            };

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i(TAG, "MsgPrivateFragment onCreateView");
        View rootView = inflater.inflate(R.layout.fragment_general, container, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        mRefresh = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh);
        noDataHintView = (TextView) rootView.findViewById(R.id.lbl_no_data);
        boolean isLogin = ImottoApplication.getInstance().isUserLogin();
        noDataHintView.setText(isLogin? R.string.lbl_no_data_pmsg:R.string.lbl_no_data_pmsg_gust);

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

        mAdapter = new TalksAdapter(getContext(), mData);
        mAdapter.setOnTalkItemActionListener(new OnTalkItemActionListener() {
            @Override
            public void onShowUserAction(RecentTalkModel m) {
                showUser(m);
            }
        });
        mAdapter.setOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                RecentTalkModel rtm = mAdapter.getDataItem(position);
                showChat(rtm);
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.registerAdapterDataObserver(new RecylcerViewAdapterDataObserver(mAdapter,mRecyclerView, noDataHintView));

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

    /**
    * 显示用户信息详情
    * author sunht
    * created at 16/11/22 上午11:07
    */
    private void showUser(RecentTalkModel m){
        if(m.WithUID.equals(ImottoApplication.getInstance().getUserId())){
            return;
        }
        Intent intent = new Intent(getContext(), UserInfoActivity.class);

        Bundle bundle = new Bundle();
        bundle.putString(Constants.BUNDLE_UID, m.WithUID);
        bundle.putString(Constants.BUNDLE_UNAME, m.UserName);
        intent.putExtras(bundle);

        startActivity(intent);
    }

    private void showChat(RecentTalkModel rtm){
        String json = CommUtils.toJson(rtm);

        Intent intent = new Intent(getContext(), ChatActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.BUNDLE_JSON_TALK, json);
        intent.putExtras(bundle);
        startActivityForResult(intent, REQUEST_CODE_CHAT);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_CODE_CHAT){
            if(resultCode == Activity.RESULT_OK){
                if(data!=null){
                    String uid = data.getStringExtra(Constants.BUNDLE_UID);
                    if(uid!=null){
                        for(int i=0;i<mData.size();i++){
                            RecentTalkModel model = mData.get(i);
                            if(uid.equals(model.WithUID)){
                                model.Msgs = 0;
                                mAdapter.notifyItemChanged(i);
                            }
                        }
                    }

                    FragmentActivity activity = getActivity();
                    if(activity instanceof MainActivity){
                        ((MainActivity)activity).update(null, null);
                    }
                }

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void processReadResult(ReadRecentTalkResp result){
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
        }
    }


    private void getData() {
        if(ImottoApplication.getInstance().isUserLogin()) {
            ImottoApi.getInstance().readRecentTalk(page, pageSize, readRecentTalkCompletionHandler);
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