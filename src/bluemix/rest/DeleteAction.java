package bluemix.rest;

import java.nio.charset.Charset;
import java.util.Base64;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.util.EntityUtils;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import bluemix.ui.ServerView;
import bluemix.ui.model.UIAction;
import bluemix.ui.model.UINamespace;

public class DeleteAction extends BaseAction {

	private UIAction uiAction;

	public void run(IAction arg0) {
			
			if(!MessageDialog.openConfirm(shell, "Delete Action", "Are you sure you want to delete selected action from server?"))
				return;
			try{
			//DELETE]	https://openwhisk.eu-gb.bluemix.net/api/v1/namespaces/_/actions/new_file
			String url = getBaseURL()+"namespaces/_/actions/"+uiAction.getAction().getName();
			System.out.println("URL  "+url);
			HttpDelete request = new HttpDelete(url);

			byte[] encodedAuth = Base64.getEncoder().encode(uiAction.getParent().getApiKey().getBytes(Charset.forName("ISO-8859-1")));
			String authHeader = "Basic " + new String(encodedAuth);
			request.setHeader(HttpHeaders.AUTHORIZATION, authHeader);
			
			request.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
			
			httpCall(request);
			MessageBox dialog =new MessageBox(shell,SWT.ICON_INFORMATION| SWT.OK);
			dialog.setText("Success");
			dialog.setMessage("Deleted Action remotely");
			dialog.open();
			
			UINamespace ns = uiAction.getParent();
			ns.getActions().remove(uiAction);
			
			ServerView serverView = (ServerView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(ServerView.ID);
			
			serverView.getViewer().refresh(ns);
			serverView.getViewer().expandAll();
			}catch (Exception e) {
				openError(shell, e);
			}
		
	
	}
	
	public void selectionChanged(IAction action, ISelection selection) {
		
		Object obj= ((IStructuredSelection)selection).getFirstElement();
		if(obj instanceof UIAction){
			uiAction = (UIAction)obj;
			action.setEnabled(true);
			return;
		}
		action.setEnabled(false);
	}
}
