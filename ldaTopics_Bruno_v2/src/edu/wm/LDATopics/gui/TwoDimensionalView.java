package edu.wm.LDATopics.gui;


// Idea:
//   Two views
//     1) Topics
//     2) Classes in a Topic

import java.util.ArrayList;

import javax.xml.parsers.FactoryConfigurationError;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.part.*;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.draw2d.ScrollPane;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;

import edu.wm.LDATopics.LDATopics;
import edu.wm.LDATopics.LDA.Topic;
import edu.wm.LDATopics.gui.actions.AnalyzeProjectActionDelegate;
import edu.wm.LDATopics.gui.actions.OpenHelpAction;

import org.malnatij.svplugin.filters.Filter;
import org.malnatij.svplugin.graph.nodes.Node;
import org.malnatij.svplugin.model.ClassRepresentation;
import org.malnatij.svplugin.model.ProjectRepresentation;
import org.malnatij.svplugin.model.core.Model;
import org.malnatij.svplugin.model.core.ModelNotPreviouslyScheduled;
import org.malnatij.svplugin.model.core.ModelingChangeListener;
import org.malnatij.svplugin.model.viewcommunication.IFillableView;
import org.malnatij.svplugin.util.Log;
import org.malnatij.svplugin.views.ListenerAdder;
import org.malnatij.svplugin.views.ViewFacade;
import org.malnatij.svplugin.views.XRayWorkbenchView;
import org.malnatij.svplugin.views.actions.ToolbarActions;
import org.malnatij.svplugin.views.actions.menuitems.providers.SelectHierarchyMenuItemProvider;
import org.malnatij.svplugin.views.handlers.PolymetricViewHandler;
// TODO: dialog for setting lda settings, particularly number of topics, eh?

/**
 * This class creates and manages the X-Ray's workbench view in which the plug-in
 * will create all the polymetric views.
 * @author Jacopo Malnati
 *
 */
