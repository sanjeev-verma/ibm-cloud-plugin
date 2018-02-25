package bluemix.rest;

import java.io.IOException;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.IActionDelegate;

import com.google.gson.Gson;

import bluemix.rest.model.Action;
import bluemix.rest.model.Exec;
import bluemix.ui.PreferenceConstants;

public class CreateAction extends BaseAction {

	
	
	/**
	 * Constructor for Action1.
	 */
	public CreateAction() {
		super();
	}


	/**
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction action) {
		
		super.run(action);
		
		String fullName = file.getName();
		String name = fullName.substring(0, fullName.indexOf('.'));
		Action act = new Action();
		act.setName(name);
		
//		act.setNamespace("_");// current namespace
		Exec exec = new Exec();
		String code;
		try {
			code = IOUtils.toString(file.getContents());
			exec.setCode(code);
			act.setExec(exec);
		
		} catch (IOException | CoreException e) {
			e.printStackTrace();
		} 
		String url =getBaseURL()+"namespaces/"+act.getNamespace()+"/actions/"+act.getName()+"?overwrite="+isOverwrite();
		System.out.println("URL:"+url);
		HttpPut request = new HttpPut(url);
		updateAuthHeader(request);
		request.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
		
		
		Gson gson = new Gson();
		String body=gson.toJson(act);;
		System.out.println("Requestbody: "+body);
		if (StringUtils.isNotBlank(body)) {
	        request.setEntity(new StringEntity(body, "utf-8"));
	    }

		try{
		httpCall(request);
		}catch (Exception e) {
			openError(shell, e);
			return;
		}
		
		 MessageBox dialog =new MessageBox(shell,SWT.ICON_INFORMATION| SWT.OK);
		 dialog.setText("Success");
		 dialog.setMessage("Operation successfull!");
		 dialog.open();

	}





	protected String isOverwrite() {
		return "false";
	}


	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	

}

