package net.imotto.imottoapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import net.imotto.imottoapp.R;
import net.imotto.imottoapp.services.models.BillRecordModel;

import java.util.List;

/**
 * Created by sunht on 16/11/15.
 */

public class RevenueAdapter extends RecyclerViewAdapter<BillRecordModel> {

    public RevenueAdapter(Context context, List<BillRecordModel> data){
        super(context, data, R.layout.list_item_revenue);
    }

    @Override
    protected RecyclerView.ViewHolder createViewHolderCore(View view) {
        return new RevenueItemViewHolder(view);
    }

    @Override
    protected void bindViewHolderCore(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof RevenueItemViewHolder){
            RevenueItemViewHolder mhodler = (RevenueItemViewHolder) holder;
            mhodler.setModel(getDataItem(position));
        }
    }
}
