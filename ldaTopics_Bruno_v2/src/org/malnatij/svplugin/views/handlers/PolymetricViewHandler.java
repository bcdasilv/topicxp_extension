package org.malnatij.svplugin.views.handlers;

import java.util.ArrayList;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.draw2d.ScrollPane;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.malnatij.svplugin.XRay;
import org.malnatij.svplugin.filters.Filter;
import org.malnatij.svplugin.filters.HiddenFilter;
import org.malnatij.svplugin.filters.PackageContentFilter;
import org.malnatij.svplugin.graph.nodes.IClassNode;
import org.malnatij.svplugin.graph.nodes.Node;
import org.malnatij.svplugin.model.ClassRepresentation;
import org.malnatij.svplugin.model.ProjectRepresentation;
import org.malnatij.svplugin.model.core.ModelNotPreviouslyScheduled;
import org.malnatij.svplugin.util.Log;
import org.malnatij.svplugin.views.ViewFacade;
import org.malnatij.svplugin.views.XRayWorkbenchView;

public abstract class PolymetricViewHandler extends XRay {
	public static final double zoomingConstant = 1.3;
	protected ArrayList<Node> highlightedFigures = new ArrayList<Node>();
	protected ArrayList<Node> selectedFigures = new ArrayList<Node>();
	protected ArrayList<Node> hiddenFigures = new ArrayList<Node>();
	protected Figure figuresWrapper = null;
	protected int figuresWrapperDefautWidth = -1;
	protected XRayWorkbenchView view = null;
	protected double zoomingCoefficient = 1;
	protected ArrayList<Filter> filters = new ArrayList<Filter>();
	
	public abstract void normalizeAllBorders(ArrayList<Filter> filters);

	public abstract void normalizeBorderColoredOf(Color color,
			ArrayList<Filter> filters);

	public abstract void invertSelection(ArrayList<Filter> filters);

	public abstract void selectNodesTagged(Color color,
			ArrayList<Filter> filters);

	public abstract void setViewContentBounds();

	protected abstract void resetAllNodesAndLinks();

	public abstract Node getFigureForClass(ClassRepresentation currentClass);

	public abstract void init();

	protected abstract void applyZooming(ArrayList<Filter> filters);

	public abstract void selectChildrenOfSelectedNodes(
			ArrayList<Filter> filters, boolean select);

	public abstract void selectParentsOfSelectedNodes(boolean select);

	public abstract void selectLeaves(ArrayList<Filter> filters, boolean select);

	public abstract void selectRoots(ArrayList<Filter> filters, boolean select);

	public abstract void selectBranches(ArrayList<Filter> filters,
			boolean select);

	public abstract void selectMatchingNodes(String pattern);

	public abstract void selectSiblingsOfSelectedNodes(
			ArrayList<Filter> filters, boolean select);
	
	public abstract void refreshView(ArrayList<Filter> filters);
	
	public abstract void selectClassKind(String kind, ArrayList<Filter> filters);
	
	public abstract void deSelectClassKind(String kind, ArrayList<Filter> currentFilters);
	
	public abstract void deSelectNodesTagged(Color color, ArrayList<Filter> filters);
	
	public abstract void showJustDependencyInRange(int[] range);
	
	public PolymetricViewHandler(){
		setDefaultHiddenFilter();
	}
	
	public ArrayList<Filter> getCurrentFilters(){
		return filters;
	}
	
	public void setDefaultHiddenFilter(){
		filters = new ArrayList<Filter>();
		filters.add(new HiddenFilter());
	}
	
	public void addFilter(Filter newFilter){
		filters.add(newFilter);
	}
	
	public void removePackageFilters(){
		for(int i = 0; i < filters.size(); i++){
			Filter currentFilter = filters.get(i);
			if(currentFilter instanceof PackageContentFilter){
				filters.remove(i);
				removePackageFilters();
			}
		}
	}

