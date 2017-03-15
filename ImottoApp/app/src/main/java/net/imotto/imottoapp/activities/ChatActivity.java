package net.imotto.imottoapp.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.readystatesoftware.systembartint.SystemBarTintManager;

import net.imotto.imottoapp.R;
import net.imotto.imottoapp.adapters.RecylcerViewAdapterDataObserver;
import net.imotto.imottoapp.adapters.TalkMsgsAdapter;
import net.imotto.imottoapp.services.ImottoApi;
import net.imotto.imottoapp.services.models.ApiResp;
import net.imotto.imottoapp.services.models.ReadTalkMsgResp;
import net.imotto.imottoapp.services.models.RecentTalkModel;
import net.imotto.imottoapp.services.models.TalkMsgModel;
import net.imotto.imottoapp.utils.CommUtils;
import net.imotto.imottoapp.utils.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by sunht on 16/11/22.
 *
 */

public class ChatActivity extends BaseBackableActivity {
    private SwipeRefreshLayout mRefresh;
    private RecyclerView mRecyclerView;
    private EditText mTxtMsg;
    private TalkMsgsAdapter mAdapter;
    private List<TalkMsgModel> mData = new ArrayList<>();

    private RecentTalkModel talk;

    private int pageSize = Constants.DEFAULT_PAGE_SIZE;
    private boolean isbof = false;
    private boolean iseof = false;
    private boolean autoLoadStopped = false;
    private boolean isLoadingMore = false;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
            localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
        }if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
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
        mTxtMsg = (EditText) findViewById(R.id.txt_msg);

        Bundle bundle = getIntent().getExtras();
        String json = bundle.getString(Constants.BUNDLE_JSON_TALK);
        talk = CommUtils.fromJson(json, RecentTalkModel.class);
        setupToolBar(talk.UserName);

        initView();
    }

    private void initView(){
        mRefresh.setColorSchemeResources(R.color.colorPrimary);
        mRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData();
            }
        });

        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        mAdapter = new TalkMsgsAdapter(this, mData);
        mAdapter.setWithUser(talk.UserName, talk.UserThumb);
        mRecyclerView.setAdapter(mAdapter);

        View noDataHintView = findViewById(R.id.lbl_no_data);
        mAdapter.registerAdapterDataObserver(new RecylcerViewAdapterDataObserver(mAdapter,mRecyclerView, noDataHintView));

        ImageView btnSend = (ImageView) findViewById(R.id.btn_send);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = mTxtMsg.getText().toString();
                if(!msg.equals("")){

                    final ProgressDialog dialog = ProgressDialog.show(ChatActivity.this, null, "正在加载...", true, false);
                    ImottoApi.getInstance().sendMsg(talk.WithUID, msg, new ImottoApi.InvokeCompletionHandler<ApiResp>() {
                        @Override
                        public void onApiCallCompletion(ApiResp result) {
                            dialog.dismiss();
                            processSendMsgResult(result);
                        }

                        @Override
                        public Class<ApiResp> getGenericClass() {
                            return ApiResp.class;
                        }
                    });
                }
            }
        });

        mRefresh.post(new Runnable() {
            @Override
            public void run() {
                mRefresh.setRefreshing(true);
                loadData();
            }
        });
    }

    private void loadData(){
        if(!isbof){
            final boolean isFirstLoad = mData.size() == 0;
            ImottoApi.getInstance().readTalkMsgs(talk.WithUID,
                    getStartIndex(), 0 - pageSize, new ImottoApi.InvokeCompletionHandler<ReadTalkMsgResp>() {
                        @Override
                        public void onApiCallCompletion(ReadTalkMsgResp result) {
                            mRefresh.setRefreshing(false);
                            if(result.isSuccess()){
                                Intent intent = new Intent();
                                intent.putExtra(Constants.BUNDLE_UID, talk.WithUID);
                                ChatActivity.this.setResult(RESULT_OK, intent);
                                if(result.Data!=null && result.Data.size()>0){
                                    mData.addAll(0, result.Data);
                                    isbof = result.Data.size() < pageSize;
                                    mAdapter.notifyItemRangeInserted(0, result.Data.size());
                                    if(isFirstLoad){
                                        mRecyclerView.smoothScrollToPosition(mData.size());
                                        autoLoadMoreAfter(3000);
                                    }
                                }else{
                                    isbof = true;
                                    mAdapter.notifyDataSetChanged();
                                }
                            }else{
                                Toast.makeText(ChatActivity.this, result.Msg    , Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public Class<ReadTalkMsgResp> getGenericClass() {
                            return ReadTalkMsgResp.class;
                        }
                    });
        }else{
            mRefresh.setRefreshing(false);
        }
    }

    private void autoLoadMoreAfter(long delay){
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(!autoLoadStopped) {
                    loadMore();
                }
            }
        }, delay);
    }

    private void loadMore(){

        if(isLoadingMore){
            return;
        }
        isLoadingMore = true;
        ImottoApi.getInstance().readTalkMsgs(talk.WithUID, getEndIndex(), pageSize, new ImottoApi.InvokeCompletionHandler<ReadTalkMsgResp>() {
            @Override
            public void onApiCallCompletion(ReadTalkMsgResp result) {
                if(result.isSuccess()){
                    if(result.Data!=null && result.Data.size()>0){
                        int lastPosition = mData.size();
                        mData.addAll(result.Data);
                        mAdapter.notifyItemRangeInserted(lastPosition, result.Data.size());
                        mRecyclerView.smoothScrollToPosition(mData.size());
                    }else{
                        iseof = true;
                    }
                }

                isLoadingMore = false;
                autoLoadMoreAfter(3000);
            }

            @Override
            public Class<ReadTalkMsgResp> getGenericClass() {
                return ReadTalkMsgResp.class;
            }
        });
    }

    private long getStartIndex(){
        if(mData.size()>0){
            return mData.get(0).ID;
        }
        return 0;
    }

    private long getEndIndex(){
        if(mData.size()>0){
            return mData.get(mData.size()-1).ID;
        }
        return 0;
    }


    private void processSendMsgResult(ApiResp result){
        if(result.isSuccess()){
            mTxtMsg.setText("");
            mTxtMsg.clearFocus();
            InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(mTxtMsg.getWindowToken(),0);
            loadMore();
        }else{
            Toast.makeText(this, result.Msg, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        autoLoadStopped = true;
    }
}
