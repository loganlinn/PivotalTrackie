package com.loganlinn.pivotaltrackie;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.loganlinn.pivotaltracker.AuthenticateError;
import com.loganlinn.pivotaltracker.AuthenticateListener;
import com.loganlinn.pivotaltracker.PivotalTracker;

public class LoginActivity extends Activity implements AuthenticateListener {
    private static final String TAG = "LoginActivity";
    private Button submitButton_;
    private EditText usernameText_;
    private EditText passwordText_;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        submitButton_ = (Button) findViewById(R.id.btn_login);
        usernameText_ = (EditText) findViewById(R.id.et_username);
        passwordText_ = (EditText) findViewById(R.id.et_password);
        
        submitButton_.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				onLoginSubmit(v);
			}
        	
        });
    }
    
    private void onLoginSubmit(View v){
    	PivotalTracker pt = new PivotalTracker(this);
    	final String username = usernameText_.getText().toString();
    	final String password = passwordText_.getText().toString();
    	pt.fetchToken(username, password, this);
    	
    }

	@Override
	public void onAuthenticateError(AuthenticateError error) {
		Toast.makeText(this, error.getMessage(), Toast.LENGTH_LONG).show();
	}

	@Override
	public void onAuthenticateSuccess(String token) {
		Toast.makeText(this, "Logged in Sucessfully", Toast.LENGTH_SHORT).show();
		finish();
	}
}