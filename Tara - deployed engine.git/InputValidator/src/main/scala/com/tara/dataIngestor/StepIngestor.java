package com.tara.dataIngestor;

import io.prediction.APIResponse;
import io.prediction.Event;
import io.prediction.EventClient;
import io.prediction.FutureAPIResponse;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.joda.time.DateTime;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.tara.common.ApiEndpoints;
import com.tara.common.FuzzyHelper;
import com.tara.common.HitApi;
import com.tara.dtos.Course;

public class StepIngestor {

	private static final int HTTP_CREATED = 201;

	private static String generateCourseTakenBeforeRequest(String tomcatPrefix,
			String userId, String courseId) {
		try {
			userId = URLEncoder.encode(userId, "UTF-8")
					.replaceAll("\\+", "%20");
			courseId = URLEncoder.encode(courseId, "UTF-8").replaceAll("\\+",
					"%20");

		} catch (Exception exp) {
			exp.printStackTrace();
		}
		return HitApi.callService(tomcatPrefix
				+ ApiEndpoints.COURSE_TAKEN_BEFORE_ENDPOINT + userId + "/"
				+ courseId);

	}

	private static String generateCourseInsertRequest(String tomcatPrefix,
			String id, String title) {
		try {
			id = URLEncoder.encode(id, "UTF-8").replaceAll("\\+", "%20");
			title = URLEncoder.encode(title, "UTF-8").replaceAll("\\+", "%20");

		} catch (Exception exp) {
			exp.printStackTrace();
		}
		return HitApi.callService(tomcatPrefix
				+ ApiEndpoints.COURSE_INSERT_ENDPOINT + id + "/" + title);

	}

	private static String generateCourseUpdateRequest(String tomcatPrefix,
			String id, String oldTitle, String newTitle) {
		try {
			id = URLEncoder.encode(id, "UTF-8").replaceAll("\\+", "%20");
			oldTitle = URLEncoder.encode(oldTitle, "UTF-8").replaceAll("\\+",
					"%20");
			newTitle = URLEncoder.encode(newTitle, "UTF-8").replaceAll("\\+",
					"%20");

		} catch (Exception exp) {
			exp.printStackTrace();
		}
		return HitApi.callService(tomcatPrefix
				+ ApiEndpoints.COURSE_UPDATE_ENDPOINT + id + "/" + oldTitle
				+ "/" + newTitle);

	}

