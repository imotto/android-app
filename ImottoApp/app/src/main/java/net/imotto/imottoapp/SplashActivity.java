package net.imotto.imottoapp;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;
import android.os.Handler;

import net.imotto.imottoapp.services.ImottoApi;
import net.imotto.imottoapp.services.models.LoginResp;
import net.imotto.imottoapp.services.models.RegisterDeviceResp;
import net.imotto.imottoapp.utils.CommUtils;
import net.imotto.imottoapp.utils.Constants;
import net.imotto.imottoapp.utils.DeviceHelper;
import net.imotto.imottoapp.utils.Encryptor;
import net.imotto.imottoapp.utils.PreferencesHelper;

import java.util.UUID;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by sunht on 16/10/27.
 *
 */

public class SplashActivity extends Activity {
    private static final String TAG = "SplashActivity";
    private static final int MY_PERMISSIONS_REQUEST_READE_PHONE_STATE = 1;

    private int attemptCount=0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN ,
                WindowManager.LayoutParams. FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                prepareApp();
            }
        }, 100);
    }

    private void prepareApp() {
        //check update here.
        if(attemptCount<3) {
            attemptCount++;
            String cachedDeviceId = PreferencesHelper.getString(this, Constants.PREFS_UUID_DEVICEID);
            Log.i(TAG, "cached device id is: " + cachedDeviceId);
            registerDevice(cachedDeviceId);
        }else{
            Toast.makeText(this, "服务器无法通讯，请稍后重试", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void prepareComplete(){
        Intent intent= new Intent(this, MainActivity.class);
        startActivity(intent);
        this.finish();
    }

    private void registerDevice(String uuid){
        String deviceId = null;
        if (uuid == null) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_PHONE_STATE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_PHONE_STATE},
                        MY_PERMISSIONS_REQUEST_READE_PHONE_STATE);
            } else {
                deviceId = DeviceHelper.getUUID(this);
            }
        }else{
            deviceId = uuid;
        }

        if(deviceId != null){
            final String useDeviceId = deviceId;
            DisplayMetrics metric = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metric);
            // 屏幕分辨率
            String screen = "" + metric.widthPixels + "x" + metric.heightPixels+"("+metric.density+")";

            ImottoApi.getInstance().registerDevice(useDeviceId, screen, new ImottoApi.InvokeCompletionHandler<RegisterDeviceResp>() {
                @Override
                public void onApiCallCompletion(RegisterDeviceResp result) {
                    if (result != null && result.isSuccess()) {
                        Log.i(TAG, result.Sign);
                        PreferencesHelper.putString(SplashActivity.this, Constants.PREFS_UUID_DEVICEID, useDeviceId);
                        PreferencesHelper.putString(SplashActivity.this, Constants.PREFS_SIGN, result.Sign);

                        saveUpdateInfo(result);

                        //auto login
                        SplashActivity.this.autoLogin();
                    } else {
                        //Toast.makeText(SplashActivity.this, "注册设备失败", Toast.LENGTH_SHORT).show();
                        //try again.
                        prepareApp();
                    }
                }

                @Override
                public Class<RegisterDeviceResp> getGenericClass() {
                    return RegisterDeviceResp.class;
                }
            });
        }
    }

    private void autoLogin(){
        String encryptedMobile = PreferencesHelper.getString(this, Constants.PREFS_MOBILE);
        if (encryptedMobile != null){
            try{
                String mobile = Encryptor.decrypt(encryptedMobile);
                String encryptedPassword = PreferencesHelper.getString(this, Constants.PREFS_PASSWORD);
                if (encryptedPassword != null) {
                    String password = Encryptor.decrypt(encryptedPassword);

                    ImottoApi.getInstance().userLogin(mobile, password, new ImottoApi.InvokeCompletionHandler<LoginResp>() {
                        @Override
                        public void onApiCallCompletion(LoginResp result) {
                            if(result.isSuccess()){
                                PreferencesHelper.putString(SplashActivity.this, Constants.PREFS_USERID, result.UserId);
                                PreferencesHelper.putString(SplashActivity.this, Constants.PREFS_USERNAME, result.UserName);
                                PreferencesHelper.putString(SplashActivity.this, Constants.PREFS_USERTHUMB, result.UserThumb);
                                PreferencesHelper.putString(SplashActivity.this, Constants.PREFS_USERTOKEN, result.UserToken);
                                Toast.makeText(SplashActivity.this, "欢迎回来，"+result.UserName, Toast.LENGTH_SHORT).show();

                                //设置Jpush别名
                                JPushInterface.setAlias(SplashActivity.this, result.UserId, null);

                                ImottoApplication.getInstance().refreshUserState();
                            }else{
                                PreferencesHelper.removeKey(SplashActivity.this, Constants.PREFS_USERID);
                                PreferencesHelper.removeKey(SplashActivity.this, Constants.PREFS_USERTOKEN);
                            }

                            prepareComplete();
                        }

                        @Override
                        public Class<LoginResp> getGenericClass() {
                            return LoginResp.class;
                        }
                    });
                }else{
                    prepareComplete();
                }

            }catch (Exception e){
                prepareComplete();
            }
        }else{
            prepareComplete();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_READE_PHONE_STATE){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                registerDevice(null);
            } else {
                registerDevice(UUID.randomUUID().toString());
            }
            return;
        }

        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
    }


    /**
     * 保存升级信息。
     * @param resp 注册设备结果
     */
    private void saveUpdateInfo(RegisterDeviceResp resp) {
        PreferencesHelper.putString(SplashActivity.this, Constants.PREFS_UPDATE_FLAG, resp.UpdateFlag);
        PreferencesHelper.putString(SplashActivity.this, Constants.PREFS_UPDATE_NEW_VERSION, resp.NewVersion);
        PreferencesHelper.putString(SplashActivity.this, Constants.PREFS_UPDATE_MESSAGE, resp.UpdateSummary);
        PreferencesHelper.putString(SplashActivity.this, Constants.PREFS_UPDATE_DOWNLOAD_URL, resp.DownloadUrl);

        if(resp.Extra!=null){
            String json = CommUtils.toJson(resp.Extra);
            PreferencesHelper.putString(SplashActivity.this, Constants.PREFS_JSON_SPOTLIGHT, json);
        }else{
            PreferencesHelper.removeKey(this, Constants.PREFS_JSON_SPOTLIGHT);
        }
    }
}
