package co.zaven.radialbarchart.listeners;

import android.animation.ValueAnimator;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.Scroller;

import co.zaven.radialbarchart.charts.RadialBarChartView;
import co.zaven.radialbarchart.utils.ChartUtils;


/**
 * The gesture listener which is used to interact with chart.
 * <p/>
 * Created on 24.02.2016.
 *
 * @author Sławomir Onyszko
 */
public class RadialBarChartGestureListener extends GestureDetector.SimpleOnGestureListener {

    /**
     * Log TAG.
     */
    private static final String TAG = RadialBarChartGestureListener.class.getSimpleName();

    /**
     * The initial fling velocity is divided by this amount.
     */
    public static final int FLING_VELOCITY_DOWNSCALE = 4;

    /**
     * The instance of chart view.
     */
    private RadialBarChartView mRadialBarChartView;
    private Scroller mScroller;
    private ValueAnimator mScrollAnimator;

    private float mCenterX;
    private float mCenterY;

    private boolean isScrollActivate;

    public RadialBarChartGestureListener(Scroller scroller, ValueAnimator scrollAnimator, RadialBarChartView radialBarChartView) {
        this.mScroller = scroller;
        this.mScrollAnimator = scrollAnimator;
        this.mRadialBarChartView = radialBarChartView;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        if (isScrollActivate) {
            float scrollTheta = ChartUtils.vectorToScalarScroll(distanceX, distanceY, e2.getX() - mCenterX, e2.getY() - mCenterY);
            mRadialBarChartView.setChartRotation(mRadialBarChartView.getChartRotation() - (int) scrollTheta / FLING_VELOCITY_DOWNSCALE, false);
        }
        return true;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if (isScrollActivate) {
            float scrollTheta = ChartUtils.vectorToScalarScroll(velocityX, velocityY, e2.getX() - mCenterX, e2.getY() - mCenterY);
            mScroller.fling(0, mRadialBarChartView.getChartRotation(), 0, (int) scrollTheta / FLING_VELOCITY_DOWNSCALE, 0, 0, Integer.MIN_VALUE, Integer.MAX_VALUE);
            mScrollAnimator.setDuration(mScroller.getDuration());
            mScrollAnimator.start();
        }
        return true;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        if (!isScrollActivate) {
            mRadialBarChartView.checkTouch(e.getX(), e.getY());
        }
        return true;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        if (isScrollActivate) {
            if (isAnimationRunning()) {
                mRadialBarChartView.stopScrolling();
            }
        } else {
            mRadialBarChartView.checkTouch(e.getX(), e.getY());
        }
        return true;
    }

    /**
     * Check if animation is running.
     *
     * @return returns true if animation is running otherwise false.
     */
    private boolean isAnimationRunning() {
        return !mScroller.isFinished();
    }

    public float getCenterX() {
        return mCenterX;
    }

    public void setCenterX(float mCenterX) {
        this.mCenterX = mCenterX;
    }

    public float getCenterY() {
        return mCenterY;
    }

    public void setCenterY(float mCenterY) {
        this.mCenterY = mCenterY;
    }

    public boolean isScrollActivate() {
        return isScrollActivate;
    }

    public void setIsScrollActivate(boolean isScrollActivate) {
        this.isScrollActivate = isScrollActivate;
    }
}
