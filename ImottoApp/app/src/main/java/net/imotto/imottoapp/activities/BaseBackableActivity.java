package net.imotto.imottoapp.activities;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import net.imotto.imottoapp.R;

/**
 *
 * Created by sunht on 16/11/3.
 */

public abstract class BaseBackableActivity extends AppCompatActivity {

    private Toolbar mToolBar;

    /**
     * 设置ToolBar
     */
    protected void setupToolBar(String title){
        mToolBar = (Toolbar) findViewById(R.id.toolbar_general);
        mToolBar.setNavigationIcon(R.drawable.ic_arrow_back);
        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack();
            }
        });

        TextView lblTitle = (TextView)mToolBar.findViewById(R.id.toolbar_title_lbl);
        lblTitle.setText(title);
    }

    protected void setupToolBar(int titleResId){
        String title = getResources().getString(titleResId);
        setupToolBar(title);
    }

    protected void goBack(){
        finish();
    }

}
