package com.loganlinn.pivotaltrackie.provider;

import java.util.Arrays;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.loganlinn.pivotaltracker.util.SelectionBuilder;
import com.loganlinn.pivotaltrackie.provider.ProjectContract.Members;
import com.loganlinn.pivotaltrackie.provider.ProjectContract.Projects;
import com.loganlinn.pivotaltrackie.provider.ProjectContract.Stories;
import com.loganlinn.pivotaltrackie.provider.ProjectDatabase.Tables;

public class ProjectProvider extends ContentProvider {
	private static final String TAG = "ProjectProvider";
	private static final boolean LOGV = Log.isLoggable(TAG, Log.VERBOSE);
	private static final String MIME_XML = "text/xml";
	private static final UriMatcher sUriMatcher = buildUriMatcher();

	// Uri values
	private static final int PROJECTS 		= 100;
	private static final int PROJECTS_ID 	= 101;
	private static final int STORIES 		= 200;
	private static final int STORIES_ID 	= 201;
	private static final int MEMBERS 		= 300;
	private static final int MEMBERS_ID 	= 301;

	private ProjectDatabase mOpenHelper;
	
	private static UriMatcher buildUriMatcher() {
		final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
		final String authority = ProjectContract.CONTENT_AUTHORITY;

		matcher.addURI(authority, "projects", PROJECTS);
		matcher.addURI(authority, "projects/*", PROJECTS_ID);

		matcher.addURI(authority, "stories", STORIES);
		matcher.addURI(authority, "stories/*", STORIES_ID);

		matcher.addURI(authority, "members", MEMBERS);
		matcher.addURI(authority, "members/*", MEMBERS_ID);

		return matcher;
	}

	@Override
	public String getType(Uri uri) {
		final int match = sUriMatcher.match(uri);
		switch (match) {
		case PROJECTS:
			return Projects.CONTENT_TYPE;
		case PROJECTS_ID:
			return Projects.CONTENT_ITEM_TYPE;
		case STORIES:
			return Stories.CONTENT_TYPE;
		case STORIES_ID:
			return Stories.CONTENT_ITEM_TYPE;
		case MEMBERS:
			return Members.CONTENT_TYPE;
		case MEMBERS_ID:
			return Members.CONTENT_ITEM_TYPE;
		default:
			throw new UnsupportedOperationException("Unknown uri: " + uri);
		}
	}

	@Override
	public boolean onCreate() {
		final Context context = getContext();
		mOpenHelper = new ProjectDatabase(context);
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		if (LOGV)
			Log.v(TAG, "query(uri=" + uri + ", proj="
					+ Arrays.toString(projection) + ")");
		final SQLiteDatabase db = mOpenHelper.getReadableDatabase();

		// final int match = uriMatcher_.match(uri);

		// switch(match){
		// default: {
		final SelectionBuilder builder = buildSelection(uri);
		return builder.where(selection, selectionArgs).query(db, projection,
				sortOrder);
		// }
		// }
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		if (LOGV)
			Log.v(TAG, "insert(uri=" + uri + ", values=" + values.toString()
					+ ")");
		final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		final int match = sUriMatcher.match(uri);

		switch (match) {
		case PROJECTS:
			db.insertOrThrow(Tables.PROJECTS, null, values);
			return Projects.buildProjectUri(values
					.getAsString(Projects.PROJECT_ID));
		case STORIES:
			db.insertOrThrow(Tables.STORIES, null, values);
			return Stories.buildStoryUri(values.getAsString(Stories.STORY_ID));
		case MEMBERS:
			db.insertOrThrow(Tables.MEMBERS, null, values);
			return Members
					.buildMemberUri(values.getAsString(Members.MEMBER_ID));
		default:
			throw new UnsupportedOperationException("Unknown uri: " + uri);
		}
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		if (LOGV)
			Log.v(TAG, "update(uri=" + uri + ", values=" + values.toString()
					+ ")");
		final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		final SelectionBuilder builder = buildSelection(uri);
		return builder.where(selection, selectionArgs).update(db, values);

	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		if (LOGV)
			Log.v(TAG, "delete(uri=" + uri + ")");
		final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		final SelectionBuilder builder = buildSelection(uri);
		return builder.where(selection, selectionArgs).delete(db);
	}

	private SelectionBuilder buildSelection(Uri uri) {
		final SelectionBuilder builder = new SelectionBuilder();
		final int match = sUriMatcher.match(uri);
		String projectId;
		String storyId;
		switch (match) {
		case PROJECTS:
			return builder.table(Tables.PROJECTS);
		case PROJECTS_ID:
			projectId = Projects.getProjectId(uri);
			return builder.table(Tables.PROJECTS).where(
					Projects.PROJECT_ID + "=?", projectId);
		case STORIES:
			projectId = Stories.getProjectId(uri);
			return builder.table(Tables.STORIES).where(
					Projects.PROJECT_ID + "=?", projectId);
		case STORIES_ID:
			projectId = Stories.getProjectId(uri);
			storyId = Stories.getStoryId(uri);
			return builder.table(Tables.STORIES).where(
					Stories.PROJECT_ID + "=?", projectId).where(
					Stories.STORY_ID + "=?", storyId);
		case MEMBERS:
			return builder.table(Tables.MEMBERS);
		case MEMBERS_ID:
			final String memberId = Members.getMemberId(uri);
			return builder.table(Tables.MEMBERS).where(
					Members.MEMBER_ID + "=?", memberId);
		default:
			throw new UnsupportedOperationException("Unknown uri: " + uri);
		}
	}

}
