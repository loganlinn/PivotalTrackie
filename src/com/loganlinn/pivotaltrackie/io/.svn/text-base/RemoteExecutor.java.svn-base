/*
 * Copyright 2010 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.loganlinn.pivotaltrackie.io;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.ContentResolver;
import android.content.Context;
import android.net.ConnectivityManager;
import android.util.Log;

import com.loganlinn.pivotaltracker.PivotalTracker;
import com.loganlinn.pivotaltrackie.io.XmlHandler.HandlerException;
import com.loganlinn.pivotaltrackie.util.ParserUtils;

/**
 * Executes an {@link HttpUriRequest} and passes the result as an
 * {@link XmlPullParser} to the given {@link XmlHandler}.
 */
public class RemoteExecutor {
	private static final String TAG = "RemoteExecutor";

	private final HttpClient mHttpClient;
	private final ContentResolver mResolver;

	public RemoteExecutor(HttpClient httpClient, ContentResolver resolver) {
		mHttpClient = httpClient;
		mResolver = resolver;
	}

	/**
	 * Execute a {@link HttpGet} request, passing a valid response through
	 * {@link XmlHandler#parseAndApply(XmlPullParser, ContentResolver)}.
	 * @throws ResponseException 
	 */
	public void executeGet(String url, XmlHandler handler)
			throws HandlerException {

		execute(PivotalTracker.getGetRequest(url), handler); // get a
		// HttpRequest
		// object with
		// the token in
		// header
	}

	public void executePut(String url, XmlHandler handler)
			throws HandlerException {

		execute(PivotalTracker.getPutRequest(url), handler);
	}

	public void executePost(String url, XmlHandler handler)
			throws HandlerException {

		execute(PivotalTracker.getPostRequest(url), handler);
	}

	/**
	 * Execute this {@link HttpUriRequest}, passing a valid response through
	 * {@link XmlHandler#parseAndApply(XmlPullParser, ContentResolver)}.
	 * @throws ResponseException 
	 */
	public void execute(HttpUriRequest request, XmlHandler handler)
			throws HandlerException {
		Log.i(TAG, "Executing Remote "+request.getMethod()+" Request: " + request.getURI());

		try {
			// Execute the HttpRequest
			final HttpResponse resp = mHttpClient.execute(request);
			final int status = resp.getStatusLine().getStatusCode();
			Log.i(TAG, "Request Response: " + status);
			
			final boolean notFound = (status != HttpStatus.SC_NOT_FOUND); 
			
			if (status != HttpStatus.SC_OK && !notFound) {
				throw new HandlerException("Unexpected server response "
						+ resp.getStatusLine() + " for "
						+ request.getRequestLine());
			}
			

			
			if(notFound){
				final InputStream input = resp.getEntity().getContent();
				// Parse the input stream using the handler
				try {
	
					final XmlPullParser parser = ParserUtils.newPullParser(input);
					handler.parseAndApply(parser, mResolver);
	
				} catch (XmlPullParserException e) {
					throw new HandlerException("Malformed response for "
							+ request.getRequestLine(), e);
				} finally {
					if (input != null)
						input.close();
				}
			}else if(handler.getContentUri() != null){
				Log.i(TAG, "Deleting "+handler.getContentUri().toString());
				mResolver.delete(handler.getContentUri(), null, null);
			}
		} catch (HandlerException e) {
			throw e;
		} catch (IOException e) {
			throw new HandlerException("Problem reading remote response for "
					+ request.getRequestLine(), e);
		}
	}

}