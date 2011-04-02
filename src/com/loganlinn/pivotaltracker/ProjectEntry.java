package com.loganlinn.pivotaltracker;

import static org.xmlpull.v1.XmlPullParser.END_DOCUMENT;
import static org.xmlpull.v1.XmlPullParser.END_TAG;
import static org.xmlpull.v1.XmlPullParser.START_TAG;
import static org.xmlpull.v1.XmlPullParser.TEXT;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Log;

import com.loganlinn.pivotaltrackie.provider.ProjectContract;

public class ProjectEntry extends HashMap<String, String> {
	private static final String TAG = "ProjectEntry";
	public static final String[] FIELDS = {
		ProjectTags.ID,
		ProjectTags.NAME,
		ProjectTags.ITERATION_LENGTH,
		ProjectTags.WEEK_START_DAY,
		ProjectTags.POINT_SCALE,
		ProjectTags.ACCOUNT,
		ProjectTags.VELOCITY_SCHEME,
		ProjectTags.CURRENT_VELOCITY,
		ProjectTags.INITIAL_VELOCITY,
		ProjectTags.NUMBER_OF_DONE_ITERATIONS_TO_SHOW,
		ProjectTags.LABELS,
		ProjectTags.ALLOW_ATTACHMENTS,
		ProjectTags.PUBLIC,
		ProjectTags.USE_HTTPS,
		ProjectTags.BUGS_AND_CHORES_ARE_ESTIMATABLE,
		ProjectTags.COMMIT_MODE,
		ProjectTags.LAST_ACTIVITY_AT,
		/*ProjectTags.MEMBERSHIPS,*/
		ProjectTags.INTEGRATIONS
	};

	/* Expected XML Tags for Project */
	public interface ProjectTags {
		String PROJECTS = "projects";
		String PROJECT = "project";

		String ID = "id";
		String NAME = "name";
		String ITERATION_LENGTH = "iteration_length";
		String WEEK_START_DAY = "week_start_day";
		String POINT_SCALE = "point_scale";
		String ACCOUNT = "account";
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
	private long mUpdated = ProjectContract.UPDATED_UNKNOWN;
	private static final SimpleDateFormat sDatetimeFormat = PivotalTracker.getDateFormat();
	
	public ProjectEntry(){
		for(String f : FIELDS){
			put(f,null);
		}
	}
	public void setUpdated(long u){
		mUpdated = u;
	}

    public long getUpdated() {
        return mUpdated;
    }
    
	public static ProjectEntry fromParser(XmlPullParser parser)
			throws XmlPullParserException, IOException {
		final int depth = parser.getDepth();
		final ProjectEntry entry = new ProjectEntry();

		String tag = null;
		int type;
		boolean inMemberships = false;
		
		while (((type = parser.next()) != END_TAG || parser.getDepth() > depth)
				&& type != END_DOCUMENT) {
			if(inMemberships) continue;
			if (type == START_TAG) {
				tag = parser.getName();
				if(ProjectTags.MEMBERSHIPS.equals(tag)){
					inMemberships = true;
				}
			} else if (type == END_TAG) {
				if(ProjectTags.MEMBERSHIPS.equals(parser.getName())){
					inMemberships = false;
				}
				tag = null;
			} else if (type == TEXT) {
				if(tag != null){
					String text = parser.getText();
					//Log.i(TAG, "tag="+tag+"\ttext="+text);
					
					if(ProjectTags.LAST_ACTIVITY_AT.equals(tag)){
						try {
							entry.setUpdated(sDatetimeFormat.parse(text).getTime());
						} catch (ParseException e) {
							Log.w(TAG, "Failed to parse updated date");
							e.printStackTrace();
						}
					}
					
					if(entry.containsKey(tag)){
						entry.put(tag, text);
						Log.i(TAG, tag+"="+text);
						
					}
				}
				
//				if (ProjectTags.ID.equals(tag)) {
//				} else if (ProjectTags.NAME.equals(tag)){
//				} else if (ProjectTags.ITERATION_LENGTH.equals(tag)){
//				} else if (ProjectTags.WEEK_START_DAY.equals(tag)){
//				} else if (ProjectTags.POINT_SCALE.equals(tag)){
//				} else if (ProjectTags.VELOCITY_SCHEME.equals(tag)){
//				} else if (ProjectTags.CURRENT_VELOCITY.equals(tag)){
//				} else if (ProjectTags.INITIAL_VELOCITY.equals(tag)){
//				} else if (ProjectTags.NUMBER_OF_DONE_ITERATIONS_TO_SHOW.equals(tag)){
//				} else if (ProjectTags.LABELS.equals(tag)){
//				} else if (ProjectTags.ALLOW_ATTACHMENTS.equals(tag)){
//				} else if (ProjectTags.USE_HTTPS.equals(tag)){
//				} else if (ProjectTags.PUBLIC.equals(tag)){
//				} else if (ProjectTags.USE_HTTPS.equals(tag)){
//				} else if (ProjectTags.BUGS_AND_CHORES_ARE_ESTIMATABLE.equals(tag)){
//				} else if (ProjectTags.COMMIT_MODE.equals(tag)){
//				} else if (ProjectTags.LAST_ACTIVITY_AT.equals(tag)){
//				} else if (ProjectTags.MEMBERSHIPS.equals(tag)){
//				} else if (ProjectTags.INTEGRATIONS.equals(tag)){
//				} else if (ProjectTags.MEMBERSHIPS.equals(tag)){
//					//TODO parseMembers(parser, batch, resolver);
//				}
			}
		}

		return entry;
	}
	

}


