package net.imotto.imottoapp.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.format.TitleFormatter;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import net.imotto.imottoapp.MainActivity;
import net.imotto.imottoapp.R;
import net.imotto.imottoapp.activities.LovedMottoActivity;
import net.imotto.imottoapp.adapters.MottosAdapter;
import net.imotto.imottoapp.adapters.RecylcerViewAdapterDataObserver;
import net.imotto.imottoapp.adapters.SimpleOnMottoActionListener;
import net.imotto.imottoapp.animations.Rotate3dAnimation;
import net.imotto.imottoapp.services.ImottoApi;
import net.imotto.imottoapp.services.models.MottoModel;
import net.imotto.imottoapp.services.models.ReadMottoResp;
import net.imotto.imottoapp.services.models.ReadResp;
import net.imotto.imottoapp.utils.Constants;
import net.imotto.imottoapp.utils.DateHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by sunht on 16/10/25.
 *
 */

public class MottosFragment extends BaseFragment implements Observer {
    private boolean isfirstLoad = true;
    private static final String TAG = "MottosFragment";
    private int theDayPicked = 0;
    private int theDay = 20170101;
    private int page = 1;
    private int pageSize = Constants.DEFAULT_PAGE_SIZE;
    private boolean isLoading = false;
    private boolean isRefreshing = false;
    private List<MottoModel> mData = new ArrayList<>();
    private SwipeRefreshLayout mRefresh;
    private MottosAdapter mAdapter;
    private GestureDetector mGestureDetector;
    private RecyclerView mRecyclerView;
    private View noDataHintView;
    private ImottoApi.InvokeCompletionHandler<ReadMottoResp> invokeCompletionHandler =
            new ImottoApi.InvokeCompletionHandler<ReadMottoResp>() {
        @Override
        public void onApiCallCompletion(ReadMottoResp result) {
            MottosFragment.this.processMottoData(result);
        }

        @Override
        public Class<ReadMottoResp> getGenericClass() {
            return ReadMottoResp.class;
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_mottos, container, false);

        mGestureDetector = new GestureDetector(getContext(),new MyGestureListener());

        mRefresh = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);

        noDataHintView = rootView.findViewById(R.id.view_no_data);
        TextView lblNoData = (TextView) rootView.findViewById(R.id.lbl_no_data);
        lblNoData.setText(R.string.lbl_no_data_mottos);

        initView();

        initEvent();

