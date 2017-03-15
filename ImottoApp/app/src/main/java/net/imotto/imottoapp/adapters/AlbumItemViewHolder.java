package net.imotto.imottoapp.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import net.imotto.imottoapp.R;
import net.imotto.imottoapp.services.models.AlbumModel;
import net.imotto.imottoapp.utils.CommUtils;
import net.imotto.imottoapp.utils.DateHelper;


/**
 * Created by sunht on 16/11/13.
 */

public class AlbumItemViewHolder extends RecyclerView.ViewHolder {

    public AlbumItemViewHolder(View view){
        super(view);

        this.lblTitle = (TextView) view.findViewById(R.id.lbl_title);
        this.lblInfo = (TextView) view.findViewById(R.id.lbl_info);
        this.lblSummary = (TextView) view.findViewById(R.id.lbl_summary);
        lblTime = (TextView) view.findViewById(R.id.lbl_time);
        lblUser = (TextView) view.findViewById(R.id.lbl_user);

    }

    public TextView lblTitle;
    public TextView lblSummary;
    public TextView lblInfo;
    public TextView lblTime;
    public TextView lblUser;

    public void setModel(final AlbumModel m , final OnAlbumItemActionListener listener){
        this.lblTitle.setText(m.Title);
        this.lblSummary.setText(m.Description);
        this.lblInfo.setText(String.format("收录%d则偶得 • %d人喜欢", m.Mottos, m.Loves));

        lblTime.setText(String.format("%s创建",DateHelper.friendlyTime(m.CreateTime)));
        lblUser.setText(m.UserName);

        this.lblUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null){
                    listener.onShowUserAction(m);
                }
            }
        });
    }
}
