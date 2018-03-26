package bluemix.rest;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import bluemix.rest.model.AccessToken;
import bluemix.rest.model.Api;
import bluemix.rest.model.Api.ApiAction;
import bluemix.ui.ApiInputDialog;
import bluemix.ui.PreferenceConstants;
import bluemix.ui.model.UIAction;

public class CreateGetApi extends BaseAction {

private UIAction uiAction;

public void selectionChanged(IAction action, ISelection selection) {
		
		Object obj= ((IStructuredSelection)selection).getFirstElement();
		if(obj instanceof UIAction){
			uiAction = (UIAction)obj;
			action.setEnabled(true);
			return;
		}
		action.setEnabled(false);
	}
	@Override
		public void run(IAction arg0) {
			try {
				
				String name = uiAction.getAction().getName();
				ApiInputDialog input = new ApiInputDialog(shell);
				if(input.open() == Window.CANCEL){
					return;
				}
				Api api = input.getValue();
				String rspnsType= input.getResponseType();
				ApiAction apiAction = new ApiAction();
				
				String apiUrl = getBaseURL()+"web/_/default/"+name+".http";
				apiAction.setBackendUrl(apiUrl);
				String auth = getPreferenceStore().getString(PreferenceConstants.BLUEMIX_AUTH_KEY);
				apiAction.setBackendMethod((String)api.getApidoc().get(Api.KYE_GATEWAYMETHOD));
				apiAction.setAuthkey(auth);
				apiAction.setName(name);
				apiAction.setNamespace("_");
				api.setAction(apiAction);
				FetchAction fetchAction = new FetchAction();
				fetchAction.performGet(uiAction);
				Map<String,String> params = new HashMap<>();
				AccessToken accessToken = getAccessToken();
				params.put("accesstoken", accessToken.getAccess_token());
				params.put("responsetype",rspnsType );
				
				
				if(StringUtils.isEmpty(auth)){
					throw new Exception("Authkey not set in prefrence store");
				}
				String spaceguid= auth.substring(0,auth.indexOf(':'));
				params.put("spaceguid",spaceguid);
				String dataUrl = getDataString(params);
				String url =getBaseURL()+"web/whisk.system/apimgmt/createApi.http?"+dataUrl;
				HttpPost request = new HttpPost(url);
				updateAuthHeader(request);
				request.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
				
				Gson gson = new Gson();
				String body=gson.toJson(api);
				System.out.println("Requestbody: "+body);
				if (StringUtils.isNotBlank(body)) {
			        request.setEntity(new StringEntity(body, "utf-8"));
			    }
				
				try{
					HttpResponse response = httpCall(request);
					handleSuccess(response);
					
					
					}catch (Exception e) {
						e.printStackTrace();
						openError(shell, e);
						return;
					}
					
				 
				
			} catch (Exception e) {
				e.printStackTrace();
				openError(shell, e);
				return;
			}
			
		}
	@Override
		protected void handleSuccess(HttpResponse response) throws Exception {
		String json = EntityUtils.toString(response.getEntity());
		
		
		JsonElement jelement = new JsonParser().parse(json);
	    JsonObject  jobject = jelement.getAsJsonObject();
	    String base  = jobject.get("gwApiUrl").getAsString();
	     Set<Entry<String,JsonElement>> pairs= jobject.get("apidoc").getAsJsonObject().get("paths").getAsJsonObject().entrySet();
	     String segement = pairs.iterator().next().getKey();
	     
		MessageConsole myConsole = findConsole("IBM Functions excecution");
		MessageConsoleStream out = myConsole.newMessageStream();
		out.println("API Created with following URL!");
		out.println(base+segement);
		}
	
}
