package net.imotto.imottoapp.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import net.imotto.imottoapp.ImottoApplication;
import net.imotto.imottoapp.R;
import net.imotto.imottoapp.services.ImottoApi;
import net.imotto.imottoapp.services.models.ApiResp;
import net.imotto.imottoapp.services.models.AwardModel;
import net.imotto.imottoapp.services.models.AwardeeModel;
import net.imotto.imottoapp.services.models.PEResultModel;
import net.imotto.imottoapp.services.models.ReadAwardeeResp;
import net.imotto.imottoapp.utils.CommUtils;
import net.imotto.imottoapp.utils.Constants;

import java.util.List;

/**
 *
 * Created by sunht on 2017/1/12.
 */

public class AwardDetailActivity extends BaseBackableActivity {
    private static final int REQUEST_CODE_SET_ADDRESS = 109;
    private AwardModel model;
    private TextView lblGainStatus;
    private TextView btnAction;
    private TextView lblSummary;
    private TextView lblTitle;
    private TextView btnShowRank;
    private TextView lblAwardees;
    private View wrapperAwardees;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_award_detail);
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
        String json = bundle.getString(Constants.BUNDLE_JSON_AWARD);

        model = CommUtils.fromJson(json, AwardModel.class);

        setupToolBar(String.format("%d年第%d期奖品", model.ID/100, model.ID%100));

        setupView();


    }

    private void setupView(){

        btnAction = (TextView) findViewById(R.id.btn_action);



        lblTitle = (TextView) findViewById(R.id.lbl_title);
        lblTitle.setText(model.Name);

        lblSummary = (TextView) findViewById(R.id.lbl_info);
        lblSummary.setText(model.Summary);

        lblAwardees = (TextView) findViewById(R.id.lbl_awardees);
        wrapperAwardees = findViewById(R.id.wrapper_awardees);
        btnShowRank = (TextView) findViewById(R.id.btn_show_rank);
        lblGainStatus = (TextView) findViewById(R.id.lbl_gain_status);

        if(model.Status == 0){
            lblAwardees.setText("本期评选正在进行中...");
            wrapperAwardees.setVisibility(View.GONE);
            btnShowRank.setVisibility(View.GONE);

            lblGainStatus.setText("评估中...");
        }else{
            lblAwardees.setText("本期评选结束，以下用户获得此奖品:");
            wrapperAwardees.setVisibility(View.VISIBLE);
            btnShowRank.setVisibility(View.VISIBLE);
            btnShowRank.setText(String.format("查看%d年%d月积分排行榜", model.ID/100, model.ID%100));

            btnShowRank.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showRank();
                }
            });

            //0:未获得 ,1：待确认地址，2: 待发放,3: 已发放, 4:已签收
            if(model.GainStatus == 0){
                lblGainStatus.setText("抱歉，您未获得本期奖品。");
                btnAction.setVisibility(View.GONE);
            }else if(model.GainStatus == 1) {
                lblGainStatus.setText("恭喜，您已获得本期奖品。");
                btnAction.setVisibility(View.VISIBLE);
                btnAction.setText("填写寄送地址");

                btnAction.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selAddress();
                    }
                });

            }else if (model.GainStatus == 2){
                lblGainStatus.setText("恭喜，您已获得本期奖品。(待寄出)");
                btnAction.setVisibility(View.GONE);
            }else if(model.GainStatus == 3){
                lblGainStatus.setText("恭喜，您已获得本期奖品。(已寄出)");
                btnAction.setVisibility(View.VISIBLE);
                btnAction.setText("奖品签收");

                btnAction.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        confirmReceive();
                    }
                });

            }else if(model.GainStatus == 4){
                lblGainStatus.setText("恭喜，您已获得本期奖品。(已签收)");
                btnAction.setVisibility(View.GONE);
            }

            loadAwardees();

        }

        ImageView imgThumb = (ImageView) findViewById(R.id.img_thumb);
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.ic_gift)
                .showImageForEmptyUri(R.drawable.ic_gift)
                .showImageOnFail(R.drawable.ic_gift)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                //.displayer()
                .build();

        ImageLoader.getInstance().displayImage(model.Img, imgThumb, options);

    }

    private void selAddress(){
        Intent intent = new Intent(this, SelAddressActivity.class);
        intent.putExtra(Constants.BUNDLE_AWARD_ID, model.ID);
        startActivityForResult(intent, REQUEST_CODE_SET_ADDRESS);
    }

    private void confirmReceive(){
        new AlertDialog.Builder(this).setTitle("奖品签收").setMessage("确认已经收到奖品了?")
                .setPositiveButton("是的", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        doReceiveAward();
                    }
                }).setNegativeButton("还没有", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(AwardDetailActivity.this, "那就再等等吧,应该快到了.", Toast.LENGTH_SHORT).show();
            }
        }).show();
    }

    private void doReceiveAward(){

        final ProgressDialog dialog = ProgressDialog.show(this,"正在提交...","");

        ImottoApi.getInstance().receiveAward(model.ID, new ImottoApi.InvokeCompletionHandler<ApiResp>() {
            @Override
            public void onApiCallCompletion(ApiResp result) {
                dialog.dismiss();
                if(result.isSuccess()){
                    model.GainStatus = 4;
                    lblGainStatus.setText("恭喜，您已获得本期奖品。(已签收)");
                    btnAction.setVisibility(View.GONE);
                    //设置 result 以更新奖品列表状态
                    Intent intent = new Intent();
                    String json = CommUtils.toJson(model);
                    intent.putExtra(Constants.BUNDLE_JSON_AWARD, json);
                    setResult(RESULT_OK, intent);

                }else{
                    Toast.makeText(AwardDetailActivity.this, result.Msg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public Class<ApiResp> getGenericClass() {
                return ApiResp.class;
            }
        });
    }

    private void showRank(){
        Intent intent = new Intent(this, RankBoardActivity.class);
        intent.putExtra(Constants.BUNDLE_THEMONTH, model.ID);

        startActivity(intent);
    }

    private void loadAwardees(){
        ImottoApi.getInstance().readAwardees(model.ID, new ImottoApi.InvokeCompletionHandler<ReadAwardeeResp>() {
            @Override
            public void onApiCallCompletion(ReadAwardeeResp result) {
                if(result.isSuccess()){
                    setAwardees(result.Data);
                }
            }

            @Override
            public Class<ReadAwardeeResp> getGenericClass() {
                return ReadAwardeeResp.class;
            }
        });
    }

    private void setAwardees(List<AwardeeModel> awardees){
        int idx = 1;
        for(AwardeeModel m: awardees){
            setAwardeeInfo(m, idx);
            idx++;
        }
    }

    private void setAwardeeInfo(final AwardeeModel m, int idx){
        ImageView imageView = null;
        switch (idx){
            case 1:
                imageView = (ImageView) findViewById(R.id.img_awardee_1);
                break;
            case 2:
                imageView = (ImageView) findViewById(R.id.img_awardee_2);
                break;
            case 3:
                imageView = (ImageView) findViewById(R.id.img_awardee_3);
                break;
            case 4:
                imageView = (ImageView) findViewById(R.id.img_awardee_4);
                break;
            case 5:
                imageView = (ImageView) findViewById(R.id.img_awardee_5);
                break;
        }

        if(imageView != null){
            ImageLoader.getInstance().displayImage(m.UserThumb, imageView, CommUtils.getDisplayImageOptions());
            if(!m.UID.equals(ImottoApplication.getInstance().getUserId())){
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(AwardDetailActivity.this, UserInfoActivity.class);

                        Bundle bundle = new Bundle();
                        bundle.putString(Constants.BUNDLE_UID, m.UID);
                        bundle.putString(Constants.BUNDLE_UNAME, m.UserName);
                        intent.putExtras(bundle);

                        startActivity(intent);
                    }
                });
            }
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_CODE_SET_ADDRESS){
            if(RESULT_OK == resultCode){
                model.GainStatus = 2;
                lblGainStatus.setText("恭喜，您已获得本期奖品。（奖品待发放）");
                btnAction.setVisibility(View.GONE);
                //设置 result 以更新奖品列表状态
                Intent intent = new Intent();
                String json = CommUtils.toJson(model);
                intent.putExtra(Constants.BUNDLE_JSON_AWARD, json);
                setResult(RESULT_OK, intent);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


}
