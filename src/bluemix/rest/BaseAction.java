package bluemix.rest;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.util.EntityUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

import bluemix.core.BluemixActivator;
import bluemix.ui.PreferenceConstants;

public abstract class BaseAction  extends RestBase implements IObjectActionDelegate  {

	protected IFile file;
	
	protected Shell shell;
	
	public void selectionChanged(IAction action, ISelection selection) {
		IResource res = (IResource) ((IStructuredSelection)selection).getFirstElement();
		if(res instanceof IFile){
			file = (IFile)res;
			action.setEnabled("JS".equals(file.getFileExtension().toUpperCase()));
			return;
		}
	action.setEnabled(false);
	}
	

	@Override
	public void setActivePart(IAction arg0, IWorkbenchPart arg1) {
		shell = arg1.getSite().getShell();
	}

	@Override
	public void run(IAction arg0) {
		String currentNS = BluemixActivator.getDefault().getPreferenceStore().getString(PreferenceConstants.BLUEMIX_NAMESPACES);
		if(currentNS == null || currentNS.isEmpty()){
			openError(shell, new RuntimeException("No current Namespace set."));
			return;
		}
		if(!currentNS.equals(file.getParent().getName())){
			openError(shell, new RuntimeException("Current Namespace is not matching with file namespace(folder name)."));
			return;
		}
		
		String auth = getPreferenceStore().getString(PreferenceConstants.BLUEMIX_AUTH_KEY);
		
		if (auth == null || auth.isEmpty()) {
			throw new RuntimeException("API key is empty.");
		} 

	}
	
	
	protected void openError(Shell shell, Exception ex){
		 MessageBox dialog =new MessageBox(shell,SWT.ICON_ERROR| SWT.OK);
		 dialog.setText("Operation Failed");
		 dialog.setMessage("Operation failed: "+ex.getMessage());
		 dialog.open();

	}
	
	protected void handleSuccess(HttpResponse response) throws Exception {
		
		String json = EntityUtils.toString(response.getEntity());
		MessageConsole myConsole = findConsole("IBM Functions excecution");
		MessageConsoleStream out = myConsole.newMessageStream();
		out.println("Operation Successfull!");
		out.println(json);
		
	}
	
	protected MessageConsole findConsole(String name) {
	      ConsolePlugin plugin = ConsolePlugin.getDefault();
	      IConsoleManager conMan = plugin.getConsoleManager();
	      IConsole[] existing = conMan.getConsoles();
	      for (int i = 0; i < existing.length; i++)
	         if (name.equals(existing[i].getName()))
	            return (MessageConsole) existing[i];
	      //no console found, so create a new one
	      MessageConsole myConsole = new MessageConsole(name, null);
	      conMan.addConsoles(new IConsole[]{myConsole});
	      return myConsole;
	   }

}
