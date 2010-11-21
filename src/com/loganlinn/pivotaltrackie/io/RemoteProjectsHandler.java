package com.loganlinn.pivotaltrackie.io;

import static org.xmlpull.v1.XmlPullParser.END_DOCUMENT;
import static org.xmlpull.v1.XmlPullParser.START_TAG;
import static org.xmlpull.v1.XmlPullParser.END_TAG;
import static org.xmlpull.v1.XmlPullParser.TEXT;

import java.io.IOException;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;

import com.loganlinn.pivotaltracker.ProjectEntry.ProjectTags;
import com.loganlinn.pivotaltracker.util.Lists;
import com.loganlinn.pivotaltrackie.provider.ProjectContract;
import com.loganlinn.pivotaltrackie.provider.ProjectContract.Projects;

public class RemoteProjectsHandler extends XmlHandler {
	private static final String TAG = "RemoteProjectsHandler";

	private RemoteExecutor executor_;

	public RemoteProjectsHandler(RemoteExecutor executor) {
		super(ProjectContract.CONTENT_AUTHORITY);
		executor_ = executor;
	}

	@Override
	public ArrayList<ContentProviderOperation> parse(XmlPullParser parser,
			ContentResolver resolver) throws XmlPullParserException,
			IOException {
		final ArrayList<ContentProviderOperation> batch = Lists.newArrayList();

		int type;

		while ((type = parser.next()) != END_DOCUMENT) {
			if (type == START_TAG
					&& ProjectTags.PROJECT.equals(parser.getName())) {
				parseProject(parser, batch, resolver);
			}
		}

		return null;
	}

	public void parseMembers(XmlPullParser parser,
			ArrayList<ContentProviderOperation> batch, ContentResolver resolver)
			throws XmlPullParserException, IOException {

	}

	public void parseProject(XmlPullParser parser,
			ArrayList<ContentProviderOperation> batch, ContentResolver resolver)
			throws XmlPullParserException, IOException {
		final int depth = parser.getDepth();
		ContentProviderOperation.Builder builder = ContentProviderOperation
				.newInsert(Projects.CONTENT_URI);

		String tag = null;
		int type;

		while (((type = parser.next()) != END_TAG || parser.getDepth() > depth)
				&& type != END_DOCUMENT) {
			if (type == START_TAG) {
				tag = parser.getName();
			} else if (type == END_TAG) {
				tag = null;
			} else if (type == TEXT) {
				if (ProjectTags.ID.equals(tag)) {
					
				}
			}
		}
	}

	/* Expected XML Tags for Project */
	public interface ProjectTags {
		String PROJECTS = "projects";
		String PROJECT = "project";

		String ID = "id";
		String NAME = "name";
		String ITERATION_LENGTH = "iteration_length";
		String WEEK_START_DAY = "week_start_day";
		String POINT_SCALE = "point_scale";
		String VELOCITY_SCHEME = "velocity_scheme";
		String CURRENT_VELOCITY = "current_velocity";
		String INITIAL_VELOCITY = "initial_velocity";
		String NUMBER_OF_DONE_ITERATIONS_TO_SHOW = "number_of_done_iterations_to_show";
		String LABELS = "labels";
		String ALLOW_ATTACHMENTS = "allow_attachments";
		String PUBLIC = "public";
		String USE_HTTPS = "use_https";
		String BUGS_AND_CHORES_ARE_ESTIMATABLE = "bugs_and_chores_are_estimatable";
		String COMMIT_MODE = "commit_mode";
		String LAST_ACTIVITY_AT = "last_activity_at";
		String MEMBERSHIPS = "memberships";
		String INTEGRATIONS = "integrations";

	}
}
