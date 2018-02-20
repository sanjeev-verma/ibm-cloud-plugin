package bluemix.ui;

import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.ListEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import bluemix.core.BluemixActivator;
import bluemix.rest.TestConnection;

/**
 * This class represents a preference page that is contributed to the
 * Preferences dialog. By subclassing <samp>FieldEditorPreferencePage</samp>, we
 * can use the field support built into JFace that allows us to create a page
 * that is small and knows how to save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They are stored in the
 * preference store that belongs to the main plug-in class. That way,
 * preferences can be accessed directly via the preference store.
 */

public class BluemixPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	private StringFieldEditor namespaceEditor;
	public BluemixPreferencePage() {
		super(GRID);
		setPreferenceStore(BluemixActivator.getDefault().getPreferenceStore());
		setDescription("Bluemix server and login credentials Details");
	}

	/**
	 * Creates the field editors. Field editors are abstractions of the common
	 * GUI blocks needed to manipulate various types of preferences. Each field
	 * editor knows how to save and restore itself.
	 */
	public void createFieldEditors() {
		addField(new StringFieldEditor(PreferenceConstants.BLUEMIX_SERVER, "Server &Name:", getFieldEditorParent()));
		addField(new StringFieldEditor(PreferenceConstants.BLUEMIX_USER, "&User Name:", getFieldEditorParent()));
		addField(new StringFieldEditor(PreferenceConstants.BLUEMIX_PASSWORD, "&Password:", getFieldEditorParent()) {
			@Override
			protected void createControl(Composite parent) {
				super.createControl(parent);
				getTextControl().setEchoChar('*');
			}
		});
//		addField(new StringFieldEditor(PreferenceConstants.BLUEMIX_NAMESPACES, "Current &Namespace:", getFieldEditorParent()));
//		
//		ListEditor list = new ListEditor(PreferenceConstants.BLUEMIX_AUTH_KEY, "Default NS @API key:",getFieldEditorParent()){
//
//			@Override
//			protected String createList(String[] arg0) {
//				return arg0[0]+"###"+arg0[1]+"###"+arg0[2];
//			}
//
//			@Override
//			protected String getNewInputObject() {
//				InputDialog dlg = new InputDialog(getShell(), "Provide namespace", "Input Data","Namespace" , null);
//				dlg.open();
//				if(dlg.getReturnCode() == 0){
//					return dlg.getValue();
//				}
//				
//				return createList(new String[]{"","",""});
//			}
//
//			@Override
//			protected String[] parseString(String arg0) {
//				
//				return arg0.split("###");
//			}
//			
//			@Override
//			protected void createControl(Composite parent) {
//				super.createControl(parent);
//				
//			}
//			
//		};
//		

		
		StringFieldEditor apiKey = new StringFieldEditor(PreferenceConstants.BLUEMIX_AUTH_KEY, "Default NS @API key:",
				getFieldEditorParent());
		apiKey.getTextControl(getFieldEditorParent()).setToolTipText(
				"Copy the API key from portal or use command 'wsk property get --auth' to generate it using command line utiliy.");
		addField(apiKey);
		Button btn = new Button(getFieldEditorParent(), SWT.NONE);
		btn.setText("&Test Connection");
		btn.addSelectionListener(getSelectionListener());
		new Label(getFieldEditorParent(), SWT.NULL);
		namespaceEditor = new StringFieldEditor(PreferenceConstants.BLUEMIX_NAMESPACES, "Current Namespace(Auto populate on Test Connection):", getFieldEditorParent());
		namespaceEditor.getTextControl(getFieldEditorParent()).setEnabled(false);;
		addField(namespaceEditor);
	}

	
	private SelectionListener getSelectionListener() {
		return new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent arg0) {

				
				performApply();
				TestConnection connection = new TestConnection();
				try{
				if(connection.connect()){
					 MessageBox dialog =new MessageBox(getShell(),SWT.ICON_INFORMATION| SWT.OK);
					 dialog.setText("Success");
					 dialog.setMessage("Connected successfully!");
					 dialog.open();
					 namespaceEditor.load();
				}
				}catch(RuntimeException ex){
					 MessageBox dialog =new MessageBox(getShell(),SWT.ICON_ERROR| SWT.OK);
					 dialog.setText("Connection Failed");
					 dialog.setMessage("Connection failed: "+ex.getMessage());
					 dialog.open();
					 

					
				}
				
			}
		};
	}

	@Override
	protected void performApply() {
		super.performApply();
		
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
	}

}