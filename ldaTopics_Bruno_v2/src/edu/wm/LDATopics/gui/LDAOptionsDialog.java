package edu.wm.LDATopics.gui;

import java.io.FileNotFoundException;
import java.text.DecimalFormat;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
//import org.eclipse.swt.internal.cocoa.OS;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.progress.UIJob;
import org.malnatij.svplugin.views.XRayWorkbenchView;

import edu.wm.LDATopics.LDATopics;
import edu.wm.LDATopics.LDA.LDAOptions;
import edu.wm.LDATopics.LDA.LDATopicUpdateRule;
import edu.wm.LDATopics.gui.actions.AnalyzeProjectActionDelegate;

public class LDAOptionsDialog 
extends Dialog{
	private Spinner topics;
	private Spinner iterations;
	private Text alpha;
	private Text beta;

	private Text customStopwords;
	
	private IProject projectToScan;
	
	private AnalyzeProjectActionDelegate opener;

	// Are we changing options or analyzing a new project?
	boolean scanningNewProject = true;
	
	// Options we're changing
	private LDAOptions options;
	private Button thresholdButton;
	private Text thresholdLevel;
	private Button cutoffButton;
	private Text cutoffPoint;
	private Label alphaLabel;
	private Label betaLabel;
	private Label stopLabel;
	private Composite left;
	private Composite right;
	private Composite container;
	
	public LDAOptionsDialog(AnalyzeProjectActionDelegate analyzeProjectActionDelegate, XRayWorkbenchView viewContainer,
			IProject projectToScan) {
		
		super(Display.getCurrent().getActiveShell());
		
		scanningNewProject = true;
		this.projectToScan = projectToScan;
		this.opener = analyzeProjectActionDelegate;
		
		options = new LDAOptions();
		try {
			options.loadMetadata(LDATopics.getDefault().getStateLocation().append("/"+projectToScan.getName()+"/metadata").toOSString());
		} catch (FileNotFoundException e) {
			// No options to load, just use defaults
			
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}

		open();
	}

	public LDAOptionsDialog() {
		super(Display.getCurrent().getActiveShell());	
		scanningNewProject = false;
		options = LDATopics.map.getOptions();
		open();
	}

	
	@Override
	protected Control createDialogArea(Composite parent) {
		container = parent;//getSuperControl(parent);
		
		parent.setLayout(new GridLayout(2,false));
		int cwidth = 225;
		int cheight = 180;
		
		left = new Composite(container,0);
		GridLayout l = new GridLayout(2, false);
		l.verticalSpacing = 2;
		left.setLayout(l);
		GridData data = new GridData(cwidth,cheight);
		left.setLayoutData(data);
		right = new Composite(container,0);
		l = new GridLayout(2, false);
		l.verticalSpacing = 2;
		right.setLayout(l);
		data = new GridData(cwidth-60,cheight);
			right.setLayoutData(data);
		
		
		Label label = new Label(left,SWT.HORIZONTAL);
		Font bold = new Font(label.getFont().getDevice(), label.getFont().getFontData()[0].getName(), label.getFont().getFontData()[0].getHeight(), SWT.BOLD);
		label.setFont(bold);
		
		label.setText("LDA Parameters");
		label.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, true, false, 2, 1));

		
		Label topicsLabel = new Label(left,SWT.HORIZONTAL);
		topicsLabel.setText("Number Topics:");
		topicsLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
		topics = new Spinner (left, SWT.HORIZONTAL);
		topics.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		topics.setMinimum(2);
		topics.setMaximum(1000);
		topics.setSelection(options.numberTopics);
		topics.setIncrement(1);
		topics.addListener (SWT.Modify, new Listener () {
			public void handleEvent (Event e) {
				int numtopics = topics.getSelection();

				DecimalFormat round = new DecimalFormat("#.##");
				alpha.setText(round.format(new Double(50.0 / Double.valueOf(numtopics))));
				alpha.pack();
			}
		});
		
		Label itersLabel = new Label(left,SWT.HORIZONTAL);
		itersLabel.setText("Number Iterations:");
		iterations = new Spinner (left, SWT.HORIZONTAL);
		iterations.setMinimum(1);
		iterations.setMaximum(10000);
		iterations.setSelection(options.numberIterations);
		iterations.setIncrement(1);
		
