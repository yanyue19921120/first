package com.graduation.common.widget.circleProgressButton;

import android.graphics.drawable.GradientDrawable;

public class StrokeGradientDrawable {
	private int mStrokeWidth;
	private int mStrokeColor;

	private GradientDrawable mGradientDrawable;

	public StrokeGradientDrawable(GradientDrawable drawable) {
		// TODO Auto-generated constructor stub
		mGradientDrawable = drawable;
	}

	public int getStrokeWidth() {
		return mStrokeWidth;
	}

	public void setStrokeWidth(int strokeWidth) {
		mStrokeWidth = strokeWidth;
		mGradientDrawable.setStroke(strokeWidth, getmStrokeColor());
	}

	public int getmStrokeColor() {
		return mStrokeColor;
	}

	public void setmStrokeColor(int mStrokeColor) {
		this.mStrokeColor = mStrokeColor;
		mGradientDrawable.setStroke(getStrokeWidth(), mStrokeColor);
	}
	
	public GradientDrawable getGradientDrawable() {
		return mGradientDrawable;
	}
	

}