        return rootView;
    }

    private void initView(){
        mRefresh.setColorSchemeResources(R.color.colorPrimary);
        mRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.i(TAG,"onRefresh.");
                mAdapter.setEof(false);
                mData.clear();
                mAdapter.notifyDataSetChanged();
                page = 1;
                getData();
            }
        });


        mRecyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getContext())
                .colorResId(R.color.ncolorDivider)
                .sizeResId(R.dimen.recycler_view_ndivider).build());
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(layoutManager);

        noDataHintView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return  mGestureDetector.onTouchEvent(event);
            }
        });




        mRecyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return mGestureDetector.onTouchEvent(event);
            }
        });

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                Log.d(TAG, "onScrolled");

                int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
                if (lastVisibleItemPosition + 1 == mAdapter.getItemCount()) {
                    Log.d(TAG, "loading executed");

                    boolean refreshing = mRefresh.isRefreshing();
                    if (refreshing) {
                        return;
                    }
                    if (!isLoading) {
                        if(!mAdapter.getEof()) {
                            isLoading = true;
                            page++;
                            getData();
                        }
                    }
                }
            }
        });

    }

    private void initEvent(){
        Log.i(TAG, "initEvent");
        Context context = getContext();
        if(context != null) {
            mAdapter = new MottosAdapter(getContext(), mData);
            mAdapter.setOnlyShowTime(true);
            mAdapter.setOnMottoItemActionListener(new SimpleOnMottoActionListener(context));
            mRecyclerView.setAdapter(mAdapter);

            mAdapter.registerAdapterDataObserver(new RecylcerViewAdapterDataObserver(mAdapter,mRecyclerView, noDataHintView));
        }
    }

    private void getData(){
        if(!isRefreshing) {
            isRefreshing = true;
            ImottoApi.getInstance().readMottos(theDay, page, pageSize, invokeCompletionHandler);
        }
    }



    private void processMottoData(ReadResp<MottoModel> result){
        Log.i(TAG, "ProcessMottoData "+theDay);
        boolean refreshing = mRefresh.isRefreshing();
        if(refreshing) {
            mRefresh.setRefreshing(false);
        }

        if(isLoading){
            mAdapter.notifyItemRemoved(mAdapter.getItemCount());
            isLoading = false;
        }

        if(result.isSuccess()){
            if(result.Data!=null && result.Data.size()>0){
                if (result.Data.size()<pageSize) {
                    mAdapter.setEof(true);
                }
                int start = mData.size();

                if(DateHelper.isTheDayPermenant(theDay)){
                    mData.addAll(result.Data);
                    mAdapter.notifyItemRangeInserted(start, result.Data.size());
                }else {
                    //theDay的偶得正在评估，可能会有重复记录，检查之.
                    List<MottoModel> newData = new ArrayList<>();

                    for (MottoModel m : result.Data) {
                        boolean exists = false;
                        for (MottoModel om : mData) {
                            if (m.ID == om.ID) {
                                //已经包含这条记录了.
                                exists = true;
                                break;
                            }
                        }

                        if(exists){
                            continue;
                        }

                        newData.add(m);
                    }

                    if (newData.size() > 0) {
                        mData.addAll(newData);
                        mAdapter.notifyItemRangeInserted(start, newData.size());
                    } else {
                        if (!mAdapter.getEof()) {
                            //本页取的数据全部已经出现，自动再加载一页
                            page++;
                            getData();
                        }
                    }
                }

            }
            else {
                mAdapter.setEof(true);
                mAdapter.notifyItemChanged(mAdapter.getItemCount()-1);
            }

        }else{
            Toast.makeText(getContext(), result.Msg, Toast.LENGTH_SHORT).show();
        }

        this.isRefreshing = false;
    }

    private void refreshData(){
        mRefresh.post(new Runnable() {
            @Override
            public void run() {
                mRefresh.setRefreshing(true);
                mAdapter.setEof(false);
                mData.clear();
                mAdapter.notifyDataSetChanged();
                page = 1;
                getData();
            }
        });

    }

    @Override
    public void update(Observable o, Object arg) {
        refreshData();
    }

    @Override
    public void onStart() {
        super.onStart();
        if(isfirstLoad){
            MainActivity act =  (MainActivity) getActivity();
            act.prepareFragment(0);

            setTheDay(DateHelper.getTheDay(new Date()));

            isfirstLoad = false;
        }
    }

    @Override
    public Toolbar getSpecialToolbar() {
        if (mSpecialToolbar == null) {

            Activity act = getActivity();
            if (act != null)
            {
                mSpecialToolbar = (Toolbar) act.getLayoutInflater().inflate(R.layout.toolbar_mottos, null);

                ImageButton btnLoved = (ImageButton) mSpecialToolbar.findViewById(R.id.toolbar_right_btn);
                btnLoved.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showLovedMottos();
                    }
                });

                Button btnTheday = (Button)(mSpecialToolbar.findViewById(R.id.toolbar_theday_btn));
                btnTheday.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showPickTheDay();

                    }
                });
            }
        }

        return mSpecialToolbar;
    }

    private void showPickTheDay(){
        final Context context = getContext();

        MaterialCalendarView mcv = new MaterialCalendarView(context);
        mcv.setShowOtherDates(MaterialCalendarView.SHOW_OUT_OF_RANGE);
        mcv.setSelectionColor(ContextCompat.getColor(context, R.color.colorPrimary));
        mcv.setArrowColor(Color.rgb(0x77,0x77,0x77));
        mcv.state().edit()
                .setMinimumDate(CalendarDay.from(2017, 0, 1))
                .setMaximumDate(CalendarDay.today())
                .commit();

        mcv.setTitleFormatter(new TitleFormatter() {
            @Override
            public CharSequence format(CalendarDay day) {
                return day.getYear()+"年"+(day.getMonth()+1)+"月";
            }
        });

        CalendarDay c = CalendarDay.from(DateHelper.getTheDayCalendar(theDay));
        mcv.setDateSelected(c, true);
        mcv.setCurrentDate(c);

        mcv.addDecorator(new DayViewDecorator() {
            @Override
            public boolean shouldDecorate(CalendarDay day) {
                // check if day is today
                return CalendarDay.today().equals(day);
            }

            @Override
            public void decorate(DayViewFacade view) {
                // add red foreground span
                view.addSpan(new ForegroundColorSpan(Color.RED));
            }
        });

        mcv.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date,
                                       boolean selected) {
                if(selected){
                    theDayPicked = date.getYear() *10000+(date.getMonth()+1)*100+ date.getDay();
                }
            }
        });
        new AlertDialog.Builder(context).setTitle(null).setView(mcv)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.i(TAG, "TheDay picked is "+ theDayPicked);
                        setTheDay(theDayPicked);
                    }
                }).setNegativeButton("取消", null).show();

    }

    private void showLovedMottos(){
        MainActivity act = (MainActivity) getActivity();
        Intent intent = new Intent(act, LovedMottoActivity.class);
        act.gotoActivity(intent, true, "登录后才能查看喜欢的偶得");
    }

    private void setTheDay(int theday) {
        if(theday < 20170101) {
            return;
        }

        theDay = theday;
        final String newDay = DateHelper.getTheDayStr(theDay);

        final Button mBtnTheday = (Button)(getSpecialToolbar().findViewById(R.id.toolbar_theday_btn));

        float centerX=mBtnTheday.getWidth()/2f;
        float centerY=mBtnTheday.getHeight()/2f;
        Rotate3dAnimation animation=new Rotate3dAnimation(0, 360, centerX, centerY, 100.0f, true);
        animation.setDuration(500);//动画持续时间设为500毫秒
        //animation.setFillAfter(true);//动画完成后保持完成的状态
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mBtnTheday.setText(newDay);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                //mBtnTheday.setText(newDay);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mBtnTheday.startAnimation(animation);

        mRefresh.post(new Runnable() {
            @Override
            public void run() {
                mRefresh.setRefreshing(true);
                mAdapter.setEof(false);
                mData.clear();
                mAdapter.notifyDataSetChanged();
                page = 1;
                getData();
            }
        });
    }

    private void slideDate(int flag){
        if(isRefreshing){
            return;
        }

        Date date = new Date();
        Calendar cal = DateHelper.getTheDayCalendar(theDay);

        if(flag<0){
            cal.add(Calendar.DATE, -1);
            date = cal.getTime();
        }else if (flag>0){
            cal.add(Calendar.DATE, 1);
            if(cal.getTime().after(date)){
                Log.i(TAG,"gesture!");
                return;
            }
            date = cal.getTime();
        }

        setTheDay(DateHelper.getTheDay(date));
    }


    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        private float lastStartX = 0;
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

            if (Math.abs(distanceX) - Math.abs(distanceY) > 32) {
                if(lastStartX != e1.getX()) {
                    lastStartX = e1.getX();
                    Log.i("Gesture Scroll", String.format("%f, %f", distanceX, distanceY));
                    Log.i("Gesture Scroll", String.format("[%f, %f],[%f, %f]", e1.getX(), e1.getY(), e2.getX(), e2.getY()));
                    slideDate(distanceX > 0 ? 1 : -1);

                    return true;
                }
            }

            return super.onScroll(e1, e2, distanceX, distanceY);
        }
    }




}
