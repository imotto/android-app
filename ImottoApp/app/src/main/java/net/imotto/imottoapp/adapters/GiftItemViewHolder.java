package net.imotto.imottoapp.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import net.imotto.imottoapp.R;
import net.imotto.imottoapp.services.models.GiftModel;


/**
 * Created by sunht on 2016/12/12.
 *
 */

public class GiftItemViewHolder extends RecyclerView.ViewHolder {

    public ImageView imgView;
    public TextView lblTitle;
    public TextView lblInfo;
    public TextView lblPrice;

    public GiftItemViewHolder(View view){
        super(view);

        imgView = (ImageView) view.findViewById(R.id.img_thumb);
        lblTitle = (TextView) view.findViewById(R.id.lbl_title);
        lblInfo = (TextView) view.findViewById(R.id.lbl_info);
        lblPrice = (TextView) view.findViewById(R.id.lbl_price);
    }

    public void setModel(GiftModel model){

        lblTitle.setText(model.Name);
        lblInfo.setText(model.Description);
        lblPrice.setText(Integer.toString(model.Price));

        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.ic_gift)
                .showImageForEmptyUri(R.drawable.ic_gift)
                .showImageOnFail(R.drawable.ic_gift)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                //.displayer()
                .build();

        ImageLoader.getInstance().displayImage(model.Img, imgView, options);

    }
}
