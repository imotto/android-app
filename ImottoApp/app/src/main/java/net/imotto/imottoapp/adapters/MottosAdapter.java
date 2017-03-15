package net.imotto.imottoapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import net.imotto.imottoapp.R;
import net.imotto.imottoapp.services.models.MottoModel;

import java.util.List;

/**
 * Created by sunht on 16/10/31.
 */

public class MottosAdapter extends RecyclerViewAdapter<MottoModel> {

    private boolean onlyShowTime = false;
    private OnMottoItemActionListener onMottoItemActionListener;

    public MottosAdapter(Context context, List<MottoModel> data){
        super(context, data, R.layout.list_item_motto);
    }

    @Override
    protected RecyclerView.ViewHolder createViewHolderCore(View view) {
        return new MottoItemViewHolder(view);
    }

    @Override
    protected void bindViewHolderCore(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof MottoItemViewHolder){
            MottoItemViewHolder mholder = (MottoItemViewHolder)holder;
            mholder.setModel(getDataItem(position), onlyShowTime, onMottoItemActionListener);
        }
    }

    public void setOnMottoItemActionListener(OnMottoItemActionListener listener) {
        this.onMottoItemActionListener = listener;
    }

    public void setOnlyShowTime(boolean showTimeOnly){
        onlyShowTime = showTimeOnly;
    }

}