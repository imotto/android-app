package net.imotto.imottoapp.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.readystatesoftware.systembartint.SystemBarTintManager;

import net.imotto.imottoapp.R;
import net.imotto.imottoapp.services.ImottoApi;
import net.imotto.imottoapp.services.models.ApiResp;
import net.imotto.imottoapp.utils.Constants;
import net.imotto.imottoapp.utils.PreferencesHelper;


/**
 * Created by sunht on 16/11/3.
 *
 */

public class AddMottoActivity extends BaseBackableActivity {
    private static final String TAG="AddMottoActivity";
    private EditText mTxtMotto;
    private boolean needSaveDraft = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_motto);
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

        mTxtMotto = (EditText) findViewById(R.id.txt_motto);

        ImageButton mSendBtn = (ImageButton) findViewById(R.id.toolbar_right_btn);
        mSendBtn.setImageResource(R.drawable.ic_paperplane_outline);
        mSendBtn.setVisibility(View.VISIBLE);
        mSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doSubmit();
            }
        });

        setupToolBar("创作");
    }

    @Override
    protected void onStart() {
        super.onStart();

        String draft = PreferencesHelper.getString(this, Constants.PREFS_DRAFT_MOTTO,"");
        if(!draft.isEmpty()){
            mTxtMotto.setText(draft);
            mTxtMotto.setSelection(draft.length());
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (needSaveDraft) {
            String draft = mTxtMotto.getText().toString();
            if (!draft.isEmpty()) {
                PreferencesHelper.putString(this, Constants.PREFS_DRAFT_MOTTO, mTxtMotto.getText().toString());
            }
        }else{
            PreferencesHelper.removeKey(AddMottoActivity.this, Constants.PREFS_DRAFT_MOTTO);
        }

    }

    private void doSubmit(){
        String motto = mTxtMotto.getText().toString();
        Log.i(TAG, motto);

        if (motto.length()<6){
            Toast.makeText(this, "偶得内容不能少于6个字。",Toast.LENGTH_SHORT).show();
            mTxtMotto.requestFocus();
            return;
        }

        final ProgressDialog dialog = ProgressDialog.show(this, null, "正在加载...", true, false);

        ImottoApi.getInstance().addMotto(0, motto, new ImottoApi.InvokeCompletionHandler<ApiResp>() {
            @Override
            public void onApiCallCompletion(ApiResp result) {
                dialog.dismiss();
                if (result.isSuccess()){
                    AlertDialog.Builder builder = new AlertDialog.Builder(AddMottoActivity.this);
                    builder.setTitle("偶得已提交");
                    builder.setMessage("您的偶得已开始接受评估，七天后结算积分。");
                    builder.setPositiveButton("好", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            //发布成功时，不再需要保存草稿
                            needSaveDraft = false;
                            goBack();
                        }
                    });

                    builder.create().show();

                }else{
                    Toast.makeText(AddMottoActivity.this, result.Msg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public Class<ApiResp> getGenericClass() {
                return ApiResp.class;
            }
        });


    }
}
