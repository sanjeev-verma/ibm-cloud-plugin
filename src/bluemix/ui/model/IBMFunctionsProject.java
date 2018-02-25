package bluemix.ui.model;

import java.util.ArrayList;
import java.util.List;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;

import org.eclipse.jface.preference.IPreferenceStore;

import bluemix.core.BluemixActivator;
import bluemix.ui.PreferenceConstants;

public class IBMFunctionsProject implements PreferenceChangeListener{
	private static IBMFunctionsProject instance;
	
	private List<UINamespace> namespaces;
	
	private String server;
	
	private IBMFunctionsProject(){
		init();
	}
	public List<UINamespace> getNamespaces() {
		return namespaces;
	}
	
	public void setNamespaces(List<UINamespace> namespaces) {
		this.namespaces = namespaces;
	}
	
	public static IBMFunctionsProject getInstance() {
		if(instance== null){
			instance = new IBMFunctionsProject();
			
		}
		return instance;
	}
	
	private void init() {
		IPreferenceStore store =BluemixActivator.getDefault().getPreferenceStore();
		server = store.getString(PreferenceConstants.BLUEMIX_SERVER);
		UINamespace current = new UINamespace(store.getString(PreferenceConstants.BLUEMIX_NAMESPACES), store.getString(PreferenceConstants.BLUEMIX_AUTH_KEY));
		namespaces = new ArrayList<>();
		namespaces.add(current);
		
	}
	public String getServer() {
		return server;
	}
	
	public void setServer(String server) {
		this.server = server;
	}
	@Override
	public void preferenceChange(PreferenceChangeEvent event) {
		
	}
	
	
}
