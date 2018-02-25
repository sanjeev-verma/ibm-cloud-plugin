package bluemix.ui;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.DrillDownAdapter;
import org.eclipse.ui.part.ViewPart;

import bluemix.ui.model.IBMFunctionsProject;
import bluemix.ui.model.UIAction;
import bluemix.ui.model.UINamespace;


public class ServerView extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "bluemix.ui.ServerView";

	private TreeViewer viewer;
	private DrillDownAdapter drillDownAdapter;
	private Action action1;
	private Action action2;
	private Action doubleClickAction;

	class ViewContentProvider implements ITreeContentProvider {

		public Object[] getElements(Object parent) {
			return getChildren(parent);
		}

		public Object getParent(Object child) {
			if (child instanceof IBMFunctionsProject) {
				return null;
			}
			if (child instanceof UINamespace) {
				return IBMFunctionsProject.getInstance();
			}
			if (child instanceof UIAction) {
				return ((UIAction) child).getParent();
			}
			return new Object[0];
		}

		public Object[] getChildren(Object parent) {
			if (parent.equals("ROOT")) {
				return new Object[] { IBMFunctionsProject.getInstance() };
			}
			if (parent instanceof IBMFunctionsProject) {
				return ((IBMFunctionsProject) parent).getNamespaces().toArray();
			}
			if (parent instanceof UINamespace) {
				return ((UINamespace) parent).getActions().toArray();
			}

			return new Object[0];
		}

		public boolean hasChildren(Object parent) {
			return true;
		}

	}

	class ViewLabelProvider extends LabelProvider {

		public String getText(Object child) {
			if (child instanceof IBMFunctionsProject) {
				return ((IBMFunctionsProject) child).getServer();
			}
			if (child instanceof UINamespace) {
				return ((UINamespace) child).getName();
			}
			if (child instanceof UIAction) {
				return ((UIAction) child).getName();
			}
			return child.toString();
		}

		public Image getImage(Object obj) {
			String imageKey = ISharedImages.IMG_OBJ_ELEMENT;
			if (obj instanceof UINamespace) {
				imageKey = ISharedImages.IMG_OBJ_FOLDER;
			}
			if (obj instanceof UIAction) {
				imageKey = ISharedImages.IMG_OBJ_ELEMENT;
			}
			if (obj instanceof IBMFunctionsProject) {
				imageKey = ISharedImages.IMG_OBJ_PROJECT;
			}
			return PlatformUI.getWorkbench().getSharedImages().getImage(imageKey);
		}
	}

	/**
	 * The constructor.
	 */
	public ServerView() {
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent) {
		viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		drillDownAdapter = new DrillDownAdapter(viewer);

		viewer.setContentProvider(new ViewContentProvider());
		viewer.setInput("ROOT");
		viewer.setLabelProvider(new ViewLabelProvider());

		// Create the help context id for the viewer's control
		PlatformUI.getWorkbench().getHelpSystem().setHelp(viewer.getControl(), "bluemix.viewer");
		getSite().setSelectionProvider(viewer);
		makeActions();
		hookContextMenu();
		hookDoubleClickAction();
		contributeToActionBars();
	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				ServerView.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager manager) {
		manager.add(action1);
		manager.add(new Separator());
		manager.add(action2);
	}

	private void fillContextMenu(IMenuManager manager) {
		manager.add(action1);
		manager.add(action2);
		manager.add(new Separator());
		drillDownAdapter.addNavigationActions(manager);
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(action1);
		manager.add(action2);
		manager.add(new Separator());
		drillDownAdapter.addNavigationActions(manager);
	}

	private void makeActions() {
		action1 = new Action() {
			public void run() {
				getViewSite().getSelectionProvider().getSelection();
				showMessage("Action 1 executed");
			}
		};
		action1.setText("Refresh");
		action1.setToolTipText("Action 1 tooltip");
		action1.setImageDescriptor(
				PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_ELCL_SYNCED));

		action2 = new Action() {
			public void run() {
				showMessage("Action 2 executed");
			}
		};
		action2.setText("Action 2");
		action2.setToolTipText("Action 2 tooltip");
		action2.setImageDescriptor(
				PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
		doubleClickAction = new Action() {
			public void run() {
				ISelection selection = viewer.getSelection();
				Object obj = ((IStructuredSelection) selection).getFirstElement();
				showMessage("Double-click detected on " + obj.toString());
			}
		};
	}

	private void hookDoubleClickAction() {
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				doubleClickAction.run();
			}
		});
	}

	private void showMessage(String message) {
		MessageDialog.openInformation(viewer.getControl().getShell(), "IBM Functions Server", message);
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}
}
