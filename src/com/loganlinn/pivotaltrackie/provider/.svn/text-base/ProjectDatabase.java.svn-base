package com.loganlinn.pivotaltrackie.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import com.loganlinn.pivotaltrackie.provider.ProjectContract.IterationColumns;
import com.loganlinn.pivotaltrackie.provider.ProjectContract.MembersColumns;
import com.loganlinn.pivotaltrackie.provider.ProjectContract.ProjectsColumns;
import com.loganlinn.pivotaltrackie.provider.ProjectContract.StoriesColumns;
import com.loganlinn.pivotaltrackie.provider.ProjectContract.SyncColumns;

public class ProjectDatabase extends SQLiteOpenHelper {
	private static final String TAG = "ProjectDatabase";

	private static final String DATABASE_NAME = "project.db";
	private static final int DATABASE_VERSION = 14;

	interface Tables {
		String PROJECTS = "projects";
		String STORIES = "stories";
		String MEMBERS = "members";
		String STORY_TASK = "story_task";
		String STORY_ATTACHMENT = "story_attachment";
		String PROJECT_INTEGRATIONS = "project_integrations";
		String PROJECT_LABELS = "project_labels";
		String STORY_FILTERS = "story_filters";
		String ITERATIONS = "iterations";
		
		// Joins
		/*
		 * Example: String SESSIONS_JOIN_BLOCKS_ROOMS = "sessions " +
		 * "LEFT OUTER JOIN blocks ON sessions.block_id=blocks.block_id " +
		 * "LEFT OUTER JOIN rooms ON sessions.room_id=rooms.room_id";
		 */
	}

	private interface References{
		String PROJECT_ID = "REFERENCES " + Tables.PROJECTS + "(" + ProjectsColumns.PROJECT_ID + ")";
		String ITERATION_ID = "REFERENCES " + Tables.ITERATIONS + "("+ IterationColumns.ITERATION_ID + ")";
	}
	
