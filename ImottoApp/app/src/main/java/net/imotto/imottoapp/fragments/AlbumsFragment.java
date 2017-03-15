package net.imotto.imottoapp.fragments;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import net.imotto.imottoapp.R;
import net.imotto.imottoapp.adapters.AlbumsPageAdapter;

import info.hoang8f.android.segmented.SegmentedGroup;

/**
 * Created by sunht on 16/10/26.
 */

public class AlbumsFragment extends BaseFragment {
    private static final String TAG = "AlbumsFragment";
    private ViewPager mViewPager;
    private AlbumsPageAdapter mSectionsPagerAdapter;
    private RadioButton mRadioExplore;
    private RadioButton mRadioLoved;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_albums, container, false);

        mViewPager = (ViewPager) rootView.findViewById(R.id.albums_container);
        mViewPager.setOffscreenPageLimit(1);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Log.i(TAG, "onpageSelected:"+position);
                if(position == 0){
                    mRadioExplore.setChecked(true);
                }else{
                    mRadioLoved.setChecked(true);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mSectionsPagerAdapter = new AlbumsPageAdapter(getChildFragmentManager(), getContext());
        mViewPager.setAdapter(mSectionsPagerAdapter);


        return rootView;
    }

    @Override
    public Toolbar getSpecialToolbar() {
        if (mSpecialToolbar == null) {
            mSpecialToolbar = (Toolbar) getActivity().getLayoutInflater().inflate(R.layout.toolbar_albums, null);
            mRadioExplore = (RadioButton) mSpecialToolbar.findViewById(R.id.radio_0);
            mRadioLoved = (RadioButton) mSpecialToolbar.findViewById(R.id.radio_1);
            SegmentedGroup radioGroup = (SegmentedGroup) mSpecialToolbar.findViewById(R.id.segmented_album);

            radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    if(mRadioExplore.getId() == checkedId){
                        mViewPager.setCurrentItem(0, true);
                    }else{
                        mViewPager.setCurrentItem(1, true);
                    }
                }
            });

        }

        return mSpecialToolbar;
    }
}
