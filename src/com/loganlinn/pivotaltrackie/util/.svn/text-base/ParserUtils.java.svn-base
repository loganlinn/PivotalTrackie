package com.loganlinn.pivotaltrackie.util;

import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;

import com.loganlinn.pivotaltrackie.provider.ProjectContract;
import com.loganlinn.pivotaltrackie.provider.ProjectContract.SyncColumns;

public class ParserUtils {
	
	
	private static XmlPullParserFactory factory_;
	
	public static XmlPullParser newPullParser(InputStream input) throws XmlPullParserException {
		if(factory_ == null){
			factory_ = XmlPullParserFactory.newInstance();
		}
		final XmlPullParser parser = factory_.newPullParser();
		parser.setInput(input, null);
		return parser;
	}
	
    /**
     * Query and return the {@link SyncColumns#UPDATED} time for the requested
     * {@link Uri}. Expects the {@link Uri} to reference a single item.
     */
    public static long queryItemUpdated(Uri uri, ContentResolver resolver) {
        final String[] projection = { SyncColumns.UPDATED };
        final Cursor cursor = resolver.query(uri, projection, null, null, null);
        try {
            if (cursor.moveToFirst()) {
                return cursor.getLong(0);
            } else {
                return ProjectContract.UPDATED_NEVER;
            }
        } finally {
            cursor.close();
        }
    }
}
