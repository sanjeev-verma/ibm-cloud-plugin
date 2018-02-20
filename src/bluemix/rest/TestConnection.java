package bluemix.rest;

import java.io.IOException;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

import bluemix.rest.model.UserInfo;


public class TestConnection extends RestBase{

	public boolean connect(){
		UserInfo info = getUserInfo();
		String auth = info.getAuthkey();
		if(auth == null || auth.isEmpty()){
			throw new RuntimeException("API key is empty.");
		}else if(info.getServer() == null || info.getServer().isEmpty()){
			throw new RuntimeException("Server name is empty.");
		}
		HttpGet request = new HttpGet("https://"+info.getServer()+"/api/v1/namespaces/whisk.system/packages");
		updateAuthHeader(auth, request);

		HttpClient client = HttpClientBuilder.create().build();
		HttpResponse response;
		try {
			response = client.execute(request);

			response.getStatusLine().getStatusCode();
			if(response.getStatusLine().getStatusCode() != HttpStatus.SC_OK){
				throw new RuntimeException("API Key or Server name not correct. Error code "+response.getStatusLine().getStatusCode());
				
			}
			
		} catch (IOException e) {
			throw new RuntimeException("Internal error. "+e.getMessage());
		}
		return true;
		
	}

	
}
/**
 * inorder to verify the username and password use following
 
 https://login.ng.bluemix.net/UAALoginServerWAR/oauth/token
 Raw body:    grant_type=password&username=bonkilep@gmail.com&password=Jaybonkile_1234
 
 Content-Type: application/x-www-form-urlencoded
 Accept: application/json;charset=utf-8
 Authorization: Basic Y2Y6
 
 */

