package net.imotto.imottoapp.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.allenliu.badgeview.BadgeFactory;
import com.allenliu.badgeview.BadgeView;
import com.nostra13.universalimageloader.core.ImageLoader;

import net.imotto.imottoapp.R;
import net.imotto.imottoapp.services.models.RecentTalkModel;
import net.imotto.imottoapp.utils.CommUtils;
import net.imotto.imottoapp.utils.DateHelper;

/**
 * Created by sunht on 16/11/13.
 */

public class RecentTalkItemViewHolder extends RecyclerView.ViewHolder {

    public RecentTalkItemViewHolder(View view){
        super(view);

        this.lblUser = (TextView) view.findViewById(R.id.lbl_user);
        this.lblContent = (TextView) view.findViewById(R.id.lbl_content);
        this.lblTime = (TextView) view.findViewById(R.id.lbl_time);
        this.imgThumb = (ImageView) view.findViewById(R.id.img_thumb);
    }

    public TextView lblUser;
    public TextView lblTime;
    public TextView lblContent;
    public ImageView imgThumb;

    public BadgeView badge;

    public void setModel(final RecentTalkModel m, final OnTalkItemActionListener listener, Context context){
        this.lblUser.setText(m.UserName);
        this.lblContent.setText(m.Content);
        this.lblTime.setText(DateHelper.friendlyTime(m.LastTime));

        ImageLoader.getInstance().displayImage(m.UserThumb, imgThumb,
                CommUtils.getDisplayImageOptions());

        if(m.Msgs>0){
            badge = BadgeFactory//.createDot(context).bind(mholder.imgThumb);
                    .create(context).setTextColor(Color.WHITE)
                    .setWidthAndHeight(18,18)
                    .setBadgeBackground(Color.RED)
                    .setTextSize(10)
                    .setBadgeGravity(Gravity.RIGHT | Gravity.TOP)
                    .setBadgeCount(m.Msgs)
                    .setShape(BadgeView.SHAPE_CIRCLE)
                    .setMargin(0,0,5,0)
                    .bind(imgThumb);
            badge.setVisibility(View.VISIBLE);
        }else if(badge != null) {
            badge.setVisibility(View.GONE);
            badge.unbind();
            badge = null;
        }

        if(listener != null){
            imgThumb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onShowUserAction(m);
                }
            });
        }
    }
}
