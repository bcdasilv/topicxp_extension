/**
 * 
 */
package edu.wm.LDATopics.gui.TableView;


import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.PlatformUI;

import edu.wm.LDATopics.LDA.LDATopicMap;
import edu.wm.LDATopics.LDA.Topic;
import edu.wm.LDATopics.LDA.TopicMember;

import java.util.ArrayList;



public class TableViewContentProvider implements IStructuredContentProvider  {

	/**
	 * 
	 */
	private TableView view;
	private TableViewer viewer;
	//private JRipplesEIG EIG;
	private Object imageChangeListener;
	private LDATopicMap map;
	private int oldMapCount = 0;
	
	/**
	 * @param table
	 */
	TableViewContentProvider(TableView view) {
		this.view = view;
		this.viewer = view.getViewer();
	}

	

	public void inputChanged(Viewer v, Object oldInput, Object newInput) {
		this.viewer = (TableViewer) v;
		
	/*	if (EIG != null)
			JRipplesEIG.removeJRipplesEIGListener(this);
		EIG = (JRipplesEIG) newInput;
		if (EIG != null)
			{JRipplesEIG.addJRipplesEIGListener(this);
			 
			}
		*/
		
	}

	public void dispose() {
		//if (resourceSelectionTracker!=null) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getPages()[0].removeSelectionListener(resourceSelectionTracker);
	//	JRipplesEIG.removeJRipplesEIGListener(this);
	//	NodeImageRegistry.removeNodesImageChangedListener((NodeImageRegistry.NodesImageChangedListener)this.imageChangeListener);
	}

	public void setElements(LDATopicMap map) {
		this.map = map;
	}
	
	public Object[] getElements(Object parent) {
	//	Object[] array = FLATTT.searchResults.toArray();
		return map.topics;
	}

	public void refreshTable() {
		
		
		/*
		JRipplesEIGNodeEvent[] nodeContentEvents=event.getNodeEvents();
		
		for (int i=0;i<nodeContentEvents.length;i++) {
			
			if (!nodeContentEvents[i].getNode().isTop()) continue;
			switch (nodeContentEvents[i].getEventType()) {
				case JRipplesEIGNodeEvent.NODE_ADDED:addedNodes.add(nodeContentEvents[i].getNode());break;
				case JRipplesEIGNodeEvent.NODE_REMOVED:removedNodes.add(nodeContentEvents[i].getNode());break;
				case JRipplesEIGNodeEvent.NODE_MARK_CHANGED:changedMarkNodes.add(nodeContentEvents[i].getNode());break;
				case JRipplesEIGNodeEvent.NODE_PROBABILITY_CHANGED:changedProbabilityNodes.add(nodeContentEvents[i].getNode());break;
				case JRipplesEIGNodeEvent.NODE_MEMBER_CHANGED:changedMemberNodes.add(nodeContentEvents[i].getNode());break;
			}
		}
			*/
		
		
		/*
	
		
		if (addedNodes.size()>0) {
			
			viewer.add(addedNodes.toArray());
			
		}	
		
		if (changedMarkNodes.size()>0) {
			viewer.update(changedMarkNodes.toArray(),new String[] {FLATTTViewsConstants.SHORT_NAME_COLUMN_TITLE, FLATTTViewsConstants.MARK_COLUMN_TITLE});
		}
		
		if (changedProbabilityNodes.size()>0) {
			viewer.update(changedProbabilityNodes.toArray(),new String[] { FLATTTViewsConstants.PROBABILITY_COLUMN_TITLE});
		}
		
		if (changedMemberNodes.size()>0) {
			viewer.update(changedMemberNodes.toArray(),new String[] { FLATTTViewsConstants.SHORT_NAME_COLUMN_TITLE,FLATTTViewsConstants.FULL_NAME_COLUMN_TITLE});
		}
		
		if (removedNodes.size()>0) {
			viewer.getTable().setRedraw(false);
			viewer.remove(removedNodes.toArray());
			viewer.getTable().setRedraw(true);
		}*/
		if (oldMapCount != 0) {
			for ( int i = 0; i < oldMapCount; i++)
				viewer.remove(0);
		}
		
		if ((map != null) && (map.topics != null)) {
			oldMapCount = 0;
			for (Topic topic : map.topics) {
				viewer.add(topic);
				oldMapCount++;
				
				for (TopicMember word : topic.words) {
					viewer.add(word);
					oldMapCount++;
				}
				for (TopicMember doc : topic.documents) {
					viewer.add(doc);
					oldMapCount++;
				}
			}
		} else {
			viewer.add("No topic map has been generated yet.");				
			oldMapCount = 1;
		}
		//viewer.add(map.topics);
		//viewer.update((Object[])getElements(null), new String[] {FLATTTViewsConstants.SHORT_NAME_COLUMN_TITLE});
		
		//list = (Object[]) getElements(null);
		
		
	}

	public void JRipplesEIGChanged() { // final JRipplesEIGEvent event
		/*
		if (!event.hasNodeEvents()) return;
		try {
			if ((viewer == null))
				return;
			if ((viewer.getTable() == null))
				return;
			if (Thread.currentThread().getName().compareTo("main") == 0) {
				refreshTable(event);
			}
			else {
				PlatformUI.getWorkbench().getDisplay().syncExec(
						new Runnable() {
							public void run() {
								view.setFocus();
								refreshTable(event);
			
							}
						});
			}
		} catch (Exception e) {
			JRipplesLog.logError(e);
		}*/
	}

}