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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bartoszlipinski.recyclerviewheader2.RecyclerViewHeader;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import net.imotto.imottoapp.ImottoApplication;
import net.imotto.imottoapp.R;
import net.imotto.imottoapp.adapters.MottosAdapter;
import net.imotto.imottoapp.adapters.SimpleOnMottoActionListener;
import net.imotto.imottoapp.services.ImottoApi;
import net.imotto.imottoapp.services.models.AlbumModel;
import net.imotto.imottoapp.services.models.ApiResp;
import net.imotto.imottoapp.services.models.MottoModel;
import net.imotto.imottoapp.services.models.ReadMottoResp;
import net.imotto.imottoapp.utils.CommUtils;
import net.imotto.imottoapp.utils.Constants;
import net.imotto.imottoapp.utils.DateHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunht on 16/11/22.
 */

public class AlbumDetailActivity extends BaseBackableActivity {
    private static final String TAG="AlbumDetail";
    private static final int REQUEST_CODE_UPDATE_ALBUM = 105;
    private SwipeRefreshLayout mRefresh;
    private RecyclerView mRecyclerView;
    private MottosAdapter mAdapter;
    private List<MottoModel> mData=new ArrayList<>();
    private AlbumModel album;

    private RecyclerViewHeader mHeader;
    private ImageView imgLove;
    private TextView lblTitle;
    private TextView lblTime;
    private TextView lblSummary;
    private TextView lblInfo;
    private TextView lblLoves;
    private TextView lblUser;

    private int page=1;
    private int pageSize=5;
    private boolean isLoading = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_detail);
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
        mHeader = (RecyclerViewHeader) findViewById(R.id.recycler_header);
        imgLove = (ImageView) findViewById(R.id.img_love);

        lblTitle = (TextView) findViewById(R.id.lbl_title);
        lblTime = (TextView) findViewById(R.id.lbl_time);
        lblSummary = (TextView) findViewById(R.id.lbl_summary);
        lblInfo = (TextView) findViewById(R.id.lbl_info);
        lblUser = (TextView) findViewById(R.id.lbl_user);
        lblLoves = (TextView) findViewById(R.id.lbl_love);

        Bundle bundle = getIntent().getExtras();
        String json = bundle.getString(Constants.BUNDLE_JSON_ALBUM);

        album = CommUtils.fromJson(json, AlbumModel.class);

        String titleFmt = getResources().getString(R.string.title_album_detail);
        setupToolBar(String.format(titleFmt, album.UserName));

        if (album.UID.equals(ImottoApplication.getInstance().getUserId())) {
            //当前用户的珍藏，显示修改按钮
            ImageButton mSendBtn = (ImageButton) findViewById(R.id.toolbar_right_btn);
            mSendBtn.setImageResource(R.drawable.ic_edit_box);
            mSendBtn.setVisibility(View.VISIBLE);
            mSendBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateAlbum();
                }
            });
        }

        initView();
    }

    private void updateAlbum(){
        Intent intent = new Intent(this, UpdateAlbumActivity.class);
        String json = CommUtils.toJson(album);
        intent.putExtra(Constants.BUNDLE_JSON_ALBUM, json);
        startActivityForResult(intent, REQUEST_CODE_UPDATE_ALBUM);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_CODE_UPDATE_ALBUM){
            if(resultCode == RESULT_OK){
                String json = data.getStringExtra(Constants.BUNDLE_JSON_ALBUM);
                AlbumModel model = CommUtils.fromJson(json, AlbumModel.class);

                album.Title = model.Title;
                album.Description = model.Description;

                lblTitle.setText(album.Title);
                lblSummary.setText(album.Description);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void initView(){
        lblTitle.setText(album.Title);
        lblTime.setText(DateHelper.friendlyTime(album.CreateTime)+"创建");
        lblInfo.setText(String.format("收录%d则偶得", album.Mottos));
        lblSummary.setText(album.Description);
        lblUser.setText(album.UserName);
        lblLoves.setText(CommUtils.friendlyNumber(album.Loves));
        imgLove.setImageResource(album.Loved==1? R.drawable.btn_love_on:R.drawable.btn_love);

        lblUser.setOnClickListener(onUserClickListener);

        lblLoves.setOnClickListener(onLoveClickListener);
        imgLove.setOnClickListener(onLoveClickListener);

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
                .sizeResId(R.dimen.recycler_view_divider).build());
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mHeader.attachTo(mRecyclerView);
        mAdapter = new MottosAdapter(this, mData);
        mAdapter.setOnMottoItemActionListener(new SimpleOnMottoActionListener(this));
        mRecyclerView.setAdapter(mAdapter);

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

    private void getData(){
        ImottoApi.getInstance().readAlbumMottos(album.ID, page, pageSize, new ImottoApi.InvokeCompletionHandler<ReadMottoResp>() {
            @Override
            public void onApiCallCompletion(ReadMottoResp result) {
                processResult(result);
            }

            @Override
            public Class<ReadMottoResp> getGenericClass() {
                return ReadMottoResp.class;
            }
        });
    }

    private View.OnClickListener onUserClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(album.UID.equals(ImottoApplication.getInstance().getUserId())){
                return;
            }
            Intent intent = new Intent(AlbumDetailActivity.this, UserInfoActivity.class);

            Bundle bundle = new Bundle();
            bundle.putString(Constants.BUNDLE_UID, album.UID);
            bundle.putString(Constants.BUNDLE_UNAME, album.UserName);
            intent.putExtras(bundle);

            startActivity(intent);
        }
    };

    private View.OnClickListener onLoveClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            
            if(ImottoApplication.getInstance().isUserLogin()) {

                if (album.Loved == 1) {
                    ImottoApi.getInstance().unloveCollection(album.ID, new ImottoApi.InvokeCompletionHandler<ApiResp>() {
                        @Override
                        public void onApiCallCompletion(ApiResp result) {
                            if (result.isSuccess()) {
                                album.Loved = 0;
                                album.Loves -= 1;
                                lblLoves.setText(String.format("%d", album.Loves));
                                imgLove.setImageResource(album.Loved == 1 ? R.drawable.btn_love_on : R.drawable.btn_love);
                            } else {
                                Toast.makeText(AlbumDetailActivity.this, result.Msg, Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public Class<ApiResp> getGenericClass() {
                            return ApiResp.class;
                        }
                    });
                } else {
                    ImottoApi.getInstance().loveCollection(album.ID, new ImottoApi.InvokeCompletionHandler<ApiResp>() {
                        @Override
                        public void onApiCallCompletion(ApiResp result) {
                            if (result.isSuccess()) {
                                album.Loved = 1;
                                album.Loves += 1;
                                lblLoves.setText(String.format("%d", album.Loves));
                                imgLove.setImageResource(album.Loved == 1 ? R.drawable.btn_love_on : R.drawable.btn_love);
                            } else {
                                Toast.makeText(AlbumDetailActivity.this, result.Msg, Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public Class<ApiResp> getGenericClass() {
                            return ApiResp.class;
                        }
                    });
                }
            }else{
                Toast.makeText(AlbumDetailActivity.this, "用户未登录", Toast.LENGTH_SHORT).show();
            }
        }
    };
}
