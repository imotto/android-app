package net.imotto.imottoapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import net.imotto.imottoapp.R;
import net.imotto.imottoapp.services.models.AlbumModel;

import java.util.List;

/**
 * Created by sunht on 16/11/18.
 */

public class AlbumsSelAdapter extends RecyclerViewAdapter<AlbumModel> {
    public AlbumsSelAdapter(Context context, List<AlbumModel> data){
        super(context, data, R.layout.list_item_album_sel);
    }

    @Override
    protected RecyclerView.ViewHolder createViewHolderCore(View view) {
        return new AlbumSelItemViewHolder(view);
    }

    @Override
    protected void bindViewHolderCore(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof AlbumSelItemViewHolder){
            AlbumSelItemViewHolder mholder = (AlbumSelItemViewHolder) holder;
            mholder.setModel(getDataItem(position));
        }
    }
}
