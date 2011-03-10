package com.loganlinn.pivotaltrackie.io;

import java.io.IOException;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.loganlinn.pivotaltrackie.provider.ProjectContract;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;

public class RemoteIterationsHandler extends XmlHandler {
	private static final String TAG = "RemoteIterationsHandler";

	private RemoteExecutor mExecutor;

	public RemoteIterationsHandler(RemoteExecutor executor) {
		super(ProjectContract.CONTENT_AUTHORITY);
		mExecutor = executor;
	}
	@Override
	public ArrayList<ContentProviderOperation> parse(XmlPullParser parser,
			ContentResolver resolver) throws XmlPullParserException,
			IOException {
		// TODO Auto-generated method stub
		return null;
	}

}
