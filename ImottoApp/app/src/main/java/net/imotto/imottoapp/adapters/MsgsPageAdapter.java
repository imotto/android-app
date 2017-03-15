package net.imotto.imottoapp.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import net.imotto.imottoapp.ImottoApplication;
import net.imotto.imottoapp.MainActivity;
import net.imotto.imottoapp.fragments.MsgNoticeFragment;
import net.imotto.imottoapp.fragments.MsgPrivateFragment;

import java.util.HashMap;
import java.util.Map;
import java.util.Observer;

/**
 * Created by sunht on 16/11/7.
 */

public class MsgsPageAdapter extends FragmentPagerAdapter {

    private Map<Integer, Fragment> mFragments = new HashMap<>();
    private Context context;

    public MsgsPageAdapter(FragmentManager fm, Context ctx){
        super(fm);
        this.context = ctx;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public Fragment getItem(int position) {
        if(mFragments.containsKey(position)){
            return mFragments.get(position);
        }

        Fragment fragment;

        switch (position)
        {
            case 0:
                MsgPrivateFragment privateFragment = new MsgPrivateFragment();
                ImottoApplication.getInstance().addUserObserver(privateFragment);
                fragment = privateFragment;
                break;
            case 1:
                MsgNoticeFragment noticeFragment = new MsgNoticeFragment();
                ImottoApplication.getInstance().addUserObserver(noticeFragment);
                fragment = noticeFragment;
                break;
            default:
                fragment= MainActivity.PlaceholderFragment.newInstance(position);
                break;
        }

        mFragments.put(position, fragment);
        return fragment;
    }
}
