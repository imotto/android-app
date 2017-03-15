package net.imotto.imottoapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.imotto.imottoapp.R;

import java.util.List;

/**
 * Created by sunht on 16/11/9.
 */

public class ProfilesAdapter extends RecyclerViewAdapter<ProfilesAdapter.ProfileItem> {

    private static final int TYPE_DIVIDER=2;

    public ProfilesAdapter(Context context, List<ProfileItem> data){
        super(context, data, R.layout.list_item_profile);
    }

    @Override
    protected RecyclerView.ViewHolder createViewHolderCore(View view) {
        ProfileItemViewHolder holder = new ProfileItemViewHolder(view);
        return holder;
    }

    @Override
    public int getItemCount() {
        return super.getItemCount()-1;
    }

    @Override
    public int getItemViewType(int position) {
        ProfileItem item = getDataItem(position);
        if (item.id == 0){
            return TYPE_DIVIDER;
        }else{
            return 0;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == TYPE_DIVIDER){
            View view = LayoutInflater.from(context).inflate(R.layout.list_item_divider, parent,
                    false);
            return new RecyclerView.ViewHolder(view) { };
        }else{
            return super.onCreateViewHolder(parent, viewType);
        }
    }

    @Override
    protected void bindViewHolderCore(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof ProfileItemViewHolder){
            ProfileItem item = getDataItem(position);
            ProfileItemViewHolder pholder = (ProfileItemViewHolder) holder;
            pholder.lblTitle.setText(item.title);
            pholder.lblInfo.setText(item.info);
            pholder.imgThumb.setImageResource(item.thumbResId);
        }
    }

    public static class ProfileItem{

        public ProfileItem(int id, String title, String info, int resId){
            this.id = id;
            this.title = title;
            this.info = info;
            this.thumbResId = resId;
        }

        public String title;

        public String info;

        public int id;

        public int thumbResId;
    }

}
