package bluemix.rest;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

import com.google.gson.JsonObject;

public class InvokeAction extends BaseAction {

	public boolean isBlockin;
	private boolean result;

	@Override
	public void run(IAction arg0) {
		super.run(arg0);
		
		InputDialog dlg = new InputDialog(shell,"Parameters","Comma seprated parameters","",null){
			@Override
			protected Control createDialogArea(Composite parent) {
				Control ctrl = super.createDialogArea(parent);
				setResult( false);
				setBlocking( false);
				Composite contentArea = (Composite)ctrl;
				Button btn = new Button(contentArea, SWT.CHECK|SWT.RIGHT);
				btn.setText("Blocking activation");
				btn.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						setBlocking( btn.getSelection());
					}
					
					
				});
				
				
				Button btn2 = new Button(contentArea, SWT.CHECK|SWT.RIGHT);
				btn2.setText("Result(Only when blocking activation enabled)");
				btn2.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						setResult( btn2.getSelection());
					}
					
				});
				
				
				return ctrl;

			}
			
			
			
		};
		
		dlg.open();
		if(dlg.getReturnCode() == Dialog.CANCEL){
			return;
		}
		try{
		String fullName = file.getName();
		String name = fullName.substring(0, fullName.indexOf('.'));
		String value = dlg.getValue();
		JsonObject jsn = new JsonObject();
		jsn.addProperty("key", value);
//		name="test-weather";
		String url = getBaseURL()+"namespaces/_/actions/"+name+"?blocking="+isBlockin+"&result="+result;
		System.out.println("URL  "+url);
		HttpPost request = new HttpPost(url);
		updateAuthHeader(request);
		request.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
		System.out.println("payload:"+ jsn);
		request.setEntity(new StringEntity(jsn.toString(), "utf-8"));
		
		
			HttpResponse response = httpCall(request);
			String json = EntityUtils.toString(response.getEntity());
			System.out.println("Output "+json);
			MessageBox dialog =new MessageBox(shell,SWT.ICON_INFORMATION| SWT.OK);
			dialog.setText("Success");
			dialog.setMessage("Response JSON:"+ json);
			dialog.open();
			MessageConsole myConsole = findConsole("IBM Functions excecution");
			MessageConsoleStream out = myConsole.newMessageStream();
//			out.print("Function : "+name);
//			out.println("   Parameters : "+value);
			out.println(json);
			 
			   
			 
			}catch (Exception e) {
				openError(shell, e);
				return;
			}
		
	}
	private void setBlocking(boolean selection) {
		isBlockin = selection;
	}

	private void setResult(boolean result) {
		this.result = result;
	}
	
	   private MessageConsole findConsole(String name) {
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
