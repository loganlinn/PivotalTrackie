package com.loganlinn.pivotaltracker;

import java.io.IOException;
import java.util.HashMap;
import java.util.regex.Matcher;
import static org.xmlpull.v1.XmlPullParser.END_DOCUMENT;
import static org.xmlpull.v1.XmlPullParser.END_TAG;
import static org.xmlpull.v1.XmlPullParser.START_TAG;
import static org.xmlpull.v1.XmlPullParser.TEXT;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class ProjectEntry extends HashMap<String, String> {
	private static final String TAG = "ProjectEntry";

	public static ProjectEntry fromParser(XmlPullParser parser)
			throws XmlPullParserException, IOException {
		final int depth = parser.getDepth();
		final ProjectEntry entry = new ProjectEntry();

		String tag = null;
		int type;
		while (((type = parser.next()) != END_TAG || parser.getDepth() > depth)
				&& type != END_DOCUMENT) {
			if(type == START_TAG){
				tag = parser.getName();
			} else if( type == END_TAG){
				tag = null;
			} else if (type == TEXT) {
				if(ProjectTags.ID.equals(tag)){
					
				}
			}
		}

		return entry;
	}
	
	/* Expected XML Tags for Project 			*/
	public interface ProjectTags{
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


