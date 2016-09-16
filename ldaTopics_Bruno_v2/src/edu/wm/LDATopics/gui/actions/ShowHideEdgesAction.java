package edu.wm.LDATopics.gui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.malnatij.svplugin.views.ViewFacade;
import org.malnatij.svplugin.views.XRayWorkbenchView;
import org.malnatij.svplugin.views.actions.dialogs.HideEdgesDialog;
import org.malnatij.svplugin.views.actions.menuitems.KindRelatedMenuItem;

import edu.wm.LDATopics.LDATopics;

public class ShowHideEdgesAction extends Action {
	
	private XRayWorkbenchView viewContainer;

	public ShowHideEdgesAction(XRayWorkbenchView viewContainer) {
		this.viewContainer = viewContainer;
		setText("Hide Given Edges...");
		setImageDescriptor(LDATopics.imageDescriptorFromPlugin(
				LDATopics.PLUGIN_ID, "icons/dependencies.gif"));
	}

	@Override
	public void run() {
			new HideEdgesDialog(viewContainer,
					viewContainer.getMaxDependencyWeigth());
	}


}
