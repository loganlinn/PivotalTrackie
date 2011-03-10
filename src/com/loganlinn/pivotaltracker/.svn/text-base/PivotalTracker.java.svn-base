package com.loganlinn.pivotaltracker;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;
import android.util.Log;

import com.loganlinn.pivotaltrackie.provider.ProjectContract.Stories;
import com.loganlinn.pivotaltrackie.ui.XMLTokenHandler;
import com.loganlinn.pivotaltrackie.util.HttpUtils;

public class PivotalTracker extends HttpUtils {
	private static final String TAG = "PivotalTrackerAPI";
	public static final String BASE_ADDRESS = "http://www.pivotaltracker.com";
	public static final String BASE_HTTPS_ADDRESS = "https://www.pivotaltracker.com";
	public static final String TOKEN_PATH = "";
	private static Context sContext;
	private static PivotalTracker sInstance = null;
	//
	private static String sToken = "408384a051c11410ce069a01c1cc665f";// REPLACE
	// WITH
	// YOUR
	// TOKEN
	// FOR
	// TESTING
	private static SimpleDateFormat sDateFormat = new SimpleDateFormat(
			"yyyy/MM/dd kk:mm:ss zzz"); // ex 2010/12/03 14:41:52 UTC
	private ContentResolver mContentResolver;

	public static String getStoriesUrl(int projectId) {
		return BASE_ADDRESS
				+ String.format("/services/v3/projects/%d/stories", projectId);
	}

	public static String getStoriesUrl(String projectId) {
		return BASE_ADDRESS
				+ String.format("/services/v3/projects/%s/stories", projectId);
	}

	public static String getStoryUrl(int projectId, int storyId) {
		return BASE_ADDRESS
				+ String.format("/services/v3/projects/%d/stories/%d",
						projectId, storyId);
	}

	public static String getStoryUrl(String projectId, String storyId) {
		return BASE_ADDRESS
				+ String.format("/services/v3/projects/%s/stories/%s",
						projectId, storyId);
	}

	public static String getIterationUrl(String projectId) {
		return BASE_ADDRESS
				+ String.format("/services/v3/projects/%s/iterations",
						projectId);
	}

	/**
	 * Forces the singleton instance to be recreated (if it exists) with the
	 * given context
	 * 
	 * @param ctx
	 * @return
	 */
	public static synchronized PivotalTracker newInstance(Context ctx) {
		sInstance = new PivotalTracker(ctx);
		return sInstance;
	}

	/**
	 * Get the singleton PivotalTracker instance If an instance already exists,
	 * create one with the passed context, otherwise the context is not used
	 * 
	 * @param ctx
	 * @return
	 */
	public static synchronized PivotalTracker getInstance(Context ctx) {
		Log.i(TAG, "Fetching PivotalTracker API");
		if (sInstance == null) {
			sInstance = new PivotalTracker(ctx);
		}

		return sInstance;
	}

	/**
	 * Private constructor for singleton pattern
	 * 
	 * @param context
	 */
	private PivotalTracker(Context context) {
		sContext = context;
		sInstance = this;
		mContentResolver = context.getContentResolver();
	}

	public static String getTokenUrl() {
		return BASE_HTTPS_ADDRESS + TOKEN_PATH;
	}

