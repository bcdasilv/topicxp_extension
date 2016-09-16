package org.malnatij.svplugin.views.actions;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.malnatij.svplugin.SVPluginActivator;
import org.malnatij.svplugin.util.Log;
import org.malnatij.svplugin.views.XRayWorkbenchView;

import edu.wm.LDATopics.LDATopics;

public abstract class ActionImageDescriptor extends Action{
	protected XRayWorkbenchView viewContainer;
	
	public ActionImageDescriptor(String name, int type,
			XRayWorkbenchView viewContainer){
		super(name, type);
		this.viewContainer = viewContainer;
	}
	
	/**
	 * Returns the image descriptor with the given relative path.
	 */
	@SuppressWarnings("deprecation")
	protected ImageDescriptor getImageDescriptor(String relativePath) {
		Log.printSVPluginView("getting the image description for: " + relativePath);
		String iconPath = "icons/";
		try {
			SVPluginActivator a = SVPluginActivator.getDefault();
			URL installURL =
				LDATopics.getDefault().getDescriptor().getInstallURL();
				//SVPluginActivator.getDefault().getDescriptor().getInstallURL();
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

}
