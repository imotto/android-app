package net.imotto.imottoapp.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
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
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.CircleBitmapDisplayer;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import net.imotto.imottoapp.ImottoApplication;
import net.imotto.imottoapp.R;
import net.imotto.imottoapp.adapters.ProfilesAdapter;
import net.imotto.imottoapp.adapters.RecyclerViewAdapter;
import net.imotto.imottoapp.controls.ActionSheet;
import net.imotto.imottoapp.services.ImottoApi;
import net.imotto.imottoapp.services.models.ApiResp;
import net.imotto.imottoapp.services.models.ReadUserResp;
import net.imotto.imottoapp.services.models.RecentTalkModel;
import net.imotto.imottoapp.services.models.UserModel;
import net.imotto.imottoapp.utils.CommUtils;
import net.imotto.imottoapp.utils.Constants;

import java.util.ArrayList;
import java.util.List;

import cn.gavinliu.android.lib.shapedimageview.ShapedImageView;

/**
 * Created by sunht on 16/11/18.
 *
 */

public class UserInfoActivity extends BaseBackableActivity implements ActionSheet.MenuItemClickListener {
    private static final String TAG="UserInfo";
    private static final int ACTION_SHEET_UNLOVE_AND_BAN=1;
    private static final int ACTION_SHEET_LOVE_AND_UNBAN=2;
    private static final int ACTION_SHEET_LOVE_AND_BAN=3;
    private SwipeRefreshLayout mSwipeRefresh;
    private RecyclerView mRecyclerView;
    private ProfilesAdapter mAdapter;
    private List<ProfilesAdapter.ProfileItem> mData;

    private RecyclerViewHeader mHeader;
    private ImageView imgThumb;
    private ShapedImageView imgSex;
    private TextView lblUser;
    private ImageButton btnRelation;

    private UserModel uModel;
    private String uid;
    private String uname;
    private int currentActionSheet=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
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

        mSwipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mHeader = (RecyclerViewHeader) findViewById(R.id.recycler_header);
        imgThumb = (ImageView) findViewById(R.id.img_thumb);
        imgSex = (ShapedImageView) findViewById(R.id.img_sex);
        lblUser = (TextView) findViewById(R.id.lbl_user);
        btnRelation = (ImageButton) findViewById(R.id.toolbar_right_btn);

        Bundle bundle = this.getIntent().getExtras();
        uid = bundle.getString(Constants.BUNDLE_UID);
        uname = bundle.getString(Constants.BUNDLE_UNAME);
        lblUser.setText(uname);

        setupToolBar(uname);

