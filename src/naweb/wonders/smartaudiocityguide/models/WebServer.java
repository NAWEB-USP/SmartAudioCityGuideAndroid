package naweb.wonders.smartaudiocityguide.models;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

public class WebServer {
	private static String baseUrl = "http://smartaudiocityguide.azurewebsites.net/";
	private static String endPoint = "http://smartaudiocityguide.azurewebsites.net/WebServices.asmx";

	public static String getBaseUrl() {
		return baseUrl;
	}

	public static String getEndPoint() {
		return endPoint;
	}

	public List<Location> requestLocationsAround(Location location) {
		String locationValue;

		try {
			locationValue = URLEncoder.encode(location.toJSON().toString(),
					"UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();

			return null;
		}

		String url = baseUrl + "LocationsWebServices/searchLocations?location="
				+ locationValue + "&radius=2&windowsPhoneId=" + "12345"
				+ "&code=wonders";

		String json = makeRequest(url);

		return Location.jsonToLocationsList(json);
	}

	private String makeRequest(final String url) {
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(url);

		try {
			HttpResponse response = httpClient.execute(httpGet);
			HttpEntity entity = response.getEntity();

			if (entity != null) {
				InputStream instream = entity.getContent();
				String result = convertStreamToString(instream);

				instream.close();

				return result;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	private static String convertStreamToString(InputStream is) {
		/*
		 * To convert the InputStream to String we use the
		 * BufferedReader.readLine() method. We iterate until the BufferedReader
		 * return null which means there's no more data to read. Each line will
		 * appended to a StringBuilder and returned as String.
		 */
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}
}
