package net.imotto.imottoapp.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import net.imotto.imottoapp.R;
import net.imotto.imottoapp.services.models.RelatedUserModel;
import net.imotto.imottoapp.utils.CommUtils;


/**
 * Created by sunht on 16/11/15.
 *
 */

public class UserItemViewHolder extends RecyclerView.ViewHolder {

    public UserItemViewHolder(View view){
        super(view);

        imgThumb = (ImageView) view.findViewById(R.id.img_thumb);
        imgSex = (ImageView) view.findViewById(R.id.img_sex);
        imgRealtion = (ImageView) view.findViewById(R.id.img_relation);
        imgScore = (ImageView) view.findViewById(R.id.img_score);

        lblUser = (TextView) view.findViewById(R.id.lbl_user);
        lblMottos = (TextView) view.findViewById(R.id.lbl_mottos);
        lblFollows = (TextView) view.findViewById(R.id.lbl_follows);
        lblFollowers = (TextView) view.findViewById(R.id.lbl_followers);
        lblRelation = (TextView) view.findViewById(R.id.lbl_relation);
        lblScore = (TextView) view.findViewById(R.id.lbl_score);

        lblMotto = (TextView) view.findViewById(R.id.lbl_motto);
        lblFollow = (TextView) view.findViewById(R.id.lbl_follow);
        lblFollower = (TextView) view.findViewById(R.id.lbl_follower);
    }

    public ImageView imgThumb;
    public ImageView imgSex;
    public ImageView imgRealtion;
    public ImageView imgScore;

    public TextView lblUser;
    public TextView lblMottos;
    public TextView lblMotto;
    public TextView lblFollows;
    public TextView lblFollow;
    public TextView lblFollowers;
    public TextView lblFollower;
    public TextView lblRelation;
    public TextView lblScore;

    public void setModel(final RelatedUserModel m, final OnUserItemActionListener listener){
        lblUser.setText(m.DisplayName);
        lblScore.setText(Integer.toString(m.Revenue));
        lblMottos.setText(Integer.toString(m.Mottos));
        lblFollows.setText(Integer.toString(m.Follows));
        lblFollowers.setText(Integer.toString(m.Followers));
        //relationState
        switch (m.RelationState){
            case 0:
                lblRelation.setText(R.string.lbl_norelation);
                imgRealtion.setImageResource(R.drawable.ic_relation_heart_outline);
                break;
            case 1:
                lblRelation.setText(R.string.lbl_youlikehim);
                imgRealtion.setImageResource(R.drawable.ic_relation_heart);
                break;
            case 2:
                lblRelation.setText(R.string.lbl_helikeyou);
                imgRealtion.setImageResource(R.drawable.ic_relation_heart_outline);
                break;
            case 3:
                lblRelation.setText(R.string.lbl_mutuallike);
                imgRealtion.setImageResource(R.drawable.ic_relation_heart_pulse);
                break;
            case 4:
                lblRelation.setText(R.string.lbl_donotlike);
                imgRealtion.setImageResource(R.drawable.ic_relation_heart_broken);
                break;
            case 6:
                lblRelation.setText(R.string.lbl_embrassed);
                imgRealtion.setImageResource(R.drawable.ic_relation_heart_broken);
                break;
        }

        if( m.Thumb != null && !m.Thumb.equals("")){
            ImageLoader.getInstance().displayImage(m.Thumb, imgThumb,
                    CommUtils.getDisplayImageOptions());
        }else{
            imgThumb.setImageResource(R.drawable.ic_person_gray);
        }

        switch (m.Sex){
            case 1:
                imgSex.setVisibility(View.VISIBLE);
                imgSex.setImageResource(R.drawable.ic_female);
                break;
            case 2:
                imgSex.setVisibility(View.VISIBLE);
                imgSex.setImageResource(R.drawable.ic_male);
                break;
            default:
                imgSex.setVisibility(View.GONE);
                break;
        }

        if(listener != null){
            lblMottos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onShowMottosAction(m);
                }
            });

            lblMotto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onShowMottosAction(m);
                }
            });

            lblFollows.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onShowFollowsAction(m);
                }
            });

            lblFollow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onShowFollowsAction(m);
                }
            });

            lblFollowers.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onShowFollowersAction(m);
                }
            });

            lblFollower.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onShowFollowersAction(m);
                }
            });

            imgScore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onShowRankAction(m);
                }
            });

            lblScore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onShowRankAction(m);
                }
            });

//            lblRelation.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    listener.onLoveAction(m);
//                }
//            });
//
//            imgRealtion.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    listener.onLoveAction(m);
//                }
//            });

        }
    }


}
