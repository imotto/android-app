package net.imotto.imottoapp.adapters;

import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import net.imotto.imottoapp.R;
import net.imotto.imottoapp.controls.MottoItemHeader;
import net.imotto.imottoapp.services.models.MottoModel;
import net.imotto.imottoapp.utils.CommUtils;
import net.imotto.imottoapp.utils.DateHelper;

/**
 * Created by sunht on 16/11/9.
 */

public class MottoItemViewHolder extends RecyclerView.ViewHolder {

    public MottoItemViewHolder(View view){
        super(view);

        headerPermanent = (MottoItemHeader) view.findViewById(R.id.header_permanent);
        actionNormal = view.findViewById(R.id.view_action_container);
        actionVote = view.findViewById(R.id.view_vote_container);
        lblContent = (TextView) view.findViewById(R.id.lbl_content);
        lblTime = (TextView) view.findViewById(R.id.lbl_time);
        btnMore = (ImageView) view.findViewById(R.id.btn_more);
        btnMark = (ImageView) view.findViewById(R.id.btn_mark);
        lblReview = (TextView) view.findViewById(R.id.lbl_review);
        btnReview = (ImageView) view.findViewById(R.id.btn_review);
        lblLove = (TextView) view.findViewById(R.id.lbl_love);
        btnLove = (ImageView) view.findViewById(R.id.btn_love);
        imgThumb = (ImageView) view.findViewById(R.id.img_thumb);
        lblPScore = (TextView) view.findViewById(R.id.lbl_pscore);
        lblUser = (TextView) view.findViewById(R.id.lbl_user);

        imgCoins = (ImageView) view.findViewById(R.id.img_coins);

        btnUp =  view.findViewById(R.id.btn_support);
        btnDown = view.findViewById(R.id.btn_oppose);
        btnNofeeling = view.findViewById(R.id.btn_nofeeling);
    }

    public TextView lblContent;
    public TextView lblTime;
    public ImageView btnMore;
    public ImageView btnMark;
    public TextView lblReview;
    public ImageView btnReview;
    public TextView lblLove;
    public ImageView btnLove;

    public ImageView imgThumb;

    public ImageView imgCoins;
    public TextView lblPScore;
    public TextView lblUser;

    public View actionNormal;
    public View actionVote;

    public View btnUp;
    public View btnDown;
    public View btnNofeeling;

    public MottoItemHeader headerPermanent;

    public void setModel(final MottoModel m, boolean onlyShowTime, final OnMottoItemActionListener listener){

        if(onlyShowTime){
            lblTime.setText(DateHelper.timeOnly(m.AddTime));
        }else {
            lblTime.setText(DateHelper.friendlyTime(m.AddTime));
        }


        lblContent.setText(m.Content);
        lblLove.setText(CommUtils.friendlyNumber(m.Loves));
        lblReview.setText(CommUtils.friendlyNumber(m.Reviews));

        btnLove.setImageResource(m.Loved == 1 ? R.drawable.btn_love_on : R.drawable.btn_love);
        btnReview.setImageResource(m.Reviewed == 1 ? R.drawable.btn_comment_on : R.drawable.btn_comment);
        btnMark.setImageResource(m.Collected == 1 ? R.drawable.btn_collection_on : R.drawable.btn_collection);


        if (m.State == 0) {
            //evaluating
            if(m.Vote == 9){
                actionVote.setVisibility(View.VISIBLE);
                actionNormal.setVisibility(View.GONE);
                headerPermanent.setVisibility(View.GONE);
                ViewGroup.LayoutParams params = lblContent.getLayoutParams();

                btnUp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (listener != null) {
                            listener.onVoteAction(m, MottoItemViewHolder.this, 1);
                        }
                    }
                });

                btnDown.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(listener!=null){
                            listener.onVoteAction(m, MottoItemViewHolder.this, -1);
                        }
                    }
                });

                btnNofeeling.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(listener!=null){
                            listener.onVoteAction(m, MottoItemViewHolder.this, 0);
                        }
                    }
                });

            }else{
                actionVote.setVisibility(View.GONE);
                actionNormal.setVisibility(View.VISIBLE);
                headerPermanent.setVisibility(View.VISIBLE);

                lblPScore.setText(CommUtils.friendlyNumber(m.Up - m.Down));
                lblUser.setText(m.UserName);

                ImageLoader.getInstance().displayImage(m.UserThumb, imgThumb,
                        CommUtils.getDisplayImageOptions());
            }

        } else {
            //permanent
            headerPermanent.setVisibility(View.VISIBLE);
            actionNormal.setVisibility(View.VISIBLE);
            actionVote.setVisibility(View.GONE);
            lblPScore.setText(CommUtils.friendlyNumber(m.Up - m.Down));
            lblUser.setText(m.UserName);

            ImageLoader.getInstance().displayImage(m.UserThumb, imgThumb,
                    CommUtils.getDisplayImageOptions());


        }


        imgThumb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onShowUserAction(m);
                }
            }
        });

        lblUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onShowUserAction(m);
                }
            }
        });

        imgCoins.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null){
                    listener.onShowVotesAction(m);
                }
            }
        });

        lblPScore.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(listener != null){
                    listener.onShowVotesAction(m);
                }
            }
        });

        btnLove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onLoveAction(m, MottoItemViewHolder.this);
                }
            }
        });

        btnReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onReviewAction(m);
                }
            }
        });

        btnMark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onMarkAction(m);
                }
            }
        });

        btnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onMoreAction(m);
                }
            }
        });
    }

    public void refreshVoteState(MottoModel m){
        if(m.Vote == 9){
            actionVote.setVisibility(View.VISIBLE);
            actionNormal.setVisibility(View.GONE);
            headerPermanent.setVisibility(View.GONE);

        }else{
            actionVote.setVisibility(View.GONE);
            actionNormal.setVisibility(View.VISIBLE);
            headerPermanent.setVisibility(View.VISIBLE);

            lblPScore.setText(CommUtils.friendlyNumber(m.Up - m.Down));
            lblUser.setText(m.UserName);

            ImageLoader.getInstance().displayImage(m.UserThumb, imgThumb,
                    CommUtils.getDisplayImageOptions());
        }
    }
}
