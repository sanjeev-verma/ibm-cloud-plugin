package com.bluemix.ui;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;

import bluemix.core.BluemixActivator;
import bluemix.ui.PreferenceConstants;

/**
 * The "New" wizard page allows setting the container for the new file as well
 * as the file name. The page will only accept file name without the extension
 * OR with the extension that matches the expected one (js).
 */

public class BluemixTemapleCreationPage extends WizardPage {
	private Text containerText;

	private Text fileText;

	private ISelection selection;

	protected String selectedTemplate;

	private Combo nsCombo;

	private String selectedNS;

	
	public static String[] templateType = new String[] {"NodeJS","Python","Java","xyz"};

	/**
	 * Constructor for SampleNewWizardPage.
	 * 
	 * @param pageName
	 */
	public BluemixTemapleCreationPage(ISelection selection) {
		super("IBM Cloud Functions ");
		setTitle("IBM Cloud Function temapltes");
		setDescription("This wizard creates IBM Cloud functions on the basis of selection type.");
		this.selection = selection;
	}

	/**
	 * @see IDialogPage#createControl(Composite)
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 3;
		layout.verticalSpacing = 9;
		
		Label label2 = new Label(container, SWT.NULL);
		label2.setText("&Action template type:");
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.grabExcessHorizontalSpace = true;
		gd.horizontalSpan=2;
		Composite radioContainer = new Composite(container, SWT.BORDER);
		radioContainer.setLayoutData(gd);
		GridLayout layout2 = new GridLayout();
		radioContainer.setLayout(layout2);
		layout2.numColumns = 4;
		layout2.verticalSpacing = 9;
		createRadioButtons(radioContainer);
		
		
		
		Label label = new Label(container, SWT.NULL);
		label.setText("&Container:");
		

		containerText = new Text(container, SWT.BORDER | SWT.SINGLE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		containerText.setLayoutData(gd);
		containerText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				dialogChanged();
			}
		});

		Button button = new Button(container, SWT.PUSH);
		button.setText("Browse...");
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				handleBrowse();
			}
		});
		
		label = new Label(container, SWT.NULL);
		label.setText("&Select Namespace:");

		nsCombo = new Combo(container, SWT.BORDER | SWT.SINGLE|SWT.READ_ONLY);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan= 2;
		nsCombo.setLayoutData(gd);
		String currentNS= BluemixActivator.getDefault().getPreferenceStore().getString(PreferenceConstants.BLUEMIX_NAMESPACES);
		nsCombo.add(currentNS);
		nsCombo.select(0);
		selectedNS = currentNS;
		
		// TODO need to add selection litener in order to support multiple namespaces.
		
		label = new Label(container, SWT.NULL);
		label.setText("&File name:");

		fileText = new Text(container, SWT.BORDER | SWT.SINGLE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		fileText.setLayoutData(gd);
		fileText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				dialogChanged();
			}
		});
		initialize();
		dialogChanged();
		setControl(container);
		setTitle("New Bluemix Action");
	}
	
	

	private void createRadioButtons(Composite radioContainer) {
		for (String lbl : templateType) {
		Button btn = new Button(radioContainer, SWT.RADIO);
		btn.setText(lbl);
		btn.addSelectionListener(new SelectionListener() {
			 @Override
			public void widgetSelected(SelectionEvent e) {
				 selectedTemplate = ((Button)e.getSource()).getText();
				 dialogChanged();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				 selectedTemplate = ((Button)e.getSource()).getText();
			}
			 
		});
		}
	}

	/**
	 * Tests if the current workbench selection is a suitable container to use.
	 */

	private void initialize() {
		if (selection != null && selection.isEmpty() == false
				&& selection instanceof IStructuredSelection) {
			IStructuredSelection ssel = (IStructuredSelection) selection;
			if (ssel.size() > 1)
				return;
			Object obj = ssel.getFirstElement();
			if (obj instanceof IResource) {
				IContainer container;
				if (obj instanceof IContainer)
					container = (IContainer) obj;
				else
					container = ((IResource) obj).getParent();
				containerText.setText(container.getFullPath().toString());
			}
		}
		fileText.setText("new_file.js");
	}

	/**
	 * Uses the standard container selection dialog to choose the new value for
	 * the container field.
	 */

	private void handleBrowse() {
		
		ContainerSelectionDialog dialog = new ContainerSelectionDialog(getShell(), ResourcesPlugin.getWorkspace().getRoot(), false,
				"Select new file container");
		if (dialog.open() == ContainerSelectionDialog.OK) {
			Object[] result = dialog.getResult();
			if (result.length == 1) {
				containerText.setText(((Path) result[0]).segment(0).toString());
			}
		}
	}

	/**
	 * Ensures that both text fields are set.
	 */

	private void dialogChanged() {
		IResource container = ResourcesPlugin.getWorkspace().getRoot()
				.findMember(new Path(getContainerName()));
		String fileName = getFileName();

		if (getContainerName().length() == 0) {
			updateStatus("File container must be specified");
			return;
		}
		if (container == null
				|| (container.getType() & (IResource.PROJECT | IResource.FOLDER)) == 0) {
			updateStatus("File container must exist");
			return;
		}
		if (!container.isAccessible()) {
			updateStatus("Project must be writable");
			return;
		}
		if (fileName.length() == 0) {
			updateStatus("File name must be specified");
			return;
		}
		if (fileName.replace('\\', '/').indexOf('/', 1) > 0) {
			updateStatus("File name must be valid");
			return;
		}
		int dotLoc = fileName.lastIndexOf('.');
		if (dotLoc != -1) {
			String ext = fileName.substring(dotLoc + 1);
			if (ext.equalsIgnoreCase("js") == false) {
				updateStatus("File extension must be \"js\"");
				return;
			}
		}
		if (!this.templateType[0].equals(selectedTemplate)) {
			updateStatus("Selection not supported. This version of Bluemix eclipse plugin only support NodeJS.");
			return;
		}
		updateStatus(null);
	}

	private void updateStatus(String message) {
		setErrorMessage(message);
		setPageComplete(message == null);
	}

	public String getContainerName() {
		return containerText.getText();
	}

	public String getFileName() {
		return fileText.getText();
	}
	
	public String getSelectedTemplate() {
		return selectedTemplate;
	}
	
	public String getSelectedNS() {
		return selectedNS;
	}
}