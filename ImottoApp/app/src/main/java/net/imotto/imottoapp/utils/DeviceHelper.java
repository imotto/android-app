package net.imotto.imottoapp.utils;

import android.content.Context;
import android.telephony.TelephonyManager;

import java.util.UUID;

/**
 * Created by sunht on 16/10/28.
 */

public class DeviceHelper{
    /**
     * @Title: isValidateDeviceId
     * @Description: TODO(检查是否为有效果的Deviceid)
     * @param @param id
     * @param @return    设定文件
     * @return boolean    返回类型
     * @throws
     */

    public static boolean isValidateDeviceId(String id) {
        if (id.equals("000000000000000")) { // 0000... 是一种情况
            return false;
        }
        return true;
    }

    /**
     * @Title: getUUID
     * @Description: TODO(通过设备信息生成UUID-32)
     * @param @param context
     * @param @return    设定文件
     * @return String    返回类型
     * @throws
     */

    public static String getUUID(Context context) {
        final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        final String tmDevice, tmSerial, androidId;
        tmDevice = "" + tm.getDeviceId();

        if (isValidateDeviceId(tmDevice)){
            return tmDevice;
        }

        tmSerial = "" + tm.getSimSerialNumber();

        androidId = "" + android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

        UUID deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
        String uniqueId = deviceUuid.toString();


        return uniqueId.replace("-", "");
    }



}
