package net.imotto.imottoapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import net.imotto.imottoapp.R;
import net.imotto.imottoapp.services.models.VoteModel;

import java.util.List;

/**
 * Created by sunht on 2016/12/18.
 *
 */

public class VotesAdapter extends RecyclerViewAdapter<VoteModel> {

    private OnUserDataItemActionListener listener;

    public VotesAdapter(Context context, List<VoteModel> data){
        super(context, data, R.layout.list_item_vote);
    }

    @Override
    protected RecyclerView.ViewHolder createViewHolderCore(View view) {
        return new VoteItemViewHolder(view);
    }

    @Override
    protected void bindViewHolderCore(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof VoteItemViewHolder){
            VoteItemViewHolder mholder = (VoteItemViewHolder) holder;
            mholder.setModel(getDataItem(position), listener);
        }
    }

    public void setOnActionListener(OnUserDataItemActionListener listener){
        this.listener = listener;
    }
}
