package org.malnatij.svplugin.layouts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.malnatij.svplugin.filters.Filter;
import org.malnatij.svplugin.graph.links.DownAnchor;
import org.malnatij.svplugin.graph.links.Link;
import org.malnatij.svplugin.graph.links.UpAnchor;
import org.malnatij.svplugin.graph.nodes.InternalNode;
import org.malnatij.svplugin.graph.nodes.Node;
import org.malnatij.svplugin.graph.nodes.SCVNode;
import org.malnatij.svplugin.model.ClassRepresentation;
import org.malnatij.svplugin.model.ProjectRepresentation;
import org.malnatij.svplugin.util.Colors;
import org.malnatij.svplugin.util.Log;
import org.malnatij.svplugin.views.XRayWorkbenchView;

public class TreeLayout extends Layout{
	private ArrayList<SCVNode> nodes;
	private ArrayList<PolylineConnection> interfaceConnections =
		new ArrayList<PolylineConnection>();
	private ArrayList<PolylineConnection> dependencyConnections =
		new ArrayList<PolylineConnection>();

	private int maxTreeX;
	private int maxTreeY;

	private void initMaxValues(){
		maxTreeX = -1;
		maxTreeY = -1;
	}
	
	private void handleRoots(ProjectRepresentation theProject,
			ArrayList<Filter> filters){
		// all the roots must be filtered
		ArrayList<ClassRepresentation> roots = 
			filterClassList(theProject.getRootClasses(filters), filters);
		Point origin = new Point(10,10);
		Point corner;
		for(int i = 0; i < roots.size(); i++){
			
			SCVNode currentFigure = getFigureForClass(roots.get(i));
			corner = drawTree(currentFigure, origin, filters);
			origin.x = (int)
				(corner.x +
				(Layout.H_SPACING_BETWEEN_NODES *
						theView.getCurrentZoomingCoefficient()));
		}
	}
	
	protected ArrayList<ClassRepresentation> filterClassList(
			ArrayList<ClassRepresentation> unFilteredList,
			ArrayList<Filter> filters){
		
		ArrayList<ClassRepresentation> filteredList =
			new ArrayList<ClassRepresentation>();
		
		
		for(int i = 0; i < unFilteredList.size(); i++){
			ClassRepresentation classToFilter = unFilteredList.get(i);
			if(!filtersOut(classToFilter, filters)){
				filteredList.add(classToFilter);
			}
		}
		
		
		return filteredList;
	}
	
	private void nodePositioning(ArrayList<Filter> filters){
		// just handling filtered nodes
		ArrayList<ClassRepresentation> allNodesClasses = filterSCVNodeList(nodes, filters);
		for(int i = 0; i < allNodesClasses.size(); i++){
			ClassRepresentation currentClass = allNodesClasses.get(i);
			
			if(currentClass.hasChildren(filters)){
				Log.printLayout("Ok, node to move: " +
						currentClass.getName());
				setNewParentPosition(getFigureForClass(currentClass), filters);
			}
		}
	}
	
	public void layout(ArrayList<SCVNode> nodes,
			ProjectRepresentation theProject,
			XRayWorkbenchView theView,
			ArrayList<Filter> filters){
		
			initMaxValues();
			this.nodes = nodes;
			this.theView = theView;
			handleRoots(theProject, filters);
			nodePositioning(filters);		
	}

