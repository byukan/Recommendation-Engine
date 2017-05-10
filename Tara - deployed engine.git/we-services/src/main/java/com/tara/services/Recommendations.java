package com.tara.services;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class Recommendations {

	@SerializedName("courses")
	private List<Course> courses = null;
	@SerializedName("employeeID")
	private String employeeID;

	public List<Course> getCourses() {
		return courses;
	}

	public void setCourses(List<Course> courses) {
		this.courses = courses;
	}

	public String getEmployeeID() {
		return employeeID;
	}

	public void setEmployeeID(String employeeID) {
		this.employeeID = employeeID;
	}

}