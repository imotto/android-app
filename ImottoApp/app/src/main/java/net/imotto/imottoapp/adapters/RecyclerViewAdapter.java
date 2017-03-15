package net.imotto.imottoapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import net.imotto.imottoapp.R;

import java.util.List;

/**
 * Created by sunht on 16/11/9.
 */

public abstract class RecyclerViewAdapter<T> extends Adapter<ViewHolder> {
    private static final int TYPE_ITEM = 0;
    private static final int TYPE_FOOTER = 1;
    protected Context context;
    private List<T> data;
    private int resId;
    private boolean isEof = false;
    private boolean showEndHint = true;


    public RecyclerViewAdapter(Context context, List<T> data, int resId) {
        this.context = context;
        this.data = data;
        this.resId = resId;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public int getItemCount() {
        return data.size() == 0 ? 0 : data.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position + 1 == getItemCount()) {
            return TYPE_FOOTER;
        } else {
            return TYPE_ITEM;
        }
    }

    public void setShowEndHint(boolean show){
        showEndHint = show;
    }

    public void setEof(boolean eof){
        isEof = eof;
        if(isEof) {
            int count = getItemCount();
            if (count > 0) {
                notifyItemChanged(count - 1);
            }
        }
    }

    public boolean getEof(){
        return isEof;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View view = LayoutInflater.from(context).inflate(this.resId, parent,
                    false);
            return createViewHolderCore(view);
        } else if (viewType == TYPE_FOOTER) {
            View view = LayoutInflater.from(context).inflate(R.layout.list_item_footer, parent,
                    false);
            return new FootViewHolder(view);
        }
        return null;
    }

    protected abstract ViewHolder createViewHolderCore(View view);

    protected abstract void bindViewHolderCore(ViewHolder holder, int position);

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        if (holder instanceof FootViewHolder) {
            Log.i("RecyclerViewAdapter","isEof = "+isEof);
            FootViewHolder fholder = (FootViewHolder)holder;
            if (isEof){
                fholder.progress.setVisibility(View.GONE);

                if(showEndHint){
                    fholder.itemView.setVisibility(View.VISIBLE);
                    fholder.lblLoading.setText(context.getResources().getString(R.string.loaded));
                }else{
                    fholder.itemView.setVisibility(View.GONE);
                }
            }else{
                fholder.progress.setVisibility(View.VISIBLE);
                fholder.lblLoading.setText(context.getResources().getString(R.string.loading));
            }
        }
        else{
            bindViewHolderCore(holder, position);
            if (onItemClickListener != null) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = holder.getLayoutPosition();
                        onItemClickListener.onItemClick(holder.itemView, position);
                    }
                });

                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        int position = holder.getLayoutPosition();
                        onItemClickListener.onItemLongClick(holder.itemView, position);
                        return false;
                    }
                });
            }
        }
    }

    public T getDataItem(int position){
        return data.get(position);
    }


    static class FootViewHolder extends ViewHolder {
        public ProgressBar progress;
        public TextView lblLoading;

        public FootViewHolder(View view) {
            super(view);
            progress = (ProgressBar) view.findViewById(R.id.progressBar);
            lblLoading = (TextView) view.findViewById(R.id.lbl_loading);
        }
    }

}
