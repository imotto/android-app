package net.imotto.imottoapp.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.allenliu.badgeview.BadgeFactory;
import com.allenliu.badgeview.BadgeView;

import net.imotto.imottoapp.R;
import net.imotto.imottoapp.services.models.NoticeModel;
import net.imotto.imottoapp.utils.DateHelper;

import java.util.List;

/**
 * Created by sunht on 16/11/8.
 */

public class NoticesAdapter extends RecyclerViewAdapter<NoticeModel> {

    private Context context;

    public NoticesAdapter(Context context, List<NoticeModel> data){
        super(context, data, R.layout.list_item_notice);
        this.context = context;
    }

    @Override
    protected RecyclerView.ViewHolder createViewHolderCore(View view) {
        return new NoticeItemViewHolder(view);
    }

    @Override
    protected void bindViewHolderCore(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof NoticeItemViewHolder){
            NoticeItemViewHolder mholder = (NoticeItemViewHolder) holder;

            NoticeModel model = getDataItem(position);
            mholder.setModel(model, context);
        }
    }
}
