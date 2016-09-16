package org.malnatij.svplugin.views;

import java.util.ArrayList;

import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.malnatij.svplugin.filters.Filter;
import org.malnatij.svplugin.util.Log;

import edu.wm.LDATopics.gui.TwoDimensionalView;

/**
 * This thread will be alive as long as he will add the editor listener
 * @author Jacopo Malnati
 *
 */
public class ListenerAdder extends Thread{
	private XRayWorkbenchView theView = null;
	private ArrayList<Filter> filters;
	
	public ListenerAdder(XRayWorkbenchView twoDimensionalView, ArrayList<Filter> filters){
		Log.printEditorListener("Creating ListenerAdder()");
		this.theView = twoDimensionalView;
		this.filters = filters;
		start();
	}
	
	public void run(){
		try{
			IWorkbenchWindow[] windows = PlatformUI.getWorkbench().getWorkbenchWindows();
			for (int i = 0; i < windows.length; i++) {
				IWorkbenchWindow window = windows[i];
				if(window != null){
					IWorkbenchPage activePage = window.getActivePage();
					while(activePage == null){
						Log.printEditorListener("null active page, retrying");
						sleep(500); // let it load..ugly hack
						activePage = window.getActivePage();
					}
					activePage.addPartListener(new EditorPartListener(theView, filters));
					Log.printEditorListener("added EditorPartListener");
				}
			}
		} catch (Exception e){
			e.printStackTrace();
		}
	}

}
