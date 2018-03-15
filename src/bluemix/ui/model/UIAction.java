package bluemix.ui.model;

import bluemix.rest.model.Action;

public class UIAction {
	private Action action;
	private UINamespace parent;
	public UINamespace getParent() {
		return parent;
	}
	public void setParent(UINamespace parent) {
		this.parent = parent;
	}
	public Action getAction() {
		return action;
	}
	public void setAction(Action action) {
		this.action = action;
	}

	
	
	
}
