package net.imotto.imottoapp.activities;

import android.app.ProgressDialog;
import android.content.Intent;
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

import me.zhanghai.android.materialratingbar.MaterialRatingBar;

/**
 * Created by sunht on 2016/12/27.
 *
 */

public class ReviewGiftActivity extends BaseBackableActivity {

    private MaterialRatingBar ratingBar;
    private EditText txtSummary;
    private long exchangeId;
    private int giftId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_gift);
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
        giftId = intent.getIntExtra(Constants.BUNDLE_GIFT_ID, 0);
        exchangeId = intent.getLongExtra(Constants.BUNDLE_EXCHANGE_ID, 0);

        ratingBar = (MaterialRatingBar) findViewById(R.id.rating_bar);
        txtSummary = (EditText) findViewById(R.id.txt_review);

        ratingBar.setRating(5.0f);

        setupToolBar(R.string.title_review_gift);

        ImageButton btnSend = (ImageButton) findViewById(R.id.toolbar_right_btn);
        btnSend.setImageResource(R.drawable.ic_paperplane_outline);
        btnSend.setVisibility(View.VISIBLE);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.i("reviewGift", "star is :"+ratingBar.getRating());
                float rating = ratingBar.getRating();
                String review = txtSummary.getText().toString();
                final ProgressDialog dialog = ProgressDialog.show(ReviewGiftActivity.this, null, "正在提交...", true, false);
                ImottoApi.getInstance().reviewGift(giftId, exchangeId, rating, review, new ImottoApi.InvokeCompletionHandler<ApiResp>() {
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
        });
    }

    private void processResult(ApiResp result){
        if(result.isSuccess()){
            Toast.makeText(ReviewGiftActivity.this, "礼品评价已成功提交.", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent();
            intent.putExtra(Constants.BUNDLE_EXCHANGE_ID, exchangeId);
            setResult(RESULT_OK, intent);

            finish();
        }else{
            Toast.makeText(ReviewGiftActivity.this, result.Msg, Toast.LENGTH_SHORT).show();
        }
    }
}
