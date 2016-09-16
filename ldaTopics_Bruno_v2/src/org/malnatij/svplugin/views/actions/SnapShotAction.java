package org.malnatij.svplugin.views.actions;

import java.util.GregorianCalendar;

import org.eclipse.draw2d.Figure;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.views.framelist.Frame;
import org.malnatij.svplugin.model.core.ModelNotPreviouslyScheduled;
import org.malnatij.svplugin.util.FigureSnapshot;
import org.malnatij.svplugin.views.XRayWorkbenchView;

public class SnapShotAction extends ActionImageDescriptor {
	XRayWorkbenchView viewContainer;

	public SnapShotAction(XRayWorkbenchView viewContainer) {
		super("Save a screenshot of the WHOLE View, in your home dir",
				Action.AS_PUSH_BUTTON, viewContainer);
		
		setImageDescriptor(getImageDescriptor("snapshot.gif"));
		this.viewContainer = viewContainer;
	}
	
	public void run(){
		snapShot();
	}
	
	public void snapShot(){
		Figure currentWrapper = viewContainer.getCurrentFiguresWrapper();
		normalizeFigureBounds(currentWrapper);
		new FigureSnapshot().createSnapshot(
				currentWrapper,
				getFileName(),
				viewContainer, this);
	}
	
	/**
	 * If the figure wrapper is smaller than the view itself, the snapshot
	 * will include the empty space if the real bounds are not normalized
	 */
	private void normalizeFigureBounds(Figure wrapper){
		if(viewContainer.getViewBounds().width >= wrapper.getBounds().width){
			viewContainer.getCurrentHandler().setViewContentBounds();
		}
	}
	
	private String getFileName(){
		String currentVisualization = viewContainer.getCurrentVisualizationDescription();
		
		String projectName;
		try {
			projectName = viewContainer.getModeledProject().getName();
		} catch (ModelNotPreviouslyScheduled e) {
			projectName = "No project has been modeled yet";
		}

		GregorianCalendar now = new GregorianCalendar();
		int year = now.get(GregorianCalendar.YEAR);
		int month = now.get(GregorianCalendar.MONTH);
		int day = now.get(GregorianCalendar.DAY_OF_MONTH);
		int hour = now.get(GregorianCalendar.HOUR);
		int minute = now.get(GregorianCalendar.MINUTE);
		int second = now.get(GregorianCalendar.SECOND);
		
		FileDialog dialog = new FileDialog(viewContainer.getSite().getShell(), SWT.SAVE);
		dialog.setFileName("screenshot"
		+ FigureSnapshot.FILE_EXTENSION);
	return	dialog.open();
		
//		+ year
//		+ normalizeNumber(month)
//		+ normalizeNumber(day)
//		+ "@"
//		+ normalizeNumber(hour)
//		+ normalizeNumber(minute)
//		+ normalizeNumber(second)
//		+ "_"
//		+ projectName
//		+ "_"
//		+ currentVisualization
//		+ FigureSnapshot.FILE_EXTENSION;
	}
	
	/**
	 * @return the path to the user home, where it will be saved the snapshot
	 */
	private String getHomePath(){
		return System.getProperty("user.home") 
		+ System.getProperty("file.separator");
	}
	
	/**
	 * @param number that might be < 10, therefore we have
	 *  to add a 0 in front of it
	 * @return the normalized number, as string
	 */
	private String normalizeNumber(int number){
		if(number < 10){
			return "0" + number;
		} else {
			return "" + number;
		}
	}

}
