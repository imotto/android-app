package net.imotto.imottoapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import net.imotto.imottoapp.R;
import net.imotto.imottoapp.services.models.GiftModel;

import java.util.List;

/**
 * Created by annda on 2016/12/12.
 */

public class GiftsAdapter extends RecyclerViewAdapter<GiftModel> {

    public GiftsAdapter(Context context, List<GiftModel> data){
        super(context, data, R.layout.list_item_gift);

    }

    @Override
    protected RecyclerView.ViewHolder createViewHolderCore(View view) {
        return new GiftItemViewHolder(view);
    }

    @Override
    protected void bindViewHolderCore(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof GiftItemViewHolder){
            GiftItemViewHolder mholder = (GiftItemViewHolder)holder;
            mholder.setModel(getDataItem(position));
        }
    }
}
