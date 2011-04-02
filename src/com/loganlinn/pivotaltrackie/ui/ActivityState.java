package com.loganlinn.pivotaltrackie.ui;

import android.os.Handler;

import com.loganlinn.pivotaltrackie.util.DetachableResultReceiver;

class ActivityState {
	public DetachableResultReceiver mReceiver;
	public boolean mSyncing = false;

	ActivityState() {
		mReceiver = new DetachableResultReceiver(new Handler());
	}
}
