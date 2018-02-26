package bluemix.ui;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.DrillDownAdapter;
import org.eclipse.ui.part.ViewPart;

import bluemix.rest.ListAction;
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
//	private Action doubleClickAction;

	public TreeViewer getViewer() {
		return viewer;
	}
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
				UINamespace namespace = (UINamespace) parent;
				Object[] objs = namespace.getActions().toArray();
				if(objs ==null || objs.length<=0){
					ListAction list = new ListAction();
					List<bluemix.rest.model.Action> performGetList = list.performGetList(namespace.getApiKey());
					namespace.getActions().clear();
					List<UIAction> uiActions = new ArrayList<>();
					for (bluemix.rest.model.Action action : performGetList) {
						UIAction uiAct = new UIAction();
						uiAct.setParent(namespace);
						uiAct.setAction(action);
						uiActions.add(uiAct);
					}
					namespace.setActions(uiActions);
					return uiActions.toArray();
				}
				return objs;
			}

			return null;
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
				bluemix.rest.model.Action act = ((UIAction) child).getAction();
				return String.format("%s	[v:%s][update: %tc]", act.getName(),act.getVersion(), new Date(act.getUpdated()));
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
//		makeActions();
		hookContextMenu();
//		hookDoubleClickAction();
//		contributeToActionBars();
	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
	}


	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}
}
