package net.imotto.imottoapp.fragments;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import net.imotto.imottoapp.R;
import net.imotto.imottoapp.adapters.MsgsPageAdapter;

import info.hoang8f.android.segmented.SegmentedGroup;

/**
 * Created by sunht on 16/10/26.
 */

public class MsgsFragment extends BaseFragment {
    private static final String TAG = "MsgsFragment";
    private ViewPager mViewPager;
    private MsgsPageAdapter mSectionsPagerAdapter;
    private RadioButton mRadioMsg;
    private RadioButton mRadioNotice;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_msgs, container, false);

        mViewPager = (ViewPager) rootView.findViewById(R.id.msgs_container);
        mViewPager.setOffscreenPageLimit(1);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(position == 0){
                    mRadioMsg.setChecked(true);
                }else{
                    mRadioNotice.setChecked(true);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mSectionsPagerAdapter = new MsgsPageAdapter(getChildFragmentManager(), getContext());
        mViewPager.setAdapter(mSectionsPagerAdapter);

        return rootView;
    }

    public void setCurrentItem(int position){
        if(position == 0 || position == 1){
            mViewPager.setCurrentItem(position);
            Object obj = mViewPager.getAdapter().instantiateItem(mViewPager, position);

            if(obj instanceof MsgPrivateFragment){
                ((MsgPrivateFragment)obj).refreshData();
            }else if(obj instanceof  MsgNoticeFragment){
                ((MsgNoticeFragment)obj).refreshData();
            }
        }
    }

    @Override
    public Toolbar getSpecialToolbar() {
        if (mSpecialToolbar == null) {
            mSpecialToolbar = (Toolbar) getActivity().getLayoutInflater().inflate(R.layout.toolbar_msgs, null);
            mRadioMsg = (RadioButton) mSpecialToolbar.findViewById(R.id.radio_0);
            mRadioNotice = (RadioButton) mSpecialToolbar.findViewById(R.id.radio_1);
            SegmentedGroup radioGroup = (SegmentedGroup) mSpecialToolbar.findViewById(R.id.segmented_msgs);
            radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    if(mRadioMsg.getId() == checkedId){
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
