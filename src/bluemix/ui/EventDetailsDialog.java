package bluemix.ui;

import java.text.Collator;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TrayDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.views.log.SharedImages;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import bluemix.rest.model.Action;
import bluemix.rest.model.Activation;
import bluemix.rest.model.Annotation;

/**
 * Displays details about Log Entry.
 * Event information is split in three sections: details, stack trace and session. Details
 * contain event date, message and severity. Stack trace is displayed if an exception is bound
 * to event. Stack trace entries can be filtered.
 */
public class EventDetailsDialog extends TrayDialog {

	public static final String FILTER_ENABLED = "detailsStackFilterEnabled"; //$NON-NLS-1$
	public static final String FILTER_LIST = "detailsStackFilterList"; //$NON-NLS-1$
	private static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/M/yyyy, hh:mm:ss");
	private ActivityView logView;

	private Activation entry;
//	private AbstractEntry parentEntry; // parent of the entry
//	private AbstractEntry[] entryChildren; // children of the entry
//
	private ITableLabelProvider labelProvider;
	private TreeViewer provider;

	private static int COPY_ID = 22;

	private int childIndex = 0;
	private boolean isOpen;
	private boolean isLastChild;
	private boolean isAtEndOfLog;

	private Label plugInIdLabel;
	private Label severityImageLabel;
	private Label severityLabel;
	private Label dateLabel;
	private Text msgText;
	private Text stackTraceText;
	private Text inputDataText;
	private Clipboard clipboard;
	private Button copyButton;
	private Button backButton;
	private Button nextButton;
	private SashForm sashForm;
	Collator collator;

	// location configuration
	private Point dialogLocation;
	private Point dialogSize;
	private int[] sashWeights;
	private Label activationLabel;
//	private Action parentEntry;


	/**
	 *
	 * @param parentShell shell in which dialog is displayed
	 * @param selection entry initially selected and to be displayed
	 * @param provider viewer
	 * @param comparator comparator used to order all entries
	 */
	protected EventDetailsDialog(Shell parentShell, ActivityView logView, IStructuredSelection selection, ISelectionProvider provider) {
		super(parentShell);
		
		this.logView = logView;
		this.provider = (TreeViewer) provider;
		labelProvider = (ITableLabelProvider) this.provider.getLabelProvider();
		this.entry = (Activation) selection.getFirstElement();
		setShellStyle(SWT.MODELESS | SWT.MIN | SWT.MAX | SWT.RESIZE | SWT.CLOSE | SWT.BORDER | SWT.TITLE);
		clipboard = new Clipboard(parentShell.getDisplay());
		collator = Collator.getInstance();
		isLastChild = false;
		isAtEndOfLog = false;
	}

