package com.loganlinn.pivotaltracker;

public interface AuthenticateListener {
	public void onAuthenticateSuccess(String token);
	public void onAuthenticateError(AuthenticateError error);
}
