package net.imotto.imottoapp.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.readystatesoftware.systembartint.SystemBarTintManager;

import net.imotto.imottoapp.ImottoApplication;
import net.imotto.imottoapp.R;
import net.imotto.imottoapp.adapters.RecyclerViewAdapter;
import net.imotto.imottoapp.adapters.SettingsAdapter;
import net.imotto.imottoapp.controls.ActionSheet;
import net.imotto.imottoapp.services.ImottoApi;
import net.imotto.imottoapp.services.models.ApiResp;
import net.imotto.imottoapp.utils.Constants;
import net.imotto.imottoapp.utils.Encryptor;
import net.imotto.imottoapp.utils.PreferencesHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunht on 16/11/19.
 *
 */

public class SettingActivity extends BaseBackableActivity implements ActionSheet.MenuItemClickListener  {
    public static final int REQUEST_CODE_PICK_THUMB=1;
    private RecyclerView mRecyclerView;
    private SettingsAdapter mAdapter;
    private List<SettingsAdapter.SettingItem> mData;

    private String uname;
    private int usex;
    private String uthumb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
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

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        Bundle bundle = this.getIntent().getExtras();
        usex = bundle.getInt(Constants.BUNDLE_USEX);
        uname = ImottoApplication.getInstance().getUserName();
        uthumb = ImottoApplication.getInstance().getUserThumb();

        String title = getResources().getString(R.string.title_user_setting);
        setupToolBar(title);

