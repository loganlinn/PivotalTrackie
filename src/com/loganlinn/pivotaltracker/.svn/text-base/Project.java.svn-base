package com.loganlinn.pivotaltracker;

import com.loganlinn.pivotaltrackie.provider.ProjectContract.Projects;

public class Project {
	public interface DefaultProjectsQuery extends CursorQuery {
		String[] PROJECTION = {Projects.PROJECT_ID, Projects.NAME,
				Projects.CURRENT_VELOCITY, Projects.LAST_ACTIVITY_AT};

		int PROJECT_ID = 0;
		int NAME = 1;
		int CURRENT_VELOCITY = 2;
		int LAST_ACTIVITY_AT = 3;
	}
}