	public Figure getFiguresWrapper() {
		return figuresWrapper;
	}
	
	protected Figure createContainerFigure(){
		Figure contents = new Figure();
		contents.addMouseListener(new MouseListener(){
			@SuppressWarnings("unused")
			public void mouseClicked(MouseEvent e) {}
			public void mouseDoubleClicked(MouseEvent arg0) {}
			public void mousePressed(MouseEvent arg0) {
				resetAllNodesAndLinks();
			}
			public void mouseReleased(MouseEvent arg0) {}
		});
		return contents;
	}
	
	public void selectNode(Node selectedNode){
		selectedNode.setSelectedBorderColor();
		selectedFigures.add(selectedNode);
	}
	
	public void deselectNode(Node selectedNode){
		selectedFigures.remove(selectedNode);
		selectedNode.setNormalBorderColor();
	}
	
	public void addSelectedNode(Node selectedNode,
			boolean deselectDuplicate, boolean singleSelection){
		//System.out.println("add selected node: " + selectedNode.getName());
		if(!selectedFigures.contains(selectedNode)){
			// reset all for a single selection
			if(singleSelection){
				resetAllNodesAndLinks();
			}
			selectNode(selectedNode);
		} else {
			if(deselectDuplicate){
				deselectNode(selectedNode);
			}
		}
	}
	
