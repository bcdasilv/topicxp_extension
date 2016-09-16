package org.malnatij.svplugin.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

public class OpenXRayViewActionDelegate 
extends ViewActionDelegate
implements IWorkbenchWindowActionDelegate {

	public void dispose() {
		
	}

	public void init(IWorkbenchWindow window) {
		this.window = window;

	}

	public void run(IAction action) {
		openView();
	}

	public void selectionChanged(IAction action, ISelection selection) {}

}
