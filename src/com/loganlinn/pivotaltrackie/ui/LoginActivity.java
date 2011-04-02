package com.loganlinn.pivotaltrackie.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.loganlinn.pivotaltracker.PivotalTrackerError;
import com.loganlinn.pivotaltracker.PivotalTrackerListener;
import com.loganlinn.pivotaltracker.PivotalTracker;
import com.loganlinn.pivotaltrackie.R;
import com.loganlinn.pivotaltrackie.util.DetachableResultReceiver;

public class LoginActivity extends Activity implements PivotalTrackerListener {
    private static final String TAG = "LoginActivity";
    private Button submitButton_;
    private EditText usernameText_;
    private EditText passwordText_;
    private PivotalTracker mPivotalTracker;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
       
       mPivotalTracker = PivotalTracker.getInstance(getBaseContext()); 
        submitButton_ = (Button) findViewById(R.id.btn_login);
        usernameText_ = (EditText) findViewById(R.id.et_username);
        passwordText_ = (EditText) findViewById(R.id.et_password);
       
    }
    
    public void onSubmitClick(View v){
    	
    	final String username = usernameText_.getText().toString();
    	final String password = passwordText_.getText().toString();
    	
    	mPivotalTracker.fetchToken(username, password, this);
    	
    }

	@Override
	public void onError(PivotalTrackerError error) {
		Toast.makeText(getBaseContext(), "Could not authenticate", Toast.LENGTH_LONG);	
	}

	@Override
	public void onSuccess(String token) {
		finish();
	}
}