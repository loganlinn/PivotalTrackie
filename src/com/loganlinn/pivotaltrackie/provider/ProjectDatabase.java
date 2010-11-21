package com.loganlinn.pivotaltrackie.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import com.loganlinn.pivotaltrackie.provider.ProjectContract.MembersColumns;
import com.loganlinn.pivotaltrackie.provider.ProjectContract.ProjectsColumns;
import com.loganlinn.pivotaltrackie.provider.ProjectContract.StoriesColumns;

public class ProjectDatabase extends SQLiteOpenHelper {
	private static final String TAG = "ProjectDatabase";

	private static final String DATABASE_NAME = "project.db";
	private static final int DATABASE_VERSION = 1;

	interface Tables {
		String PROJECTS = "projects";
		String STORIES = "stories";
		String MEMBERS = "members";
		String STORY_TASK = "story_task";
		String STORY_ATTACHMENT = "story_attachment";
		String PROJECT_INTEGRATIONS = "project_integrations";
		String PROJECT_LABELS = "project_labels";
		String STORY_FILTERS = "story_filters";

		// Joins
		/*
		 * Example: String SESSIONS_JOIN_BLOCKS_ROOMS = "sessions " +
		 * "LEFT OUTER JOIN blocks ON sessions.block_id=blocks.block_id " +
		 * "LEFT OUTER JOIN rooms ON sessions.room_id=rooms.room_id";
		 */
	}

	private interface References{
		String PROJECT_ID = "REFERENCES " + Tables.PROJECTS + "(" + ProjectsColumns.PROJECT_ID + ")";
	}
	
	public ProjectDatabase(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		/*
		 * Projects Table
		 */
		db.execSQL("CREATE TABLE " + Tables.PROJECTS + " ("
				+ BaseColumns._ID 						+ " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ ProjectsColumns.NAME 					+ " TEXT NOT NULL,"
				+ ProjectsColumns.ITERATION_LENGTH 		+ " INTEGER NOT NULL,"
				+ ProjectsColumns.WEEK_START_DATE 		+ " TEXT NOT NULL,"
				+ ProjectsColumns.POINT_SCALE 			+ " TEXT NOT NULL,"
				+ ProjectsColumns.ACCOUNT 				+ " TEXT NOT NULL,"
				+ ProjectsColumns.VELOCITY_SCHEME 		+ " TEXT NOT NULL,"
				+ ProjectsColumns.CURRENT_VELOCITY 		+ " INTEGER NOT NULL,"
				+ ProjectsColumns.INITIAL_VELOCITY 		+ " INTEGER NOT NULL,"
				+ ProjectsColumns.NUMBER_OF_DONE_ITERATIONS_TO_SHOW + " INTEGER NOT NULL,"
				+ ProjectsColumns.ALLOW_ATTACHMENTS 	+ " INTEGER NOT NULL,"
				+ ProjectsColumns.PUBLIC				+ " INTEGER NOT NULL,"
				+ ProjectsColumns.USE_HTTPS				+ " INTEGER NOT NULL,"
				+ ProjectsColumns.BUGS_AND_CHORES_ARE_ESTIMATABLE + " INTEGER NOT NULL,"
				+ ProjectsColumns.COMMIT_MODE			+ " INTEGER NOT NULL,"
				+ ProjectsColumns.LAST_ACTIVITY_AT 		+ " TEXT NOT NULL,"
				+ ProjectsColumns.LABELS		 		+ " TEXT,"
				+ "UNIQUE (" + ProjectsColumns.PROJECT_ID + ") ON CONFLICT REPLACE)");
		
		/*
		 * Stories Table
		 */
		db.execSQL("CREATE TABLE " + Tables.STORIES + " ("
				+ BaseColumns._ID 				+ " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ StoriesColumns.PROJECT_ID 	+ " INTEGER NOT NULL "+ References.PROJECT_ID +","
				+ StoriesColumns.URL 			+ " TEXT NOT NULL,"
				+ StoriesColumns.ESTIMATE 		+ " INTEGER NOT NULL,"
				+ StoriesColumns.CURRENT_STATE 	+ " TEXT NOT NULL,"
				+ StoriesColumns.DESCRIPTION 	+ " TEXT NOT NULL,"
				+ StoriesColumns.NAME 			+ " TEXT NOT NULL,"
				+ StoriesColumns.REQUESTED_BY 	+ " TEXT NOT NULL,"
				+ StoriesColumns.OWNED_BY 		+ " TEXT NOT NULL,"
				+ StoriesColumns.CREATED 		+ " TEXT NOT NULL,"
				+ StoriesColumns.ACCEPTED 		+ " TEXT NOT NULL,"
				+ StoriesColumns.LABELS 		+ " TEXT,"
				+ "UNIQUE (" + StoriesColumns.STORY_ID + ") ON CONFLICT REPLACE)");
		
		/*
		 * Members Table
		 */
		db.execSQL("CREATE TABLE " + Tables.MEMBERS + " ("
				+ BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ MembersColumns.PROJECT_ID + "INTEGER NOT NULL "+ References.PROJECT_ID +","
				+ MembersColumns.EMAIL + " TEXT NOT NULL,"
				+ MembersColumns.NAME + " TEXT NOT NULL,"
				+ MembersColumns.INITIALS + " TEXT NOT NULL,"
				+ MembersColumns.ROLE + " TEXT,"
				+ "UNIQUE (" + MembersColumns.MEMBER_ID + ") ON CONFLICT REPLACE)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

}
