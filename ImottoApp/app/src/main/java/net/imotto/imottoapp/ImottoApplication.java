package net.imotto.imottoapp;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import net.imotto.imottoapp.utils.Constants;
import net.imotto.imottoapp.utils.PreferencesHelper;

import java.util.Observable;
import java.util.Observer;

import cn.jpush.android.api.JPushInterface;


/**
 * Created by sunht on 16/10/26.
 */

public class ImottoApplication extends Application {

    private static ImottoApplication mInstance;

    public static ImottoApplication getInstance(){
        return mInstance;
    }
    private String sign;
    private String userId;
    private String userToken;
    private String userName;
    private String userThumb;
    private UserObservable userObservable = new UserObservable();


    public String getSign(){
        if(sign == null){
            sign = PreferencesHelper.getString(this, Constants.PREFS_SIGN);
        }

        return sign;
    }

    public String getUserThumb(){
        if(userThumb == null){
            userThumb = PreferencesHelper.getString(this, Constants.PREFS_USERTHUMB);
        }

        return userThumb;
    }

    public String getUserName(){
        if(userName == null){
            userName = PreferencesHelper.getString(this, Constants.PREFS_USERNAME);
        }
        return userName;
    }

    public String getUserId(){
        if(userId == null){
            userId = PreferencesHelper.getString(this, Constants.PREFS_USERID);
        }

        return userId;
    }

    public String getUserToken(){
        if(userToken == null){
            userToken = PreferencesHelper.getString(this, Constants.PREFS_USERTOKEN);
        }

        return userToken;
    }

    public boolean isUserLogin(){
        return getUserToken() != null && getUserToken() != "";
    }

    public void refreshUserState(){
        sign = null;
        userId = null;
        userToken = null;
        userName = null;
    }

    public void refreshUserInfo(){
        userThumb = null;
        userName = null;
    }

    public void notifyUserChanged(){
        refreshUserState();
        userObservable.changeAccount();
        userObservable.notifyObservers();
    }

    public void addUserObserver(Observer observer){
        userObservable.addObserver(observer);
    }

    public String getVersion() {

        int versionCode = -1;
        try {
            PackageManager packageManager = this.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(this.getPackageName(), 0);
            versionCode = packageInfo.versionCode;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return Integer.toString(versionCode);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        CrashHandler handler = CrashHandler.getInstance();
        handler.init(getApplicationContext());
        Thread.setDefaultUncaughtExceptionHandler(handler);

        //todo: debugmode should off
        JPushInterface.setDebugMode(false);
        JPushInterface.init(this);

        initImageLoader(getApplicationContext());
    }

    public static void initImageLoader(Context context) {
        // This configuration tuning is custom. You can tune every option, you may tune some of them,
        // or you can create default configuration by
        //  ImageLoaderConfiguration.createDefault(this);
        // method.
        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
        config.threadPriority(Thread.NORM_PRIORITY - 2);
        config.denyCacheImageMultipleSizesInMemory();
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
        config.tasksProcessingOrder(QueueProcessingType.LIFO);
        //config.writeDebugLogs(); // Remove for release app

        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config.build());
    }

    private class UserObservable extends Observable{

        public void changeAccount(){
            setChanged();
        }
    }
}
