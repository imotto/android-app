package net.imotto.imottoapp.controls;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;


import net.imotto.imottoapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SelCityPopupWindow extends PopupWindow implements View.OnClickListener {
    private static final String TAG="SelCityPopupWindow";

    private WheelView wvProvince;
    private WheelView wvCitys;
    private WheelView wvArea;
    private View lyChangeAddress;
    private View lyChangeAddressChild;
    private TextView btnSure;
    private TextView btnCancel;

    private Context context;
    private JSONObject mJsonObj;
    /**
     * 所有省
     */
    private ArrayList<String> mProvinces = new ArrayList<>();
    /**
     * key - 省 value - 市s
     */
    private Map<String, List<String>> mCitys = new HashMap<>();
    /**
     * key - 市 values - 区s
     */
    private Map<String, List<String>> mAreas = new HashMap<>();

    private String strProvince ;
    private String strCity;
    private String strArea;
    private OnCitySelectedListener onCitySelectedListener;

    private boolean isIniting = true;

    public SelCityPopupWindow(final Context context, String province, String city, String area) {
        super(context);
        this.context = context;
        this.strProvince = province;
        this.strCity = city;
        this.strArea = area;

        View view = View.inflate(context, R.layout.actionsheet_sel_city, null);

        wvProvince = (WheelView) view.findViewById(R.id.wv_address_province);
        wvCitys = (WheelView) view.findViewById(R.id.wv_address_city);
        wvArea = (WheelView) view.findViewById(R.id.wv_address_area);
        lyChangeAddress = view.findViewById(R.id.ly_myinfo_changeaddress);
        lyChangeAddressChild = view.findViewById(R.id.ly_myinfo_changeaddress_child);
        btnSure = (TextView) view.findViewById(R.id.btn_myinfo_sure);
        btnCancel = (TextView) view.findViewById(R.id.btn_myinfo_cancel);


        //设置SelectPicPopupWindow的View
        this.setContentView(view);
        //设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        //设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        //设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        //设置SelectPicPopupWindow弹出窗体动画效果
//      this.setAnimationStyle(R.style.AnimBottom);
        //实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0xb0000000);
        //设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);

        lyChangeAddressChild.setOnClickListener(this);
        btnSure.setOnClickListener(this);
        btnCancel.setOnClickListener(this);

        initJsonData();
        initDatas();

        wvProvince.setOffset(2);
        wvProvince.setItems(mProvinces);

        int idx = 0;
        if(strProvince!=null){
            idx = mProvinces.indexOf(strProvince);
            if(idx<0){
                idx = 0;
            }
        }
        wvProvince.setSeletion(idx);
        String currentProvince = wvProvince.getSeletedItem();
        onProvinceSelected(currentProvince);

        wvProvince.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
            @Override
            public void onSelected(int selectedIndex, String item) {
                onProvinceSelected(item);
            }
        });

        wvCitys.setOnWheelViewListener(new WheelView.OnWheelViewListener(){
            @Override
            public void onSelected(int selectedIndex, String item) {
                onCitySelected(item);
            }
        });

        wvArea.setOnWheelViewListener(new WheelView.OnWheelViewListener(){
            @Override
            public void onSelected(int selectedIndex, String item) {
                onAreaSelected(item);
            }
        });
    }

    private void onProvinceSelected(String currentProvince){
        strProvince = currentProvince;
        List<String> citys = mCitys.get(currentProvince);

        wvCitys.setOffset(2);
        wvCitys.setItems(citys);
        int idx = 0;
        if(isIniting){
            if(strCity!=null) {
                idx = citys.indexOf(strCity);
                if (idx<0){
                    idx = 0;
                }
            }
        }
        wvCitys.setSeletion(idx);

        String currentCity = wvCitys.getSeletedItem();
        onCitySelected(currentCity);
    }

    private void onCitySelected(String currentCity){

        strCity = currentCity;
        List<String> areas = mAreas.get(strProvince+ "-"+ currentCity);
        wvArea.setOffset(2);
        wvArea.setItems(areas);
        int idx = 0;
        if(isIniting){
            if(strArea!=null){
                idx = areas.indexOf(strArea);
                if(idx<0){
                    idx = 0;
                }
            }

            isIniting = false;
        }
        wvArea.setSeletion(idx);

        String currentArea = wvArea.getSeletedItem();
        onAreaSelected(currentArea);
    }

    private void onAreaSelected(String currentArea){
        strArea = currentArea;
    }

    public void setOnCitySelectedListener(OnCitySelectedListener listener) {
        this.onCitySelectedListener = listener;
    }

    @Override
    public void onClick(View v) {
        if (v == btnSure) {
            if (onCitySelectedListener != null) {
                onCitySelectedListener.onSelected(strProvince, strCity,strArea);
            }
        } else if (v == btnCancel) {
            if(onCitySelectedListener!=null){
                onCitySelectedListener.onCanceled();
            }
        } else if (v == lyChangeAddressChild) {
            return;
        }
        dismiss();
    }

    /**
     * 回调接口
     *
     */
    public interface OnCitySelectedListener {
        void onSelected(String province, String city, String area);
        void onCanceled();
    }

    /**
     * 从文件中读取地址数据
     */
    private void initJsonData() {
        try {
            StringBuffer sb = new StringBuffer();
            AssetManager manager = context.getAssets();
            InputStream is = manager.open("citys.json");

            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String str;
            while (true){
                str = reader.readLine();
                if(str != null){
                    sb.append(str);
                }else{
                    break;
                }
            }

            is.close();
            mJsonObj = new JSONObject(sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 解析整个Json对象，完成后释放Json对象的内存
     */
    private void initDatas()
    {
        try
        {
            JSONArray jsonArray = mJsonObj.getJSONArray("p");
            for (int i = 0; i < jsonArray.length(); i++)
            {
                mProvinces.add(jsonArray.getString(i));
            }

            JSONObject citysObj = mJsonObj.getJSONObject("c");
            JSONObject areasObj = mJsonObj.getJSONObject("a");
            for (String province : mProvinces) {
                //取省内城市
                JSONArray cityArr = citysObj.getJSONArray(province);
                List<String> citys = new ArrayList<>();
                for(int i = 0; i<cityArr.length();i++){
                    String city = cityArr.getString(i);
                    citys.add(city);

                    //取城市内区县
                    String areaKey = province+"-"+city;
                    JSONArray areaArr = areasObj.getJSONArray(areaKey);
                    List<String> areas = new ArrayList<>();
                    for(int j=0;j<areaArr.length();j++){
                        areas.add(areaArr.getString(j));
                    }

                    mAreas.put(areaKey, areas); //城区map的键为  省-市，以避免市名重复.
                }
                mCitys.put(province, citys);
            }

        } catch (JSONException e)
        {
            e.printStackTrace();
        }
        mJsonObj = null;
    }
}