package com.example.cycleviewtest;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.WindowManager;

/**
 * Created by clark on 2016/10/11.
 */

public class CircleProgress extends View {
    private int mCircleXY;
    private Paint mCirclePaint, mArcPaint, mTextPaint;
    private float mRadious;
    private RectF mArcRectF;
    private float mSweepAngle;
    private String mShowText;
    private float mSweepValue;

    public CircleProgress(Context context) {
        this(context, null);
    }

    public CircleProgress(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        int length = wm.getDefaultDisplay().getWidth();
        mCircleXY = length / 2;
        mRadious = (float) (length * 0.5 / 2);
        mArcRectF = new RectF(
                (float) (length * 0.1),
                (float) (length * 0.1),
                (float) (length * 0.9),
                (float) (length * 0.9));
        mCirclePaint = new Paint();
        mCirclePaint.setAntiAlias(true);
        mCirclePaint.setColor(Color.BLUE);
        mCirclePaint.setStyle(Paint.Style.FILL);

        mArcPaint = new Paint();
        mArcPaint.setAntiAlias(true);
        mArcPaint.setStyle(Paint.Style.STROKE);
        mArcPaint.setStrokeWidth(120);
        mArcPaint.setColor(Color.MAGENTA);

        mTextPaint = new Paint();
        mTextPaint.setTextSize(35);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setColor(Color.BLACK);
        mTextPaint.setStyle(Paint.Style.FILL);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        mSweepAngle = (mSweepValue / 100) * 360;
        mShowText = "比例："+mSweepValue + "%";
        float textSize = mTextPaint.measureText(mShowText);
        super.onDraw(canvas);
        //绘制圆
        canvas.drawCircle(mCircleXY, mCircleXY, mRadious, mCirclePaint);
        //
        canvas.drawArc(mArcRectF, 270, mSweepAngle, false, mArcPaint);
        //
        canvas.drawText(mShowText, 0, mShowText.length(),
                mCircleXY - textSize/2, mCircleXY+mTextPaint.getTextSize()/4, mTextPaint);
    }

    public void setSweepValue(float sweepValue) {
        if (sweepValue != 0) {
            mSweepValue = sweepValue;
        } else {
            mSweepValue = 25;
        }
        this.invalidate();
    }
}
