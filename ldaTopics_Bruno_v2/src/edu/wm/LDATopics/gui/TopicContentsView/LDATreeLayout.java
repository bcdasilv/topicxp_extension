package edu.wm.LDATopics.gui.TopicContentsView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.text.View;

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
import org.malnatij.svplugin.layouts.Layout;
import org.malnatij.svplugin.model.ClassRepresentation;
import org.malnatij.svplugin.model.ProjectRepresentation;
import org.malnatij.svplugin.util.Colors;
import org.malnatij.svplugin.util.Log;
import org.malnatij.svplugin.views.XRayWorkbenchView;

import edu.wm.LDATopics.LDATopics;
import edu.wm.LDATopics.LDA.TopicMember;
import edu.wm.LDATopics.LDA.documents.LDAClassDocument;

public class LDATreeLayout extends Layout{
	private ArrayList<TreeNode> nodes;
	private ArrayList<PolylineConnection> interfaceConnections =
		new ArrayList<PolylineConnection>();
	private ArrayList<PolylineConnection> dependencyConnections =
		new ArrayList<PolylineConnection>();

	private int maxTreeX;
	private int maxTreeY;
	int width;
	int height;
	private TopicContentsHandler handler;
	private TreeNode rootNode;

	public LDATreeLayout(TopicContentsHandler topicContentsHandler, int width, int height) {
		this.handler = topicContentsHandler;
		this.width = width;
		this.height = height;
	}

	private void initMaxValues(){
		maxTreeX = -1;
		maxTreeY = -1;
	}
	
	private void handleRoots(ProjectRepresentation theProject,
			ArrayList<Filter> filters){
		// all the roots must be filtered
//		ArrayList<ClassRepresentation> roots = 
//			filterClassList(theProject.getRootClasses(filters), filters);
//		Point origin = new Point(0,0);
		Point corner;
//		for(int i = 0; i < roots.size(); i++){
//			//DEBUGING:
//			TreeNode currentFigure = getFigureForClass(roots.get(i));
			corner = drawTree(rootNode, new Point (width,0), new Point(0, height), filters, 0);
			//origin.x = (int)
			//	(corner.x +
			//	(Layout.H_SPACING_BETWEEN_NODES *
			//			theView.getCurrentZoomingCoefficient()));
//		}
	}
	
	protected ArrayList<ClassRepresentation> filterClassList(
			ArrayList<ClassRepresentation> unFilteredList,
			ArrayList<Filter> filters){
		
		ArrayList<ClassRepresentation> filteredList =
			new ArrayList<ClassRepresentation>();
		
		
		for(int i = 0; i < unFilteredList.size(); i++){
			ClassRepresentation classToFilter = unFilteredList.get(i);
			if(!filtersOut(classToFilter, filters)){
				TopicMember classDoc = LDATopics.getMap().getClassMap().topics[handler.viewTopic].getDocumentByName(classToFilter.getName()+".java"); //TODO: replace this hack with something proper
				// Only if the class is present in the topic we're investigating
				if (classDoc != null) {		
					filteredList.add(classToFilter);
				}
			}
		}
		
		
		return filteredList;
	}
	
	private void nodePositioning(ArrayList<Filter> filters){
		// just handling filtered nodes
	//	ArrayList<ClassRepresentation> allNodesClasses = //filterSCVNodeList(nodes, filters);
//		for(int i = 0; i < allNodesClasses.size(); i++){
//			ClassRepresentation currentClass = allNodesClasses.get(i);
//			
//			if(currentClass.hasChildren(filters)){
//				Log.printLayout("Ok, node to move: " +
//						currentClass.getName());
//				setNewParentPosition(getFigureForClass(currentClass), filters);
//			}
//		}
	}
	
	public void layout(TreeNode node,
			ProjectRepresentation theProject,
			XRayWorkbenchView theView,
			ArrayList<Filter> filters){
		
			initMaxValues();
			this.rootNode = node;
			rootNode.setBounds(new Rectangle(10, 10, 200, 400));
			this.theView = theView;
			handleRoots(theProject, filters);
			nodePositioning(filters);		
	}
	

