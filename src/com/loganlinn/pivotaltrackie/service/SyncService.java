package com.loganlinn.pivotaltrackie.service;

import org.apache.http.client.HttpClient;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import com.loganlinn.pivotaltracker.PivotalTracker.Prefs;
import com.loganlinn.pivotaltracker.util.HttpUtils;
import com.loganlinn.pivotaltrackie.io.RemoteExecutor;
import com.loganlinn.pivotaltrackie.io.RemoteProjectsHandler;

public class SyncService extends IntentService {
	private static final String TAG = "SyncService";

	public static final String EXTRA_STATUS_RECEIVER = "com.loganlinn.pivotaltrackie.extra.STATUS_RECEIVER";

	public static final int STATUS_RUNNING = 0x1;
	public static final int STATUS_ERROR = 0x2;
	public static final int STATUS_FINISHED = 0x3;



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

        //localExecutor_ = new LocalExecutor(getResources(), resolver);
        mRemoteExecutor = new RemoteExecutor(httpClient, resolver);
    }
    
	@Override
	protected void onHandleIntent(Intent intent) {
		Log.d(TAG, "onHandleIntent(intent=" + intent.toString() + ")");
		final ResultReceiver receiver = intent
				.getParcelableExtra(EXTRA_STATUS_RECEIVER);
		if (receiver != null)
			receiver.send(STATUS_RUNNING, Bundle.EMPTY);
		final SharedPreferences prefs = getSharedPreferences(Prefs.NAME, Context.MODE_PRIVATE);
		final Context context = this;

		// TODO: Spawn executors
		try {
			final long startRemote = System.currentTimeMillis();
			
			mRemoteExecutor.executeGet(PROJECTS_URL, new RemoteProjectsHandler(mRemoteExecutor));
			
			Log.d(TAG, "remote sync took " + (System.currentTimeMillis() - startRemote) + "ms");
		} catch (Exception e) {
			Log.e(TAG, "Problem while syncing", e);

			if (receiver != null) {
				// Pass back error to surface listener
				final Bundle bundle = new Bundle();
				bundle.putString(Intent.EXTRA_TEXT, e.toString());
				receiver.send(STATUS_ERROR, bundle);
			}
		}
		
		// Announce success to any surface listener
        Log.d(TAG, "sync finished");
        if (receiver != null) receiver.send(STATUS_FINISHED, Bundle.EMPTY);
	}

}
