package net.imotto.imottoapp.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import net.imotto.imottoapp.R;
import net.imotto.imottoapp.adapters.ExchangeInfoAdapter;
import net.imotto.imottoapp.adapters.RecyclerViewAdapter;
import net.imotto.imottoapp.controls.RecyclerViewDivider;
import net.imotto.imottoapp.services.ImottoApi;
import net.imotto.imottoapp.services.models.ApiResp;
import net.imotto.imottoapp.services.models.ExchangeInfoModel;
import net.imotto.imottoapp.services.models.ReadAddressResp;
import net.imotto.imottoapp.services.models.UserAddressModel;
import net.imotto.imottoapp.utils.CommUtils;
import net.imotto.imottoapp.utils.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by sunht on 2017/1/13.
 */

public class SelAddressActivity extends BaseBackableActivity {
    public static final int REQUEST_ADD_ADDRESS=101;
    private RecyclerView mRecyclerView;
    private ExchangeInfoAdapter mAdapter;
    private int awardId;
    private long selInfoId = 0;
    private List<ExchangeInfoModel> mData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sel_exchange_info);
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

        awardId = bundle.getInt(Constants.BUNDLE_AWARD_ID);

        setupToolBar(R.string.title_set_award_address);

        ImageButton btnSend = (ImageButton) findViewById(R.id.toolbar_right_btn);
        btnSend.setImageResource(R.drawable.ic_plus_small);
        btnSend.setVisibility(View.VISIBLE);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addExchangeInfo();
            }
        });

        initView();

        loadUserAddresses();
    }

    private void initView(){

        mRecyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this)
                .colorResId(R.color.colorRecyclerViewDivider)
                .sizeResId(R.dimen.recycler_view_divider).build());
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        mAdapter = new ExchangeInfoAdapter(this, mData);
        mAdapter.setOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                ExchangeInfoModel model = mAdapter.getDataItem(position);
                switchSelState(model);
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });

        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new RecyclerViewDivider(this,LinearLayoutManager.VERTICAL, 2, R.color.colorLightGray));

        mAdapter.setShowEndHint(false);
        mAdapter.setEof(true);


        TextView btnCancel = (TextView) findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        TextView btnConfirm = (TextView) findViewById(R.id.btn_exchange);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmSetAddress();
            }
        });
    }

    private void confirmSetAddress(){
        if(selInfoId == 0){
            Toast.makeText(this, "请提供奖品寄送地址", Toast.LENGTH_SHORT).show();
            return;
        }
        
        new AlertDialog.Builder(this).setTitle("确认地址").setMessage("确认要将奖品寄送至选中的地址吗？")
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        doSetAddress();
                    }
                }).show();

    }

    private void doSetAddress(){
        final ProgressDialog dialog = ProgressDialog.show(this, "正在设置...","");
        ImottoApi.getInstance().setAwardAddress(awardId, selInfoId, new ImottoApi.InvokeCompletionHandler<ApiResp>() {
            @Override
            public void onApiCallCompletion(ApiResp result) {
                dialog.dismiss();

                if(result.isSuccess()){
                    Intent intent = new Intent();
                    intent.putExtra(Constants.BUNDLE_AWARD_ID, awardId);
                    setResult(RESULT_OK, intent);
                    finish();
                }else{
                    Toast.makeText(SelAddressActivity.this, result.Msg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public Class<ApiResp> getGenericClass() {
                return ApiResp.class;
            }
        });
    }

    private void loadUserAddresses(){
        ImottoApi.getInstance().readAddresses(new ImottoApi.InvokeCompletionHandler<ReadAddressResp>() {
            @Override
            public void onApiCallCompletion(ReadAddressResp result) {
                if(result.isSuccess()){
                    processAddresses(result.Data);
                }else{
                    Toast.makeText(SelAddressActivity.this, result.Msg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public Class<ReadAddressResp> getGenericClass() {
                return ReadAddressResp.class;
            }
        });
    }

    private void processAddresses(List<UserAddressModel> addresses){
        for (UserAddressModel addr: addresses){
            mData.add(new ExchangeInfoModel(addr));
        }
        if(mData.size()>0) {
            ExchangeInfoModel m = mData.get(0);
            m.selected = true;
            selInfoId = m.id;
        }

        mAdapter.notifyDataSetChanged();
    }



    private void addExchangeInfo(){
        //添加地址
        Intent intent = new Intent(this, AddAddressActivity.class);
        startActivityForResult(intent, REQUEST_ADD_ADDRESS);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_ADD_ADDRESS){
            if(resultCode == RESULT_OK){
                String json = data.getStringExtra(Constants.BUNDLE_JSON_ADDRESS);
                UserAddressModel model = CommUtils.fromJson(json, UserAddressModel.class);

                ExchangeInfoModel emodel = new ExchangeInfoModel(model);
                mData.add(0, emodel);
                switchSelState(emodel);
            }
        }
    }

    private void switchSelState(ExchangeInfoModel model){
        selInfoId = model.id;
        if(!model.selected){
            for (ExchangeInfoModel m : mData){
                m.selected = false;
            }

            model.selected = true;
            mAdapter.notifyDataSetChanged();
        }
    }
}
