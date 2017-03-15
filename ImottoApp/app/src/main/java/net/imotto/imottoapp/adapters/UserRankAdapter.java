package net.imotto.imottoapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import net.imotto.imottoapp.R;
import net.imotto.imottoapp.services.models.UserRankModel;

import java.util.List;

/**
 * Created by sunht on 2017/1/6.
 *
 */

public class UserRankAdapter extends RecyclerViewAdapter<UserRankModel> {

    private OnUserDataItemActionListener listener;

    public UserRankAdapter(Context context, List<UserRankModel> data){
        super(context, data, R.layout.list_item_user_rank);
    }

    @Override
    public int getItemCount() {
        int count = super.getItemCount();
        if(count>0) {
            return super.getItemCount() - 1;
        }

        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    protected RecyclerView.ViewHolder createViewHolderCore(View view) {
        return new UserRankItemViewHolder(view);
    }

    @Override
    protected void bindViewHolderCore(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof UserRankItemViewHolder){
            UserRankItemViewHolder mholder = (UserRankItemViewHolder) holder;
            mholder.setModel(getDataItem(position), listener);
        }
    }

    public void setOnActionListener(OnUserDataItemActionListener listener){
        this.listener = listener;
    }
}
