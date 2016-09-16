package edu.wm.LDATopics.gui;

import java.util.ArrayList;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.draw2d.ScrollPane;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.malnatij.svplugin.XRay;
import org.malnatij.svplugin.filters.Filter;
import org.malnatij.svplugin.graph.nodes.Node;
import org.malnatij.svplugin.model.ClassRepresentation;
import org.malnatij.svplugin.util.Log;
import org.malnatij.svplugin.views.XRayWorkbenchView;
import org.malnatij.svplugin.views.handlers.ClassDependencyHandler;
import org.malnatij.svplugin.views.handlers.PackageDependencyHandler;
import org.malnatij.svplugin.views.handlers.PolymetricViewHandler;
import org.malnatij.svplugin.views.handlers.SystemComplexityHandler;

import edu.wm.LDATopics.LDATopics;
import edu.wm.LDATopics.LDA.Topic;
import edu.wm.LDATopics.gui.TwoDimensionalView;
import edu.wm.LDATopics.gui.TopicContentsView.TopicContentsHandler;
import edu.wm.LDATopics.gui.TopicsDependencyView.TopicDependencyHandler;

public class LDAViewFacade extends XRay{
	// viewParent
	private Composite parent = null;
	// viewPanel
	private ScrollPane panel = null;
	// lws
	private LightweightSystem lws = null;
	
	// constants
	public static final String TOPIC_CONTENTS = "topic contents";
	public static final String CLASS_DEPENDENCY = "class dependency";
	public static final String TOPIC_DEPENDENCY = "topic dependency";
	public static String CURRENT_VISUALIZATION = TOPIC_DEPENDENCY;
	
	//Handlers
	private TopicContentsHandler topicContentsHandler = null;
	private ClassDependencyHandler classDependencyHandler = null;
	private TopicDependencyHandler packageDependencyHandler = null;
	
	public LDAViewFacade(Composite parent, ScrollPane panel, LightweightSystem lws){
		this.parent = parent;
		this.panel = panel;
		this.lws = lws;
	}
	
	private void setCurrentViewTo(String viewKind){
		CURRENT_VISUALIZATION = viewKind;
	}
	
	public void setTopicContentsView(){
		setCurrentViewTo(TOPIC_CONTENTS);
	}
	
	public void setClassDependencyView(){
		setCurrentViewTo(TOPIC_DEPENDENCY);
	}
	
	public void setTopicDependencyView(){
		setCurrentViewTo(TOPIC_DEPENDENCY);
	}
	
	public void initSystemComplexityHandler(){
		topicContentsHandler.init();
	}
	
	public void initClassDependencyHandler(){
		classDependencyHandler.init();
	}
	
	public void initPackageDependencyHandler(){
		packageDependencyHandler.init();
	}

	public void initAllViewHandlers() {
		initSystemComplexityHandler();
		initClassDependencyHandler();
		initPackageDependencyHandler();
	}

	public void createViewHandlers(XRayWorkbenchView twoDimensionalView) {
		topicContentsHandler = new TopicContentsHandler(twoDimensionalView);
		classDependencyHandler = new ClassDependencyHandler(twoDimensionalView);
		packageDependencyHandler = new TopicDependencyHandler(twoDimensionalView);
	}

	public void swapViewWith(Figure toSwap, String description,
			ScrollPane panel, LightweightSystem lws) {
		if(toSwap != null){
			// Add a mouse listener for easy navigation between visualizations
			toSwap.addMouseListener(new MouseListener(){
					@SuppressWarnings("unused")
					public void mouseClicked(MouseEvent e) {}
					public void mouseDoubleClicked(MouseEvent arg0) {
					//	addToSelectedNodes(false);
					}
					public void mousePressed(MouseEvent arg0) {
						// Transition back to the topic dependency view
						if (CURRENT_VISUALIZATION == TOPIC_CONTENTS)
							showTopicDependency();
						
					}
					public void mouseReleased(MouseEvent arg0) {}
				});
			panel.setContents(toSwap);
			if(description.equals(TOPIC_DEPENDENCY)){
				CURRENT_VISUALIZATION = TOPIC_DEPENDENCY;
				packageDependencyHandler.setViewContentBounds();
			} else if(description.equals(CLASS_DEPENDENCY)){
				CURRENT_VISUALIZATION = CLASS_DEPENDENCY;
				classDependencyHandler.setViewContentBounds();
			} else {
				CURRENT_VISUALIZATION = TOPIC_CONTENTS;
				topicContentsHandler.setViewContentBounds();
			}
			lws.setContents(panel);
			panel.repaint();
		} else {
			if(description.equals(CLASS_DEPENDENCY)){
				CURRENT_VISUALIZATION = CLASS_DEPENDENCY;
				classDependencyHandler.init();
				swapViewWith(classDependencyHandler.getFiguresWrapper(),
						description, panel, lws);
			} else if(description.equals(TOPIC_DEPENDENCY)){
				CURRENT_VISUALIZATION = TOPIC_DEPENDENCY;
				packageDependencyHandler.init();
				swapViewWith(packageDependencyHandler.getFiguresWrapper(),
						description, panel, lws);
			} else {
				Log.printError("ViewFacade.swapViewWith()," +
						" null swap for: " + description);
			}
		}
	}

