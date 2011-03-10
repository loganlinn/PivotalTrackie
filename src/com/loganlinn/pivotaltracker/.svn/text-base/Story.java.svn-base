package com.loganlinn.pivotaltracker;

import com.loganlinn.pivotaltracker.StoryEntry.StoryTags;
import com.loganlinn.pivotaltrackie.provider.ProjectContract.Stories;

public class Story {
	public static interface DefaultStoriesQuery extends CursorQuery {
		String[] PROJECTION = {
				Stories.STORY_ID,
				Stories.PROJECT_ID,
				Stories.NAME,
				Stories.CURRENT_STATE,
				Stories.ESTIMATE,
				Stories.STORY_TYPE
		};
		int STORY_ID = 0;
		int PROJECT_ID = 1;
		int NAME = 2;
		int CURRENT_STATE = 3;
		int ESTIMATE = 4;
		int STORY_TYPE = 5;
	}
	public static interface DetailStoriesQuery{
		String[] PROJECTION = {
				Stories.STORY_ID,
				Stories.PROJECT_ID,
				Stories.STORY_TYPE, 
				Stories.URL,
				Stories.ESTIMATE,
				Stories.CURRENT_STATE,
				Stories.DESCRIPTION,
				Stories.NAME,
				Stories.REQUESTED_BY,
				Stories.OWNED_BY,
				Stories.CREATED_AT,
				Stories.ACCEPTED_AT,
				Stories.LABELS,
		};
		int STORY_ID = 0;
		int PROJECT_ID = 1;
		int STORY_TYPE = 2;
		int URL = 3; 
		int ESTIMATE = 4;
		int CURRENT_STATE = 5;
		int DESCRIPTION = 6;
		int NAME = 7;
		int REQUESTED_BY = 8;
		int OWNED_BY = 9;
		int CREATED_AT = 10;
		int ACCEPTED_AT = 11;
		int LABELS = 12;
		//TODO: add full story query
	}
	
}
