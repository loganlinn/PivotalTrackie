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

import com.loganlinn.pivotaltracker.ProjectEntry;
import com.loganlinn.pivotaltracker.ProjectEntry.ProjectTags;
import com.loganlinn.pivotaltrackie.provider.ProjectContract;
import com.loganlinn.pivotaltrackie.provider.ProjectContract.Projects;
import com.loganlinn.pivotaltrackie.provider.ProjectContract.SyncColumns;
import com.loganlinn.pivotaltrackie.util.Lists;
import com.loganlinn.pivotaltrackie.util.ParserUtils;

public class RemoteProjectsHandler extends XmlHandler {
	private static final String TAG = "RemoteProjectsHandler";

	private RemoteExecutor mExecutor;

	public RemoteProjectsHandler(RemoteExecutor executor) {
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
					&& ProjectEntry.ProjectTags.PROJECT
							.equals(parser.getName())) {
				final ProjectEntry entry = ProjectEntry.fromParser(parser);
				Log.d(TAG, "found project " + entry.toString());
				final String projectId = entry.get(ProjectTags.ID);
				final Uri projectUri = Projects.buildProjectUri(projectId);
				
				final long localUpdated = ParserUtils.queryItemUpdated(projectUri, resolver);
				final long serverUpdated = entry.getUpdated();
                Log.i(TAG, "found localUpdated=" + localUpdated + ", server=" + serverUpdated);
				//if (localUpdated >= serverUpdated) continue;
				Log.i(TAG, "Updating project with id= "+projectId);
				batch.add(ContentProviderOperation.newDelete(projectUri).build());
				
				final ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(Projects.CONTENT_URI);
				builder.withValue(SyncColumns.UPDATED, serverUpdated);
				builder.withValue(Projects.PROJECT_ID, entry.get(ProjectTags.ID));
				builder.withValue(Projects.NAME, entry.get(ProjectTags.NAME));
				builder.withValue(Projects.ITERATION_LENGTH, entry.get(ProjectTags.ITERATION_LENGTH));
				builder.withValue(Projects.WEEK_START_DAY, entry.get(ProjectTags.WEEK_START_DAY));
				builder.withValue(Projects.POINT_SCALE, entry.get(ProjectTags.POINT_SCALE));
				builder.withValue(Projects.ACCOUNT, entry.get(ProjectTags.ACCOUNT));
				builder.withValue(Projects.VELOCITY_SCHEME, entry.get(ProjectTags.VELOCITY_SCHEME));
				builder.withValue(Projects.CURRENT_VELOCITY, entry.get(ProjectTags.CURRENT_VELOCITY));
				builder.withValue(Projects.INITIAL_VELOCITY, entry.get(ProjectTags.INITIAL_VELOCITY));
				builder.withValue(Projects.NUMBER_OF_DONE_ITERATIONS_TO_SHOW, entry.get(ProjectTags.NUMBER_OF_DONE_ITERATIONS_TO_SHOW));
				builder.withValue(Projects.ALLOW_ATTACHMENTS, entry.get(ProjectTags.ALLOW_ATTACHMENTS));
				builder.withValue(Projects.PUBLIC, entry.get(ProjectTags.USE_HTTPS));
				builder.withValue(Projects.USE_HTTPS, entry.get(ProjectTags.USE_HTTPS));
				builder.withValue(Projects.BUGS_AND_CHORES_ARE_ESTIMATABLE, entry.get(ProjectTags.BUGS_AND_CHORES_ARE_ESTIMATABLE));
				builder.withValue(Projects.COMMIT_MODE, entry.get(ProjectTags.COMMIT_MODE));
				builder.withValue(Projects.LAST_ACTIVITY_AT, entry.get(ProjectTags.LAST_ACTIVITY_AT));
				builder.withValue(Projects.LABELS, entry.get(ProjectTags.LABELS));
				batch.add(builder.build());
			}
		}

		return batch;
	}
	
//	private ContentValues queryProjectDetails(Uri uri, ContentResolver resolver){
//		final ContentValues values = new ContentValues();
//        final Cursor cursor = resolver.query(uri, ProjectsQuery.PROJECTION, null, null, null);
//        try {
//            if (cursor.moveToFirst()) {
//                values.put(SyncColumns.UPDATED, cursor.getLong(Projects.UPDATED));
//                values.put(Vendors.STARRED, cursor.getInt(VendorsQuery.STARRED));
//            } else {
//                values.put(SyncColumns.UPDATED, ScheduleContract.UPDATED_NEVER);
//            }
//        } finally {
//            cursor.close();
//        }
//        return values;
//	}


}
