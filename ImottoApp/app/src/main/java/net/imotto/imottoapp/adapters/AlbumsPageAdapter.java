package net.imotto.imottoapp.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import net.imotto.imottoapp.ImottoApplication;
import net.imotto.imottoapp.MainActivity;
import net.imotto.imottoapp.fragments.AlbumExploreFragment;
import net.imotto.imottoapp.fragments.AlbumLovedFragment;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Observer;

/**
 * Created by sunht on 16/11/7.
 *
 */

public class AlbumsPageAdapter extends FragmentPagerAdapter {
    private Context context;
    private Map<Integer, Fragment> mFragments= new HashMap<>();
    public AlbumsPageAdapter(FragmentManager fm, Context ctx){
        super(fm);
        this.context = ctx;
    }

    @Override
    public Fragment getItem(int position) {

        Log.i("AlbumsPageAdapter", "albumsPage getItem:"+position);
        if(mFragments.containsKey(position))
        {
            return mFragments.get(position);
        }

        Fragment fragment;

        switch (position)
        {
            case 0:
                AlbumExploreFragment exploreFragment = new AlbumExploreFragment();
                ImottoApplication.getInstance().addUserObserver(exploreFragment);
                fragment = exploreFragment;
                break;
            case 1:
                AlbumLovedFragment lovedFragment = new AlbumLovedFragment();
                ImottoApplication.getInstance().addUserObserver(lovedFragment);
                fragment = lovedFragment;
                break;
            default:
                fragment = MainActivity.PlaceholderFragment.newInstance(position);
                break;
        }

        mFragments.put(position, fragment);

        return fragment;
    }

    @Override
    public int getCount() {
        return 2;
    }

}
