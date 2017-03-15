package net.imotto.imottoapp.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bartoszlipinski.recyclerviewheader2.RecyclerViewHeader;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.CircleBitmapDisplayer;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import net.imotto.imottoapp.ImottoApplication;
import net.imotto.imottoapp.R;
import net.imotto.imottoapp.activities.AwardsActivity;
import net.imotto.imottoapp.activities.LoginActivity;
import net.imotto.imottoapp.activities.MyGiftsActivity;
import net.imotto.imottoapp.activities.RegisterActivity;
import net.imotto.imottoapp.activities.SettingActivity;
import net.imotto.imottoapp.activities.ShowWebActivity;
import net.imotto.imottoapp.activities.UserAlbumActivity;
import net.imotto.imottoapp.activities.UserBanActivity;
import net.imotto.imottoapp.activities.UserFollowersActivity;
import net.imotto.imottoapp.activities.UserFollowsActivity;
import net.imotto.imottoapp.activities.UserMottoActivity;
import net.imotto.imottoapp.activities.UserRevenueActivity;
import net.imotto.imottoapp.activities.UserScoreActivity;
import net.imotto.imottoapp.adapters.ProfilesAdapter;
import net.imotto.imottoapp.adapters.ProfilesAdapter.ProfileItem;
import net.imotto.imottoapp.adapters.RecyclerViewAdapter;
import net.imotto.imottoapp.services.ImottoApi;
import net.imotto.imottoapp.services.models.ApiResp;
import net.imotto.imottoapp.services.models.ReadUserResp;
import net.imotto.imottoapp.services.models.UserModel;
import net.imotto.imottoapp.utils.Constants;
import net.imotto.imottoapp.utils.PreferencesHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by sunht on 16/10/26.
 *
 */

public class ProfileFragment extends BaseFragment implements Observer {
    private static final String TAG = "ProfileFragment";
    public static final int REQUEST_CODE_LOGIN = 101;
    private SwipeRefreshLayout mSwipeRefresh;
    private RecyclerView mRecyclerView;
    private List<ProfileItem> mData;
    private ProfilesAdapter mAdapter;
    private RecyclerViewHeader mHeader;
    private String uthumb;
    private int usex;

    private ImageView imgThumb;
    private ImageView imgSex;
    private TextView btnRegister;
    private TextView btnLogin;
    private TextView lblUser;
    private RelativeLayout viewActionContainer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        mSwipeRefresh = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        mHeader = (RecyclerViewHeader) rootView.findViewById(R.id.recycler_header);
        imgThumb = (ImageView) rootView.findViewById(R.id.img_thumb);
        imgSex = (ImageView) rootView.findViewById(R.id.img_sex);
        btnLogin = (TextView) rootView.findViewById(R.id.btn_login);
        btnRegister = (TextView) rootView.findViewById(R.id.btn_register);
        lblUser = (TextView) rootView.findViewById(R.id.lbl_user);
        viewActionContainer = (RelativeLayout) rootView.findViewById(R.id.action_container);

        initView();

