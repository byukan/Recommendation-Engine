package com.tara.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tara.common.CommandRunner;
import com.tara.common.MySqlConnector;
import com.tara.common.Recommendation;
import com.tara.common.RecommendationDetail;

@Path("/tara")
public class TaraResource {

	public static void main(String[] args) throws ParseException, IOException {
		System.out.println("kaab");
		// MySqlConnector mySqlConnector = new MySqlConnector();
		TaraResource taraResource = new TaraResource();

		System.out.println(taraResource.generateRecommendationsV2("NWHT8280",
				10));

	}

	@SuppressWarnings("unchecked")
	@GET
	@Path("insertRecommendation/{userId}/{courseId}")
	@Produces("application/json")
	public String insertRecommendation(@PathParam("userId") String userId,
			@PathParam("courseId") String courseId) {

		courseId = courseId.replace("kaabCustomSlash", "/");
		courseId = courseId.replace("kaabCustomBackSlash", "\\");

		userId = userId.replace("kaabCustomSlash", "/");
		userId = userId.replace("kaabCustomBackSlash", "\\");

		JSONObject json = new JSONObject();
		MySqlConnector mySqlConnector = new MySqlConnector();
		int id = mySqlConnector.insertRecommendation(userId, courseId);
		if (id == -1)
			json.put("status", "failed");
		else {
			json.put("status", "success");
			json.put("recommendationId", id);
		}

		return json.toJSONString();
	}

	@SuppressWarnings("unchecked")
	@GET
	@Path("insertCourseTaken/{userId}/{courseId}")
	@Produces("application/json")
	public String insertCourseTaken(@PathParam("userId") String userId,
			@PathParam("courseId") String courseId) {

		courseId = courseId.replace("kaabCustomSlash", "/");
		courseId = courseId.replace("kaabCustomBackSlash", "\\");

		userId = userId.replace("kaabCustomSlash", "/");
		userId = userId.replace("kaabCustomBackSlash", "\\");

		JSONObject json = new JSONObject();
		MySqlConnector mySqlConnector = new MySqlConnector();
		int id = mySqlConnector.insertCourseTaken(userId, courseId);
		if (id == -1)
			json.put("status", "failed");
		else {
			json.put("status", "success");
			json.put("message", "course taken request successfull");
		}

		return json.toJSONString();
	}

	@SuppressWarnings("unchecked")
	@GET
	@Path("insertCourse/{id}/{title}")
	@Produces("application/json")
	public String insertCourse(@PathParam("id") String id,
			@PathParam("title") String title) {
		id = id.replace("kaabCustomSlash", "/");
		id = id.replace("kaabCustomBackSlash", "\\");

		title = title.replace("kaabCustomSlash", "/");
		title = title.replace("kaabCustomBackSlash", "\\");
		JSONObject json = new JSONObject();
		MySqlConnector mySqlConnector = new MySqlConnector();
		int retId = mySqlConnector.insertCourse(id, title);
		if (retId == -1)
			json.put("status", "failed");
		else {
			json.put("status", "success");
		}

		return json.toJSONString();
	}

	@SuppressWarnings("unchecked")
	@GET
	@Path("insertLookupCourse/{id}")
	@Produces("application/json")
	public String insertLookupCourse(@PathParam("id") String id) {
		id = id.replace("kaabCustomSlash", "/");
		id = id.replace("kaabCustomBackSlash", "\\");

		JSONObject json = new JSONObject();
		MySqlConnector mySqlConnector = new MySqlConnector();
		int retId = mySqlConnector.insertLookupCourse(id);
		if (retId == -1)
			json.put("status", "failed");
		else {
			json.put("status", "success");
		}

		return json.toJSONString();
	}

