package com.loganlinn.pivotaltracker;

import static org.xmlpull.v1.XmlPullParser.END_DOCUMENT;
import static org.xmlpull.v1.XmlPullParser.END_TAG;
import static org.xmlpull.v1.XmlPullParser.START_TAG;
import static org.xmlpull.v1.XmlPullParser.TEXT;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;

import com.loganlinn.pivotaltracker.Story.DefaultStoriesQuery;
import com.loganlinn.pivotaltrackie.provider.ProjectContract.Stories;
import com.loganlinn.pivotaltrackie.provider.ProjectContract.SyncColumns;

public class StoryEntry extends HashMap<String, String> {
	private static final String TAG = "StoryEntry";
	
	public static final String[] FIELDS = {
		Stories.STORY_ID,
		StoryTags.PROJECT_ID,
		StoryTags.STORY_TYPE, 
		StoryTags.URL,
		StoryTags.ESTIMATE,
		StoryTags.CURRENT_STATE,
		StoryTags.DESCRIPTION,
		StoryTags.NAME,
		StoryTags.REQUESTED_BY,
		StoryTags.OWNED_BY,
		StoryTags.CREATED_AT,
		StoryTags.ACCEPTED_AT,
		StoryTags.LABELS
		//TODO: Allow add-in fields for integrations
	};
	
	public static final String[] SAVEABLE_FIELDS = {
		StoryTags.ESTIMATE,
		StoryTags.DESCRIPTION,
		StoryTags.NAME,
		StoryTags.REQUESTED_BY,
		StoryTags.OWNED_BY,
		StoryTags.LABELS
	};
	
	public interface StoryTags {
		String STORY			= "story";
		
		String STORY_ID 		= "id";
		String PROJECT_ID 		= "project_id";
		String STORY_TYPE 		= "story_type";
		String URL 				= "url";
		String ESTIMATE 		= "estimate";
		String CURRENT_STATE 	= "current_state";
		String DESCRIPTION 		= "description";
		String NAME 			= "name";
		String REQUESTED_BY 	= "requested_by";
		String OWNED_BY 		= "owned_by";
		String CREATED_AT 		= "created_at";
		String ACCEPTED_AT 		= "accepted_at";
		String LABELS 			= "labels";
		String ATTACHMENTS 		= "attachments";
	}
	
	public StoryEntry(){
		for(String f : FIELDS){
			put(f,null);
		}
	}

	public void sanitizeNullValues(){
		for(String k: keySet()){
			if(get(k) == null){
				put(k,"");
			}
		}
	}
	public static StoryEntry fromCursor(Cursor cursor){
		return fromCursor(cursor, DefaultStoriesQuery.PROJECTION);
	}
	
	
	public static StoryEntry fromCursor(Cursor cursor, String[] projection){
		final StoryEntry entry = new StoryEntry(); 
		final int len = projection.length;

		for(int i = 0; i < len; i++){
			entry.put(projection[i], cursor.getString(i));
		}
		
		return entry;
	}
	
	public static StoryEntry fromParser(XmlPullParser parser)
			throws XmlPullParserException, IOException {
		final int depth = parser.getDepth();
		final StoryEntry entry = new StoryEntry();
		
		String tag = null;
		int type;
		
		while (((type = parser.next()) != END_TAG || parser.getDepth() > depth)
				&& type != END_DOCUMENT) {
			if (type == START_TAG) {
				tag = parser.getName();
			} else if (type == END_TAG) {
				tag = null;
			} else if (type == TEXT) {
				if(tag != null){
					String text = parser.getText();
					if(StoryTags.STORY_ID.equals(tag)){
						entry.put(Stories.STORY_ID, text);//store as story_id not id
					}else if(entry.containsKey(tag)){
						entry.put(tag, text);
					}
				}
			}
		}

		entry.put(SyncColumns.UPDATED, String.valueOf(System.currentTimeMillis()));
		
		return entry;
	}

	public static StoryEntry fromBundle(Bundle data, String[] projection) {
		final StoryEntry entry = new StoryEntry();
		for(String f: projection){
			if(Stories.STORY_ID.equals(f) || Stories.PROJECT_ID.equals(f)){
				final Object storyId = data.get(f);
				
				if(storyId instanceof String){
					entry.put(f, (String) storyId);
				}else{
					entry.put(f, String.valueOf(storyId));
				}
			}else{
				entry.put(f, data.getString(f));
			}
		}
		return entry;
	}
	
	public static StoryEntry fromBundle(Bundle data){
		return fromBundle(data, Story.DefaultStoriesQuery.PROJECTION);
	}

	public Bundle toBundle() {
		final Bundle bundle = new Bundle();
		for(String f: FIELDS){
			bundle.putString(f, get(f));
		}
		return bundle;
	}
	
	public String toXml(){
		final StringBuilder builder = new StringBuilder();
		Log.i(TAG, this.toString());
		
		builder.append("<story>");
		for(String k: SAVEABLE_FIELDS){
			final String v = get(k);
			if(v != null && !v.equals("")){
				builder.append("<"+k+">");
				builder.append(v);
				builder.append("</"+k+">");
			}
		}
		builder.append("</story>");
		Log.i(TAG, builder.toString());
		
		return builder.toString();
	}
	
	public String toString(){
		final StringBuilder builder = new StringBuilder();
		builder.append("{");
		
		for(String k : keySet()){
			builder.append(k+":"+get(k));
			builder.append(", ");
		}
		
		builder.append("}");
		
		return builder.toString();
	}
}