	public void openAllNodes(){
		try{
			ArrayList<Node> selectedFiguresSnapshot = new ArrayList<Node>();
			int size = selectedFigures.size();
			// copy the selected nodes in a temporary copy as snapshot
			for(int i = 0; i < size; i++){
				selectedFiguresSnapshot.add(selectedFigures.get(i));
			}
			
			// active page
			IWorkbenchPage activePage =
				PlatformUI.getWorkbench().
				getActiveWorkbenchWindow().getActivePage();
			
			Node currentNode = null;
			for(int i = 0; i < size; i++){
				currentNode = selectedFiguresSnapshot.get(i);
				// resource to show

				if(currentNode instanceof IClassNode){
					IResource resource = 
						ResourcesPlugin.getWorkspace().getRoot().findMember(
								((IClassNode)currentNode).getClassRepresentation().getRelpath());
					
					if(resource != null &&
							resource.exists() &&
							resource.getType() == IResource.FILE) {
						try {
							IDE.openEditor(activePage, (IFile) resource, true);
					
							if(i < size - 1){
								resetAllNodesAndLinks();
							}
							
						} catch (PartInitException e) {
							Log.printError("Exception while opening: " + resource);
						} 
					}
				}
				
			}
			
			if(currentNode != null){
				view.centerViewOnNode(currentNode);;
			}
			
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
	public void deSelectNodes(){
		int size = selectedFigures.size();
		for(int i = 0; i < size; i++){
			Node currentNode = selectedFigures.get(i);
			currentNode.setNormalBorderColor();
		}
		selectedFigures = new ArrayList<Node>();
	}
	
	public void deHilightNodes(){
		int size = highlightedFigures.size();
		for(int i = 0; i < size; i++){
			Node currentNode = highlightedFigures.get(i);
			currentNode.setNormalBorderColor();
		}
		highlightedFigures = new ArrayList<Node>();
	}
	
	public void addHighlightNode(Node currentNode){
		if(!highlightedFigures.contains(currentNode)){
			highlightedFigures.add(currentNode);
		}
	}

	public void zoomIn(ArrayList<Filter> filters) {
		zoomingCoefficient *= zoomingConstant;
		applyZooming(filters);
	}
	
	public void zoom(Double coefficient, ArrayList<Filter> filters){
		zoomingCoefficient *= coefficient;
		applyZooming(filters);
	}

	public double getZoomingCoefficient() {
		return zoomingCoefficient;
	}

	public void zoomOut(ArrayList<Filter> filters) {
		zoomingCoefficient /= zoomingConstant;
		applyZooming(filters);
	}

	public abstract int getMaxDependencyWeigth();
	
	public abstract void originalSize(ArrayList<Filter> filters);

	public void fitSize(ArrayList<Filter> filters) {
		originalSize(filters); // pretty bad hack FIXME
		double viewWidth = (double)view.getViewBounds().width;
		double viewHeight = (double)view.getViewBounds().height;
		
		if(view.getCurrentVisualizationDescription().
				equals(ViewFacade.SYSTEM_COMPLEXITY)){
			// fit the width
			zoomingCoefficient *= viewWidth / (double)figuresWrapperDefautWidth;
			
		} else if (view.getCurrentVisualizationDescription().
				equals(ViewFacade.CLASS_DEPENDENCY)){
			// fit the height
			zoomingCoefficient *= 0.92 * viewHeight / (double)figuresWrapperDefautWidth;
		} else {
			zoomingCoefficient *= viewHeight / (double)figuresWrapperDefautWidth;
		}
		
		applyZooming(filters);
	}
	
	public void colorizeSelectedNodes(Color theColor) {
		for(int i = 0; i < selectedFigures.size(); i++){
			selectedFigures.get(i).setColoredBorder(theColor);
		}
		
	}
	
	public void defaultColorForSelectedNodes() {
		for(int i = 0; i < selectedFigures.size(); i++){
			selectedFigures.get(i).removeBorderTag();
		}
		
	}

	public void hideSelectedNode(ArrayList<Filter> filters) {
		selectChildrenOfSelectedNodes(filters, true);
		
		for(int i = 0; i < selectedFigures.size(); i++){
			Node currentNode = selectedFigures.get(i);
			hiddenFigures.add(currentNode);
			currentNode.setHiddenNode();
		}
		
		deSelectNodes();
		refreshView(filters);
	}

	public void showHiddenNodes(ArrayList<Filter> filters) {
		for(int i = 0; i < hiddenFigures.size(); i++){
			Node currentHiddenNode = hiddenFigures.get(i);
			currentHiddenNode.setVisibleNode();
		}
		
		hiddenFigures = new ArrayList<Node>();
		
		refreshView(filters);
	}

	public boolean isSelected(Node node) {
		return selectedFigures.contains(node);
	}
	
	public void printFilters(){
		for(int i = 0; i < filters.size(); i++){
			System.out.println(filters.get(i).toString());
		}	
	}

	public void selectHighlightedNodes(ArrayList<Filter> currentFilters) {
		for(int i = 0; i < highlightedFigures.size(); i++){
			Node currentHighlightedNode = highlightedFigures.get(0);
			addSelectedNode(currentHighlightedNode, false, false);
			highlightedFigures.remove(0);
			selectHighlightedNodes(filters);
		}
	}
	
	protected ProjectRepresentation getProject(){
		try {
			return view.getModeledProject();
		} catch (ModelNotPreviouslyScheduled e) {
			// should NEVER happen
			Log.printError("Critical error: PolymetricViewHandler.getProject()");
			e.printStackTrace();
			return null;
		}
	}

	public void centerViewOnNodeInPanel(Node currentNode, ScrollPane panel, Composite parent) {
		int x = currentNode.x;
		int y = currentNode.y;
		setHorizontalScrolling(x +
				currentNode.getBounds().width/2 -
				getViewBounds(parent).width/2, panel);
		setVerticalScrolling(y - 100, panel);
	}
	
	public Rectangle getViewBounds(Composite parent) {
		return new Rectangle(parent.getBounds());
	}
	
	public void setHorizontalScrolling(int x, ScrollPane panel) {
		panel.getHorizontalScrollBar().setValue(x);
	}

	public void setVerticalScrolling(int y, ScrollPane panel) {
		panel.getVerticalScrollBar().setValue(y);
	}
	
	public void setOriginScrolling(ScrollPane panel) {
		setHorizontalScrolling(0, panel);
		setVerticalScrolling(0, panel);
		panel.repaint();
	}
}
