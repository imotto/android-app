package net.imotto.imottoapp.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.readystatesoftware.systembartint.SystemBarTintManager;

import net.imotto.imottoapp.ImottoApplication;
import net.imotto.imottoapp.R;
import net.imotto.imottoapp.services.ImottoApi;
import net.imotto.imottoapp.services.models.ApiResp;
import net.imotto.imottoapp.utils.Constants;
import net.imotto.imottoapp.utils.PreferencesHelper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by sunht on 16/11/21.
 */

public class PickThumbActivity extends BaseBackableActivity {
    private static final String TAG="PickThumb";
    private ImageView imgThumb;
    private Button btnGallery;
    private Button btnCamera;
    private static final int PERMISSION_REQUEST_CAMERA = 11;
    private static final int PERMISSION_REQUEST_STORAGE = 12;
    private static final int PHOTO_REQUEST_TAKEPHOTO = 1;// 拍照
    private static final int PHOTO_REQUEST_GALLERY = 2;// 从相册中选择
    private static final int PHOTO_REQUEST_CUT = 3;// 结果
    private Bitmap thumbBitmap;

    File tempFile = new File(Environment.getExternalStorageDirectory(), getPhotoFileName());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_thumb);
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

        imgThumb = (ImageView) findViewById(R.id.img_thumb);
        btnGallery = (Button) findViewById(R.id.btn_gallery);
        btnCamera = (Button) findViewById(R.id.btn_camera);

        setupToolBar(R.string.title_pick_thumb);

        initView();
    }

    private void initView() {

        btnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, null);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*");
                startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
            }
        });

        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(PickThumbActivity.this,
                        Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    //申请WRITE_EXTERNAL_STORAGE权限
                    ActivityCompat.requestPermissions(PickThumbActivity.this, new String[]{Manifest.permission.CAMERA},
                            PERMISSION_REQUEST_CAMERA);
                }else {
                    takePhoto();
                }

            }
        });


        ImageView btnOk = (ImageView) findViewById(R.id.toolbar_right_btn);
        btnOk.setImageResource(R.drawable.ic_done);
        btnOk.setVisibility(View.VISIBLE);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //确认选择图片
                if(imgThumb.getDrawable()!=null){
                    ImottoApi.getInstance().modifyThumb(tempFile, new ImottoApi.InvokeCompletionHandler<ApiResp>() {
                        @Override
                        public void onApiCallCompletion(ApiResp result) {
                            processResult(result);
                        }

                        @Override
                        public Class<ApiResp> getGenericClass() {
                            return ApiResp.class;
                        }
                    });

                }else{
                    Toast.makeText(PickThumbActivity.this, "请先选取要使用的照片", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void processResult(ApiResp result){
        if(result.isSuccess()){
            PreferencesHelper.putString(this, Constants.PREFS_USERTHUMB, result.Msg);
            ImottoApplication.getInstance().refreshUserInfo();
            Toast.makeText(this, "用户头像更新成功", Toast.LENGTH_SHORT).show();
            setResult(SettingActivity.REQUEST_CODE_PICK_THUMB);
            finish();
        }else{
            Toast.makeText(this, result.Msg, Toast.LENGTH_SHORT).show();
        }
    }

    private void takePhoto(){
        // 调用系统的拍照功能
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // 指定调用相机拍照后照片的储存路径
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));
        startActivityForResult(intent, PHOTO_REQUEST_TAKEPHOTO);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if(requestCode == PERMISSION_REQUEST_CAMERA){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                takePhoto();
            }else{
                Toast.makeText(this, "未授权使用摄像头", Toast.LENGTH_SHORT).show();
            }
        }else if(requestCode == PERMISSION_REQUEST_STORAGE){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                saveBitmap();
            }else{
                Toast.makeText(this, "无法保存修剪后的图片", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PHOTO_REQUEST_TAKEPHOTO:
                startPhotoZoom(Uri.fromFile(tempFile), 240);
                break;

            case PHOTO_REQUEST_GALLERY:
                if (data != null)
                    startPhotoZoom(data.getData(), 240);
                break;

            case PHOTO_REQUEST_CUT:
                if (data != null)
                    setPicToView(data);
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void startPhotoZoom(Uri uri, int size) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // crop为true是设置在开启的intent中设置显示的view可以剪裁
        intent.putExtra("crop", "true");

        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);

        // outputX,outputY 是剪裁图片的宽高
        intent.putExtra("outputX", size);
        intent.putExtra("outputY", size);
        intent.putExtra("return-data", true);

        startActivityForResult(intent, PHOTO_REQUEST_CUT);
    }

    //将进行剪裁后的图片显示到UI界面上
    private void setPicToView(Intent picdata) {
        Bundle bundle = picdata.getExtras();
        if (bundle != null) {
            Bitmap photo = bundle.getParcelable("data");
            thumbBitmap = photo;
            if (ContextCompat.checkSelfPermission(PickThumbActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                //申请WRITE_EXTERNAL_STORAGE权限
                ActivityCompat.requestPermissions(PickThumbActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        PERMISSION_REQUEST_STORAGE);
            }else {
                saveBitmap();
            }
            imgThumb.setImageBitmap(photo);
        }
    }

    /** 保存方法 */
    public void saveBitmap() {
        Log.e(TAG, "保存图片");

        if (tempFile.exists()) {
            tempFile.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(tempFile);
            thumbBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
            Log.i(TAG, "已经保存");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // 使用系统当前日期加以调整作为照片的名称
    private String getPhotoFileName() {
        return ImottoApplication.getInstance().getUserId()+".jpg";
    }
}