public class TwoDimensionalView extends XRayWorkbenchView 
implements IFillableView{
	// facade to the views
	private LDAViewFacade viewFacade;
	
	// graphics
	private LightweightSystem lws = null;
	private ScrollPane panel = null;
	private Composite parent = null;
	private Display display = null;
	
	//Model
	private Model theModel;

	private ToolbarActions actionsContainer;

	/**
	 * The constructor.
	 */
	public TwoDimensionalView() {
		Log.printSVPluginView("XRayWorkbenchView, SVPluginView created");
		LDATopics.twoDimensionalView = this;
	}
	
	public Composite getParent(){
		return parent;
	}
	
	public ProjectRepresentation getModeledProject()
	throws ModelNotPreviouslyScheduled{
		return theModel.getModeledProject();
	}
	
	public Display getDisplay(){
		return display;
	}

	/**
	 * This is a call-back that will allow us
	 * to create the viewer and initialize it.
	 */
	public void createPartControl(Composite parent) {
		try{
			this.parent = parent;
			Canvas canvas = new Canvas (parent, SWT.NONE);
			lws = new LightweightSystem(canvas);
			panel = new ScrollPane();
			panel.setBackgroundColor(new Color(null, 255, 255, 255));

			getViewSite().getActionBars().getToolBarManager().add(new OpenHelpAction());
			
			display = Display.getCurrent();
			
			viewFacade = new LDAViewFacade(parent, panel, lws);
			viewFacade.setTopicDependencyView();
			Log.printSVPluginView("Callback to the view with SVPluginView.createPartControl");
			
			// if there is a project to model
			if(AnalyzeProjectActionDelegate.getProjectToModel() != null){
				// create the model for the project
				theModel = new Model(AnalyzeProjectActionDelegate.getProjectToModel(),
						"creating the model.. please be patient..");
				// schedule the model to run and gets filled with data
				theModel.schedule();
				// once the model has been filled, open the view
				theModel.addJobChangeListener(new ModelingChangeListener(this));
				
			}
		} catch(Exception e){
			e.printStackTrace();
			Log.printError(e.getMessage() + " in createPartControl");
		}		
	}
	
	public void fillView(boolean createActions){
		try{
			if(createActions){
				Log.printSVPluginView("Going to init handlers");
				createViewHandlers();
				createAndAddActions();
				viewFacade.initPackageDependencyHandler();
				Log.printSVPluginView("Going to add listeners");
				new ListenerAdder(this, getCurrentFilters());
			} else {
				initViewHandlers();
				viewFacade.refreshView(getCurrentFilters());
				//getCurrentHandler().refreshView(getCurrentFilters());
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	public ArrayList<Filter> getCurrentFilters(){
		return viewFacade.getCurrentFilters();
		//return getCurrentHandler().getCurrentFilters();
	}
	
	public void initViewHandlers(){
		viewFacade.initAllViewHandlers();
	}
	
	public void createViewHandlers(){
		viewFacade.createViewHandlers(this);
	}
	
	public void createAndAddActions() {
		actionsContainer = new ToolbarActions(this);
		actionsContainer.createActions();
		actionsContainer.createToolbar();
	}
	
	public Action getInfoTrickAction() {
		return actionsContainer.getInfoTrickAction();
	}

	public Figure getCurrentFiguresWrapper(){
		return viewFacade.getFiguresWrapper();
	}
	
	public void addSelectedNode(Node selectedNode,
			boolean deselectDuplicate, boolean singleSelection){
		viewFacade.addSelectedNode(selectedNode, deselectDuplicate, singleSelection);
	}

	public void openAllNodes(){
		viewFacade.openAllNodes();
	}
	
	public void centerViewOnClass(ClassRepresentation currentClass){
		viewFacade.centerViewOnClass(currentClass);
	}
	
	public void centerViewOnNode(Node currentNode){
		viewFacade.centerViewOnNode(currentNode);
	}
	
	/**
	 * If the user selects an internal node, it can ask for interface handling
	 * If the node is an interface, it will be connected to all the classes
	 * in the project that implements that interface, while if it is
	 * a class, it will be linked to all the interfaces that it implements
	 */
	public void handleInterfaceLinkRequest(ArrayList<Filter> filters){
		viewFacade.handleInterfaceLinkRequest(filters);
	}
	
	public void handleOutgoingDependencyLinkRequest(ArrayList<Filter> filters){
		viewFacade.handleOutgoingDependencyLinkRequest(filters);
	}
	
	public void handleIncomingDependencyLinkRequest(ArrayList<Filter> filters){
		viewFacade.handleIncomingDependencyLinkRequest(filters);
	}
	
	public void handleDependencyLinkRequest(ClassRepresentation classToLink,
			ArrayList<Filter> filters){
		viewFacade.handleDependencyLinkRequest(classToLink, filters);
	}	

	public void setFocus() {}
	
	public void refresh() {
		theModel.schedule();
	}
	
	public void setHorizontalScrolling(int x){
		viewFacade.setHorizontalScrolling(x);
	}
	
	public void setVerticalScrolling(int y){
		viewFacade.setVerticalScrolling(y);
	}
	
	public void setOriginScrolling(){
		viewFacade.setOriginScrolling();
	}

	public void showClassDependency(){
		viewFacade.showClassDependency();
	}
	
	public void showPackageDependency(){
		viewFacade.showTopicDependency();
	}
	
	public void showTopicDependency(){
		viewFacade.showTopicDependency();
		updateTextTrick();
	}
	
	public void showSystemComplexity() {
		viewFacade.showTopicContents();
	}
		
	public String getCurrentVisualizationDescription(){
		return viewFacade.getCurrentVisualizationDescription();
	}

	public void addHighlightNode(Node currentNode) {
		viewFacade.addHighlightNode(currentNode);
	}

	public LightweightSystem getLws() {
		return lws;
	}

	public ScrollPane getPanel() {
		return panel;
	}

	public void zoomIn(ArrayList<Filter> filters) {
		viewFacade.zoomIn(filters);
	}
	
	public void zoom(Double coefficient, ArrayList<Filter> filters){
		viewFacade.zoom(coefficient, filters);
	}
	
	public double getCurrentZoomingCoefficient() {
		return viewFacade.getZoomingCoefficient();
	}

	public void zoomOut(ArrayList<Filter> filters) {
		viewFacade.zoomOut(filters);
	}

	public Rectangle getViewBounds(){
		return viewFacade.getViewBounds();
	}
	
	public void originalSize(ArrayList<Filter> filters) {
		viewFacade.originalSize(filters);
	}

	public void fitSize(ArrayList<Filter> filters) {
		viewFacade.fitSize(filters);
	}

	public void colorizeSelectedNodes(Color color) {
		viewFacade.colorizeSelectedNodes(color);
	}

	public void normalizeAllBorders(ArrayList<Filter> filters) {
		viewFacade.normalizeAllBorders(filters);
	}

	public void normalizeBorderColoredOf(Color color, ArrayList<Filter> filters) {
		viewFacade.normalizeBorderColoredOf(color, filters);
	}

	public void defaultColorForSelectedNodes() {
		viewFacade.defaultColorForSelectedNodes();
	}

	public void invertSelection(ArrayList<Filter> filters) {
		viewFacade.invertSelection(filters);
	}
	
	public void selectNodesTagged(ArrayList<Color> colors, ArrayList<Filter> filters){
		viewFacade.selectNodesTagged(colors, filters);
	}

	public void deSelectNodesTagged(ArrayList<Color> colors, ArrayList<Filter> filters) {
		viewFacade.deSelectNodesTagged(colors, filters);
	}

	public void selectLeaves(ArrayList<Filter> filters, boolean select) {
		viewFacade.selectLeaves(filters, select);
	}

	public void selectRoots(ArrayList<Filter> filters, boolean select) {
		viewFacade.selectRoots(filters, select);
	}
	
	public void selectBranches(ArrayList<Filter> filters, boolean select) {
		viewFacade.selectBranches(filters, select);
	}

	public void hideSelectedNode(ArrayList<Filter> filters) {
		viewFacade.hideSelectedNode(filters);
	}

	private void selectChildrenOfSelectedNodes(
			ArrayList<Filter> filters, boolean select) {
		viewFacade.selectChildrenOfSelectedNodes(filters, select);
	}

	private void selectParentsOfSelectedNodes(boolean select) {
		viewFacade.selectParentsOfSelectedNodes(select);
	}

	public void showHiddenNodes(ArrayList<Filter> filters) {
		viewFacade.showHiddenNodes(filters);
	}

	public void selectMatchingNodes(String value) {
		viewFacade.selectMatchingNodes(value);
	}

	private void selectSiblingsOfSelectedNodes(
			ArrayList<Filter> filters, boolean select) {
		viewFacade.selectSiblingsOfSelectedNodes(filters, select);
	}

	public void selectClassKind(String kind, ArrayList<Filter> filters) {
		viewFacade.selectClassKind(kind, filters);
	}
	
	public void deSelectClassKind(String kind, ArrayList<Filter> currentFilters) {
		viewFacade.deSelectClassKind(kind, currentFilters);
	}

	public void selectNodeOfHierarchy(String kind, ArrayList<Filter> filters,
			boolean select) {
		if(kind.equals(SelectHierarchyMenuItemProvider.PARENTS)){
			selectParentsOfSelectedNodes(select);
		} else if(kind.equals(SelectHierarchyMenuItemProvider.CHILDREN)){
			selectChildrenOfSelectedNodes(filters, select);
		} else if(kind.equals(SelectHierarchyMenuItemProvider.SIBLING)){
			selectSiblingsOfSelectedNodes(filters, select);
		}
	}

	public boolean isSelected(Node node) {
		return viewFacade.isSelected(node);
	}

	public void showJustDependencyInRange(int[] range) {
		viewFacade.showJustDependencyInRange(range);
	}

	public void selectHighlightedNodes(ArrayList<Filter> currentFilters) {
		viewFacade.selectHighlightedNodes(currentFilters);
	}

	public int getMaxDependencyWeigth() {
		return viewFacade.getMaxDependencyWeigth();
	}

	public PolymetricViewHandler getCurrentHandler() {
		return viewFacade.getCurrentHandler();
	}

	public void viewTopicContents(Topic topicInNode) {
		viewFacade.viewTopicContents(topicInNode);
		
	}
	
	public int getTopicShown() {
		return viewFacade.getTopicShown();
	}

	public void updateTextTrick() {
		if(LDATopics.map.getClassMap().project != null) {
			String topic = "";
			
			if (viewFacade.CURRENT_VISUALIZATION.equals(viewFacade.TOPIC_CONTENTS))
				topic = " -- "+LDATopics.map.getClassMap().topics[getTopicShown()-1].getBestWords();
		
			LDATopics.twoDimensionalView.getInfoTrickAction().setText(LDATopics.map.getClassMap().project+topic);
			getViewSite().getActionBars().getToolBarManager().update(true);
		} else {
			LDATopics.twoDimensionalView.getInfoTrickAction().setText("[No project]");
			getViewSite().getActionBars().getToolBarManager().update(true);
		}		
	}

	public void updateToolbar() {
		if (actionsContainer != null)
			actionsContainer.updateToolbar();
	}

	public void resetTopicContentsView() {
		viewFacade.resetTopicContentsView();
	}
	
}