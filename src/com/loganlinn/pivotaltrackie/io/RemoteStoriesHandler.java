package com.loganlinn.pivotaltrackie.io;

import static org.xmlpull.v1.XmlPullParser.END_DOCUMENT;
import static org.xmlpull.v1.XmlPullParser.START_TAG;

import java.io.IOException;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.net.Uri;
import android.util.Log;

import com.loganlinn.pivotaltracker.StoryEntry;
import com.loganlinn.pivotaltracker.StoryEntry.StoryTags;
import com.loganlinn.pivotaltrackie.provider.ProjectContract;
import com.loganlinn.pivotaltrackie.provider.ProjectContract.Stories;
import com.loganlinn.pivotaltrackie.provider.ProjectContract.SyncColumns;
import com.loganlinn.pivotaltrackie.util.Lists;
import com.loganlinn.pivotaltrackie.util.ParserUtils;

public class RemoteStoriesHandler extends XmlHandler {
	private static final String TAG = "RemoteStoriesHandler";

	private RemoteExecutor mExecutor;
	public Uri mContentUri = null;
	
	public RemoteStoriesHandler(RemoteExecutor executor) {
		super(ProjectContract.CONTENT_AUTHORITY);
		mExecutor = executor;
	}

	@Override
	public ArrayList<ContentProviderOperation> parse(XmlPullParser parser,
			ContentResolver resolver) throws XmlPullParserException,
			IOException {
		final ArrayList<ContentProviderOperation> batch = Lists.newArrayList();
		
		int type;
		while ((type = parser.next()) != END_DOCUMENT) {
			if (type == START_TAG
					&& StoryEntry.StoryTags.STORY.equals(parser.getName())) {
				final StoryEntry entry = StoryEntry.fromParser(parser);

				final String storyId = entry.get(Stories.STORY_ID);
				// final String projectId = entry.get(StoryTags.PROJECT_ID);

				final Uri storyUri = Stories.buildStoryUri(storyId);
				final long localUpdated = ParserUtils.queryItemUpdated(
						storyUri, resolver);

				// TODO: Determine if story needs to be updated

				if (localUpdated > 0) {
					Log.i(TAG, "Updating story with id= " + storyId);
					batch.add(ContentProviderOperation.newDelete(storyUri)
							.build());
				} else {
					Log.i(TAG, "Inserting story with id= " + storyId);
				}

				final ContentProviderOperation.Builder builder = ContentProviderOperation
						.newInsert(Stories.CONTENT_URI);

				builder.withValue(SyncColumns.UPDATED, entry
						.get(SyncColumns.UPDATED));

				for (String f : entry.keySet()) {
					builder.withValue(f, entry.get(f));
				}

				batch.add(builder.build());
			}
		}
		//TODO: delete removed stories from local store
		return batch;
	}
}
