package net.imotto.imottoapp.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

/**
 * Created by annda on 2016/12/4.
 */

public class RecylcerViewAdapterDataObserver extends RecyclerView.AdapterDataObserver {
    private static final String TAG = "AdapterDataObserver";
    private RecyclerView.Adapter<RecyclerView.ViewHolder> adapter;
    private View showView;
    private View emptyView;

    public RecylcerViewAdapterDataObserver(RecyclerView.Adapter<RecyclerView.ViewHolder> adapter, View oriView, View emptyView){
        super();
        this.adapter = adapter;
        this.showView = oriView;
        this.emptyView = emptyView;
    }

    @Override
    public void onChanged() {
        Log.i(TAG,"onchanged");
        super.onChanged();
        checkIfEmpty();
    }

    @Override
    public void onItemRangeInserted(int positionStart, int itemCount) {
        super.onItemRangeInserted(positionStart, itemCount);
        checkIfEmpty();
    }

    @Override
    public void onItemRangeRemoved(int positionStart, int itemCount) {
        super.onItemRangeRemoved(positionStart, itemCount);
        checkIfEmpty();
    }

    private void checkIfEmpty(){

        Log.i(TAG,"checkIfEmpty");
        boolean showEmptyView = adapter.getItemCount() == 0;
        showView.setVisibility(showEmptyView? View.GONE : View.VISIBLE);
        emptyView.setVisibility(showEmptyView? View.VISIBLE : View.GONE);

    }
}
