package com.graduation.common.widget.circleProgressButton;


public class StateManager {
	private boolean mIsEnabled;
	private int mProgress;

	public StateManager(CircleProgressButton progressButton) {
		// TODO Auto-generated constructor stub
		mIsEnabled = progressButton.isEnabled();
		mProgress = progressButton.getProgress();
	}

	public void saveProgress(CircleProgressButton progressButton) {
		mProgress = progressButton.getProgress();
	}

	public boolean isEnabled() {
		return mIsEnabled;
	}

	public int getProgress() {
		return mProgress;
	}

	public void CheckState(CircleProgressButton progressButton) {
		if (progressButton.getProgress() != getProgress()) {
			progressButton.setProgress(progressButton.getProgress());

		} else if (progressButton.isEnabled() != isEnabled()) {
			progressButton.setEnabled(progressButton.isEnabled());
		}
	}

}
