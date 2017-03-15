package net.imotto.imottoapp.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import net.imotto.imottoapp.R;
import net.imotto.imottoapp.services.models.ScoreRecordModel;
/**
 * Created by sunht on 16/11/14.
 */

public class ScoreItemViewHolder extends RecyclerView.ViewHolder {

    public ScoreItemViewHolder(View view){
        super(view);

        lblTitle = (TextView) view.findViewById(R.id.lbl_title);
        lblInfo = (TextView) view.findViewById(R.id.lbl_info);
        lblScore = (TextView) view.findViewById(R.id.lbl_score);
    }

    public TextView lblTitle;
    public TextView lblInfo;
    public TextView lblScore;

    public void setModel(ScoreRecordModel m){
        lblTitle.setText(Integer.toString(m.TheDay));
        lblInfo.setText(String.format("%d则偶得", m.Mottos));
        lblScore.setText(String.format("+%d", m.Revenue));
    }
}
