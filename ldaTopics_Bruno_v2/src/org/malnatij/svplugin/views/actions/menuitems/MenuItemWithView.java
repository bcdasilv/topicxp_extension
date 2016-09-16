package org.malnatij.svplugin.views.actions.menuitems;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.malnatij.svplugin.SVPluginActivator;
import org.malnatij.svplugin.XRay;
import org.malnatij.svplugin.util.Log;
import org.malnatij.svplugin.views.XRayWorkbenchView;

import edu.wm.LDATopics.LDATopics;

public abstract class MenuItemWithView extends XRay{
	protected XRayWorkbenchView viewContainer;
	protected Menu menu;
	
	public MenuItemWithView(XRayWorkbenchView viewContainer, Menu menu){
		this.viewContainer = viewContainer;
		this.menu = menu;
	}
	
	@SuppressWarnings("deprecation")
	protected ImageDescriptor getImageDescriptor(String relativePath) {
		Log.printSVPluginView("getting the image description for: " + relativePath);
		String iconPath = "icons/";
		try {
			URL installURL =
				LDATopics.getDefault().getDescriptor().getInstallURL();
			URL url = new URL(installURL, iconPath + relativePath);
			Log.printSVPluginView("got: " + url.toString());
			return ImageDescriptor.createFromURL(url);
		}
		catch (MalformedURLException e) {
			// should not happen
			Log.printError(e.toString());
			return ImageDescriptor.getMissingImageDescriptor();
		}
	}
	
	public abstract MenuItem getMenuItem();

}
