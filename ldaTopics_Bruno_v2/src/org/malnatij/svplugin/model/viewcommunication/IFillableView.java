package org.malnatij.svplugin.model.viewcommunication;

import org.eclipse.swt.widgets.Display;

public interface IFillableView {

	public void fillView(boolean createActions);
	
	public Display getDisplay();
}