	public static void main(String[] args) throws IOException {
		Authenticator.setDefault(new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication("tara.shared", "taraorange"
						.toCharArray());
			}
		});
		for (int i = 0; i < args.length; i++) {
			System.out.println(i + "," + args[i]);
		}

		String appurl = args[0];
		String accessKey = args[1];
		String inputFile = args[2];
		String lineSplitter = args[3];
		String recordSplitter = args[4];
		int inputLength = Integer.parseInt(args[5]);
		String oldDataPath = args[6];
		String oldDataSplitter = args[7];
		String newDataPath = args[8];
		int fuzzyScoreThreshold = Integer.parseInt(args[9]);
		Boolean updateDb = Boolean.parseBoolean(args[10]);
		String tomcatPrefix = args[11];
		String line = null;

		int updatedCount = 0;
		if (oldDataSplitter.equals("null")) {
			oldDataSplitter = "@#";
		}

		FuzzyHelper fuzzyHelper = new FuzzyHelper();
		Map<String, Course> oldCourses = new HashMap<>();
		// ArrayList<Course> oldCourses = new ArrayList<Course>();
		if (!oldDataPath.equals("null")) {
			BufferedReader reader = new BufferedReader(new FileReader(
					oldDataPath));
			while ((line = reader.readLine()) != null) {
				String[] splits = line.split(oldDataSplitter);
				Course course = new Course().withId(splits[0]).withTitle(
						splits[1]);
				oldCourses.put(course.title, course);
				// oldCourses.add(course);
			}
			reader.close();
		}
		Map<String, Course> newCourses = new HashMap<>();
		// ArrayList<Course> newCourses = new ArrayList<>();
		/*
		 * for (Course oldCourse : oldCourses) { newCourses.add(oldCourse); }
		 */
		for (String title : oldCourses.keySet()) {
			newCourses.put(title, oldCourses.get(title));
		}
		int corrupted = 0;
		int total = 0;
		int totalCourses = 0;

		EventClient client = null;

		List<FutureAPIResponse> listOfFutures = new ArrayList<FutureAPIResponse>();

		try {
			/* Create a client with the access key */
			client = new EventClient(accessKey, appurl);

			Set<String> courseTitles = new TreeSet<String>();

			/* Get API status */
			System.out.println(client.getStatus());

			File file = new File(inputFile);
			FileInputStream fis = new FileInputStream(file);
			byte[] data = new byte[(int) file.length()];
			fis.read(data);
			fis.close();

			String fileContents = new String(data, "UTF-8");
			String[] lines = fileContents.split(lineSplitter);

			FutureAPIResponse future;
			for (int i = 1; i < lines.length; i++) {
				line = lines[i];
				String[] splits = line.split(recordSplitter);
				if (splits.length != inputLength) {
					corrupted++;
				} else {

					for (int j = 0; j < splits.length; j++) {
						splits[j] = splits[j].replaceAll("\"", "");
						splits[j] = splits[j].replaceAll("'", "");
					}

					String empId = splits[3];

					String courseId = splits[12];
					if (courseId != null && !courseId.equals("")
							&& splits[11] != null && !splits[11].equals("")
							&& courseTitles.add(splits[11])) {
						totalCourses++;
						Course newCourse = new Course().withId(courseId);
						newCourse.withTitle(splits[11]);
						//no description column so taking category as description to get better results
						if (!(splits[13] == null && splits[13].equals("")))
							newCourse.withDescription(splits[13]);

						Course foundCourse = null;

						for (String oldCourseTitle : oldCourses.keySet()) {
							Course oldCourse = oldCourses.get(oldCourseTitle);
							if (fuzzyHelper.fuzzyScore(oldCourse.title,
									newCourse.title) >= fuzzyScoreThreshold
									&& !oldCourses.containsKey(newCourse.title)) {
								foundCourse = new Course(oldCourse);
								break;
							}
						}
						if (foundCourse != null) {
							updatedCount++;
							Event courseDeleteEvent = new Event()
									.event("$delete").entityType("item")
									.entityId(foundCourse.id);
							future = client
									.createEventAsFuture(courseDeleteEvent);
							listOfFutures.add(future);
							newCourses.remove(foundCourse.title);

							if (updateDb)
								/*
								 * mySqlConnector.updateCourse(newCourse.id,
								 * foundCourse.title, newCourse.title);
								 */
								System.out.println(generateCourseUpdateRequest(
										tomcatPrefix, newCourse.id,
										foundCourse.title, newCourse.title));

						} else {
							if (updateDb)
								/*
								 * mySqlConnector.insertCourse(newCourse.id,
								 * newCourse.title);
								 */
								System.out.println(generateCourseInsertRequest(
										tomcatPrefix, newCourse.id,
										newCourse.title));
						}

						// newCourses.add(newCourse);
						newCourses.put(newCourse.title, newCourse);
						Map<String, Object> courseProperties = newCourse
								.getPropertiesMap();
						Event courseAddEvent = new Event().event("$set")
								.entityType("item").entityId(newCourse.id)
								.properties(courseProperties);
						future = client.createEventAsFuture(courseAddEvent);
						listOfFutures.add(future);

						Futures.addCallback(future.getAPIResponse(),
								getFutureCallback("course " + courseId));
					}
					System.out.println(generateCourseTakenBeforeRequest(
							tomcatPrefix, empId, courseId));
					System.out.println("inserting courseTaken");
					Event courseTakenEvent = new Event().event("courseTaken")
							.entityType("user").entityId(empId)
							.targetEntityType("item").targetEntityId(courseId)
							.properties(new HashMap<>())
							.eventTime(new DateTime());

					future = client.createEventAsFuture(courseTakenEvent);
					listOfFutures.add(future);
					Futures.addCallback(future.getAPIResponse(),
							getFutureCallback("event " + empId + " taken "
									+ courseId));
				}
				total++;
			}
			System.out.println("total count=" + total);
			System.out.println("corrupt count=" + corrupted);
			System.out.println("updated courses count=" + updatedCount);
			System.out.println("total courses count=" + totalCourses);

			File fileOutput = new File(newDataPath);
			fileOutput.delete();

			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(newDataPath), "UTF-8"));
			for (String courseTitle : newCourses.keySet()) {
				writer.write(newCourses.get(courseTitle).toString(
						oldDataSplitter));
				writer.newLine();
			}
			writer.close();

		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
			e.printStackTrace();
		} finally {

			// wait for the import result
			ListenableFuture<List<APIResponse>> futures = Futures
					.allAsList(listOfFutures);
			try {
				List<APIResponse> responses = futures.get();
				for (APIResponse response : responses) {
					if (response.getStatus() != HTTP_CREATED) {
						System.err
								.println("Error importing some record, first error message is: "
										+ response.getMessage());
						// only print the first error
						break;
					}
				}
			} catch (Exception e) {
				System.err
						.println("Error importing some record, error message: "
								+ e.getStackTrace());
			}
			if (client != null) {
				client.close();
			}
		}
	}

	private static FutureCallback<APIResponse> getFutureCallback(
			final String name) {
		return new FutureCallback<APIResponse>() {
			public void onSuccess(APIResponse response) {
				System.out.println(name + " added: " + response.getMessage());
			}

			public void onFailure(Throwable thrown) {
				System.out.println("failed to add " + name + ": "
						+ thrown.getMessage());
			}
		};
	}

}
