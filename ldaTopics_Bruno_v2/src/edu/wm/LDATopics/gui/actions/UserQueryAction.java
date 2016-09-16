package edu.wm.LDATopics.gui.actions;

import java.util.Iterator;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.malnatij.svplugin.actions.ViewActionDelegate;
import org.malnatij.svplugin.util.Log;

import edu.wm.LDATopics.LDATopics;
import edu.wm.LDATopics.gui.LDAOptionsDialog;
import edu.wm.LDATopics.gui.UserQueryDialog;

public class UserQueryAction 
extends Action {
	public UserQueryAction() {
		setText(LDATopics
				.getResourceString("actions.UserQueryAction.Label"));
		setImageDescriptor(LDATopics.imageDescriptorFromPlugin(
				LDATopics.PLUGIN_ID, "icons/magnifier.png"));
		setToolTipText(LDATopics
				.getResourceString("actions.UserQueryAction.ToolTip"));
	}
	

	public void run() {
		UserQueryDialog dialog = new UserQueryDialog();
			
	}

}
