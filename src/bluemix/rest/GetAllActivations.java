package bluemix.rest;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import bluemix.rest.model.Action;
import bluemix.rest.model.Activation;
import bluemix.ui.ActivityView;
import bluemix.ui.model.UIAction;

public class GetAllActivations extends BaseAction {

	
	private UIAction uiAction;
	
	public void selectionChanged(IAction action, ISelection selection) {
		
		Object obj= ((IStructuredSelection)selection).getFirstElement();
		if(obj instanceof UIAction){
			uiAction = (UIAction)obj;
			action.setEnabled(true);
			return;
		}
		action.setEnabled(false);
	}
	
	@Override
	public void run(IAction arg0) {
		try {
		List<Activation> activations =fetchAllActivations();
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			ActivityView viewPart = (ActivityView) page.showView(ActivityView.ID);
			viewPart.update(activations);
		} catch (PartInitException e) {
			e.printStackTrace();
		}
	}
	
	public List<Activation> fetchAllActivations(){
		String baseUrl = getBaseURL();
		String url = baseUrl+"namespaces/_/activations?limit=50&skip=0&docs=true";
		if(uiAction != null){
			url = url+"&name="+uiAction.getAction().getName();
		}
		
		HttpGet request = new HttpGet(url);
		System.out.println(url);
		updateAuthHeader(request);
		request.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
		try{
			HttpResponse response= httpCall(request);
			String json = EntityUtils.toString(response.getEntity());
			Gson gson = new Gson();
			
			JsonParser jsonParser = new JsonParser();
			JsonArray jsonArray = (JsonArray) jsonParser.parse(json);
			List<Action> parents = new ArrayList<>();
			List<Activation> allActivations = new ArrayList<>();

			for (JsonElement jsonElement : jsonArray) {
				Activation activation = gson.fromJson(jsonElement.toString(), Activation.class);
				Action action = gson.fromJson(jsonElement.toString(), Action.class);
				allActivations.add(activation);
				if(parents.contains(action)){
					Action orignal = parents.get(parents.indexOf(action));
					activation.setAction(orignal);
					orignal.getActivations().add(activation);
				}else{
					activation.setAction(action);
					action.getActivations().add(activation);
					parents.add(action);
				}
			}
			
			return allActivations;
			}catch (Exception e) {
				e.printStackTrace();
				openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), e);
				return null;
			}
		
	}
	
	
	
}
