package net.imotto.imottoapp.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;

import com.allenliu.badgeview.BadgeFactory;
import com.allenliu.badgeview.BadgeView;

import net.imotto.imottoapp.R;
import net.imotto.imottoapp.services.models.RecentTalkModel;

import java.util.List;

/**
 * Created by sunht on 16/11/8.
 *
 */

public class TalksAdapter extends RecyclerViewAdapter<RecentTalkModel> {

    private Context context;
    private OnTalkItemActionListener talkItemActionListener;

    public TalksAdapter(Context context, List<RecentTalkModel> data){
        super(context, data, R.layout.list_item_talk);
        this.context = context;
    }

    @Override
    protected RecyclerView.ViewHolder createViewHolderCore(View view) {
        return new RecentTalkItemViewHolder(view);
    }

    @Override
    protected void bindViewHolderCore(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof RecentTalkItemViewHolder){
            RecentTalkItemViewHolder mholder = (RecentTalkItemViewHolder)holder;
            RecentTalkModel model = getDataItem(position);
            mholder.setModel(model, talkItemActionListener, context);

        }
    }

    public void setOnTalkItemActionListener(OnTalkItemActionListener listener){
        talkItemActionListener = listener;
    }
}
