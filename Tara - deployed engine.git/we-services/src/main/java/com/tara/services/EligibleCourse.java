package com.tara.services;

import com.google.gson.annotations.SerializedName;

public class EligibleCourse {

	public EligibleCourse(String courseId, String courseUrl, Boolean eligibility) {
		super();
		this.courseId = courseId;
		this.courseUrl = courseUrl;
		this.eligibility = eligibility;
	}

	@SerializedName("courseId")
	private String courseId;
	@SerializedName("courseUrl")
	private String courseUrl;
	@SerializedName("eligibleForRecommend")
	private Boolean eligibility;

	public String getCourseId() {
		return courseId;
	}

	public void setCourseId(String courseId) {
		this.courseId = courseId;
	}

	public String getCourseUrl() {
		return courseUrl;
	}

	public void setCourseUrl(String courseUrl) {
		this.courseUrl = courseUrl;
	}

}