package com.loganlinn.pivotaltracker;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.net.ssl.HttpsURLConnection;

import org.apache.http.conn.ssl.AllowAllHostnameVerifier;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;
import android.util.Log;

import com.loganlinn.pivotaltracker.util.HttpUtils;
import com.loganlinn.pivotaltracker.xml.XMLTokenHandler;

public class PivotalTracker extends HttpUtils implements PivotalTrackerAPI {
	private static final String TAG = "PivotalTrackerAPI";

	private static Context sContext;

	public PivotalTracker(Context context) {
		sContext = context;
	}

	public static String getTokenUrl() {
		return BASE_HTTPS_ADDRESS + TOKEN_PATH;
	}

	@Override
	public void addStory(Story story) {

	}

	@Override
	public void addTask(StoryTask task) {

	}

	@Override
	public void deleteStory(int storyId) {

	}

	@Override
	public void deleteTask(int taskId) {

	}

	@Override
	public void fetchToken(final String username, final String password,
			AuthenticateListener authenticateListener) {
		// TODO: cleanup connection logic
		// HttpClient client = getHttpClient(context_);
		URLConnection con = null;
		try {
			con = new URL(getTokenUrl()).openConnection();

			if (con instanceof HttpsURLConnection) {
				Log.w(TAG, "fetchToken connection is Https");
				((HttpsURLConnection) con)
						.setHostnameVerifier(new AllowAllHostnameVerifier());
			}

			HttpsURLConnection httpsCon = (HttpsURLConnection) con;
			final byte[] credentials = ((String) username + ":" + password)
					.getBytes();

			httpsCon.setRequestProperty("Authorization", "Basic "
					+ Base64.encodeToString(credentials, Base64.DEFAULT));
			httpsCon.connect();

			if (httpsCon.getResponseCode() == HttpsURLConnection.HTTP_OK) {
				final String token = new XMLTokenHandler()
						.getTokenAndClose(httpsCon.getInputStream());
				Log.i(TAG, "Token = " + token);

				saveToken(token);

				authenticateListener.onAuthenticateSuccess(token);
			} else {
				authenticateListener.onAuthenticateError(new AuthenticateError(
						"HttpsConnection responseCode="
								+ httpsCon.getResponseCode()));
			}
		} catch (MalformedURLException e) {
			authenticateListener.onAuthenticateError(new AuthenticateError(e
					.getMessage()));
			e.printStackTrace();
			return;
		} catch (IOException e) {
			authenticateListener.onAuthenticateError(new AuthenticateError(e
					.getMessage()));
			e.printStackTrace();
			return;
		}

	}

	@Override
	public void getActivityFeed() {
		// TODO Auto-generated method stub

	}

	@Override
	public void getActivityFeed(int projectId) {
		// TODO Auto-generated method stub

	}

	@Override
	public void getAttachment(int attachmentId) {
		// TODO Auto-generated method stub

	}

	@Override
	public void getIterations(int offset, int limit) {
		// TODO Auto-generated method stub

	}

	@Override
	public void getIterations(int offset) {
		// TODO Auto-generated method stub

	}

	@Override
	public void getIterations() {
		// TODO Auto-generated method stub

	}

	@Override
	public void getProject(int id) {
		// TODO Auto-generated method stub

	}

	@Override
	public void getProjects() {
		// TODO Auto-generated method stub

	}

	@Override
	public void getStories() {
		// TODO Auto-generated method stub

	}

	@Override
	public void getStories(String filter) {
		// TODO Auto-generated method stub

	}

	@Override
	public void getStories(int offset, int limit) {
		// TODO Auto-generated method stub

	}

	@Override
	public void getStory(int storyId) {
		// TODO Auto-generated method stub

	}

	@Override
	public void getTasks() {
		// TODO Auto-generated method stub

	}

	@Override
	public void getTasks(int storyId) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getToken() {
		final SharedPreferences prefs = getPreferences();
		return prefs.getString(Prefs.TOKEN, null);
	}

	@Override
	public void moveStory(int sourceStoryId, int targetStoryId, int order) {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateStory(Story story) {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateTask(StoryTask task) {
		// TODO Auto-generated method stub

	}

	private void saveToken(String token) {
		final SharedPreferences prefs = getPreferences();
		prefs.edit().putString(Prefs.TOKEN, token);
	}

	public static SharedPreferences getPreferences() {
		return sContext.getSharedPreferences(Prefs.NAME, Context.MODE_PRIVATE);
	}

	public interface Prefs {
		String NAME = "pivotaltracker";
		String TOKEN = "token";
		String ID = "id";
	}
}
