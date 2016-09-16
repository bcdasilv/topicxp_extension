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

public class RegenerateAnalysisAction extends Action
{
	private TableViewContentProvider contentProvider;
	//private ProjectTopicMap map;
	
	public RegenerateAnalysisAction()
	{
		//this.concernDomain = concernDomain;
		//this.site = site;
		this.contentProvider = contentProvider;
		//this.map = map;
		setText(LDATopics
				.getResourceString("actions.RegenerateAnalysisAction.Label"));
		setImageDescriptor(LDATopics.imageDescriptorFromPlugin(
				LDATopics.PLUGIN_ID, "icons/table_refresh.png"));
		setToolTipText(LDATopics
				.getResourceString("actions.RegenerateAnalysisAction.ToolTip"));
	}

	@Override
	public void run()
	{
		LDATopics.getMap().updateTopicMaps();

		// And refresh the X-ray model too
		LDATopics.twoDimensionalView.refresh();
	}
}
