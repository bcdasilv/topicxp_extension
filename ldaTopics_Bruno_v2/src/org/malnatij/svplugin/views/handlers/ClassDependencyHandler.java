package org.malnatij.svplugin.views.handlers;

import java.util.ArrayList;
import java.util.regex.Pattern;

import org.eclipse.draw2d.Figure;
import org.eclipse.swt.graphics.Color;
import org.malnatij.svplugin.filters.Filter;
import org.malnatij.svplugin.graph.nodes.DVClassNode;
import org.malnatij.svplugin.graph.nodes.Node;
import org.malnatij.svplugin.layouts.CircleLayout;
import org.malnatij.svplugin.model.ClassRepresentation;
import org.malnatij.svplugin.model.EntityRepresentation;
import org.malnatij.svplugin.util.Log;
import org.malnatij.svplugin.views.ViewFacade;
import org.malnatij.svplugin.views.XRayWorkbenchView;

public class ClassDependencyHandler extends DependencyHandler{
	private ArrayList<DVClassNode> nodesDVClass = null;
	
	public ClassDependencyHandler(XRayWorkbenchView view) {
		super();
		this.view = view;
	}
	
	public void init(){
		figuresWrapperDefautWidth = -1;
		figuresWrapper = createContainerFigure();
		createDVClassNodes();
		addDVClassNodesToContent(figuresWrapper);
		circleLayout = createCircleClassLayout(getCurrentFilters());
	}
	
	private void createDVClassNodes(){
		nodesDVClass = new ArrayList<DVClassNode>();
		ArrayList<ClassRepresentation> allClasses =
			getProject().getAllClasses();
		int size = allClasses.size();
		for(int i = 0; i < size; i++){
			ClassRepresentation currentClass = allClasses.get(i);
			DVClassNode currentNode = null;
			if(currentClass.isComplete()){
				currentNode = new DVClassNode(currentClass, view);
				nodesDVClass.add(currentNode);
			}
		}
	}
	
	private void addDVClassNodesToContent(Figure contents){
		for(int i = 0; i < nodesDVClass.size(); i++){
			DVClassNode currentFigure = nodesDVClass.get(i);
			contents.add(currentFigure);
		}
	}
	
	private CircleLayout createCircleClassLayout(ArrayList<Filter> filters){
		CircleLayout circleLayout = new CircleLayout();
		circleLayout.layoutClass(nodesDVClass, figuresWrapper, view, true, filters);
		return circleLayout;
	}
	
	public void setViewContentBounds(){
		figuresWrapper.setBounds(
				circleLayout.getBounds(ViewFacade.CLASS_DEPENDENCY,
						getCurrentFilters()));
		
		figuresWrapperDefautWidth = figuresWrapper.getBounds().width;
	}
	
	public Node getFigureForClass(ClassRepresentation currentClass){
		return circleLayout.getNodeForClass(currentClass);
	}

