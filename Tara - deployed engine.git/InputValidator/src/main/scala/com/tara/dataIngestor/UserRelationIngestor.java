package com.tara.dataIngestor;

import io.prediction.APIResponse;
import io.prediction.Event;
import io.prediction.EventClient;
import io.prediction.FutureAPIResponse;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.joda.time.DateTime;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

public class UserRelationIngestor {

	private static final int HTTP_CREATED = 201;
	private static BufferedReader reader;

	public static void main(String[] args) {

		String appurl = args[0];

		String accessKey = args[1];
		String inputFile = args[2];
		String fileSplitter = args[3];

		EventClient client = null;
		Reader fileReader = null;

		List<FutureAPIResponse> listOfFutures = new ArrayList<>();

		try {
			/* Create a client with the access key */
			client = new EventClient(accessKey, appurl);

			/* Get API status */
			System.out.println(client.getStatus());

			/* Open data file for reading */
			fileReader = new FileReader(inputFile);
			reader = new BufferedReader(fileReader);

			/* Some local variables */
			String line;
			FutureAPIResponse future;

			String header[] = reader.readLine().split(fileSplitter);

			while ((line = reader.readLine()) != null) {

				String[] splits = line.split(fileSplitter);

				for (int j = 0; j < splits.length; j++) {
					splits[j] = splits[j].replaceAll("\"", "");
					splits[j] = splits[j].replaceAll("'", "");
				}

				String userId = splits[3];
				String relatedUserId = splits[11];
				Map<String, Object> eventProperties = new HashMap<>();

				for (int j = 0; j < splits.length; j++) {
					eventProperties.put(header[j], splits[j]);
				}

				Event projectTakenEvent = new Event().event("relatedTo")
						.entityType("user").entityId(userId)
						.targetEntityType("item").targetEntityId(relatedUserId)
						.properties(eventProperties).eventTime(new DateTime());

				future = client.createEventAsFuture(projectTakenEvent);
				listOfFutures.add(future);
				Futures.addCallback(
						future.getAPIResponse(),
						getFutureCallback("user " + userId + " related to " + relatedUserId));

			}
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
			e.printStackTrace();
		} finally {
			if (fileReader != null) {
				try {
					fileReader.close();
				} catch (IOException e) {
					System.err.println("Error: " + e.getMessage());
				}
			}
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
			} catch (InterruptedException | ExecutionException e) {
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