	public TreeNode getFigureForClass(ClassRepresentation theClass) {
//		try{
//			for (int i = 0; i < nodes.size(); i++) {
//				TreeNode currentNode = nodes.get(i);
//				if ((currentNode instanceof TreeClassNode) &&
//					((TreeClassNode)currentNode).getClassRepresentation().equals(theClass)) {
//					return currentNode;
//				}
//			}
//		}catch(Exception e){
//			e.printStackTrace();
//		}
//		Log.printError("0_o Panic: no node for: " + theClass);
//		return null;
		// Is this sufficient? If not, ask the root node for the figure we're looking for.
		return rootNode;
	}

	private List<TreeNode> getChildrenForNode(TreeNode currentNode,
			ArrayList<Filter> filters){
		ArrayList<TreeNode> result = new ArrayList<TreeNode>();
//		ArrayList<ClassRepresentation> children =
//			currentNode.getClassRepresentation().getChildren(filters);
//		for(int i = 0; i < children.size(); i++){
//			DVTreeNode currentChild = getFigureForClass(children.get(i));
//			setConnectionBetween(currentNode, currentChild);
//			result.add(currentChild);
//		}
//		return result;
		List children = currentNode.getChildren();
		for (Object child : children) 
			if (child instanceof TreeNode) result.add((TreeNode)child);
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

	private int[] getMinMaxFromChild(TreeNode node, ArrayList<Filter> filters){
		//Log.printLayout("analyzing child: " + node.getClassRepresentation().getName());
		int[] minMaxChild = {-1, -1};
		minMaxChild[0] = node.getBounds().x;
		minMaxChild[1] = minMaxChild[0] + node.getBounds().width;
		return minMaxChild;
	}

	private int[] getMinMaxX(TreeNode currentNode, ArrayList<Filter> filters){
//		int[] minMaxX = {currentNode.getBounds().x,
//				currentNode.getBounds().x + currentNode.getBounds().width}; // min, max
//		ClassRepresentation classRepresentation = currentNode.getClassRepresentation();
//		ArrayList<DVTreeNode> childrenFigures =  getChildrenForNode(currentNode, filters);
//		if(classRepresentation.hasChildren(filters)){
//			int size = childrenFigures.size();
//			minMaxX[0] = childrenFigures.get(0).getBounds().x;
//			int i;
//			for(i = 0; i < size; i++){
//				int[] minMaxChild = getMinMaxFromChild(childrenFigures.get(i), filters);
//				// smaller min X
//				if(minMaxChild[0] < minMaxX[0]){
//					Log.printLayout("found a smaller x: " + minMaxChild[0]);
//					minMaxX[0] = minMaxChild[0];
//				}
//				// bigger max X
//				if(minMaxChild[1] > minMaxX[1]){
//					Log.printLayout("found a bigger x: " + minMaxChild[1]);
//					minMaxX[1] = minMaxChild[1];
//				}
//			}
//			return minMaxX;
//		} else {
//			// return the min max X for the node itself
//			int[] currentMinMaxX = new int[2];
//			currentMinMaxX[0] = currentNode.getLocation().x;
//			currentMinMaxX[1] = currentMinMaxX[0] + currentNode.getSize().width;
//			return currentMinMaxX;
//		}
		return new int[2];
	}

	private void setNewParentPosition(TreeNode currentNode, ArrayList<Filter> filters){
//		try{
//			Log.printLayout("setting new position for: "
//					+ currentNode.getClassRepresentation().getName() +
//					" [" + currentNode.getBounds().x 
//					+ "-" + (currentNode.getBounds().x +
//							currentNode.getBounds().width) + "]");
//			int[] bounds = getMinMaxX(currentNode, filters);
//			int newX = (bounds[0] +
//					((bounds[1] - bounds[0]) / 2)) - (currentNode.getBounds().width / 2);
//			int previousY = currentNode.getLocation().y;
//			if(newX > currentNode.getBounds().x){
//				Log.printLayout("set new coords: [" + newX 
//						+ "-" + (newX + currentNode.getBounds().width) + "]");
//				currentNode.setLocation(new Point(newX, previousY));
//				currentNode.setNodePosition(newX, previousY);
//				if(currentNode.getClassRepresentation().hasSuperClass(filters)){
//					ClassRepresentation superClass = 
//						currentNode.getClassRepresentation().getSuperclass();
//					Log.printLayout("propagating to the parent: " + superClass.getName());
//					setNewParentPosition(getFigureForClass(superClass), filters);				
//				}
//			} else {
//				if(currentNode.getClassRepresentation().isComplete()){
//					Log.printLayout("to fix children of: " +
//							currentNode.getClassRepresentation().getName());
//					setNewChildrenPositionOf(currentNode, filters);
//				}	
//			}
//		} catch(Exception e){
//			e.printStackTrace();
//		}
	}

	private void setNewChildrenPositionOf(TreeNode parent, ArrayList<Filter> filters){
//		ArrayList<Node> childrenNodes = new ArrayList<Node>();
//		int childrenSize = 0;
//		ArrayList<ClassRepresentation> childrenClasses =
//			parent.getClassRepresentation().getChildren(filters);
//		int length = childrenClasses.size();
//		for(int i = 0; i < length; i++){
//			Node currentNode = getFigureForClass(childrenClasses.get(i));
//			childrenNodes.add(currentNode);
//			childrenSize +=
//				(int)
//				(currentNode.getBounds().width +
//						Layout.H_SPACING_BETWEEN_NODES *
//						theView.getCurrentZoomingCoefficient());
//		}
//		childrenSize -=
//			(int) (Layout.H_SPACING_BETWEEN_NODES *
//					theView.getCurrentZoomingCoefficient());
//		int parentSize = parent.getBounds().width;
//		Log.printLayout("children size: " + childrenSize + " parent size: " + parentSize);
//		int shift = (parentSize-childrenSize)/2;
//		if(shift > 0){
//			shiftChildrenBy(shift, childrenNodes);;
//		}
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

	private Point drawTree(TreeNode currentFigure, Point upperRight, Point lowerLeft, ArrayList<Filter> filters, int axis){
		Point currentCorner = null;
		int X = 0;
		int Y = 1;
		int width = upperRight.x-lowerLeft.x;
		int height = lowerLeft.y-upperRight.y;
		//System.out.println(currentFigure.getName() + ": ["+lowerLeft.x+","+upperRight.y+"] ["+width+","+height+"]");
		
		// If it's a package, we need to drill down some more possibly
		if(currentFigure instanceof TreePackageNode){
			currentFigure.setSize(width,height);
			currentFigure.setLocation(new Point(lowerLeft.x,upperRight.y));
			if (currentFigure.getName() != "default") {
				upperRight.y += 15; // Buffer for package label
				upperRight.x -= 5;
				lowerLeft.x +=5;
				lowerLeft.y -=5;
			}
			
			// has children
			List<TreeNode> childrenFigures =  getChildrenForNode(currentFigure, filters);
			// The treemap algorithm:
			// p = upper right, q=lower left
        //	width := LL[axis] - UR[axis] -- compute location of next slice
	    //  for i := 1 to num_children do
	 	//	LL[axis] := UR[axis] + (Size(child[i])/Size(root))*width
		//  Treemap(child[i], UR, Q, 1 - axis, color) -- recur on each slice, flipping axes
		//	UR[axis] := LL[axis]; 
			int newWidth = 0;
			if (axis == X){
				newWidth = lowerLeft.x - upperRight.x;
				//newWidth -=2*(childrenFigures.size()-1);
			}
			if (axis == Y) {
				newWidth = lowerLeft.y - upperRight.y;
				//newWidth -=2*(childrenFigures.size()-1);
			}
			for(int i = 0; i < childrenFigures.size(); i++){
				TreeNode child = childrenFigures.get(i);
				if (axis == X) {
					lowerLeft.x = (int) (upperRight.x + ((double)child.getTreeNodeSize() / currentFigure.getTreeNodeSize())*newWidth);
					if (i < (childrenFigures.size()-1))
						lowerLeft.x +=2;
				}
				if (axis == Y) {
					lowerLeft.y = (int) (upperRight.y + ((double)child.getTreeNodeSize() / currentFigure.getTreeNodeSize())*newWidth);
					if (i < (childrenFigures.size()-1))
						lowerLeft.y -=2;
				}
				
				currentCorner = drawTree(childrenFigures.get(i), upperRight.getCopy(), lowerLeft.getCopy(), filters,1-axis);
				if (axis == X) {
					upperRight.x = lowerLeft.x;
					if (i < (childrenFigures.size()-1)){
						upperRight.x -= 2;
						}
				}
				if (axis == Y) {
					upperRight.y = lowerLeft.y;
					if (i < (childrenFigures.size()-1)){
						upperRight.y += 2;
						}
				}
			}
			return currentCorner;
		} else {
			// has no children
			//System.out.println(currentFigure.getName() + ": ["+lowerLeft.x+","+upperRight.y+"] ["+width+","+height+"]");
			currentFigure.setSize(width,height);
			currentFigure.setLocation(new Point(lowerLeft.x,upperRight.y));
		//	checkMaxTreeXY(currentFigure);
			return new Point(lowerLeft.x + currentFigure.getBounds().width,
					upperRight.y + currentFigure.getBounds().height);
		}
	}

	public int getMaxTreeX(){
		return width;
	}

	public int getMaxTreeY(){
		return height;
	}

	private void checkMaxTreeXY(TreeNode currentFigure){
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
//LDAMOD
//		ArrayList<Node> implementors = new ArrayList<Node>();
//		
//		for (int i = 0; i < nodes.size(); i++) {
//			DVTreeNode currentNode = nodes.get(i);
//			ArrayList<ClassRepresentation> implementedClasses = currentNode
//					.getClassRepresentation().getImplementedClass();
//
//			for (int j = 0; j < implementedClasses.size(); j++) {
//				// FIXME equals
//				if (interfaceClass.getUniqueID().
//						equals(implementedClasses.get(j).getUniqueID())) {
//					if (!filtersOut(currentNode.getClassRepresentation(),
//							filters)) {
//						implementors.add(currentNode);
//						((InternalNode) currentNode).addToHighlightNode();
//					}
//				}
//			}
//		}
//		
//		// if someone implemented this interface..
//		if(implementors.size() > 0){
//			// link them to this interface
//			linkClassAndInterface(implementors, interfaceClass);
//		}
		
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
		//LDAMOD
//		ArrayList<Node> interfaces = new ArrayList<Node>();
//		ArrayList<ClassRepresentation> implementedInterfaces = concreteClass.getImplementedClass();
//		
//		for(int i = 0; i < nodes.size(); i++){
//			DVTreeNode currentNode = nodes.get(i);
//			if(isImplementedInterface(
//					currentNode.getClassRepresentation().getUniqueID(),
//					implementedInterfaces)
//					&& !filtersOut(currentNode.getClassRepresentation(), filters)){
//				
//				interfaces.add(currentNode);
//				currentNode.addToHighlightNode();
//			}
//		}
//		linkClassAndInterface(interfaces, concreteClass);
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
			TreeNode from =
				(TreeNode)currentConnection.getSourceAnchor().getOwner();
			
			TreeNode to =
				(TreeNode)currentConnection.getTargetAnchor().getOwner();
			
		//	if(!filtersOut(from.getClassRepresentation(), filters) &&
			//		!filtersOut(to.getClassRepresentation(), filters)){
				filteredInterfaceConnections.add(currentConnection);
		//	}
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
//		// check which nodes have dependency for this class
//		for(int i = 0 ; i < nodes.size(); i++){
//			TreeNode currentNode = nodes.get(i);
//			if (currentNode instanceof TreeClassNode) {
//				ClassRepresentation currentClass = ((TreeClassNode)currentNode).getClassRepresentation();
//				if(!filtersOut(currentClass, filters)){
//					// check if the current node has a dependency on this class
//					if(currentClass.dependsOn(classInSelectedNode)){
//						// add to the list of classes that have a dependency on this class
//				//		((InternalNode) currentNode).addToHighlightNode();
//						dependencyConnections.add(linkSizedDependency(
//								currentNode, getFigureForClass(classInSelectedNode),
//								currentClass.getDependencyWeightFor(classInSelectedNode),
//								Layout.COLORED_AND_SIZED, Link.CLASS_LINK));
//					}
//				}
//			}
//		}
	}
	
	public void resetDependencyConnections(){
		dependencyConnections = new ArrayList<PolylineConnection>();
	}

	public ArrayList<PolylineConnection> getDependencyConnections(
			ArrayList<Filter> filters) {
		ArrayList<PolylineConnection> filteredConnections =
			new ArrayList<PolylineConnection>();
		
		for(int i = 0; i < dependencyConnections.size(); i++){
			TreeNode from =
				(TreeNode)dependencyConnections.get(i).getSourceAnchor().getOwner();
			TreeNode to =
				(TreeNode)dependencyConnections.get(i).getTargetAnchor().getOwner();
			
			//if(!filtersOut(from.getClassRepresentation(), filters)
			//		&& !filtersOut(to.getClassRepresentation(), filters)){
				filteredConnections.add(dependencyConnections.get(i));
			//}
		}
		
		return filteredConnections;
	}

}
