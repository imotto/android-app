package net.imotto.imottoapp.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.allenliu.badgeview.BadgeFactory;
import com.allenliu.badgeview.BadgeView;

import net.imotto.imottoapp.R;
import net.imotto.imottoapp.services.models.NoticeModel;
import net.imotto.imottoapp.utils.DateHelper;

/**
 * Created by sunht on 16/11/13.
 */

public class NoticeItemViewHolder extends RecyclerView.ViewHolder {

    public NoticeItemViewHolder(View view){
        super(view);

        lblInfo = (TextView) view.findViewById(R.id.lbl_info);
        lblTime = (TextView) view.findViewById(R.id.lbl_time);
        lblTitle = (TextView) view.findViewById(R.id.lbl_title);
        imgThumb = (ImageView) view.findViewById(R.id.img_thumb);

    }

    public TextView lblInfo;
    public TextView lblTime;
    public TextView lblTitle;
    public ImageView imgThumb;
    public BadgeView badge;

    public void setModel(NoticeModel m, Context context){

        //imgThumb.setImageResource(m.State == 1? R.drawable.ic_email_open : R.drawable.ic_email);
        lblTitle.setTextColor(m.State == 1? Color.rgb(0x99,0x99,0x99) : Color.rgb(0x33,0x33,0x33));

        lblTitle.setText(m.Title);
        lblInfo.setText(m.Content);
        lblTime.setText(DateHelper.friendlyTime(m.SendTime));

        if(m.State == 0){
            if(badge==null) {
                badge = BadgeFactory//.createCircle(context).setBadgeCount(model.Msgs).bind(mholder.imgThumb);
                        .createDot(context)
                        .bind(imgThumb);
            }else{
                badge.setVisibility(View.VISIBLE);
            }
        }else if(badge!=null) {
            badge.setVisibility(View.GONE);
            badge.unbind();
            badge = null;
        }
    }
}
