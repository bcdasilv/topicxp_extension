package org.malnatij.svplugin.actions;

import java.util.Iterator;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.malnatij.svplugin.util.Log;

public class AnalyzeProjectActionDelegate 
extends ViewActionDelegate 
implements IObjectActionDelegate{

	private IWorkbenchPart targetPart;
	private ISelection selection;
	private static IProject projectToModel = null;

	public AnalyzeProjectActionDelegate() {

	}
	
	public static IProject getProjectToModel(){
		return projectToModel;
	}

	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		this.targetPart = targetPart;
	}

	@SuppressWarnings("unchecked")
	public void run(IAction action) {
		try{
			Iterator iter = ((IStructuredSelection) selection).iterator();

			while(iter.hasNext()){
				IProject projectToScan = (IProject)iter.next();
			
				if(projectToScan != null 
						&& projectToScan.isAccessible()){ // open project
					
					projectToModel = projectToScan;
					openView();
					
				} else {
					MessageDialog.openInformation(
							targetPart.getSite().getShell(),
							"X-Ray Warning",
							"It is not possible to analyze a closed project");
				}
			}
		}catch(Exception e){
			Log.printError("Exception in AnalyzeProjectActionDelegate.run()");
			e.printStackTrace();
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
		this.selection = selection;
		action.setEnabled(!selection.isEmpty());
	}

}
