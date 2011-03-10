package com.loganlinn.pivotaltrackie.service;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.client.HttpClient;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.app.IntentService;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import com.loganlinn.pivotaltracker.PivotalTracker;
import com.loganlinn.pivotaltracker.StoryEntry;
import com.loganlinn.pivotaltracker.PivotalTracker.Prefs;
import com.loganlinn.pivotaltrackie.io.RemoteExecutor;
import com.loganlinn.pivotaltrackie.io.RemoteProjectsHandler;
import com.loganlinn.pivotaltrackie.io.RemoteStoriesHandler;
import com.loganlinn.pivotaltrackie.io.XmlHandler;
import com.loganlinn.pivotaltrackie.io.XmlHandler.HandlerException;
import com.loganlinn.pivotaltrackie.provider.ProjectContract;
import com.loganlinn.pivotaltrackie.provider.ProjectContract.Projects;
import com.loganlinn.pivotaltrackie.provider.ProjectContract.Stories;
import com.loganlinn.pivotaltrackie.util.HttpUtils;

/**
 * IntentService is a base class for Services that handle asynchronous requests
 * (expressed as Intents) on demand. Clients send requests through
 * startService(Intent) calls; the service is started as needed, handles each
 * Intent in turn using a worker thread, and stops itself when it runs out of
 * work.
 * 
 * @author loganlinn
 * 
 */
public class SyncService extends IntentService {
	private static final String TAG = "SyncService";
	public static final String EXTRA_STATUS_RECEIVER = "com.loganlinn.pivotaltrackie.extra.STATUS_RECEIVER";
	// public static final String ACTION_SYNC_PROJECTS =
	// "com.loganlinn.pivotaltrackie.action.SYNC_PROJECTS";
	public static final int STATUS_RUNNING = 0x1;
	public static final int STATUS_ERROR = 0x2;
	public static final int STATUS_FINISHED = 0x3;

	public static final String ACTION_SYNC_PROJECTS = "com.loganlinn.pivotaltrackie.action.SYNC_PROJECTS";
	public static final String ACTION_SYNC_STORIES = "com.loganlinn.pivotaltrackie.action.SYNC_STORIES";
	public static final String ACTION_SYNC_STORY = "com.loganlinn.pivotaltrackie.action.SYNC_STORY";
	public static final String ACTION_DELETE_STORY = "com.loganlinn.pivotaltrackie.action.DELETE_STORY";

	public static final String SEND_TO_API = "SendToAPI";
	private static final String PROJECTS_URL = "http://www.pivotaltracker.com/services/v3/projects";

	private RemoteExecutor mRemoteExecutor;

	public SyncService() {
		super(TAG);
	}

	@Override
	public void onCreate() {
		super.onCreate();

		final HttpClient httpClient = HttpUtils.getHttpClient(this);
		final ContentResolver resolver = getContentResolver();

		// localExecutor_ = new LocalExecutor(getResources(), resolver);
		mRemoteExecutor = new RemoteExecutor(httpClient, resolver);
	}

	private boolean connectionAvailable() {

		ConnectivityManager cm = ((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE));
		final boolean network = cm.getNetworkInfo(
				ConnectivityManager.TYPE_MOBILE).isAvailable();
		final boolean wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
				.isAvailable();

		return network | wifi | true;
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.d(TAG, "onHandleIntent(intent=" + intent.toString() + ")");
		final ResultReceiver receiver = intent
				.getParcelableExtra(EXTRA_STATUS_RECEIVER);
		if (receiver != null)
			receiver.send(STATUS_RUNNING, Bundle.EMPTY);
		final SharedPreferences prefs = getSharedPreferences(Prefs.NAME,
				Context.MODE_PRIVATE);
		final Context context = this;
		Bundle returnData = Bundle.EMPTY;
		
		if (connectionAvailable()) {

			try {
				final long startRemote = System.currentTimeMillis();
				final String action = intent.getAction();

				if (ACTION_SYNC_PROJECTS.equals(action)) {
					mRemoteExecutor.executeGet(PROJECTS_URL,
							new RemoteProjectsHandler(mRemoteExecutor));
				} else if (ACTION_SYNC_STORIES.equals(action)) {

					final String projectId = Projects.getProjectId(intent
							.getData());
					Log.i(TAG, "Fetching stories for project_id=" + projectId);
					mRemoteExecutor.executeGet(PivotalTracker
							.getStoriesUrl(projectId),
							new RemoteStoriesHandler(mRemoteExecutor));

				} else if (ACTION_SYNC_STORY.equals(action)) {
					final Bundle intentData = intent.getExtras();
					if (intentData != null) {

						getContentResolver().delete(Stories.CONTENT_URI, null,
								null);
						final RemoteStoriesHandler handler = new RemoteStoriesHandler(
								mRemoteExecutor);

						final int projectId = intent.getIntExtra(
								Stories.PROJECT_ID, -1);
						final int storyId = intent.getIntExtra(
								Stories.STORY_ID, -1);
						Log.i(TAG, "Syncing story with id = "
								+ String.valueOf(storyId));
						if (intentData.getBoolean(SEND_TO_API, false)) {
							final StoryEntry story = StoryEntry
									.fromBundle(intentData);

							if (story.get(Stories.STORY_ID) == String
									.valueOf(Stories.NEW_STORY)) { // add the
																	// story
								Log.i(TAG, "Adding the story");
								mRemoteExecutor.execute(PivotalTracker
										.getPostRequest(story), handler);
							} else { // update story
								Log.i(TAG, "Updating the story");
								mRemoteExecutor.execute(PivotalTracker
										.getPutRequest(story), handler);
							}
						} else if (storyId != -1 && projectId != -1) {
							handler.setContentUri(Stories
									.buildStoryUri(storyId));

							mRemoteExecutor.executeGet(PivotalTracker
									.getStoryUrl(projectId, storyId), handler);
						}
					}

				} else if (ACTION_DELETE_STORY.equals(action)) {

					final int projectId = intent.getIntExtra(
							Stories.PROJECT_ID, -5);
					final int storyId = intent.getIntExtra(Stories.STORY_ID,
							Stories.NEW_STORY);

					if (projectId > 0 && storyId > 0) {
						Log.i(TAG, "Deleting story " + storyId);
						mRemoteExecutor.execute(PivotalTracker
								.getDeleteStoryRequest(projectId, storyId),
								new XmlHandler(
										ProjectContract.CONTENT_AUTHORITY) {

									@Override
									public ArrayList<ContentProviderOperation> parse(
											XmlPullParser parser,
											ContentResolver resolver)
											throws XmlPullParserException,
											IOException {
										final ArrayList<ContentProviderOperation> batch = new ArrayList<ContentProviderOperation>();
										return batch;
									}

								});
					}

				}

				Log.d(TAG, "Remote sync took "
						+ (System.currentTimeMillis() - startRemote)
						+ "ms for " + action);

			} catch (HandlerException e) {

			} catch (Exception e) {

				Log.e(TAG, "Problem while syncing", e);

				if (receiver != null) {
					// Pass back error to surface listener
					final Bundle bundle = new Bundle();
					bundle.putString(Intent.EXTRA_TEXT, e.toString());
					receiver.send(STATUS_ERROR, bundle);
				} else {
					Log.d(TAG, "No receiver found");
				}
			}
		}else{
			//we do not have connection
			if(receiver != null){
				receiver.send(STATUS_ERROR, returnData);
			}
		}
		// Announce success to any surface listener
		Log.d(TAG, "sync finished");
		if (receiver != null)
			receiver.send(STATUS_FINISHED, returnData);
	}

	private void syncProjects() {

	}
}
