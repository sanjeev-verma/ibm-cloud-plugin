package bluemix.rest;

import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.eclipse.jface.action.IAction;

public class UpdateAction extends CreateAction {

	
	/**
	 * Constructor for Action1.
	 */
	public UpdateAction() {
		super();
	}

	@Override
	public void run(IAction action) {
		String name = getActionName();
		String url = getBaseURL()+"namespaces/_/actions/"+name;
		HttpGet request = new HttpGet(url);
		updateAuthHeader(request);
		request.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
		try{
			httpCall(request);
			super.run(action);	
			}catch (Exception e) {
				openError(shell, e);
				return;
			}
		
	}
	@Override
	protected String isOverwrite() {
		return "true";
	}
}

