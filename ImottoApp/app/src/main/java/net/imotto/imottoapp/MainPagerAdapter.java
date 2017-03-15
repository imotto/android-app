package net.imotto.imottoapp;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import net.imotto.imottoapp.fragments.AlbumsFragment;
import net.imotto.imottoapp.fragments.MottosFragment;
import net.imotto.imottoapp.fragments.MsgsFragment;
import net.imotto.imottoapp.fragments.ProfileFragment;

import java.util.HashMap;
import java.util.Map;
import java.util.Observer;

/**
 * Created by sunht on 16/10/25.
 */

public class MainPagerAdapter extends FragmentPagerAdapter {
    private Context context;
    private String tabTitles[] = new String[]{"偶得","珍藏","消息","我"};
    private Map<Integer, Fragment> mFragments = new HashMap<Integer, Fragment>();
    private int imageResId[] = new int[]{
            R.drawable.selector_tab_item_img_mottos,
            R.drawable.selector_tab_item_img_albums,
            R.drawable.selector_tab_item_img_msgs,
            R.drawable.selector_tab_item_img_profile
    };

    public MainPagerAdapter(FragmentManager fm, Context ctx) {
        super(fm);
        this.context = ctx;
    }

    @Override
    public Fragment getItem(int position) {
        if (mFragments.containsKey(position)){
            return mFragments.get(position);
        }

        Fragment result;

        switch (position){
            case 0:
                MottosFragment mottosFragment = new MottosFragment();
                ImottoApplication.getInstance().addUserObserver(mottosFragment);
                result = mottosFragment;
                break;
            case 1:
                result = new AlbumsFragment();
                break;
            case 2:
                result = new MsgsFragment();
                break;
            case 3:
                ProfileFragment profileFragment = new ProfileFragment();
                ImottoApplication.getInstance().addUserObserver(profileFragment);
                result = profileFragment;
                break;
            default:
                result = MainActivity.PlaceholderFragment.newInstance(position + 1);
                break;
        }

        mFragments.put(position, result);

        return result;
    }

    @Override
    public int getCount() {
        return tabTitles.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }


    public View getTabView(int position) {
        View v = LayoutInflater.from(context).inflate(R.layout.tab_item, null);
        TextView tv = (TextView) v.findViewById(R.id.tab_item_txt);
        tv.setText(tabTitles[position]);
        ImageView img = (ImageView) v.findViewById(R.id.tab_item_img);
        img.setImageResource(imageResId[position]);
        return v;
    }

}
