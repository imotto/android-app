package net.imotto.imottoapp.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import net.imotto.imottoapp.R;

/**
 * Created by sunht on 16/11/20.
 */

public class PhotoItemViewHolder extends RecyclerView.ViewHolder {

    public PhotoItemViewHolder(View view){
        super(view);

        imgThumb = (ImageView) view.findViewById(R.id.img_thumb);
        lblInfo = (TextView) view.findViewById(R.id.lbl_info);
    }

    public ImageView imgThumb;
    public TextView lblInfo;
}
