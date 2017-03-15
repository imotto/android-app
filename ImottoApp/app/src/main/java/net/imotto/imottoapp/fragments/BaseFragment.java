package net.imotto.imottoapp.fragments;

import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;

/**
 * Created by sunht on 16/10/26.
 */

public abstract class BaseFragment extends Fragment {
    protected Toolbar mSpecialToolbar;

    public abstract Toolbar getSpecialToolbar();

    public interface OnFragmentAttachedListener{

        void OnFragmentAttached();

    }


}
