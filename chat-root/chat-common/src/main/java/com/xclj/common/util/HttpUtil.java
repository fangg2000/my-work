package com.xclj.common.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpUtil {

	public static String getData(String path) {
		try {
			URL url = new URL(path);
			HttpURLConnection openConnection = (HttpURLConnection) url
					.openConnection();
			openConnection.setConnectTimeout(5000);
			openConnection.setReadTimeout(5000);
			openConnection.setRequestProperty("contentType", "gbk");
			openConnection.setRequestMethod("GET");
			int responseCode = openConnection.getResponseCode();
			if (responseCode == 200) {
				InputStream inputStream = openConnection.getInputStream();
				BufferedReader in = new BufferedReader(new InputStreamReader(inputStream, "gbk"));
			    StringBuffer buffer = new StringBuffer();
			    String line = "";
			    while ((line = in.readLine()) != null){
			      buffer.append(line);
			    }
				return buffer.toString();
			}
		} catch (IOException e) {
			//e.printStackTrace();
		}
		return null;
	}
}
