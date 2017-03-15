package net.imotto.imottoapp.activities;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import net.imotto.imottoapp.R;
import net.imotto.imottoapp.services.ImottoApi;
import net.imotto.imottoapp.services.models.GiftModel;
import net.imotto.imottoapp.services.models.PEResultModel;
import net.imotto.imottoapp.utils.CommUtils;
import net.imotto.imottoapp.utils.Constants;


/**
 * Created by sunht on 2016/12/12.
 *
 */

public class GiftDetailActivity extends BaseBackableActivity {

    private static final int REQUEST_CODE_SEL_EXCHANGE_INFO = 101;
    private GiftModel model;
    private PEResultModel prepareResult;
    private TextView lblBalance;
    private TextView btnExchange;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gift_detail);
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

        Bundle bundle = getIntent().getExtras();
        String json = bundle.getString(Constants.BUNDLE_JSON_GIFT);

        model = CommUtils.fromJson(json, GiftModel.class);

        setupToolBar(model.Name);

        setupView();


        prepareExchange();
    }

    private void setupView(){

        lblBalance = (TextView) findViewById(R.id.lbl_balance);
        btnExchange = (TextView) findViewById(R.id.btn_exchange);

        btnExchange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doExchange();
            }
        });

        TextView lblTitle = (TextView) findViewById(R.id.lbl_title);
        lblTitle.setText(model.Name);

        TextView lblInfo = (TextView) findViewById(R.id.lbl_info);
        lblInfo.setText(model.Description);

        TextView lblAvailable = (TextView) findViewById(R.id.lbl_avaiable);
        lblAvailable.setText(Integer.toString(model.Available));

        TextView lblPrice = (TextView) findViewById(R.id.lbl_price);
        lblPrice.setText(Integer.toString(model.Price));

        TextView lblVendor = (TextView) findViewById(R.id.lbl_vendor);
        lblVendor.setText("由【"+model.Vendor+"】提供");

//        TextView lblReviews = (TextView) findViewById(R.id.lbl_reviews);
//        if (model.Reviews>0){
//            lblReviews.setText("查看评价("+ model.Reviews +")");
//            lblReviews.setTextColor(Color.rgb(0x1c,0xa8,0xdd));
//        }else{
//            lblReviews.setText("还没有人评价");
//        }

        TextView lblDetailLink = (TextView) findViewById(R.id.lbl_detail_link);
        if(model.URL != ""){
            lblDetailLink.setText("查看详细信息");
            lblDetailLink.setTextColor(Color.rgb(0x1c,0xa8,0xdd));

            lblDetailLink.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(model.URL));
                    startActivity(intent);
                }
            });

        }else{
            lblDetailLink.setText("未提供详细信息");
        }

        ImageView imgThumb = (ImageView) findViewById(R.id.img_thumb);
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.ic_gift)
                .showImageForEmptyUri(R.drawable.ic_gift)
                .showImageOnFail(R.drawable.ic_gift)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                //.displayer()
                .build();

        ImageLoader.getInstance().displayImage(model.Img, imgThumb, options);

    }

    private void doExchange(){
        if(prepareResult!=null){
            Intent intent = new Intent(this, SelExchangeInfoActivity.class);
            Bundle bundle = new Bundle();
            String json = CommUtils.toJson(prepareResult);
            bundle.putString(Constants.BUNDLE_JSON_PERESULT, json);
            bundle.putInt(Constants.BUNDLE_GIFT_ID, model.ID);
            intent.putExtras(bundle);
            startActivityForResult(intent, REQUEST_CODE_SEL_EXCHANGE_INFO);

        }else{
            prepareExchange();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_CODE_SEL_EXCHANGE_INFO){
            prepareExchange();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void prepareExchange(){
        ImottoApi.getInstance().prepareExchange(model.ID, model.RequireInfo, new ImottoApi.InvokeCompletionHandler<PEResultModel>() {
            @Override
            public void onApiCallCompletion(PEResultModel result) {
                processResult(result);
            }

            @Override
            public Class<PEResultModel> getGenericClass() {
                return PEResultModel.class;
            }
        });

    }

    private void processResult(PEResultModel result){
        if(result.isSuccess()){
            lblBalance.setText(Integer.toString(result.Data.Balance));
            prepareResult = result;
            btnExchange.setClickable(true);
        }else{
            Toast.makeText(this, "无法读取您的可用金币", Toast.LENGTH_SHORT).show();
            prepareResult = null;
        }
    }
}
