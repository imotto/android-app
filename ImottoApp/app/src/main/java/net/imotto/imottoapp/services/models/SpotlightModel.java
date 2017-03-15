package net.imotto.imottoapp.services.models;

/**
 * Created by sunht on 2017/1/11.
 * 注册设备时返回的附加消息，一般用于特殊提示.
 */

public class SpotlightModel {
    /**
     * yyyyMM 当前月份
     */
    public int TheMonth;
    /**
     * 类型： 1:上月排行榜出炉，2：公布当月奖品设置，3：其它
     */
    public int Type;
    public String Img;
    public String Info;
}
