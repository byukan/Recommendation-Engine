package com.tara.dtos;

import java.util.HashMap;
import java.util.Map;

public class ProjectDetail {

	public String name;
	public String startDate;
	public String dueDate;
	public String creationDate;
	Map<String, Object> propertiesMap;

	public ProjectDetail() {
		propertiesMap = new HashMap<String, Object>();

	}

	public Map<String, Object> getPropertiesMap() {
		return propertiesMap;
	}

	public ProjectDetail withName(String name) {
		this.name = name;
		propertiesMap.put("name", name);
		return this;

	}

	public ProjectDetail withStartDate(String startDate) {
		this.startDate = startDate;
		propertiesMap.put("startDate", startDate);
		return this;
	}

	public ProjectDetail withDueDate(String dueDate) {
		this.dueDate = dueDate;
		propertiesMap.put("dueDate", dueDate);
		return this;
	}

	public ProjectDetail withCreationDate(String creationDate) {
		this.creationDate = creationDate;
		propertiesMap.put("creationDate", creationDate);
		return this;
	}

}
