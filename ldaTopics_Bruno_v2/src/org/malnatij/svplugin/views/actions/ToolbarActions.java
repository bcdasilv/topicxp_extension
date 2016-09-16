package org.malnatij.svplugin.views.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.malnatij.svplugin.XRay;
import org.malnatij.svplugin.model.ProjectRepresentation;
import org.malnatij.svplugin.model.core.ModelNotPreviouslyScheduled;
import org.malnatij.svplugin.util.Log;
import org.malnatij.svplugin.views.XRayWorkbenchView;
import org.malnatij.svplugin.views.actions.dialogs.HideEdgesDialog;
import org.malnatij.svplugin.views.actions.menuitems.KindRelatedMenuItem;
import org.malnatij.svplugin.views.actions.menuitems.providers.ShowEdgesMenuItemProvider;

import edu.wm.LDATopics.LDATopics;
import edu.wm.LDATopics.gui.TwoDimensionalView;
import edu.wm.LDATopics.gui.actions.ChangeLDAOptionsAction;
import edu.wm.LDATopics.gui.actions.OpenHelpAction;
import edu.wm.LDATopics.gui.actions.RegenerateAnalysisAction;
import edu.wm.LDATopics.gui.actions.RegenerateAnalysisTableViewAction;
import edu.wm.LDATopics.gui.actions.RemoveUserQueryAction;
import edu.wm.LDATopics.gui.actions.ShowHideEdgesAction;
import edu.wm.LDATopics.gui.actions.UserQueryAction;

/**
 * A set of actions that will be added to the view
 * @author Jacopo Malnati
 *
 */
public class ToolbarActions extends XRay {
	private XRayWorkbenchView viewContainer;
	// a string representing the project
	private Action infoTrickAction = null;
	private Action removeUserQueryAction=  null;
	private IToolBarManager mgr = null;
	
	public ToolbarActions(XRayWorkbenchView twoDimensionalView){
		this.viewContainer = twoDimensionalView;
		// get the manager
		mgr = twoDimensionalView.getViewSite().getActionBars().getToolBarManager();
	}
	
	public void createActions(){
		Log.printSVPluginView("Creating actions");
		
		
		// other inner actions..
	}
	
	public void updateToolbar() {
		removeUserQueryAction.setEnabled(LDATopics.map.getClassMap().filterUsingQuery);
	}
	
	/**
	 * Create tool-bar.
	 */
	public void createToolbar() {
		mgr.removeAll();

		createInfoTrickAction();
		
		removeUserQueryAction = new RemoveUserQueryAction();
		
		mgr.add(new UserQueryAction());
		mgr.add(removeUserQueryAction);
		mgr.add(new ChangeLDAOptionsAction());
		mgr.add(new RegenerateAnalysisAction());
//		mgr.add(new RefreshAction(viewContainer, this));
		mgr.add(new SnapShotAction(viewContainer));
	//	mgr.add(new OpenSelectedClassesAction(viewContainer));
		mgr.add(new Separator());
	//	mgr.add(new PackageFilterAction(viewContainer)); //TODO: could fix this one and readd it
	//	mgr.add(new HideNodeAction(viewContainer));
//		mgr.add(new SelectionAction(viewContainer));
	//	mgr.add(new ColorTagAction(viewContainer));
		mgr.add(new ZoomAction(viewContainer));
		mgr.add(new ShowHideEdgesAction(viewContainer));
		mgr.add(new OpenHelpAction());
		//mgr.add(new EdgesCreatorAction(viewContainer));  // TODO: fix the other edge actions??
		//mgr.add(new SelectViewAction(viewContainer));
		mgr.update(true);
		
		
	}
	
	public void createInfoTrickAction() {
		ProjectRepresentation theProject;
		try {
			theProject = viewContainer.getModeledProject();
//			Action spacer = new Action("this_is_a_spacer_to_make_the_toolbar_big") {
//				public void run() {
//
//					this.setText(" ");
//					// nothing it's just a trick to show text in the action bar
//				}
//			};
//			mgr.add(spacer);
			infoTrickAction = new Action(theProject.getName()) {
				public void run() {
					// nothing it's just a trick to show text in the action bar
				}
			};
			mgr.add(infoTrickAction);
			
			mgr.update(true);

			
		} catch (ModelNotPreviouslyScheduled e) {
			// ignore the request
			return;
		}
	}

	public Action getInfoTrickAction() {
		return infoTrickAction;
	}

}
