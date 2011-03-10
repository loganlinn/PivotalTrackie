package com.loganlinn.pivotaltrackie.util;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ViewFlipper;

import com.loganlinn.pivotaltrackie.R;

public class FlingGestureListener extends SimpleOnGestureListener {
	private static final String TAG = "FlingGestureListener";

	private static final int SWIPE_MIN_DISTANCE = 120;
	private static final int SWIPE_MAX_OFF_PATH = 250;
	private static final int SWIPE_THRESHOLD_VELOCITY = 200;

	private ViewFlipper mViewFlipper;
	private Animation mSlideRightIn;
	private Animation mSlideRightOut;
	private Animation mSlideLeftIn;
	private Animation mSlideLeftOut;

	public int mFlipperLength = 2; // Number of views in the flipper
	public int mCurrentFlipperIndex = 0; // Current position of the flipper -

	// used to avoid circular rotation

	public FlingGestureListener(ViewFlipper flipper) {
		mViewFlipper = flipper;
		final Context context = flipper.getContext();
		mSlideRightIn = AnimationUtils.loadAnimation(context,
				R.anim.slide_right_in);
		mSlideRightOut = AnimationUtils.loadAnimation(context,
				R.anim.slide_right_out);
		mSlideLeftIn = AnimationUtils.loadAnimation(context,
				R.anim.slide_left_in);
		mSlideLeftOut = AnimationUtils.loadAnimation(context,
				R.anim.slide_left_out);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.view.GestureDetector.SimpleOnGestureListener#onFling(android.
	 * view.MotionEvent, android.view.MotionEvent, float, float)
	 */
	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		// Flip between views, but do not rotate circularly
		if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
				&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {

			if (mCurrentFlipperIndex < mFlipperLength-1) {
				mViewFlipper.setInAnimation(mSlideRightIn);
				mViewFlipper.setOutAnimation(mSlideRightOut);
				mViewFlipper.showNext();

				mCurrentFlipperIndex++;
				Log.i(TAG, "Flipped to next, ind=" + mCurrentFlipperIndex);
			} else {
				Log.i(TAG, "Didn't flip to next");

			}
		} else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
				&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {

			if (mCurrentFlipperIndex > 0) {
				mViewFlipper.setInAnimation(mSlideLeftIn);
				mViewFlipper.setOutAnimation(mSlideLeftOut);
				mViewFlipper.showPrevious();

				mCurrentFlipperIndex--;
				Log.i(TAG, "Flipped to previous, ind=" + mCurrentFlipperIndex);
			} else {
				Log.i(TAG, "Didn't flip to previous");
			}
		}

		return super.onFling(e1, e2, velocityX, velocityY);
	}

}