	public void fetchToken(final String username, final String password,
			PivotalTrackerListener authenticateListener) {
		//
		// HttpClient client = getHttpClient(sContext);
		// URLConnection con = null;
		// try {
		// con = new URL(getTokenUrl()).openConnection();
		//
		// if (con instanceof HttpsURLConnection) {
		// Log.w(TAG, "fetchToken connection is Https");
		// // ((HttpsURLConnection) con)
		// // .setHostnameVerifier(new AllowAllHostnameVerifier());
		// }
		//
		// HttpsURLConnection httpsCon = (HttpsURLConnection) con;
		// httpsCon.setHostnameVerifier(new AllowAllHostnameVerifier());
		//
		// final byte[] credentials = ((String) username + ":" + password)
		// .getBytes();
		//
		// httpsCon.setRequestProperty("Authorization", "Basic "
		// + Base64.encodeToString(credentials, Base64.DEFAULT));
		// httpsCon.setAllowUserInteraction(false);
		// httpsCon.setInstanceFollowRedirects(true);
		// httpsCon.setRequestMethod("GET");
		//
		// Log.i(TAG, "Requesting URL: " + httpsCon.getURL());
		// httpsCon.connect();
		//
		// if (httpsCon.getResponseCode() == HttpsURLConnection.HTTP_OK) {
		// final String token = new XMLTokenHandler()
		// .getTokenAndClose(httpsCon.getInputStream());
		// Log.i(TAG, "Token = " + token);
		//
		// saveToken(token);
		//
		// authenticateListener.onSuccess(token);
		// } else {
		// authenticateListener.onError(new PivotalTrackerError(
		// "HttpsConnection responseCode="
		// + httpsCon.getResponseCode()));
		// }
		// } catch (MalformedURLException e) {
		// authenticateListener
		// .onError(new PivotalTrackerError(e.getMessage()));
		// e.printStackTrace();
		// return;
		// } catch (IOException e) {
		// authenticateListener
		// .onError(new PivotalTrackerError(e.getMessage()));
		// e.printStackTrace();
		// return;
		// }

		/**
		 * TODO: FIX HTTPS SUPPORT! This throws javax.net.ssl.SSLException: Not
		 * trusted server certificate!
		 */
		HttpParams parameters = new BasicHttpParams();
		SchemeRegistry schemeRegistry = new SchemeRegistry();
		SSLSocketFactory sslSocketFactory = SSLSocketFactory.getSocketFactory();
		sslSocketFactory
				.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
		schemeRegistry.register(new Scheme("https", sslSocketFactory, 443));
		ClientConnectionManager manager = new ThreadSafeClientConnManager(
				parameters, schemeRegistry);
		HttpClient httpClient = new DefaultHttpClient(manager, parameters);

		// DefaultHttpClient client = HttpUtils.getHttpClient(sContext);

		HttpGet request = new HttpGet(this.getTokenUrl());
		final byte[] credentials = ((String) username + ":" + password)
				.getBytes();
		request.setHeader("Authorization", "Basic "
				+ Base64.encodeToString(credentials, Base64.DEFAULT));
		try {

			HttpResponse response = httpClient.execute(request);

			final String token = new XMLTokenHandler()
					.getTokenAndClose(response.getEntity().getContent());
			Log.i(TAG, "Token: " + token);

			// saveToken(token);

			authenticateListener.onSuccess(token);
		} catch (ClientProtocolException e) {
			authenticateListener
					.onError(new PivotalTrackerError(e.getMessage()));
			e.printStackTrace();
		} catch (IOException e) {
			authenticateListener
					.onError(new PivotalTrackerError(e.getMessage()));
			e.printStackTrace();
		}

	}

	public static String getToken(Context context) {
		sContext = context;
		return getToken();
	}

	public static String getToken() {
		if (sToken == null) {
			Log.i(TAG, "Getting token from SavedPreferences");

			final SharedPreferences prefs = getPreferences();
			sToken = prefs.getString(Prefs.TOKEN, null);
		}

		return sToken;
	}

	public static HttpUriRequest getGetRequest(String url) {
		final HttpUriRequest request = new HttpGet(url);
		request.addHeader("X-TrackerToken", getToken());
		return request;
	}

	public static HttpUriRequest getPutRequest(String url) {
		final HttpUriRequest request = new HttpPut(url);
		request.addHeader("X-TrackerToken", getToken());
		return request;
	}

	public static HttpUriRequest getPostRequest(String url) {
		final HttpUriRequest request = new HttpPost(url);
		request.addHeader("X-TrackerToken", getToken());
		return request;
	}

	public static HttpUriRequest getDeleteRequest(String url) {
		final HttpUriRequest request = new HttpDelete(url);
		request.addHeader("X-TrackerToken", getToken());
		return request;
	}

	public static HttpUriRequest getPostRequest(StoryEntry story) {
		final String projectId = story.get(Stories.PROJECT_ID);
		Log.i(TAG, "Creating new story for project " + projectId);

		final HttpPost request = (HttpPost) getPostRequest(getStoriesUrl(projectId));
		try {
			final StringEntity entity = new StringEntity(story.toXml());

			entity.setContentType("text/xml");

			request.setHeader("Content-Type", "application/xml");
			request.setEntity(entity);

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return request;
	}

	public static HttpUriRequest getPutRequest(StoryEntry story) {
		final String projectId = story.get(Stories.PROJECT_ID);
		final String storyId = story.get(Stories.STORY_ID);

		final HttpPut request = (HttpPut) getPutRequest(getStoryUrl(projectId,
				storyId));
		try {
			final StringEntity entity = new StringEntity(story.toXml());

			entity.setContentType("text/xml");

			request.setHeader("Content-Type", "application/xml");
			request.setEntity(entity);

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return request;
	}

	public static HttpUriRequest getDeleteStoryRequest(int storyId,
			int projectId) {

		return (HttpDelete) getDeleteRequest(getStoryUrl(storyId, projectId));
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

	public static SimpleDateFormat getUTCDateFormat() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss 'UTC'");// simpledateformat
																				// is
																				// not
																				// thread
																				// safe
																				// --
																				// return
																				// a
																				// new
																				// instance
		df.setTimeZone(TimeZone.getTimeZone("UTC"));
		return df;
	}

	public static SimpleDateFormat getDateFormat() {
		return getUTCDateFormat(); // use the UTC format for now
	}
}
