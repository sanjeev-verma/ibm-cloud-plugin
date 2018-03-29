package bluemix.rest;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate;

import com.google.gson.Gson;

import bluemix.rest.model.Action;
import bluemix.rest.model.Annotation;
import bluemix.rest.model.Exec;

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
		
		String name = getActionName();
		Action act = new Action();
		act.setName(name);
		
		act.setNamespace("_");// current namespace
		Exec exec = new Exec();
		String code;
		try {
			code = IOUtils.toString(file.getContents());
			exec.setCode(code);
			if("py".equalsIgnoreCase(file.getFileExtension()))
				exec.setKind(Exec.KIND_PYTHON);
			act.setExec(exec);
		
		
		} catch (IOException | CoreException e) {
			e.printStackTrace();
		} 
		
		
		updateWebActionAnnotations(act);
		
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
		HttpResponse response = httpCall(request);
		handleSuccess(response);
		}catch (Exception e) {
			openError(shell, e);
			return;
		}

	}


	protected String getActionName() {
		String fullName = file.getName();
		String name = fullName.substring(0, fullName.indexOf('.'));
		return name;
	}





	private void updateWebActionAnnotations(Action act) {
		
		MessageBox messageBox = new MessageBox(new Shell(), SWT.ICON_QUESTION | SWT.YES | SWT.NO);
        messageBox.setMessage("Do you want make this action as web-Action so that it can be exposed as Web API?");
        messageBox.setText("Web API Action");
        int response = messageBox.open();
        if (response == SWT.NO)
          return;
		
       Annotation a1 = new Annotation();
       a1.setKey("web-export");
       a1.setValue(true);
       act.getAnnotations().add(a1);
       
       Annotation a2 = new Annotation();
       a2.setKey("raw-http");
       a2.setValue(false);
       act.getAnnotations().add(a2);
       
       Annotation a3 = new Annotation();
       a3.setKey("final");
       a3.setValue(false);
       act.getAnnotations().add(a3);
       
	}


	protected String isOverwrite() {
		return "false";
	}


	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	

}

