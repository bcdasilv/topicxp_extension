package edu.wm.LDATopics.gui.TopicsDependencyView;

import java.util.ArrayList;
import java.util.regex.Pattern;

import org.eclipse.draw2d.Figure;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.malnatij.svplugin.filters.Filter;
import org.malnatij.svplugin.graph.nodes.DVPackageNode;
import org.malnatij.svplugin.graph.nodes.Node;
import org.malnatij.svplugin.layouts.CircleLayout;
import org.malnatij.svplugin.model.ClassRepresentation;
import org.malnatij.svplugin.model.PackageRepresentation;
import org.malnatij.svplugin.util.Log;

import org.malnatij.svplugin.views.XRayWorkbenchView;
import org.malnatij.svplugin.views.handlers.DependencyHandler;

import edu.wm.LDATopics.LDATopics;
import edu.wm.LDATopics.LDA.LDATopicMap;
import edu.wm.LDATopics.LDA.ProjectTopicMap;
import edu.wm.LDATopics.LDA.Topic;
import edu.wm.LDATopics.gui.LDAViewFacade;

public class TopicDependencyHandler extends DependencyHandler{
	private ArrayList<DVTopicNode> nodesDVPackage = null;
	
	public TopicDependencyHandler(XRayWorkbenchView view) {
		super();
		this.view = view;
	}

	public void init(){
		figuresWrapperDefautWidth = -1;
		figuresWrapper = createContainerFigure();
		createDVPackageNodes();
		addDVPackageNodesToContent(figuresWrapper);
		circleLayout = createCirclePackageLayout();
	}
	
	private ProjectTopicMap getTopics() {
		//if (map == null) map = new ProjectTopicMap("jEdit");
		//return map;
		return LDATopics.getMap();
	}
	
	private void createDVPackageNodes(){
		nodesDVPackage = new ArrayList<DVTopicNode>();
		ProjectTopicMap proj = getTopics();
		LDATopicMap classMap = getTopics().getClassMap();
		LDATopicMap map = getTopics().getPackageMap();
		boolean topicRemoved = false;

		// Currently, we only display topics which have class documents in them
		// If a topic only has packages in it, we don't display it since there'd be nothing
		// particular for the user to explore
		for (int i = 0; i < map.topics.length; i++) {
			Topic packageTopic = map.topics[i];
			Topic classTopic = classMap.topics[i];
			// If the topic has no documents, ignore it
			if (classTopic.documents.size() != 0) {
				DVTopicNode currentNode =	new DVTopicNode(packageTopic, view);
				//System.out.println(currentNode.getName());
				nodesDVPackage.add(currentNode);
			} else
				topicRemoved = true;
			}
	
		// Inform user if topics were culled due to lack of documents
		if (topicRemoved) {
			MessageBox box = new MessageBox(Display.getCurrent().getShells()[0]);
			box.setMessage("After applying threshold or cutoff, some topics were empty. They will not be displayed");
			box.open();
		}
	}
	
	
	private void addDVPackageNodesToContent(Figure contents){
		for(int i = 0; i < nodesDVPackage.size(); i++){
			DVTopicNode currentFigure = nodesDVPackage.get(i);
			contents.add(currentFigure);
		}
	}
	
	private CircleLayout createCirclePackageLayout(){
		CircleLayout circleLayout = new LDACircleLayout();
		try{
			circleLayout.layoutTopics(nodesDVPackage,
					figuresWrapper, view,
					true, getCurrentFilters());
		}catch(Exception e){
			e.printStackTrace();
		}
		return circleLayout;
	}
	
	public void setViewContentBounds(){
		figuresWrapper.setBounds(
				circleLayout.getBounds(LDAViewFacade.TOPIC_DEPENDENCY,
						getCurrentFilters()));		
		
		figuresWrapperDefautWidth = figuresWrapper.getBounds().width;
	}
	
	public Node getFigureForClass(ClassRepresentation currentClass){
		return circleLayout.getNodeForPackage(currentClass.getPackageContainer());
	}

