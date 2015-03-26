package com.graduation.common.widget.circleProgressButton;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

public class CircularProgressDrawable extends Drawable {

	private float mSweepAngle;
	private float mStartAngle;
	private int mSize;
	private int mStrokeWidth;
	private int mStrokeColor;
	
	
	private RectF mRectF;
    private Paint mPaint;
    private Path mPath;

	public CircularProgressDrawable(int size, int strokeWidth, int strokeColor) {
		mSize = size;
		mStrokeWidth = strokeWidth;
		mStrokeColor = strokeColor;
		mStartAngle = -90;
		mSweepAngle = 0;
	}

	public void setSweepAngle(float sweepAngle) {
		mSweepAngle = sweepAngle;
	}

	public int getSize() {
		return mSize;
	}

	@Override
	public void draw(Canvas canvas) {
		final Rect bounds=getBounds();
		if(mPath==null)
		{
			mPath=new Path();
		}
		mPath.reset();
		mPath.addArc(getRect(), mStartAngle, mSweepAngle);
		mPath.offset(bounds.left, bounds.top);
		canvas.drawPath(mPath, createPaint());
		
	}
	
	private Paint createPaint() {
		if(mPaint==null)
		{
			mPaint=new Paint();
			mPaint.setAntiAlias(true);
			mPaint.setStyle(Paint.Style.STROKE);
			mPaint.setStrokeWidth(mStrokeWidth);
			mPaint.setColor(mStrokeColor);
		}
		return mPaint;
	}

	private RectF getRect() {
		if(mRectF==null) {
			int index=mStrokeWidth;
			mRectF=new RectF(index,index, getSize()-index, getSize()-index);
		}
		return mRectF;
	}
	

	@Override
	public void setAlpha(int alpha) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setColorFilter(ColorFilter cf) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getOpacity() {
		// TODO Auto-generated method stub
		return 1;
	}

}
