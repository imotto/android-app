package net.imotto.imottoapp.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import net.imotto.imottoapp.R;
import net.imotto.imottoapp.services.models.TalkMsgModel;
import net.imotto.imottoapp.utils.CommUtils;
import net.imotto.imottoapp.utils.DateHelper;

/**
 * Created by sunht on 2017/1/4.
 *
 */

public class ChatMsgItemViewHolder extends RecyclerView.ViewHolder {

    public TextView lblTime;
    public View wrapperReceiveMsg;
    public View wrapperSendMsg;
    public ImageView imgThumb;
    public TextView lblContent;
    public ImageView imgThumbMe;
    public TextView lblContentMe;

    public ChatMsgItemViewHolder(View view){
        super(view);

        lblTime = (TextView) view.findViewById(R.id.lbl_time);
        wrapperReceiveMsg = view.findViewById(R.id.wrapper_receive_msg);
        wrapperSendMsg = view.findViewById(R.id.wrapper_send_msg);
        imgThumb = (ImageView) view.findViewById(R.id.img_thumb);
        lblContent = (TextView) view.findViewById(R.id.lbl_content);
        imgThumbMe = (ImageView) view.findViewById(R.id.img_thumb_me);
        lblContentMe = (TextView) view.findViewById(R.id.lbl_content_me);
    }

    public void setModel(TalkMsgModel m, String uthumb, boolean showTime){

        if(showTime){
            lblTime.setText(DateHelper.friendlyTime(m.SendTime));
            lblTime.setVisibility(View.VISIBLE);
        }else{
            lblTime.setVisibility(View.GONE);
        }

        if(m.Direction == 1){
            //receive
            wrapperSendMsg.setVisibility(View.GONE);
            wrapperReceiveMsg.setVisibility(View.VISIBLE);
            lblContent.setText(m.Content);


            ImageLoader.getInstance().displayImage(uthumb, imgThumb,
                    CommUtils.getDisplayImageOptions());

        }else{
            //send
            wrapperSendMsg.setVisibility(View.VISIBLE);
            wrapperReceiveMsg.setVisibility(View.GONE);
            lblContentMe.setText(m.Content);


            ImageLoader.getInstance().displayImage(uthumb, imgThumbMe,
                    CommUtils.getDisplayImageOptions());
        }
    }


}
