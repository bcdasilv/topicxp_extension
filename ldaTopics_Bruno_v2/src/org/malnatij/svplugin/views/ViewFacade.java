package org.malnatij.svplugin.views;

import java.util.ArrayList;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.draw2d.ScrollPane;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.malnatij.svplugin.XRay;
import org.malnatij.svplugin.filters.Filter;
import org.malnatij.svplugin.graph.nodes.Node;
import org.malnatij.svplugin.model.ClassRepresentation;
import org.malnatij.svplugin.util.Log;
import org.malnatij.svplugin.views.handlers.ClassDependencyHandler;
import org.malnatij.svplugin.views.handlers.PackageDependencyHandler;
import org.malnatij.svplugin.views.handlers.PolymetricViewHandler;
import org.malnatij.svplugin.views.handlers.SystemComplexityHandler;

import edu.wm.LDATopics.gui.TwoDimensionalView;

public class ViewFacade extends XRay{
	// viewParent
	private Composite parent = null;
	// viewPanel
	private ScrollPane panel = null;
	// lws
	private LightweightSystem lws = null;
	
	// constants
	public static final String SYSTEM_COMPLEXITY = "system complexity";
	public static final String CLASS_DEPENDENCY = "class dependency";
	public static final String PACKAGE_DEPENDENCY = "package dependency";
	public static String CURRENT_VISUALIZATION = SYSTEM_COMPLEXITY;
	
	//Handlers
	private SystemComplexityHandler systemComplexityHandler = null;
	private ClassDependencyHandler classDependencyHandler = null;
	private PackageDependencyHandler packageDependencyHandler = null;
	
	public ViewFacade(Composite parent, ScrollPane panel, LightweightSystem lws){
		this.parent = parent;
		this.panel = panel;
		this.lws = lws;
	}
	
	private void setCurrentViewTo(String viewKind){
		CURRENT_VISUALIZATION = viewKind;
	}
	
	public void setSystemComplexityView(){
		setCurrentViewTo(SYSTEM_COMPLEXITY);
	}
	
	public void setClassDependencyView(){
		setCurrentViewTo(CLASS_DEPENDENCY);
	}
	
	public void setPackageDependencyView(){
		setCurrentViewTo(PACKAGE_DEPENDENCY);
	}
	
	public void initSystemComplexityHandler(){
		systemComplexityHandler.init();
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
		systemComplexityHandler = new SystemComplexityHandler(twoDimensionalView);
		classDependencyHandler = new ClassDependencyHandler(twoDimensionalView);
		packageDependencyHandler = new PackageDependencyHandler(twoDimensionalView);
	}

	public void swapViewWith(Figure toSwap, String description,
			ScrollPane panel, LightweightSystem lws) {
		if(toSwap != null){
			panel.setContents(toSwap);
			if(description.equals(PACKAGE_DEPENDENCY)){
				CURRENT_VISUALIZATION = PACKAGE_DEPENDENCY;
				packageDependencyHandler.setViewContentBounds();
			} else if(description.equals(CLASS_DEPENDENCY)){
				CURRENT_VISUALIZATION = CLASS_DEPENDENCY;
				classDependencyHandler.setViewContentBounds();
			} else {
				CURRENT_VISUALIZATION = SYSTEM_COMPLEXITY;
				systemComplexityHandler.setViewContentBounds();
			}
			lws.setContents(panel);
			panel.repaint();
		} else {
			if(description.equals(CLASS_DEPENDENCY)){
				CURRENT_VISUALIZATION = CLASS_DEPENDENCY;
				classDependencyHandler.init();
				swapViewWith(classDependencyHandler.getFiguresWrapper(),
						description, panel, lws);
			} else if(description.equals(PACKAGE_DEPENDENCY)){
				CURRENT_VISUALIZATION = PACKAGE_DEPENDENCY;
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
		} else if(CURRENT_VISUALIZATION.equals(PACKAGE_DEPENDENCY)){
			return packageDependencyHandler;
		} else if(CURRENT_VISUALIZATION.equals(SYSTEM_COMPLEXITY)){
			return systemComplexityHandler;
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
		} else if(CURRENT_VISUALIZATION.equals(PACKAGE_DEPENDENCY)){
			currentNode = packageDependencyHandler.getFigureForClass(currentClass);
			deHilightNodes();
			addHighlightNode(currentNode);
		} else if(CURRENT_VISUALIZATION.equals(SYSTEM_COMPLEXITY)){
			currentNode = systemComplexityHandler.getFigureForClass(currentClass);
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
		if(CURRENT_VISUALIZATION.equals(SYSTEM_COMPLEXITY)){
			systemComplexityHandler.handleInterfaceLinkRequest(filters);
		} // TODO else
	}

	public void handleOutgoingDependencyLinkRequest(ArrayList<Filter> filters) {
		if(CURRENT_VISUALIZATION.equals(SYSTEM_COMPLEXITY)){
			systemComplexityHandler.handleOutgoingDependencyLinkRequest(filters);
		}
	}

	public void handleIncomingDependencyLinkRequest(ArrayList<Filter> filters) {
		if(CURRENT_VISUALIZATION.equals(SYSTEM_COMPLEXITY)){
			systemComplexityHandler.handleIncomingDependencyLinkRequest(filters);
		}
	}

	public void handleDependencyLinkRequest(ClassRepresentation classToLink,
			ArrayList<Filter> filters) {
		if(CURRENT_VISUALIZATION.equals(SYSTEM_COMPLEXITY)){
			systemComplexityHandler.handleOutgoingDependencyLinkRequest(classToLink, filters);
		}
	}

	public void showClassDependency() {
		swapViewWith(classDependencyHandler.getFiguresWrapper(), CLASS_DEPENDENCY, panel, lws);
	}

	public void showPackageDependency() {
		swapViewWith(packageDependencyHandler.getFiguresWrapper(), PACKAGE_DEPENDENCY, panel, lws);
	}

	public void showSystemComplexity() {
		swapViewWith(systemComplexityHandler.getFiguresWrapper(), SYSTEM_COMPLEXITY, panel, lws);
	}

	public String getCurrentVisualizationDescription() {
		return CURRENT_VISUALIZATION;
	}

	private void displayOriginalSize(ArrayList<Filter> filters){
		getCurrentHandler().originalSize(filters);
		setOriginScrolling();
	}
	
	public void originalSize(ArrayList<Filter> filters) {
		if(CURRENT_VISUALIZATION.equals(SYSTEM_COMPLEXITY)){
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
		if(CURRENT_VISUALIZATION.equals(SYSTEM_COMPLEXITY)){
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
}