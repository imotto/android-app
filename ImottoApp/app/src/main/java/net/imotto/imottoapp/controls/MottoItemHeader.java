package net.imotto.imottoapp.controls;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import net.imotto.imottoapp.R;

/**
 * Created by sunht on 16/11/4.
 *
 */

public class MottoItemHeader extends RelativeLayout {

    public MottoItemHeader(Context context, AttributeSet attrs){
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.header_motto_item, this);
    }

}
