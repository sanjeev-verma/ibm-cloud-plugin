package bluemix.rest;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Base64;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.HttpClientBuilder;
import org.eclipse.jface.preference.IPreferenceStore;

import bluemix.core.BluemixActivator;
import bluemix.rest.model.UserInfo;
import bluemix.ui.PreferenceConstants;

public abstract class RestBase{


	protected void updateAuthHeader( HttpRequestBase request) {
		String auth = getPreferenceStore().getString(PreferenceConstants.BLUEMIX_AUTH_KEY);
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
			throw new RuntimeException("Reason Code: "+code);
		}

		} catch (IOException e) {
			throw new RuntimeException("Reason : "+e.getMessage());
		}
	}


	
}
