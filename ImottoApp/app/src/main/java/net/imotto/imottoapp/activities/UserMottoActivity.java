package net.imotto.imottoapp.activities;


import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
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

import net.imotto.imottoapp.ImottoApplication;
import net.imotto.imottoapp.R;
import net.imotto.imottoapp.adapters.MottosAdapter;
import net.imotto.imottoapp.adapters.RecylcerViewAdapterDataObserver;
import net.imotto.imottoapp.adapters.SimpleOnMottoActionListener;
import net.imotto.imottoapp.controls.RecyclerViewDivider;
import net.imotto.imottoapp.services.ImottoApi;
import net.imotto.imottoapp.services.models.MottoModel;
import net.imotto.imottoapp.services.models.ReadMottoResp;
import net.imotto.imottoapp.utils.Constants;
import net.imotto.imottoapp.utils.Encryptor;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by sunht on 16/11/14.
 *
 */

public class UserMottoActivity extends BaseBackableActivity {
    private static final String TAG="UserMottos";
    private SwipeRefreshLayout mRefresh;
    private RecyclerView mRecyclerView;
    private MottosAdapter mAdapter;
    private TextView noDataHintView;
    private List<MottoModel> mData = new ArrayList<>();
    private boolean isLoading = false;
    private String uname;
    private String uid;
    private int page = 1;
    private int pageSize = Constants.DEFAULT_PAGE_SIZE;
    //private Handler handler = new Handler();

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
        noDataHintView = (TextView) findViewById(R.id.lbl_no_data);

        String titleFormat = this.getResources().getString(R.string.title_user_mottos);

        Bundle bundle = this.getIntent().getExtras();
        uname = bundle.getString(Constants.BUNDLE_UNAME);
        uid = bundle.getString(Constants.BUNDLE_UID);

        setupToolBar(String.format(titleFormat, uname));

        if(ImottoApplication.getInstance().isUserLogin() &&
                uid.equals(ImottoApplication.getInstance().getUserId())){
            //当前用户的偶得

            ImageButton mSendBtn = (ImageButton) findViewById(R.id.toolbar_right_btn);
            mSendBtn.setImageResource(R.drawable.ic_archive_white);
            mSendBtn.setVisibility(View.VISIBLE);
            mSendBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    archiveMyMottos();
                }
            });

            noDataHintView.setText(R.string.lbl_no_data_my_mottos);
        }else{
            String fmt = getResources().getString(R.string.lbl_no_data_other_mottos);
            noDataHintView.setText(String.format(fmt, uname));
        }

        initView();
    }

    private void archiveMyMottos(){
        String rnd = UUID.randomUUID().toString().substring(0, 5);
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMddHHmmss");
        String time = fmt.format(new Date());
        String param = rnd + uid + time;
        byte[] bytes = param.getBytes();
        String encryptedParam = null;
        try {
            encryptedParam = Encryptor.encryptBASE64(bytes);
        }catch (Exception e){
            e.printStackTrace();
        }

        if(encryptedParam != null) {

            final String url = getResources().getString(R.string.url_archive) + encryptedParam;

            new AlertDialog.Builder(this).setTitle(R.string.dialog_title_archive)
                    .setMessage(R.string.dialog_msg_archive)
                    .setPositiveButton("下载", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Uri uri = Uri.parse(url);
                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                            startActivity(intent);
                        }
                    })
                    .setNeutralButton("复制地址", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                            // 将文本内容放到系统剪贴板里。
                            cm.setPrimaryClip(ClipData.newPlainText(null, url));
                            Toast.makeText(UserMottoActivity.this, "已复制下载地址到剪贴板", Toast.LENGTH_LONG).show();
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).show();
        }

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
                .colorResId(R.color.ncolorDivider)
                .sizeResId(R.dimen.recycler_view_ndivider).build());
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        mAdapter = new MottosAdapter(this, mData);
        mAdapter.setOnMottoItemActionListener(new SimpleOnMottoActionListener(this));
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new RecyclerViewDivider(this,LinearLayoutManager.VERTICAL, 2, R.color.colorLightGray));

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

    private void getData(){
        ImottoApi.getInstance().readUserMottos(uid, page, pageSize, readCompletionHandler);
    }

    private void processResult(ReadMottoResp result){
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

    private ImottoApi.InvokeCompletionHandler<ReadMottoResp> readCompletionHandler = new ImottoApi.InvokeCompletionHandler<ReadMottoResp>() {
        @Override
        public void onApiCallCompletion(ReadMottoResp result) {
            processResult(result);
        }

        @Override
        public Class<ReadMottoResp> getGenericClass() {
            return ReadMottoResp.class;
        }
    };
}
