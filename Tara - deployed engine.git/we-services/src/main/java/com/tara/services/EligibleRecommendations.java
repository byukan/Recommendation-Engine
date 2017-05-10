package com.tara.services;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class EligibleRecommendations {

	@SerializedName("EligibleCourses")
	private List<EligibleCourse> EligibleCourses = null;
	@SerializedName("employeeID")
	private String employeeID;

	public List<EligibleCourse> getEligibleCourses() {
		return EligibleCourses;
	}

	public void setEligibleCourses(List<EligibleCourse> EligibleCourses) {
		this.EligibleCourses = EligibleCourses;
	}

	public String getEmployeeID() {
		return employeeID;
	}

	public void setEmployeeID(String employeeID) {
		this.employeeID = employeeID;
	}

}