package org.malnatij.svplugin.views.handlers;

import java.util.ArrayList;
import java.util.regex.Pattern;

import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Color;
import org.malnatij.svplugin.filters.Filter;
import org.malnatij.svplugin.graph.links.Link;
import org.malnatij.svplugin.graph.nodes.ExternalNode;
import org.malnatij.svplugin.graph.nodes.InternalNode;
import org.malnatij.svplugin.graph.nodes.Node;
import org.malnatij.svplugin.graph.nodes.SCVNode;
import org.malnatij.svplugin.layouts.TreeLayout;
import org.malnatij.svplugin.mapping.NullMapping;
import org.malnatij.svplugin.mapping.Mapping;
import org.malnatij.svplugin.model.ClassRepresentation;
import org.malnatij.svplugin.model.EntityRepresentation;
import org.malnatij.svplugin.util.Log;
import org.malnatij.svplugin.views.XRayWorkbenchView;

public class SystemComplexityHandler
extends PolymetricViewHandler{
	
	// SCV (system complexity view)
	private TreeLayout treeLayout = null;
	private ArrayList<SCVNode> nodesSCV = null;
	private ArrayList<PolylineConnection> interfaceConnections = 
		new ArrayList<PolylineConnection>();
	private ArrayList<PolylineConnection> dependencyConnections =
		new ArrayList<PolylineConnection>();
	
	
	public SystemComplexityHandler(XRayWorkbenchView view){
		super();
		this.view = view;
	}

	public void init() {
		try {
			if (getProject() == null) {
				Log.printError("null project in SVPLuginView.drawProject()");
				return;
			}
			figuresWrapperDefautWidth = -1;
			Mapping currentMapping = new NullMapping();
			// Scaling currentScaling = new MaxBoundsScaling(view.getProject(),
			// 80, 350);
			// create all the nodes from the classes
			createSCVNodes(currentMapping);
			// create a tree layout and use that to create the nodes' tree
			treeLayout = createTreeLayout();
			// create a figure as container
			figuresWrapper = createContainerFigure();
			// add the nodes to the content
			addSCVNodesToContent(filters);
			// add the connection between nodes to the content
			addConnectionsToContent(filters);
			// set the dimension of the whole content
			setViewContentBounds();
			view.getPanel().setContents(figuresWrapper);
			view.getLws().setContents(view.getPanel());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void refreshFigureWrapper(ArrayList<Filter> filters){
		// create a figure as container
		figuresWrapper = createContainerFigure();
		
		addSCVNodesToContent(filters);
		addConnectionsToContent(filters);
		addDependencyLinks(filters);
		addInterfaceLinks();
		
		view.getPanel().setContents(figuresWrapper);
		// set the dimension of the whole content
		setViewContentBounds();
	}
	
	protected void resetAllNodesAndLinks(){
		deSelectNodes();
		deHilightNodes();
		removeLinks();
	}
	
	public void removeLinks(){
		int dSize = dependencyConnections.size();
		for(int i = 0 ; i < dSize; i++){
			figuresWrapper.remove(dependencyConnections.get(i));
		}

		int iSize = interfaceConnections.size();
		for(int i = 0 ; i < iSize; i++){
			figuresWrapper.remove(interfaceConnections.get(i));
		}

		treeLayout.resetInterfaceConnections();
		interfaceConnections = new ArrayList<PolylineConnection>();
		treeLayout.resetDependencyConnections();
		dependencyConnections = new ArrayList<PolylineConnection>();

		figuresWrapper.repaint();
	}
	
	private void createSCVNodes(Mapping nodeScaling){
		nodesSCV = new ArrayList<SCVNode>();
		ArrayList<ClassRepresentation> allClasses = getProject().getAllClasses();
		int size = allClasses.size();
		for(int i = 0; i < size; i++){
			ClassRepresentation currentClass = allClasses.get(i);
			SCVNode currentNode = null;
			if(currentClass.isComplete()){
				currentNode = new InternalNode(currentClass, nodeScaling, view);
			} else {
				currentNode = new ExternalNode(currentClass, view);
			}

			nodesSCV.add(currentNode);
		}
	}
	
	private TreeLayout createTreeLayout(){
		TreeLayout treeLayout = new TreeLayout();
		treeLayout.layout(nodesSCV, getProject(), view, getCurrentFilters());
		return treeLayout;
	}
	
	private void addSCVNodesToContent(ArrayList<Filter> filters){
		for(int i = 0; i < nodesSCV.size(); i++){
			SCVNode currentFigure = (SCVNode) nodesSCV.get(i);
			if(!filterOutNode(currentFigure, filters)){
				figuresWrapper.add(currentFigure);
				Log.printSVPluginView(
						"Figure:" + currentFigure.getClassRepresentation().getName() +
						", " + currentFigure.getBounds().x + ", "
						+ currentFigure.getBounds().y);
			}	
		}
	}
	
	private void addConnectionsToContent(ArrayList<Filter> filters){
		ArrayList<PolylineConnection> connections = treeLayout.getConnections();
		for(int i = 0; i < connections.size(); i++){
			if(!filterOutNode((SCVNode)connections.get(i).
					getSourceAnchor().getOwner(), filters)
					&& !filterOutNode((SCVNode)connections.get(i).
							getTargetAnchor().getOwner(), filters)){
				figuresWrapper.add(connections.get(i));
			}
		}
	}
	
	public void setViewContentBounds(){
		Rectangle currentBounds = new Rectangle(0, 0,
				treeLayout.getMaxTreeX(),
				treeLayout.getMaxTreeY());
		
		figuresWrapper.setBounds(currentBounds);
		
		figuresWrapperDefautWidth = figuresWrapper.getBounds().width;
	}
	
	public Node getFigureForClass(ClassRepresentation currentClass){
		return treeLayout.getFigureForClass(currentClass);
	}
	
	public void handleInterfaceLinkRequest(ArrayList<Filter> filters){
		try{
			for(int i = 0; i < selectedFigures.size(); i++){
				SCVNode currentNode = (SCVNode) selectedFigures.get(i);
				// check if it is an interface or a concrete class
				ClassRepresentation classInSelectedNode =
					currentNode.getClassRepresentation();
				if(classInSelectedNode.isInterface()){
					// interface, then link all the implementors
					Log.printSVPluginView("interface: " + classInSelectedNode.getName());
					treeLayout.linkImplementingClassOf(classInSelectedNode, filters);
					addInterfaceLinks();
				} else {
					// concrete, then link all the implemented interfaces
					Log.printSVPluginView("concrete " + classInSelectedNode.getName());
					if(classInSelectedNode.hasInterfaces()){
						Log.printSVPluginView(
								"has #interface(s): " 
								+ classInSelectedNode.getImplementedClass().size());
						treeLayout.linkImplementedInterfaces(classInSelectedNode, filters);
						addInterfaceLinks();
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private void addInterfaceLinks(){
		interfaceConnections =
			treeLayout.getInterfaceConnections(filters);
		int size = interfaceConnections.size();
		if(size > 0){
			Log.printSVPluginView("going to add #connection(s): " + size);
			for(int i = 0; i < size; i++){
				figuresWrapper.add(interfaceConnections.get(i));
			}
			figuresWrapper.repaint();
		}
	}

	public void handleOutgoingDependencyLinkRequest(ArrayList<Filter> filters) {
		for(int i = 0; i < selectedFigures.size(); i++){
			InternalNode currentNode = (InternalNode) selectedFigures.get(i);
			ClassRepresentation classInSelectedNode =
				currentNode.getClassRepresentation();
			if(classInSelectedNode.hasDependencies()){
				treeLayout.linkOutgoingDependencyClassesFor(classInSelectedNode, filters);
				addDependencyLinks(filters);
			}
		}
	}
	
	public void handleIncomingDependencyLinkRequest(ArrayList<Filter> filters){
		for(int i = 0; i < selectedFigures.size(); i++){
			InternalNode currentNode = (InternalNode) selectedFigures.get(i);
			ClassRepresentation classInSelectedNode =
				currentNode.getClassRepresentation();
			treeLayout.linkIncomingDependencyClassesFor(classInSelectedNode, filters);
			addDependencyLinks(filters);
		}
	}
	
	public void handleOutgoingDependencyLinkRequest(ClassRepresentation classToLink,
			ArrayList<Filter> filters){
		resetAllNodesAndLinks();
		InternalNode node = (InternalNode) treeLayout.getFigureForClass(classToLink);
		addSelectedNode(node, true, true);
		if(classToLink.hasDependencies()){
			treeLayout.linkOutgoingDependencyClassesFor(classToLink, filters);
			addDependencyLinks(filters);
		}
	}
	
	private void addDependencyLinks(ArrayList<Filter> filters){
		dependencyConnections = treeLayout.getDependencyConnections(filters);
		int size = dependencyConnections.size();
		if(size > 0){
			Log.printSVPluginView("going to add #connection(s): " + size);
			for(int i = 0; i < size; i++){
				figuresWrapper.add(dependencyConnections.get(i));
			}
			figuresWrapper.repaint();
		}
	}

	protected void applyZooming(ArrayList<Filter> filters) {
		try{
			int size = nodesSCV.size();
			for(int i = 0; i < size; i++){
				SCVNode currentNode = nodesSCV.get(i);
				currentNode.zoomSize(zoomingCoefficient);
			}
			refreshView(filters);
		}catch (Exception e){
			e.printStackTrace();
		}
		
	}

	@Override
	public void originalSize(ArrayList<Filter> filters) {
		zoomingCoefficient = 1;
		int size = nodesSCV.size();
		for(int i = 0; i < size; i++){
			SCVNode currentNode = nodesSCV.get(i);
			currentNode.setOriginalSize();
		}
		refreshView(filters);
		
	}

	@Override
	public void normalizeAllBorders(ArrayList<Filter> filters) {
		for(int i = 0; i < nodesSCV.size(); i++){
			if(!filterOutNode(nodesSCV.get(i), filters)){
				nodesSCV.get(i).removeBorderTag();
			}
		}
		
	}

	@Override
	public void normalizeBorderColoredOf(Color color, ArrayList<Filter> filters) {
		for(int i = 0; i < nodesSCV.size(); i++){
			SCVNode currentNode = nodesSCV.get(i);
			if(currentNode.isTagged(color) && !filterOutNode(currentNode, filters)){
				currentNode.removeBorderTag();
			}
		}
	}
	
	private boolean filterOutNode(SCVNode nodeToFilter, ArrayList<Filter> filters){
		for(int i = 0; i < filters.size(); i++){
			if(filters.get(i).matches(nodeToFilter.getClassRepresentation())){
				return true;
			}
		}
		return false;
	}

	@Override
	public void invertSelection(ArrayList<Filter> filters) {
		for(int i = 0; i < nodesSCV.size(); i++){
			if(!filterOutNode(nodesSCV.get(i), filters)){
				addSelectedNode(nodesSCV.get(i), true, false);
			}
		}
		
	}

	@Override
	public void selectNodesTagged(Color color, ArrayList<Filter> filters) {
		for(int i = 0; i < nodesSCV.size(); i++){
			SCVNode currentNode = nodesSCV.get(i);
			if(currentNode.isTagged(color) && !filterOutNode(currentNode, filters)){
				addSelectedNode(currentNode, false, false);
			}
		}
	}

	@Override
	public void selectLeaves(ArrayList<Filter> filters, boolean select) {
		for(int i = 0; i < nodesSCV.size(); i++){
			SCVNode currentNode = nodesSCV.get(i);
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
		for(int i = 0; i < nodesSCV.size(); i++){
			SCVNode currentNode = nodesSCV.get(i);
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
		for(int i = 0; i < nodesSCV.size(); i++){
			SCVNode currentNode = nodesSCV.get(i);
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
				treeLayout.getFigureForClass(
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
			selectParentOfGivenNode((SCVNode)selectedFigures.get(i), select);
		}
	}

	private void selectParentOfGivenNode(SCVNode child, boolean select) {
		if(select){
			addSelectedNode(child, false, false);
		} else {
			deselectNode(child);
		}
		
		ClassRepresentation parent = child.getClassRepresentation().getSuperclass();
		
		if(parent != null){
			selectParentOfGivenNode(treeLayout.getFigureForClass(parent), select);
		}
		
	}

	@Override
	public void selectMatchingNodes(String pattern) {
		try{
			pattern = pattern.toLowerCase().trim();
			
			for(int i = 0; i < nodesSCV.size(); i++){
				SCVNode currentNode = nodesSCV.get(i);
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
		ClassRepresentation parent = ((SCVNode)node).getClassRepresentation().getSuperclass();
		
		if(parent != null){
			ArrayList<ClassRepresentation> sibling = parent.getChildren(filters);
			for(int i = 0; i < sibling.size(); i++){
				if(select){
					addSelectedNode(
							treeLayout.getFigureForClass(sibling.get(i)), false, false);
				} else {
					deselectNode(treeLayout.getFigureForClass(sibling.get(i)));
				}
				
			}
		}
		
	}

	@Override
	public void refreshView(ArrayList<Filter> filters) {
		treeLayout.layout(nodesSCV, getProject(), view, filters);
		refreshFigureWrapper(filters);
	}

	@Override
	public void selectClassKind(String kind, ArrayList<Filter> filters) {
		ArrayList<SCVNode> nodeOfKind = getNodesOfKind(kind);

		for(int i = 0; i < nodeOfKind.size(); i++){
			if(!filterOutNode(nodeOfKind.get(i), filters)){
				addSelectedNode(nodeOfKind.get(i), false, false);
			}
		}

	}
	
	@Override
	public void deSelectClassKind(String kind, ArrayList<Filter> currentFilters) {
		ArrayList<SCVNode> nodeOfKind = getNodesOfKind(kind);

		for(int i = 0; i < nodeOfKind.size(); i++){
			if(!filterOutNode(nodeOfKind.get(i), filters)){
				deselectNode(nodeOfKind.get(i));
			}
		}
	}

	private ArrayList<SCVNode> getNodesOfKind(String kind){
		ArrayList<SCVNode> nodeOfKind = new ArrayList<SCVNode>();

		for(int i = 0; i < nodesSCV.size(); i++){
			SCVNode current = nodesSCV.get(i);
			if(current.getClassRepresentation().is(kind)){
				nodeOfKind.add(current);
			}
		}

		return nodeOfKind;
	}

	@Override
	public void deSelectNodesTagged(Color color, ArrayList<Filter> filters) {
		for(int i = 0; i < selectedFigures.size(); i++){
			SCVNode currentNode = (SCVNode)selectedFigures.get(i);
			if(currentNode.isTagged(color) && !filterOutNode(currentNode, filters)){
				deselectNode(currentNode); // changing selectedFigures.size()
				deSelectNodesTagged(color, filters);
			} 
		}
	}
	
	public String toString(){
		return "System Complexity Handler";
	}

	@Override
	public void showJustDependencyInRange(int[] range) {
		ArrayList<PolylineConnection> links =
			treeLayout.getDependencyConnections(filters);
		
		for(int i = 0; i < links.size(); i++){
			Link currentLink = (Link) links.get(i);
			
			if(currentLink.getWeigth() >= range[0] &&
					currentLink.getWeigth() <= range[1]){
				currentLink.setVisible(true);
			} else {
				currentLink.setVisible(false);
			}
			
			currentLink.repaint();
		}
		
	}

	@Override
	public int getMaxDependencyWeigth() {
		int max = 0;
		ArrayList<PolylineConnection> links =
			treeLayout.getDependencyConnections(filters);
		
		for(int i = 0; i < links.size(); i++){
			Link currentLink = (Link) links.get(i);
			
			if(currentLink.getWeigth() > max) 
				max = currentLink.getWeigth();
		}
		
		return max;
	}

}
