package co.zaven.radialbarchart.charts;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Scroller;

import java.util.ArrayList;

import co.zaven.radialbarchart.entities.ChartDictionary;
import co.zaven.radialbarchart.utils.ChartUtils;
import co.zaven.radialbarchart.R;
import co.zaven.radialbarchart.listeners.RadialBarChartGestureListener;
import co.zaven.radialbarchart.models.RadialBarChartModel;

/**
 * Created on 19.02.2016.
 *
 * @author Sławomir Onyszko
 */
public class RadialBarChartView extends BaseChartView {

    /**
     * Log tag.
     */
    private static final String TAG = RadialBarChartView.class.getSimpleName();

    private Context mContext;

    private ArrayList<RadialBarChartModel> data;
    private PointF mSliceVector;
    private RectF mSliceBounds;
    private Paint mSlicePaint;
    private Paint mLinesPaint;
    private Paint mPointerLabelPaint;
    private Paint mCenterCirclePaint;
    private Paint mCenterLabelPaint;
    private Paint mSliceHeaderLabelPaint;
    private Paint mRingsFill;

    private SlicesView mSlicesView;
    private CenterCircleView mCenterCircleView;
    private PointerView mPointerView;

    private GestureDetector mDetector;
    private RadialBarChartGestureListener mRadialBarChartGestureListener;
    private Scroller mScroller;
    private ValueAnimator mScrollAnimator;
    private ValueAnimator mRotateAnimator;
    private String mCenterLabelText;

    private ArrayList<Integer> chartColors;

    private float mCenterLabelX;
    private float mCenterLabelY;
    private float mCenterLabelWidth;
    private float mCenterLabelHeight;
    private float mCenterCircleRadius;
    private float mPointerLabelX;
    private float mPointerLabelY;
    private float mPointerLabelWidth;
    private float mPointerLabelHeight;
    private float mPointerRadius;
    private float mPointerX;
    private float mPointerY;
    private float mSliceHeaderLabelWidth;
    private float mSliceHeaderLabelHeight;
    private float mCenterX;
    private float mCenterY;
    private float mDensity;
    private float mSliceScale;
    private float mSliceSpacing;
    private float mArcSegmentStrokeWidth;
    private float mArcSegmentSpacing;
    private float mOuterRingWidth;
    private float mStartAngleRotation;
    private float mTargetAngleRotation;

    private int mPointerAngle;
    private int mSliceHeaderLabelTextColor;
    private int mSliceHeaderLabelBackgroundColor;
    private int mPointerLabelTextColor;
    private int mCenterLabelTextColor;
    private int mCurrentSlice;
    private int mChartRotation;
    private int mTotal;
    private int mChartMode = ChartDictionary.ChartMode.DEFAULT;

    private boolean isScrollActivate;

    public RadialBarChartView(Context context) {
        super(context);
        init(context);
    }

    public RadialBarChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        obtainStyleable(context, attrs);
        init(context);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        // do nothing
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        float xpad = (float) (getPaddingLeft() + getPaddingRight());
        float ypad = (float) (getPaddingTop() + getPaddingBottom());

        float ww = (float) w - xpad;
        float hh = (float) h - ypad;

        float diameter = Math.min(ww, hh);

        mSliceBounds = new RectF(0.0f, 0.0f, diameter, diameter);
        mSliceBounds.offsetTo(getPaddingLeft(), getPaddingTop());

        mCenterX = mSliceBounds.centerX();
        mCenterY = mSliceBounds.centerY();

        mPointerLabelX = mCenterX;
        mPointerLabelY = mSliceBounds.bottom + (mPointerLabelHeight + 70);

        mPointerX = mPointerLabelX;
        mPointerY = mPointerLabelY - mPointerLabelHeight - 20;

        mPointerAngle = (int) ChartUtils.pointToAngle(mPointerX, mPointerY, mCenterX, mCenterY);
        mSliceScale = 360f / mTotal;

        mSlicesView.layout((int) mSliceBounds.left, (int) mSliceBounds.top, (int) mSliceBounds.right, (int) mSliceBounds.bottom);
        mSlicesView.setPivot(mSliceBounds.width() / 2, mSliceBounds.height() / 2);
        mCenterCircleView.layout(0, 0, w, h);
        mPointerView.layout(0, 0, w, h);

        mRadialBarChartGestureListener.setCenterX(mCenterX);
        mRadialBarChartGestureListener.setCenterY(mCenterY);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean result = mDetector.onTouchEvent(event);

