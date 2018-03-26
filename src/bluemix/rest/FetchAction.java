package bluemix.rest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Base64;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;
import org.eclipse.ui.part.FileEditorInput;

import com.google.gson.Gson;

import bluemix.rest.model.Action;
import bluemix.rest.model.Exec;
import bluemix.ui.model.UIAction;

public class FetchAction extends BaseAction {

	private UIAction uiAction;

	public void run(IAction arg0) {
			IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
			ContainerSelectionDialog dlg = new ContainerSelectionDialog(shell,root,true,"Select Location to download action source."){
				@Override
				protected void setSelectionResult(Object[] newResult) {
					super.setSelectionResult(newResult);
				}
				
			};
			
			if(dlg.open() == ContainerSelectionDialog.CANCEL)
				return;
			IPath path = (IPath)dlg.getResult()[0];
			String name = uiAction.getAction().getName();
			Action action = uiAction.getAction();
			IPath folderLocation = path.append(""+IPath.SEPARATOR).append("["+action.getNamespace()+"]");
			IFile file = root.getFile(folderLocation.append(""+IPath.SEPARATOR).append(name+action.getExtension()));
			if(file.exists()){
				if(!MessageDialog.openConfirm(shell, "Override Action", "The action source is already exist at given location. Are you sure you want to override the action?"))
					return;
			}
			try{
			action = performGet(uiAction);
			Exec exec= action.getExec();
			String code = exec.getCode();
			
			IFolder resource = root.getFolder(folderLocation);
			if(!resource.exists()){
				
				resource.create(0, true, null);
			}
			
			try {
				InputStream stream =  new ByteArrayInputStream(code.getBytes());
				if (file.exists()) {
					file.setContents(stream, true, true, null);
				} else {
					file.create(stream, true, null);
				}
				stream.close();
			} catch (IOException e) {
			}
			IEditorInput input = new FileEditorInput(file);
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(input ,"org.eclipse.wst.jsdt.ui.CompilationUnitEditor" );
			
			}catch (Exception e) {
				openError(shell, e);
			}
		
	
	}

	public Action performGet(UIAction uiAction) throws IOException {
		String name = uiAction.getAction().getName();
		Action action;
		String url = getBaseURL()+"namespaces/_/actions/"+name;
		System.out.println("URL  "+url);
		HttpGet request = new HttpGet(url);

		
		byte[] encodedAuth = Base64.getEncoder().encode(uiAction.getParent().getApiKey().getBytes(Charset.forName("ISO-8859-1")));
		String authHeader = "Basic " + new String(encodedAuth);
		request.setHeader(HttpHeaders.AUTHORIZATION, authHeader);
		request.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
		HttpResponse response =httpCall(request);
		String json = EntityUtils.toString(response.getEntity());
		Gson gson = new Gson();
		return gson.fromJson(json, Action.class);
		
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
