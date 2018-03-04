package bluemix.ui;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.DrillDownAdapter;
import org.eclipse.ui.part.ViewPart;

import bluemix.rest.GetAllActivations;
import bluemix.rest.model.Action;
import bluemix.rest.model.Activation;


public class ActivityView extends ViewPart{

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "bluemix.ui.ActivityView";
	private TreeViewer	      m_treeViewer;
	private String currentNs;
	private List<Activation> activations;
	
	private DrillDownAdapter drillDownAdapter;
	private org.eclipse.jface.action.Action action1;
	private org.eclipse.jface.action.Action action2;
	private org.eclipse.jface.action.Action doubleClickAction;

	 
	   class ViewLabelProvider implements ITableLabelProvider {
		   private SimpleDateFormat sdf = new SimpleDateFormat("dd/M/yyyy, hh:mm:ss");
		   

		public String getColumnText(Object obj, int index){
	    	  if(obj instanceof Action ){
	    		  Action action = (Action)obj;
	    		  if(index ==0)
	    			  return  action.getName() ;
	    		  
	    	  }else if(obj instanceof Activation){
	    		  Activation act = (Activation)obj;
	    		  if(index ==0){
	    			  return act.getActivationId().substring(7);
	    		  }else if(index ==1){
	    			  return sdf.format(new Date(act.getStart()));
	    		  }else if(index ==2){
	    			  return act.getDuration()+"";
	    		  }else if(index ==3 && act.getResponse() != null){
	    			  
	    			  return act.getResponse().getResult().getMessage(); 
	    		  }else if(index ==4 && act.getLogs() != null){
	    			  System.out.println("Logs"+act.getLogs());
	    			  String logStr=""; 
	    			  for (String log : act.getLogs()) {
						logStr=log+"\n"+logStr;
					}
	    			  
	    			  return logStr; 
	    		  }
	    	  }
	    	  
			return "";
	      }
	 
	      public Image getColumnImage(Object obj, int index){
	    	  if(index != 0)
	    		  return null;
	    	  String name=ISharedImages.IMG_OBJ_ADD;
	    	  if(obj instanceof Action ){
	    		  name =  ISharedImages.IMG_OBJ_ELEMENT;
	    	  }else if(obj instanceof Activation ){
	    		  Activation ac = (Activation)obj;
	    		  if(ac.getStatusCode() == 0){
	    			  name = ISharedImages.IMG_OBJS_INFO_TSK;
	    		  }else if(ac.getStatusCode() == 1){
	    			  name = ISharedImages.IMG_OBJS_WARN_TSK;
	    		  }else if(ac.getStatusCode() == 2){
	    			  name = ISharedImages.IMG_OBJS_ERROR_TSK;
	    		  }
	    	  }
	         return getImage(name);
	      }
	 
	      public Image getImage(String name){
	         return PlatformUI.getWorkbench().getSharedImages().getImage(name);
	         }

		@Override
		public void addListener(ILabelProviderListener arg0) {
			
		}

		@Override
		public void dispose() {
			
		}

		@Override
		public boolean isLabelProperty(Object arg0, String arg1) {
			return false;
		}

		@Override
		public void removeListener(ILabelProviderListener arg0) {
			
		}
	   }
	 
