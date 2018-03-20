package bluemix.rest;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Shell;

import com.google.gson.Gson;

import bluemix.core.BluemixActivator;
import bluemix.rest.model.AccessToken;
import bluemix.rest.model.UserInfo;
import bluemix.ui.PreferenceConstants;

public abstract class RestBase{


	protected void updateAuthHeader( HttpRequestBase request) {
		String auth = getPreferenceStore().getString(PreferenceConstants.BLUEMIX_AUTH_KEY);
		System.out.println("Auth: "+auth);
		byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(Charset.forName("ISO-8859-1")));
		String authHeader = "Basic " + new String(encodedAuth);
		
		request.setHeader(HttpHeaders.AUTHORIZATION, authHeader);
	}
	
	protected UserInfo getUserInfo(){
		UserInfo info = new UserInfo();
		info.setAuthkey(getPreferenceStore().getString(PreferenceConstants.BLUEMIX_AUTH_KEY));
		info.setPassword(getPreferenceStore().getString(PreferenceConstants.BLUEMIX_PASSWORD));
		info.setServer(getPreferenceStore().getString(PreferenceConstants.BLUEMIX_SERVER));
		info.setUser(getPreferenceStore().getString(PreferenceConstants.BLUEMIX_USER));
		return info;
	}

	protected IPreferenceStore getPreferenceStore() {
		return BluemixActivator.getDefault().getPreferenceStore();
	}
	
	protected String getBaseURL() {
		String server = getPreferenceStore().getString(PreferenceConstants.BLUEMIX_SERVER);
		 if (server == null || server.isEmpty()) {
				throw new RuntimeException("Server name is empty.");
			}
		return "https://" + server + "/api/v1/";
	}
	
	protected HttpResponse httpCall(HttpRequestBase request) {
		HttpClient client = HttpClientBuilder.create().build();
		HttpResponse response;
		try {
		response = client.execute(request);
		int code = response.getStatusLine().getStatusCode();
		if ( code == HttpStatus.SC_OK  || code == HttpStatus.SC_ACCEPTED) {
			return response;
		}else{
			String json = EntityUtils.toString(response.getEntity());
			throw new RuntimeException("Reason "+json);
		} 

		} catch (IOException e) {
			throw new RuntimeException("Reason : "+e.getMessage());
		}
	}


	protected AccessToken getAccessToken() throws Exception {
		
		String user = getPreferenceStore().getString(PreferenceConstants.BLUEMIX_USER);
		String password =getPreferenceStore().getString(PreferenceConstants.BLUEMIX_PASSWORD);
		if(StringUtils.isEmpty(user) || StringUtils.isEmpty(user)){
			ErrorDialog.openError(new Shell(), "HTTP Error", "User name or password not set in prerfrence store. Please set username and password in prefrence page", Status.CANCEL_STATUS);
			return null;
		}
		
		String url = "https://login.ng.bluemix.net/UAALoginServerWAR/oauth/token";
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("grant_type", "password"));
		params.add(new BasicNameValuePair("username", user));
		params.add(new BasicNameValuePair("password", password));
		
		HttpPost request = new HttpPost(url);
//		byte[] encodedAuth = Base64.getEncoder().encode("Y2Y6".getBytes(Charset.forName("ISO-8859-1")));
		String authHeader = "Basic Y2Y6";
		request.setHeader(HttpHeaders.AUTHORIZATION, authHeader);
//		updateAuthHeader(request);
		request.setHeader(HttpHeaders.CONTENT_TYPE,"application/x-www-form-urlencoded"); 
		request.setEntity(new UrlEncodedFormEntity(params));
		HttpResponse response= httpCall(request);
		
		String json = EntityUtils.toString(response.getEntity());
		Gson gson = new Gson();
		return gson.fromJson(json, AccessToken.class);
		
	}
	
	protected String getDataString(Map<String, String> params) throws UnsupportedEncodingException{
	    StringBuilder result = new StringBuilder();
	    boolean first = true;
	    for(Map.Entry<String, String> entry : params.entrySet()){
	        if (first)
	            first = false;
	        else
	            result.append("&");    
	        result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
	        result.append("=");
	        result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
	    }    
	    return result.toString();
	}
	
	
}
