package edu.wm.LDATopics.gui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import edu.wm.LDATopics.LDATopics;
import edu.wm.LDATopics.LDA.LDATopicMap;
import edu.wm.LDATopics.LDA.ProjectTopicMap;
import edu.wm.LDATopics.gui.TableView.TableViewContentProvider;

public class RegenerateAnalysisTableViewAction extends Action
{
	private TableViewContentProvider contentProvider;
	//private ProjectTopicMap map;
	
	public RegenerateAnalysisTableViewAction(TableViewContentProvider contentProvider)
	{
		//this.concernDomain = concernDomain;
		//this.site = site;
		this.contentProvider = contentProvider;
		//this.map = map;
		setText(LDATopics
				.getResourceString("actions.RegenerateAnalysisAction.Label"));
		//setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(
		//		LDATopics.PLUGIN_ID, "icons/text_search.png"));
		setToolTipText(LDATopics
				.getResourceString("actions.RegenerateAnalysisAction.ToolTip"));
	}

	@Override
	public void run()
	{
		LDATopics.getMap().updateTopicMaps();
		System.out.println("MWE Cohesion for Estimator.java: "+LDATopics.getMap().getMWECohesion("=JGibLDA-v.1.0/<jgibblda{Estimator.java[Estimator"));
		contentProvider.setElements(LDATopics.getMap().getPackageMap());
		contentProvider.refreshTable();
	}
}
