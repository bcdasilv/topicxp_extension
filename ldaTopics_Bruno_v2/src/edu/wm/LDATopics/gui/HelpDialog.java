package edu.wm.LDATopics.gui;

import org.eclipse.core.resources.IProject;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.malnatij.svplugin.views.XRayWorkbenchView;

import edu.wm.LDATopics.LDATopics;
import edu.wm.LDATopics.gui.actions.AnalyzeProjectActionDelegate;

public class HelpDialog 
extends Dialog{
	
	public HelpDialog(AnalyzeProjectActionDelegate analyzeProjectActionDelegate, XRayWorkbenchView viewContainer,
			IProject projectToScan) {
		
		super(Display.getCurrent().getActiveShell());
	
		open();
	}

	public HelpDialog() {
		super(Display.getCurrent().getActiveShell());	
		setBlockOnOpen(false);
		open();
	}

	
	@Override
	protected Control createDialogArea(Composite parent) {
		//Composite container = parent;//getSuperControl(parent);
	//	parent.setLayout(new FillLayout(SWT.HORIZONTAL));
		Composite container  = new Composite(parent, 0);
		int colWidth = 350;
		int height = 560;
		container.setSize(colWidth*2, height);
		container.setLayout(new GridLayout(2, false));
	
		Composite left = new Composite(container,0);
		GridLayout l = new GridLayout(1, false);
		l.verticalSpacing = 9;
		left.setLayout(l);
		GridData data = new GridData(colWidth, height);
		left.setLayoutData(data);
		Composite right = new Composite(container,0);
		l = new GridLayout(1, false);
		l.verticalSpacing = 9;
		right.setLayout(l);
		
		 data = new GridData(colWidth, height);
//		data.verticalAlignment = SWT.END;
		right.setLayoutData(data);
		
		
		Label label = new Label(left,SWT.BOLD);

		Font bold = new Font(label.getFont().getDevice(), label.getFont().getFontData()[0].getName(), label.getFont().getFontData()[0].getHeight(), SWT.BOLD);
		label.setText("Getting Started");
		label.setFont(bold);
		//label.setLayoutData(new GridData(colWidth,100));
		
		label = new Label(left,SWT.WRAP);
		label.setText("To begin using the tool, right click on a project and select \"Explore With LDA\". The topic model will then be generated; this will take a few minutes, or longer for large projects. Once this is done you can explore the model using the following two views; the model will not need to be recomputed unless you change it's options.");
		label.setLayoutData(new GridData(colWidth-5,90));
		
		
		label = new Label(left,SWT.BOLD);
		label.setText("Topic Dependency View");
		label.setFont(bold);
		label = new Label(left,SWT.HORIZONTAL);
		label.setImage(LDATopics.imageDescriptorFromPlugin(
				LDATopics.PLUGIN_ID, "images/topicDependencies.png").createImage());
		label.setAlignment(SWT.CENTER);
		label.setLayoutData(new GridData(colWidth,260));
		
		label = new Label(left,SWT.WRAP);
		label.setText("Each box represents a topic. The words most relevant to the topic are displayed on the first line, followed by the most relevant classes and packages.");
		label.setLayoutData(new GridData(colWidth,50));
		label = new Label(left,SWT.WRAP);
		label.setText("Arrows represent dependency links between topics (calls in one topic to methods in another topic). Color and size of arrow corresponds to number of method calls.");
		label.setLayoutData(new GridData(colWidth,45));
		
		label = new Label(left,SWT.WRAP);
		label.setText("Clicking on a topic-box will take you to the Topic Contents View.");
		label.setLayoutData(new GridData(colWidth,40));
		
		label = new Label(right,SWT.BOLD);
		label.setText("Topic Contents View");
		label.setFont(bold);
		label = new Label(right,SWT.HORIZONTAL);
		label.setImage(LDATopics.imageDescriptorFromPlugin(
				LDATopics.PLUGIN_ID, "images/topicContents.png").createImage()	);
		label.setAlignment(SWT.CENTER);
		label.setLayoutData(new GridData(colWidth,235));
		label = new Label(right,SWT.WRAP);
		label.setText("Each colored box represents a class. The size of the box corresponds to the probability with which it belongs to the topic. Each grey box corresponds to a package, so you can view the package heirarchy and the classes within that heirarchy.");
		label.setLayoutData(new GridData(colWidth,60));

		
		label = new Label(right,SWT.HORIZONTAL);
		label.setImage(LDATopics.imageDescriptorFromPlugin(
				LDATopics.PLUGIN_ID, "images/spectrum_dark.png").createImage()	);
		label.setLayoutData(new GridData(colWidth,60));
		label.setAlignment(SWT.CENTER);
		
		label = new Label(right,SWT.WRAP);
		label.setText("The color of the box corresponds to the class's MWE Cohesion. Red maps to a value of about 0, green to around .5, and blue/purple to around 1.");
		label.setLayoutData(new GridData(colWidth,45));

		label = new Label(right,SWT.WRAP);
		label.setText("Right clicking takes you back to the Topic Dependency View. Double clicking on a class opens the class's source code. Hovering over a class shows you a tooltip with more information on the class and it's place in the model.");		
		label.setLayoutData(new GridData(colWidth-5,60));
		return parent;
	}
	
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
				true);
	}
	
	@Override
	public void okPressed() {
		
		this.close();

	}	
}
