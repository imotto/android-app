package net.imotto.imottoapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import net.imotto.imottoapp.R;
import net.imotto.imottoapp.services.models.RelatedUserModel;

import java.util.List;

/**
 * Created by sunht on 16/11/15.
 */

public class UserAdapter extends RecyclerViewAdapter<RelatedUserModel> {

    private OnUserItemActionListener onUserItemActionListener;

    public UserAdapter(Context context, List<RelatedUserModel> data){
        super(context, data, R.layout.list_item_user);
    }

    @Override
    protected RecyclerView.ViewHolder createViewHolderCore(View view) {
        return new UserItemViewHolder(view);
    }

    @Override
    protected void bindViewHolderCore(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof  UserItemViewHolder){
            UserItemViewHolder mholder = (UserItemViewHolder) holder;
            mholder.setModel(getDataItem(position), onUserItemActionListener);
        }
    }

    public void setOnUserItemActionListener(OnUserItemActionListener listener){
        onUserItemActionListener = listener;
    }
}
