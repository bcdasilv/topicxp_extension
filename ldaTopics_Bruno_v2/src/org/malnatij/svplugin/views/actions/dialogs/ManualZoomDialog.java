package org.malnatij.svplugin.views.actions.dialogs;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.malnatij.svplugin.views.XRayWorkbenchView;
import org.malnatij.svplugin.views.actions.dialogs.validators.ManualZoomStringValidator;


public class ManualZoomDialog extends ExtendendInputDialog{

	public ManualZoomDialog(XRayWorkbenchView theView) {
		super(Display.getCurrent().getActiveShell(),
				"Manually Zoom In & Out",
				"Provide a zoming coefficient (I.E. 0.85 for 85%)",
				"",
				new ManualZoomStringValidator(),
				theView);
		
		open();
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = getSuperControl(parent);
		
		return container;
	}

	@Override
	public void okPressed() {
		viewContainer.zoom(new Double(getValue()),
				viewContainer.getCurrentFilters());
	}

	
}
