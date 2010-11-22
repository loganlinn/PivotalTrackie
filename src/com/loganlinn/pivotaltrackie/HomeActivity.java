package com.loganlinn.pivotaltrackie;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.loganlinn.pivotaltracker.util.DetachableResultReceiver;
import com.loganlinn.pivotaltracker.util.NotifyingAsyncQueryHandler;
import com.loganlinn.pivotaltracker.util.NotifyingAsyncQueryHandler.AsyncQueryListener;
import com.loganlinn.pivotaltrackie.provider.ProjectContract.Projects;
import com.loganlinn.pivotaltrackie.service.SyncService;

public class HomeActivity extends Activity implements AsyncQueryListener,
		DetachableResultReceiver.Receiver {
	private static final String TAG = "HomeActivity";

	private HomeState mState;
	private Handler mMessageHandler = new Handler();
	private NotifyingAsyncQueryHandler mQueryHandler;
	
	private Button mRefreshButton;
	
	private void showLoginIfNoTokenExists(){
		
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		showLoginIfNoTokenExists();
		
		mState = (HomeState) getLastNonConfigurationInstance();
		final boolean previousState = (mState != null);

		if (previousState) {

		} else {
			mState = new HomeState();
			mState.receiver_.setReceiver(this);
			onRefreshClick(null);
		}
		
		// Set up handler for now project list query
        mQueryHandler = new NotifyingAsyncQueryHandler(getContentResolver(), this);
        
        mRefreshButton = (Button)findViewById(R.id.refresh_project);
        mRefreshButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				onRefreshClick(v);
			}
        });
	}
	
	public void onProjectClick(View v){
		
	}
	
	@Override
	public void onQueryComplete(int token, Object cookie, Cursor cursor) {
		try{
			// Check if the query did not return any results
			if(!cursor.moveToFirst()){
				//TODO: notify user that no results were returned
				return;
			}
			
		} finally {
			cursor.close();
		}
	}

	private void updateRefreshStatus() {
		// TODO: if syncing, show animation, otherwise show refresh button
	}

	@Override
	public Object onRetainNonConfigurationInstance() {
		// Clear any strong references to this Activity, we'll reattach to
		// handle events on the other side.
		mState.receiver_.clearReceiver();
		return mState;
	}

	@Override
	public void onReceiveResult(int resultCode, Bundle resultData) {
		switch (resultCode) {
		case SyncService.STATUS_RUNNING: {
			mState.syncing_ = true;
			updateRefreshStatus();
			break;
		}
		case SyncService.STATUS_FINISHED: {
			mState.syncing_ = false;
			updateRefreshStatus();
			// TODO: reload with results
			break;
		}
		case SyncService.STATUS_ERROR: {
			mState.syncing_ = false;
			updateRefreshStatus();
			final String errorText = getString(R.string.toast_sync_error,
					resultData.getStringArray(Intent.EXTRA_TEXT));
			Toast.makeText(HomeActivity.this, errorText, Toast.LENGTH_LONG)
					.show();
		}
		}
	}

	/** Handle "refresh" title-bar action. */
	public void onRefreshClick(View v) {
		// trigger off background sync
		final Intent intent = new Intent(Intent.ACTION_SYNC, null, this,
				SyncService.class);
		intent.putExtra(SyncService.EXTRA_STATUS_RECEIVER, mState.receiver_);
		startService(intent);

		// reloadNowPlaying(true);
	}

	private static class HomeState {
		public DetachableResultReceiver receiver_;
		public boolean syncing_ = false;

		private HomeState() {
			receiver_ = new DetachableResultReceiver(new Handler());
		}
	}

	private interface ProjectsQuery {
		String[] PROJECTION = { Projects.PROJECT_ID, Projects.NAME,
				Projects.CURRENT_VELOCITY, Projects.LAST_ACTIVITY_AT, };

		int PROJECT_ID = 0;
		int NAME = 1;
		int CURRENT_VELOCITY = 2;
		int LAST_ACTIVITY_AT = 3;
	}
	
}
