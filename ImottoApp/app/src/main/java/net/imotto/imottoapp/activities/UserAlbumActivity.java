package net.imotto.imottoapp.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
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

import net.imotto.imottoapp.R;
import net.imotto.imottoapp.adapters.AlbumsAdapter;
import net.imotto.imottoapp.adapters.RecyclerViewAdapter;
import net.imotto.imottoapp.adapters.RecylcerViewAdapterDataObserver;
import net.imotto.imottoapp.adapters.SimpleOnAlbumActionListener;
import net.imotto.imottoapp.controls.RecyclerViewDivider;
import net.imotto.imottoapp.services.ImottoApi;
import net.imotto.imottoapp.services.models.AlbumModel;
import net.imotto.imottoapp.services.models.ReadAlbumResp;
import net.imotto.imottoapp.utils.CommUtils;
import net.imotto.imottoapp.utils.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunht on 16/11/14.
 */

public class UserAlbumActivity extends BaseBackableActivity {
    private static final String TAG="UserAlbums";
    private SwipeRefreshLayout mRefresh;
    private RecyclerView mRecyclerView;
    private AlbumsAdapter mAdapter;
    private List<AlbumModel> mData = new ArrayList<>();
    private boolean isLoading = false;
    private String uname;
    private String uid;
    private int page = 1;
    private int pageSize = Constants.DEFAULT_PAGE_SIZE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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

        mRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        String titleFormat = this.getResources().getString(R.string.title_user_albums);

        Bundle bundle = this.getIntent().getExtras();
        uname = bundle.getString(Constants.BUNDLE_UNAME);
        uid = bundle.getString(Constants.BUNDLE_UID);

        setupToolBar(String.format(titleFormat, uname));

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
                .colorResId(R.color.ncolorDivider)
                .sizeResId(R.dimen.recycler_view_ndivider).build());
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        mAdapter = new AlbumsAdapter(this, mData);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new RecyclerViewDivider(this,LinearLayoutManager.VERTICAL, 2, R.color.colorLightGray));

        TextView noDataHintView = (TextView) findViewById(R.id.lbl_no_data);
        noDataHintView.setText(R.string.lbl_no_data_album);
        mAdapter.registerAdapterDataObserver(new RecylcerViewAdapterDataObserver(mAdapter,mRecyclerView, noDataHintView));

        mAdapter.setOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                //show detail here.
                AlbumModel model = mAdapter.getDataItem(position);
                showAlbumDetail(model);
            }

            @Override
            public void onItemLongClick(View view, int position) {

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

    private void getData(){
        ImottoApi.getInstance().readUserAlbum(uid, 0, page, pageSize, readAlbumCompletionHandler);
    }

    private void showAlbumDetail(AlbumModel album){
        Intent intent = new Intent(this, AlbumDetailActivity.class);
        Bundle bundle = new Bundle();
        String json = CommUtils.toJson(album);
        bundle.putString(Constants.BUNDLE_JSON_ALBUM, json);
        intent.putExtras(bundle);

        startActivity(intent);
    }

    private void processResult(ReadAlbumResp result){
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

    private ImottoApi.InvokeCompletionHandler<ReadAlbumResp> readAlbumCompletionHandler=
            new ImottoApi.InvokeCompletionHandler<ReadAlbumResp>() {
                @Override
                public void onApiCallCompletion(ReadAlbumResp result) {
                    processResult(result);
                }

                @Override
                public Class<ReadAlbumResp> getGenericClass() {
                    return ReadAlbumResp.class;
                }
            };
}

