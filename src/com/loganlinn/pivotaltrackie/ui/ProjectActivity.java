package com.loganlinn.pivotaltrackie.ui;

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
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

import com.loganlinn.pivotaltracker.StoryEntry;
import com.loganlinn.pivotaltracker.Story.DefaultStoriesQuery;
import com.loganlinn.pivotaltrackie.R;
import com.loganlinn.pivotaltrackie.provider.ProjectContract.Projects;
import com.loganlinn.pivotaltrackie.provider.ProjectContract.Stories;
import com.loganlinn.pivotaltrackie.service.SyncService;
import com.loganlinn.pivotaltrackie.util.DetachableResultReceiver;
import com.loganlinn.pivotaltrackie.util.FlingGestureListener;
import com.loganlinn.pivotaltrackie.util.Lists;
import com.loganlinn.pivotaltrackie.util.NotifyingAsyncQueryHandler;
import com.loganlinn.pivotaltrackie.util.SelectionBuilder;
import com.loganlinn.pivotaltrackie.util.NotifyingAsyncQueryHandler.AsyncQueryListener;

public class ProjectActivity extends Activity implements AsyncQueryListener,
		DetachableResultReceiver.Receiver, OnItemClickListener,
		OnItemLongClickListener {
	private static final String TAG = "ProjectActivity";
	private static final Integer EMPTY_PROJECT_ID = -1;
	
	private List<StoryEntry>[] mStoryList;
	private ListView[] mListView;
	private StoryListAdapter[] mListAdapter;
	private ActivityState mState;
	private ContentResolver mContentResolver;
	private NotifyingAsyncQueryHandler mQueryHandler;
	private Handler mMessageHandler = new Handler();
	private Integer mProjectId;
	
	private ViewFlipper mListViewFlipper;
	private GestureDetector mGestureDetector;
	private FlingGestureListener mGestureListener;

	private TextView mTitle;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_project);

		mState = (ActivityState) getLastNonConfigurationInstance();
		final boolean previousState = (mState != null);

		if (previousState) {
			mState.mReceiver.setReceiver(this);
			updateRefreshStatus();
			reloadStories(true);
		} else {
			mProjectId = getIntent().getIntExtra(Projects.PROJECT_ID,
					EMPTY_PROJECT_ID);
			final String projectName = getIntent().getStringExtra(Projects.NAME);
			if(projectName != null){
				mTitle = (TextView) findViewById(R.id.tv_title_bar_title);
				mTitle.setText(projectName);
				
			}
			Log.i(TAG, "Initializing stories for project id=" + mProjectId);
			mState = new ActivityState();
			mState.mReceiver.setReceiver(this);
			onRefreshClick(null);
		}
		mContentResolver = getContentResolver();

		mQueryHandler = new NotifyingAsyncQueryHandler(mContentResolver, this);
		
		mTitle = (TextView) findViewById(R.id.tv_title_bar_title);
		mStoryList = new List[2];
		mStoryList[0] = Lists.newArrayList();
		//mStoryList[1] = Lists.newArrayList();

		mListView = new ListView[2];
		mListView[0] = (ListView) findViewById(R.id.lv_stories0);
		//mListView[1] = (ListView) findViewById(R.id.lv_stories1);
		mListView[0].setOnItemClickListener(this);
		mListView[0].setOnItemLongClickListener(this);
		//mListView[1].setOnItemClickListener(this);
		//mListView[1].setOnItemLongClickListener(this);
		mListAdapter = new StoryListAdapter[2];
		
		mListAdapter[0] = new StoryListAdapter(this, mStoryList[0]);
		mListView[0].setAdapter(mListAdapter[0]);
		
		//mListAdapter[1] = new StoryListAdapter(this, mStoryList[1]);
		//mListView[1].setAdapter(mListAdapter[1]);
		
		mListViewFlipper = (ViewFlipper) findViewById(R.id.vf_stories_flipper);
		
		mGestureListener = new FlingGestureListener(mListViewFlipper);
		mGestureDetector = new GestureDetector(mGestureListener);
		
		OnTouchListener emptyTouchListener = new OnTouchListener(){
			//Used to padd touch event to parent, where touch event is handled
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return mGestureDetector.onTouchEvent(event);
			}
		};
		mListView[0].setOnTouchListener(emptyTouchListener);
		//mListView[1].setOnTouchListener(emptyTouchListener);
		queryStories();
	}
	private void updateTitle(){
		switch(mGestureListener.mCurrentFlipperIndex){
			
		}
	}
	private void reloadStories(boolean forceRefresh) {
		queryStories();
	}

	private void queryStories() {
		final Uri uri = Stories.CONTENT_URI;
		final SelectionBuilder builder = new SelectionBuilder();

		builder.where(Stories.PROJECT_ID + "=?", mProjectId.toString());
		
		
		//Start the query, receive response in onQueryComplete method
		mQueryHandler.startQuery(uri,
				DefaultStoriesQuery.PROJECTION, builder.getSelection(), builder
						.getSelectionArgs(), null);
				
	}
	
	@Override
	public void onQueryComplete(int token, Object cookie, Cursor cursor) {
		//TODO: Split up stories based on iteration
		//TODO: Display in UI that there are multiple lists
		if(cursor == null) return;
		try {
			if (cursor.moveToFirst()) {
				mStoryList[0].clear();
				//mStoryList[1].clear();
				int i = 0;
				do {
					final StoryEntry story = StoryEntry.fromCursor(cursor);
					//SPLIT the lists
					mStoryList[0].add(story);

				} while (cursor.moveToNext());
				mListAdapter[0].notifyDataSetChanged();
				//mListAdapter[1].notifyDataSetChanged();
			} else {
				Log.i(TAG, "No stories found in database for project");
			}

		} finally {
			cursor.close();
		}
		
	}
	/* (non-Javadoc)
	 * @see android.app.Activity#onTouchEvent(android.view.MotionEvent)
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return mGestureDetector.onTouchEvent(event);
	}

	/** Handle "refresh" title-bar action. */
	public void onRefreshClick(View v) {
		// trigger off background sync
		if (mProjectId == EMPTY_PROJECT_ID) {
			Log.e(TAG, "Project ID unspecified. Cannot refresh");
			finish();
		}
		final Intent intent = new Intent(SyncService.ACTION_SYNC_STORIES,
				Projects.buildProjectUri(mProjectId), this, SyncService.class);
		intent.putExtra(SyncService.EXTRA_STATUS_RECEIVER, mState.mReceiver);
		startService(intent);
	}

	private void updateRefreshStatus() {
		// TODO: if syncing, show animation, otherwise show refresh button
		findViewById(R.id.btn_title_refresh).setVisibility(
				mState.mSyncing ? View.GONE : View.VISIBLE);
		findViewById(R.id.title_refresh_progress).setVisibility(
				mState.mSyncing ? View.VISIBLE : View.GONE);
	}
	
	public void onNewStoryClick(View v){
		final Intent intent = new Intent(this, EditStoryActivity.class);
		intent.putExtra(Stories.PROJECT_ID, mProjectId);
		intent.putExtra(Stories.STORY_ID, Stories.NEW_STORY);
		startActivity(intent);
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Log.i(TAG,"Clicked story at position "+position);
		final ListView listView = (ListView) view.getParent();
		final ListAdapter listAdapter = listView.getAdapter();
		final StoryEntry entry = (StoryEntry) listAdapter.getItem(position); //mStoryList[mGestureListener.mCurrentFlipperIndex].get(position);
		
		Intent intent = new Intent(this, ViewStoryActivity.class);
		
		final int storyId = Integer.valueOf(entry.get(Stories.STORY_ID));
		intent.putExtra(Stories.STORY_ID, storyId);
		intent.putExtra(Stories.PROJECT_ID, mProjectId);
		startActivity(intent);	
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		onRefreshClick(null);
		super.onResume();
	}
	/**
	 * TODO: Show story menu
	 */
	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {

		return true;
	}

	private class StoryListAdapter extends BaseAdapter {
		private LayoutInflater mInflater;
		private List mList;
		public StoryListAdapter(Context context, List list) {
			mInflater = LayoutInflater.from(context);
			mList = list;
		}

		@Override
		public int getCount() {
			return mList.size();
		}

		@Override
		public Object getItem(int position) {
			return mList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;

			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.list_item_story, null);
				holder = new ViewHolder();
				holder.mName = (TextView) convertView
						.findViewById(R.id.tv_story_name);
				holder.mEstimate = (TextView) convertView
						.findViewById(R.id.tv_story_estimate);
				holder.mTypeIcon = (ImageView) convertView.findViewById(R.id.iv_story_type);
				convertView.setTag(holder);

			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			final StoryEntry story = (StoryEntry) getItem(position);
			final String estimate = story.get(Stories.ESTIMATE);
			holder.mName.setText(story.get(Stories.NAME));

			if ("-1".equals(estimate)) {
				holder.mEstimate.setText("");
			} else {
				holder.mEstimate.setText(estimate);
			}
			
			final String currentStatus = story.get(Stories.CURRENT_STATE);
			
			//Set the background color of the story depending on its status
			if(Stories.CURRENT_STATUS_ACCEPTED.equals(currentStatus)){
				convertView.setBackgroundResource(R.color.story_accepted);
			}else if(Stories.CURRENT_STATUS_DELIVERED.equals(currentStatus)){
				convertView.setBackgroundResource(R.color.story_delivered);
			}else if(Stories.CURRENT_STATUS_STARTED.equals(currentStatus)){
				convertView.setBackgroundResource(R.color.story_started);
			}else if(Stories.CURRENT_STATUS_FINISHED.equals(currentStatus)){
				convertView.setBackgroundResource(R.color.story_finished);
			}else{ // CURRENT_STATUS_UNSCHEDULED
				convertView.setBackgroundResource(R.color.story_unscheduled);
			}
			
			final String storyType = story.get(Stories.STORY_TYPE);
			
			if(Stories.STORY_TYPE_BUG.equals(storyType)){
				holder.mTypeIcon.setImageResource(R.drawable.bug_icon);
				holder.mTypeIcon.setVisibility(ImageView.VISIBLE);
			}else if(Stories.STORY_TYPE_FEATURE.equals(storyType)){
				holder.mTypeIcon.setImageResource(R.drawable.feature);
				holder.mTypeIcon.setVisibility(ImageView.VISIBLE);
			}else if(Stories.STORY_TYPE_CHORE.equals(storyType)){
				holder.mTypeIcon.setImageResource(R.drawable.chore_icon);
				holder.mTypeIcon.setVisibility(ImageView.VISIBLE);
			}else{
				holder.mTypeIcon.setVisibility(ImageView.INVISIBLE);
			}
			
			return convertView;
		}

		private class ViewHolder {
			TextView mName;
			TextView mEstimate;
			ImageView mTypeIcon;
		}
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
			reloadStories(false);
			// TODO: reload with results
			break;
		}
		case SyncService.STATUS_ERROR: {
			mState.mSyncing = false;
			updateRefreshStatus();
			final String errorText = getString(R.string.toast_sync_error,
					resultData.getStringArray(Intent.EXTRA_TEXT));
			Toast.makeText(ProjectActivity.this, errorText, Toast.LENGTH_LONG)
					.show();
		}
		}

	}

}
