package bluemix.ui.model;

import java.util.List;

public class NamespaceManager {

	private NamespaceManager currentNamespace;
	
	private List<NamespaceManager> allNamespaces;
	
	private NamespaceManager(){
		
	}
	
	public static NamespaceManager instance;
	public static NamespaceManager getInstnce(){
		if(instance== null){
			instance = new NamespaceManager();
		}
		return instance;
	}
	
	public List<NamespaceManager> getAllNamespaces() {
		return allNamespaces;
	}
	
	
}
