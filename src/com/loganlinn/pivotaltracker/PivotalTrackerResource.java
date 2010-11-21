package com.loganlinn.pivotaltracker;

public abstract class PivotalTrackerResource {
	public long id_;
	public String tableName_;
	
	public PivotalTrackerResource(String tableName){
		tableName_ = tableName;
	}
	public void save(){
		
	}
	public abstract String getPropertyNames();
	
	
}
