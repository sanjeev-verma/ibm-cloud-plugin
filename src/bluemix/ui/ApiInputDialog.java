package bluemix.ui;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import bluemix.rest.model.Api;

public class ApiInputDialog extends TitleAreaDialog {

    private Text txtFirstName;
    private Text baseURLText;
    private Combo apiMethodCmb;
	private Text apiURLText;
    
	private Api api;
    
    public ApiInputDialog(Shell parentShell) {
        super(parentShell);
    }

    @Override
    public void create() {
        super.create();
        setTitle("Parameters for create API");
//        setMessage("This is a TitleAreaDialog", IMessageProvider.INFORMATION);
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite area = (Composite) super.createDialogArea(parent);
        Composite container = new Composite(area, SWT.NONE);
        container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        GridLayout layout = new GridLayout(2, false);
        container.setLayout(layout);

        apiLabel(container);
        urlSection(container);

        return area;
    }

    private void apiLabel(Composite container) {
        Label lbtFirstName = new Label(container, SWT.NONE);
        lbtFirstName.setText("First Name");

        GridData dataFirstName = new GridData();
        dataFirstName.grabExcessHorizontalSpace = true;
        dataFirstName.horizontalAlignment = GridData.FILL;

        txtFirstName = new Text(container, SWT.BORDER);
        txtFirstName.setLayoutData(dataFirstName);
    }

    private void urlSection(Composite container) {
        Label lbtLastName = new Label(container, SWT.NONE);
        lbtLastName.setText("Base URL:");

        GridData dataLastName = new GridData();
        dataLastName.grabExcessHorizontalSpace = true;
        dataLastName.horizontalAlignment = GridData.FILL;
        baseURLText = new Text(container, SWT.BORDER);
        baseURLText.setLayoutData(dataLastName);
        
        lbtLastName = new Label(container, SWT.NONE);
        lbtLastName.setText("API URL:");

        apiURLText = new Text(container, SWT.BORDER);
        apiURLText.setLayoutData(dataLastName);
        
        lbtLastName = new Label(container, SWT.NONE);
        lbtLastName.setText("Method:");

        apiMethodCmb = new Combo(container, SWT.READ_ONLY);
        apiMethodCmb.setLayoutData(dataLastName);
        apiMethodCmb.add("GET", 0);
//        apiMethod.add("POST", 1);
//        apiMethod.add("PUT", 1);
//        apiMethod.add("DELETE", 1);
    }



    @Override
    protected boolean isResizable() {
        return true;
    }

    // save content of the Text fields because they get disposed
    // as soon as the Dialog closes
    private void saveInput() {
    	api = new Api();
    	String apiMethod = apiMethodCmb.getItem(apiMethodCmb.getSelectionIndex());
		api.getApidoc().put(Api.KYE_API_NAME, txtFirstName.getText());
		api.getApidoc().put(Api.KYE_NAMESPACE, "_");
		api.getApidoc().put(Api.KYE_GATEWAYBASEPATH, apiURLText.getText());
		api.getApidoc().put(Api.KYE_GATEWAYMETHOD, apiMethod);
		api.getApidoc().put(Api.KYE_GATEWAYPATH, baseURLText.getText());
		api.getApidoc().put(Api.KYE_ID, "API:_:"+apiURLText.getText());
    }

    @Override
    protected void okPressed() {
        saveInput();
        super.okPressed();
    }


    public Api getValue(){
    	return api;
    }

}