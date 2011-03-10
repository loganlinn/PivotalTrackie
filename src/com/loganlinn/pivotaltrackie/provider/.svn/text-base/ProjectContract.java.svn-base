package com.loganlinn.pivotaltrackie.provider;

import java.util.HashMap;

import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

public class ProjectContract {
	/**
	 * Special value for {@link SyncColumns#UPDATED} indicating that an entry
	 * has never been updated, or doesn't exist yet.
	 */
	public static final long UPDATED_NEVER = -2;

	/**
	 * Special value for {@link SyncColumns#UPDATED} indicating that the last
	 * update time is unknown, usually when inserted from a local file source.
	 */
	public static final long UPDATED_UNKNOWN = -1;

	interface ProjectsColumns {
		String PROJECT_ID = "project_id";
		String NAME = "name";
		String ITERATION_LENGTH = "iteration_length";
		String WEEK_START_DAY = "week_start_day";
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

	interface IterationColumns {
		String ITERATION_ID = "iteration_id";
		String PROJECT_ID = Projects.PROJECT_ID;
		String NUMBER = "number";
		String START = "start";
		String END = "end";

	}

	interface StoriesColumns {
		String STORY_ID = "story_id";
		String PROJECT_ID = Projects.PROJECT_ID;
		String STORY_TYPE = "story_type";
		String URL = "url";
		String ESTIMATE = "estimate";
		String CURRENT_STATE = "current_state";
		String DESCRIPTION = "description";
		String NAME = "name";
		String REQUESTED_BY = "requested_by";
		String OWNED_BY = "owned_by";
		String CREATED_AT = "created_at";
		String ACCEPTED_AT = "accepted_at";
		String LABELS = "labels";
		String ITERATION_ID = IterationColumns.ITERATION_ID;
	}

	interface MembersColumns {
		String MEMBER_ID = "member_id";
		String PROJECT_ID = "project_id";
		String EMAIL = "email";
		String NAME = "name";
		String INITIALS = "initials";
		String ROLE = "role";
	}

	public interface SyncColumns {
		/** Last time this entry was updated or synchronized. */
		String UPDATED = "updated";
	}

	public static final String CONTENT_AUTHORITY = "com.loganlinn.pivotaltrackie";

	private static final Uri BASE_CONTENT_URI = Uri.parse("content://"
			+ CONTENT_AUTHORITY);

	private static final String PATH_PROJECTS = "projects";
	private static final String PATH_STORIES = "stories";
	private static final String PATH_MEMBERS = "members";
	private static final String PATH_ITERATIONS = "iterations";

	public static class Projects implements ProjectsColumns, BaseColumns,
			SyncColumns {
		private static final String TAG = "Projects";

		public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
				.appendPath(PATH_PROJECTS).build();
		public static final String CONTENT_TYPE = "vnd.loganlinn.cursor.dir/vnd.pivotaltrackie.project";
		public static final String CONTENT_ITEM_TYPE = "vnd.loganlinn.cursor.item/vnd.pivotaltrackie.project";

		public static Uri buildProjectUri(String projectId) {
			return CONTENT_URI.buildUpon().appendPath(projectId).build();
		}

		public static Uri buildProjectUri(Integer projectId) {
			return buildProjectUri(projectId.toString());
		}

		public static String getProjectId(Uri uri) {
			Log.i(TAG, "ProjectId in [" + uri + "] is "
					+ uri.getPathSegments().get(1));
			return uri.getPathSegments().get(1);
		}
	}

	public static class Members implements MembersColumns, BaseColumns,
			SyncColumns {
		private static final String TAG = "Members";
		public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
				.appendPath(PATH_MEMBERS).build();
		public static final String CONTENT_TYPE = "vnd.loganlinn.cursor.dir/vnd.pivotaltrackie.member";
		public static final String CONTENT_ITEM_TYPE = "vnd.loganlinn.cursor.item/vnd.pivotaltrackie.member";

		public static Uri buildMemberUri(String memberId) {
			return CONTENT_URI.buildUpon().appendPath(memberId).build();
		}

		public static String getMemberId(Uri uri) {
			Log.i(TAG, "MemberId in [" + uri + "] is "
					+ uri.getPathSegments().get(1));
			return uri.getPathSegments().get(1);
		}
	}

	public static class Iterations implements IterationColumns, BaseColumns,
			SyncColumns {
		public static final String TAG = "Iterations";
		public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
				.appendPath(PATH_ITERATIONS).build();
		public static final String CONTENT_TYPE = "vnd.loganlinn.cursor.dir/vnd.pivotaltrackie.iteration";
		public static final String CONTENT_ITEM_TYPE = "vnd.loganlinn.cursor.item/vnd.pivotaltrackie.iteration";
		
		public static Uri buildIterationUri(String iterationId) {
			return CONTENT_URI.buildUpon().appendPath(iterationId).build();
		}
		
		public static String getIterationId(Uri uri) {
			return uri.getPathSegments().get(1);
		}
	}

	public static class Stories implements StoriesColumns, BaseColumns,
			SyncColumns {
		private static final String TAG = "Stories";
		public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
				.appendPath(PATH_STORIES).build();
		public static final String CONTENT_TYPE = "vnd.loganlinn.cursor.dir/vnd.pivotaltrackie.story";
		public static final String CONTENT_ITEM_TYPE = "vnd.loganlinn.cursor.item/vnd.pivotaltrackie.story";

		public static final String STORY_TYPE_FEATURE = "feature";
		public static final String STORY_TYPE_CHORE = "chore";
		public static final String STORY_TYPE_BUG = "bug";
		public static final String CURRENT_STATUS_ACCEPTED = "accepted";
		public static final String CURRENT_STATUS_UNSCHEDULED = "unscheduled";
		public static final String CURRENT_STATUS_FINISHED = "finished";
		public static final String CURRENT_STATUS_DELIVERED = "delivered";
		public static final String CURRENT_STATUS_STARTED = "started";
		public static final int NEW_STORY = -2;

		/** Default "ORDER BY" clause. */
		// public static final String DEFAULT_SORT = BlocksColumns.BLOCK_START +
		// " ASC, "
		// + BlocksColumns.BLOCK_END + " ASC";

		public static Uri buildStoryUri(String storyId) {
			return CONTENT_URI.buildUpon().appendPath(storyId).build();
		}

		public static Uri buildStoryUri(Integer storyId) {
			return buildStoryUri(storyId.toString());
		}

		public static Uri buildStoryFilterUri(StoryFilter filter) {
			return CONTENT_URI.buildUpon()
					.appendPath(filter.getEncodedResult()).build();
		}

		public static String getStoryId(Uri uri) {
			return uri.getPathSegments().get(1);
		}
		
	}

	
	interface StoryFilter {
		HashMap<String, String> filterValues_ = new HashMap<String, String>();

		public void addLabelFilter(String label);

		public void addIdFilter(String id);

		public void addStoryTypeFilter(String type);

		public void addCurrentStateFilter(String state);

		public void addNameFilter(String name);

		public String getEncodedResult();
	}

	private ProjectContract() {

	}
}