	public PolymetricViewHandler getCurrentHandler() {
		if(CURRENT_VISUALIZATION.equals(CLASS_DEPENDENCY)){
			return classDependencyHandler;
		} else if(CURRENT_VISUALIZATION.equals(TOPIC_DEPENDENCY)){
			return packageDependencyHandler;
		} else if(CURRENT_VISUALIZATION.equals(TOPIC_CONTENTS)){
			return topicContentsHandler;
		} else {
			Log.printError("Unable to get the current view handler");
			return null;
		}
	}

	public void centerViewOnClass(ClassRepresentation currentClass) {
		Node currentNode = null;
		if(CURRENT_VISUALIZATION.equals(CLASS_DEPENDENCY)){
			currentNode = classDependencyHandler.getFigureForClass(currentClass);
			addSelectedNode(currentNode, false, true);
		} else if(CURRENT_VISUALIZATION.equals(TOPIC_DEPENDENCY)){
			currentNode = packageDependencyHandler.getFigureForClass(currentClass);
			deHilightNodes();
			addHighlightNode(currentNode);
		} else if(CURRENT_VISUALIZATION.equals(TOPIC_CONTENTS)){
			currentNode = topicContentsHandler.getFigureForClass(currentClass);
		} else {
			Log.printError("unable to find view: " + CURRENT_VISUALIZATION);
			return;
		}
		centerViewOnNode(currentNode);
	}
	
	private void deHilightNodes(){
		getCurrentHandler().deHilightNodes();
	}

	public void addSelectedNode(Node selectedNode, boolean deselectDuplicate, boolean singleSelection) {
		getCurrentHandler().addSelectedNode(
				selectedNode, deselectDuplicate, singleSelection);	
	}

	public void addHighlightNode(Node currentNode) {
		currentNode.setHighlightBorderColor();
		getCurrentHandler().addHighlightNode(currentNode);
	}

	public void centerViewOnNode(Node currentNode) {
		if(currentNode != null){
			getCurrentHandler().centerViewOnNodeInPanel(currentNode, panel, parent);
		}
	}

	public void setOriginScrolling() {
		getCurrentHandler().setOriginScrolling(panel);
	}

	public void handleInterfaceLinkRequest(ArrayList<Filter> filters) {
//		if(CURRENT_VISUALIZATION.equals(SYSTEM_COMPLEXITY)){
//			systemComplexityHandler.handleInterfaceLinkRequest(filters);
//		} // TODO else
	}

	public void handleOutgoingDependencyLinkRequest(ArrayList<Filter> filters) {
//		if(CURRENT_VISUALIZATION.equals(SYSTEM_COMPLEXITY)){
//			systemComplexityHandler.handleOutgoingDependencyLinkRequest(filters);
//		}
	}

	public void handleIncomingDependencyLinkRequest(ArrayList<Filter> filters) {
//		if(CURRENT_VISUALIZATION.equals(SYSTEM_COMPLEXITY)){
//			systemComplexityHandler.handleIncomingDependencyLinkRequest(filters);
//		}
	}

	public void handleDependencyLinkRequest(ClassRepresentation classToLink,
			ArrayList<Filter> filters) {
//		if(CURRENT_VISUALIZATION.equals(SYSTEM_COMPLEXITY)){
//			systemComplexityHandler.handleOutgoingDependencyLinkRequest(classToLink, filters);
//		}
	}

	public void showClassDependency() {
		swapViewWith(classDependencyHandler.getFiguresWrapper(), CLASS_DEPENDENCY, panel, lws);
	}

	public void showTopicDependency() {
		swapViewWith(packageDependencyHandler.getFiguresWrapper(), TOPIC_DEPENDENCY, panel, lws);
	}

	public void showTopicContents() {
		swapViewWith(topicContentsHandler.getFiguresWrapper(), TOPIC_CONTENTS, panel, lws);
	}

	public String getCurrentVisualizationDescription() {
		return CURRENT_VISUALIZATION;
	}

	private void displayOriginalSize(ArrayList<Filter> filters){
		getCurrentHandler().originalSize(filters);
		setOriginScrolling();
	}
	
	public void originalSize(ArrayList<Filter> filters) {
		if(CURRENT_VISUALIZATION.equals(TOPIC_CONTENTS)){
			displayOriginalSize(filters);
		} else { // ugly hack FIXME
			displayOriginalSize(filters);
			displayOriginalSize(filters);
		}
	}
	
	private void displayFitSize(ArrayList<Filter> filters){
		getCurrentHandler().fitSize(filters);
		setOriginScrolling();
	}

	public void fitSize(ArrayList<Filter> filters) {
		if(CURRENT_VISUALIZATION.equals(TOPIC_CONTENTS)){
			displayFitSize(filters);
		} else { // ugly hack FIXME
			displayFitSize(filters);
			displayFitSize(filters);
		}
	}

	public void refreshView(ArrayList<Filter> currentFilters) {
		getCurrentHandler().refreshView(getCurrentFilters());
	}

