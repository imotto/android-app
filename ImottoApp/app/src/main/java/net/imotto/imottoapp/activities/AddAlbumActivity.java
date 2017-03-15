package net.imotto.imottoapp.activities;

import android.app.ProgressDialog;
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
import net.imotto.imottoapp.services.models.ApiResp;

/**
 * Created by annda on 16/11/23.
 */

public class AddAlbumActivity extends BaseBackableActivity {

    private EditText txtTitle;
    private EditText txtSummary;

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

        txtTitle = (EditText) findViewById(R.id.txt_title);
        txtSummary = (EditText) findViewById(R.id.txt_summary);

        setupToolBar(R.string.title_add_album);

        ImageButton btnSend = (ImageButton) findViewById(R.id.toolbar_right_btn);
        btnSend.setImageResource(R.drawable.ic_paperplane_outline);
        btnSend.setVisibility(View.VISIBLE);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = txtTitle.getText().toString();
                String summary = txtSummary.getText().toString();
                if(!title.isEmpty()){
                    final ProgressDialog dialog = ProgressDialog.show(AddAlbumActivity.this, null, "正在加载...", true, false);
                    ImottoApi.getInstance().addCollection(title, "", summary, new ImottoApi.InvokeCompletionHandler<ApiResp>() {
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
            Toast.makeText(AddAlbumActivity.this, "珍藏创建成功", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();
        }else{
            Toast.makeText(AddAlbumActivity.this, result.Msg, Toast.LENGTH_SHORT).show();
        }
    }
}
