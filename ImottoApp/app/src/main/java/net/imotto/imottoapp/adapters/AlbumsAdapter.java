package net.imotto.imottoapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import net.imotto.imottoapp.R;
import net.imotto.imottoapp.services.models.AlbumModel;
import net.imotto.imottoapp.utils.DateHelper;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by sunht on 16/11/8.
 */

public class AlbumsAdapter extends RecyclerViewAdapter<AlbumModel> {

    private OnAlbumItemActionListener mOnAlbumActionListener;

    public AlbumsAdapter(Context context, List<AlbumModel> data){
        super(context, data, R.layout.list_item_album);
    }

    @Override
    protected RecyclerView.ViewHolder createViewHolderCore(View view) {
        return new AlbumItemViewHolder(view);
    }

    @Override
    protected void bindViewHolderCore(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof AlbumItemViewHolder){
            AlbumItemViewHolder mholder = (AlbumItemViewHolder)holder;
            mholder.setModel(getDataItem(position), mOnAlbumActionListener);
        }
    }


    public void setOnAlbumActionListener(OnAlbumItemActionListener listener){
        mOnAlbumActionListener = listener;
    }
}
