package com.tara.common;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;

public class HitApi {

	public static String callService(String serviceUrl) {
		String output = "false";
		try {
			serviceUrl = serviceUrl.replace("%2F", "kaabCustomSlash");
			serviceUrl = serviceUrl.replace("%5C", "kaabCustomBackSlash");
			URL url = new URL(serviceUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");

			BufferedReader rd = new BufferedReader(new InputStreamReader(
					conn.getInputStream()));
			StringBuffer result = new StringBuffer();
			String line = "";
			while ((line = rd.readLine()) != null) {
				result.append(line);
			}

			System.out.println(serviceUrl + "kaab" + conn.getResponseCode()
					+ "kaab" + result);
			System.out.println();
			// System.out.println(result);
			output = result.toString();
		} catch (Exception e) {
			e.printStackTrace();
			output = "false";
		}
		return output;
	}

	public static void main(String[] args) throws UnsupportedEncodingException {
		String url = "asd / /asdsad%7&";
		url = URLEncoder.encode(url, "UTF-8").replaceAll("\\+", "%20");
		System.out.println(URLDecoder.decode(url, "UTF-8"));
		System.out.println(url);

	}
}
