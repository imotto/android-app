package net.imotto.imottoapp.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.readystatesoftware.systembartint.SystemBarTintManager;

import net.imotto.imottoapp.ImottoApplication;
import net.imotto.imottoapp.R;
import net.imotto.imottoapp.services.ImottoApi;
import net.imotto.imottoapp.services.models.ApiResp;
import net.imotto.imottoapp.services.models.NoticeModel;
import net.imotto.imottoapp.utils.CommUtils;
import net.imotto.imottoapp.utils.Constants;
import net.imotto.imottoapp.utils.DateHelper;

/**
 * Created by sunht on 2016/12/12.
 */

public class NoticeDetailActivity extends BaseBackableActivity {
    private static final String TAG="NoticeDetailAct";
    private NoticeModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_detail);
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
        String json = bundle.getString(Constants.BUNDLE_JSON_NOTICE);
        model = CommUtils.fromJson(json, NoticeModel.class);

        TextView lblTitle = (TextView) findViewById(R.id.lbl_title);
        lblTitle.setText(model.Title);

        TextView lblStatus = (TextView) findViewById(R.id.lbl_status);
        lblStatus.setText(model.State == 1? "已读":"未读");

        ImageView imgThumb = (ImageView) findViewById(R.id.img_thumb);
        imgThumb.setImageResource(model.State == 1? R.drawable.ic_email_open:R.drawable.ic_email);

        TextView lblTime = (TextView) findViewById(R.id.lbl_time);
        lblTime.setText(DateHelper.friendlyTime(model.SendTime));

        TextView lblContent = (TextView) findViewById(R.id.lbl_content);
        lblContent.setText(model.Content);

        setupLinkLabel();

        setupToolBar("系统提醒");

        if(model.State == 0) {
            beginSetNoticeStatus();
        }
    }

    private void setupLinkLabel() {
        TextView lblLink = (TextView) findViewById(R.id.lbl_link);
        switch (model.Type)
        {
            //0:系统通知，1:关注消息，2：偶得消息，3：珍藏消息，4：礼品发放
            case 0:
                lblLink.setText("查看我的积分记录");
                lblLink.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(NoticeDetailActivity.this, UserScoreActivity.class);
                        startActivity(intent);
                    }
                });
                break;
            case 1:
                lblLink.setText("查看["+model.TargetInfo+"]用户详细信息");
                lblLink.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(model.TargetID.equals(ImottoApplication.getInstance().getUserId())){
                            return;
                        }
                        Intent intent = new Intent(NoticeDetailActivity.this, UserInfoActivity.class);

                        Bundle bundle = new Bundle();
                        bundle.putString(Constants.BUNDLE_UID, model.TargetID);
                        bundle.putString(Constants.BUNDLE_UNAME, model.TargetInfo);
                        intent.putExtras(bundle);

                        startActivity(intent);
                    }
                });
                break;
            case 4:
                lblLink.setText("查看我的礼品");
                lblLink.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(NoticeDetailActivity.this, MyGiftsActivity.class);
                        startActivity(intent);
                    }
                });
                break;
        }
    }

    private void beginSetNoticeStatus(){
        ImottoApi.getInstance().setNoticeRead(model.ID, new ImottoApi.InvokeCompletionHandler<ApiResp>() {
            @Override
            public void onApiCallCompletion(ApiResp result) {
                processResult(result);
            }

            @Override
            public Class<ApiResp> getGenericClass() {
                return ApiResp.class;
            }
        });

    }

    private void processResult(ApiResp result){
        if (result.isSuccess()){
            Intent intent = new Intent();
            intent.putExtra(Constants.BUNDLE_NOTICE_ID, model.ID);
            setResult(RESULT_OK, intent);
        }
    }


}
