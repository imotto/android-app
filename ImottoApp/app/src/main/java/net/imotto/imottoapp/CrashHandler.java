package net.imotto.imottoapp;

import android.content.Context;
import android.util.Log;

/**
 * Created by sunht on 16/10/26.
 */

public class CrashHandler implements Thread.UncaughtExceptionHandler {
    private static final String TAG = "CrashHandler";
    // 需求是 整个应用程序 只有一个 MyCrash-Handler
    private static CrashHandler INSTANCE ;
    private Context context;

    //1.私有化构造方法
    private CrashHandler(){

    }

    public static synchronized CrashHandler getInstance(){
        if (INSTANCE == null)
            INSTANCE = new CrashHandler();
        return INSTANCE;
    }

    public void init(Context context){
        this.context = context;
    }


    public void uncaughtException(Thread arg0, Throwable arg1) {

        // 在此可以把用户手机的一些信息以及异常信息捕获并上传,由于UMeng在这方面有很程序的api接口来调用，故没有考虑

        Log.e(TAG, arg0.toString());
        Log.e(TAG, arg1.toString());
        arg1.printStackTrace();

        //干掉当前的程序
        android.os.Process.killProcess(android.os.Process.myPid());
    }

}