        if (!result) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                stopScrolling();
                result = true;
            }
        }

        return result;
    }

    /**
     * This method obtains styleable from xml.
     *
     * @param context {@link Context}
     * @param attrs   {@link AttributeSet}
     */
    private void obtainStyleable(Context context, AttributeSet attrs) {
        try {
            TypedArray attributes = context.getTheme().obtainStyledAttributes(attrs, R.styleable.RadialBarChartView, 0, 0);
            mPointerLabelX = attributes.getDimension(R.styleable.RadialBarChartView_pointerLabelX, 0.0f);
            mPointerLabelY = attributes.getDimension(R.styleable.RadialBarChartView_pointerLabelY, 0.0f);
            mPointerLabelWidth = attributes.getDimension(R.styleable.RadialBarChartView_pointerLabelWidth, 0.0f);
            mPointerLabelHeight = attributes.getDimension(R.styleable.RadialBarChartView_pointerLabelHeight, 0.0f);
            mPointerLabelTextColor = attributes.getColor(R.styleable.RadialBarChartView_pointerLabelColor, 0xff000000);

            mCenterLabelText = attributes.getString(R.styleable.RadialBarChartView_centerLabelText);
            mCenterLabelX = attributes.getDimension(R.styleable.RadialBarChartView_centerLabelX, 0.0f);
            mCenterLabelY = attributes.getDimension(R.styleable.RadialBarChartView_centerLabelY, 0.0f);
            mCenterLabelWidth = attributes.getDimension(R.styleable.RadialBarChartView_centerLabelWidth, 0.0f);
            mCenterLabelHeight = attributes.getDimension(R.styleable.RadialBarChartView_centerLabelHeight, 0.0f);
            mCenterLabelTextColor = attributes.getColor(R.styleable.RadialBarChartView_centerLabelColor, 0xff000000);
            mCenterCircleRadius = attributes.getDimension(R.styleable.RadialBarChartView_centerCircleRadius, ChartDictionary.DefaultMetrics.DEFAULT_CENTER_RADIUS_DP);

            mSliceHeaderLabelWidth = attributes.getDimension(R.styleable.RadialBarChartView_sliceHeaderLabelWidth, 0.0f);
            mSliceHeaderLabelHeight = attributes.getDimension(R.styleable.RadialBarChartView_sliceHeaderLabelHeight, 0.0f);
            mSliceHeaderLabelTextColor = attributes.getColor(R.styleable.RadialBarChartView_sliceHeaderLabelColor, 0xff000000);
            mSliceSpacing = attributes.getDimension(R.styleable.RadialBarChartView_sliceSpacing, ChartDictionary.DefaultMetrics.DEFAULT_SLICE_SPACING_DP);
            mSliceHeaderLabelBackgroundColor = attributes.getColor(R.styleable.RadialBarChartView_sliceHeaderBackgroundColor, getContext().getResources().getColor(R.color.md_grey_500));

            mArcSegmentStrokeWidth = attributes.getDimension(R.styleable.RadialBarChartView_arcSegmentStrokeWidth, ChartDictionary.DefaultMetrics.DEFAULT_ARC_SEGMENT_WIDTH_DP);
            mArcSegmentSpacing = attributes.getDimension(R.styleable.RadialBarChartView_arcSegmentSpacing, ChartDictionary.DefaultMetrics.DEFAULT_ARC_SEGMENT_STROKE_SPACING_DP);
            mOuterRingWidth = attributes.getDimension(R.styleable.RadialBarChartView_outerRingWidth, ChartDictionary.DefaultMetrics.DEFAULT_OUTER_RING_WIDTH_DP);

            mChartRotation = attributes.getInt(R.styleable.RadialBarChartView_rotation, 0);
            mPointerRadius = attributes.getDimension(R.styleable.RadialBarChartView_pointerRadius, 2.0f);
            isScrollActivate = attributes.getBoolean(R.styleable.RadialBarChartView_scrollActivate, false);
            attributes.recycle();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    /**
     * This method initializes all required objects.
     *
     * @param context {@link Context}
     */
    private void init(Context context) {
        mContext = context;

        data = new ArrayList<>();
        chartColors = new ArrayList<>();
        mSliceVector = new PointF();
        mSliceBounds = new RectF();

        ChartUtils.setLayerToSW(this);

        mPointerLabelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPointerLabelPaint.setTextAlign(Paint.Align.CENTER);
        mPointerLabelPaint.setColor(mPointerLabelTextColor);
        if (mPointerLabelHeight == 0) {
            mPointerLabelHeight = mPointerLabelPaint.getTextSize();
        } else {
            mPointerLabelPaint.setTextSize(mPointerLabelHeight);
        }

        mCenterLabelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCenterLabelPaint.setTextAlign(Paint.Align.CENTER);
        mCenterLabelPaint.setFakeBoldText(true);
        mCenterLabelPaint.setColor(mCenterLabelTextColor);
        if (mCenterLabelHeight == 0) {
            mCenterLabelHeight = mCenterLabelPaint.getTextSize();
        } else {
            mCenterLabelPaint.setTextSize(mCenterLabelHeight);
        }

        mCenterCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCenterCirclePaint.setStyle(Paint.Style.FILL);
        mCenterCirclePaint.setColor(Color.WHITE);
        mCenterCirclePaint.setTextSize(mCenterLabelHeight);

        mSlicePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mSlicePaint.setStyle(Paint.Style.FILL);
        mSlicePaint.setAlpha((int) (255f * 0.5));

        mLinesPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinesPaint.setStyle(Paint.Style.STROKE);
        mLinesPaint.setStrokeCap(Paint.Cap.SQUARE);
        mLinesPaint.setColor(Color.WHITE);

        mSliceHeaderLabelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mSliceHeaderLabelPaint.setTextAlign(Paint.Align.CENTER);
        mSliceHeaderLabelPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        mSliceHeaderLabelPaint.setColor(mSliceHeaderLabelTextColor);
        if (mSliceHeaderLabelHeight == 0) {
            mSliceHeaderLabelHeight = mSliceHeaderLabelPaint.getTextSize();
        } else {
            mSliceHeaderLabelPaint.setTextSize(mSliceHeaderLabelHeight);
        }

        mRingsFill = new Paint(Paint.ANTI_ALIAS_FLAG);

        mSlicesView = new SlicesView(getContext());
        addView(mSlicesView);

        mCenterCircleView = new CenterCircleView(getContext());
        addView(mCenterCircleView);

        mPointerView = new PointerView(getContext());
        addView(mPointerView);

        setupAnimation();
        setupInEditMode();
        setupChartColors();
    }

    /**
     * Setting up objects for animation.
     */
    private void setupAnimation() {
        mScroller = new Scroller(getContext(), null, true);
        mScrollAnimator = ValueAnimator.ofFloat(0, 1);
        mScrollAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                tickScrollAnimation();
            }
        });
        mRadialBarChartGestureListener = new RadialBarChartGestureListener(mScroller, mScrollAnimator, this);
        mRadialBarChartGestureListener.setIsScrollActivate(isScrollActivate);
        mDetector = new GestureDetector(RadialBarChartView.this.getContext(), mRadialBarChartGestureListener);
        mDetector.setIsLongpressEnabled(false);
        mRotateAnimator = ValueAnimator.ofFloat(0.0f, 1.0f);
        mRotateAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                updateRotationAnimation(animation);
            }
        });
    }

    /**
     * Fill the chart data for preview.
     */
    private void setupInEditMode() {
        if (this.isInEditMode()) {
            Resources res = getResources();
            addItem("Blue", 45, 2, res.getColor(R.color.md_blue_400));
            addItem("Red", 75, 2, res.getColor(R.color.md_red_400));
            addItem("Orange", 55, 2, res.getColor(R.color.md_orange_400));
            addItem("Yellow", 30, 2, res.getColor(R.color.md_yellow_400));
            addItem("Teal", 90, 2, res.getColor(R.color.md_teal_400));
        }
    }

    private void setupChartColors() {
        Resources resources = getResources();
        chartColors.add(resources.getColor(R.color.chart_color_excellent));
        chartColors.add(resources.getColor(R.color.chart_color_good));
        chartColors.add(resources.getColor(R.color.chart_color_almost_good));
        chartColors.add(resources.getColor(R.color.chart_color_not_bad));
        chartColors.add(resources.getColor(R.color.chart_color_bad));
    }

    /**
     * Check if slice was touch ane
     *
     * @param x x touch coordinate.
     * @param y y touch coordinate.
     * @return returns true if the slice has been affected otherwise false.
     */
    public boolean checkTouch(float x, float y) {
        boolean result = true;
        float circleRadius = mSliceBounds.width() / 2f;
        PointF position = new PointF(x - mCenterX, y - mCenterY);
        if (position.length() > circleRadius) {
            result = false;
        }
        float touchAngle = (ChartUtils.pointToAngle(x, y, mCenterX, mCenterY) - mChartRotation + 360f) % 360f;
        float startAngle = 0;
        for (int i = 0; i < data.size(); i++) {
            RadialBarChartModel model = data.get(i);
            float sliceValue = model.getSliceWeight();
            float endAngle = (Math.abs(sliceValue) * mSliceScale);
            float newStartAngle = startAngle + endAngle;

            if (mSliceBounds.contains(x, y) && touchAngle >= startAngle && touchAngle <= newStartAngle) {
                mCurrentSlice = i;
                int centerAngle = (int) (startAngle + (newStartAngle - startAngle) / 2);
                int targetAngle = (mPointerAngle - centerAngle) % 360;

                setChartRotation(targetAngle, true);
                invalidate();
            }
            startAngle = newStartAngle;
        }
        return result;
    }

    /**
     * This method is used to update an animation after user touched the slice.
     *
     * @param animation {@link ValueAnimator}
     */
    private void updateRotationAnimation(ValueAnimator animation) {
        float scale = animation.getAnimatedFraction();
        float rotation = mStartAngleRotation + (mTargetAngleRotation - mStartAngleRotation) * scale;
        rotation = (rotation % 360 + 360) % 360;
        setChartRotation((int) rotation, false);
    }

    /**
     * This method starts rotation animation from start angle to target angle.
     *
     * @param startAngleRotation  the angle from rotation begins.
     * @param targetAngleRotation the target angle.
     */
    private void startAnimation(float startAngleRotation, float targetAngleRotation) {
        this.mStartAngleRotation = (startAngleRotation % 360 + 360) % 360;
        this.mTargetAngleRotation = (targetAngleRotation % 360 + 360) % 360;
        mRotateAnimator.start();
    }

    /**
     * Method gets chart rotation.
     *
     * @return the chart rotation.
     */
    public int getChartRotation() {
        return mChartRotation;
    }

    /**
     * Method is used to set chart rotation with animation or without animation.
     *
     * @param rotation  angle rotation.
     * @param isAnimate set true if you want the animation otherwise, set false.
     */
    public void setChartRotation(int rotation, boolean isAnimate) {
        if (isAnimate) {
            mRotateAnimator.cancel();
            startAnimation(mChartRotation, rotation);
        } else {
            rotation = (rotation % 360 + 360) % 360;
            this.mChartRotation = rotation;
        }
        mSlicesView.invalidate();
    }

    private class SlicesView extends View {

        private static final float CIRCLE_LIMIT = 359.9999f;

        private RectF mainBounds;
        private RectF smallerBounds;

        /**
         * X coordinate of center.
         */
        private float cx;

        /**
         * Y coordinate of center.
         */
        private float cy;

        /**
         * Radius of smaller bounds.
         */
        private float circleRadius;

        public SlicesView(Context context) {
            super(context);
        }

        public SlicesView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            mainBounds = new RectF(0, 0, w, h);
            cx = mainBounds.centerX();
            cy = mainBounds.centerY();

            smallerBounds = new RectF(mainBounds.left + mOuterRingWidth, mainBounds.top + mOuterRingWidth, w - mOuterRingWidth, h - mOuterRingWidth);
            circleRadius = smallerBounds.width() / 2f;

        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            drawSlices(canvas);
            drawHeaders(canvas);
            drawArcRings(canvas);
            drawLines(canvas);
        }

        /**
         * This method sets pivot accordingly to specified coordinates.
         *
         * @param x x coordinate.
         * @param y y coordinate.
         */
        public void setPivot(float x, float y) {
            setPivotX(x);
            setPivotY(y);
        }

        /**
         * This method draws slices.
         *
         * @param canvas {@link Canvas}
         */
        private void drawSlices(Canvas canvas) {
            float lastAngle = mChartRotation;
            for (int i = 0; i < data.size(); i++) {
                RadialBarChartModel model = data.get(i);
                float angle = Math.abs(model.getSliceWeight()) * mSliceScale;
                mSlicePaint.setColor(Color.WHITE);
                canvas.drawArc(smallerBounds, lastAngle, angle, true, mSlicePaint);
                lastAngle += angle;
            }
        }

        /**
         * This method draws lines that separate the slices.
         *
         * @param canvas {@link Canvas}
         */
        private void drawLines(Canvas canvas) {
            if (mSliceSpacing < 1) {
                return;
            }
            float lastAngle = mChartRotation;
            mLinesPaint.setStrokeWidth(mSliceSpacing);
            for (int i = 0; i < data.size(); i++) {
                RadialBarChartModel model = data.get(i);
                float angle = Math.abs(model.getSliceWeight()) * mSliceScale;
                mSliceVector.set((float) (Math.cos(Math.toRadians(lastAngle))), (float) (Math.sin(Math.toRadians(lastAngle))));
                ChartUtils.normalizeVector(mSliceVector);
                float x1 = cx + mSliceVector.x * (circleRadius - (mSliceSpacing / 2));
                float y1 = cy + mSliceVector.y * (circleRadius - (mSliceSpacing / 2));
                canvas.drawLine(cx, cy, x1, y1, mLinesPaint);
                lastAngle += angle;
            }
        }

        /**
         * This method draws headers.
         *
         * @param canvas {@link Canvas}
         */
        private void drawHeaders(Canvas canvas) {
            float lastAngle = mChartRotation;
            for (int i = 0; i < data.size(); i++) {
                RadialBarChartModel model = data.get(i);
                float angle = Math.abs(model.getSliceWeight()) * mSliceScale;
                mRingsFill.setColor(mSliceHeaderLabelBackgroundColor);
                drawArcSegment(canvas, cx, cy, circleRadius, circleRadius + mOuterRingWidth, lastAngle, angle, mRingsFill, model.getLabel(), mSliceHeaderLabelPaint);
                lastAngle += angle;
            }
        }

        /**
         * This method draws rings that visualize percentage value of slice.
         *
         * @param canvas {@link Canvas}
         */
        private void drawArcRings(Canvas canvas) {
            int arcSegmentCount;
            float range;
            float lastAngle = mChartRotation;
            for (int i = 0; i < data.size(); i++) {
                RadialBarChartModel model = data.get(i);
                float angle = Math.abs(model.getSliceWeight()) * mSliceScale;

                range = (model.getPercent() * (circleRadius - mCenterCircleRadius - 0.01f)) / 100;
                arcSegmentCount = Math.round(range / (mArcSegmentSpacing + mArcSegmentStrokeWidth));

                for (int j = 0; j < arcSegmentCount; j++) {
                    float rInn = j > 0 ? mCenterCircleRadius + ((mArcSegmentSpacing + mArcSegmentStrokeWidth) * j) : mCenterCircleRadius;
                    float rOut = rInn + mArcSegmentStrokeWidth;

                    mRingsFill.setColor(model.getColor());

                    drawArcSegment(canvas, cx, cy, rInn, rOut, lastAngle, angle, mRingsFill, null, null);
                }
                lastAngle += angle;
            }

        }


        /**
         * This method draws rings that visualize percentage value of slice.
         *
         * @param canvas {@link Canvas}
         */
        private void drawArcRings(Canvas canvas, int segmentCount) {
            float range;
            float lastAngle = mChartRotation;

            float mArcSegmentSpacing = 0;
            float mArcSegmentStrokeWidth = 0;
            for (int i = 0; i < data.size(); i++) {
                RadialBarChartModel model = data.get(i);
                float angle = Math.abs(model.getSliceWeight()) * mSliceScale;

                range = (model.getPercent() * (circleRadius - mCenterCircleRadius - 0.01f)) / 100;
                mArcSegmentSpacing = (range / segmentCount);
                mArcSegmentStrokeWidth = (range / segmentCount);

                for (int j = 0; j < segmentCount; j++) {
                    float rInn = j > 0 ? mCenterCircleRadius + ((mArcSegmentSpacing + mArcSegmentStrokeWidth) * j) : mCenterCircleRadius;
                    float rOut = rInn + mArcSegmentStrokeWidth;

                    for (int k = 0; k < chartColors.size(); k++) {
                        mRingsFill.setColor(chartColors.get(i));
                    }

                    drawArcSegment(canvas, cx, cy, rInn, rOut, lastAngle, angle, mRingsFill, null, null);
                }
                lastAngle += angle;
            }

        }

        /**
         * Draws a thick arc between the defined angles, see {@link Canvas#drawArc} for more
         * This method is equivalent to
         * <pre><code>
         * float rMid = (rInn + rOut) / 2;
         * paint.setStyle(Style.STROKE); // there's nothing to fill
         * paint.setStrokeWidth(rOut - rInn); // thickness
         * canvas.drawArc(new RectF(cx - rMid, cy - rMid, cx + rMid, cy + rMid), startAngle, sweepAngle, false, paint);
         * </code></pre>
         * but supports different fill and stroke paints.
         *
         * @param canvas     canvas.
         * @param cx         horizontal middle point of the oval.
         * @param cy         vertical middle point of the oval.
         * @param rInn       inner radius of the arc segment.
         * @param rOut       outer radius of the arc segment.
         * @param startAngle see {@link Canvas#drawArc}
         * @param sweepAngle see {@link Canvas#drawArc}, capped at +/-360
         * @param fill       filling paint, can be <code>null</code>
         * @param labelText  the slice label text.
         * @param labelPaint the slice label paint
         * @see Canvas#drawArc
         */
        private void drawArcSegment(Canvas canvas, float cx, float cy, float rInn, float rOut, float startAngle, float sweepAngle, Paint fill, String labelText, Paint labelPaint) {
            if (sweepAngle > CIRCLE_LIMIT) {
                sweepAngle = CIRCLE_LIMIT;
            }
            if (sweepAngle < -CIRCLE_LIMIT) {
                sweepAngle = -CIRCLE_LIMIT;
            }

            RectF outerRect = new RectF(cx - rOut, cy - rOut, cx + rOut, cy + rOut);
            RectF innerRect = new RectF(cx - rInn, cy - rInn, cx + rInn, cy + rInn);

            Path segmentPath = new Path();
            double start = Math.toRadians(startAngle);
            segmentPath.moveTo((float) (cx + rInn * Math.cos(start)), (float) (cy + rInn * Math.sin(start)));
            segmentPath.lineTo((float) (cx + rOut * Math.cos(start)), (float) (cy + rOut * Math.sin(start)));
            segmentPath.arcTo(outerRect, startAngle, sweepAngle);
            double end = Math.toRadians(startAngle + sweepAngle);
            segmentPath.lineTo((float) (cx + rInn * Math.cos(end)), (float) (cy + rInn * Math.sin(end)));
            segmentPath.arcTo(innerRect, startAngle + sweepAngle, -sweepAngle);

            if (fill != null) {
                canvas.drawPath(segmentPath, fill);
            }

            if (labelPaint != null && labelText != null && !labelText.isEmpty()) {
                Path midway = new Path();
                float r = (rInn + rOut) / 2;
                RectF segment = new RectF(cx - r, cy - r, cx + r, cy + r);
                midway.addArc(segment, startAngle, sweepAngle);
                canvas.drawTextOnPath(labelText, midway, 0, 10, labelPaint);
            }
        }
    }

    /**
     * This is a view that displays the number of points obtained.
     */
    private class CenterCircleView extends View {

        public CenterCircleView(Context context) {
            super(context);
        }

        public CenterCircleView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            try {
                canvas.drawCircle(mCenterX, mCenterY, mCenterCircleRadius, mCenterCirclePaint);
                canvas.drawText(mCenterLabelText, mCenterX, mCenterY + (mCenterLabelHeight / 3), mCenterLabelPaint);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }

        }
    }

    /**
     * It is the view of the indicator, which indicates the selected slice.
     */
    private class PointerView extends View {

        public PointerView(Context context) {
            super(context);
        }

        public PointerView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            try {
                if (data != null && data.size() > 0) {
                    canvas.drawText(data.get(mCurrentSlice).getLabel(), mPointerLabelX, mPointerLabelY, mPointerLabelPaint);
                }
                canvas.drawCircle(mPointerX, mPointerY, mPointerRadius, mPointerLabelPaint);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
        }
    }

    private void tickScrollAnimation() {
        if (!mScroller.isFinished()) {
            mScroller.computeScrollOffset();
            setChartRotation(mScroller.getCurrY(), false);
        } else {
            mScrollAnimator.cancel();
        }
    }

    /**
     * This method stops chart scrolling animation.
     */
    public void stopScrolling() {
        mScroller.forceFinished(true);
    }


    /**
     * This method sets list to the list of chart data.
     *
     * @param data list of {@link RadialBarChartModel}
     */
    public void setData(ArrayList<RadialBarChartModel> data) {
        this.data = data;
        for (int i = 0; i < data.size(); i++) {
            mTotal += data.get(i).getSliceWeight();
        }
    }

    /**
     * This method adds an item to the list of chart data.
     *
     * @param label       the slice label text.
     * @param percent     the slice percentage value.
     * @param sliceWeight the slice weight.
     * @param color       the slice color.
     */
    public void addItem(String label, float percent, int sliceWeight, int color) {
        RadialBarChartModel model = new RadialBarChartModel(label, percent, sliceWeight, color);
        mTotal += sliceWeight;
        data.add(model);
    }

}
