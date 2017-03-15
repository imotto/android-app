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
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import net.imotto.imottoapp.ImottoApplication;
import net.imotto.imottoapp.R;
import net.imotto.imottoapp.adapters.ExchangeInfoAdapter;
import net.imotto.imottoapp.adapters.RecyclerViewAdapter;
import net.imotto.imottoapp.controls.RecyclerViewDivider;
import net.imotto.imottoapp.services.ImottoApi;
import net.imotto.imottoapp.services.models.AddExchangeInfoResp;
import net.imotto.imottoapp.services.models.ApiResp;
import net.imotto.imottoapp.services.models.ExchangeInfoModel;
import net.imotto.imottoapp.services.models.PEResultModel;
import net.imotto.imottoapp.services.models.RelAccountModel;
import net.imotto.imottoapp.services.models.UserAddressModel;
import net.imotto.imottoapp.utils.CommUtils;
import net.imotto.imottoapp.utils.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunht on 2016/12/12.
 *
 */

public class SelExchangeInfoActivity extends BaseBackableActivity {
    private static final String TAG = "SelExInfo";

    public static final int REQUEST_ADD_ADDRESS=101;
    private RecyclerView mRecyclerView;
    private ExchangeInfoAdapter mAdapter;
    private PEResultModel prepareResult;
    private int giftId;
    private long selInfoId;
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
        String json = bundle.getString(Constants.BUNDLE_JSON_PERESULT);
        giftId = bundle.getInt(Constants.BUNDLE_GIFT_ID);
        prepareResult = CommUtils.fromJson(json, PEResultModel.class);

        setupToolBar(prepareResult.Data.ReqInfoHint);

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
    }

    private void initView(){

        if(prepareResult.Data.ReqInfoType == 0){
            //需要地址
            if(prepareResult.Data.Addresses != null && prepareResult.Data.Addresses.size()>0)
            {
                for (UserAddressModel addr: prepareResult.Data.Addresses){
                    mData.add(new ExchangeInfoModel(addr));
                }
                ExchangeInfoModel m = mData.get(0);
                m.selected = true;
                selInfoId = m.id;
            }

        }else{
            //需要关联账号
            if(prepareResult.Data.Accounts != null && prepareResult.Data.Accounts.size()>0)
            {
                for (RelAccountModel account: prepareResult.Data.Accounts){
                    mData.add(new ExchangeInfoModel(account));
                }

                ExchangeInfoModel m = mData.get(0);
                m.selected = true;
                selInfoId = m.id;
            }
        }


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

        TextView btnExchange = (TextView) findViewById(R.id.btn_exchange);
        btnExchange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doExchange();
            }
        });
    }

    private void doExchange(){

        if(selInfoId == 0){
            Toast.makeText(this, prepareResult.Data.ReqInfoHint, Toast.LENGTH_SHORT).show();
            return;
        }

        final ProgressDialog dialog = ProgressDialog.show(this, null, "正在加载...", true, false);
        ImottoApi.getInstance().doExchange(giftId, selInfoId, 1, new ImottoApi.InvokeCompletionHandler<ApiResp>() {
            @Override
            public void onApiCallCompletion(ApiResp result) {
                dialog.dismiss();
                if(result.isSuccess()){
                    Toast.makeText(SelExchangeInfoActivity.this, "兑换成功,请静候礼品发放", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                }else{
                    Toast.makeText(SelExchangeInfoActivity.this, result.Msg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public Class<ApiResp> getGenericClass() {
                return ApiResp.class;
            }
        });
    }

    private void addExchangeInfo(){

        if(prepareResult.Data.ReqInfoType == 0){
            //添加地址
            Intent intent = new Intent(this, AddAddressActivity.class);
            startActivityForResult(intent, REQUEST_ADD_ADDRESS);

        }else{
            //添加关联账号
            addRelAccount();
        }

    }

    private void addRelAccount(){
        final View modifyView = LayoutInflater.from(this).inflate(R.layout.dialog_add_rel_account, null, false);
        new AlertDialog.Builder(this).setTitle(prepareResult.Data.ReqInfoHint).setView(modifyView).
                setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1){
                        EditText txtAccountNo = (EditText)modifyView.findViewById(R.id.txt_account_no);
                        EditText txtAccountName = (EditText)modifyView.findViewById(R.id.txt_account_name);
                        final String accountNo = txtAccountNo.getText().toString();
                        final String accountName = txtAccountName.getText().toString();
                        if(accountNo.equals("")) {
                            Toast.makeText(SelExchangeInfoActivity.this, "账号不能为空，请重新输入", Toast.LENGTH_SHORT).show();
                        }else if(accountName.equals("")){
                            Toast.makeText(SelExchangeInfoActivity.this, "用户名称不能为空,请重新输入", Toast.LENGTH_SHORT).show();
                        }else {

                            final ProgressDialog dialog = ProgressDialog.show(SelExchangeInfoActivity.this, null, "正在加载...", true, false);
                            ImottoApi.getInstance().addRelAccount(prepareResult.Data.ReqInfoType, accountNo, accountName, new ImottoApi.InvokeCompletionHandler<AddExchangeInfoResp>() {
                                @Override
                                public void onApiCallCompletion(AddExchangeInfoResp result) {
                                    dialog.dismiss();
                                    processAddRelAccountResult(result, accountNo, accountName);
                                }

                                @Override
                                public Class<AddExchangeInfoResp> getGenericClass() {
                                    return AddExchangeInfoResp.class;
                                }
                            });
                        }
                    }
                }).setNegativeButton("取消",null).show();
    }

    private void processAddRelAccountResult(AddExchangeInfoResp result, String accountNo, String accountName){
        if(result.isSuccess()){
            RelAccountModel model = new RelAccountModel();
            model.AccountName = accountName;
            model.AccountNo = accountNo;
            model.Platform = prepareResult.Data.ReqInfoType;
            model.ID = result.Data;
            model.UID = ImottoApplication.getInstance().getUserId();
            prepareResult.Data.Accounts.add(0, model);

            ExchangeInfoModel emodel = new ExchangeInfoModel(model);
            mData.add(0, emodel);
            switchSelState(emodel);

        }else{
            Toast.makeText(this, result.Msg, Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_ADD_ADDRESS){
            if(resultCode == RESULT_OK){
                String json = data.getStringExtra(Constants.BUNDLE_JSON_ADDRESS);
                UserAddressModel model = CommUtils.fromJson(json, UserAddressModel.class);
                prepareResult.Data.Addresses.add(0, model);

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
