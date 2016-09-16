package org.malnatij.svplugin.views.actions.dialogs;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.malnatij.svplugin.util.FigureSnapshot;
import org.malnatij.svplugin.views.XRayWorkbenchView;
import org.malnatij.svplugin.views.actions.SnapShotAction;

public class SnapshotWarning extends MessageDialog {

	public SnapshotWarning(FigureSnapshot figureSnapshot,
			XRayWorkbenchView viewContainer,
			SnapShotAction snapShotAction) {
		super(Display.getCurrent().getActiveShell(),
				"Warning, this action may throw a memory heap Exception",
				null,
				"The figure that you are trying to save to a file is too big " +
				"and, depending on the amount of heap memory allocated by Eclipse, " +
				"this operation might throw a memory exception.\n\n" +
				"Press Ok to continue with the operation, or Cancel to abort it " +
				"and manually decrease the size of the image with the zooming actions " +
				"or providing more filters.",
				WARNING,
				new String[] { "Ok", "Cancel"},
				0);
		
		int button = open();
		
		switch(button){
		case 0:
			figureSnapshot.saveImage(viewContainer.getCurrentFiguresWrapper());
			break;

		default: break;
		}
		
	}

}
