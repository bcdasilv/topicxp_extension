package org.malnatij.svplugin.model.core;

import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.malnatij.svplugin.XRay;
import org.malnatij.svplugin.model.viewcommunication.IFillableView;
import org.malnatij.svplugin.model.viewcommunication.ViewFiller;

public class ModelingChangeListener extends XRay

implements IJobChangeListener {
	private IFillableView view;
	private ViewFiller viewFiller;
	
	public ModelingChangeListener(IFillableView view){
		this.view = view;
		this.viewFiller = new ViewFiller(view);
	}

	public void aboutToRun(IJobChangeEvent event) {}

	public void awake(IJobChangeEvent event) {}

	public void done(IJobChangeEvent event) {
		view.getDisplay().asyncExec(viewFiller);
	}

	public void running(IJobChangeEvent event) {}

	public void scheduled(IJobChangeEvent event) {}

	public void sleeping(IJobChangeEvent event) {}

}