        initView();
    }

    private void initView(){
        initData();

        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new SettingsAdapter(this, mData);
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
    }

    public void handleItemClick(int position){

        SettingsAdapter.SettingItem item = mAdapter.getDataItem(position);

        switch (item.id){
            case 1: //修改头像
                Intent intent = new Intent(this, PickThumbActivity.class);
                startActivityForResult(intent, REQUEST_CODE_PICK_THUMB);
                break;
            case 2: //修改用户名
                final EditText et=new EditText(this);

                new AlertDialog.Builder(this).setTitle("修改用户名称").setView(et).
                        setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1){
                                final String newName = et.getText().toString();

                                final ProgressDialog dialog = ProgressDialog.show(SettingActivity.this, null, "正在加载...", true, false);
                                ImottoApi.getInstance().modifyUserName(newName, new ImottoApi.InvokeCompletionHandler<ApiResp>() {
                                    @Override
                                    public void onApiCallCompletion(ApiResp result) {
                                        dialog.dismiss();
                                        processModifyUserNameResult(result, newName);
                                    }

                                    @Override
                                    public Class<ApiResp> getGenericClass() {
                                        return ApiResp.class;
                                    }
                                });

                            }
                        }).setNegativeButton("取消",null).show();
                break;
            case 3: //修改性别
                ActionSheet menuView = new ActionSheet(this);
                menuView.setCancelButtonTitle("取消");// before add items
                menuView.addItems("女", "男", "保密");
                menuView.setItemClickListener(this);
                menuView.setCancelableOnTouchMenuOutside(true);
                menuView.showMenu();
                break;
            case 4: //修改密码
                final View modifyView = LayoutInflater.from(this).inflate(R.layout.dialog_modify_password, null, false);
                new AlertDialog.Builder(this).setTitle("修改登录密码").setView(modifyView).
                        setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1){
                                EditText txtPass = (EditText)modifyView.findViewById(R.id.txt_password);
                                EditText txtNewPass = (EditText)modifyView.findViewById(R.id.txt_newpassword);
                                String pass = txtPass.getText().toString();
                                final String newPass = txtNewPass.getText().toString();
                                if(newPass.equals("")){
                                    Toast.makeText(SettingActivity.this, "密码不能为空，请重试", Toast.LENGTH_SHORT).show();
                                }else {

                                    final ProgressDialog dialog = ProgressDialog.show(SettingActivity.this, null, "正在加载...", true, false);
                                    ImottoApi.getInstance().modifyPassword(pass, newPass, new ImottoApi.InvokeCompletionHandler<ApiResp>() {
                                        @Override
                                        public void onApiCallCompletion(ApiResp result) {
                                            dialog.dismiss();
                                            processModifyPassResult(result, newPass);
                                        }

                                        @Override
                                        public Class<ApiResp> getGenericClass() {
                                            return ApiResp.class;
                                        }
                                    });
                                }
                            }
                        }).setNegativeButton("取消",null).show();

                break;

            default:
                break;
        }
    }

    @Override
    public void onItemClick(int itemPosition) {
        int gender = 0;
        switch (itemPosition){
            case 0: //女
               gender = 1;
                break;
            case 1: //男
                gender = 2;
                break;
            case 2: //保密
                gender = 0;
                break;
            default:
                break;
        }

        final int sex = gender;


        final ProgressDialog dialog = ProgressDialog.show(this, null, "正在加载...", true, false);
        ImottoApi.getInstance().modifySex(sex, new ImottoApi.InvokeCompletionHandler<ApiResp>() {
            @Override
            public void onApiCallCompletion(ApiResp result) {
                dialog.dismiss();
                processModifySexResult(result, sex);
            }

            @Override
            public Class<ApiResp> getGenericClass() {
                return ApiResp.class;
            }
        });

    }

    private void processModifyPassResult(ApiResp result, String newPass){
        if(result.isSuccess()){
            try {
                String encryptedPassword = Encryptor.encrypt(newPass);
                PreferencesHelper.putString(this, Constants.PREFS_PASSWORD, encryptedPassword);

                Toast.makeText(this, "密码修改成功", Toast.LENGTH_SHORT).show();
                
            }catch (Exception e){
                e.printStackTrace();
            }
        }else{
            Toast.makeText(this, result.Msg, Toast.LENGTH_SHORT).show();
        }
    }
    
    private void processModifySexResult(ApiResp result, int gender){
        if(result.isSuccess()){
            SettingsAdapter.SettingItem item = mData.get(5);
            if(gender == 1){
                item.title="女";
                item.thumbResId = R.drawable.ic_female;
            }else if(gender == 2){
                item.title="男";
                item.thumbResId = R.drawable.ic_male;
            }else{
                item.title="保密";
                item.thumbResId = R.drawable.ic_circle_outline_blue;
            }
            mAdapter.notifyDataSetChanged();
        }else{
            Toast.makeText(this, result.Msg, Toast.LENGTH_SHORT).show();
        }
    }

    private void processModifyUserNameResult(ApiResp result, String newName){
        if(result.isSuccess()) {
            uname = newName;
            PreferencesHelper.putString(this, Constants.PREFS_USERNAME, newName);
            ImottoApplication.getInstance().refreshUserInfo();
            mData.get(3).title = uname;
            mAdapter.notifyDataSetChanged();
        }else{
            Toast.makeText(this, result.Msg, Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_CODE_PICK_THUMB){
            mData.get(1).img = ImottoApplication.getInstance().getUserThumb();
            mAdapter.notifyDataSetChanged();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private List<SettingsAdapter.SettingItem> initData(){

        if(mData == null) {
            List<SettingsAdapter.SettingItem> data = new ArrayList<>();

            data.add(new SettingsAdapter.SettingItem(0, null, "照片","action", 0));
            data.add(new SettingsAdapter.SettingItem(1, uthumb, "title", "更换", 0));

            data.add(new SettingsAdapter.SettingItem(0, null, "用户名","action", 0));
            data.add(new SettingsAdapter.SettingItem(2, null, uname, "更改", R.drawable.ic_info_empty));

            data.add(new SettingsAdapter.SettingItem(0, null, "性别","action", 0));
            if(usex == 1){
                data.add(new SettingsAdapter.SettingItem(3, null, "女", "设置", R.drawable.ic_female));
            }else if(usex == 2){
                data.add(new SettingsAdapter.SettingItem(3, null, "男", "设置", R.drawable.ic_male));
            }else{
                data.add(new SettingsAdapter.SettingItem(3, null, "保密", "设置", R.drawable.ic_circle_outline_blue));
            }

            data.add(new SettingsAdapter.SettingItem(0, null, "登录密码","action", 0));
            data.add(new SettingsAdapter.SettingItem(4, null, "******", "更改", R.drawable.ic_lock_outline));

            mData = data;
        }

        return mData;

    }


}
