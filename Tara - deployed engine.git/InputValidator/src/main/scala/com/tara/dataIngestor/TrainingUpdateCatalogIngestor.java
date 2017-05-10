package com.tara.dataIngestor;

import java.io.File;
import java.io.FileInputStream;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URLEncoder;

import com.tara.common.ApiEndpoints;
import com.tara.common.HitApi;

public class TrainingUpdateCatalogIngestor {
	private static String generateLookupCourseInsertRequest(
			String tomcatPrefix, String id) {
		try {
			id = URLEncoder.encode(id, "UTF-8").replaceAll("\\+", "%20");
		} catch (Exception exp) {
			exp.printStackTrace();
		}
		return HitApi.callService(tomcatPrefix
				+ ApiEndpoints.LOOKUP_COURSE_INSERT_ENDPOINT + id);

	}

	public static void main(String[] args) {

		Authenticator.setDefault(new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication("tara.shared", "taraorange"
						.toCharArray());
			}
		});
		for (int i = 0; i < args.length; i++) {
			System.out.println(i + "," + args[i]);
		}

		int insertedCount = 0;
		String inputFile = args[0];
		String lineSplitter = args[1];
		String recordSplitter = args[2];
		String tomcatPrefix = args[3];
		String line = null;
		try {
			File file = new File(inputFile);
			FileInputStream fis = new FileInputStream(file);
			byte[] data = new byte[(int) file.length()];
			fis.read(data);
			fis.close();

			String fileContents = new String(data, "UTF-8");
			String[] lines = fileContents.split(lineSplitter);

			for (int i = 1; i < lines.length; i++) {
				line = lines[i];
				String[] splits = line.split(recordSplitter);
				generateLookupCourseInsertRequest(tomcatPrefix, splits[5]);
				insertedCount++;
			}
			System.out.println(insertedCount
					+ " number of courses inserted please check in db.....");
		} catch (Exception exp) {

		}

	}
}