	@Override
	protected void applyZooming(ArrayList<Filter> filters) {
		try{
			int size = nodesDVClass.size();
			
			for(int i = 0; i < size; i++){
				DVClassNode currentNode = nodesDVClass.get(i);
				currentNode.zoomSize(zoomingCoefficient);
			}
			
			refreshView(filters);
			refreshView(filters); // FIXME ugly hack
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public void originalSize(ArrayList<Filter> filters) {
		zoomingCoefficient = 1;
		int size = nodesDVClass.size();
		for(int i = 0; i < size; i++){
			DVClassNode currentNode = nodesDVClass.get(i);
			currentNode.setOriginalSize();
		}
		refreshView(filters);
	}
	
	@Override
	public void refreshView(ArrayList<Filter> filters){
		circleLayout.layoutClass(nodesDVClass, figuresWrapper, view, false, filters);
		setViewContentBounds();
		view.showClassDependency();
	}

	@Override
	public void normalizeAllBorders(ArrayList<Filter> filters) {
		for(int i = 0; i < nodesDVClass.size(); i++){
			if(!filterOutNode(nodesDVClass.get(i), filters)){
				nodesDVClass.get(i).removeBorderTag();
			}
		}
	}

	private boolean filterOutNode(DVClassNode nodeToFilter, ArrayList<Filter> filters){
		for(int i = 0; i < filters.size(); i++){
			if(filters.get(i).matches(nodeToFilter.getClassRepresentation())){
				return true;
			}
		}
		return false;
	}

	@Override
	public void normalizeBorderColoredOf(Color color, ArrayList<Filter> filters) {
		for(int i = 0; i < nodesDVClass.size(); i++){
			DVClassNode currentNode = nodesDVClass.get(i);
			if(currentNode.isTagged(color) && !filterOutNode(currentNode, filters)){
				currentNode.removeBorderTag();
			}
		}
	}

	@Override
	public void invertSelection(ArrayList<Filter> filters) {
		for(int i = 0; i < nodesDVClass.size(); i++){
			if(!filterOutNode(nodesDVClass.get(i), filters)){
				addSelectedNode(nodesDVClass.get(i), true, false);
			}
		}
	}

	@Override
	public void selectNodesTagged(Color color, ArrayList<Filter> filters) {
		for(int i = 0; i < nodesDVClass.size(); i++){
			DVClassNode currentNode = nodesDVClass.get(i);
			if(currentNode.isTagged(color) && !filterOutNode(currentNode, filters)){
				addSelectedNode(currentNode, false, false);
			}
		}
	}

	@Override
	public void selectLeaves(ArrayList<Filter> filters, boolean select) {
		for(int i = 0; i < nodesDVClass.size(); i++){
			DVClassNode currentNode = nodesDVClass.get(i);
			if(currentNode.isLeaf(filters)){
				if(select){
					addSelectedNode(currentNode, false, false);
				} else {
					deselectNode(currentNode);
				}
			}
		}	
	}

	@Override
	public void selectRoots(ArrayList<Filter> filters, boolean select) {
		for(int i = 0; i < nodesDVClass.size(); i++){
			DVClassNode currentNode = nodesDVClass.get(i);
			if(currentNode.isRoot(filters)){
				if(select){
					addSelectedNode(currentNode, false, false);
				} else {
					deselectNode(currentNode);
				}
			}
		}
	}
	
	@Override
	public void selectBranches(ArrayList<Filter> filters, boolean select) {
		for(int i = 0; i < nodesDVClass.size(); i++){
			DVClassNode currentNode = nodesDVClass.get(i);
			if(!currentNode.isRoot(filters) &&
					!currentNode.isLeaf(filters)){
				if(select){
					addSelectedNode(currentNode, false, false);
				} else {
					deselectNode(currentNode);
				}
			}
		}
	}

	@Override
	public void selectChildrenOfSelectedNodes(
			ArrayList<Filter> filters, boolean select) {		
		for(int i = 0; i < selectedFigures.size(); i++){
			selectChildrenOfGivenNode(
					selectedFigures.get(i), filters, select);
		}
		
	}
	
	private void selectChildrenOfGivenNode(
			Node parent, ArrayList<Filter> filters, boolean select){
		ArrayList<EntityRepresentation> childrenClasses =
			new ArrayList<EntityRepresentation>();
		
		childrenClasses = parent.getChildrenEntities(filters);
		
		for(int j = 0; j < childrenClasses.size(); j++){
			Node childrenNode =
				circleLayout.getNodeForClass(
						(ClassRepresentation)childrenClasses.get(j));
			if(select){
				addSelectedNode(childrenNode, false, false);
			} else {
				deselectNode(childrenNode);
			}
			
			selectChildrenOfGivenNode(childrenNode, filters, select);
		}
	}

	@Override
	public void selectParentsOfSelectedNodes(boolean select) {
		for(int i = 0; i < selectedFigures.size(); i++){
			selectParentOfGivenNode(
					(DVClassNode)selectedFigures.get(i), select);
		}
	}
	
	private void selectParentOfGivenNode(DVClassNode child, boolean select) {
		if(select){
			addSelectedNode(child, false, false);
		} else {
			deselectNode(child);
		}
		
		ClassRepresentation parent = child.getClassRepresentation().getSuperclass();
		
		if(parent != null){
			selectParentOfGivenNode(circleLayout.getNodeForClass(parent), select);
		}
		
	}

	@Override
	public void selectMatchingNodes(String pattern) {
		try{
			pattern = pattern.toLowerCase().trim();
			
			for(int i = 0; i < nodesDVClass.size(); i++){
				DVClassNode currentNode = nodesDVClass.get(i);
				ClassRepresentation classInNode = currentNode.getClassRepresentation();
				if(!filterOutNode(currentNode, filters) &&
						Pattern.matches(pattern, classInNode.getName().toLowerCase())){
					addSelectedNode(currentNode, false, false);
				}
			}
		}catch(Exception e){
			Log.printError("selectMatchingNodes:" + e.toString());
		}
	}

	@Override
	public void selectSiblingsOfSelectedNodes(
			ArrayList<Filter> filters, boolean select) {
		for(int i = 0; i < selectedFigures.size(); i++){
			selectSiblingsofNode(selectedFigures.get(i), filters, select);
		}
	}
	
	private void selectSiblingsofNode(
			Node node, ArrayList<Filter> filters, boolean select) {
		ClassRepresentation parent = 
			((DVClassNode)node).getClassRepresentation().getSuperclass();
		if(parent != null){
			ArrayList<ClassRepresentation> sibling = parent.getChildren(filters);
			for(int i = 0; i < sibling.size(); i++){
				if(select){
					addSelectedNode(
							circleLayout.getNodeForClass(sibling.get(i)), false, false);
				} else {
					deselectNode(circleLayout.getNodeForClass(sibling.get(i)));
				}
			}
		}
		
	}

	@Override
	public void selectClassKind(String kind, ArrayList<Filter> filters) {
		ArrayList<DVClassNode> nodeOfKind = getNodesOfKind(kind);
		for(int i = 0; i < nodeOfKind.size(); i++){
			if(!filterOutNode(nodeOfKind.get(i), filters)){
				addSelectedNode(nodeOfKind.get(i), false, false);
			}
		}
	}
	
	@Override
	public void deSelectClassKind(String kind, ArrayList<Filter> currentFilters) {
		ArrayList<DVClassNode> nodeOfKind = getNodesOfKind(kind);
		for(int i = 0; i < nodeOfKind.size(); i++){
			if(!filterOutNode(nodeOfKind.get(i), filters)){
				deselectNode(nodeOfKind.get(i));
			}
		}
	}
	
	private ArrayList<DVClassNode> getNodesOfKind(String kind){
		ArrayList<DVClassNode> nodeOfKind = new ArrayList<DVClassNode>();
		for(int i = 0; i < nodesDVClass.size(); i++){
			DVClassNode current = nodesDVClass.get(i);
			if(current.getClassRepresentation().is(kind)){
				nodeOfKind.add(current);
			}
		}
		return nodeOfKind;
	}

	@Override
	public void deSelectNodesTagged(Color color, ArrayList<Filter> filters) {
		for(int i = 0; i < selectedFigures.size(); i++){
			DVClassNode currentNode = (DVClassNode)selectedFigures.get(i);
			if(currentNode.isTagged(color) && !filterOutNode(currentNode, filters)){
				deselectNode(currentNode); // changing selectedFigures.size()
				deSelectNodesTagged(color, filters);
			} 
		}
	}
	
	public String toString(){
		return "Class Dependency Handler";
	}

}
