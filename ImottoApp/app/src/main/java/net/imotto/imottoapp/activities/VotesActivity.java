package net.imotto.imottoapp.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import net.imotto.imottoapp.ImottoApplication;
import net.imotto.imottoapp.R;
import net.imotto.imottoapp.adapters.OnUserDataItemActionListener;
import net.imotto.imottoapp.adapters.RecylcerViewAdapterDataObserver;
import net.imotto.imottoapp.adapters.VotesAdapter;
import net.imotto.imottoapp.controls.RecyclerViewDivider;
import net.imotto.imottoapp.services.ImottoApi;
import net.imotto.imottoapp.services.models.ReadVotesResp;
import net.imotto.imottoapp.services.models.VoteModel;
import net.imotto.imottoapp.utils.CommUtils;
import net.imotto.imottoapp.utils.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunht on 2016/12/18.
 *
 */

public class VotesActivity extends BaseBackableActivity{
    private static final String TAG = "VotesActivity";
    private SwipeRefreshLayout mRefresh;
    private RecyclerView mRecyclerView;
    private VotesAdapter mAdapter;
    private List<VoteModel> mData = new ArrayList<>();
    private boolean isLoading = false;
    private int page = 1;
    private int pageSize = Constants.DEFAULT_PAGE_SIZE;
    private long mid = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
            localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
            if (Build.VERSION.SDK_INT < 21) {
                localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION | localLayoutParams.flags);

                SystemBarTintManager tintManager = new SystemBarTintManager(this);
                tintManager.setStatusBarTintColor(getResources().getColor(R.color.colorPrimary));
                tintManager.setStatusBarTintEnabled(true);
            }
        }

        Bundle bundle = getIntent().getExtras();
        mid = bundle.getLong(Constants.BUNDLE_MID);

        mRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        String title = this.getResources().getString(R.string.title_votes);

        setupToolBar(title);

        initView();
    }

    private void initView(){
        mRefresh.setColorSchemeResources(R.color.colorPrimary);
        mRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mAdapter.setEof(false);
                mData.clear();
                mAdapter.notifyDataSetChanged();
                page = 1;
                getData();
            }
        });

        mRecyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this)
                .margin(CommUtils.dp2px(56, this), 0)
                .colorResId(R.color.colorRecyclerViewDivider)
                .sizeResId(R.dimen.recycler_view_divider).build());
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        mAdapter = new VotesAdapter(this, mData);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new RecyclerViewDivider(this,LinearLayoutManager.VERTICAL, 2, R.color.colorLightGray));

        TextView noDataHintView = (TextView) findViewById(R.id.lbl_no_data);
        noDataHintView.setText(R.string.lbl_no_data_vote);
        mAdapter.registerAdapterDataObserver(new RecylcerViewAdapterDataObserver(mAdapter,mRecyclerView, noDataHintView));
        mAdapter.setOnActionListener(new OnUserDataItemActionListener() {
            @Override
            public void onShowUserAction(String uid, String uname, String uthumb) {
                showUser(uid, uname, uthumb);
            }
        });

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                Log.d(TAG, "StateChanged = " + newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                Log.d(TAG, "onScrolled");

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
    }

    private void showUser(String uid, String uname, String uthumb){
        if(uid.equals(ImottoApplication.getInstance().getUserId())){
            return;
        }
        Intent intent = new Intent(this, UserInfoActivity.class);

        Bundle bundle = new Bundle();
        bundle.putString(Constants.BUNDLE_UID, uid);
        bundle.putString(Constants.BUNDLE_UNAME, uname);
        bundle.putString(Constants.BUNDLE_UTHUMB, uthumb);
        intent.putExtras(bundle);

        startActivity(intent);
    }

    private void getData(){
        ImottoApi.getInstance().readVotes(mid, page, pageSize, readCompletionHandler);
    }

    private void processResult(ReadVotesResp result){
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
            Toast.makeText(this, result.Msg, Toast.LENGTH_SHORT).show();
        }
    }

    private ImottoApi.InvokeCompletionHandler<ReadVotesResp> readCompletionHandler =
            new ImottoApi.InvokeCompletionHandler<ReadVotesResp>() {
                @Override
                public void onApiCallCompletion(ReadVotesResp result) {
                    processResult(result);
                }

                @Override
                public Class<ReadVotesResp> getGenericClass() {
                    return ReadVotesResp.class;
                }
            };
}
