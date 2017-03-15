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
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import net.imotto.imottoapp.ImottoApplication;
import net.imotto.imottoapp.R;
import net.imotto.imottoapp.adapters.OnUserDataItemActionListener;
import net.imotto.imottoapp.adapters.RecylcerViewAdapterDataObserver;
import net.imotto.imottoapp.adapters.UserRankAdapter;
import net.imotto.imottoapp.controls.RecyclerViewDivider;
import net.imotto.imottoapp.controls.WheelView;
import net.imotto.imottoapp.services.ImottoApi;
import net.imotto.imottoapp.services.models.ReadUserRankResp;
import net.imotto.imottoapp.services.models.UserRankModel;
import net.imotto.imottoapp.utils.Constants;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by sunht on 2017/1/6.
 *
 */

public class RankBoardActivity extends BaseBackableActivity {
    private SwipeRefreshLayout mRefresh;
    private RecyclerView mRecyclerView;
    private UserRankAdapter mAdapter;
    private TextView lblTitle;
    private List<UserRankModel> mData = new ArrayList<>();
    private int theMonth = 0;
    private int selMonthIdx = 0;

    private int minMonth = 201701;
    private List<String> months;
    private List<Integer> theMonths;
    private String titleFormat;

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
        if(bundle != null) {
            theMonth = bundle.getInt(Constants.BUNDLE_THEMONTH, 0);
        }

        initMonthes();

        mRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        lblTitle = (TextView) findViewById(R.id.toolbar_title_lbl);

        titleFormat = this.getResources().getString(R.string.title_user_rank);
        if(theMonth == 0) {
            setupToolBar(String.format(titleFormat, "总"));
        }else{
            setupToolBar(String.format(titleFormat, getPeriodName(theMonth)));
        }

        ImageButton mSendBtn = (ImageButton) findViewById(R.id.toolbar_right_btn);
        mSendBtn.setImageResource(R.drawable.ic_calendar_question);
        mSendBtn.setVisibility(View.VISIBLE);
        mSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //pick the month
                pickTheMonth();
            }
        });

        initView();
    }

    private void initMonthes(){
        months = new ArrayList<>();
        theMonths = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int startYear = calendar.get(Calendar.YEAR);
        int startMonth = calendar.get(Calendar.MONTH);
        if(day<=7){
            //向前推两个月
            for(int i=0; i<2; i++) {
                if (startMonth == 0) {
                    startMonth = 11;
                    startYear = startYear - 1;
                }else{
                    startMonth = startMonth - 1;
                }
            }
        }else{
            //向前推一个月
            if(startMonth == 0){
                startMonth = 11;
                startYear = startYear - 1;
            }else{
                startMonth = startMonth - 1;
            }
        }

        while (true) {
            if(startYear*100 + startMonth + 1 >= minMonth){
                months.add(String.format("%d年%d月", startYear, startMonth+1 ));
                theMonths.add(startYear*100 + startMonth + 1);
            }else{
                break;
            }

            if(startMonth == 0){
                startMonth = 11;
                startYear = startYear - 1;
            }else{
                startMonth = startMonth - 1;
            }
        }
    }

    private void pickTheMonth(){

        if(months.size()<=0){
            return;
        }

        View view = getLayoutInflater().inflate(R.layout.dialog_pick_month, null);

        WheelView wvMonth = (WheelView) view.findViewById(R.id.wv_month);
        wvMonth.setItems(months);

        int selIndex = 0;
        if(theMonth != 0){
            String theMonthName = getPeriodName(theMonth);
            int tmp = months.indexOf(theMonthName);
            if(tmp > 0){
                selIndex = tmp;
            }
        }

        wvMonth.setOnWheelViewListener(new WheelView.OnWheelViewListener(){
            @Override
            public void onSelected(int selectedIndex, String item) {
                selMonthIdx = selectedIndex-1;
            }
        });

        wvMonth.setSeletion(selIndex);

        AlertDialog.Builder builder = new AlertDialog.Builder(this).setView(view).setTitle("查看月度积分排行榜")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        theMonth = theMonths.get(selMonthIdx);
                        mRefresh.post(new Runnable() {
                            @Override
                            public void run() {
                                mRefresh.setRefreshing(true);
                                mAdapter.setEof(false);
                                mData.clear();
                                mAdapter.notifyDataSetChanged();
                                getData();
                            }
                        });
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        if(theMonth != 0){
            builder.setNeutralButton("总积分榜", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    theMonth = 0;
                    mRefresh.post(new Runnable() {
                        @Override
                        public void run() {
                            mRefresh.setRefreshing(true);
                            mAdapter.setEof(false);
                            mData.clear();
                            mAdapter.notifyDataSetChanged();
                            getData();
                        }
                    });
                }
            });
        }

        builder.show();


    }

    private String getPeriodName(int themonth){
        if(themonth == 0){
            return "总";
        }
        if(themonth<201600||themonth>299900){
            return Integer.toString(themonth);
        }

        return String.format("%d年%d月", themonth/100, themonth%100);
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
                getData();
            }
        });

        mRecyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this)
                .colorResId(R.color.colorRecyclerViewDivider)
                .sizeResId(R.dimen.recycler_view_divider).build());
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        mAdapter = new UserRankAdapter(this, mData);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new RecyclerViewDivider(this,LinearLayoutManager.VERTICAL, 2, R.color.colorLightGray));

        TextView noDataHintView = (TextView) findViewById(R.id.lbl_no_data);
        noDataHintView.setText("");
        mAdapter.registerAdapterDataObserver(new RecylcerViewAdapterDataObserver(mAdapter,mRecyclerView, noDataHintView));

        mAdapter.setShowEndHint(false);

        mAdapter.setOnActionListener(new OnUserDataItemActionListener() {
            @Override
            public void onShowUserAction(String uid, String uname, String uthumb) {
                showUser(uid, uname, uthumb);
            }
        });


        mRefresh.post(new Runnable() {
            @Override
            public void run() {
                mRefresh.setRefreshing(true);
                mAdapter.setEof(false);
                mData.clear();
                mAdapter.notifyDataSetChanged();
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
        lblTitle.setText(String.format(titleFormat, getPeriodName(theMonth)));
        ImottoApi.getInstance().readUsers(theMonth, readCompletionHandler);
    }

    private void processResult(ReadUserRankResp result){
        boolean refreshing = mRefresh.isRefreshing();
        if(refreshing) {
            mRefresh.setRefreshing(false);
        }

        if(result.isSuccess()){
            if(result.Data!=null && result.Data.size()>0){
                mData.addAll(result.Data);
            }

            mAdapter.notifyDataSetChanged();

        }else{
            Toast.makeText(this, result.Msg, Toast.LENGTH_SHORT).show();
        }
    }

    private ImottoApi.InvokeCompletionHandler<ReadUserRankResp> readCompletionHandler=
            new ImottoApi.InvokeCompletionHandler<ReadUserRankResp>() {
                @Override
                public void onApiCallCompletion(ReadUserRankResp result) {
                    processResult(result);
                }

                @Override
                public Class<ReadUserRankResp> getGenericClass() {
                    return ReadUserRankResp.class;
                }
            };
}
