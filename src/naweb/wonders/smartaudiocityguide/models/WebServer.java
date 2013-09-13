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
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class WebServer {
	private static String baseUrl = "http://smartaudiocityguide.azurewebsites.net/";
	private static String soapAddress = "http://smartaudiocityguide.azurewebsites.net/WebServices.asmx";
	private static String namespace = "http://tempuri.org/";
	private static String code = "wonders";

	public byte[] requestSoundCommentFromLocation(Location location) {
		String action = "getSoundCommentFromLocation";

		SoapObject soapObject = new SoapObject(namespace, action);

		PropertyInfo propertyInfo = new PropertyInfo();
		propertyInfo.setName("idLocation");
		propertyInfo.setValue(location.getId());
		propertyInfo.setType(String.class);
		soapObject.addProperty(propertyInfo);

		propertyInfo = new PropertyInfo();
		propertyInfo.setName("code");
		propertyInfo.setValue(code);
		propertyInfo.setType(String.class);
		soapObject.addProperty(propertyInfo);

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		envelope.dotNet = true;
		envelope.setOutputSoapObject(soapObject);

		HttpTransportSE httpTransport = new HttpTransportSE(soapAddress);

		try {
			httpTransport.call(namespace + action, envelope);
			SoapPrimitive result = (SoapPrimitive) envelope.getResponse();

			String json = result.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
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
				+ "&code=" + code;

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