	public ProjectDatabase(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.i(TAG, "Creating database tables");
		/*
		 * Projects Table
		 */
		db.execSQL("CREATE TABLE " + Tables.PROJECTS + " ("
				+ BaseColumns._ID 						+ " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ ProjectsColumns.PROJECT_ID			+ " INTEGER NOT NULL,"
				+ ProjectsColumns.NAME 					+ " TEXT NOT NULL,"
				+ ProjectsColumns.ITERATION_LENGTH 		+ " INTEGER NOT NULL,"
				+ ProjectsColumns.WEEK_START_DAY 		+ " TEXT NOT NULL,"
				+ ProjectsColumns.POINT_SCALE 			+ " TEXT NOT NULL,"
				+ ProjectsColumns.ACCOUNT 				+ " TEXT NOT NULL,"
				+ ProjectsColumns.VELOCITY_SCHEME 		+ " TEXT NOT NULL,"
				+ ProjectsColumns.CURRENT_VELOCITY 		+ " INTEGER NOT NULL,"
				+ ProjectsColumns.INITIAL_VELOCITY 		+ " INTEGER NOT NULL,"
				+ ProjectsColumns.NUMBER_OF_DONE_ITERATIONS_TO_SHOW + " INTEGER NOT NULL,"
				+ ProjectsColumns.ALLOW_ATTACHMENTS 	+ " INTEGER NOT NULL,"
				+ ProjectsColumns.PUBLIC				+ " INTEGER NOT NULL,"
				+ ProjectsColumns.USE_HTTPS				+ " INTEGER NOT NULL,"
				+ ProjectsColumns.BUGS_AND_CHORES_ARE_ESTIMATABLE + " INTEGER,"
				+ ProjectsColumns.COMMIT_MODE			+ " INTEGER,"
				+ ProjectsColumns.LAST_ACTIVITY_AT 		+ " TEXT,"
				+ ProjectsColumns.LABELS		 		+ " TEXT,"
				+ SyncColumns.UPDATED					+ " INTEGER NOT NULL,"
				+ "UNIQUE (" + BaseColumns._ID + ") ON CONFLICT REPLACE)");
		
		/*
		 * Stories Table
		 */
		db.execSQL("CREATE TABLE " + Tables.STORIES + " ("
				+ BaseColumns._ID 				+ " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ StoriesColumns.STORY_ID		+ " INTEGER NOT NULL,"
				+ StoriesColumns.PROJECT_ID 	+ " INTEGER NOT NULL "+ References.PROJECT_ID +","
				+ StoriesColumns.ITERATION_ID	+ " INTEGER "+References.ITERATION_ID+","
				+ StoriesColumns.STORY_TYPE		+ " TEXT NOT NULL,"
				+ StoriesColumns.URL 			+ " TEXT NOT NULL,"
				+ StoriesColumns.ESTIMATE 		+ " INTEGER,"
				+ StoriesColumns.CURRENT_STATE 	+ " TEXT NOT NULL,"
				+ StoriesColumns.DESCRIPTION 	+ " TEXT,"
				+ StoriesColumns.NAME 			+ " TEXT NOT NULL,"
				+ StoriesColumns.REQUESTED_BY 	+ " TEXT,"
				+ StoriesColumns.OWNED_BY 		+ " TEXT,"
				+ StoriesColumns.CREATED_AT 	+ " TEXT,"
				+ StoriesColumns.ACCEPTED_AT 	+ " TEXT,"
				+ StoriesColumns.LABELS 		+ " TEXT,"
				+ SyncColumns.UPDATED			+ " INTEGER NOT NULL,"
				+ "UNIQUE (" + BaseColumns._ID + ") ON CONFLICT REPLACE)");
		
		/*
		 * Members Table
		 */
		db.execSQL("CREATE TABLE " + Tables.MEMBERS + " ("
				+ BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ MembersColumns.MEMBER_ID 	+ " INTEGER NOT NULL,"
				+ MembersColumns.PROJECT_ID + " INTEGER NOT NULL "+ References.PROJECT_ID +","
				+ MembersColumns.EMAIL 		+ " TEXT NOT NULL,"
				+ MembersColumns.NAME 		+ " TEXT NOT NULL,"
				+ MembersColumns.INITIALS 	+ " TEXT NOT NULL,"
				+ MembersColumns.ROLE 		+ " TEXT NOT NULL,"
				+ "UNIQUE (" + BaseColumns._ID + ") ON CONFLICT REPLACE)");
		
		/*
		 * Iterations Table
		 */
		db.execSQL("CREATE TABLE " + Tables.ITERATIONS + " ("
				+ BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ IterationColumns.ITERATION_ID + " INTEGER NOT NULL,"
				+ IterationColumns.PROJECT_ID	+ " INTEGER NOT NULL "+References.PROJECT_ID+","
				+ IterationColumns.NUMBER		+ " INTEGER NOT NULL,"
				+ IterationColumns.START		+ " TEXT NOT NULL,"
				+ IterationColumns.END			+ " TEXT NOT NULL,"
				+ SyncColumns.UPDATED			+ " INTEGER NOT NULL,"
				+ "UNIQUE (" + BaseColumns._ID + ") ON CONFLICT REPLACE)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.d(TAG, "onUpgrade() from " + oldVersion + " to " + newVersion);
		
		if(oldVersion != DATABASE_VERSION){
			Log.w(TAG, "Destroying old data during upgrade");
			db.execSQL("DROP TABLE IF EXISTS " + Tables.PROJECTS);
			db.execSQL("DROP TABLE IF EXISTS " + Tables.MEMBERS);
			db.execSQL("DROP TABLE IF EXISTS " + Tables.STORIES);
			db.execSQL("DROP TABLE IF EXISTS " + Tables.PROJECT_INTEGRATIONS);
			db.execSQL("DROP TABLE IF EXISTS " + Tables.PROJECT_LABELS);
			db.execSQL("DROP TABLE IF EXISTS " + Tables.STORY_ATTACHMENT);
			db.execSQL("DROP TABLE IF EXISTS " + Tables.STORY_TASK);
			db.execSQL("DROP TABLE IF EXISTS " + Tables.STORY_FILTERS);
			db.execSQL("DROP TABLE IF EXISTS " + Tables.ITERATIONS);
			onCreate(db);	
		}
		

	}

}
