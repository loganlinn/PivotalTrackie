package com.loganlinn.pivotaltracker;

public interface PivotalTrackerListener {
	public void onSuccess(String token);
	public void onError(PivotalTrackerError error);
}
