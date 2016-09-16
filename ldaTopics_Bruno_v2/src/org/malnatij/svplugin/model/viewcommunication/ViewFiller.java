package org.malnatij.svplugin.model.viewcommunication;

import org.malnatij.svplugin.XRay;

import edu.wm.LDATopics.LDATopics;
import edu.wm.LDATopics.gui.TwoDimensionalView;
import edu.wm.LDATopics.gui.TopicsDependencyView.TopicDependencyHandler;

public class ViewFiller
extends XRay
implements Runnable{
	private IFillableView view;
	private int timesRunned = 0; // how many times the filler has been called?
	
	public ViewFiller(IFillableView view){
		this.view = view;
	}

	public void run() {

		// Switch views so we don't try to view a topic that's dissapeared
		LDATopics.twoDimensionalView.resetTopicContentsView();
		
		if(++timesRunned > 1){
			view.fillView(false);
		} else {
			view.fillView(true);
		}
		
		if (view instanceof TwoDimensionalView)
			((TwoDimensionalView)view).showPackageDependency();

	}

}
