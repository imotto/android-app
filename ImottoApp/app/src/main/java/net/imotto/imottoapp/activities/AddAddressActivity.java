package net.imotto.imottoapp.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.readystatesoftware.systembartint.SystemBarTintManager;

import net.imotto.imottoapp.R;
import net.imotto.imottoapp.controls.SelCityPopupWindow;
import net.imotto.imottoapp.services.ImottoApi;
import net.imotto.imottoapp.services.models.AddExchangeInfoResp;
import net.imotto.imottoapp.services.models.UserAddressModel;
import net.imotto.imottoapp.utils.CommUtils;
import net.imotto.imottoapp.utils.Constants;

/**
 * Created by sunht on 2016/12/13.
 *
 */

public class AddAddressActivity extends BaseBackableActivity {

    private TextView btnSelCity;
    private EditText txtAddr;
    private EditText txtZip;
    private EditText txtContact;
    private EditText txtMobile;
    private String mPronvice;
    private String mCity;
    private String mArea;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_address);
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

        btnSelCity = (TextView) findViewById(R.id.btn_sel_city);
        txtAddr = (EditText) findViewById(R.id.txt_addr);
        txtZip = (EditText) findViewById(R.id.txt_zip);
        txtContact = (EditText) findViewById(R.id.txt_contact);
        txtMobile = (EditText) findViewById(R.id.txt_mobile);

        setupToolBar("添加地址");

        ImageButton btnSend = (ImageButton) findViewById(R.id.toolbar_right_btn);
        btnSend.setImageResource(R.drawable.ic_done);
        btnSend.setVisibility(View.VISIBLE);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doAddUserAddress();
            }
        });


        btnSelCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBtnSelCitySelected(true);
                SelCityPopupWindow popupWindow = new SelCityPopupWindow(AddAddressActivity.this, mPronvice, mCity, mArea);
                popupWindow.showAtLocation(btnSelCity, Gravity.BOTTOM, 0, 0);


                popupWindow.setOnCitySelectedListener(new SelCityPopupWindow.OnCitySelectedListener() {
                    @Override
                    public void onSelected(String province, String city, String area) {
                        mPronvice = province;
                        mCity = city;
                        mArea = area;
                        setBtnSelCitySelected(false);
                    }

                    @Override
                    public void onCanceled() {
                        setBtnSelCitySelected(false);
                    }
                });
            }
        });
    }

    private void doAddUserAddress(){
        if (mPronvice == null){
            Toast.makeText(this, "请选择城市", Toast.LENGTH_SHORT).show();
            return;
        }

        String addr = txtAddr.getText().toString();
        String contact = txtContact.getText().toString();
        String mobile = txtMobile.getText().toString();
        String zip = txtZip.getText().toString();
        
        if(addr.isEmpty()){
            Toast.makeText(this, "请填写具体地址", Toast.LENGTH_SHORT).show();
            return;
        }
        if(contact.isEmpty()){
            Toast.makeText(this, "请填写收件人", Toast.LENGTH_SHORT).show();
            return;
        }
        if(mobile.isEmpty()){
            Toast.makeText(this, "请填写收件人手机号码", Toast.LENGTH_SHORT).show();
            return;
        }

        final UserAddressModel model = new UserAddressModel();
        model.Address = addr;
        model.District = mArea;
        model.City = mCity;
        model.Province = mPronvice;
        model.Zip = zip;
        model.Contact = contact;
        model.Mobile = mobile;

        final ProgressDialog dialog = ProgressDialog.show(this, null, "正在加载...", true, false);
        ImottoApi.getInstance().addAddress(model, new ImottoApi.InvokeCompletionHandler<AddExchangeInfoResp>() {
            @Override
            public void onApiCallCompletion(AddExchangeInfoResp result) {
                dialog.dismiss();
                if(result.isSuccess()) {
                    model.ID = result.Data;
                    onAddressAdded(model);
                }else{
                    Toast.makeText(AddAddressActivity.this, result.Msg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public Class<AddExchangeInfoResp> getGenericClass() {
                return AddExchangeInfoResp.class;
            }
        });

    }

    private void onAddressAdded(UserAddressModel model){

        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        String json = CommUtils.toJson(model);
        bundle.putString(Constants.BUNDLE_JSON_ADDRESS,json);
        intent.putExtras(bundle);

        setResult(RESULT_OK, intent);
        finish();
    }

    private void setBtnSelCitySelected(boolean selected){
        if(selected){
            btnSelCity.setBackgroundResource(R.color.colorPrimary);
            btnSelCity.setTextColor(Color.WHITE);
            btnSelCity.setText("选择城市");
        }else{
            btnSelCity.setBackgroundColor(Color.WHITE);
            btnSelCity.setTextColor(Color.rgb(0x1c,0x8a,0xdd));
            if(mPronvice!=null && mPronvice!="")
            btnSelCity.setText(mPronvice+mCity+mArea);
        }
    }


}
