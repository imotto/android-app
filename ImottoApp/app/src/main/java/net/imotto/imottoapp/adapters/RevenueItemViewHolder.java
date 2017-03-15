package net.imotto.imottoapp.adapters;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import net.imotto.imottoapp.R;
import net.imotto.imottoapp.services.models.BillRecordModel;

/**
 * Created by sunht on 16/11/15.
 */

public class RevenueItemViewHolder extends RecyclerView.ViewHolder {
    public RevenueItemViewHolder(View view){
        super(view);

        imgThumb = (ImageView) view.findViewById(R.id.img_thumb);
        lblTitle = (TextView) view.findViewById(R.id.lbl_title);
        lblInfo = (TextView) view.findViewById(R.id.lbl_info);
        lblScore = (TextView) view.findViewById(R.id.lbl_score);
    }

    public ImageView imgThumb;
    public TextView lblTitle;
    public TextView lblInfo;
    public TextView lblScore;

    public void setModel(BillRecordModel m){
        lblTitle.setText(m.Summary);
        lblInfo.setText(m.ChangeTime);
        if(m.ChangeType == 0){
            lblScore.setText("+"+m.ChangeAmount);
            lblScore.setTextColor(Color.rgb(0, 128, 0));
            imgThumb.setImageResource(R.drawable.ic_trendingup);
        }else {
            lblScore.setText(Integer.toString(m.ChangeAmount));
            lblScore.setTextColor(Color.RED);
            imgThumb.setImageResource(R.drawable.ic_trendingdown);
        }
    }
}
