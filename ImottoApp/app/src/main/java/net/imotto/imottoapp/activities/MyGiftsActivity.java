package net.imotto.imottoapp.activities;

import android.app.AlertDialog;
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
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import net.imotto.imottoapp.R;
import net.imotto.imottoapp.adapters.ExchangesAdapter;
import net.imotto.imottoapp.adapters.OnExchangeRecordActionListener;
import net.imotto.imottoapp.adapters.RecylcerViewAdapterDataObserver;
import net.imotto.imottoapp.controls.RecyclerViewDivider;
import net.imotto.imottoapp.services.ImottoApi;
import net.imotto.imottoapp.services.models.ApiResp;
import net.imotto.imottoapp.services.models.ExchangeRecordModel;
import net.imotto.imottoapp.services.models.ReadExchangeRecordResp;
import net.imotto.imottoapp.utils.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunht on 2016/12/21.
 * 我的礼品
 */

public class MyGiftsActivity extends BaseBackableActivity implements OnExchangeRecordActionListener {
    private static final String TAG="MyGifts";
    public static final int REQUEST_CODE_REVIEW_GIFT = 109;
    private SwipeRefreshLayout mRefresh;
    private RecyclerView mRecyclerView;
    private ExchangesAdapter mAdapter;
    private List<ExchangeRecordModel> mData = new ArrayList<>();
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

        setupToolBar(R.string.title_user_gifts);

        ImageButton mSendBtn = (ImageButton) findViewById(R.id.toolbar_right_btn);
        mSendBtn.setImageResource(R.drawable.ic_cart_outline_white);
        mSendBtn.setVisibility(View.VISIBLE);
        mSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoGifts();
            }
        });

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

        mAdapter = new ExchangesAdapter(this, mData);
        mAdapter.setOnActionListener(this);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new RecyclerViewDivider(this,LinearLayoutManager.VERTICAL, 2, R.color.colorLightGray));

        TextView noDataHintView = (TextView) findViewById(R.id.lbl_no_data);
        noDataHintView.setText(R.string.lbl_no_data_mygift);
        mAdapter.registerAdapterDataObserver(new RecylcerViewAdapterDataObserver(mAdapter,mRecyclerView, noDataHintView));

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

    private void gotoGifts(){
        Intent intent = new Intent(this, GiftsActivity.class);
        startActivity(intent);
    }

    private void getData(){
        ImottoApi.getInstance().readMyExchangeRecord(page, pageSize,readCompletionHandler);
    }

    private void processResult(ReadExchangeRecordResp result){
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

    private ImottoApi.InvokeCompletionHandler<ReadExchangeRecordResp> readCompletionHandler=
            new ImottoApi.InvokeCompletionHandler<ReadExchangeRecordResp>() {
                @Override
                public void onApiCallCompletion(ReadExchangeRecordResp result) {
                    processResult(result);
                }

                @Override
                public Class<ReadExchangeRecordResp> getGenericClass() {
                    return ReadExchangeRecordResp.class;
                }
            };


    @Override
    public void onReceiveGiftAction(final ExchangeRecordModel m) {

        new AlertDialog.Builder(this).setTitle("礼品签收").setMessage("确认已经收到礼品了?")
                .setPositiveButton("是的", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        doReceiveGift(m);
                    }
                }).setNegativeButton("还没有", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(MyGiftsActivity.this, "那就再等等吧,应该快到了.", Toast.LENGTH_SHORT).show();
                    }
                }).show();
    }

    private void doReceiveGift(final ExchangeRecordModel m){
        ImottoApi.getInstance().receiveGift(m.ID, new ImottoApi.InvokeCompletionHandler<ApiResp>() {
            @Override
            public void onApiCallCompletion(ApiResp result) {
                if(result.isSuccess()){
                    m.DeliverState = 2;
                    int index = mData.indexOf(m);
                    mAdapter.notifyItemChanged(index);
                }else{
                    Toast.makeText(MyGiftsActivity.this, result.Msg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public Class<ApiResp> getGenericClass() {
                return ApiResp.class;
            }
        });
    }

    @Override
    public void onReviewGiftAction(ExchangeRecordModel m) {
        Intent intent = new Intent(MyGiftsActivity.this, ReviewGiftActivity.class);
        intent.putExtra(Constants.BUNDLE_GIFT_ID, m.GiftId);
        intent.putExtra(Constants.BUNDLE_EXCHANGE_ID, m.ID);
        startActivityForResult(intent,REQUEST_CODE_REVIEW_GIFT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_CODE_REVIEW_GIFT){
            if(resultCode == RESULT_OK){
                long exchangeId = data.getLongExtra(Constants.BUNDLE_EXCHANGE_ID, 0);
                if(exchangeId != 0){
                    for(int i=0;i<mData.size();i++){
                        ExchangeRecordModel m = mData.get(i);
                        if(m.ID == exchangeId) {
                            m.DeliverState = 9;// 设为已评价
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
