package net.imotto.imottoapp.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import net.imotto.imottoapp.ImottoApplication;
import net.imotto.imottoapp.R;
import net.imotto.imottoapp.adapters.OnReviewItemActionListener;
import net.imotto.imottoapp.adapters.RecylcerViewAdapterDataObserver;
import net.imotto.imottoapp.adapters.ReviewAdapter;
import net.imotto.imottoapp.controls.ActionSheet;
import net.imotto.imottoapp.controls.RecyclerViewDivider;
import net.imotto.imottoapp.services.ImottoApi;
import net.imotto.imottoapp.services.models.ApiResp;
import net.imotto.imottoapp.services.models.ReadReviewResp;
import net.imotto.imottoapp.services.models.ReviewModel;
import net.imotto.imottoapp.utils.Constants;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunht on 16/11/17.
 *
 */

public class ReviewActivity extends BaseBackableActivity implements ActionSheet.MenuItemClickListener {
    private static final String TAG="ReviewActivity";
    private EditText mTxtReview;
    private SwipeRefreshLayout mRefresh;
    private RecyclerView mRecyclerView;
    private ReviewAdapter mAdapter;
    private List<ReviewModel> mData = new ArrayList<>();
    private boolean isLoading = false;

    private long mid;
    private int theDay;
    private int page = 1;
    private int pageSize = Constants.DEFAULT_PAGE_SIZE;
    private ReviewModel currentModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
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
        mTxtReview = (EditText) findViewById(R.id.txt_review);

        String title = this.getResources().getString(R.string.title_review);

