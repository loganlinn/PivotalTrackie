package com.loganlinn.pivotaltrackie.ui;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.loganlinn.pivotaltracker.StoryEntry;
import com.loganlinn.pivotaltracker.Story.DetailStoriesQuery;
import com.loganlinn.pivotaltrackie.R;
import com.loganlinn.pivotaltrackie.provider.ProjectContract.Projects;
import com.loganlinn.pivotaltrackie.provider.ProjectContract.Stories;
import com.loganlinn.pivotaltrackie.service.SyncService;
import com.loganlinn.pivotaltrackie.util.DetachableResultReceiver;
import com.loganlinn.pivotaltrackie.util.NotifyingAsyncQueryHandler;
import com.loganlinn.pivotaltrackie.util.SelectionBuilder;
import com.loganlinn.pivotaltrackie.util.NotifyingAsyncQueryHandler.AsyncQueryListener;

public class ViewStoryActivity extends Activity implements AsyncQueryListener,
		DetachableResultReceiver.Receiver {
	private static final String TAG = "ViewStoryActivity";
	private TextView mStoryName;
	private TextView mRequestedBy;
	private TextView mOwnedBy;
	private TextView mAcceptedAt;
	private TextView mCreatedAt;
	private TextView mCurrentState;
	private TextView mDescription;
	private TextView mLabels;
	private TextView mStoryType;
	private TextView mEstimate;

	private Integer mStoryId;
	private Integer mProjectId;

	private StoryEntry mStoryEntry;

	private ActivityState mState;
	private ContentResolver mContentResolver;
	private NotifyingAsyncQueryHandler mQueryHandler;
	private Handler mMessageHandler = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_story);
		// Get intent data
		mStoryId = getIntent().getIntExtra(Stories.STORY_ID, -1);
		mProjectId = getIntent().getIntExtra(Stories.PROJECT_ID, -1);

		if (mStoryId == -1 || mProjectId == -1) {
			Log.e(TAG, "Need story_id and project id,leaving activity");

			finish();
		}
		// Check for previous state
		mState = (ActivityState) getLastNonConfigurationInstance();

		final boolean previousState = (mState != null);

		if (previousState) {
			mState.mReceiver.setReceiver(this);
			updateRefreshStatus();
		} else {
			mState = new ActivityState();
			mState.mReceiver.setReceiver(this);
			onRefreshClick(null);
		}
		mContentResolver = getContentResolver();
		mQueryHandler = new NotifyingAsyncQueryHandler(mContentResolver, this);

		mStoryName = (TextView) findViewById(R.id.tv_story_name);
		mRequestedBy = (TextView) findViewById(R.id.tv_story_requested_by);
		mOwnedBy = (TextView) findViewById(R.id.tv_story_owned_by);
		mAcceptedAt = (TextView) findViewById(R.id.tv_story_accepted_at);
		mCreatedAt = (TextView) findViewById(R.id.tv_story_created_at);
		mCurrentState = (TextView) findViewById(R.id.tv_story_current_state);
		mDescription = (TextView) findViewById(R.id.tv_story_description);
		mLabels = (TextView) findViewById(R.id.tv_story_labels);
		mStoryType = (TextView) findViewById(R.id.tv_story_type);
		mEstimate = (TextView) findViewById(R.id.tv_story_estimate);

		queryStory();
	}

	private void reloadStory(boolean force) {
		if (force) { // if forced, do the query. the async listener (this) will
			// return back here
			queryStory();
			return;
		}
		mStoryEntry.sanitizeNullValues();
		final String storyName = mStoryEntry.get(Stories.NAME);
		final String storyType = mStoryEntry.get(Stories.STORY_TYPE);
		final String requestedBy = mStoryEntry.get(Stories.REQUESTED_BY);
		final String ownedBy = mStoryEntry.get(Stories.OWNED_BY);
		final String acceptedAt = mStoryEntry.get(Stories.ACCEPTED_AT);
		final String createdAt = mStoryEntry.get(Stories.CREATED_AT);
		final String currentState = mStoryEntry.get(Stories.CURRENT_STATE);
		String description = mStoryEntry.get(Stories.DESCRIPTION);
		final String labels = mStoryEntry.get(Stories.LABELS);
		String estimate = mStoryEntry.get(Stories.ESTIMATE);
		
		if(estimate.length() == 0){
			estimate = "0 points";
		}else{
			estimate += " points";
		}
		
		
		Log.i(TAG, "Displaying story: " + storyName);

		mEstimate.setText(estimate);
		mStoryType.setText(storyType);
		mStoryName.setText(storyName);
		mRequestedBy.setText(requestedBy);
		mOwnedBy.setText(ownedBy);
		mAcceptedAt.setText(acceptedAt);
		mCreatedAt.setText(createdAt);
		mCurrentState.setText(currentState);
		mDescription.setText(description);
		mLabels.setText(labels);
	}
	

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		updateRefreshStatus();
		queryStory();
		super.onResume();
	}

	private void updateRefreshStatus() {
		// TODO: if syncing, show animation, otherwise show refresh button
		findViewById(R.id.btn_title_refresh).setVisibility(
				mState.mSyncing ? View.GONE : View.VISIBLE);
		findViewById(R.id.title_refresh_progress).setVisibility(
				mState.mSyncing ? View.VISIBLE : View.GONE);
	}

	private void queryStory() {
		final String storyId = mStoryId.toString();
		final Uri uri = Stories.buildStoryUri(storyId);

		final SelectionBuilder builder = new SelectionBuilder();
		builder.where(Stories.STORY_ID + "=?", storyId);

		mQueryHandler.startQuery(uri, DetailStoriesQuery.PROJECTION, builder
				.getSelection(), builder.getSelectionArgs(), null);

	}

	// Called when the edit button on the toolbar is clicked
	public void onEditClick(View v) {
		final Intent intent = new Intent(this, EditStoryActivity.class);

		intent.putExtras(mStoryEntry.toBundle());
		intent.putExtra(Stories.STORY_ID, mStoryId); // ensure the intent is
														// passed an integer
		intent.putExtra(Stories.PROJECT_ID, mProjectId);
		startActivity(intent);

	}

	// Called when the refresh button on toolbar is clicked
	public void onRefreshClick(View v) {
		final Intent intent = new Intent(SyncService.ACTION_SYNC_STORY, Stories
				.buildStoryUri(mStoryId), this, SyncService.class);

		intent.putExtra(SyncService.EXTRA_STATUS_RECEIVER, mState.mReceiver);
		intent.putExtra(Stories.PROJECT_ID, mProjectId);
		intent.putExtra(Stories.STORY_ID, mStoryId);

		startService(intent);
	}

	@Override
	public void onQueryComplete(int token, Object cookie, Cursor cursor) {
		if (cursor == null)
			return;
		try {
			if (cursor.moveToFirst()) {
				mStoryEntry = StoryEntry.fromCursor(cursor,
						DetailStoriesQuery.PROJECTION);
			}
		} finally {
			cursor.close();
		}
		reloadStory(false);
		Log.i(TAG, mStoryEntry.toString());
	}

	@Override
	public void onReceiveResult(int resultCode, Bundle resultData) {
		Log.v(TAG, "Received Result code=" + resultCode);
		Log.v(TAG, resultData.keySet().toString());

		switch (resultCode) {
		case SyncService.STATUS_RUNNING: {
			mState.mSyncing = true;
			updateRefreshStatus();
			break;
		}
		case SyncService.STATUS_FINISHED: {
			mState.mSyncing = false;
			updateRefreshStatus();
			reloadStory(true);
			// TODO: reload with results
			break;
		}
		case SyncService.STATUS_ERROR: {
			mState.mSyncing = false;
			updateRefreshStatus();
			reloadStory(true);
			final String errorText = getString(R.string.toast_sync_error,
					resultData.getStringArray(Intent.EXTRA_TEXT));
			Toast
					.makeText(ViewStoryActivity.this, errorText,
							Toast.LENGTH_LONG).show();
		}
		}

	}

	@Override
	public Object onRetainNonConfigurationInstance() {
		// Clear any strong references to this Activity, we'll reattach to
		// handle events on the other side.
		mState.mReceiver.clearReceiver();
		return mState;
	}
}
