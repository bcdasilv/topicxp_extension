package org.malnatij.svplugin.actions;

import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.malnatij.svplugin.XRay;
import org.malnatij.svplugin.util.Log;

public abstract class ViewActionDelegate extends XRay{
	protected IWorkbenchWindow window;
	
	public void openView() {
		// open the view
		try {
			window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();

			if (window == null) {
				Log.printError("null window in ViewActionDelegate.openView()");
				return;
			} else {
				Log.printSVPluginView("got a window to fill with a view");
			}

			IWorkbenchPage page = window.getActivePage();
			if (page == null) {
				Log.printError("null page in ViewActionDelegate.openView()");
				return;
			} else {
				Log.printSVPluginView("got a page to fill with a view");
			}

			IViewPart oldView = page.findView(XRay.ID);
			if (oldView != null) {
				// found an old view
				window.getActivePage().hideView(oldView);
			}
			
			// show the view
			page.showView(XRay.ID);
		
		} catch (PartInitException e) {
			Log.printError("Error while showing the view");
			e.printStackTrace();
		}
	}
}
