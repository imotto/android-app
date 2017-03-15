package net.imotto.imottoapp.services.models;

/**
 * Created by sunht on 16/10/27.
 */

public class ApiResp {
    public String Code;
    public int State;
    public String Msg;

    public boolean isSuccess(){
        return State == 0;
    }
}
