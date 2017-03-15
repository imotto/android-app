package net.imotto.imottoapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import net.imotto.imottoapp.ImottoApplication;
import net.imotto.imottoapp.R;
import net.imotto.imottoapp.services.models.TalkMsgModel;
import net.imotto.imottoapp.utils.DateHelper;

import java.util.List;

/**
 * Created by sunht on 16/11/22.
 *
 */

public class TalkMsgsAdapter extends RecyclerViewAdapter<TalkMsgModel> {

    private String withUname;
    private String withUThumb;

    public TalkMsgsAdapter(Context context, List<TalkMsgModel> data){
        super(context, data, R.layout.list_item_chat_msg);
    }

    @Override
    public int getItemCount() {
        int count = super.getItemCount();
        if(count > 0){
            return count - 1;
        }

        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        //不显示加载更多
        return 0;
    }

    @Override
    protected RecyclerView.ViewHolder createViewHolderCore(View view) {
        return new ChatMsgItemViewHolder(view);
    }

    @Override
    protected void bindViewHolderCore(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof ChatMsgItemViewHolder){
            ChatMsgItemViewHolder mholder = (ChatMsgItemViewHolder)holder;
            TalkMsgModel model = getDataItem(position);
            boolean showTime = false;
            if(position == 0){
                showTime = true;
            }else{
                TalkMsgModel previous = getDataItem(position-1);

                long seconds = DateHelper.getSecondsBetween(previous.SendTime, model.SendTime);
                if(seconds>300){
                    //与前一条记录的时间相差超过5分钟时显示时间
                    showTime = true;
                }
            }

            if(model.Direction == 1){
                mholder.setModel(model, withUThumb, showTime);
            }else{
                mholder.setModel(model, ImottoApplication.getInstance().getUserThumb(), showTime);
            }
        }
    }

    public void setWithUser(String uname, String uthumb){
        withUname = uname;
        withUThumb = uthumb;

    }
}
