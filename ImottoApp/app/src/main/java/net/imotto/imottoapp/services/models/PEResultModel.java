package net.imotto.imottoapp.services.models;

import java.util.List;

/**
 * Created by annda on 2016/12/12.
 */

public class PEResultModel extends ApiResp {

    public ResultWrapper Data;

    public class ResultWrapper{
        public int Balance;
        public int ReqInfoType;
        public String ReqInfoHint;

        public List<RelAccountModel> Accounts;
        public List<UserAddressModel> Addresses;

    }
}