        initEvent();
        return rootView;
    }

    private void initView(){
        initData();

        Log.i(TAG, "ProfileFragment initView();");
        mSwipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.i(TAG,"onRefresh.");
                getData();
            }
        });


        mRecyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getContext())
                .colorResId(R.color.colorLightDivider)
                .margin(120, 0)
                .sizeResId(R.dimen.recycler_view_divider).build());
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(layoutManager);
        mHeader.attachTo(mRecyclerView);
        mAdapter = new ProfilesAdapter(getContext(), mData);
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

        refreshData();
    }

    private void initEvent(){
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoRegister();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                gotoLogin();
            }
        });

        imgThumb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setupProfile();
            }
        });
    }

    private void setupProfile(){
        if(ImottoApplication.getInstance().isUserLogin()) {
            Intent intent = new Intent(this.getContext(), SettingActivity.class);
            Bundle bundle = new Bundle();
            if(uthumb!=null) {
                bundle.putString(Constants.BUNDLE_UTHUMB, uthumb);
            }else{
                bundle.putString(Constants.BUNDLE_UTHUMB, "");
            }
            bundle.putInt(Constants.BUNDLE_USEX, usex);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }

    private void refreshData(){
        mSwipeRefresh.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefresh.setRefreshing(true);
                getData();
            }
        });
    }


    private void gotoLogin(){
        Intent intent = new Intent(getContext(), LoginActivity.class);
        startActivityForResult(intent, REQUEST_CODE_LOGIN);
    }

    private void gotoRegister(){
        Intent intent = new Intent(getContext(), RegisterActivity.class);
        startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_LOGIN){
            //use observer instead
            //refreshData();
        }
    }

    private void getData(){

        if(ImottoApplication.getInstance().isUserLogin()){
            viewActionContainer.setVisibility(View.GONE);
            lblUser.setVisibility(View.VISIBLE);
            lblUser.setText(ImottoApplication.getInstance().getUserName());
            ImottoApi.getInstance().readUserInfo(ImottoApplication.getInstance().getUserId(),
                    readUserRespInvokeCompletionHandler);
        }else{
            viewActionContainer.setVisibility(View.VISIBLE);
            lblUser.setVisibility(View.GONE);
            imgThumb.setImageResource(R.drawable.ic_person_white);
            imgSex.setVisibility(View.GONE);
            mData.get(1).info = "";
            mData.get(2).info = "";
            mData.get(4).info = "";
            mData.get(5).info = "";
            mData.get(7).info = "";
            mData.get(8).info = "";
            mData.get(9).info = "";

            mAdapter.notifyDataSetChanged();
            mSwipeRefresh.setRefreshing(false);
        }
    }

    public void handleItemClick(int position){

        ProfileItem item = mAdapter.getDataItem(position);

        switch (item.id){
            case 1: //积分
                if(ImottoApplication.getInstance().isUserLogin()) {
                    Intent intent = new Intent(getContext(), UserScoreActivity.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(getContext(), "请登录后查看", Toast.LENGTH_SHORT).show();
                }
                break;
            case 2: //金币
                if(ImottoApplication.getInstance().isUserLogin()) {
                    Intent intent = new Intent(getContext(), UserRevenueActivity.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(getContext(), "请登录后查看", Toast.LENGTH_SHORT).show();
                }
                break;
            case 3: //偶得
                if(ImottoApplication.getInstance().isUserLogin()) {
                    Intent intent = new Intent(getContext(), UserMottoActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString(Constants.BUNDLE_UID, ImottoApplication.getInstance().getUserId());
                    bundle.putString(Constants.BUNDLE_UNAME, "我");
                    intent.putExtras(bundle);
                    startActivity(intent);
                }else{
                    Toast.makeText(getContext(), "请登录后查看", Toast.LENGTH_SHORT).show();
                }
                break;
            case 4: //珍藏
                if(ImottoApplication.getInstance().isUserLogin()) {
                    Intent intent = new Intent(getContext(), UserAlbumActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString(Constants.BUNDLE_UID, ImottoApplication.getInstance().getUserId());
                    bundle.putString(Constants.BUNDLE_UNAME, "我");
                    intent.putExtras(bundle);
                    startActivity(intent);
                }else{
                    Toast.makeText(getContext(), "请登录后查看", Toast.LENGTH_SHORT).show();
                }
                break;
            case 5: //我喜欢的人
                if(ImottoApplication.getInstance().isUserLogin()) {
                    Intent intent = new Intent(getContext(), UserFollowsActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString(Constants.BUNDLE_UID, ImottoApplication.getInstance().getUserId());
                    bundle.putString(Constants.BUNDLE_UNAME, "我");
                    intent.putExtras(bundle);
                    startActivity(intent);
                }else{
                    Toast.makeText(getContext(), "请登录后查看", Toast.LENGTH_SHORT).show();
                }
                break;
            case 6: //喜欢我的人
                if(ImottoApplication.getInstance().isUserLogin()) {
                    Intent intent = new Intent(getContext(), UserFollowersActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString(Constants.BUNDLE_UID, ImottoApplication.getInstance().getUserId());
                    bundle.putString(Constants.BUNDLE_UNAME, "我");
                    intent.putExtras(bundle);
                    startActivity(intent);
                }else{
                    Toast.makeText(getContext(), "请登录后查看", Toast.LENGTH_SHORT).show();
                }
                break;
            case 7: //不喜欢的人
                if(ImottoApplication.getInstance().isUserLogin()) {
                    Intent intent = new Intent(getContext(), UserBanActivity.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(getContext(), "请登录后查看", Toast.LENGTH_SHORT).show();
                }
                break;
            case 8: //邀请好友
                shareMsg("邀请好友", "App推荐","这个叫【偶得】的App有点意思，来一起玩吧！[ https://www.imotto.net ]", null);
                break;
            case 9: //帮助中心
                Intent intentWeb = new Intent(getContext(), ShowWebActivity.class);
                intentWeb.putExtra(Constants.BUNDLE_ACT_TITLE, getResources().getString(R.string.title_help_center));
                intentWeb.putExtra(Constants.BUNDLE_URL, getResources().getString(R.string.url_help_center));
                startActivity(intentWeb);
                break;
            case 10: //退出登录
                doLogout();
                break;
            case 11:// 礼品
                if(ImottoApplication.getInstance().isUserLogin()) {
                    Intent intent = new Intent(getContext(), MyGiftsActivity.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(getContext(), "请登录后查看", Toast.LENGTH_SHORT).show();
                }
                break;
            case 12: //奖品
                if(ImottoApplication.getInstance().isUserLogin()) {
                    Intent intentAward = new Intent(getContext(), AwardsActivity.class);
                    startActivity(intentAward);
                }else{
                    Toast.makeText(getContext(), "请登录后查看", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void doLogout(){
        ImottoApi.getInstance().userLogout(new ImottoApi.InvokeCompletionHandler<ApiResp>() {
            @Override
            public void onApiCallCompletion(ApiResp result) {
                handleLogoutResult(result);
            }

            @Override
            public Class<ApiResp> getGenericClass() {
                return ApiResp.class;
            }
        });
    }

    private void handleLogoutResult(ApiResp result){
        if(result.isSuccess()) {
            Context context = getContext();
            Toast.makeText(context, "可惜不是你，陪我到最后~", Toast.LENGTH_SHORT).show();
            PreferencesHelper.removeKey(context, Constants.PREFS_USERTOKEN);

            //设置Jpush别名
            JPushInterface.setAlias(context, "", null);

            ImottoApplication.getInstance().notifyUserChanged();
        }else{
            Toast.makeText(getContext(), result.Msg, Toast.LENGTH_SHORT).show();
        }
    }


    private void processReadResult(ReadUserResp result){
        mSwipeRefresh.setRefreshing(false);

        if(result.isSuccess()){
            if(result.Data != null){
                UserModel m = result.Data;
                uthumb = m.Thumb;
                usex = m.Sex;

                for(ProfileItem item : mData)
                {
                    switch (item.id)
                    {
                        case 1:
                            item.info = Integer.toString(m.Statistics.Revenue);
                            break;
                        case 2:
                            item.info = Integer.toString(m.Statistics.Balance);
                            break;
                        case 3:
                            item.info = Integer.toString(m.Statistics.Mottos);
                            break;
                        case 4:
                            item.info = Integer.toString(m.Statistics.Collections);
                            break;
                        case 5:
                            item.info = Integer.toString(m.Statistics.Follows);
                            break;
                        case 6:
                            item.info = Integer.toString(m.Statistics.Followers);
                            break;
                        case 7:
                            item.info = Integer.toString(m.Statistics.Bans);
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

            }
        }

    }

    private List<ProfileItem> initData(){

        if(mData == null) {
            List<ProfileItem> data = new ArrayList<>();

            data.add(new ProfileItem(0,"","",0));
            data.add(new ProfileItem(1, "我的积分", "", R.drawable.ic_chart));
//            data.add(new ProfileItem(2, "我的金币", "", R.drawable.ic_receipt));
//            data.add(new ProfileItem(11,"我的礼品", "", R.drawable.icon_gift));
            data.add(new ProfileItem(12,"我的奖品", "", R.drawable.icon_gift));
            data.add(new ProfileItem(0,"","",0));

            data.add(new ProfileItem(3, "我的偶得", "", R.drawable.ic_document));
            data.add(new ProfileItem(4, "我的珍藏", "", R.drawable.ic_library_book));
            data.add(new ProfileItem(0,"","",0));

            data.add(new ProfileItem(5, "我喜欢的人", "", R.drawable.ic_account_star_variant));
            data.add(new ProfileItem(6, "喜欢我的人", "", R.drawable.ic_account_star));
            data.add(new ProfileItem(7, "黑名单", "", R.drawable.ic_account_alert));
            data.add(new ProfileItem(0,"","",0));

            data.add(new ProfileItem(8, "邀请好友", "", R.drawable.ic_share_variant));
            data.add(new ProfileItem(9, "关于偶得", "", R.drawable.ic_help_outline));
            data.add(new ProfileItem(0,"","",0));

            data.add(new ProfileItem(10, "退出登录", "", R.drawable.ic_logout_variant_red));
            data.add(new ProfileItem(0,"","",0));

            mData = data;
        }

        return mData;

    }

    @Override
    public Toolbar getSpecialToolbar() {
        if (mSpecialToolbar == null) {
            mSpecialToolbar = (Toolbar) getActivity().getLayoutInflater().inflate(R.layout.toolbar_profile, null);

            ImageButton btnLoved = (ImageButton) mSpecialToolbar.findViewById(R.id.toolbar_right_btn);
            btnLoved.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setupProfile();
                }
            });

        }

        return mSpecialToolbar;
    }

    @Override
    public void update(Observable o, Object arg) {
        refreshData();
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


    public void shareMsg(String activityTitle, String msgTitle, String msgText,
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
}
