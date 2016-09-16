package org.malnatij.svplugin.views.actions.dialogs;

import java.util.StringTokenizer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Scale;
import org.malnatij.svplugin.views.XRayWorkbenchView;
import org.malnatij.svplugin.views.actions.dialogs.validators.NumberRangeStringValidator;

public class HideEdgesDialog 
extends ExtendendInputDialog{
	private Scale scale;
	private int MaxWeigth;

	public HideEdgesDialog(XRayWorkbenchView viewContainer,
			int maxWeigth) {
		
		super(Display.getCurrent().getActiveShell(),
				"Hide incoming or outgoing Dependency Edges",
				"Specify the range (expressed in weigths) of the VISIBLE edges, or the" +
				" limit up to which the edges must be HIDDEN.",
				"",
				new NumberRangeStringValidator(),
				viewContainer);
		
		this.MaxWeigth = maxWeigth;
		
		open();
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = getSuperControl(parent);

		scale = new Scale (container, SWT.HORIZONTAL);
		scale.setLayoutData(new GridData(600, 20));
		scale.setMaximum(MaxWeigth);
		scale.setPageIncrement(1);
		scale.addSelectionListener(new ScaleListener(viewContainer, scale));

		
		return container;
	}
@Override
protected void createButtonsForButtonBar(Composite parent) {
	super.createButtonsForButtonBar(parent);
	this.getButton(OK).setText("Set Range");
	this.getButton(CANCEL).setText("Done");
}
	@Override
	public void okPressed() {
		

	

		if(scale.getSelection() == 0){
			String value = getValue();
			StringTokenizer st = new StringTokenizer(value, "-");

			Integer from = new Integer(st.nextToken());
			Integer to = new Integer(st.nextToken());
			
			int[] range = {from.intValue(), to.intValue()};
			
			viewContainer.showJustDependencyInRange(range);
		}

	}


}