	   public void createPartControl(Composite parent){
	      Tree addressTree = new Tree(parent, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
			addressTree.setHeaderVisible(true);
	      m_treeViewer = new TreeViewer(addressTree);
	      drillDownAdapter = new DrillDownAdapter(m_treeViewer);
	 
	      TreeColumn column1 = new TreeColumn(addressTree, SWT.LEFT);
	      addressTree.setLinesVisible(true);
	      column1.setAlignment(SWT.LEFT);
	      column1.setText("Activation ID");
	      column1.setWidth(80);
	      
	      TreeColumn column2 = new TreeColumn(addressTree, SWT.RIGHT);
	      column2.setAlignment(SWT.LEFT);
	      column2.setText("Start At");
	      column2.setWidth(80);
	      
	      TreeColumn column3 = new TreeColumn(addressTree, SWT.RIGHT);
	      column3.setAlignment(SWT.LEFT);
	      column3.setText("Duration");
	      column3.setWidth(40);
	 
	      TreeColumn column4 = new TreeColumn(addressTree, SWT.RIGHT);
	      column4.setAlignment(SWT.LEFT);
	      column4.setText("Response");
	      column4.setWidth(120);
	 
	      TreeColumn column5 = new TreeColumn(addressTree, SWT.RIGHT);
	      column5.setAlignment(SWT.LEFT);
	      column5.setText("Logs");
	      column5.setWidth(120);
	 
	      
	      m_treeViewer.setContentProvider(new AddressContentProvider());
	      m_treeViewer.setLabelProvider(new ViewLabelProvider());
			makeActions();
			hookContextMenu();
			hookDoubleClickAction();
			contributeToActionBars();

//	      List<City> cities = new ArrayList<City>();
//	      cities.add(new City());
//	      m_treeViewer.setInput(cities);
//	      m_treeViewer.expandAll();
	   }
	   
	   
	   public void refreshView(){
		   activations = new GetAllActivations().fetchAllActivations();
		   currentNs="_";
		   m_treeViewer.setInput(currentNs);
	   }
	 
	   public void setFocus(){
	      m_treeViewer.getControl().setFocus();
	   }
	 
	 
	   
	   
	   class AddressContentProvider implements ITreeContentProvider{
	      public Object[] getChildren(Object parentElement){
	         if (currentNs.equals(parentElement)){
	        	 List<Action> actions = new ArrayList<>();
	        	 for (Activation activation : activations) {
					if(!actions.contains(activation.getAction())){
						actions.add(activation.getAction());
					}
				}
	        	 return actions.toArray();
	         }
	         if (parentElement instanceof Action)
	            return ((Action) parentElement).getActivations().toArray();
	         
	         return new Object[0];
	      }
	 
	      public Object getParent(Object element){
	         if (element instanceof Activation)
	            return ((Activation) element).getAction();
	         if (element instanceof Action)
	            return currentNs;
	         return new Object[0];
	      }
	 
	      public boolean hasChildren(Object element){
	         return true;
	      }
	 
	      public Object[] getElements(Object cities){
	         return getChildren(cities);
	      }
	 
	      public void dispose(){
	      }
	 
	      public void inputChanged(Viewer viewer, Object oldInput, Object newInput){
	      }
	   }
	 
	 
		private void hookContextMenu() {
			MenuManager menuMgr = new MenuManager("#PopupMenu");
			menuMgr.setRemoveAllWhenShown(true);
			menuMgr.addMenuListener(new IMenuListener() {
				public void menuAboutToShow(IMenuManager manager) {
					ActivityView.this.fillContextMenu(manager);
				}
			});
			Menu menu = menuMgr.createContextMenu(m_treeViewer.getControl());
			m_treeViewer.getControl().setMenu(menu);
			getSite().registerContextMenu(menuMgr, m_treeViewer);
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
			action1 = new org.eclipse.jface.action.Action() {
				public void run() {
					showMessage("Action 1 executed");
					ActivityView.this.refreshView();
				}
			};
			action1.setText("Action 1");
			action1.setToolTipText("Action 1 tooltip");
			action1.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
				getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
			
			action2 = new org.eclipse.jface.action.Action() {
				public void run() {
					showMessage("Action 2 executed");
				}
			};
			action2.setText("Action 2");
			action2.setToolTipText("Action 2 tooltip");
			action2.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
					getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
			doubleClickAction = new org.eclipse.jface.action.Action() {
				public void run() {
					ISelection selection = m_treeViewer.getSelection();
					Object obj = ((IStructuredSelection)selection).getFirstElement();
					
					EventDetailsDialog dlg = new EventDetailsDialog(getViewSite().getShell(), ActivityView.this,
							((IStructuredSelection)selection),((ISelectionProvider)m_treeViewer), null);
					dlg.open();
//					showMessage("Double-click detected on "+obj.toString());
				}
			};
		}

		private void hookDoubleClickAction() {
			m_treeViewer.addDoubleClickListener(new IDoubleClickListener() {
				public void doubleClick(DoubleClickEvent event) {
					doubleClickAction.run();
				}
			});
		}
		private void showMessage(String message) {
			MessageDialog.openInformation(
				m_treeViewer.getControl().getShell(),
				"Sample View",
				message);
		}


	   
}
