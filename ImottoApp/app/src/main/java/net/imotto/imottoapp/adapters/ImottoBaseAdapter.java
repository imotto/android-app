package net.imotto.imottoapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunht on 16/11/1.
 */

public abstract class ImottoBaseAdapter<T> extends BaseAdapter {

    private Context context;
    private int listCellId = 0;
    private List<T> datas = new ArrayList<T>();

    public ImottoBaseAdapter(Context context, int resId){
        this.context = context;
        this.listCellId = resId;
    }

    public Context getContext(){
        return context;
    }

    public void add(T item){
        datas.add(item);
        this.notifyDataSetChanged();
    }

    public void add(List<T> items){
        datas.addAll(items);
        this.notifyDataSetChanged();
    }

    public void resetTo(List<T> items){
        datas = items;
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public T getItem(int position) {
        return datas.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(listCellId,
                    null);
        }
        initListCell(position, convertView, parent);
        return convertView;
    }


    protected abstract void initListCell(int position, View listCell,
                                         ViewGroup parent);
}
