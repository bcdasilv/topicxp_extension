package edu.wm.LDATopics.gui;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.malnatij.svplugin.views.XRayWorkbenchView;
import org.malnatij.svplugin.views.actions.BrowserContentProvider;
import org.malnatij.svplugin.views.actions.dialogs.ExtendendInputDialog;
import org.malnatij.svplugin.views.actions.dialogs.validators.RegExStringValidator;

import edu.wm.LDATopics.LDATopics;

public class UserQueryDialog extends Dialog{
	Text thresholdLevel;
	private Text queryText;
	
	public UserQueryDialog() {
		super(Display.getCurrent().getActiveShell());

		open();
		
	}
	
	protected Control createDialogArea(Composite parent) {
		  // create composite
        Composite composite = (Composite) super.createDialogArea(parent);
        
        composite.getShell().setText("User Query");
        
        Composite container = composite;
//		container = getSuperControl(parent);
		container.setLayout(new GridLayout(2, false));
		
        Label label = new Label(container, SWT.WRAP);
        label.setText("Enter keywords, seperated by spaces:");
        GridData data = new GridData(SWT.LEFT, SWT.FILL, true, true,2,1);
        data.widthHint = convertHorizontalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH);
        label.setLayoutData(data);
        label.setFont(parent.getFont());
        
        queryText = new Text(container, SWT.NO_SCROLL | SWT.BORDER);
        queryText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true,2,1));
		
		Label stopLabel = new Label(container,SWT.HORIZONTAL);
		stopLabel.setText("Hellinger distance threshold: ");
		stopLabel.setLayoutData(new GridData(SWT.LEFT, SWT.NONE, false, false, 1, 1));
		
		thresholdLevel = new Text(container, SWT.NO_SCROLL | SWT.BORDER);
		thresholdLevel.setText(Double.toString(LDATopics.map.getClassMap().hellingerThreshold));
		thresholdLevel.setLayoutData(new GridData(SWT.LEFT, SWT.NONE, false, false, 1, 1));
		

        applyDialogFont(container);
		return container;
	}
	
	public void okPressed(){
		// Set the threshold to filter at
		LDATopics.map.getClassMap().hellingerThreshold = Double.valueOf(thresholdLevel.getText());
		// Run the user query
		LDATopics.map.getClassMap().userQuery(queryText.getText());


		// Return to the topic dependency view
		LDATopics.twoDimensionalView.showTopicDependency();
		
		// Refresh X-Ray model
		LDATopics.twoDimensionalView.refresh();

		// UPdate toolbar
		LDATopics.twoDimensionalView.updateToolbar();
		
		close();
	}

}
