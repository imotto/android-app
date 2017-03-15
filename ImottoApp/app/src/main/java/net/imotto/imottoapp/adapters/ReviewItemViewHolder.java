package net.imotto.imottoapp.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import net.imotto.imottoapp.R;
import net.imotto.imottoapp.services.models.ReviewModel;
import net.imotto.imottoapp.utils.CommUtils;
import net.imotto.imottoapp.utils.DateHelper;

/**
 * Created by sunht on 16/11/17.
 */

public class ReviewItemViewHolder extends RecyclerView.ViewHolder {
    public ReviewItemViewHolder(View view){
        super(view);

        imgThumb = (ImageView) view.findViewById(R.id.img_thumb);
        btnMore = (ImageView) view.findViewById(R.id.btn_more);
        btnUp = (ImageView) view.findViewById(R.id.btn_up);

        lblUser = (TextView) view.findViewById(R.id.lbl_user);
        lblContent = (TextView) view.findViewById(R.id.lbl_content);
        lblUps = (TextView) view.findViewById(R.id.lbl_support);
        lblTime = (TextView) view.findViewById(R.id.lbl_time);
    }

    public ImageView imgThumb;
    public ImageView btnMore;
    public ImageView btnUp;
    public TextView lblUser;
    public TextView lblContent;
    public TextView lblUps;
    public TextView lblTime;

    public void setModel(final ReviewModel m, final OnReviewItemActionListener listener){
        lblUser.setText(m.UserName);
        lblTime.setText(DateHelper.friendlyTime(m.AddTime));
        lblContent.setText(m.Content);
        lblUps.setText(Integer.toString(m.Up));

        btnUp.setImageResource(m.Supported == 1? R.drawable.ic_thumb_up : R.drawable.ic_thumb_up_outline);

        ImageLoader.getInstance().displayImage(m.UserThumb, imgThumb,
                CommUtils.getDisplayImageOptions());

        if(listener!=null){
            imgThumb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onShowUserAction(m);
                }
            });

            btnUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onVoteAction(m, true);
                }
            });

            btnMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onMoreAction(m);
                }
            });
        }
    }
}
