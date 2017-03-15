package net.imotto.imottoapp.adapters;

import net.imotto.imottoapp.services.models.RelatedUserModel;

/**
 * Created by annda on 16/11/22.
 */

public interface OnUserItemActionListener {

    void onShowMottosAction(RelatedUserModel model);

    void onShowFollowsAction(RelatedUserModel model);

    void onShowFollowersAction(RelatedUserModel model);

    void onShowRankAction(RelatedUserModel model);

    void onLoveAction(RelatedUserModel model);
}
