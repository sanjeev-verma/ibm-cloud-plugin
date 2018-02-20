package bluemix.rest;

import java.nio.charset.Charset;
import java.util.Base64;

import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.HttpRequestBase;
import org.eclipse.jface.preference.IPreferenceStore;

import bluemix.core.BluemixActivator;
import bluemix.rest.model.UserInfo;
import bluemix.ui.PreferenceConstants;

public abstract class RestBase {

	protected void updateAuthHeader(String auth, HttpRequestBase request) {
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
	
	
}
