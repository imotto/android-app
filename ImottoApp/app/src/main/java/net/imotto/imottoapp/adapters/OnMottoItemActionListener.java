package net.imotto.imottoapp.adapters;

import net.imotto.imottoapp.services.models.MottoModel;

/**
 * Created by sunht on 16/11/9.
 *
 */

public interface OnMottoItemActionListener {

    void onLoveAction(MottoModel m, MottoItemViewHolder holder);

    void onReviewAction(MottoModel m);

    void onMarkAction(MottoModel m);

    void onMoreAction(MottoModel m);

    void onShowUserAction(MottoModel m);

    void onShowVotesAction(MottoModel m);

    /**
     * 投票动作
     * @param m
     * @param holder
     * @param vote  1:支持，-1：反对， 0: 中立
     */
    void onVoteAction(MottoModel m, MottoItemViewHolder holder, int vote);
}
