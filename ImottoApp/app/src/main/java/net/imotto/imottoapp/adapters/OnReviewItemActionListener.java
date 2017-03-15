package net.imotto.imottoapp.adapters;

import net.imotto.imottoapp.services.models.ReviewModel;

/**
 * Created by annda on 16/11/22.
 */

public interface OnReviewItemActionListener {

    void onShowUserAction(ReviewModel m);

    void onVoteAction(ReviewModel m, boolean support);

    void onMoreAction(ReviewModel m);
}
