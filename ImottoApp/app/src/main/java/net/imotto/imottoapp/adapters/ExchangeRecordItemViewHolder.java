package net.imotto.imottoapp.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import net.imotto.imottoapp.R;
import net.imotto.imottoapp.services.models.ExchangeRecordModel;
import net.imotto.imottoapp.utils.DateHelper;

/**
 * Created by sunht on 2016/12/21.
 *
 */

public class ExchangeRecordItemViewHolder extends RecyclerView.ViewHolder {

    public ImageView imgThumb;
    public TextView lblTtile;
    public TextView lblStatus;
    public TextView lblInfo;
    public TextView btnReceive;
    public TextView btnReview;

    public ExchangeRecordItemViewHolder(View view){
        super(view);

        imgThumb = (ImageView) view.findViewById(R.id.img_thumb);
        lblTtile = (TextView) view.findViewById(R.id.lbl_title);
        lblInfo = (TextView) view.findViewById(R.id.lbl_info);
        lblStatus = (TextView) view.findViewById(R.id.lbl_status);
        btnReceive = (TextView) view.findViewById(R.id.btn_receive);
        btnReview = (TextView) view.findViewById(R.id.btn_review);
    }

    public void setModel(final ExchangeRecordModel model, final OnExchangeRecordActionListener listener){
        lblTtile.setText(model.GiftName);

        btnReview.setVisibility(View.GONE);
        btnReceive.setVisibility(View.GONE);

        btnReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener!=null){
                    listener.onReviewGiftAction(model);
                }
            }
        });

        btnReceive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener!=null){
                    listener.onReceiveGiftAction(model);
                }
            }
        });

        String status;
        String statusHint;
        switch (model.DeliverState){
            case 0:
                status = "待发放";
                statusHint = "请耐心等待礼品发放";
                break;
            case 1:
                status = "运送中";
                statusHint = "请耐心等待礼品投递";
                btnReceive.setVisibility(View.VISIBLE);
                break;
            case 2:
                status = "已签收";
                statusHint = "已经确认签收";
                btnReview.setVisibility(View.VISIBLE);
                break;
            case 3:
                status = "已退货";
                statusHint = "已经进行退货处理";
                break;
            case 9:
                status = "已评价";
                statusHint = "交易已完成";
                break;
            default:
                status="";
                statusHint = "已无法跟踪状态";
                break;
        }

        String info = "您在"+
                DateHelper.friendlyTime(model.ExchangeTime)
                +"使用"+model.Total+"枚金币兑换"+model.Amount+"件此礼品，"+statusHint+" 。";

        lblStatus.setText(status);
        lblInfo.setText(info);


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
