package com.tara.dtos;

import java.util.HashMap;
import java.util.Map;

public class GroupDetail {

	public String name;
	public String type;
	public String displayName;
	public String status;
	public String creationDate;
	public String entiteDeaffection;
	Map<String, Object> propertiesMap;

	public GroupDetail() {
		propertiesMap = new HashMap<String, Object>();

	}

	public Map<String, Object> getPropertiesMap() {
		return propertiesMap;
	}

	public GroupDetail withName(String value) {
		this.name = value;
		propertiesMap.put("name", name);

		return this;

	}

	public GroupDetail withType(String value) {
		this.type = value;
		propertiesMap.put("type", type);

		return this;
	}

	public GroupDetail withDisplayName(String value) {
		this.displayName = value;
		propertiesMap.put("displayName", displayName);

		return this;
	}

	public GroupDetail withStatus(String value) {
		this.status = value;
		propertiesMap.put("status", status);

		return this;
	}

	public GroupDetail withEntiteDeaffection(String value) {
		this.entiteDeaffection = value;
		propertiesMap.put("entiteDeaffection", entiteDeaffection);

		return this;
	}

}
