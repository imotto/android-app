package net.imotto.imottoapp.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import net.imotto.imottoapp.R;
import net.imotto.imottoapp.services.models.UserRankModel;
import net.imotto.imottoapp.utils.CommUtils;

/**
 * Created by sunht on 2017/1/6.
 *
 */

public class UserRankItemViewHolder extends RecyclerView.ViewHolder {

    public ImageView imgThumb;
    public TextView lblUser;
    public TextView lblScore;
    public TextView lblRank;
    public ImageView imgModal;

    public UserRankItemViewHolder(View view){
        super(view);

        imgThumb = (ImageView) view.findViewById(R.id.img_thumb);
        imgModal = (ImageView) view.findViewById(R.id.img_medal);
        lblScore = (TextView) view.findViewById(R.id.lbl_score);
        lblRank = (TextView) view.findViewById(R.id.lbl_rank);
        lblUser = (TextView) view.findViewById(R.id.lbl_user);
    }

    public void setModel(final UserRankModel m, final OnUserDataItemActionListener listener){

        if(m.Rank <= 3){
            lblRank.setVisibility(View.GONE);
            imgModal.setVisibility(View.VISIBLE);

            if (m.Rank == 1){
                imgModal.setImageResource(R.drawable.icon_goldmedal);
            }else if(m.Rank == 2){
                imgModal.setImageResource(R.drawable.btn_silvermedal);
            }else if(m.Rank == 3){
                imgModal.setImageResource(R.drawable.btn_bronzemedal);
            }
        }else{
            lblRank.setVisibility(View.VISIBLE);
            imgModal.setVisibility(View.GONE);

            lblRank.setText("# "+ m.Rank);
        }

        lblUser.setText(m.UserName);
        lblScore.setText(Integer.toString(m.Score));

        if(listener!=null){
            imgThumb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onShowUserAction(m.UID, m.UserName, m.UserThumb);
                }
            });

        }

        if( m.UserThumb != null && !m.UserThumb.equals("")){
            ImageLoader.getInstance().displayImage(m.UserThumb, imgThumb,
                    CommUtils.getDisplayImageOptions());
        }else{
            imgThumb.setImageResource(R.drawable.ic_person_gray);
        }
    }
}
