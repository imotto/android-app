package net.imotto.imottoapp.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import net.imotto.imottoapp.R;
import net.imotto.imottoapp.services.models.AwardModel;

/**
 *
 * Created by sunht on 2017/1/12.
 */

public class AwardItemViewHolder extends RecyclerView.ViewHolder {

    public ImageView imgThumb;
    public TextView lblTtile;
    public TextView lblStatus;
    public TextView lblInfo;
    public TextView btnReceive;
    public TextView btnSetAddress;
    public TextView lblSubTitle;

    public AwardItemViewHolder(View view){
        super(view);

        imgThumb = (ImageView) view.findViewById(R.id.img_thumb);
        lblTtile = (TextView) view.findViewById(R.id.lbl_title);
        lblInfo = (TextView) view.findViewById(R.id.lbl_info);
        lblStatus = (TextView) view.findViewById(R.id.lbl_status);
        btnReceive = (TextView) view.findViewById(R.id.btn_receive);
        btnSetAddress = (TextView) view.findViewById(R.id.btn_set_address);
        lblSubTitle = (TextView) view.findViewById(R.id.lbl_sub_title);
    }




    public void setModel(final AwardModel model, final OnAwardItemActionListener listener){
        lblTtile.setText(model.Name);

        lblSubTitle.setText(String.format("【%d年第%d期奖品】", model.ID/100, model.ID%100));

        btnSetAddress.setVisibility(View.GONE);
        btnReceive.setVisibility(View.GONE);

        btnSetAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener!=null){
                    listener.onSetAddressAction(model);
                }
            }
        });

        btnReceive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener!=null){
                    listener.onReceiveAwardAction(model);
                }
            }
        });

        String status;

        if(model.Status == 0){
            status ="评估中";
        }else {
            //GainStatus : 0:未获得 ,1：待确认地址，2: 待发放,3: 已发放, 4:已签收
            switch (model.GainStatus) {
                case 0:
                    status = "未获得";
                    break;
                case 1:
                    status = "待确认地址";
                    btnSetAddress.setVisibility(View.VISIBLE);
                    break;
                case 2:
                    status = "待寄出";
                    break;
                case 3:
                    status = "已寄出";
                    btnReceive.setVisibility(View.VISIBLE);
                    break;
                case 4:
                    status = "已签收";
                    break;
                default:
                    status = "";
                    break;
            }
        }


        lblStatus.setText(status);
        lblInfo.setText(model.Summary);


        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.ic_gift)
                .showImageForEmptyUri(R.drawable.ic_gift)
                .showImageOnFail(R.drawable.ic_gift)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                //.displayer()
                .build();

        ImageLoader.getInstance().displayImage(model.Img, imgThumb, options);
    }
}
