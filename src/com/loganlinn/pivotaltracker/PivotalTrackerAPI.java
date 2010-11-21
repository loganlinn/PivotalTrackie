package com.loganlinn.pivotaltracker;

import android.app.Activity;

public interface PivotalTrackerAPI {
	void authenticate(Activity activity, AuthenticateListener authenticateListener);
	void getProject(int id);  
	void getProjects(); 
	void getActivityFeed();
	void getActivityFeed(int projectId);
	void getIterations(int offset, int limit);
	void getIterations(int offset);
	void getIterations();
	void getStory(int storyId);
	void getStories();
	void getStories(String filter);
	void getStories(int offset, int limit);
	void addStory(Story story);
	void updateStory(Story story);
	void deleteStory(int storyId);
	void moveStory(int sourceStoryId, int targetStoryId, int order);
	void getTasks();
	void getTasks(int storyId);
	void addTask(StoryTask task);
	void updateTask(StoryTask task);
	void deleteTask(int taskId);
	void getAttachment(int attachmentId);
}