	@SuppressWarnings("unchecked")
	@GET
	@Path("updateCourse/{id}/{oldTitle}/{newTitle}")
	@Produces("application/json")
	public String updateCourse(@PathParam("id") String id,
			@PathParam("oldTitle") String oldTitle,
			@PathParam("newTitle") String newTitle) {

		id = id.replace("kaabCustomSlash", "/");
		id = id.replace("kaabCustomBackSlash", "\\");
		oldTitle = oldTitle.replace("kaabCustomSlash", "/");
		oldTitle = oldTitle.replace("kaabCustomBackSlash", "\\");

		newTitle = newTitle.replace("kaabCustomSlash", "/");
		newTitle = newTitle.replace("kaabCustomBackSlash", "\\");

		JSONObject json = new JSONObject();
		MySqlConnector mySqlConnector = new MySqlConnector();
		boolean ret = mySqlConnector.updateCourse(id, oldTitle, newTitle);
		if (!ret)
			json.put("status", "failed");
		else {
			json.put("status", "success");
		}

		return json.toJSONString();
	}

	@SuppressWarnings("unchecked")
	@GET
	@Path("markRecommendationTaken/{userId}/{courseId}")
	@Produces("application/json")
	public String markRecommendationTaken(@PathParam("userId") String userId,
			@PathParam("courseId") String courseId) {

		userId = userId.replace("kaabCustomSlash", "/");
		userId = userId.replace("kaabCustomBackSlash", "\\");
		courseId = courseId.replace("kaabCustomSlash", "/");
		courseId = courseId.replace("kaabCustomBackSlash", "\\");

		JSONObject json = new JSONObject();
		MySqlConnector mySqlConnector = new MySqlConnector();
		boolean response = mySqlConnector.markRecommendationTaken(userId,
				courseId);
		if (response == false)
			json.put("status", "failed");
		else {
			json.put("status", "success");
		}

		return json.toJSONString();
	}

	@GET
	@Path("/hello")
	@Produces("text/plain")
	public String hello() {
		System.out.println("testing.....................");
		return "hello  kaab bin tariq";

	}

	@SuppressWarnings("unchecked")
	@GET
	@Path("getRecommendations/{startDate}/{endDate}/{includeDate}/{state}")
	@Produces("application/json")
	public String getRecommendations(@PathParam("startDate") long start,
			@PathParam("endDate") long end,
			@PathParam("includeDate") boolean includeDate,
			@PathParam("state") String state) {
		JSONObject json = new JSONObject();
		MySqlConnector mySqlConnector = new MySqlConnector();
		int recommendationsCount = mySqlConnector.getRecommendationsCount(
				start, end, includeDate, state);
		if (recommendationsCount == -1) {
			json.put("status", "failed");
			json.put("count", "0");
		} else {
			json.put("status", "success");
			json.put("count", recommendationsCount);
		}
		return json.toJSONString();
	}

	@SuppressWarnings("unchecked")
	@GET
	@Path("generateRecommendations/{employeeID}/{count}")
	@Produces("application/json")
	public String generateRecommendations(
			@PathParam("employeeID") String employeeID,
			@PathParam("count") int count) {
		try {
			String command = "bash /apache/tomcat/bin/rest.sh " + employeeID
					+ " " + count;

			String output = CommandRunner.triggerCommand(command);
			if (output.equals("false")) {
				JSONObject json = new JSONObject();
				json.put("status", "failed");
				/*
				 * json.put("requestedCommand", command);
				 * json.put("serviceResponse", output);
				 */return json.toJSONString();
			} else {

				Gson g = new Gson();
				Recommendations recommendations = g.fromJson(output,
						Recommendations.class);

				for (Course course : recommendations.getCourses()) {
					this.insertRecommendation(recommendations.getEmployeeID(),
							course.getCourseId());
				}
				return output;

			}

		} catch (Exception exp) {
			JSONObject json = new JSONObject();
			json.put("status", "failed");
			/*
			 * json.put("requestedCommand", command);
			 * json.put("serviceResponse", output);
			 */return json.toJSONString();
		}
	}

