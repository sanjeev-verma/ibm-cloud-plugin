package bluemix.ui;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
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

		StringFieldEditor apiKey = new StringFieldEditor(PreferenceConstants.BLUEMIX_AUTH_KEY, "Default NS @API key:",
				getFieldEditorParent());
		apiKey.getTextControl(getFieldEditorParent()).setToolTipText(
				"Copy the API key from portal or use command 'wsk property get --auth' to generate it using command line utiliy.");
		addField(apiKey);
		Button btn = new Button(getFieldEditorParent(), SWT.NONE);
		btn.setText("&Test Connection");
		btn.addSelectionListener(getSelectionListener());
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
	}

}