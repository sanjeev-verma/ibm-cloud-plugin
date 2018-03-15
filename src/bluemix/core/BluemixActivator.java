package bluemix.core;

import org.eclipse.ui.plugin.AbstractUIPlugin;

public class BluemixActivator extends AbstractUIPlugin {
	
	
	private static BluemixActivator instance;

	public BluemixActivator() {
		if(instance == null){
			instance = this;
		}
	}
	
	public static BluemixActivator getDefault(){
		if(instance == null){
			instance = new BluemixActivator();
		}
		return instance;
	}


}
