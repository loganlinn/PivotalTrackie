package com.loganlinn.pivotaltrackie.ui;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.loganlinn.pivotaltracker.PivotalTracker;
import com.loganlinn.pivotaltracker.Project.DefaultProjectsQuery;
import com.loganlinn.pivotaltrackie.R;
import com.loganlinn.pivotaltrackie.provider.ProjectContract.Projects;
import com.loganlinn.pivotaltrackie.service.SyncService;
import com.loganlinn.pivotaltrackie.util.DateTimeUtils;
import com.loganlinn.pivotaltrackie.util.DetachableResultReceiver;
import com.loganlinn.pivotaltrackie.util.NotifyingAsyncQueryHandler;
import com.loganlinn.pivotaltrackie.util.NotifyingAsyncQueryHandler.AsyncQueryListener;

public class HomeActivity extends Activity implements AsyncQueryListener,
		DetachableResultReceiver.Receiver, OnItemClickListener {
	private static final String TAG = "HomeActivity";

	private ActivityState mState;
	private Handler mMessageHandler = new Handler();
	private NotifyingAsyncQueryHandler mQueryHandler;

	public ProjectListAdapter mListAdapter;
	protected ListView mListView;
	protected List<Object[]> mProjectList;

	private ContentResolver mContentResolver;
	private final SimpleDateFormat mDatetimeFormat = PivotalTracker
			.getDateFormat();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (showLoginIfNoTokenExists()) {
			return;
		}

		setContentView(R.layout.activity_home);

		mState = (ActivityState) getLastNonConfigurationInstance();
		final boolean previousState = (mState != null);

		if (previousState) {
			mState.mReceiver.setReceiver(this);
			updateRefreshStatus();
			// reloadProjects(true);
		} else {
			mState = new ActivityState();
			mState.mReceiver.setReceiver(this);
			onRefreshClick(null);
		}
		mContentResolver = getContentResolver();
		// Set up handler for now project list query
		mQueryHandler = new NotifyingAsyncQueryHandler(mContentResolver, this);

		mListView = (ListView) findViewById(R.id.lv_home);
		mListView.setOnItemClickListener(this);
		mListAdapter = new ProjectListAdapter(this);
		mProjectList = new ArrayList<Object[]>();

		mListView.setAdapter(mListAdapter);
		
	}
	
	private boolean showLoginIfNoTokenExists() {

		String token = PivotalTracker.getToken(getBaseContext());

		if (token == null) {
			Log.i(TAG, "No token found, showing login activity");
			Intent intent = new Intent(this, LoginActivity.class);
			startActivity(intent);
			return true;
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		updateRefreshStatus();
		super.onResume();
	}
	
	@Override
	public void onQueryComplete(int token, Object cookie, Cursor cursor) {
		if (cursor == null)
			return;
		try {
			if (cursor.moveToFirst()) {
				mProjectList.clear();
				do {

					final Object[] projectData = {
							cursor.getInt(DefaultProjectsQuery.PROJECT_ID),
							cursor.getString(DefaultProjectsQuery.NAME),
							cursor
									.getInt(DefaultProjectsQuery.CURRENT_VELOCITY),
							cursor
									.getString(DefaultProjectsQuery.LAST_ACTIVITY_AT) };
					Log.v(TAG, "Adding Project, " + projectData[1]
							+ ", to list");
					mProjectList.add(projectData);
				} while (cursor.moveToNext());

				mListAdapter.notifyDataSetChanged();
			} else {
				onNoProjectsToDisplay();
			}
		} finally {
			cursor.close();
		}
	}

	private void updateRefreshStatus() {
		// TODO: if syncing, show animation, otherwise show refresh button
		findViewById(R.id.btn_title_refresh).setVisibility(
				mState.mSyncing ? View.GONE : View.VISIBLE);
		findViewById(R.id.title_refresh_progress).setVisibility(
				mState.mSyncing ? View.VISIBLE : View.GONE);
	}

	@Override
	public Object onRetainNonConfigurationInstance() {
		// Clear any strong references to this Activity, we'll reattach to
		// handle events on the other side.
		mState.mReceiver.clearReceiver();
		return mState;
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
			queryProjects();
			// TODO: reload with results
			break;
		}
		case SyncService.STATUS_ERROR: {
			mState.mSyncing = false;
			updateRefreshStatus();
			final String errorText = getString(R.string.toast_sync_error,
					resultData.getStringArray(Intent.EXTRA_TEXT));
			Toast.makeText(HomeActivity.this, errorText, Toast.LENGTH_LONG)
					.show();
		}
		}
	}

	private void onNoProjectsToDisplay() {
		Toast.makeText(HomeActivity.this, "No projects found!",
				Toast.LENGTH_SHORT).show();
	}

	private void queryProjects() {
		final Uri uri = Projects.CONTENT_URI;
		mQueryHandler.startQuery(uri, DefaultProjectsQuery.PROJECTION, null,
				null, null);

	}

	/** Handle "refresh" title-bar action. */
	public void onRefreshClick(View v) {
		// trigger off background sync
		final Intent intent = new Intent(SyncService.ACTION_SYNC_PROJECTS,
				null, this, SyncService.class);
		intent.putExtra(SyncService.EXTRA_STATUS_RECEIVER, mState.mReceiver);
		startService(intent);

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		final Object[] projectData = mProjectList.get(position);
		final Integer projectId = (Integer) projectData[DefaultProjectsQuery.PROJECT_ID];
		final String projectName = (String) projectData[DefaultProjectsQuery.NAME];

		final Intent intent = new Intent(getBaseContext(),
				ProjectActivity.class);
		intent.putExtra(Projects.PROJECT_ID, projectId);
		intent.putExtra(Projects.NAME, projectName);
		startActivity(intent);
	}

	private class ProjectListAdapter extends BaseAdapter {
		private LayoutInflater mInflater;

		public ProjectListAdapter(Context context) {
			mInflater = LayoutInflater.from(context);

		}

		@Override
		public int getCount() {
			return mProjectList.size();
		}

		@Override
		public Object getItem(int position) {
			return mProjectList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int pos, View convertView, ViewGroup parent) {
			// A ViewHolder keeps references to children views to avoid
			// unneccessary calls
			// to findViewById() on each row.
			ViewHolder holder;
			// When convertView is not null, we can reuse it directly, there is
			// no need
			// to reinflate it. We only inflate a new View when the convertView
			// supplied
			// by ListView is null.

			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.list_item_project,
						null);
				holder = new ViewHolder();
				holder.mProjectName = (TextView) convertView
						.findViewById(R.id.tv_project_name);
				holder.mLastActivity = (TextView) convertView
						.findViewById(R.id.tv_project_last_activity);
				holder.mCurrentVelocity = (TextView) convertView
						.findViewById(R.id.tv_project_velocity);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			final Object[] projectData = mProjectList.get(pos);
			String lastActivity = (String) projectData[DefaultProjectsQuery.LAST_ACTIVITY_AT];
			if (lastActivity != null) {
				try {
					Log.i(TAG, lastActivity);
					lastActivity = "2010/12/06 14:34:29 UTC";
					
					final Date d = mDatetimeFormat.parse(lastActivity);
					long updated = d.getTime();
					lastActivity = "Last activity "
							+ DateTimeUtils.getInstance(
									convertView.getContext())
									.getTimeDiffString(updated).toLowerCase();
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}else{
				lastActivity = "";
			}
			holder.mProjectName
					.setText((CharSequence) projectData[DefaultProjectsQuery.NAME]);
			holder.mLastActivity.setText(lastActivity);
			holder.mCurrentVelocity
					.setText(String
							.valueOf(projectData[DefaultProjectsQuery.CURRENT_VELOCITY]));

			return convertView;
		}

		private class ViewHolder {
			TextView mProjectName;
			TextView mLastActivity;
			TextView mCurrentVelocity;
		}

	}
}
