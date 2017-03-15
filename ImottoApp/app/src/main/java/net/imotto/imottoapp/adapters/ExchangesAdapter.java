package net.imotto.imottoapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import net.imotto.imottoapp.R;
import net.imotto.imottoapp.services.models.ExchangeRecordModel;

import java.util.List;

/**
 * Created by sunht on 2016/12/21.
 *
 */

public class ExchangesAdapter extends RecyclerViewAdapter<ExchangeRecordModel> {

    private OnExchangeRecordActionListener mListener;

    public ExchangesAdapter(Context context, List<ExchangeRecordModel> exchanges){
        super(context, exchanges, R.layout.list_item_exchange_record);
    }

    @Override
    protected RecyclerView.ViewHolder createViewHolderCore(View view) {
        return new ExchangeRecordItemViewHolder(view);
    }

    @Override
    protected void bindViewHolderCore(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof ExchangeRecordItemViewHolder){
            ExchangeRecordItemViewHolder mholder = (ExchangeRecordItemViewHolder) holder;
            mholder.setModel(getDataItem(position), mListener);
        }
    }

    public void setOnActionListener(OnExchangeRecordActionListener listener ){
        mListener = listener;
    }
}