	public SCVNode getFigureForClass(ClassRepresentation theClass) {
		try{
			for (int i = 0; i < nodes.size(); i++) {
				SCVNode currentNode = nodes.get(i);
				if (theClass.equals(currentNode.getClassRepresentation())) {
					return currentNode;
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		Log.printError("0_o Panic: no node for: " + theClass);
		return null;
	}

	private ArrayList<SCVNode> getChildrenForNode(SCVNode currentNode,
			ArrayList<Filter> filters){
		ArrayList<SCVNode> result = new ArrayList<SCVNode>();
		ArrayList<ClassRepresentation> children =
			currentNode.getClassRepresentation().getChildren(filters);
		for(int i = 0; i < children.size(); i++){
			SCVNode currentChild = getFigureForClass(children.get(i));
			setConnectionBetween(currentNode, currentChild);
			result.add(currentChild);
		}
		return result;
	}

	private void setConnectionBetween(Node currentNode, Node currentChild){
		// a node can have just one father
		if(currentChild.getIncomingConnection() == null){
			PolylineConnection c = new PolylineConnection();
			DownAnchor sourceAnchor = new DownAnchor(currentNode);
			UpAnchor targetAnchor = new UpAnchor(currentChild);
			c.setSourceAnchor(sourceAnchor);
			c.setTargetAnchor(targetAnchor);
			c.setForegroundColor(new Color(null, 100, 100, 100));
			connections.add(c);
			// add the incoming connection to the node so that the node can hide it
			currentChild.setIncomingConnection(c);
		}
	}

	public ArrayList<PolylineConnection> getConnections(){
		return connections;
	}

	private int[] getMinMaxFromChild(SCVNode node, ArrayList<Filter> filters){
		Log.printLayout("analyzing child: " + node.getClassRepresentation().getName());
		int[] minMaxChild = {-1, -1};
		minMaxChild[0] = node.getBounds().x;
		minMaxChild[1] = minMaxChild[0] + node.getBounds().width;
		return minMaxChild;
	}

	private int[] getMinMaxX(SCVNode currentNode, ArrayList<Filter> filters){
		int[] minMaxX = {currentNode.getBounds().x,
				currentNode.getBounds().x + currentNode.getBounds().width}; // min, max
		ClassRepresentation classRepresentation = currentNode.getClassRepresentation();
		ArrayList<SCVNode> childrenFigures =  getChildrenForNode(currentNode, filters);
		if(classRepresentation.hasChildren(filters)){
			int size = childrenFigures.size();
			minMaxX[0] = childrenFigures.get(0).getBounds().x;
			int i;
			for(i = 0; i < size; i++){
				int[] minMaxChild = getMinMaxFromChild(childrenFigures.get(i), filters);
				// smaller min X
				if(minMaxChild[0] < minMaxX[0]){
					Log.printLayout("found a smaller x: " + minMaxChild[0]);
					minMaxX[0] = minMaxChild[0];
				}
				// bigger max X
				if(minMaxChild[1] > minMaxX[1]){
					Log.printLayout("found a bigger x: " + minMaxChild[1]);
					minMaxX[1] = minMaxChild[1];
				}
			}
			return minMaxX;
		} else {
			// return the min max X for the node itself
			int[] currentMinMaxX = new int[2];
			currentMinMaxX[0] = currentNode.getLocation().x;
			currentMinMaxX[1] = currentMinMaxX[0] + currentNode.getSize().width;
			return currentMinMaxX;
		}
	}

	private void setNewParentPosition(SCVNode currentNode, ArrayList<Filter> filters){
		try{
			Log.printLayout("setting new position for: "
					+ currentNode.getClassRepresentation().getName() +
					" [" + currentNode.getBounds().x 
					+ "-" + (currentNode.getBounds().x +
							currentNode.getBounds().width) + "]");
			int[] bounds = getMinMaxX(currentNode, filters);
			int newX = (bounds[0] +
					((bounds[1] - bounds[0]) / 2)) - (currentNode.getBounds().width / 2);
			int previousY = currentNode.getLocation().y;
			if(newX > currentNode.getBounds().x){
				Log.printLayout("set new coords: [" + newX 
						+ "-" + (newX + currentNode.getBounds().width) + "]");
				currentNode.setLocation(new Point(newX, previousY));
				currentNode.setNodePosition(newX, previousY);
				if(currentNode.getClassRepresentation().hasSuperClass(filters)){
					ClassRepresentation superClass = 
						currentNode.getClassRepresentation().getSuperclass();
					Log.printLayout("propagating to the parent: " + superClass.getName());
					setNewParentPosition(getFigureForClass(superClass), filters);				
				}
			} else {
				if(currentNode.getClassRepresentation().isComplete()){
					Log.printLayout("to fix children of: " +
							currentNode.getClassRepresentation().getName());
					setNewChildrenPositionOf(currentNode, filters);
				}	
			}
		} catch(Exception e){
			e.printStackTrace();
		}
	}

	private void setNewChildrenPositionOf(SCVNode parent, ArrayList<Filter> filters){
		ArrayList<Node> childrenNodes = new ArrayList<Node>();
		int childrenSize = 0;
		ArrayList<ClassRepresentation> childrenClasses =
			parent.getClassRepresentation().getChildren(filters);
		int length = childrenClasses.size();
		for(int i = 0; i < length; i++){
			Node currentNode = getFigureForClass(childrenClasses.get(i));
			childrenNodes.add(currentNode);
			childrenSize +=
				(int)
				(currentNode.getBounds().width +
						Layout.H_SPACING_BETWEEN_NODES *
						theView.getCurrentZoomingCoefficient());
		}
		childrenSize -=
			(int) (Layout.H_SPACING_BETWEEN_NODES *
					theView.getCurrentZoomingCoefficient());
		int parentSize = parent.getBounds().width;
		Log.printLayout("children size: " + childrenSize + " parent size: " + parentSize);
		int shift = (parentSize-childrenSize)/2;
		if(shift > 0){
			shiftChildrenBy(shift, childrenNodes);;
		}
	}

	private void shiftChildrenBy(int shift, ArrayList<Node> childrenNodes){
		Log.printLayout("going to shift: " + childrenNodes.size() + " node(s) by " + shift);
		int length = childrenNodes.size();
		for(int i = 0; i < length; i++){
			Node currentNode = childrenNodes.get(i);
			int previousX = currentNode.getBounds().x;
			currentNode.getBounds().x = previousX + shift;
		}

	}

	private Point drawTree(SCVNode currentFigure, Point origin, ArrayList<Filter> filters){
		ClassRepresentation classRepresentation = currentFigure.getClassRepresentation();
		Point currentCorner = null;
		double childrenY = (int) (origin.y +
		currentFigure.getBounds().height +
		Layout.V_SPACING_BETWEEN_NODES *
				theView.getCurrentZoomingCoefficient());
		double currentX = origin.x;
		if(classRepresentation.hasChildren(filters)){
			// has children
			ArrayList<SCVNode> childrenFigures =  getChildrenForNode(currentFigure, filters);
			for(int i = 0; i < childrenFigures.size(); i++){
				Point newOrigin = new Point(currentX, childrenY);
				currentCorner = drawTree(childrenFigures.get(i), newOrigin, filters);
				currentX = (int) (
					currentCorner.x +
					Layout.H_SPACING_BETWEEN_NODES *
							theView.getCurrentZoomingCoefficient());
			}
			currentFigure.setLocation(origin);
			currentFigure.setNodePosition(origin.x, origin.y);
			// check if the last children has an X+width smaller than its parent
			if(currentCorner.x <
					currentFigure.getBounds().x + currentFigure.getBounds().width){
				Log.printLayout("x smaller for last children than for parent:"
						+ currentFigure.getClassRepresentation().getName());
				currentCorner.setLocation(
						currentFigure.getBounds().x 
						+ currentFigure.getBounds().width,
						currentCorner.y);
			}
			checkMaxTreeXY(currentFigure);
			return currentCorner;
		} else {
			// has no children
			currentFigure.setLocation(origin);
			currentFigure.setNodePosition(origin.x, origin.y);
			checkMaxTreeXY(currentFigure);
			return new Point(origin.x + currentFigure.getBounds().width,
					origin.y + currentFigure.getBounds().height);
		}
	}

	public int getMaxTreeX(){
		return maxTreeX;
	}

	public int getMaxTreeY(){
		return maxTreeY;
	}

	private void checkMaxTreeXY(SCVNode currentFigure){
		Rectangle bounds = currentFigure.getBounds();
		int maxX = 
			(int) (bounds.x +
			bounds.width +
			Layout.H_SPACING_BETWEEN_NODES *
			theView.getCurrentZoomingCoefficient());
		int maxY = 
			(int) (bounds.y +
			bounds.height +
			Layout.V_SPACING_BETWEEN_NODES *
					theView.getCurrentZoomingCoefficient());
		if(maxX > maxTreeX){
			maxTreeX = maxX;
		}
		if(maxY > maxTreeY){
			maxTreeY = maxY;
		}
	}

	/**
	 * Given an interface, create links for all the classes implementing it
	 * @param interfaceClass the interface, used as root for the connections
	 */
	public void linkImplementingClassOf(ClassRepresentation interfaceClass,
			ArrayList<Filter> filters){
		
		ArrayList<Node> implementors = new ArrayList<Node>();
		
		for (int i = 0; i < nodes.size(); i++) {
			SCVNode currentNode = nodes.get(i);
			ArrayList<ClassRepresentation> implementedClasses = currentNode
					.getClassRepresentation().getImplementedClass();

			for (int j = 0; j < implementedClasses.size(); j++) {
				// FIXME equals
				if (interfaceClass.getUniqueID().
						equals(implementedClasses.get(j).getUniqueID())) {
					if (!filtersOut(currentNode.getClassRepresentation(),
							filters)) {
						implementors.add(currentNode);
						((InternalNode) currentNode).addToHighlightNode();
					}
				}
			}
		}
		
		// if someone implemented this interface..
		if(implementors.size() > 0){
			// link them to this interface
			linkClassAndInterface(implementors, interfaceClass);
		}
		
	}
	
	private void linkClassAndInterface(ArrayList<Node> children,
			ClassRepresentation root){
		Node interfaceNode = getFigureForClass(root);
		// now let's link the interface to the implementors
		int size = children.size();
		for(int i = 0; i < size; i++){
			interfaceConnections.add(
					setCenterConnectionBetween(interfaceNode,
							children.get(i),
							Colors.HIGHLIGHT_COLOR,
							SWT.LINE_SOLID));
		}
	}
	
	public void linkImplementedInterfaces(ClassRepresentation concreteClass,
			ArrayList<Filter> filters){
		
		ArrayList<Node> interfaces = new ArrayList<Node>();
		ArrayList<ClassRepresentation> implementedInterfaces = concreteClass.getImplementedClass();
		
		for(int i = 0; i < nodes.size(); i++){
			SCVNode currentNode = nodes.get(i);
			if(isImplementedInterface(
					currentNode.getClassRepresentation().getUniqueID(),
					implementedInterfaces)
					&& !filtersOut(currentNode.getClassRepresentation(), filters)){
				
				interfaces.add(currentNode);
				currentNode.addToHighlightNode();
			}
		}
		linkClassAndInterface(interfaces, concreteClass);
	}
	
	private boolean isImplementedInterface(String ID, ArrayList<ClassRepresentation> implementedInterfaces){
		for(int i = 0; i < implementedInterfaces.size(); i++){
			if(implementedInterfaces.get(i).getUniqueID().equals(ID)){
				return true;
			}
		}
		return false;
	}
	
	public ArrayList<PolylineConnection> getInterfaceConnections(
			ArrayList<Filter> filters){
		ArrayList<PolylineConnection> filteredInterfaceConnections = 
			new ArrayList<PolylineConnection>();
		
		for(int i = 0; i < interfaceConnections.size(); i++){
			PolylineConnection currentConnection = interfaceConnections.get(i);
			SCVNode from =
				(SCVNode)currentConnection.getSourceAnchor().getOwner();
			
			SCVNode to =
				(SCVNode)currentConnection.getTargetAnchor().getOwner();
			
			if(!filtersOut(from.getClassRepresentation(), filters) &&
					!filtersOut(to.getClassRepresentation(), filters)){
				filteredInterfaceConnections.add(currentConnection);
			}
		}
		
		return filteredInterfaceConnections;
	}

	public void resetInterfaceConnections() {
		interfaceConnections = new ArrayList<PolylineConnection>();
	}

	public void linkOutgoingDependencyClassesFor(ClassRepresentation classInSelectedNode,
			ArrayList<Filter> filters) {
		Node originNode = getFigureForClass(classInSelectedNode);
		HashMap<ClassRepresentation, Integer> dependencyMap =
			classInSelectedNode.getUsedClasses();
		Set<ClassRepresentation> keySet = dependencyMap.keySet();
		Iterator<ClassRepresentation> classIterator = keySet.iterator();
		while(classIterator.hasNext()){
			ClassRepresentation currentClass = classIterator.next();
			if(!filtersOut(currentClass, filters)){
				int weigth = dependencyMap.get(currentClass);
				Node toNode = getFigureForClass(currentClass);
				((InternalNode) toNode).addToHighlightNode();
				dependencyConnections.add(linkSizedDependency(
						originNode,toNode, weigth,
						Layout.COLORED_AND_SIZED, Link.CLASS_LINK));
			}
		}
	}
	
	public void linkIncomingDependencyClassesFor(ClassRepresentation classInSelectedNode,
			ArrayList<Filter> filters) {
		// check which nodes have dependency for this class
		for(int i = 0 ; i < nodes.size(); i++){
			SCVNode currentNode = nodes.get(i);
			ClassRepresentation currentClass = currentNode.getClassRepresentation();
			if(!filtersOut(currentClass, filters)){
				// check if the current node has a dependency on this class
				if(currentClass.dependsOn(classInSelectedNode)){
					// add to the list of classes that have a dependency on this class
					((InternalNode) currentNode).addToHighlightNode();
					dependencyConnections.add(linkSizedDependency(
							currentNode, getFigureForClass(classInSelectedNode),
							currentClass.getDependencyWeightFor(classInSelectedNode),
							Layout.COLORED_AND_SIZED, Link.CLASS_LINK));
				}
			}
		}
	}
	
	public void resetDependencyConnections(){
		dependencyConnections = new ArrayList<PolylineConnection>();
	}

	public ArrayList<PolylineConnection> getDependencyConnections(
			ArrayList<Filter> filters) {
		ArrayList<PolylineConnection> filteredConnections =
			new ArrayList<PolylineConnection>();
		
		for(int i = 0; i < dependencyConnections.size(); i++){
			SCVNode from =
				(SCVNode)dependencyConnections.get(i).getSourceAnchor().getOwner();
			SCVNode to =
				(SCVNode)dependencyConnections.get(i).getTargetAnchor().getOwner();
			
			if(!filtersOut(from.getClassRepresentation(), filters)
					&& !filtersOut(to.getClassRepresentation(), filters)){
				filteredConnections.add(dependencyConnections.get(i));
			}
		}
		
		return filteredConnections;
	}

}
