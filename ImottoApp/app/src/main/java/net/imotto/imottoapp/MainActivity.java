package net.imotto.imottoapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.allenliu.badgeview.BadgeFactory;
import com.allenliu.badgeview.BadgeView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.BinaryHttpResponseHandler;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import net.imotto.imottoapp.activities.AddMottoActivity;
import net.imotto.imottoapp.activities.AwardsActivity;
import net.imotto.imottoapp.activities.LoginActivity;
import net.imotto.imottoapp.activities.RankBoardActivity;
import net.imotto.imottoapp.fragments.BaseFragment;
import net.imotto.imottoapp.fragments.MsgsFragment;
import net.imotto.imottoapp.services.ImottoApi;
import net.imotto.imottoapp.services.models.ReadBadgeResp;
import net.imotto.imottoapp.services.models.SpotlightModel;
import net.imotto.imottoapp.utils.CommUtils;
import net.imotto.imottoapp.utils.Constants;
import net.imotto.imottoapp.utils.PreferencesHelper;
import net.imotto.imottoapp.utils.UtilApp;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity implements Observer {
    private static final String TAG = "MainActivity";
    private SystemBarTintManager tintManager;
    private ImageView msgTabImg;
    private BadgeView msgBadgeView;

    private static final int PERMISSION_REQUEST_STORAGE = 12;


    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private MainPagerAdapter mSectionsPagerAdapter;


    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private CollapsingToolbarLayout mToolbarContainer;
    private FloatingActionButton mfab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
            localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
            if (Build.VERSION.SDK_INT < 21) {
                localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION | localLayoutParams.flags);

                tintManager = new SystemBarTintManager(this);
                tintManager.setStatusBarTintColor(getResources().getColor(R.color.colorPrimary));
                tintManager.setStatusBarTintEnabled(true);
            }
        }


        mToolbarContainer = (CollapsingToolbarLayout) findViewById(R.id.toolbar_container);

        mSectionsPagerAdapter = new MainPagerAdapter(getSupportFragmentManager(), this);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setOffscreenPageLimit(3);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                Log.i(TAG, "page selected: " + position);
                prepareFragment(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });


        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            if (tab != null) {
                View tabView = mSectionsPagerAdapter.getTabView(i);
                if(i == 2){
                    //设置 tab 消息按钮图片, 以设置badge
                    msgTabImg = (ImageView) tabView.findViewById(R.id.tab_item_img);
                }
                tab.setCustomView(tabView);
            }
        }

        mfab = (FloatingActionButton) findViewById(R.id.fab);
        mfab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this, AddMottoActivity.class);
                gotoActivity(intent, true, "登录后才能发表偶得");

            }
        });

        ImottoApplication.getInstance().addUserObserver(this);
        update(null, null);

        showExtraNotice();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        checkOpenFlag(intent);
    }

    private void checkOpenFlag(Intent intent){
        int openFlag = intent.getIntExtra(Constants.BUNDLE_OPEN_FLAG, 0);
        Log.e(TAG, "newIntent, OpenFlag:"+ openFlag);

        if(openFlag == 1) {
            mViewPager.setCurrentItem(2);
            Object obj = mViewPager.getAdapter().instantiateItem(mViewPager, 2);
            if(obj instanceof MsgsFragment){
                MsgsFragment fragment = (MsgsFragment) obj;
                fragment.setCurrentItem(0);
            }
        }else if (openFlag == 2){
            mViewPager.setCurrentItem(2);
            Object obj = mViewPager.getAdapter().instantiateItem(mViewPager, 2);
            if(obj instanceof MsgsFragment){
                ((MsgsFragment) obj).setCurrentItem(1);
            }
        }
    }

    private void showExtraNotice(){
        String json = PreferencesHelper.getString(this, Constants.PREFS_JSON_SPOTLIGHT);

        if(json!= null && (!json.isEmpty())){

            final SpotlightModel model = CommUtils.fromJson(json, SpotlightModel.class);
            if(model!=null){
                if(model.Type == 1 || model.Type ==2){
                    View noticeView = getLayoutInflater().inflate(R.layout.dialog_notice, null);
                    ImageView imgThumb = (ImageView) noticeView.findViewById(R.id.img_thumb);
                    TextView lblInfo = (TextView) noticeView.findViewById(R.id.lbl_info);
                    lblInfo.setText(model.Info);

                    ImageLoader.getInstance().displayImage(model.Img, imgThumb);

                    new AlertDialog.Builder(this).setTitle(null).setView(noticeView)
                            .setPositiveButton("查看", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //Toast.makeText(MainActivity.this, "show notice", Toast.LENGTH_SHORT).show();
                                    gotoNoticeInfo(model);
                                }
                            })
                            .setNegativeButton("关闭", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    checkUpdateInfo();
                                }
                            }).show();

                    return;
                }
            }
        }

        //检查升级。
        checkUpdateInfo();
    }

    private void gotoNoticeInfo(SpotlightModel model){
        //1:上月排行榜出炉，2：公布当月奖品设置，3：其它
        if(model.Type == 1){
            Intent intent = new Intent(this, RankBoardActivity.class);
            int lastMonth;
            if(model.TheMonth%100 == 1){
                lastMonth = model.TheMonth - 101 + 12;
            }else{
                lastMonth = model.TheMonth - 1;
            }

            intent.putExtra(Constants.BUNDLE_THEMONTH, lastMonth);
            startActivity(intent);
        }else if(model.Type == 2){
            Intent intent = new Intent(this, AwardsActivity.class);
            startActivity(intent);
        }
    }

    private void setMsgBadge(int badgeCount){

        if(badgeCount>99){
            badgeCount = 99;
        }

        if(msgBadgeView == null) {
            if(badgeCount > 0) {
                if (msgTabImg != null) {
                    msgBadgeView = BadgeFactory.createCircle(this)
                            .setBadgeCount(badgeCount)
                            .setMargin(0, 0, 5, 0);
                    msgBadgeView.setPadding(0, 0, 0, 0);
                    msgBadgeView.bind(msgTabImg);
                }
            }
        }else{
            if(badgeCount>0){
                msgBadgeView.setBadgeCount(badgeCount);
            }else{
                msgBadgeView.unbind();
                msgBadgeView = null;
            }

        }
    }

    public void prepareFragment(final int position) {
        if (position == 0) {
            mfab.show();
        } else {
            mfab.hide();
        }

        Object obj = mSectionsPagerAdapter.instantiateItem(mViewPager, position);
        if (obj != null) {
            Log.i(TAG, obj.toString());
            if (obj instanceof BaseFragment) {
                BaseFragment fragment = (BaseFragment) obj;
                Toolbar toolbar = fragment.getSpecialToolbar();

                if (toolbar != null) {
                    mToolbarContainer.removeAllViews();
                    mToolbarContainer.addView(toolbar);
                }
            }
        }
    }


    public void gotoActivity(Intent intent, boolean checkLogin, String loginHint) {

        if (checkLogin) {
            if (!ImottoApplication.getInstance().isUserLogin()) {

                if (loginHint == null) {
                    loginHint = "请先登录再执行此操作";
                }

                Toast.makeText(this, loginHint, Toast.LENGTH_SHORT).show();
                Intent loginIntent = new Intent(this, LoginActivity.class);
                startActivity(loginIntent);
                return;
            }
        }

        startActivity(intent);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }

    private long mExitTime = 0; // 点击两次退出，记录点击时间

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
         /* 直接抛给上层的Activity处理  */
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {

            if ((System.currentTimeMillis() - mExitTime) > 2000) // System.currentTimeMillis()无论何时调用，肯定大于2000
            {
                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                mExitTime = System.currentTimeMillis();
            } else {
                finish();
                // 退出前把线程清理一下.
                // BaseRequest.cancelAllRequest();
                // FGoodsApp.getInstance().exit();
            }
            return true;
        }

        return super.onKeyDown(keyCode, event);


    }

    /**
     * 保存升级信息
     */
    private void checkUpdateInfo() {
        String updateFlag = PreferencesHelper.getString(this, Constants.PREFS_UPDATE_FLAG);

        if ("0".equals(updateFlag)) {
            // no update
        } else if ("1".equals(updateFlag)) {
            // select update
            showSelecUpdate();
        } else if ("2".equals(updateFlag)) {
            // must update
            showMustUpdate();
        }
    }

    /**
     * 可选升级.
     */
    private void showSelecUpdate() {
        String newVersion = PreferencesHelper.getString(this, Constants.PREFS_UPDATE_NEW_VERSION);
        String updateMessage = PreferencesHelper.getString(this, Constants.PREFS_UPDATE_MESSAGE);

        new AlertDialog.Builder(this).setTitle("版本更新 " + newVersion).setMessage(updateMessage).setPositiveButton("升级", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(MainActivity.this, "up", Toast.LENGTH_SHORT).show();
//                downLoadApkAndInstall();

                installApk();

            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //start main aty


            }
        }).create().show();
    }

    /**
     * 必须升级
     */
    private void showMustUpdate() {
        //暂时没实现。
    }


    /**
     * 下载apk
     */
    private void downLoadApkAndInstall() {
        String downLoadUrl = PreferencesHelper.getString(this, Constants.PREFS_UPDATE_DOWNLOAD_URL);

        Log.e(TAG,downLoadUrl);


        AsyncHttpClient client = new AsyncHttpClient();
        client.get(downLoadUrl, new FileAsyncHttpResponseHandler(/* Context */ this) {

            private String[] mAllowedContentTypes = new String[] { "image/jpeg",
                    "image/png", "applicatoin/vnd.android" };

            @Override
            public void onSuccess(int statusCode, Header[] headers, File response) {
                // Do something with the file `response`

                Log.e(TAG,response.getName());
                Log.e(TAG,response.getAbsolutePath());

                File installFile = new File(response.getAbsolutePath() + ".apk");
                response.renameTo(installFile);


                //installApk(installFile);
            }

            @Override
            public void onProgress(long bytesWritten, long totalSize) {
                super.onProgress(bytesWritten, totalSize);
                Log.e(TAG, "total " + totalSize + "writen " + bytesWritten);

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {

            }


        });

    }


    /**
     * [ref] http://blog.csdn.net/nalw2012/article/details/49491743
     * 安装Apk
     */
    private  void installApk() {

        if (ContextCompat.checkSelfPermission(MainActivity.this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            //申请WRITE_EXTERNAL_STORAGE权限
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_STORAGE);
            return;
        }else {


        }

        String downLoadUrl = PreferencesHelper.getString(this, Constants.PREFS_UPDATE_DOWNLOAD_URL);

        Log.e(TAG,downLoadUrl);


        BinaryHttpResponseHandler downloadNewVersionHandler = new BinaryHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                if(bytes == null){
                    Log.e(TAG, "Can't download newest version apk.");
                    return;
                }
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

                    try {
                        File folder = new File(getDiskCachePath(MainActivity.this) + "/" + Constants.APK_FILE_PATH);
                        if(!folder.exists())
                            folder.mkdirs();
                        String fileName = Constants.APK_FILE_PATH + "OnLineLearning"
                                 + ".apk";

                        File file = new File(folder + "/" + fileName);
                        if(!file.exists()) {
                            file.createNewFile();
                        } else {
                            file.delete();
                            file.createNewFile();
                        }
                        FileOutputStream fos = new FileOutputStream(file);
                        BufferedOutputStream bfw = new BufferedOutputStream(fos);
                        bfw.write(bytes);
                        bfw.flush();
                        fos.flush();
                        if(bfw != null)
                            bfw.close();
                        if(fos != null) {
                            fos.close();
                        }

                        UtilApp.installApk(file, MainActivity.this);
//                        updateVersion();
                        Log.d(TAG, "download file: " + fileName);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else{
                    Log.e(TAG, "SDCard isn't exist. ");
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                Log.d(TAG, "onFailure " + throwable.getMessage());
            }

            @Override
            public void onStart() {
                super.onStart();
//                httpApi.setDialogMessage(R.string.text_download_new_version);
//                httpApi.showDialog();
            }
        };

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(downLoadUrl, downloadNewVersionHandler);
    }

    /**
     * 获取cache目录路径
     */
    public static String getDiskCachePath(Context context) {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) || !Environment.isExternalStorageRemovable()) {
            return context.getExternalCacheDir().getPath();
        } else {
            return context.getCacheDir().getPath();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if(requestCode == PERMISSION_REQUEST_STORAGE){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "升级", Toast.LENGTH_SHORT).show();
                installApk();
            }else{
                Toast.makeText(this, "升级失败", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    @Override
    public void update(Observable o, Object arg) {
        if(ImottoApplication.getInstance().isUserLogin()){
            ImottoApi.getInstance().readUserBadge(new ImottoApi.InvokeCompletionHandler<ReadBadgeResp>() {
                @Override
                public void onApiCallCompletion(ReadBadgeResp result) {
                    if(result.isSuccess()){
                        setMsgBadge(result.Badge);
                    }else{
                        Log.e(TAG, "read badge failed:"+result.Msg);
                    }
                }

                @Override
                public Class<ReadBadgeResp> getGenericClass() {
                    return ReadBadgeResp.class;
                }
            });
        }else{
            setMsgBadge(0);
        }
    }
}
