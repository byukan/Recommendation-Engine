package com.tara.dtos;

import java.util.HashMap;
import java.util.Map;

public class Course {
	public String title;
	public String description;
	public String objective;
	public String id;
	Map<String, Object> propertiesMap;

	public String toString(String appender) {

		return id + appender + title;

	}

	public Map<String, Object> getPropertiesMap() {
		return propertiesMap;
	}
	public Course(Course course) {
		title = course.title;
		description = course.description;
		objective = course.objective;
		id =course.id;
		propertiesMap = new HashMap<String, Object>();
	}
	public Course() {
		title = null;
		description = null;
		objective = null;
		id = null;
		propertiesMap = new HashMap<String, Object>();
	}

	public Course withId(String id) {
		this.id = id;

		return this;
	}

	public Course withTitle(String title) {
		this.title = title;
		propertiesMap.put("title", title);
		return this;
	}

	public Course withDescription(String description) {
		this.description = description;
		propertiesMap.put("description", description);
		return this;
	}

	public Course withObjective(String objective) {
		this.objective = objective;
		propertiesMap.put("objective", objective);
		return this;
	}

}
