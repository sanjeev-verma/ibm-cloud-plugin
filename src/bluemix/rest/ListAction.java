package bluemix.rest;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Base64;
import java.util.List;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import bluemix.rest.model.Action;

public class ListAction extends RestBase {
//[GET]	https://openwhisk.eu-gb.bluemix.net/api/v1/namespaces/_/actions?limit=30&skip=0


	public List<Action> performGetList(String namespace){
		String url = getBaseURL();
		HttpGet request = new HttpGet(url+"namespaces/_/actions?limit=30&skip=0");
		
		
		byte[] encodedAuth = Base64.getEncoder().encode(namespace.getBytes(Charset.forName("ISO-8859-1")));
		String authHeader = "Basic " + new String(encodedAuth);
		request.setHeader(HttpHeaders.AUTHORIZATION, authHeader);
		
		HttpClient client = HttpClientBuilder.create().build();
		HttpResponse response;
		try {
			response = client.execute(request);

			if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
				throw new RuntimeException(
						"Internal server error " + response.getStatusLine().getStatusCode());
			}
			String json = EntityUtils.toString(response.getEntity());
			System.out.println("json:"+ json);
			Gson gson = new Gson();
			TypeToken<List<Action>> token = new TypeToken<List<Action>>() {};
			List<Action> actions = gson.fromJson(json, token.getType());

			return actions;
		} catch (IOException e) {
			throw new RuntimeException("Internal error. " + e.getMessage());
		}

		
	}
}