        initView();
    }

    private void initView(){
        initData();

        mSwipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.i(TAG,"onRefresh.");
                getData();
            }
        });


        mRecyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this)
                .colorResId(R.color.colorLightDivider)
                .margin(120, 0)
                .sizeResId(R.dimen.recycler_view_divider).build());
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mHeader.attachTo(mRecyclerView);
        mAdapter = new ProfilesAdapter(this, mData);
        mAdapter.setOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                handleItemClick(position);
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
        mRecyclerView.setAdapter(mAdapter);


        btnRelation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLoveAction();
            }
        });

        refreshData();
    }

    private void refreshData(){
        mSwipeRefresh.post(new Runnable() {
            @Override
            public void run() {
                getData();
            }
        });
    }

    private void getData(){
        ImottoApi.getInstance().readUserInfo(uid, readUserRespInvokeCompletionHandler);

    }

    public void handleItemClick(int position){

        ProfilesAdapter.ProfileItem item = mAdapter.getDataItem(position);

        switch (item.id){
            case 1: //发消息
                if(ImottoApplication.getInstance().isUserLogin()){

                    Intent intent1 = new Intent(this, ChatActivity.class);
                    RecentTalkModel talk = new RecentTalkModel();
                    if(uModel != null) {
                        talk.WithUID = uModel.Id;
                        talk.UserName = uModel.UserName;
                        talk.UserThumb = uModel.Thumb;
                    }else{
                        talk.WithUID = uid;
                        talk.UserName = uname;
                        talk.UserThumb = "";
                    }

                    Bundle bundle1 = new Bundle();
                    bundle1.putString(Constants.BUNDLE_JSON_TALK, CommUtils.toJson(talk));
                    intent1.putExtras(bundle1);
                    startActivity(intent1);

                }else{
                    Toast.makeText(this, "请先登录再发送消息", Toast.LENGTH_SHORT).show();
                }
                break;
            case 2: //积分
                //不显示积分记录，只显示积分结果
                if(uModel!=null){
                    Toast.makeText(this, String.format("%s的累计积分为%d。", uname, uModel.Statistics.Revenue),
                            Toast.LENGTH_SHORT).show();
                }
                break;
            case 3: //偶得
                Intent intent4 = new Intent(this, UserMottoActivity.class);
                Bundle bundle4 = new Bundle();
                bundle4.putString(Constants.BUNDLE_UID, uid);
                bundle4.putString(Constants.BUNDLE_UNAME, uname);
                intent4.putExtras(bundle4);
                startActivity(intent4);
                break;
            case 4: //珍藏
                Intent intent5 = new Intent(this, UserAlbumActivity.class);
                Bundle bundle5 = new Bundle();
                bundle5.putString(Constants.BUNDLE_UID, uid);
                bundle5.putString(Constants.BUNDLE_UNAME, uname);
                intent5.putExtras(bundle5);
                startActivity(intent5);
                break;
            case 5: //喜欢的偶得
                Intent intent7 = new Intent(this, LovedMottoActivity.class);
                Bundle bundle7 = new Bundle();
                bundle7.putString(Constants.BUNDLE_UID, uid);
                bundle7.putString(Constants.BUNDLE_UNAME, uname);
                intent7.putExtras(bundle7);
                startActivity(intent7);
                break;
            case 6: //喜欢的珍藏
                Intent intent8 = new Intent(this, LovedAlbumActivity.class);
                Bundle bundle8 = new Bundle();
                bundle8.putString(Constants.BUNDLE_UID, uid);
                bundle8.putString(Constants.BUNDLE_UNAME, uname);
                intent8.putExtras(bundle8);
                startActivity(intent8);
                break;
            case 7: //我喜欢的人
                Intent intent10 = new Intent(this, UserFollowsActivity.class);
                Bundle bundle10 = new Bundle();
                bundle10.putString(Constants.BUNDLE_UID, uid);
                bundle10.putString(Constants.BUNDLE_UNAME, uname);
                intent10.putExtras(bundle10);
                startActivity(intent10);
                break;
            case 8: //喜欢我的人
                Intent intent11 = new Intent(this, UserFollowersActivity.class);
                Bundle bundle11 = new Bundle();
                bundle11.putString(Constants.BUNDLE_UID, uid);
                bundle11.putString(Constants.BUNDLE_UNAME, uname);
                intent11.putExtras(bundle11);
                startActivity(intent11);

                break;
            default:
                break;
        }
    }

    private void processReadResult(ReadUserResp result){
        mSwipeRefresh.setRefreshing(false);

        if(result.isSuccess()){
            if(result.Data != null){
                UserModel m = result.Data;
                uModel = result.Data;
                for (ProfilesAdapter.ProfileItem item: mData) {
                    switch (item.id)
                    {
                        case 2:
                            item.info = Integer.toString(m.Statistics.Revenue);
                            break;
                        case 3:
                            item.info = Integer.toString(m.Statistics.Mottos);
                            break;
                        case 4:
                            item.info = Integer.toString(m.Statistics.Collections);
                            break;
                        case 5:
                            item.info = Integer.toString(m.Statistics.LovedMottos);
                            break;
                        case 6:
                            item.info = Integer.toString(m.Statistics.LovedCollections);
                            break;
                        case 7:
                            item.info = Integer.toString(m.Statistics.Follows);
                            break;
                        case 8:
                            item.info = Integer.toString(m.Statistics.Followers);
                            break;
                    }

                }

                mAdapter.notifyDataSetChanged();

                DisplayImageOptions options = new DisplayImageOptions.Builder()
                        .showImageOnLoading(R.drawable.ic_person_white)
                        .showImageForEmptyUri(R.drawable.ic_person_white)
                        .showImageOnFail(R.drawable.ic_person_white)
                        .cacheInMemory(true)
                        .cacheOnDisk(true)
                        .considerExifParams(true)
                        .displayer(new CircleBitmapDisplayer(Color.WHITE, 1))
                        .build();

                lblUser.setText(uname);
                ImageLoader.getInstance().displayImage(m.Thumb, imgThumb, options);
                switch (m.Sex){
                    case 1:
                        imgSex.setImageResource(R.drawable.ic_female);

                        imgSex.setVisibility(View.VISIBLE);
                        break;
                    case 2:
                        imgSex.setImageResource(R.drawable.ic_male);
                        imgSex.setVisibility(View.VISIBLE);
                        break;
                    default:
                        imgSex.setVisibility(View.GONE);
                        break;
                }

                setRelation(m.RelationState);
            }
        }

    }

    private void setRelation(int relation){
        if(relation == 3){
            btnRelation.setImageResource(R.drawable.ic_relation_heart_pulse_white);
        }else if( (relation &1) == 1){
            btnRelation.setImageResource(R.drawable.ic_relation_heart_white);
        }else if((relation & 4) == 4){
            btnRelation.setImageResource(R.drawable.ic_relation_heart_broken_white);
        }else{
            btnRelation.setImageResource(R.drawable.ic_relation_heart_outline_white);
        }

        btnRelation.setVisibility(View.VISIBLE);
    }

    public void onLoveAction() {
        if(uModel==null){
            return;
        }

        if(!ImottoApplication.getInstance().isUserLogin()){
            Toast.makeText(this, "请先登录后再添加喜欢的人", Toast.LENGTH_SHORT).show();
            return;
        }

        UserModel model = uModel;

        String relation="你想要将TA：";
        if (model.RelationState == 1){
            relation ="TA是你喜欢的人，你想要将TA：";
        }else if(model.RelationState == 2){
            relation="TA喜欢你，你想要将TA：";
        }else if(model.RelationState == 3){
            relation = "你和TA互相喜欢，你想要将TA：";
        }else if(model.RelationState == 4){
            relation = "TA在你的黑名单里，你想要将TA：";
        }
        else if(model.RelationState == 6){
            relation="TA喜欢你，却呆在你的黑名单里，你想要将TA：";
        }

        ActionSheet menuView = new ActionSheet(this);
        menuView.setTitle(relation);
        menuView.setCancelButtonTitle("取消");// before add items

        if((model.RelationState & 1) == 1){
            menuView.addItems("移出喜欢的人", "加入黑名单");
            currentActionSheet = ACTION_SHEET_UNLOVE_AND_BAN;
        }else if((model.RelationState & 4) == 4){
            menuView.addItems("添加为喜欢的人", "移出黑名单");
            currentActionSheet = ACTION_SHEET_LOVE_AND_UNBAN;
        }else{
            menuView.addItems("添加为喜欢的人", "加入黑名单");
            currentActionSheet = ACTION_SHEET_LOVE_AND_BAN;
        }


        menuView.setItemClickListener(this);
        menuView.setCancelableOnTouchMenuOutside(true);
        menuView.showMenu();

    }

    @Override
    public void onItemClick(int itemPosition) {
        if(uModel == null){
            return;
        }

        if((currentActionSheet == ACTION_SHEET_LOVE_AND_BAN && itemPosition == 0)
                ||(currentActionSheet == ACTION_SHEET_LOVE_AND_UNBAN && itemPosition == 0)) {
            //love user

            final ProgressDialog dialog = ProgressDialog.show(this, null, "正在加载...", true, false);
            ImottoApi.getInstance().followUser(uModel.Id, new ImottoApi.InvokeCompletionHandler<ApiResp>() {
                @Override
                public void onApiCallCompletion(ApiResp result) {
                    dialog.dismiss();
                    if(result.isSuccess()){
                        refreshData();
                    }else {
                        Toast.makeText(UserInfoActivity.this, result.Msg, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public Class<ApiResp> getGenericClass() {
                    return ApiResp.class;
                }
            });


        }else if((currentActionSheet == ACTION_SHEET_LOVE_AND_BAN && itemPosition == 1)
                ||(currentActionSheet == ACTION_SHEET_UNLOVE_AND_BAN && itemPosition == 1)){
            //ban user

            final ProgressDialog dialog = ProgressDialog.show(this, null, "正在加载...", true, false);
            ImottoApi.getInstance().addBanUser(uModel.Id, new ImottoApi.InvokeCompletionHandler<ApiResp>() {
                @Override
                public void onApiCallCompletion(ApiResp result) {
                    dialog.dismiss();
                    if(result.isSuccess()){
                        refreshData();
                    }else{
                        Toast.makeText(UserInfoActivity.this, result.Msg, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public Class<ApiResp> getGenericClass() {
                    return ApiResp.class;
                }
            });


        }else if(currentActionSheet == ACTION_SHEET_LOVE_AND_UNBAN && itemPosition == 1){
            //unban user

            final ProgressDialog dialog = ProgressDialog.show(this, null, "正在加载...", true, false);
            ImottoApi.getInstance().removeBanUser(uModel.Id, new ImottoApi.InvokeCompletionHandler<ApiResp>() {
                @Override
                public void onApiCallCompletion(ApiResp result) {
                    dialog.dismiss();
                    if(result.isSuccess()){
                        refreshData();
                    }else{
                        Toast.makeText(UserInfoActivity.this,result.Msg, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public Class<ApiResp> getGenericClass() {
                    return ApiResp.class;
                }
            });

        }else if(currentActionSheet == ACTION_SHEET_UNLOVE_AND_BAN && itemPosition == 0) {
            //unlove user

            final ProgressDialog dialog = ProgressDialog.show(this, null, "正在加载...", true, false);
            ImottoApi.getInstance().unfollowUser(uModel.Id, new ImottoApi.InvokeCompletionHandler<ApiResp>() {
                @Override
                public void onApiCallCompletion(ApiResp result) {
                    dialog.dismiss();
                    if(result.isSuccess()){
                        refreshData();
                    }else{
                        Toast.makeText(UserInfoActivity.this, result.Msg, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public Class<ApiResp> getGenericClass() {
                    return ApiResp.class;
                }
            });
        }
    }


    private List<ProfilesAdapter.ProfileItem> initData(){

        if(mData == null) {
            List<ProfilesAdapter.ProfileItem> data = new ArrayList<>();

            data.add(new ProfilesAdapter.ProfileItem(0,"","",0));
            data.add(new ProfilesAdapter.ProfileItem(1, "发送消息给他", "", R.drawable.ic_send_msg));
            data.add(new ProfilesAdapter.ProfileItem(0,"","",0));

            data.add(new ProfilesAdapter.ProfileItem(2, "TA的积分", "", R.drawable.ic_chart));
            data.add(new ProfilesAdapter.ProfileItem(0,"","",0));

            data.add(new ProfilesAdapter.ProfileItem(3, "TA的偶得", "", R.drawable.ic_document));
            data.add(new ProfilesAdapter.ProfileItem(4, "TA的珍藏", "", R.drawable.ic_library_book));
            data.add(new ProfilesAdapter.ProfileItem(0,"","",0));

            data.add(new ProfilesAdapter.ProfileItem(5, "TA喜欢的偶得", "", R.drawable.ic_document));
            data.add(new ProfilesAdapter.ProfileItem(6, "TA喜欢的珍藏", "", R.drawable.ic_library_book));
            data.add(new ProfilesAdapter.ProfileItem(0,"","",0));


            data.add(new ProfilesAdapter.ProfileItem(7, "TA喜欢的人", "", R.drawable.ic_account_star_variant));
            data.add(new ProfilesAdapter.ProfileItem(8, "喜欢TA的人", "", R.drawable.ic_account_star));
            data.add(new ProfilesAdapter.ProfileItem(0,"","",0));

            mData = data;
        }

        return mData;

    }



    private ImottoApi.InvokeCompletionHandler<ReadUserResp> readUserRespInvokeCompletionHandler = new ImottoApi.InvokeCompletionHandler<ReadUserResp>() {
        @Override
        public void onApiCallCompletion(ReadUserResp result) {
            processReadResult(result);
        }

        @Override
        public Class<ReadUserResp> getGenericClass() {
            return ReadUserResp.class;
        }
    };
}
