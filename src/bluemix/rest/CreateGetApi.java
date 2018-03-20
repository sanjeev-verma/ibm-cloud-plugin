package bluemix.rest;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;

import com.google.gson.Gson;

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
				ApiAction apiAction = new ApiAction();
				api.setAction(apiAction);
				String apiUrl = getBaseURL()+"web/_/default/"+name+".http";
				apiAction.setBackendUrl(apiUrl);
				String auth = getPreferenceStore().getString(PreferenceConstants.BLUEMIX_AUTH_KEY);
				apiAction.setBackendMethod(api.getApidoc().get(Api.KYE_GATEWAYMETHOD));
				apiAction.setAuthkey(auth);
				apiAction.setName(name);
				apiAction.setNamespace("_");
				FetchAction fetchAction = new FetchAction();
				fetchAction.performGet(uiAction);
				Map<String,String> params = new HashMap<>();
				AccessToken accessToken = getAccessToken();
				params.put("accessToken", accessToken.getAccess_token());
				params.put("requestType","http" );
				
				
				if(StringUtils.isEmpty(auth)){
					throw new Exception("Authkey not set in prefrence store");
				}
				String spaceguid= auth.substring(0,auth.indexOf(':'));
				params.put("spaceguid",spaceguid);
				String dataUrl = getDataString(params);
				String url =getBaseURL()+"web/whisk.system/apimgmt/createApi.http?"+dataUrl;
				System.out.println("URL:"+url);
				HttpPost request = new HttpPost(url);
				updateAuthHeader(request);
				request.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
				
				Gson gson = new Gson();
				String body=gson.toJson(api);
				System.out.println(body);
				System.out.println("Requestbody: "+body);
				if (StringUtils.isNotBlank(body)) {
			        request.setEntity(new StringEntity(body, "utf-8"));
			    }
				
				try{
					httpCall(request);
					}catch (Exception e) {
						e.printStackTrace();
						openError(shell, e);
						return;
					}
					
					 MessageBox dialog =new MessageBox(shell,SWT.ICON_INFORMATION| SWT.OK);
					 dialog.setText("Success");
					 dialog.setMessage("Operation successfull!");
					 dialog.open();
				
			} catch (Exception e) {
				e.printStackTrace();
				openError(shell, e);
				return;
			}
			
		}
	
}
