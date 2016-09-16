package org.malnatij.svplugin.views.actions.dialogs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.malnatij.svplugin.views.XRayWorkbenchView;
import org.malnatij.svplugin.views.actions.BrowserContentProvider;
import org.malnatij.svplugin.views.actions.dialogs.validators.RegExStringValidator;

public class RegExFilterDialog extends ExtendendInputDialog{
	
	public RegExFilterDialog(XRayWorkbenchView viewContainer) {
		super(Display.getCurrent().getActiveShell(),
				"Enter a Regular Expression",
				"Enter a Regular Expression",
				"",
				new RegExStringValidator(),
				viewContainer);
		
		open();
		
	}
	
	protected Control createDialogArea(Composite parent) {
		Composite container = null;
		container = getSuperControl(parent);
		
		Browser browser = new Browser(container, SWT.NONE);
		browser.setText(BrowserContentProvider.getInstance().getRegExHelp());
		browser.setLayoutData(new GridData(600, 300));	

		return container;
	}
	
	public void okPressed(){
		viewContainer.selectMatchingNodes(getValue());
		close();
	}

}