	public ArrayList<Filter> getCurrentFilters() {
		return getCurrentHandler().getCurrentFilters();
	}

	public Figure getFiguresWrapper() {
		return getCurrentHandler().getFiguresWrapper();
	}

	public void openAllNodes() {
		getCurrentHandler().openAllNodes();
	}

	public void zoomIn(ArrayList<Filter> filters) {
		getCurrentHandler().zoomIn(filters);
	}

	public void zoom(Double coefficient, ArrayList<Filter> filters) {
		getCurrentHandler().zoom(coefficient, filters);
	}

	public double getZoomingCoefficient() {
		return getCurrentHandler().getZoomingCoefficient();
	}

	public void zoomOut(ArrayList<Filter> filters) {
		getCurrentHandler().zoomOut(filters);
	}

	public void colorizeSelectedNodes(Color color) {
		getCurrentHandler().colorizeSelectedNodes(color);
	}

	public void normalizeAllBorders(ArrayList<Filter> filters) {
		getCurrentHandler().normalizeAllBorders(filters);
	}

	public void normalizeBorderColoredOf(Color color, ArrayList<Filter> filters) {
		getCurrentHandler().normalizeBorderColoredOf(color, filters);
	}

	public void defaultColorForSelectedNodes() {
		getCurrentHandler().defaultColorForSelectedNodes();
	}

	public void invertSelection(ArrayList<Filter> filters) {
		getCurrentHandler().invertSelection(filters);
	}

	public void selectNodesTagged(ArrayList<Color> colors, ArrayList<Filter> filters) {
		PolymetricViewHandler handler = getCurrentHandler();
		for(int i = 0; i < colors.size(); i++){
			handler.selectNodesTagged(colors.get(i), filters);
		}
	}

	public void deSelectNodesTagged(ArrayList<Color> colors, ArrayList<Filter> filters) {
		PolymetricViewHandler handler = getCurrentHandler();
		for(int i = 0; i < colors.size(); i++){
			handler.deSelectNodesTagged(colors.get(i), filters);
		}
	}

	public void selectLeaves(ArrayList<Filter> filters, boolean select) {
		getCurrentHandler().selectLeaves(filters, select);
	}

	public void selectRoots(ArrayList<Filter> filters, boolean select) {
		getCurrentHandler().selectRoots(filters, select);
	}

	public void selectBranches(ArrayList<Filter> filters, boolean select) {
		getCurrentHandler().selectBranches(filters, select);
	}

	public void hideSelectedNode(ArrayList<Filter> filters) {
		getCurrentHandler().hideSelectedNode(filters);
	}

	public void selectChildrenOfSelectedNodes(ArrayList<Filter> filters, boolean select) {
		getCurrentHandler().selectChildrenOfSelectedNodes(filters, select);
	}

	public void selectParentsOfSelectedNodes(boolean select) {
		getCurrentHandler().selectParentsOfSelectedNodes(select);
	}

	public void showHiddenNodes(ArrayList<Filter> filters) {
		getCurrentHandler().showHiddenNodes(filters);
	}

	public void selectMatchingNodes(String value) {
		getCurrentHandler().selectMatchingNodes(value);
	}

	public void selectSiblingsOfSelectedNodes(ArrayList<Filter> filters, boolean select) {
		getCurrentHandler().selectSiblingsOfSelectedNodes(filters, select);
	}

	public void selectClassKind(String kind, ArrayList<Filter> filters) {
		getCurrentHandler().selectClassKind(kind, filters);
	}

	public void deSelectClassKind(String kind, ArrayList<Filter> currentFilters) {
		getCurrentHandler().deSelectClassKind(kind, currentFilters);
	}

	public boolean isSelected(Node node) {
		return getCurrentHandler().isSelected(node);
	}

	public void showJustDependencyInRange(int[] range) {
		getCurrentHandler().showJustDependencyInRange(range);
	}

	public void selectHighlightedNodes(ArrayList<Filter> currentFilters) {
		getCurrentHandler().selectHighlightedNodes(currentFilters);
	}

	public int getMaxDependencyWeigth() {
		return getCurrentHandler().getMaxDependencyWeigth();
	}

	public Rectangle getViewBounds() {
		return getCurrentHandler().getViewBounds(parent);
	}

	public void setVerticalScrolling(int y) {
		getCurrentHandler().setVerticalScrolling(y, panel);
	}

	public void setHorizontalScrolling(int x) {
		getCurrentHandler().setHorizontalScrolling(x, panel);
	}

	public void viewTopicContents(Topic topicInNode) {
		topicContentsHandler.setTopic(topicInNode);
		topicContentsHandler.init();
		//topicContentsHandler.fitSize(getCurrentFilters()); //TODO: add this back
		showTopicContents();
		
		LDATopics.twoDimensionalView.updateTextTrick();
	}

	public int getTopicShown() {
		return topicContentsHandler.viewTopic +1;
	}

	public void resetTopicContentsView() {
		if (topicContentsHandler != null)
			topicContentsHandler.setTopic(-1);		
	}
}