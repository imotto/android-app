package net.imotto.imottoapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import net.imotto.imottoapp.R;
import net.imotto.imottoapp.services.models.ScoreRecordModel;

import java.util.List;

/**
 * Created by sunht on 16/11/14.
 */

public class ScoreAdapter extends RecyclerViewAdapter<ScoreRecordModel> {

    public ScoreAdapter(Context context, List<ScoreRecordModel> data){
        super(context, data, R.layout.list_item_score);
    }

    @Override
    protected RecyclerView.ViewHolder createViewHolderCore(View view) {
        return new ScoreItemViewHolder(view);
    }

    @Override
    protected void bindViewHolderCore(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ScoreItemViewHolder){
            ScoreItemViewHolder mholder = (ScoreItemViewHolder) holder;
            mholder.setModel(getDataItem(position));
        }
    }
}