//		Group group = new Group(left, SWT.NONE);
//		group.setText("Advanced Settings");
//		group.setLayout(new GridLayout(2, false));
//		group.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, false, 2, 3));
//	
		label = new Label(left,SWT.HORIZONTAL);
		label.setFont(bold);
		
		label.setText("");
		label.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, true, false, 2, 1));

		
		label = new Label(left,SWT.HORIZONTAL);
		label.setFont(bold);
		
		label.setText("Advanced LDA Parameters");
		label.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, true, false, 2, 1));

		Composite alphabetaComposite = new Composite(left, SWT.NONE);
		l = new GridLayout(2, false);
		l.verticalSpacing = 2;
		l.marginBottom =0;
		l.marginHeight = 0;
		l.marginLeft = 0;
		l.marginRight = 0;
		l.marginTop = 0;
		l.marginWidth = 0;
		alphabetaComposite.setLayout(l);
		alphabetaComposite.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, false,2,2));
		
		alphaLabel = new Label(alphabetaComposite,SWT.HORIZONTAL);
		alphaLabel.setText("Alpha:"); // (default is 50/#topics)
		alphaLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));

		alpha = new Text(alphabetaComposite, SWT.BORDER | SWT.NO_SCROLL);
		alpha.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, true, false, 1, 1));

        GC gc = new GC (alpha);
        FontMetrics fm = gc.getFontMetrics ();
        int width = 10 * fm.getAverageCharWidth ();
        int height = fm.getHeight ();
        gc.dispose ();
		alpha.setText(new Double(options.alpha).toString());
        alpha.setSize (alpha.computeSize (width, height));
		alpha.addListener (SWT.Verify, new Listener () {
			public void handleEvent (Event e) {
				String string = e.text;
				char [] chars = new char [string.length ()];
				string.getChars (0, chars.length, chars, 0);
				for (int i=0; i<chars.length; i++) {
					if (!(('0' <= chars [i] && chars [i] <= '9') || (chars[i] == '.'))) {
						e.doit = false;
						return;
					}
				}
			}
		});

		
		betaLabel = new Label(alphabetaComposite,SWT.HORIZONTAL);
		betaLabel.setText("Beta:");
		betaLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));

		beta = new Text(alphabetaComposite,  SWT.BORDER | SWT.NO_SCROLL);	
		beta.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, true, false, 1, 1));
        gc = new GC (beta);
        fm = gc.getFontMetrics ();
        width = 10 * fm.getAverageCharWidth ();
        height = fm.getHeight ();
        gc.dispose ();
        beta.setSize (beta.computeSize (width, height)); 
		beta.setText(Double.toString(options.beta));
		beta.addListener (SWT.Verify, new Listener () {
			public void handleEvent (Event e) {
				String string = e.text;
				char [] chars = new char [string.length ()];
				string.getChars (0, chars.length, chars, 0);
				for (int i=0; i<chars.length; i++) {
					if (!(('0' <= chars [i] && chars [i] <= '9') || (chars[i] == '.'))) {
						e.doit = false;
						return;
					}
				}
			}
		});
		
		stopLabel = new Label(left,SWT.HORIZONTAL);
		stopLabel.setText("Custom stopwords, comma seperated:");
		stopLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 2, 1));

		customStopwords = new Text(left,  SWT.BORDER | SWT.NO_SCROLL);
        gc = new GC (customStopwords);
        fm = gc.getFontMetrics ();
        //width = 10 * fm.getAverageCharWidth ();
        width = cwidth-15;
        height = fm.getHeight ();
        gc.dispose ();
        customStopwords.setSize (width,height);//customStopwords.computeSize (width, height)); 
		customStopwords.setText(options.getStopWordsAsString());
		data = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
		customStopwords.setLayoutData(data);
		
		Label topicMethodLabel = new Label(right,SWT.HORIZONTAL);
		topicMethodLabel.setText("Parameters for Assigning \nDocuments to Topics");
		topicMethodLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 2, 1));
		topicMethodLabel.setFont(bold);

		thresholdButton = new Button(right, SWT.RADIO);
		thresholdButton.setSelection(options.useThreshold);
		thresholdButton.setText("Threshold level:");
		thresholdButton.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
		thresholdLevel = new Text(right, SWT.SINGLE | SWT.BORDER);
		thresholdLevel.setText(Double.toString(options.topicAssociationThreshold));
		thresholdLevel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		
		cutoffButton = new Button(right, SWT.RADIO);
		cutoffButton.setSelection(!options.useThreshold);
		cutoffButton.setText("Cutoff point:");
		cutoffButton.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
		cutoffPoint = new Text(right, SWT.SINGLE | SWT.BORDER);
		cutoffPoint.setText(Integer.toString(options.topicAssociationCutoff));
		cutoffPoint.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
	
		// TODO: options for mapping things to class size,
		// options for mapping things to color/etc
		
		return parent;
	}

	
	protected Control createButtonBar(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		// create a layout with spacing and margins appropriate for the font
		// size.
		GridLayout layout = new GridLayout();
		layout.numColumns = 0; // this is incremented by createButton
		layout.makeColumnsEqualWidth = true;
		layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
		layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
		layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
		layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
		composite.setLayout(layout);
		GridData data = new GridData(SWT.END, SWT.CENTER, false, false, 2, 1);
		composite.setLayoutData(data);
		composite.setFont(parent.getFont());
		
		// Add the buttons to the button bar.
		createButtonsForButtonBar(composite);
		return composite;
	}
	
	@Override
	public void okPressed() {
		LDAOptions newOptions = new LDAOptions();
		newOptions.modificationStamp = options.modificationStamp;
		newOptions.numberTopics = topics.getSelection();
		newOptions.numberIterations = iterations.getSelection();
		newOptions.alpha = Double.valueOf(alpha.getText());
		newOptions.beta = Double.valueOf(beta.getText());
		newOptions.customStopWords = customStopwords.getText().split(",");
		newOptions.useThreshold = thresholdButton.getSelection();
		newOptions.topicAssociationThreshold = Double.valueOf(thresholdLevel.getText());
		newOptions.topicAssociationCutoff = Integer.valueOf(cutoffPoint.getText());

		
		this.close();
		
		if (scanningNewProject) {
			LDATopics.setProject(projectToScan, newOptions);
			Job openView = new openViewJob(opener);
			openView.schedule();
		} else {
			// Just update the options
			boolean refreshNeeded = LDATopics.map.setOptions(newOptions, false);
			
			// And refresh the X-ray model too
			if (refreshNeeded)
				LDATopics.twoDimensionalView.refresh();
		}
	}

	// Job for opening the view when other processing is done
	public class openViewJob extends UIJob {
		private AnalyzeProjectActionDelegate opener;

		public openViewJob(AnalyzeProjectActionDelegate opener) {
			super("Opening LDA Topics Visualization");
			this.opener = opener;
			this.setPriority(Job.SHORT);
			this.setRule(new LDATopicUpdateRule());
		}

		public IStatus runInUIThread(IProgressMonitor monitor) {
			if (!LDATopics.getDefault().getPreferenceStore().getBoolean("helpShown")){
				HelpDialog help = new HelpDialog();
				LDATopics.getDefault().getPreferenceStore().setValue("helpShown", true);
		}
			
			this.opener.openView();
			return Status.OK_STATUS;
		}
	}
	
}
