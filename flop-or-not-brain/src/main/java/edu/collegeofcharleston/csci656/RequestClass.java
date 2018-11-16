package edu.collegeofcharleston.csci656;

public class RequestClass {
	String action;
	String value;
	String director;
	String budget;
	String actors;
	
	public void setAction(String action) {
		this.action = action;
	}
	
	public String getAction() {
		return action;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
	
	public void setDirector(String director) {
		this.director = director;
	}
	
	public String getDirector() {
		return this.director;
	}
	
	public void setBudget(String budget) {
		this.budget = budget;
	}
	
	public String getBudget() {
		return this.budget;
	}
	
	public void setActors(String actors) {
		this.actors = actors;
	}
	
	public String getActors() {
		return actors;
	}
	
	public RequestClass() {
		
	}
}
