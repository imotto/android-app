package net.imotto.imottoapp.activities;

import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.readystatesoftware.systembartint.SystemBarTintManager;

import net.imotto.imottoapp.R;
import net.imotto.imottoapp.services.ImottoApi;
import net.imotto.imottoapp.services.models.AcquireVerifyCodeResp;
import net.imotto.imottoapp.services.models.ApiResp;
import net.imotto.imottoapp.utils.CommUtils;
import net.imotto.imottoapp.utils.Constants;

/**
 * Created by annda on 2016/12/1.
 */

public class ResetPasswordActivity extends BaseBackableActivity {
    private EditText mTxtMobile;
    private EditText mTxtVCode;
    private EditText mTxtPassword;
    private TextView mLblSendVCode;
    private TimeCount mTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
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

        mTimer = new TimeCount(60000, 1000);
        mTxtMobile = (EditText) findViewById(R.id.txt_mobile);
        mTxtPassword = (EditText) findViewById(R.id.txt_password);
        mTxtVCode = (EditText) findViewById(R.id.txt_vcode);

        mLblSendVCode = (TextView) findViewById(R.id.lbl_send_vcode);
        mLblSendVCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendVCode();
            }
        });

        Button btnConfirm = (Button) findViewById(R.id.btn_ok);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doResetPassword();
            }
        });

        setupToolBar("重置登录密码");
    }

    private void sendVCode(){
        String mobile = mTxtMobile.getText().toString();
        if (mobile.length()<1){
            Toast.makeText(this, "请输入手机号码", Toast.LENGTH_SHORT).show();
            mTxtMobile.requestFocus();
            return;
        }

        if(!CommUtils.isMobileNO(mobile)){
            Toast.makeText(this, "请输入正确的手机号码", Toast.LENGTH_SHORT).show();
            mTxtMobile.requestFocus();
            return;
        }

        final ProgressDialog dialog = ProgressDialog.show(this, null, "正在加载...", true, false);
        ImottoApi.getInstance().acquireVerifyCode(mobile, Constants.OPCODE_ACQUIRE_FIND_PASS_CODE,
                new ImottoApi.InvokeCompletionHandler<AcquireVerifyCodeResp>() {
                    @Override
                    public void onApiCallCompletion(AcquireVerifyCodeResp result) {
                        dialog.dismiss();
                        if(result.isSuccess()){
                            mTimer.start();
                        }else{
                            Toast.makeText(ResetPasswordActivity.this, result.Msg, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public Class<AcquireVerifyCodeResp> getGenericClass() {
                        return AcquireVerifyCodeResp.class;
                    }
                });

    }

    private void doResetPassword(){
        String mobile = mTxtMobile.getText().toString();
        String vcode = mTxtVCode.getText().toString();
        String password = mTxtPassword.getText().toString();

        if (mobile.length()<1){
            Toast.makeText(this, "请输入手机号码", Toast.LENGTH_SHORT).show();
            mTxtMobile.requestFocus();
            return;
        }

        if(!CommUtils.isMobileNO(mobile)){
            Toast.makeText(this, "请输入正确的手机号码", Toast.LENGTH_SHORT).show();
            mTxtMobile.requestFocus();
            return;
        }

        if(vcode.length()!=6){
            Toast.makeText(this,"请输入您接收到的验证码", Toast.LENGTH_SHORT).show();
            mTxtVCode.requestFocus();
            return;
        }

        if(password.length()<6){
            Toast.makeText(this,"密码不能少于6个字符", Toast.LENGTH_SHORT).show();
            mTxtPassword.requestFocus();
            return;
        }

        final ProgressDialog dialog = ProgressDialog.show(this, null, "正在加载...", true, false);
        ImottoApi.getInstance().resetPassword(mobile, vcode, password, new ImottoApi.InvokeCompletionHandler<ApiResp>() {
            @Override
            public void onApiCallCompletion(ApiResp result) {
                dialog.dismiss();
                if(result.isSuccess()){
                    Toast.makeText(ResetPasswordActivity.this, "密码已重置，现在使用您的新密码登录吧", Toast.LENGTH_SHORT).show();
                    goBack();
                }else{
                    Toast.makeText(ResetPasswordActivity.this, result.Msg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public Class<ApiResp> getGenericClass() {
                return ApiResp.class;
            }
        });
    }

    class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {// 计时完毕
            mLblSendVCode.setText("发送验证码到手机");
            mLblSendVCode.setClickable(true);
        }

        @Override
        public void onTick(long millisUntilFinished) {// 计时过程
            mLblSendVCode.setClickable(false);//防止重复点击
            mLblSendVCode.setText(""+(millisUntilFinished / 1000) + "秒后重新发送");
        }
    }
}
