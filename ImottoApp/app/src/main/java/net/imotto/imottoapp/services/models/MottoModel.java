package net.imotto.imottoapp.services.models;

/**
 * Created by sunht on 16/10/31.
 */

public class MottoModel {
    public long ID;
    public String UID;
    public double Score;
    public int Up;
    public int Down;
    public int Reviews;
    public int Loves;
    public int RecruitID;
    public String RecruitTitle;
    public String Content;
    public String AddTime;
    public String UserName;
    public String UserThumb;
    public int Reviewed;
    public int State;
    public int Loved;
    /**
     *  投票状态： 1：已支持，-1：反对，0：中立，9：未投票
     */
    public int Vote;
    public int Collected;
}
