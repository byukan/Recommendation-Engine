package com.tara.common;

public class ApiEndpoints {

	// http://localhost:8080/TaraServices/rest/tara/hello
	public static final String WEBSERVICES_PREFIX = "";

	public static final String WEBSERVICES_URL = "/TaraServices/rest/tara/";

	public static final String COURSE_TAKE_ENDPOINT = WEBSERVICES_URL
			+ "markRecommendationTaken/";
	
	public static final String COURSE_TAKEN_BEFORE_ENDPOINT = WEBSERVICES_URL
	+ "insertCourseTaken/";
	public static final String LOOKUP_COURSE_INSERT_ENDPOINT = WEBSERVICES_URL
			+ "insertLookupCourse/";
	public static final String COURSE_INSERT_ENDPOINT = WEBSERVICES_URL
			+ "insertCourse/";
	public static final String COURSE_UPDATE_ENDPOINT = WEBSERVICES_URL
			+ "updateCourse/";

	public static final String RECOMMENDATION_SERVICE_ENDPOINT = "http://192.168.161.79:5000/recommendations/rest";
	public static final String EMAIL_SERVICE_ENDPOINT = "http://192.168.161.79:5000/recommendations/email";

}
