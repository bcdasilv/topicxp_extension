package org.malnatij.svplugin.views.actions;

import org.eclipse.jface.action.Action;
import org.malnatij.svplugin.model.ProjectRepresentation;
import org.malnatij.svplugin.model.core.ModelNotPreviouslyScheduled;
import org.malnatij.svplugin.views.XRayWorkbenchView;

public class RefreshAction extends ActionImageDescriptor{
	private ToolbarActions parent;

	public RefreshAction(XRayWorkbenchView viewContainer, ToolbarActions parent) {
		super("Refresh the current View", Action.AS_PUSH_BUTTON, viewContainer);
		this.parent = parent;
		setImageDescriptor(getImageDescriptor("refresh.gif"));
	}
	
	public void run(){
		viewContainer.refresh();
		try {
			updateInfoTrickActionText();
		} catch (ModelNotPreviouslyScheduled e) {
			// do nothing
		}
	}
	
	private void updateInfoTrickActionText() throws ModelNotPreviouslyScheduled{
		ProjectRepresentation theProject =
			viewContainer.getModeledProject();
		Action infoTrickAction = parent.getInfoTrickAction();
		if(theProject != null) {
			infoTrickAction.setText(theProject.getName()); //toShortString
		} else {
			infoTrickAction.setText("[No project]");
		}
	}

}
