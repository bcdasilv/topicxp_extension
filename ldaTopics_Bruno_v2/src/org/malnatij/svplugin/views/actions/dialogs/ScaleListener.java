package org.malnatij.svplugin.views.actions.dialogs;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Scale;
import org.malnatij.svplugin.XRay;
import org.malnatij.svplugin.views.XRayWorkbenchView;

public class ScaleListener
extends XRay 
implements SelectionListener{

	private XRayWorkbenchView viewContainer;
	private Scale scale;
	
	public ScaleListener(XRayWorkbenchView viewContainer,
			Scale scale){
		this.viewContainer = viewContainer;
		this.scale = scale;
	}

	public void widgetDefaultSelected(SelectionEvent e) {
		// do nothing
		
	}

	public void widgetSelected(SelectionEvent e) {
		int[] range = {scale.getSelection(), scale.getMaximum()};
		// filter out the dependencies
		viewContainer.showJustDependencyInRange(range);
	}

}
