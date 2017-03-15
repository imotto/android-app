package net.imotto.imottoapp.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.readystatesoftware.systembartint.SystemBarTintManager;

import net.imotto.imottoapp.ImottoApplication;
import net.imotto.imottoapp.R;
import net.imotto.imottoapp.fragments.ProfileFragment;
import net.imotto.imottoapp.services.ImottoApi;
import net.imotto.imottoapp.services.models.LoginResp;
import net.imotto.imottoapp.utils.CommUtils;
import net.imotto.imottoapp.utils.Constants;
import net.imotto.imottoapp.utils.Encryptor;
import net.imotto.imottoapp.utils.PreferencesHelper;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by sunht on 16/11/3.
 *
 */

public class LoginActivity extends BaseBackableActivity {
    private static final String TAG = "LoginActivity";
    private EditText mTxtMobile;
    private EditText mTxtPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
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

        mTxtMobile = (EditText) findViewById(R.id.txt_mobile);
        mTxtPassword = (EditText) findViewById(R.id.txt_password);

        TextView lblRegister = (TextView) findViewById(R.id.lbl_register_now);
        lblRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goRegister();
            }
        });

        Button btnLogin = (Button) findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(new BtnLoginClickListener());

        TextView lblForget = (TextView) findViewById(R.id.lbl_forget);
        lblForget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goFindPass();
            }
        });

        setupToolBar("登录");

        readCachedMObile();
    }

    private void readCachedMObile(){
        String encryptedMobile = PreferencesHelper.getString(this, Constants.PREFS_MOBILE);
        if (encryptedMobile != null){
            try{
                String mobile = Encryptor.decrypt(encryptedMobile);
                this.mTxtMobile.setText(mobile);
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        String encryptedPassword = PreferencesHelper.getString(this, Constants.PREFS_PASSWORD);
        if(encryptedPassword!=null){
            try{
                String pwd = Encryptor.decrypt(encryptedPassword);
                this.mTxtPassword.setText(pwd);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private void goFindPass(){
        Intent intent = new Intent(this, ResetPasswordActivity.class);
        startActivity(intent);
    }

    protected void goRegister(){
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    private void doLogin(){
        final String mobile = mTxtMobile.getText().toString();
        final String password = mTxtPassword.getText().toString();

        if (mobile.length()<1){
            Toast.makeText(this, "请输入手机号", Toast.LENGTH_SHORT).show();
            mTxtMobile.requestFocus();
            return;
        }

        if(!CommUtils.isMobileNO(mobile)){
            Toast.makeText(this, "请输入正确的手机号码", Toast.LENGTH_SHORT).show();
            mTxtMobile.requestFocus();
            return;
        }

        if (password.length()<1){
            Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show();
            mTxtPassword.requestFocus();
            return;
        }

        final ProgressDialog dialog = ProgressDialog.show(this, null, "正在加载...", true, false);
        ImottoApi.getInstance().userLogin(mobile, password, new ImottoApi.InvokeCompletionHandler<LoginResp>() {
            @Override
            public void onApiCallCompletion(LoginResp result) {
                dialog.dismiss();
                if (result.isSuccess()){
                    Toast.makeText(LoginActivity.this, "欢迎回来,"+ result.UserName, Toast.LENGTH_SHORT).show();
                    PreferencesHelper.putString(LoginActivity.this, Constants.PREFS_USERID, result.UserId);
                    PreferencesHelper.putString(LoginActivity.this, Constants.PREFS_USERNAME, result.UserName);
                    PreferencesHelper.putString(LoginActivity.this, Constants.PREFS_USERTHUMB, result.UserThumb);
                    PreferencesHelper.putString(LoginActivity.this, Constants.PREFS_USERTOKEN, result.UserToken);

                    try {
                        String encryptedMobile = Encryptor.encrypt(mobile);
                        String encryptedPassword = Encryptor.encrypt(password);
                        PreferencesHelper.putString(LoginActivity.this, Constants.PREFS_MOBILE, encryptedMobile);
                        PreferencesHelper.putString(LoginActivity.this, Constants.PREFS_PASSWORD, encryptedPassword);
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }

                    JPushInterface.setAlias(LoginActivity.this, result.UserId, null);

                    setResult(ProfileFragment.REQUEST_CODE_LOGIN);
                    ImottoApplication.getInstance().notifyUserChanged();

                    goBack();
                }else{
                    Toast.makeText(LoginActivity.this, result.Msg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public Class<LoginResp> getGenericClass() {
                return LoginResp.class;
            }
        });
    }

    class BtnLoginClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            doLogin();
        }
    }
}
