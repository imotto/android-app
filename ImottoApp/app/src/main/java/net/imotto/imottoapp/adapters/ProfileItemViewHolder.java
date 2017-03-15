package net.imotto.imottoapp.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import net.imotto.imottoapp.R;

/**
 * Created by sunht on 16/11/20.
 */
class ProfileItemViewHolder extends RecyclerView.ViewHolder {

    public ProfileItemViewHolder(View view) {
        super(view);

        lblInfo = (TextView) view.findViewById(R.id.lbl_info);
        lblTitle = (TextView) view.findViewById(R.id.lbl_title);
        imgThumb = (ImageView) view.findViewById(R.id.img_thumb);
    }

    public TextView lblTitle;
    public TextView lblInfo;
    public ImageView imgThumb;

}
