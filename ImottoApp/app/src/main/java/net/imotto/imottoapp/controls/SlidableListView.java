package net.imotto.imottoapp.controls;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ListView;

/**
 * Created by sunht on 16/11/2.
 */

public class SlidableListView extends ListView {
    Context context;
    GestureDetector gestureDetector;
    OnFlingListener mListener;

    /*
     * 设置左右滑动监听
     * */
    public void setOnFlingListener(OnFlingListener listener) {
        this.mListener = listener;
        gestureDetector = new GestureDetector(context, new Gesture(context,
                mListener));
    }

    public SlidableListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;

    }

    public SlidableListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
    }

    public SlidableListView(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        if (gestureDetector.onTouchEvent(ev))
            return true;//当左右滑动时自己处理
        return super.onTouchEvent(ev);
    }

    /*
     * 滑动监听
     * */
    public class Gesture implements GestureDetector.OnGestureListener {
        Context context;
        OnFlingListener mListener;

        public Gesture(Context context, OnFlingListener listener) {
            this.context = context;
            this.mListener = listener;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                float distanceX, float distanceY) {
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {

        }

        @Override
        /**
         *
         */
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                               float velocityY) {
            Log.i("SlidableListView","onFling");
            if (Math.abs(e1.getX() - e2.getX()) > Math.abs(e1.getY()
                    - e2.getY())) {//当左右滑动距离大于上下滑动距离时才认为是左右滑
                // 左滑
                if (e1.getX() - e2.getX() > 50) {
                    mListener.onLeftFling();
                    return true;
                }
                // 右滑
                else if (e1.getX() - e2.getX() < -50) {
                    mListener.onRightFling();
                    return true;
                }
            }
            return true;
        }

    }

    /*
     * 左右滑动时调用的监听接口
     * */
    public interface OnFlingListener {
        public void onLeftFling();
        public void onRightFling();
    }
}