package bluemix.ui.model;

import bluemix.rest.model.Action;

public class UIAction extends Action {
	private UINamespace parent;
	public UINamespace getParent() {
		return parent;
	}
	public void setParent(UINamespace parent) {
		this.parent = parent;
	}
}
