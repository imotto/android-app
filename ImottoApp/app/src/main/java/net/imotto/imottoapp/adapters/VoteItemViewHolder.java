package net.imotto.imottoapp.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import net.imotto.imottoapp.R;
import net.imotto.imottoapp.services.models.VoteModel;
import net.imotto.imottoapp.utils.CommUtils;
import net.imotto.imottoapp.utils.DateHelper;

/**
 * Created by sunht on 2016/12/18.
 *
 */

public class VoteItemViewHolder extends RecyclerView.ViewHolder {

    public VoteItemViewHolder(View view){
        super(view);

        lblUser = (TextView) view.findViewById(R.id.lbl_user);

        imgThumb = (ImageView) view.findViewById(R.id.img_thumb);
        lblInfo = (TextView) view.findViewById(R.id.lbl_info);
        lblScore = (TextView) view.findViewById(R.id.lbl_score);
    }

    public TextView lblUser;
    public ImageView imgThumb;
    public TextView lblInfo;
    public TextView lblScore;


    public void setModel(final VoteModel model, final OnUserDataItemActionListener listener){

        lblUser.setText(model.UserName);
        lblInfo.setText(DateHelper.friendlyTime(model.VoteTime)+ " 觉得" + (model.Support == 1 ?"有趣":"无聊"));

        if(model.Support ==1){
            lblScore.setText("+1");
        }else{
            lblScore.setText("-1");
        }

        if(listener!=null){
            imgThumb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onShowUserAction(model.UID, model.UserName, model.UserThumb);
                }
            });
        }

        ImageLoader.getInstance().displayImage(model.UserThumb, imgThumb,
                CommUtils.getDisplayImageOptions());

    }
}
