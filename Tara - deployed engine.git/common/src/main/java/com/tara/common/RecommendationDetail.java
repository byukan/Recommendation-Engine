package com.tara.common;

public class RecommendationDetail {
	

	public RecommendationDetail(String userId, String courseId, int taken) {
		super();
		this.userId = userId;
		this.courseId = courseId;
		this.taken = taken;
	}
	public String userId;
	public String courseId;
	public int taken;

}
