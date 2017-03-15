package net.imotto.imottoapp.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import net.imotto.imottoapp.R;

/**
 * Created by sunht on 16/11/20.
 */

public class GroupViewHolder extends RecyclerView.ViewHolder {

    public GroupViewHolder(View view){
        super(view);
        lblGroupName = (TextView) view.findViewById(R.id.lbl_title);
    }

    public TextView lblGroupName;

    public void setTitle(String title){
        lblGroupName.setText(title);
    }
}
