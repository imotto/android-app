package net.imotto.imottoapp.services;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.loopj.android.http.BaseJsonHttpResponseHandler;

//import java.lang.reflect.ParameterizedType;
//import java.lang.reflect.Type;

/**
 * Created by sunht on 16/10/27.
 */

public abstract class GsonHttpResponseHandler<T> extends BaseJsonHttpResponseHandler<T> {
    private final String TAG = this.getClass().getSimpleName();
    private Gson gson = new Gson();
    private Class<T> tClass;

    public GsonHttpResponseHandler(Class<T> tclass){
        this.tClass = tclass;
    }

    @Override
    protected T parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
        if (!isFailure && !TextUtils.isEmpty(rawJsonData)) {
            try {
                //Type genType = getClass().getGenericSuperclass();
                //Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
                //Class<T> objCls = (Class) params[0];
                //放弃使用反射
                return gson.fromJson(rawJsonData, tClass);
            } catch (Exception e) {
                Log.e(TAG, "json parser error!!!! "+e.getLocalizedMessage());
            }
        }
        return null;
    }
}
