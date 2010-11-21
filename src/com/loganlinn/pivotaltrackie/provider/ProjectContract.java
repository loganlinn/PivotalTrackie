package com.loganlinn.pivotaltrackie.provider;

import java.util.HashMap;

import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

public class ProjectContract {
	interface ProjectsColumns {
		String PROJECT_ID = "project_id";
		String NAME = "name";
		String ITERATION_LENGTH = "iteration_length";
		String WEEK_START_DATE = "week_start_date";
		String POINT_SCALE = "point_scale";
		String ACCOUNT = "account";
		String VELOCITY_SCHEME = "velocity_sceme";
		String CURRENT_VELOCITY = "current_velocity";
		String INITIAL_VELOCITY = "initial_velocity";
		String NUMBER_OF_DONE_ITERATIONS_TO_SHOW = "iterations_shown";
		String ALLOW_ATTACHMENTS = "allow_attachments";
		String PUBLIC = "public";
		String USE_HTTPS = "use_https";
		String BUGS_AND_CHORES_ARE_ESTIMATABLE = "estimate_bugs_chores";
		String COMMIT_MODE = "commit_mode";
		String LAST_ACTIVITY_AT = "last_activity";
		String LABELS = "labels";
	}

	interface StoriesColumns {
		String STORY_ID = "story_id";
		String PROJECT_ID = "prioject_id";
		String URL = "url";
		String ESTIMATE = "estimate";
		String CURRENT_STATE = "current_state";
		String DESCRIPTION = "description";
		String NAME = "name";
		String REQUESTED_BY = "requested_by";
		String OWNED_BY = "owned_by";
		String CREATED = "created";
		String ACCEPTED = "accepted";
		String LABELS = "labels";
	}
	
	interface MembersColumns{
		String MEMBER_ID = "member_id";
		String PROJECT_ID = "project_id";
		String EMAIL = "email";
		String NAME = "name";
		String INITIALS = "intiials";
		String ROLE = "role";
		
	}
	
	public static final String CONTENT_AUTHORITY = "com.loganlinn.pivotaltrackie";

    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    private static final String PATH_PROJECTS = "projects";
    private static final String PATH_STORIES = "stories";
    private static final String PATH_MEMBERS = "members";
    
    public static class Projects implements ProjectsColumns, BaseColumns{
    	private static final String TAG = "Projects";
    	
    	public static final Uri CONTENT_URI = 
			BASE_CONTENT_URI.buildUpon().appendPath(PATH_PROJECTS).build();
    	public static final String CONTENT_TYPE =
            "vnd.loganlinn.cursor.dir/vnd.pivotaltrackie.project";
    	public static final String CONTENT_ITEM_TYPE =
            "vnd.loganlinn.cursor.item/vnd.pivotaltrackie.project"; 
    	
    	public static Uri buildProjectUri(String projectId){
    		return CONTENT_URI.buildUpon().appendPath(projectId).build();
    	}

		public static String getProjectId(Uri uri) {
			Log.i(TAG, "ProjectId in ["+uri+"] is "+uri.getPathSegments().get(1));
			return uri.getPathSegments().get(1);
		}
    }
    
    public static class Stories implements StoriesColumns, BaseColumns{
    	private static final String TAG = "Stories";
    	public static final Uri CONTENT_URI = 
    		BASE_CONTENT_URI.buildUpon().appendPath(PATH_STORIES).build();
    	public static final String CONTENT_TYPE =
    		"vnd.loganlinn.cursor.dir/vnd.pivotaltrackie.story";
    	public static final String CONTENT_ITEM_TYPE =
    		 "vnd.loganlinn.cursor.item/vnd.pivotaltrackie.story"; 
    	 
    	 /** Default "ORDER BY" clause. */
    	 //public static final String DEFAULT_SORT = BlocksColumns.BLOCK_START + " ASC, "
         // + BlocksColumns.BLOCK_END + " ASC";
    	 
    	 public static Uri buildStoryUri(String storyId){
    		 return CONTENT_URI.buildUpon().appendPath(storyId).build();
    	 }
    	 
    	 public static Uri buildStoryFilterUri(StoryFilter filter){
    		 return CONTENT_URI.buildUpon().appendPath(filter.getEncodedResult()).build();
    	 }

		public static String getProjectId(Uri uri) {
			Log.i(TAG, "ProjectId in ["+uri+"] is "+uri.getPathSegments().get(1));
			return uri.getPathSegments().get(1);
		}

		public static String getStoryId(Uri uri) {
			Log.i(TAG, "StoryId in ["+uri+"] is "+uri.getPathSegments().get(3));
			return uri.getPathSegments().get(3);
		}
    }
    
    public static class Members implements MembersColumns, BaseColumns{
    	private static final String TAG = "Members";
    	public static final Uri CONTENT_URI = 
    		BASE_CONTENT_URI.buildUpon().appendPath(PATH_MEMBERS).build();
    	public static final String CONTENT_TYPE =
    		"vnd.loganlinn.cursor.dir/vnd.pivotaltrackie.member";
    	public static final String CONTENT_ITEM_TYPE =
    		 "vnd.loganlinn.cursor.item/vnd.pivotaltrackie.member"; 
    	
    	public static Uri buildMemberUri(String memberId){
    		return CONTENT_URI.buildUpon().appendPath(memberId).build();
    	}

		public static String getMemberId(Uri uri) {
			Log.i(TAG, "MemberId in ["+uri+"] is "+uri.getPathSegments().get(1));
			return uri.getPathSegments().get(1);
		}
    }
    
    interface StoryFilter{
    	HashMap<String,String> filterValues_ = new HashMap<String,String>();
    	public void addLabelFilter(String label);
    	public void addIdFilter(String id);
    	public void addStoryTypeFilter(String type);
    	public void addCurrentStateFilter(String state);
    	public void addNameFilter(String name);
    	public String getEncodedResult();
    }
    
    private ProjectContract(){
    	
    }
}
