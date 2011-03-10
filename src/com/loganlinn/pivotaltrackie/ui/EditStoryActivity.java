package com.loganlinn.pivotaltrackie.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.loganlinn.pivotaltracker.StoryEntry;
import com.loganlinn.pivotaltracker.Member.DefaultMemberQuery;
import com.loganlinn.pivotaltrackie.R;
import com.loganlinn.pivotaltrackie.provider.ProjectContract.Members;
import com.loganlinn.pivotaltrackie.provider.ProjectContract.Stories;
import com.loganlinn.pivotaltrackie.service.SyncService;
import com.loganlinn.pivotaltrackie.util.DetachableResultReceiver;
import com.loganlinn.pivotaltrackie.util.NotifyingAsyncQueryHandler;
import com.loganlinn.pivotaltrackie.util.SelectionBuilder;
import com.loganlinn.pivotaltrackie.util.NotifyingAsyncQueryHandler.AsyncQueryListener;

//import com.loganlinn.pivotaltrackie.provider.ProjectContract;
public class EditStoryActivity extends Activity implements AsyncQueryListener,
		DetachableResultReceiver.Receiver {
	private static final String TAG = "EditStoryActivity";

	private Spinner mEstimateSpinner;
	private Spinner mStoryTypeSpinner;
	private Spinner mRequestedBySpinner;
	private Spinner mOwnedBySpinner;
	private EditText mLabelsText;
	private EditText mDescriptionText;
	private EditText mNameText;
	private TextView mTitle;
	private int mProjectId;
	private int mStoryId;
	private StoryEntry mStoryEntry = null;

	private ArrayList<String> mMembers;
	private ActivityState mState;
	private ContentResolver mContentResolver;
	private NotifyingAsyncQueryHandler mQueryHandler;
	private ArrayAdapter<CharSequence> mMemberListAdapter;
	private ArrayAdapter<CharSequence> mEstimateAdapter;
	private ArrayAdapter<CharSequence> mStoryTypeAdapter;
	private ArrayList<CharSequence> mMemberList;
	private ArrayList<CharSequence> mEstimateList;
	private ArrayList<CharSequence> mStoryTypeList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_story);

		// Check for previous state
		mState = (ActivityState) getLastNonConfigurationInstance();

		final boolean previousState = (mState != null);

		if (previousState) {
			mState.mReceiver.setReceiver(this);
		} else {
			mState = new ActivityState();
			mState.mReceiver.setReceiver(this);
		}
		
		mContentResolver = getContentResolver();
		mQueryHandler = new NotifyingAsyncQueryHandler(mContentResolver, this);

		mStoryId = getIntent().getIntExtra(Stories.STORY_ID, Stories.NEW_STORY);
		mProjectId = getIntent().getIntExtra(Stories.PROJECT_ID, -5);
		Log.i(TAG, "Editing story "+mStoryId+" in project "+mProjectId);
		final Bundle bundleData = getIntent().getExtras();
		if (bundleData != null) {
			mStoryEntry = StoryEntry.fromBundle(bundleData);
			mStoryEntry.sanitizeNullValues();
			Log.i(TAG, mStoryEntry.toString());
			
		}

		// TODO: get point scheme from project
		final int estimateValues = R.array.estimate123;
		final int spinnerItem = android.R.layout.simple_spinner_item;
		final Resources resources = this.getResources();

		mEstimateList = new ArrayList(Arrays.asList(resources
				.getStringArray(estimateValues)));
		mStoryTypeList = new ArrayList(Arrays.asList(resources
				.getStringArray(R.array.story_types)));
		mMemberList = new ArrayList<CharSequence>();
		mMemberList.add(""); // add empty value

		mEstimateAdapter = ArrayAdapter.createFromResource(this,
				estimateValues, spinnerItem);
		mStoryTypeAdapter = ArrayAdapter.createFromResource(this,
				R.array.story_types, spinnerItem);

		mMemberListAdapter = new ArrayAdapter(this, spinnerItem, mMemberList);

		mStoryTypeAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mEstimateAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mMemberListAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		mEstimateSpinner = (Spinner) findViewById(R.id.sp_estimate);
		mEstimateSpinner.setAdapter(mEstimateAdapter);
		mStoryTypeSpinner = (Spinner) findViewById(R.id.sp_story_type);
		mStoryTypeSpinner.setAdapter(mStoryTypeAdapter);

		mRequestedBySpinner = (Spinner) findViewById(R.id.sp_requested_by);
		mRequestedBySpinner.setAdapter(mMemberListAdapter);
		mOwnedBySpinner = (Spinner) findViewById(R.id.sp_owned_by);
		mOwnedBySpinner.setAdapter(mMemberListAdapter);

		mLabelsText = (EditText) findViewById(R.id.et_labels);
		mDescriptionText = (EditText) findViewById(R.id.et_description);
		mNameText = (EditText) findViewById(R.id.et_story_name);

		// When creating a new story, show a clear button instead of delete
		// button
		mTitle = (TextView) findViewById(R.id.tv_title_bar_title);
		if (mStoryId == Stories.NEW_STORY) {
			mTitle.setText(R.string.add_story);
			ImageButton clearButton = (ImageButton) findViewById(R.id.ic_title_cancel);
			ImageButton deleteButton = (ImageButton) findViewById(R.id.ic_title_delete);
			deleteButton.setVisibility(ImageButton.INVISIBLE);
			clearButton.setVisibility(ImageButton.VISIBLE);
			ImageView delSeparator = (ImageView) findViewById(R.id.iv_delete_sep);
			ImageView clrSeparator = (ImageView) findViewById(R.id.iv_clear_sep);
			delSeparator.setVisibility(ImageView.INVISIBLE);
			clrSeparator.setVisibility(ImageView.INVISIBLE);
			
			
			
		} else {
			mTitle.setText(R.string.edit_story);
			displayData();
		}

	}

	public void setSpinnerByValue(String value, List list, Spinner spinner) {
		int position = list.indexOf(value);
		if (position == -1 && "".equals(value) == false) {
			Log.i(TAG, value + " not in list, adding & moving spinner to it");
			Log.i(TAG, list.toString());
			Log.i(TAG, spinner.toString());

			position = list.size();
			list.add(value);
		}
		try{
			spinner.setSelection(position);
		}catch(java.lang.IndexOutOfBoundsException e){
			e.printStackTrace();
		}
	}

	public void onCancelClick(View v) {
		finish();
	}

	public void onSaveClick(View v) {
		Log.i(TAG, "Saving story for project " + mProjectId);

		stageData();
		Intent intent = new Intent(SyncService.ACTION_SYNC_STORY, null, this,
				SyncService.class);

		Bundle storyData = mStoryEntry.toBundle();
		storyData.putBoolean(SyncService.SEND_TO_API, true);
		intent.putExtras(storyData);
		intent.putExtra(SyncService.EXTRA_STATUS_RECEIVER, mState.mReceiver);
		intent.putExtra(Stories.PROJECT_ID, mProjectId);
		intent.putExtra(Stories.STORY_ID, mStoryId);
		startService(intent);
		finish();
	}

	public void onDeleteClick(View v) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Are you sure you want to delete?").setCancelable(
				false).setPositiveButton("Yes",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						Intent intent = new Intent(
								SyncService.ACTION_DELETE_STORY, null, EditStoryActivity.this,
								SyncService.class);

						intent.putExtra(SyncService.EXTRA_STATUS_RECEIVER,
								mState.mReceiver);
						intent.putExtra(Stories.PROJECT_ID, mProjectId);
						intent.putExtra(Stories.STORY_ID, mStoryId);

						startService(intent);

						EditStoryActivity.this.finish();
					}
				}).setNegativeButton("No",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
		AlertDialog alert = builder.create();
		alert.show();
	}

	public void stageData() {
		mStoryEntry.put(Stories.STORY_ID, String.valueOf(mStoryId));
		mStoryEntry.put(Stories.NAME, mNameText.getText().toString());
		mStoryEntry.put(Stories.DESCRIPTION, mDescriptionText.getText()
				.toString());
		mStoryEntry.put(Stories.LABELS, mLabelsText.getText().toString());
		String estimate = ((CharSequence) mEstimateSpinner
				.getSelectedItem()).toString();
		estimate = estimate.replace(" points", "");
		mStoryEntry.put(Stories.ESTIMATE, estimate);
		mStoryEntry.put(Stories.STORY_TYPE, ((CharSequence) mStoryTypeSpinner
				.getSelectedItem()).toString());
		mStoryEntry.put(Stories.OWNED_BY, ((CharSequence) mOwnedBySpinner
				.getSelectedItem()).toString());
		mStoryEntry.put(Stories.REQUESTED_BY,
				((CharSequence) mRequestedBySpinner.getSelectedItem())
						.toString());
	}

	public void displayData() {

		mNameText.setText(mStoryEntry.get(Stories.NAME));
		mDescriptionText.setText(mStoryEntry.get(Stories.DESCRIPTION));
		mLabelsText.setText(mStoryEntry.get(Stories.LABELS));
		setSpinnerByValue(mStoryEntry.get(Stories.REQUESTED_BY), mMemberList,
				mRequestedBySpinner);
		setSpinnerByValue(mStoryEntry.get(Stories.OWNED_BY), mMemberList,
				mOwnedBySpinner);
		
		String estimate = mStoryEntry.get(Stories.ESTIMATE);
		if(estimate.length() == 0){
			estimate = "0 points";
		}else{
			estimate += " points";
		}
		Log.i(TAG, "ESTIMATE: "+estimate);
		
		setSpinnerByValue(estimate, mEstimateList,
				mEstimateSpinner);
		setSpinnerByValue(mStoryEntry.get(Stories.STORY_TYPE), mStoryTypeList,
				mStoryTypeSpinner);

	}

	public void queryMembers() {
		final Uri membersUri = Members.CONTENT_URI;
		final SelectionBuilder builder = new SelectionBuilder();
		builder.where(Members.PROJECT_ID + "=?", String.valueOf(mProjectId));
		mQueryHandler.startQuery(membersUri, DefaultMemberQuery.PROJECTION);
	}

	@Override
	public void onQueryComplete(int token, Object cookie, Cursor cursor) {
		if (cursor != null && cursor.moveToFirst()) {
			do {

				mMembers.add(cursor.getString(DefaultMemberQuery.NAME));

			} while (cursor.moveToNext());
		}
		displayData();
	}

	@Override
	public void onReceiveResult(int resultCode, Bundle resultData) {
		Log.v(TAG, "Received Result code=" + resultCode);
		Log.v(TAG, resultData.keySet().toString());

		switch (resultCode) {
		case SyncService.STATUS_RUNNING: {
			mState.mSyncing = true;
			break;
		}
		case SyncService.STATUS_FINISHED: {
			mState.mSyncing = false;
			Toast.makeText(EditStoryActivity.this, "Save sucessful",
					Toast.LENGTH_LONG).show();
			break;
		}
		case SyncService.STATUS_ERROR: {
			mState.mSyncing = false;
			final String errorText = getString(R.string.toast_sync_error,
					resultData.getStringArray(Intent.EXTRA_TEXT));
			Toast
					.makeText(EditStoryActivity.this, errorText,
							Toast.LENGTH_LONG).show();
		}
		}
	}

}
