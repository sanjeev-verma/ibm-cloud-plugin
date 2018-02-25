package bluemix.rest;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.PlatformUI;

import bluemix.ui.ServerView;
import bluemix.ui.model.UIAction;
import bluemix.ui.model.UINamespace;

public class RefreshAction extends BaseAction {

	private UINamespace namespace;

	public void run(IAction arg0) {
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
			ServerView serverView = (ServerView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(ServerView.ID);
			
			serverView.getViewer().refresh(namespace);
			serverView.getViewer().expandAll();
		
	
	}
	
	public void selectionChanged(IAction action, ISelection selection) {
		
		Object obj= ((IStructuredSelection)selection).getFirstElement();
		if(obj instanceof UINamespace){
			namespace = (UINamespace)obj;
			action.setEnabled(true);
			return;
		}
		action.setEnabled(false);
	}
}
