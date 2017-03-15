package net.imotto.imottoapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import net.imotto.imottoapp.R;
import net.imotto.imottoapp.services.models.ReviewModel;

import java.util.List;

/**
 * Created by sunht on 16/11/17.
 */

public class ReviewAdapter extends RecyclerViewAdapter<ReviewModel> {

    private OnReviewItemActionListener onReviewItemActionListener;

    public ReviewAdapter(Context context, List<ReviewModel> data){
        super(context, data, R.layout.list_item_review);
    }

    @Override
    protected RecyclerView.ViewHolder createViewHolderCore(View view) {
        return new ReviewItemViewHolder(view);
    }

    @Override
    protected void bindViewHolderCore(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof ReviewItemViewHolder){
            ReviewItemViewHolder mholder = (ReviewItemViewHolder) holder;
            mholder.setModel(getDataItem(position), onReviewItemActionListener);
        }
    }

    public void setOnReviewItemActionListener(OnReviewItemActionListener listener){
        this.onReviewItemActionListener = listener;
    }
}
