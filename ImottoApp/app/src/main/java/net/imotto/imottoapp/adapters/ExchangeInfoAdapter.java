package net.imotto.imottoapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import net.imotto.imottoapp.R;
import net.imotto.imottoapp.services.models.ExchangeInfoModel;

import java.util.List;

/**
 * Created by sunht on 2016/12/12.
 *
 */

public class ExchangeInfoAdapter extends RecyclerViewAdapter<ExchangeInfoModel> {

    public ExchangeInfoAdapter(Context context, List<ExchangeInfoModel> data){
        super(context,data, R.layout.list_item_exchange_info);
    }

    @Override
    protected RecyclerView.ViewHolder createViewHolderCore(View view) {
        return new ExchangeInfoItemViewHolder(view);
    }

    @Override
    protected void bindViewHolderCore(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof  ExchangeInfoItemViewHolder){
            ExchangeInfoItemViewHolder vholder = (ExchangeInfoItemViewHolder) holder;
            vholder.setModel(getDataItem(position));
        }
    }
}
