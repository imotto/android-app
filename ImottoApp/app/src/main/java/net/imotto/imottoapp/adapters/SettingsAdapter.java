package net.imotto.imottoapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nostra13.universalimageloader.core.ImageLoader;

import net.imotto.imottoapp.R;
import net.imotto.imottoapp.utils.CommUtils;

import java.util.List;

/**
 * Created by sunht on 16/11/19.
 */

public class SettingsAdapter extends RecyclerViewAdapter<SettingsAdapter.SettingItem> {

    private static final int TYPE_GROUP=3;
    private static final int TYPE_PHOTO =4;

    public SettingsAdapter(Context context, List<SettingItem> data){
        super(context, data, R.layout.list_item_profile);
    }

    @Override
    public int getItemCount() {
        return super.getItemCount() - 1;
    }

    @Override
    public int getItemViewType(int position) {
        SettingItem item = getDataItem(position);
        if(item.id==0){
            return TYPE_GROUP;
        }else if(item.img != null){
            return TYPE_PHOTO;
        }

        return 0;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == TYPE_GROUP){
            View view= LayoutInflater.from(context).inflate(R.layout.list_item_group, parent, false);
            return new GroupViewHolder(view);
        }else if(viewType == TYPE_PHOTO){
            View view = LayoutInflater.from(context).inflate(R.layout.list_item_photo, parent, false);
            return new PhotoItemViewHolder(view);
        }else {
            return super.onCreateViewHolder(parent, viewType);
        }
    }


    @Override
    protected RecyclerView.ViewHolder createViewHolderCore(View view) {
        ProfileItemViewHolder holder = new ProfileItemViewHolder(view);
        return holder;
    }

    @Override
    protected void bindViewHolderCore(RecyclerView.ViewHolder holder, int position) {
        SettingItem item = getDataItem(position);

        if(holder instanceof ProfileItemViewHolder){
            ProfileItemViewHolder mholder = (ProfileItemViewHolder)holder;
            mholder.lblTitle.setText(item.title);
            mholder.lblInfo.setText(item.action);
            mholder.imgThumb.setImageResource(item.thumbResId);
        }else if (holder instanceof PhotoItemViewHolder){
            PhotoItemViewHolder mholder = (PhotoItemViewHolder)holder;
            mholder.lblInfo.setText(item.action);

            ImageLoader.getInstance().displayImage(item.img,mholder.imgThumb,
                    CommUtils.getDisplayImageOptions());
        }else if (holder instanceof GroupViewHolder) {
            GroupViewHolder mholder = (GroupViewHolder) holder;
            mholder.setTitle(item.title);
        }
    }

    public static class SettingItem{

        public SettingItem(int id, String img, String title, String action, int resId){
            this.id = id;
            this.img = img;
            this.title = title;
            this.action = action;
            this.thumbResId = resId;
        }

        public int id;

        public String img;

        public String title;

        public String action;

        public int thumbResId;
    }
}
