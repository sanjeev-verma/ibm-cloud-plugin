package bluemix.rest.model;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;

public class Api{

	private Map<String, Object> apidoc = new HashMap<String, Object>();
	
	
	public static final String  KYE_NAMESPACE= "namespace";
	public static final String  KYE_API_NAME = "apiName";
	public static final String  KYE_GATEWAYBASEPATH = "gatewayBasePath";
	public static final String  KYE_GATEWAYPATH = "gatewayPath";
	public static final String  KYE_GATEWAYMETHOD = "gatewayMethod";
	public static final String  KYE_ID = "id"; 
	public static final String  KYE_API ="API";
	public static final String  KYE_ACTION ="action";
	
	public Map<String, Object> getApidoc() {
		return apidoc;
	}
	public void setApidoc(Map<String, Object> apiDoc) {
		this.apidoc = apiDoc;
	}
	
	
	public void setAction(ApiAction action) {
		Gson g = new Gson();
		apidoc.put(KYE_ACTION, action);
	}
	
	
	public static class ApiAction{
		private String name;
		private String namespace;
		private String backendMethod;
		private String backendUrl;
		private String authkey;
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getNamespace() {
			return namespace;
		}
		public void setNamespace(String namespace) {
			this.namespace = namespace;
		}
		public String getBackendMethod() {
			return backendMethod;
		}
		public void setBackendMethod(String backendMethod) {
			this.backendMethod = backendMethod;
		}
		public String getBackendUrl() {
			return backendUrl;
		}
		public void setBackendUrl(String backendUrl) {
			this.backendUrl = backendUrl;
		}
		
		
		public String getAuthkey() {
			return authkey;
		}
		
		public void setAuthkey(String authkey) {
			this.authkey = authkey;
		}
		
	}
	
}
