package bluemix.ui.model;

import java.util.ArrayList;
import java.util.List;

public class UINamespace {

	private String name;
	private String apiKey;
	private List<UIAction> actions = new ArrayList<>();
	
	public UINamespace(String name, String apiKey) {
		this.name=name;
		this.apiKey=apiKey;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getApiKey() {
		return apiKey;
	}
	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}
	
	public List<UIAction> getActions() {
		return actions;
	}
	
	public void setActions(List<UIAction> actions) {
		this.actions = actions;
	}
	
}
