package net.imotto.imottoapp.adapters;

import net.imotto.imottoapp.services.models.ExchangeRecordModel;

/**
 * Created by sunht on 2016/12/21.
 *
 */

public interface OnExchangeRecordActionListener {

    void onReceiveGiftAction(ExchangeRecordModel m);

    void onReviewGiftAction(ExchangeRecordModel m);
}
