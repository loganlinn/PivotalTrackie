package com.loganlinn.pivotaltrackie.util;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.HttpRequest;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class PivotalTrackerToken {
	private static final String TAG = "PivotalTrackerToken";
	public static final String PREFS = "pivotaltracker_auth";
	public static final String TOKEN = "token";
	private static String sToken = null;
	
	private static final SharedPreferences getPrefs(Context ctx){
		return ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
	}
	
	public static String getToken(Context ctx){
		if(sToken == null){
			
			final SharedPreferences prefs = getPrefs(ctx);
			sToken = prefs.getString(TOKEN, null);

		}
		
		return sToken;
	}
	
	public static void clearToken(Context ctx){
		final SharedPreferences prefs = getPrefs(ctx);
		prefs.edit().remove(TOKEN);
	}
	
	private static String fetchToken(Context ctx, String username, String password){
		Log.i(TAG, "Fetching PT token for username="+username);
		String token = null;
		
		DefaultHttpClient client = new DefaultHttpClient();
		HttpRequest req = new HttpGet();
		
		
		
		getPrefs(ctx).edit().putString(TOKEN, token);
		sToken = token;
		return sToken;
	}
	

	private PivotalTrackerToken(){
		
	}
	
	
}
