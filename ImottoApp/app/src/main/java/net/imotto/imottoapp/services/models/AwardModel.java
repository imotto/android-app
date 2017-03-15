package net.imotto.imottoapp.services.models;

/**
 * Created by sunht on 2017/1/11.
 *
 */

public class AwardModel {
    public int ID;
    public String Name;
    public String Summary;
    public String Img;
    /**
     * 奖品状态：0：评估中 , 1: 已结束
     */
    public int Status;

    public int Amount;
    /**
     * 0:未获得 ,1：待确认地址，2: 待发放,3: 已发放, 4:已签收
     */
    public int GainStatus;
}
