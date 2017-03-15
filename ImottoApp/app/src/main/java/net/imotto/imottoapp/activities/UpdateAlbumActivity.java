package net.imotto.imottoapp.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.readystatesoftware.systembartint.SystemBarTintManager;

import net.imotto.imottoapp.R;
import net.imotto.imottoapp.services.ImottoApi;
import net.imotto.imottoapp.services.models.AlbumModel;
import net.imotto.imottoapp.services.models.ApiResp;
import net.imotto.imottoapp.utils.CommUtils;
import net.imotto.imottoapp.utils.Constants;

/**
 * Created by sunht on 2016/12/27.
 *
 */

public class UpdateAlbumActivity extends BaseBackableActivity {

    private EditText txtTitle;
    private EditText txtSummary;
    private AlbumModel album;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_album);
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

        Intent intent = getIntent();
        String json = intent.getStringExtra(Constants.BUNDLE_JSON_ALBUM);
        album = CommUtils.fromJson(json, AlbumModel.class);

        txtTitle = (EditText) findViewById(R.id.txt_title);
        txtSummary = (EditText) findViewById(R.id.txt_summary);

        txtTitle.setText(album.Title);
        txtSummary.setText(album.Description);

        setupToolBar(R.string.title_update_album);

        ImageButton btnSend = (ImageButton) findViewById(R.id.toolbar_right_btn);
        btnSend.setImageResource(R.drawable.ic_paperplane_outline);
        btnSend.setVisibility(View.VISIBLE);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = txtTitle.getText().toString();
                String summary = txtSummary.getText().toString();
                if(!title.isEmpty()){
                    album.Title = title;
                    album.Description = summary;
                    final ProgressDialog dialog = ProgressDialog.show(UpdateAlbumActivity.this, null, "正在加载...", true, false);
                    ImottoApi.getInstance().updateCollection(album.ID, title, "", summary, new ImottoApi.InvokeCompletionHandler<ApiResp>() {
                        @Override
                        public void onApiCallCompletion(ApiResp result) {
                            dialog.dismiss();
                            processResult(result);
                        }

                        @Override
                        public Class<ApiResp> getGenericClass() {
                            return ApiResp.class;
                        }
                    });
                }
            }
        });
    }

    private void processResult(ApiResp result){
        if(result.isSuccess()){
            Toast.makeText(UpdateAlbumActivity.this, "珍藏信息更新成功", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent();
            String json = CommUtils.toJson(album);
            intent.putExtra(Constants.BUNDLE_JSON_ALBUM, json);
            setResult(RESULT_OK, intent);
            finish();
        }else{
            Toast.makeText(UpdateAlbumActivity.this, result.Msg, Toast.LENGTH_SHORT).show();
        }
    }
}