	/*
	 * @see org.eclipse.jface.window.Window#configureShell(Shell)
	 */
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
	}



	public boolean isOpen() {
		return isOpen;
	}

	@Override
	public int open() {
		isOpen = true;
		if (sashWeights == null) {
			int a, b, c;
			int height = 100;//getSashForm().getClientArea().height;
			if (height < 250) {
				a = b = c = height / 3;
			} else {
				a = 100; // Details section needs about 100
				c = 100; // Text area gets 100
				b = height - a - c; // Stack trace should take up majority of room
			}
			sashWeights = new int[] {a, b, c};
		}
//		getSashForm().setWeights(sashWeights);
		return super.open();
	}

	@Override
	public boolean close() {
		if (clipboard != null) {
			clipboard.dispose();
			clipboard = null;
		}
		isOpen = false;
//		labelProvider.disconnect(this);
		return super.close();
	}

	@Override
	public void create() {
		super.create();

		// dialog location
		if (dialogLocation != null)
			getShell().setLocation(dialogLocation);

		// dialog size
		if (dialogSize != null)
			getShell().setSize(dialogSize);
		else
			getShell().setSize(500, 550);

		applyDialogFont(buttonBar);
		getButton(IDialogConstants.OK_ID).setFocus();
	}

	@Override
	protected void buttonPressed(int buttonId) {
		if (IDialogConstants.OK_ID == buttonId)
			okPressed();
		else if (IDialogConstants.CANCEL_ID == buttonId)
			cancelPressed();
		else if (IDialogConstants.BACK_ID == buttonId)
			backPressed();
		else if (IDialogConstants.NEXT_ID == buttonId)
			nextPressed();
		else if (COPY_ID == buttonId)
			copyPressed();
	}

	protected void backPressed() {
		List<Activation> children = entry.getAction().getActivations();
		int indx = children.indexOf(entry);
		if(indx <= 0){
			ITreeContentProvider contentProvider = (ITreeContentProvider)provider.getContentProvider();
			Action action = (Action) contentProvider.getParent(entry);
			Object obj = contentProvider.getParent(action);
			Object[] allParents = contentProvider.getChildren(obj);
			int parentIndx = Arrays.asList(allParents).indexOf(action);
			if(parentIndx <= 0){
				return;
				//do nothing
			}else{
				provider.expandAll();
				Object previousChildren[] = contentProvider.getChildren(allParents[parentIndx-1]);
				entry = (Activation) previousChildren[previousChildren.length-1]; 
			}
			//moveParent();
		}else{
			entry = children.get(indx-1);
		}	
		updateProperties();

		setEntrySelectionInTable();
		
	}

	protected void nextPressed() {
		List<Activation> children = entry.getAction().getActivations();
		int indx = children.indexOf(entry);
		if(indx >= children.size()-1){
			ITreeContentProvider contentProvider = (ITreeContentProvider)provider.getContentProvider();
			Action action = (Action) contentProvider.getParent(entry);
			Object obj = contentProvider.getParent(action);
			Object[] allParents = contentProvider.getChildren(obj);
			int parentIndx = Arrays.asList(allParents).indexOf(action);
			if(parentIndx >= allParents.length -1 ){
				return;
				//do nothing
			}else{
				provider.expandAll();
				Object previousChildren[] = contentProvider.getChildren(allParents[parentIndx+1]);
				entry = (Activation) previousChildren[0]; 
			}
			
		}else{
			entry = children.get(indx+1);
		}
		updateProperties();
		setEntrySelectionInTable();
	}

	protected void copyPressed() {
		Gson gson = new Gson();
		String textVersion = gson.toJson(entry);
		clipboard.setContents(new Object[] {textVersion}, new Transfer[] {TextTransfer.getInstance()});
	}




	public void resetButtons() {
		backButton.setEnabled(false);
		nextButton.setEnabled(false);
	}

	private void setEntrySelectionInTable() {
		ISelection selection = new StructuredSelection(entry);
		provider.setSelection(selection);
	}

	public void updateProperties() {
			
			
			String strDate = DATE_FORMAT.format(new Date(entry.getStart()));
			dateLabel.setText(strDate);
			plugInIdLabel.setText(entry.getAction().getName());
			severityImageLabel.setImage(labelProvider.getColumnImage(entry, 0));
			severityLabel.setText(entry.getResponse().getStatus());
			activationLabel.setText(entry.getActivationId());
			
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			JsonParser parser = new JsonParser();
			
			updateInputSection(gson, parser);
			updateResultSection(gson, parser);
			updateLogsSection(gson, parser); 


		updateButtons();
	}

	private void updateInputSection(Gson gson, JsonParser parser) {
		List<Annotation> result = entry.getAction().getAnnotations();
		if(result!= null){
			try{
			this.inputDataText.setText(gson.toJson(result));
			}catch(Exception ex){
				inputDataText.setText("");
			}
		}
		
	}

	private void updateLogsSection(Gson gson, JsonParser parser) {
		StringBuilder stack = new StringBuilder();
		for (String log : entry.getLogs()) {
			try{
			JsonObject json = parser.parse(log).getAsJsonObject();
			stack.append("\n"+gson.toJson(json));
			}catch(Exception ex){
				stack.append("\n"+log);
			}
		}

		if (stack != null) {
			stackTraceText.setText(stack.toString());
		}
	}

	private void updateResultSection(Gson gson, JsonParser parser) {
		String result = labelProvider.getColumnText(entry, 3);
		if(result!= null){
			try{
			JsonObject json = parser.parse(result).getAsJsonObject();
			msgText.setText(gson.toJson(json));
			}catch(Exception ex){
				msgText.setText(result);
			}
		}
	}

	private void updateButtons() {/*
		boolean isAtEnd = childIndex == entryChildren.length - 1;
		if (isChild(entry)) {
			boolean canGoToParent = (entry.getParent(entry) instanceof LogEntry);
			backButton.setEnabled((childIndex > 0) || canGoToParent);
			nextButton.setEnabled(nextChildExists(entry, parentEntry, entryChildren) || entry.hasChildren() || !isLastChild || !isAtEnd);
		} else {
			backButton.setEnabled(childIndex != 0);
			nextButton.setEnabled(!isAtEnd || entry.hasChildren());
		}
	*/}


	public SashForm getSashForm() {
		return sashForm;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		container.setLayout(layout);
		GridData gd = new GridData(GridData.FILL_BOTH);
		container.setLayoutData(gd);

		createSashForm(container);
		createDetailsSection(getSashForm());
		createSessionSection(getSashForm());
		createStackSection(getSashForm());
//		

		updateProperties();
		Dialog.applyDialogFont(container);
		return container;
	}

	private void createSashForm(Composite parent) {
		sashForm = new SashForm(parent, SWT.VERTICAL);
		GridLayout layout = new GridLayout();
		layout.marginHeight = layout.marginWidth = 0;
		sashForm.setLayout(layout);
		sashForm.setLayoutData(new GridData(GridData.FILL_BOTH));
		sashForm.setSashWidth(10);
	}

	private void createToolbarButtonBar(Composite parent) {
		Composite comp = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginWidth = layout.marginHeight = 0;
		layout.numColumns = 1;
		comp.setLayout(layout);
		comp.setLayoutData(new GridData(GridData.FILL_VERTICAL));
		((GridData) comp.getLayoutData()).verticalAlignment = SWT.BOTTOM;

		Composite container = new Composite(comp, SWT.BORDER);
		layout = new GridLayout();
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		container.setLayout(layout);
		container.setLayoutData(new GridData(GridData.FILL_BOTH | GridData.VERTICAL_ALIGN_CENTER));
		((GridData) container.getLayoutData()).verticalAlignment = SWT.CENTER;
		((GridData) container.getLayoutData()).verticalSpan= 2;
		
		backButton = createButton(container, IDialogConstants.BACK_ID, "", false); //$NON-NLS-1$
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		backButton.setLayoutData(gd);
		backButton.setToolTipText("");
		backButton.setImage(SharedImages.getImage(SharedImages.DESC_PREV_EVENT));

		copyButton = createButton(container, COPY_ID, "", false); //$NON-NLS-1$
		gd = new GridData();
		copyButton.setLayoutData(gd);
		copyButton.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_TOOL_COPY));
		copyButton.setToolTipText("Copy");

		nextButton = createButton(container, IDialogConstants.NEXT_ID, "", false); //$NON-NLS-1$
		gd = new GridData();
		nextButton.setLayoutData(gd);
		nextButton.setToolTipText("Next");
		nextButton.setImage(SharedImages.getImage(SharedImages.DESC_NEXT_EVENT));
		
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		// create OK button only by default
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
	}

	private void createDetailsSection(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginWidth = layout.marginHeight = 0;
		layout.numColumns = 2;
		container.setLayout(layout);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.heightHint = 200;
		container.setLayoutData(data);

		createTextSection(container);
		createToolbarButtonBar(container);
	}

	private void createTextSection(Composite parent) {
		Composite textContainer = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		layout.marginHeight = layout.marginWidth = 0;
		textContainer.setLayout(layout);
		textContainer.setLayoutData(new GridData(GridData.FILL_BOTH));

		Label label = new Label(textContainer, SWT.NONE);
		label.setText("Action:");
		plugInIdLabel = new Label(textContainer, SWT.NONE);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		plugInIdLabel.setLayoutData(gd);

		label = new Label(textContainer, SWT.NONE);
		label.setText("Activation ID:");
		activationLabel = new Label(textContainer, SWT.NONE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		activationLabel.setLayoutData(gd);

		
		
		label = new Label(textContainer, SWT.NONE);
		label.setText("Status:");
		severityImageLabel = new Label(textContainer, SWT.NONE);
		severityLabel = new Label(textContainer, SWT.NONE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		severityLabel.setLayoutData(gd);

		label = new Label(textContainer, SWT.NONE);
		label.setText("Date:");
		dateLabel = new Label(textContainer, SWT.NONE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		dateLabel.setLayoutData(gd);

		label = new Label(textContainer, SWT.NONE);
		label.setText("Result");
		gd = new GridData(GridData.VERTICAL_ALIGN_CENTER);
		label.setLayoutData(gd);
		msgText = new Text(textContainer, SWT.MULTI | SWT.V_SCROLL | SWT.WRAP | SWT.BORDER);
		msgText.setEditable(false);
		gd = new GridData(GridData.FILL_BOTH | GridData.VERTICAL_ALIGN_BEGINNING | GridData.GRAB_VERTICAL);
		gd.horizontalSpan = 2;
		gd.grabExcessVerticalSpace = true;
		msgText.setLayoutData(gd);
	}

	private void createStackSection(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		container.setLayout(layout);
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.heightHint = 200;
		container.setLayoutData(gd);

		Label label = new Label(container, SWT.NONE);
		label.setText("Logs");
		gd = new GridData();
		gd.verticalAlignment = SWT.BOTTOM;
		label.setLayoutData(gd);

		stackTraceText = new Text(container, SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);
		gd = new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL);
		gd.grabExcessHorizontalSpace = true;
		gd.horizontalSpan = 2;
		stackTraceText.setLayoutData(gd);
		stackTraceText.setEditable(false);
	}

	private void createSessionSection(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		container.setLayout(layout);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.heightHint = 100;
		container.setLayoutData(gd);

		Label label = new Label(container, SWT.NONE);
		label.setText("Input");
		gd = new GridData(GridData.FILL_HORIZONTAL);
		label.setLayoutData(gd);
		inputDataText = new Text(container, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		gd = new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL);
		gd.grabExcessHorizontalSpace = true;
		inputDataText.setLayoutData(gd);
		inputDataText.setEditable(false);
	}



}
