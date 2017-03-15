package net.imotto.imottoapp.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import net.imotto.imottoapp.R;
import net.imotto.imottoapp.services.models.AlbumModel;
import net.imotto.imottoapp.utils.DateHelper;

/**
 * Created by sunht on 16/11/18.
 */

public class AlbumSelItemViewHolder extends RecyclerView.ViewHolder {

    public AlbumSelItemViewHolder(View view){
        super(view);

        this.lblTitle = (TextView) view.findViewById(R.id.lbl_title);
        this.lblInfo = (TextView) view.findViewById(R.id.lbl_info);
        this.lblSummary = (TextView) view.findViewById(R.id.lbl_summary);
        this.imgSel = (ImageView) view.findViewById(R.id.img_sel);
    }

    public TextView lblTitle;
    public TextView lblSummary;
    public TextView lblInfo;
    public ImageView imgSel;

    public void setModel(AlbumModel m){
        this.lblTitle.setText(m.Title);
        this.lblSummary.setText(m.Description);
        this.lblInfo.setText(String.format("%s创建 • 收录%d则偶得 • %d人喜欢",
                DateHelper.friendlyTime(m.CreateTime), m.Mottos, m.Loves));

        imgSel.setImageResource(m.ContainsMID>0 ? R.drawable.ic_checkmark_outline : R.drawable.ic_circle_outline);
    }
}
