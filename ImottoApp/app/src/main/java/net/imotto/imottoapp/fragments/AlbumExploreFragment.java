package net.imotto.imottoapp.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import net.imotto.imottoapp.R;
import net.imotto.imottoapp.activities.AlbumDetailActivity;
import net.imotto.imottoapp.adapters.AlbumsAdapter;
import net.imotto.imottoapp.adapters.RecyclerViewAdapter;
import net.imotto.imottoapp.adapters.RecylcerViewAdapterDataObserver;
import net.imotto.imottoapp.adapters.SimpleOnAlbumActionListener;
import net.imotto.imottoapp.services.ImottoApi;
import net.imotto.imottoapp.services.models.AlbumModel;
import net.imotto.imottoapp.services.models.ReadAlbumResp;
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

public class AlbumExploreFragment extends Fragment implements Observer {
    private static final String TAG="AlbumExplore";
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mRefresh;
    private AlbumsAdapter mAdapter;
    private View noDataHintView;
    private List<AlbumModel> mData = new ArrayList<>();

    private int page=1;
    private int pageSize=10;
    private boolean isLoading = false;

    private ImottoApi.InvokeCompletionHandler<ReadAlbumResp> readAlbumCompletionHandler=
            new ImottoApi.InvokeCompletionHandler<ReadAlbumResp>() {
                @Override
                public void onApiCallCompletion(ReadAlbumResp result) {
                    processReadResult(result);
                }

                @Override
                public Class<ReadAlbumResp> getGenericClass() {
                    return ReadAlbumResp.class;
                }
            };

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_general, container, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        mRefresh = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh);
        noDataHintView = rootView.findViewById(R.id.lbl_no_data);
        initView();

        return rootView;

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


        mRecyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getContext())
                .colorResId(R.color.ncolorDivider)
                .sizeResId(R.dimen.recycler_view_ndivider).build());
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(layoutManager);

        mAdapter = new AlbumsAdapter(getContext(), mData);

        mAdapter.setOnAlbumActionListener(new SimpleOnAlbumActionListener(getContext()));
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

    private void showAlbumDetail(AlbumModel album){
        Intent intent = new Intent(getContext(), AlbumDetailActivity.class);
        Bundle bundle = new Bundle();
        String json = CommUtils.toJson(album);
        bundle.putString(Constants.BUNDLE_JSON_ALBUM, json);
        intent.putExtras(bundle);

        startActivity(intent);
    }


    private void getData(){
            ImottoApi.getInstance().readAlbum(page, pageSize, readAlbumCompletionHandler);
    }

    private void processReadResult(ReadAlbumResp result){
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

    @Override
    public void update(Observable o, Object arg) {
        // no user related data, so don't need to refresh.
        // refreshData();
    }
}
