package net.imotto.imottoapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import net.imotto.imottoapp.R;
import net.imotto.imottoapp.services.models.AwardModel;

import java.util.List;

/**
 *
 * Created by sunht on 2017/1/12.
 */

public class AwardsAdapter extends RecyclerViewAdapter<AwardModel> {

    private OnAwardItemActionListener mListener;

    public AwardsAdapter(Context context,List<AwardModel> data){
        super(context, data, R.layout.list_item_award);
    }

    @Override
    protected RecyclerView.ViewHolder createViewHolderCore(View view) {
        return new AwardItemViewHolder(view);
    }

    @Override
    protected void bindViewHolderCore(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof  AwardItemViewHolder){
            AwardItemViewHolder mholder = (AwardItemViewHolder)holder;
            mholder.setModel(getDataItem(position), mListener);
        }
    }

    public void setOnActionListener(OnAwardItemActionListener listener ){
        mListener = listener;
    }
}