        Bundle bundle = this.getIntent().getExtras();
        mid = bundle.getLong(Constants.BUNDLE_MID);
        theDay = bundle.getInt(Constants.BUNDLE_THEDAY);

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
                .colorResId(R.color.colorRecyclerViewDivider)
                .sizeResId(R.dimen.recycler_view_divider).build());
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        mAdapter = new ReviewAdapter(this, mData);
        mAdapter.setOnReviewItemActionListener(new OnReviewItemActionListener() {
            @Override
            public void onShowUserAction(ReviewModel m) {
                showUser(m);
            }

            @Override
            public void onVoteAction(ReviewModel m, boolean support) {
                doVote(m, support);
            }

            @Override
            public void onMoreAction(ReviewModel m) {
                doMore(m);
            }
        });
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new RecyclerViewDivider(this,LinearLayoutManager.VERTICAL, 2, R.color.colorLightGray));

        TextView noDataHintView = (TextView) findViewById(R.id.lbl_no_data);
        noDataHintView.setText(R.string.lbl_no_data_review);
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

        refreshData();

        ImageView btnSend = (ImageView) findViewById(R.id.btn_send);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ImottoApplication.getInstance().isUserLogin()) {
                    String review = mTxtReview.getText().toString();
                    if (!review.equals("")) {

                        final ProgressDialog dialog = ProgressDialog.show(ReviewActivity.this, null, "正在加载...", true, false);
                        ImottoApi.getInstance().addReview(mid, theDay, review, new ImottoApi.InvokeCompletionHandler<ApiResp>() {
                            @Override
                            public void onApiCallCompletion(ApiResp result) {
                                dialog.dismiss();
                                processAddReviewResult(result);
                            }

                            @Override
                            public Class<ApiResp> getGenericClass() {
                                return ApiResp.class;
                            }
                        });
                    }
                }
                else{
                    Toast.makeText(ReviewActivity.this,"请登录后再发表评论.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void doMore(ReviewModel m){
        currentModel = m;
        ActionSheet menuView = new ActionSheet(this);
        menuView.setCancelButtonTitle("取消");// before add items
        menuView.addItems("复制到剪贴板", "分享到...", "举报不良信息");
        menuView.setItemClickListener(this);
        menuView.setCancelableOnTouchMenuOutside(true);
        menuView.showMenu();
    }

    @Override
    public void onItemClick(int itemPosition) {
        if(currentModel == null){
            return;
        }
        switch (itemPosition){
            case 0: //复制
                // 从API11开始android推荐使用android.content.ClipboardManager
                // 为了兼容低版本我们这里使用旧版的android.text.ClipboardManager，虽然提示deprecated，但不影响使用。
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                // 将文本内容放到系统剪贴板里。
                cm.setPrimaryClip(ClipData.newPlainText(null, currentModel.Content + "[偶得]"));
                Toast.makeText(this, "复制到剪贴板成功", Toast.LENGTH_LONG).show();
                break;
            case 1: //分享
                shareMsg("分享","评论", currentModel.Content + "[偶得]", null);
                break;
            case 2: //举报
                //ImottoApi.getInstance().addReport(""+currentModel.ID,);
                final EditText et=new EditText(this);

                new AlertDialog.Builder(this).setTitle("填写举报原因").setView(et).
                        setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1){
                                String reason=et.getText().toString();
                                Log.i(TAG,reason);
                                ImottoApi.getInstance().addReport("" + currentModel.ID, 0, reason, new ImottoApi.InvokeCompletionHandler<ApiResp>() {
                                    @Override
                                    public void onApiCallCompletion(ApiResp result) {
                                        if(result.isSuccess()){
                                            Toast.makeText(ReviewActivity.this, "举报信息已提交，我们会及时处理并向您反馈处理结果。",Toast.LENGTH_SHORT).show();
                                        }else{
                                            Toast.makeText(ReviewActivity.this, result.Msg, Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public Class<ApiResp> getGenericClass() {
                                        return ApiResp.class;
                                    }
                                });
                            }
                        }).setNegativeButton("取消",null).show();
                break;
            default:
                break;
        }
    }

    private void shareMsg(String activityTitle, String msgTitle, String msgText,
                          String imgPath) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        if (imgPath == null || imgPath.equals("")) {
            intent.setType("text/plain"); // 纯文本
        } else {
            File f = new File(imgPath);
            if (f.exists() && f.isFile()) {
                intent.setType("image/jpg");
                Uri u = Uri.fromFile(f);
                intent.putExtra(Intent.EXTRA_STREAM, u);
            }
        }
        intent.putExtra(Intent.EXTRA_SUBJECT, msgTitle);
        intent.putExtra(Intent.EXTRA_TEXT, msgText);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(Intent.createChooser(intent, activityTitle));
    }

    private void doVote(final ReviewModel m, boolean support){

        if(support) {
            boolean vote = m.Supported == 0; //未赞时为赞（true）， 已赞时为取赞(false).
            ImottoApi.getInstance().addReviewVote(m.MID, m.ID, vote, new ImottoApi.InvokeCompletionHandler<ApiResp>() {
                @Override
                public void onApiCallCompletion(ApiResp result) {
                    if(result.isSuccess()){
                        if(m.Supported == 0){
                            m.Supported = 1;
                            m.Up += 1;
                        }else{
                            m.Supported = 0;
                            m.Up -= 1;
                        }

                        mAdapter.notifyDataSetChanged();

                    }else{
                        Toast.makeText(ReviewActivity.this, result.Msg, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public Class<ApiResp> getGenericClass() {
                    return ApiResp.class;
                }
            });
        }


    }

    /**
     * 显示用户信息详情
     * author sunht
     * created at 16/11/22 上午11:07
     */
    private void showUser(ReviewModel m){
        if(m.UID.equals(ImottoApplication.getInstance().getUserId())){
            return;
        }
        Intent intent = new Intent(this, UserInfoActivity.class);

        Bundle bundle = new Bundle();
        bundle.putString(Constants.BUNDLE_UID, m.UID);
        bundle.putString(Constants.BUNDLE_UNAME, m.UserName);
        intent.putExtras(bundle);

        startActivity(intent);
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
        //ImottoApi.getInstance().readUserMottos(uid, page, pageSize, readCompletionHandler);
        ImottoApi.getInstance().readReviews(mid, page, pageSize, readCompletionHandler);
    }

    private void processAddReviewResult(ApiResp result){
        if(result.isSuccess()){
            Toast.makeText(this, "评论提交成功", Toast.LENGTH_SHORT).show();
            mTxtReview.setText("");
            mTxtReview.clearFocus();
            InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(mTxtReview.getWindowToken(),0);
            refreshData();
        }else{
            Toast.makeText(this, result.Msg, Toast.LENGTH_SHORT).show();
        }
    }

    private void processResult(ReadReviewResp result){
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

    private ImottoApi.InvokeCompletionHandler<ReadReviewResp> readCompletionHandler = new ImottoApi.InvokeCompletionHandler<ReadReviewResp>() {
        @Override
        public void onApiCallCompletion(ReadReviewResp result) {
            processResult(result);
        }

        @Override
        public Class<ReadReviewResp> getGenericClass() {
            return ReadReviewResp.class;
        }
    };
}