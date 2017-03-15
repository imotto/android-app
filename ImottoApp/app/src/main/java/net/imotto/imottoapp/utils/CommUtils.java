package net.imotto.imottoapp.utils;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.CircleBitmapDisplayer;

import net.imotto.imottoapp.R;

import java.lang.reflect.Type;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by sunht on 16/11/3.
 */

public class CommUtils {

    private static DisplayImageOptions options;

    public static boolean isMobileNO(String mobiles){
        Pattern p = Pattern.compile("^1[345789]\\d{9}$");
        Matcher m = p.matcher(mobiles);

        return m.matches();
    }

    public static DisplayImageOptions getDisplayImageOptions(){
        if(options == null){
            options = new DisplayImageOptions.Builder()
                    .showImageOnLoading(R.drawable.ic_person_gray)
                    .showImageForEmptyUri(R.drawable.ic_person_gray)
                    .showImageOnFail(R.drawable.ic_person_gray)
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .considerExifParams(true)
                    .displayer(new CircleBitmapDisplayer(Color.WHITE, 1))
                    .build();
        }

        return options;
    }

    /**
     * 对象转换成json字符串
     * @param obj
     * @return
     */
    public static String toJson(Object obj) {
        Gson gson = new Gson();
        return gson.toJson(obj);
    }

    /**
     * json字符串转成对象
     * @param str
     * @param type
     * @return
     */
    public static <T> T fromJson(String str, Type type) {
        Gson gson = new Gson();
        return gson.fromJson(str, type);
    }

    /**
     * json字符串转成对象
     * @param str
     * @param type
     * @return
     */
    public static <T> T fromJson(String str, Class<T> type) {
        Gson gson = new Gson();
        return gson.fromJson(str, type);
    }

    public static String friendlyNumber(int num){
        if(num<1000){
            return Integer.toString(num);
        }else if (num<1000000){
            return String.format("%.1fK", num/1000.0);
        }else{
            return String.format("%.1fM", num/1000000.0);
        }
    }


    public static int dp2px(int dp, Context context)
    {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources()
                .getDisplayMetrics());
    }

}
