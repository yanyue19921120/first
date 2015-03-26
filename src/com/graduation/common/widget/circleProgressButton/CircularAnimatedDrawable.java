package com.graduation.common.widget.circleProgressButton;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.util.Property;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

public class CircularAnimatedDrawable extends Drawable implements Animatable {

	private static final Interpolator ANGLE_INTERPOLATOR = new LinearInterpolator();
	private static final Interpolator SWEEP_INTERPOLATOR = new DecelerateInterpolator();
	private static final int ANGLE_ANIMATOR_DURATION = 2000;
	private static final int SWEEP_ANIMATOR_DURATION = 600;

	public static final int MIN_SWEEP_ANGLE = 30;
	private final RectF fBounds = new RectF();

	private ObjectAnimator mObjectAnimatorSweep;
	private ObjectAnimator mObjectAnimatorAngle;
	private boolean mModeAppearing;
	private Paint mPaint;
	private float mCurrentGlobalAngleOffset;
	private float mCurrentGlobalAngle;
	private float mCurrentSweepAngle;
	private float mBorderWidth;
	private boolean mRuning;

	private Property<CircularAnimatedDrawable, Float> mAngleProperty = new Property<CircularAnimatedDrawable, Float>(
			Float.class, "angle") {

		@Override
		public Float get(CircularAnimatedDrawable object) {
			// TODO Auto-generated method stub
			return object.getCurrentGlobalAngle();
		}

		@Override
		public void set(CircularAnimatedDrawable object, Float value) {
			object.setCurrentGlobalAngle(value);
		};
	};
	private Property<CircularAnimatedDrawable, Float> mSweepProperty = new Property<CircularAnimatedDrawable, Float>(
			Float.class, "arc") {
		@Override
		public Float get(CircularAnimatedDrawable object) {
			return object.getCurrentSweepAngle();
		}

		@Override
		public void set(CircularAnimatedDrawable object, Float value) {
			object.setCurrentSweepAngle(value);
		}
	};

	public float getCurrentGlobalAngle() {
		return mCurrentGlobalAngle;
	}

	public void setCurrentGlobalAngle(float currentGlobalAngle) {
		mCurrentGlobalAngle = currentGlobalAngle;
		invalidateSelf();
	}

	public float getCurrentSweepAngle() {
		return mCurrentSweepAngle;
	}

	public void setCurrentSweepAngle(float currentSweepAngle) {
		mCurrentSweepAngle = currentSweepAngle;
		invalidateSelf();
	}

	public CircularAnimatedDrawable(int color, float borderWidth) {
		// TODO Auto-generated constructor stub
		mBorderWidth = borderWidth;
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeWidth(borderWidth);
		mPaint.setColor(color);
		setupAnimations();
	}

	private void setupAnimations() {
		// TODO Auto-generated method stub
		mObjectAnimatorAngle = ObjectAnimator.ofFloat(this, mAngleProperty,
				360f);
		mObjectAnimatorAngle.setInterpolator(ANGLE_INTERPOLATOR);
		mObjectAnimatorAngle.setDuration(ANGLE_ANIMATOR_DURATION);
		mObjectAnimatorAngle.setRepeatMode(ValueAnimator.RESTART);
		mObjectAnimatorAngle.setRepeatCount(ValueAnimator.INFINITE);

		mObjectAnimatorSweep = ObjectAnimator.ofFloat(this, mSweepProperty,
				360f - MIN_SWEEP_ANGLE * 2);
		mObjectAnimatorSweep.setInterpolator(SWEEP_INTERPOLATOR);
		mObjectAnimatorSweep.setDuration(SWEEP_ANIMATOR_DURATION);
		mObjectAnimatorSweep.setRepeatMode(ValueAnimator.RESTART);
		mObjectAnimatorSweep.setRepeatCount(ValueAnimator.INFINITE);
		mObjectAnimatorSweep.addListener(new Animator.AnimatorListener() {

			@Override
			public void onAnimationStart(Animator animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationRepeat(Animator animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animator animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationCancel(Animator animation) {
				// TODO Auto-generated method stub
				toggleAppearingMode();
			}
		});
	}

	private void toggleAppearingMode() {
		mModeAppearing = !mModeAppearing;
		if (mModeAppearing) {
			mCurrentGlobalAngleOffset = (mCurrentGlobalAngleOffset + MIN_SWEEP_ANGLE * 2) % 360;
		}
	}

	@Override
	public void start() {
		// TODO Auto-generated method stub
		if (isRunning()) {
			return;
		}
		mRuning = true;
		mObjectAnimatorAngle.start();
		mObjectAnimatorSweep.start();
		invalidateSelf();

	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
		if(!isRunning())
		{
			return;
		}
		mRuning=false;
		mObjectAnimatorAngle.cancel();
		mObjectAnimatorSweep.cancel();
		invalidateSelf();

	}

	@Override
	public boolean isRunning() {
		// TODO Auto-generated method stub
		return mRuning;
	}

	@Override
	public void draw(Canvas canvas) {
		// TODO Auto-generated method stub
		float startAngle = mCurrentGlobalAngle - mCurrentGlobalAngleOffset;
		float sweepAngle = mCurrentSweepAngle;
		if (!mModeAppearing) {
			startAngle = startAngle + sweepAngle;
			sweepAngle = 360 - sweepAngle - MIN_SWEEP_ANGLE;
		} else {
			sweepAngle += MIN_SWEEP_ANGLE;
		}
		canvas.drawArc(fBounds, startAngle, sweepAngle, false, mPaint);
	}

	@Override
	public void setAlpha(int alpha) {
		// TODO Auto-generated method stub
		mPaint.setAlpha(alpha);

	}

	@Override
	public void setColorFilter(ColorFilter cf) {
		// TODO Auto-generated method stub
		mPaint.setColorFilter(cf);

	}

	@Override
	public int getOpacity() {
		// TODO Auto-generated method stub
		return PixelFormat.TRANSPARENT;
	}

	@Override
	protected void onBoundsChange(Rect bounds) {
		// TODO Auto-generated method stub
		super.onBoundsChange(bounds);
		fBounds.left = bounds.left + mBorderWidth / 2f + .5f;
		fBounds.right = bounds.right - mBorderWidth / 2f - .5f;
		fBounds.top = bounds.top + mBorderWidth / 2f + .5f;
		fBounds.bottom = bounds.bottom - mBorderWidth / 2f - .5f;
	}

}