	@GET
	@Path("sendEmail/v2/{employeeID}/{count}")
	@Produces("application/json")
	public String sendEmail2(@PathParam("employeeID") String employeeID,
			@PathParam("count") int count) {
		String command = "bash /apache/tomcat/bin/email.sh " + employeeID + " "
				+ count;
		String output = CommandRunner.triggerCommand(command);
		System.out.println(output);
		if (output.equals("false") || output.equals("error")) {
			JSONObject json = new JSONObject();
			/*
			 * json.put("status", "failed"); json.put("requestedCommand",
			 * command);
			 */return json.toJSONString();
		} else {
			return output;
		}
	}

	@SuppressWarnings("unchecked")
	@GET
	@Path("generateRecommendations/v2/{employeeID}/{count}")
	@Produces("application/json")
	public String generateRecommendationsV2(
			@PathParam("employeeID") String employeeID,
			@PathParam("count") int count) {
		// try {
		//
		// MySqlConnector mySqlConnector = new MySqlConnector();
		// Gson gson = new GsonBuilder().serializeNulls().create();
		// Recommendations recommendationResults = null;
		// String command = "bash /apache/tomcat/bin/rest.sh " + employeeID
		// + " " + count;
		// String output = CommandRunner.triggerCommand(command);
		// if (output.equals("false")) {
		// JSONObject json = new JSONObject();
		// json.put("status", "failed");
		// return json.toJSONString();
		// } else {
		// recommendationResults = gson.fromJson(output,
		// Recommendations.class);
		//
		// List<Course> recommendedEligibleCourses = new ArrayList<Course>();
		// for (Course course : recommendationResults.getCourses()) {
		// if (!(mySqlConnector.isCourseTaken(employeeID,
		// course.getCourseId()) && mySqlConnector
		// .checkLookupCourse(course.getCourseId()))) {
		// recommendedEligibleCourses.add(course);
		//
		// }
		// }
		// recommendationResults.setCourses(recommendedEligibleCourses);
		//
		// for (Course course : recommendedEligibleCourses) {
		// this.insertRecommendation(
		// recommendationResults.getEmployeeID(),
		// course.getCourseId());
		// }
		// return gson.toJson(recommendationResults);
		// }
		//
		// } catch (Exception exp) {
		// JSONObject json = new JSONObject();
		// json.put("status", "failed");
		// return json.toJSONString();
		// }
		return generateRecommendations(employeeID, count);
	}

	@POST
	@Path("getRecommendationEligibility/")
	@Consumes("application/json")
	@Produces("application/json")
	public String getRecommendationEligibility(String json)
			throws ParseException {
		try {
			MySqlConnector mySqlConnector = new MySqlConnector();
			Gson gson = new GsonBuilder().serializeNulls().create();
			Recommendations recommendationResults = null;
			recommendationResults = gson.fromJson(json, Recommendations.class);

			String employeeID = recommendationResults.getEmployeeID();

			List<EligibleCourse> recommendedEligibleCourses = new ArrayList<EligibleCourse>();
			for (Course course : recommendationResults.getCourses()) {
				if (!(mySqlConnector.isCourseTaken(employeeID,
						course.getCourseId()) && mySqlConnector
						.checkLookupCourse(course.getCourseId())))
					recommendedEligibleCourses.add(new EligibleCourse(course
							.getCourseId(), course.getCourseUrl(), true));
				else
					recommendedEligibleCourses.add(new EligibleCourse(course
							.getCourseId(), course.getCourseUrl(), false));
			}

			EligibleRecommendations eligibleRecommendations = new EligibleRecommendations();
			eligibleRecommendations.setEmployeeID(employeeID);
			eligibleRecommendations
					.setEligibleCourses(recommendedEligibleCourses);
			return gson.toJson(eligibleRecommendations);

		} catch (Exception exp) {
			JSONObject outJson = new JSONObject();
			outJson.put("status", "failed");
			return outJson.toJSONString();
		}
	}

	@GET
	@Path("sendEmail/{employeeID}/{count}")
	@Produces("application/json")
	public String sendEmail(@PathParam("employeeID") String employeeID,
			@PathParam("count") int count) {
		String command = "bash /apache/tomcat/bin/email.sh " + employeeID + " "
				+ count;
		String output = CommandRunner.triggerCommand(command);
		System.out.println(output);
		if (output.equals("false") || output.equals("error")) {
			JSONObject json = new JSONObject();
			/*
			 * json.put("status", "failed"); json.put("requestedCommand",
			 * command);
			 */return json.toJSONString();
		} else {
			return output;
		}
	}

