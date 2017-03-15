package net.imotto.imottoapp.adapters;

import net.imotto.imottoapp.services.models.AwardModel;

/**
 *
 * Created by sunht on 2017/1/12.
 */

public interface OnAwardItemActionListener {

    void onReceiveAwardAction(AwardModel m);

    void onSetAddressAction(AwardModel m);
}
