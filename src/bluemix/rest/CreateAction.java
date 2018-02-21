package bluemix.rest;

import java.io.IOException;

import javax.management.RuntimeErrorException;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import com.google.gson.Gson;

import bluemix.rest.model.Action;
import bluemix.rest.model.Exec;
import bluemix.ui.PreferenceConstants;

public class CreateAction extends RestBase implements IObjectActionDelegate {

	private Shell shell;
	private IFile file;
	
	/**
	 * Constructor for Action1.
	 */
	public CreateAction() {
		super();
	}

	/**
	 * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		shell = targetPart.getSite().getShell();
	}

	/**
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction action) {
		String fullName = file.getName();
		String name = fullName.substring(0, fullName.indexOf('.'));
		Action act = new Action();
		act.setName(name);
		String currentNS = getPreferenceStore().getString(PreferenceConstants.BLUEMIX_NAMESPACES);
		if(currentNS == null || currentNS.isEmpty()){
			openError(new RuntimeException("No current Namespace set."));
			return;
		}
		if(!currentNS.equals(file.getParent().getName())){
			openError(new RuntimeException("Current Namespace is not matching with file namespace(folder name)."));
			return;
		}
		act.setNamespace("_");// current namespace
		Exec exec = new Exec();
		String code;
		try {
			code = IOUtils.toString(file.getContents());
			exec.setCode(code);
			act.setExec(exec);
		
		} catch (IOException | CoreException e) {
			e.printStackTrace();
		} 
		
		
		String auth = getPreferenceStore().getString(PreferenceConstants.BLUEMIX_AUTH_KEY);
		
		if (auth == null || auth.isEmpty()) {
			throw new RuntimeException("API key is empty.");
		} 
		String url =getBaseURL()+"namespaces/"+act.getNamespace()+"/actions/"+act.getName()+"?overwrite="+isOverwrite();
		System.out.println("URL:"+url);
		HttpPut request = new HttpPut(url);
		updateAuthHeader(auth, request);
		request.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
		
		
		Gson gson = new Gson();
		String body=gson.toJson(act);;
		System.out.println("Requestbody: "+body);
		if (StringUtils.isNotBlank(body)) {
	        request.setEntity(new StringEntity(body, "utf-8"));
	    }

		HttpClient client = HttpClientBuilder.create().build();
		HttpResponse response;
		try {
			response = client.execute(request);
		if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
			openError(new RuntimeException("Reason Code: "+response.getStatusLine().getStatusCode()));
			
		}

		} catch (IOException e) {
			openError(e);
		}
		
		 MessageBox dialog =new MessageBox(shell,SWT.ICON_INFORMATION| SWT.OK);
		 dialog.setText("Success");
		 dialog.setMessage("Operation successfull!");
		 dialog.open();

	}

	private void openError(Exception ex){
		 MessageBox dialog =new MessageBox(shell,SWT.ICON_ERROR| SWT.OK);
		 dialog.setText("Operation Failed");
		 dialog.setMessage("Operation failed: "+ex.getMessage());
		 dialog.open();

	}
	protected String isOverwrite() {
		return "false";
	}

	protected String getBaseURL() {
		String server = getPreferenceStore().getString(PreferenceConstants.BLUEMIX_SERVER);
		 if (server == null || server.isEmpty()) {
				throw new RuntimeException("Server name is empty.");
			}
		return "https://" + server + "/api/v1/";
	}

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
			IResource res = (IResource) ((IStructuredSelection)selection).getFirstElement();
			if(res instanceof IFile){
				file = (IFile)res;
				action.setEnabled("JS".equals(file.getFileExtension().toUpperCase()));
				return;
			}
		action.setEnabled(false);
	}
	

}