	@SuppressWarnings("unchecked")
	@GET
	@Path("getRecommendationsByUser/{startDate}/{endDate}/{includeDate}/{state}/{count}")
	@Produces("application/json")
	public String getRecommendationsByUser(@PathParam("startDate") long start,
			@PathParam("endDate") long end,
			@PathParam("includeDate") boolean includeDate,
			@PathParam("state") String state, @PathParam("count") int count) {
		JSONObject json = new JSONObject();
		if (count == -1)
			count = Integer.MAX_VALUE;
		MySqlConnector mySqlConnector = new MySqlConnector();
		ArrayList<Recommendation> recommendations = mySqlConnector
				.getRecommendationsCountByUser(start, end, includeDate, state,
						count);
		if (recommendations == null) {
			json.put("status", "failed");
			json.put("values", "0");
		} else {
			json.put("status", "success");

			JSONArray list = new JSONArray();

			for (Recommendation recommendation : recommendations) {
				JSONObject user = new JSONObject();
				user.put("userId", recommendation.userId);
				user.put("count", recommendation.count);
				list.add(user);
			}

			json.put("userInfo", list);

		}
		return json.toJSONString();
	}

	@SuppressWarnings("unchecked")
	@GET
	@Path("getDetailedRecommendationsByUser/{startDate}/{endDate}/{includeDate}/{state}/{count}")
	@Produces("application/json")
	public String getDetailedRecommendationsByUser(
			@PathParam("startDate") long start, @PathParam("endDate") long end,
			@PathParam("includeDate") boolean includeDate,
			@PathParam("state") String state, @PathParam("count") int count) {
		JSONObject json = new JSONObject();
		MySqlConnector mySqlConnector = new MySqlConnector();
		if (count == -1)
			count = Integer.MAX_VALUE;
		ArrayList<RecommendationDetail> recommendations = mySqlConnector
				.getRecommendations(start, end, includeDate, state, count);
		if (recommendations == null) {
			json.put("status", "failed");
			json.put("values", "0");
		} else {
			json.put("status", "success");

			JSONArray list = new JSONArray();

			for (RecommendationDetail recommendationDetail : recommendations) {
				JSONObject user = new JSONObject();
				user.put("userId", recommendationDetail.userId);
				user.put("courseId", recommendationDetail.courseId);
				user.put("taken", recommendationDetail.taken);
				list.add(user);
			}

			json.put("recommendations", list);

		}
		return json.toJSONString();
	}

	@SuppressWarnings("unchecked")
	@POST
	@Path("rateCourse/")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public String rateCourse(@FormParam("userId") String userId,
			@FormParam("courseId") String courseId,
			@FormParam("score") float score,
			@FormParam("feedback") String feedback) {
		JSONObject json = new JSONObject();
		try {
			// EventClient client = new
			// EventClient(Configuration.PIOAPPACCESSKEY,
			// Configuration.PIOAPPURL);
			// Map<String, Object> properties = new HashMap<String, Object>();
			// properties.put("rateScore", score);
			// Event courseTakenEvent = new Event().event("rate")
			// .entityType("user").entityId(userId)
			// .targetEntityType("item").targetEntityId(courseId)
			// .properties(properties).eventTime(new DateTime());
			// List<FutureAPIResponse> listOfFutures = new
			// ArrayList<FutureAPIResponse>();
			//
			// FutureAPIResponse future;
			//
			// future = client.createEventAsFuture(courseTakenEvent);
			//
			// listOfFutures.add(future);
			// client.close();

			MySqlConnector mySqlConnector = new MySqlConnector();
			if (mySqlConnector.addFeedback(userId, courseId, feedback, score))
				json.put("status", "success");
			else
				json.put("status", "failed");
			return json.toJSONString();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			json.put("status", "failed");
			return json.toJSONString();
		}
	}
}