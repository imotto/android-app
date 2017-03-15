package net.imotto.imottoapp.utils;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FileFilter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.msebera.android.httpclient.util.TextUtils;

/**
 * @ClassName: UtilApp
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author NiuFC
 * @维护人:
 * @version V1.0
 * 
 */
public class UtilApp {

    private final static String TAG = "UtilApp";

    /**
     * 对网络连接状态进行判断
     * 
     * @return true, 可用； false， 不可用
     */
    public static boolean isOpenNetwork(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connManager.getActiveNetworkInfo() != null) {
            return connManager.getActiveNetworkInfo().isAvailable();
        }
        return false;
    }

    /**
     * 查询WiFi与Mobile的移动网络是否可用
     * 
     * @param context
     * @return 可用返回true,否则返回false
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager mConnMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = mConnMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mMobile = mConnMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        boolean flag = false;
        if ((mWifi != null) && ((mWifi.isAvailable()) || (mMobile.isAvailable()))) {
            if ((mWifi.isConnected()) || (mMobile.isConnected())) {
                flag = true;
            }
        }
        return flag;
    }

    /**
     * 验证电话号码 TODO(这里用一句话描述这个方法的作用)
     * 
     * @Title: isMobileNO
     * @param mobiles
     * @return 设定文件
     * @return boolean 返回类型
     * @author 牛丰产
     * @date 2014年10月16日 下午6:59:38
     * @维护人:
     * @version V1.0
     */
    public static boolean isMobileNO(String mobiles) {
        if (mobiles == null || mobiles.length() != 11) {
            return false;
        }
        /*
         * 130、131、132、133、134、135、136、137、138、139； 145、147；
         * 150、151、152、153、155、156、157、158、159； 176、177、178；
         * 180、181、182、183、184、185、186、187、188、189。
         */

        Pattern p = Pattern.compile("^((13[0-9])|(14[^4,\\D])|(17[^4,\\D])|(15[^4,\\D])|(18[0-9]))\\d{8}$");
        // Pattern p = Pattern.compile("^1[34578]\\d{9}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }

    /**
     * 程序是否在前台运行
     * 
     * @return
     */
    public static boolean isAppOnForeground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        String packageName = context.getPackageName();

        List<RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null)
            return false;

        for (RunningAppProcessInfo appProcess : appProcesses) {
            // The name of the process that this object is associated with.
            if (appProcess.processName.equals(packageName) && appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true;
            }
        }

        return false;
    }

    /**
     * 
     * TODO 隐藏键盘
     * 
     * @Title: hideInputKeyboard
     * @param context
     *            设定文件
     * @return void 返回类型
     * @author 牛丰产
     * @date 2014年10月10日 下午2:01:40
     * @维护人:
     * @version V1.0
     */
    public static void hideInputKeyboard(Context context, EditText view) {

        InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
    }

    /**
     * 显示键盘 TODO(这里用一句话描述这个方法的作用)
     * 
     * @Title: showInputKeyboard
     * @param context
     * @param view
     *            设定文件
     * @return void 返回类型
     * @author 牛丰产
     * @date 2014年10月10日 下午2:01:54
     * @维护人:
     * @version V1.0
     */
    public static void showInputKeyboard(Context context, EditText view) {
        InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.showSoftInput(view, InputMethodManager.RESULT_SHOWN);
    }

    /**
     * 根据包名判断应用是否已经安装
     * 
     * @Title: hasInstalled
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @param @param context
     * @param @param packageName
     * @param @return 设定文件
     * @return boolean 返回类型
     * @author 张博
     * @date 2014-12-22 下午4:30:04
     * @维护人:
     * @version V1.0
     */
    public static boolean hasInstalled(Context context, String packageName) {
        if (TextUtils.isEmpty(packageName.trim())) {
            return false;
        } else {
            if (getPackageInfo(context, packageName) == null) {
                return false;
            } else {
                return true;
            }
        }
    }

    /**
     * 判断应用是否该升级
     * 
     * @Title: whetherUpdate
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @param @param context
     * @param @param packageName
     * @param @param version
     * @param @return 设定文件
     * @return boolean 返回类型
     * @author 张博
     * @date 2014-12-22 下午4:31:38
     * @维护人:
     * @version V1.0
     */
    public static boolean whetherUpdate(Context context, String packageName, String version) {
        if (TextUtils.isEmpty(packageName.trim()) || TextUtils.isEmpty(version.trim())) {
            return false;
        } else {
            PackageInfo info = getPackageInfo(context, packageName);
            if (info != null) {
                if (info.versionCode < Integer.parseInt(version)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 根据包名获取应用信息，如果该包名应用未安装，返回null
     * 
     * @Title: getPackageInfo
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @param @param context
     * @param @param packageName
     * @param @return 设定文件
     * @return PackageInfo 返回类型
     * @author 张博
     * @date 2014-12-22 下午4:30:58
     * @维护人:
     * @version V1.0
     */
    public static PackageInfo getPackageInfo(Context context, String packageName) {
        if (TextUtils.isEmpty(packageName)) {
            return null;
        } else {
            PackageManager pm = context.getPackageManager();
            try {
                return pm.getPackageInfo(packageName, 0);
            }
            catch (NameNotFoundException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    /**
     * 安装应用程序
     * 
     * @Title: installApp
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @param @param context
     * @param @param url 设定文件
     * @return void 返回类型
     * @author 张博
     * @date 2014-12-1 下午4:25:57
     * @维护人:
     * @version V1.0
     */
    public static void installApp(Context context, String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(new File(url)), "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    /**
     * 根据包名启动应用，如果应用已经不存在，则弹出toast提示打开
     * 
     * @Title: launchApp
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @param @param context
     * @param @param packageName 设定文件
     * @return void 返回类型
     * @author 张博
     * @date 2014-12-16 下午4:16:56
     * @维护人:
     * @version V1.0
     */
    public static boolean launchApp(Context context, String packageName) {
        Intent intent = new Intent();
        if (packageName == null || TextUtils.isEmpty(packageName.trim())) {
            return false;
        }
        if (packageName.equals(context.getPackageName())) {
            Toast.makeText(context, "程序已经在运行中", Toast.LENGTH_SHORT).show();
            return true;
        }
        try {
            intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
            context.startActivity(intent);
            return true;
        }
        catch (Exception e) {
            Toast.makeText(context, "打开应用失败", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    /**
     * install APK
     */
    public static void installApk(File apkFile, Context context) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        intent.setAction(android.content.Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    /**
     * remove dir 下面的 apk
     * 
     * @param dirFile
     */
    public static void removeApk(final File dirFile) {
        if (dirFile.isDirectory()) {
            File[] fileList = dirFile.listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    if (pathname.getName().endsWith(".apk")) {
                        return true;
                    }
                    return false;
                }
            });
            for (File f : fileList) {
                f.delete();
            }
        }
    }

}
