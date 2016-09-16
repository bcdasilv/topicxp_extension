package org.malnatij.svplugin.views.actions;

import org.eclipse.jface.action.Action;
import org.malnatij.svplugin.views.XRayWorkbenchView;

/**
 * An action that requires a drop down menu item
 * @author Jacopo Malnati
 *
 */
public abstract class DropDownImageAction
extends ActionImageDescriptor {

	public DropDownImageAction(String name,
			XRayWorkbenchView viewContainer) {
		
		super(name, Action.AS_DROP_DOWN_MENU, viewContainer);
	}

	/**
	 * A drop-down action must have a drop-down menu
	 */
	protected abstract void createAndSetMenu();
}