	@Override
	protected void applyZooming(ArrayList<Filter> filters) {
		try{	
			int size = nodesDVPackage.size();
			
			for(int i = 0; i < size; i++){
				DVTopicNode currentNode = nodesDVPackage.get(i);
				currentNode.zoomSize(zoomingCoefficient);
			}
			
			refreshView(filters);
			refreshView(filters);
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	
	@Override
	public void refreshView(ArrayList<Filter> filters){
		circleLayout.layoutTopics(nodesDVPackage, figuresWrapper, view,
				false, filters);
		setViewContentBounds();
		view.showPackageDependency();
	}

	@Override
	public void originalSize(ArrayList<Filter> filters) {
		zoomingCoefficient = 1;
		int size = nodesDVPackage.size();
		for(int i = 0; i < size; i++){
			DVTopicNode currentNode = nodesDVPackage.get(i);
			currentNode.setOriginalSize();
		}
		refreshView(filters);
	}

	@Override
	public void normalizeAllBorders(ArrayList<Filter> filters) {
		for(int i = 0; i < nodesDVPackage.size(); i++){
			if(!filterOutNode(nodesDVPackage.get(i), filters)){
				nodesDVPackage.get(i).removeBorderTag();
			}
		}
	}

	//LDAMOD: fix if necessary
	private boolean filterOutNode(DVTopicNode topicNode,
			ArrayList<Filter> filters) {
//		for(int i = 0; i < filters.size(); i++){
//			if(filters.get(i).matches(topicNode.getTopicInNode())){
//				return true;
//			}
//		}
		return false;
	}

	@Override
	public void normalizeBorderColoredOf(Color color, ArrayList<Filter> filters) {
		for(int i = 0; i < nodesDVPackage.size(); i++){
			DVTopicNode currentNode = nodesDVPackage.get(i);
			if(currentNode.isTagged(color) && !filterOutNode(currentNode, filters)){
				currentNode.removeBorderTag();
			}
		}
	}

	@Override
	public void invertSelection(ArrayList<Filter> filters) {
		for(int i = 0; i < nodesDVPackage.size(); i++){
			if(!filterOutNode(nodesDVPackage.get(i), filters)){
				addSelectedNode(nodesDVPackage.get(i), true, false);
			}
		}
	}

	@Override
	public void selectNodesTagged(Color color, ArrayList<Filter> filters) {
		for(int i = 0; i < nodesDVPackage.size(); i++){
			DVTopicNode currentNode = nodesDVPackage.get(i);
			if(currentNode.isTagged(color) && !filterOutNode(currentNode, filters)){
				addSelectedNode(currentNode, false, false);
			}
		}
	}

	@Override
	public void deSelectNodesTagged(Color color, ArrayList<Filter> filters) {
		for(int i = 0; i < selectedFigures.size(); i++){
			DVTopicNode currentNode = (DVTopicNode)selectedFigures.get(i);
			if(currentNode.isTagged(color) && !filterOutNode(currentNode, filters)){
				deselectNode(currentNode); // changing selectedFigures.size()
				deSelectNodesTagged(color, filters);
			} 
		}
	}
	
	@Override
	public void selectMatchingNodes(String pattern) {
		try{
			pattern = pattern.toLowerCase().trim();
			
			for(int i = 0; i < nodesDVPackage.size(); i++){
				DVTopicNode currentNode = (DVTopicNode) nodesDVPackage.get(i);
				if(!filterOutNode(currentNode, filters) &&
						Pattern.matches(pattern,
								currentNode.getTopicInNode().getName().toLowerCase())){
					addSelectedNode(currentNode, false, false);
				}
			}
		}catch(Exception e){
			Log.printError("selectMatchingNodes:" + e.toString());
		}
	}
	
	public String toString(){
		return "Package Dependency Handler";
	}

	
	/**
	 * So far, packages are flat, without any hierarchy
	 */
	@Override
	public void selectLeaves(ArrayList<Filter> filters, boolean select) {}
	@Override
	public void selectRoots(ArrayList<Filter> filters, boolean select) {}
	@Override
	public void selectChildrenOfSelectedNodes(
			ArrayList<Filter> filters, boolean select) {}
	@Override
	public void selectParentsOfSelectedNodes(boolean select) {}
	@Override
	public void selectSiblingsOfSelectedNodes(
			ArrayList<Filter> filters, boolean select) {}
	@Override
	public void selectClassKind(String kind, ArrayList<Filter> filters) {}
	@Override
	public void deSelectClassKind(String kind, ArrayList<Filter> currentFilters) {}
	@Override
	public void selectBranches(ArrayList<Filter> filters, boolean select) {}
	
	
}
