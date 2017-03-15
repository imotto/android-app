package net.imotto.imottoapp.controls;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import net.imotto.imottoapp.R;

/**
 * Created by sunht on 16/11/4.
 */

public class MottoRankingItemHeader extends RelativeLayout {

    public MottoRankingItemHeader(Context context, AttributeSet attrs){
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.header_ranking_motto_item, this);
    }
}
