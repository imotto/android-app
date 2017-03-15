package net.imotto.imottoapp.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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

import net.imotto.imottoapp.R;
import net.imotto.imottoapp.adapters.AwardsAdapter;
import net.imotto.imottoapp.adapters.OnAwardItemActionListener;
import net.imotto.imottoapp.adapters.RecyclerViewAdapter;
import net.imotto.imottoapp.adapters.RecylcerViewAdapterDataObserver;
import net.imotto.imottoapp.controls.RecyclerViewDivider;
import net.imotto.imottoapp.services.ImottoApi;
import net.imotto.imottoapp.services.models.ApiResp;
import net.imotto.imottoapp.services.models.AwardModel;
import net.imotto.imottoapp.services.models.ReadAwardsResp;
import net.imotto.imottoapp.utils.CommUtils;
import net.imotto.imottoapp.utils.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunht on 2017/1/12.
 *
 */

public class AwardsActivity extends BaseBackableActivity implements OnAwardItemActionListener {
    private static final String TAG="MyGifts";
    private static final int REQUEST_CODE_SET_ADDRESS = 109;
    private static final int REQUEST_CODE_SHOW_DETAIL = 110;
    private SwipeRefreshLayout mRefresh;
    private RecyclerView mRecyclerView;
    private AwardsAdapter mAdapter;
    private List<AwardModel> mData = new ArrayList<>();
    private boolean isLoading = false;
    private int page = 1;
    private int pageSize = Constants.DEFAULT_PAGE_SIZE;


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

        mRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        setupToolBar(R.string.title_user_awards);

        initView();
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

        mRecyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this)
                .colorResId(R.color.colorRecyclerViewDivider)
                .sizeResId(R.dimen.recycler_view_divider).build());
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        mAdapter = new AwardsAdapter(this, mData);
        mAdapter.setOnActionListener(this);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new RecyclerViewDivider(this,LinearLayoutManager.VERTICAL, 2, R.color.colorLightGray));

        TextView noDataHintView = (TextView) findViewById(R.id.lbl_no_data);
        noDataHintView.setText(R.string.lbl_no_data_mygift);
        mAdapter.registerAdapterDataObserver(new RecylcerViewAdapterDataObserver(mAdapter,mRecyclerView, noDataHintView));
        mAdapter.setOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                showAwardDetail(mAdapter.getDataItem(position));
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

    private void showAwardDetail(AwardModel model){
        Intent intent = new Intent(this, AwardDetailActivity.class);
        String json = CommUtils.toJson(model);
        intent.putExtra(Constants.BUNDLE_JSON_AWARD, json);
        startActivityForResult(intent, REQUEST_CODE_SHOW_DETAIL);
    }

    private void getData(){
        ImottoApi.getInstance().readAwards(page, pageSize, readCompletionHandler);
    }

    private void processResult(ReadAwardsResp result){
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

    private ImottoApi.InvokeCompletionHandler<ReadAwardsResp> readCompletionHandler=
            new ImottoApi.InvokeCompletionHandler<ReadAwardsResp>() {
                @Override
                public void onApiCallCompletion(ReadAwardsResp result) {
                    processResult(result);
                }

                @Override
                public Class<ReadAwardsResp> getGenericClass() {
                    return ReadAwardsResp.class;
                }
            };


    @Override
    public void onReceiveAwardAction(final AwardModel m) {

        new AlertDialog.Builder(this).setTitle("奖品签收").setMessage("确认已经收到奖品了?")
                .setPositiveButton("是的", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        doReceiveAward(m);
                    }
                }).setNegativeButton("还没有", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(AwardsActivity.this, "那就再等等吧,应该快到了.", Toast.LENGTH_SHORT).show();
            }
        }).show();
    }

    private void doReceiveAward(final AwardModel m){

        final ProgressDialog dialog = ProgressDialog.show(this,"正在提交...","");

        ImottoApi.getInstance().receiveAward(m.ID, new ImottoApi.InvokeCompletionHandler<ApiResp>() {
            @Override
            public void onApiCallCompletion(ApiResp result) {
                dialog.dismiss();
                if(result.isSuccess()){
                    m.GainStatus = 4;
                    int index = mData.indexOf(m);
                    mAdapter.notifyItemChanged(index);
                }else{
                    Toast.makeText(AwardsActivity.this, result.Msg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public Class<ApiResp> getGenericClass() {
                return ApiResp.class;
            }
        });
    }

    @Override
    public void onSetAddressAction(AwardModel m) {
        Intent intent = new Intent(this, SelAddressActivity.class);
        intent.putExtra(Constants.BUNDLE_AWARD_ID, m.ID);
        startActivityForResult(intent, REQUEST_CODE_SET_ADDRESS);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_CODE_SET_ADDRESS){
            if(resultCode == RESULT_OK){
                int awardId = data.getIntExtra(Constants.BUNDLE_AWARD_ID, 0);
                if(awardId != 0){
                    for(int i=0;i<mData.size();i++){
                        AwardModel m = mData.get(i);
                        if(m.ID == awardId) {
                            m.GainStatus = 2;// 设为待发放
                            mAdapter.notifyItemChanged(i);
                            break;
                        }
                    }
                }
            }
        }else if(requestCode == REQUEST_CODE_SHOW_DETAIL){
            if(resultCode == RESULT_OK){
                String json = data.getStringExtra(Constants.BUNDLE_JSON_AWARD);
                if(json !=null && (!json.isEmpty())) {
                    AwardModel model = CommUtils.fromJson(json, AwardModel.class);
                    for(int i=0;i<mData.size();i++){
                        AwardModel m = mData.get(i);
                        if(m.ID == model.ID){
                            m.GainStatus = model.GainStatus;
                            mAdapter.notifyItemChanged(i);
                            break;
                        }
                    }
                }

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
