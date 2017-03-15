package net.imotto.imottoapp.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import net.imotto.imottoapp.R;
import net.imotto.imottoapp.services.models.ExchangeInfoModel;

/**
 * Created by sunht on 2016/12/12.
 *
 */

public class ExchangeInfoItemViewHolder extends RecyclerView.ViewHolder {


    public ExchangeInfoItemViewHolder(View view){
        super(view);
        this.lblTitle = (TextView) view.findViewById(R.id.lbl_title);
        this.lblInfo = (TextView) view.findViewById(R.id.lbl_info);
        this.imgSel = (ImageView) view.findViewById(R.id.img_sel);
    }

    public TextView lblTitle;
    public TextView lblInfo;
    public ImageView imgSel;

    public void setModel(ExchangeInfoModel m){
        this.lblTitle.setText(m.title);
        this.lblInfo.setText(m.info);

        imgSel.setImageResource(m.selected ? R.drawable.ic_checkmark_outline : R.drawable.ic_circle_outline);
    }
}
