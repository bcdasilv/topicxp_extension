package org.malnatij.svplugin.views.handlers;

import java.util.ArrayList;
import java.util.regex.Pattern;

import org.eclipse.draw2d.Figure;
import org.eclipse.swt.graphics.Color;
import org.malnatij.svplugin.filters.Filter;
import org.malnatij.svplugin.graph.nodes.DVPackageNode;
import org.malnatij.svplugin.graph.nodes.Node;
import org.malnatij.svplugin.layouts.CircleLayout;
import org.malnatij.svplugin.model.ClassRepresentation;
import org.malnatij.svplugin.model.PackageRepresentation;
import org.malnatij.svplugin.util.Log;
import org.malnatij.svplugin.views.ViewFacade;
import org.malnatij.svplugin.views.XRayWorkbenchView;

public class PackageDependencyHandler extends DependencyHandler{
	private ArrayList<DVPackageNode> nodesDVPackage = null;
	
	public PackageDependencyHandler(XRayWorkbenchView view) {
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
	
	private void createDVPackageNodes(){
		nodesDVPackage = new ArrayList<DVPackageNode>();
		ArrayList<PackageRepresentation> allPackages =
			getProject().getPackages();
		for(int i = 0; i < allPackages.size(); i++){
			PackageRepresentation currentPackage = allPackages.get(i);
			if(!currentPackage.getName().equals("unknown")){
				DVPackageNode currentNode =
					new DVPackageNode(currentPackage, view);
				nodesDVPackage.add(currentNode);
			}
		}
	}
	
	private void addDVPackageNodesToContent(Figure contents){
		for(int i = 0; i < nodesDVPackage.size(); i++){
			DVPackageNode currentFigure = nodesDVPackage.get(i);
			contents.add(currentFigure);
		}
	}
	
	private CircleLayout createCirclePackageLayout(){
		CircleLayout circleLayout = new CircleLayout();
		try{
			circleLayout.layoutPackages(nodesDVPackage,
					figuresWrapper, view,
					true, getCurrentFilters());
		}catch(Exception e){
			e.printStackTrace();
		}
		return circleLayout;
	}
	
	public void setViewContentBounds(){
		figuresWrapper.setBounds(
				circleLayout.getBounds(ViewFacade.PACKAGE_DEPENDENCY,
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
				DVPackageNode currentNode = nodesDVPackage.get(i);
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
		circleLayout.layoutPackages(nodesDVPackage, figuresWrapper, view,
				false, filters);
		setViewContentBounds();
		view.showPackageDependency();
	}

	@Override
	public void originalSize(ArrayList<Filter> filters) {
		zoomingCoefficient = 1;
		int size = nodesDVPackage.size();
		for(int i = 0; i < size; i++){
			DVPackageNode currentNode = nodesDVPackage.get(i);
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

	private boolean filterOutNode(DVPackageNode packageNode,
			ArrayList<Filter> filters) {
		for(int i = 0; i < filters.size(); i++){
			if(filters.get(i).matches(packageNode.getPackageInNode())){
				return true;
			}
		}
		return false;
	}

	@Override
	public void normalizeBorderColoredOf(Color color, ArrayList<Filter> filters) {
		for(int i = 0; i < nodesDVPackage.size(); i++){
			DVPackageNode currentNode = nodesDVPackage.get(i);
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
			DVPackageNode currentNode = nodesDVPackage.get(i);
			if(currentNode.isTagged(color) && !filterOutNode(currentNode, filters)){
				addSelectedNode(currentNode, false, false);
			}
		}
	}

	@Override
	public void deSelectNodesTagged(Color color, ArrayList<Filter> filters) {
		for(int i = 0; i < selectedFigures.size(); i++){
			DVPackageNode currentNode = (DVPackageNode)selectedFigures.get(i);
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
				DVPackageNode currentNode = nodesDVPackage.get(i);
				if(!filterOutNode(currentNode, filters) &&
						Pattern.matches(pattern,
								currentNode.getPackageInNode().getName().toLowerCase())){
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
