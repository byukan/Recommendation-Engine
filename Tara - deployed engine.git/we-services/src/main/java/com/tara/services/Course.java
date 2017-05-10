package com.tara.services;

import com.google.gson.annotations.SerializedName;

public class Course {

	public Course(String courseId, String courseName, String courseUrl) {
		super();
		this.courseId = courseId;
		this.courseName = courseName;
		this.courseUrl = courseUrl;
	}

	@SerializedName("courseId")
	private String courseId;
	@SerializedName("courseName")
	private String courseName;
	@SerializedName("courseUrl")
	private String courseUrl;

	public String getCourseId() {
		return courseId;
	}

	public void setCourseId(String courseId) {
		this.courseId = courseId;
	}

	public String getCourseName() {
		return courseName;
	}

	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}

	public String getCourseUrl() {
		return courseUrl;
	}

	public void setCourseUrl(String courseUrl) {
		this.courseUrl = courseUrl;
	}

}