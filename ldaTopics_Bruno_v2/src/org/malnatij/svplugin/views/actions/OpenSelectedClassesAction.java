package org.malnatij.svplugin.views.actions;

import org.eclipse.jface.action.Action;
import org.malnatij.svplugin.views.XRayWorkbenchView;

public class OpenSelectedClassesAction extends ActionImageDescriptor {

	public OpenSelectedClassesAction(XRayWorkbenchView viewContainer) {
		
		super("Show the selected classes in the Editor",
				Action.AS_PUSH_BUTTON, viewContainer);
		
		setImageDescriptor(getImageDescriptor("page.gif"));
	}
	
	
	public void run(){
		viewContainer.openAllNodes();
	}

	
}
