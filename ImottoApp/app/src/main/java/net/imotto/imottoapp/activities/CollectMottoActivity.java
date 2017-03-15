package net.imotto.imottoapp.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.Toast;

import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import net.imotto.imottoapp.ImottoApplication;
import net.imotto.imottoapp.R;
import net.imotto.imottoapp.adapters.AlbumsSelAdapter;
import net.imotto.imottoapp.adapters.RecyclerViewAdapter;
import net.imotto.imottoapp.controls.RecyclerViewDivider;
import net.imotto.imottoapp.services.ImottoApi;
import net.imotto.imottoapp.services.models.AlbumModel;
import net.imotto.imottoapp.services.models.ApiResp;
import net.imotto.imottoapp.services.models.ReadAlbumResp;
import net.imotto.imottoapp.utils.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunht on 16/11/18.
 *
 */

public class CollectMottoActivity extends BaseBackableActivity {
    private static final String TAG="CollectMotto";

    public static final int REQUEST_ADD_ALBUM=1;

    private SwipeRefreshLayout mRefresh;
    private RecyclerView mRecyclerView;
    private AlbumsSelAdapter mAdapter;
    private List<AlbumModel> mData = new ArrayList<>();
    private boolean isLoading = false;

    private long mid;
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

        Bundle bundle = this.getIntent().getExtras();
        mid = bundle.getLong(Constants.BUNDLE_MID);

        String title = this.getResources().getString(R.string.title_collect_motto);
        setupToolBar(title);

        ImageButton btnSend = (ImageButton) findViewById(R.id.toolbar_right_btn);
        btnSend.setImageResource(R.drawable.ic_plus_small);
        btnSend.setVisibility(View.VISIBLE);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoCreateAlbum();
            }
        });

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
                .colorResId(R.color.colorRecyclerViewDivider)
                .sizeResId(R.dimen.recycler_view_divider).build());
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        mAdapter = new AlbumsSelAdapter(this, mData);
        mAdapter.setOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                AlbumModel model = mAdapter.getDataItem(position);
                switchCollectState(model, position);
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new RecyclerViewDivider(this,LinearLayoutManager.VERTICAL, 2, R.color.colorLightGray));
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

        refreshData();
    }

    private void gotoCreateAlbum(){
        Intent intent = new Intent(this, AddAlbumActivity.class);
        startActivityForResult(intent, REQUEST_ADD_ALBUM);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_ADD_ALBUM){
            refreshData();
        }
    }

    private void switchCollectState(final AlbumModel model, final int position){
        if(model.ContainsMID == mid){
            //remove

            final ProgressDialog dialog = ProgressDialog.show(this, null, "正在加载...", true, false);
            ImottoApi.getInstance().removeMottoFromCollection(mid, model.ID, new ImottoApi.InvokeCompletionHandler<ApiResp>() {
                @Override
                public void onApiCallCompletion(ApiResp result) {
                    dialog.dismiss();
                    if(result.isSuccess()) {
                        model.ContainsMID = 0;
                        model.Mottos -= 1;
                        mAdapter.notifyItemChanged(position);
                    } else{
                        Toast.makeText(CollectMottoActivity.this, result.Msg, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public Class<ApiResp> getGenericClass() {
                    return ApiResp.class;
                }
            });
        }else{

            final ProgressDialog dialog = ProgressDialog.show(this, null, "正在加载...", true, false);
            ImottoApi.getInstance().addMottoToCollection(mid, model.ID, new ImottoApi.InvokeCompletionHandler<ApiResp>() {
                @Override
                public void onApiCallCompletion(ApiResp result) {
                    dialog.dismiss();
                    if(result.isSuccess()) {
                        model.ContainsMID = mid;
                        model.Mottos += 1;
                        mAdapter.notifyItemChanged(position);
                    }else{
                        Toast.makeText(CollectMottoActivity.this, result.Msg, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public Class<ApiResp> getGenericClass() {
                    return ApiResp.class;
                }
            });
        }
    }

    private void refreshData() {
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
        String uid = ImottoApplication.getInstance().getUserId();
        ImottoApi.getInstance().readUserAlbum(uid, mid, page, pageSize, readCompletionHandler);
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

    private ImottoApi.InvokeCompletionHandler<ReadAlbumResp> readCompletionHandler = new ImottoApi.InvokeCompletionHandler<ReadAlbumResp>() {
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