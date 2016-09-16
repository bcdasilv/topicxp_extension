package org.malnatij.svplugin.views.actions.dialogs;

import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.malnatij.svplugin.views.XRayWorkbenchView;

public abstract class ExtendendInputDialog extends InputDialog{
	protected XRayWorkbenchView viewContainer;

	public ExtendendInputDialog(Shell parentShell,
			String dialogTitle,
			String dialogMessage,
			String initialValue,
			IInputValidator validator,
			XRayWorkbenchView viewContainer) {
		
		super(parentShell, dialogTitle, dialogMessage,
				initialValue, validator);
		
		this.viewContainer = viewContainer;
	}
	
	public abstract void okPressed();
	
	protected abstract Control createDialogArea(Composite parent);
	
	protected Composite getSuperControl(Composite parent){
		return (Composite) super.createDialogArea(parent);
	}


}
