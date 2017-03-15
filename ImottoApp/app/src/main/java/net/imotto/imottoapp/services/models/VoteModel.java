package net.imotto.imottoapp.services.models;

/**
 * Created by sunht on 2016/12/18.
 *
 */

public class VoteModel {
    public long ID;
    public long MID;
    //1 时表示反对，与Support字段互斥
    public int Oppose;
    //1 时表示支持
    public int Support;
    public int TheDay;
    public String VoteTime;
    public String UID;
    public String UserName;
    public String UserThumb;

}
