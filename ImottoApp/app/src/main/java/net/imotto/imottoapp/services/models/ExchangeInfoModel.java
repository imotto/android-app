package net.imotto.imottoapp.services.models;

/**
 * Created by sunht on 2016/12/12.
 */

public class ExchangeInfoModel {
    public long id;
    public String title;
    public String info;
    public boolean selected = false;

    public ExchangeInfoModel(UserAddressModel address) {
        this.id = address.ID;
        this.title = address.Contact + "(" + address.Mobile + ")";
        this.info = address.Province + address.City + address.District + address.Address;

    }

    public ExchangeInfoModel(RelAccountModel account){
        this.id = account.ID;
        this.title = account.AccountName;
        this.info = account.AccountNo;
    }

}
