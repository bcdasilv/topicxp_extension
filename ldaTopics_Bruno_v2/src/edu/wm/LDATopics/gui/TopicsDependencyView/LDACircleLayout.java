package edu.wm.LDATopics.gui.TopicsDependencyView;

import org.malnatij.svplugin.layouts.CircleLayout;
import org.malnatij.svplugin.layouts.Layout;

	import java.util.ArrayList;
	import java.util.HashMap;
	import java.util.Iterator;
	import java.util.Set;

	import org.eclipse.draw2d.Figure;
	import org.eclipse.draw2d.PolylineConnection;
	import org.eclipse.draw2d.geometry.Point;
	import org.eclipse.draw2d.geometry.Rectangle;
	import org.malnatij.svplugin.filters.Filter;
import org.malnatij.svplugin.graph.links.ColoredArrowLink;
	import org.malnatij.svplugin.graph.links.Link;
import org.malnatij.svplugin.graph.links.SizedArrowLink;
	import org.malnatij.svplugin.graph.nodes.DVClassNode;
	import org.malnatij.svplugin.graph.nodes.DVNode;
	import org.malnatij.svplugin.graph.nodes.DVPackageNode;
	import org.malnatij.svplugin.model.ClassRepresentation;
	import org.malnatij.svplugin.model.PackageRepresentation;
	import org.malnatij.svplugin.model.ProjectRepresentation;
	import org.malnatij.svplugin.model.core.ModelNotPreviouslyScheduled;
	import org.malnatij.svplugin.util.Log;
	import org.malnatij.svplugin.views.ViewFacade;
	import org.malnatij.svplugin.views.XRayWorkbenchView;

import edu.wm.LDATopics.LDATopics;
	import edu.wm.LDATopics.LDA.Topic;
import edu.wm.LDATopics.LDA.TopicMember;
import edu.wm.LDATopics.gui.LDAViewFacade;
import edu.wm.LDATopics.gui.TopicsDependencyView.DVTopicNode;

	/**
	 * This layout is responsible for positioning DVNodes in circle
	 * @author Jacopo Malnati
	 *
	 */

	public class LDACircleLayout extends CircleLayout {
		private Figure container  = null;
		private ArrayList<DVClassNode> classNodes = null;
		private ArrayList<DVPackageNode> packageNodes = null;
		private ArrayList<DVTopicNode> topicNodes = null;

		public void layoutClass(ArrayList<DVClassNode> nodesDVClass,
				Figure container,
				XRayWorkbenchView theView,
				boolean init,
				ArrayList<Filter> filters) {
			
			this.theView = theView;
			this.container = container;
			this.classNodes = nodesDVClass;
			
			int radius = computeRadius(ViewFacade.CLASS_DEPENDENCY,
					filters);
			
			double PI = 0; // initial PI
			
			int goodNodes = getGoodClassNodes(filters);
			
			for(int i = 0; i < classNodes.size(); i++){
				DVClassNode currentNode = classNodes.get(i);

				if(!filtersOut(currentNode.getClassRepresentation(), filters)){
					currentNode.setVisibleNode();
					int x = (int) (radius * Math.cos(PI));
					int y = (int) (radius * Math.sin(PI));
					x += radius + DEPENDENCY_NODE_SPACING;
					y += radius + DEPENDENCY_NODE_SPACING;
					PI += 6.28 / goodNodes;
					Point location = new Point(x, y);			

					currentNode.setLocation(location);
					currentNode.setNodePosition(x, y);
				} else {
					currentNode.setVisible(false);
				}
			}
			
			if(init){
				createClassDependencyLinks(filters);
				addDependencyLinks();
			} else {
				updateDependencyClassLinks(filters);
			}
			
		}
		
		private int getGoodClassNodes(ArrayList<Filter> filters){
			int goodNodes = 0;
			for(int i = 0; i < classNodes.size(); i++){
				if(!filtersOut(classNodes.get(i).getClassRepresentation(),
						filters)){
					goodNodes++;
				}
			}
			return goodNodes;
		}
		
		private int getGoodPackagesNodes(ArrayList<Filter> filters){
			int goodNodes = 0;
			for(int i = 0; i < packageNodes.size(); i++){
				if(!filtersOut(packageNodes.get(i).getPackageInNode(),
						filters)){
					goodNodes++;
				}
			}
			return goodNodes;
		}
		
		
		private void updateDependencyClassLinks(ArrayList<Filter> filters){
			for(int i = 0; i < connections.size(); i++){
				PolylineConnection connection = connections.get(i);
				
				if(!filtersOut(((DVClassNode)connection.getSourceAnchor()
						.getOwner()).getClassRepresentation(),
						filters) &&
						!filtersOut(((DVClassNode)connection.getTargetAnchor()
								.getOwner()).getClassRepresentation(),
								filters)){
					connection.setVisible(true);
				} else {
					connection.setVisible(false);
				}
			}
			
		}
		
		private void updateDependencypackageLinks(ArrayList<Filter> filters){
			for(int i = 0; i < connections.size(); i++){
				PolylineConnection connection = connections.get(i);
				
				// This is unnecessary since we don't do filtering of packages, right? 
//				if(!filtersOut(((DVPackageNode)connection.getSourceAnchor()
//						.getOwner()).getPackageInNode(),
//						filters) &&
//						!filtersOut(((DVPackageNode)connection.getTargetAnchor()
//								.getOwner()).getPackageInNode(),
//								filters)){
//					connection.setVisible(true);
//				} else {
//					connection.setVisible(false);
//				}
			}
			
		}
		
		
		public void layoutPackages(ArrayList<DVPackageNode> nodesDVPackage,
				Figure figuresWrapperPackageDV,
				XRayWorkbenchView theView,
				boolean init,
				ArrayList<Filter> filters) {
			
				this.theView = theView;
				this.container = figuresWrapperPackageDV;
				this.packageNodes = nodesDVPackage;
				int radius = computeRadius(ViewFacade.PACKAGE_DEPENDENCY,
						filters);
				double PI = 0; // initial PI
				
				int goodNodes = getGoodPackagesNodes(filters);
				
				int size = packageNodes.size();
				for(int i = 0; i < size; i++){
					DVPackageNode currentNode = packageNodes.get(i);
					if(!filtersOut(currentNode.getPackageInNode(), filters)){
						currentNode.setVisibleNode();
						int x = (int) (radius * Math.cos(PI));
						int y = (int) (radius * Math.sin(PI));
						x += radius + DEPENDENCY_NODE_SPACING;
						y += radius + DEPENDENCY_NODE_SPACING;
						PI += 6.28 / goodNodes;
						Point location = new Point(x, y);
						currentNode.setNodePosition(x, y);
						currentNode.setLocation(location);
					} else {
						currentNode.setVisible(false);
					}
				}
				
				if(init){
					createPackageDependencyLinks(filters);
					addDependencyLinks();
				} else {
					updateDependencypackageLinks(filters);
				}
		}
		
		public void layoutTopics(ArrayList<DVTopicNode> nodesDVPackage,
				Figure figuresWrapperPackageDV,
				XRayWorkbenchView theView,
				boolean init,
				ArrayList<Filter> filters) {
			
				this.theView = theView;
				this.container = figuresWrapperPackageDV;
				this.topicNodes = nodesDVPackage;
				int radius = computeRadius(LDAViewFacade.TOPIC_DEPENDENCY,
						filters);
				double PI = 0; // initial PI
				
				int goodNodes = getGoodTopicNodes(filters);
				
				int size = topicNodes.size();
				for(int i = 0; i < size; i++){
					DVTopicNode currentNode = topicNodes.get(i);
					//LDAMOD: filter fix if necessary
					if(true) {//(!filtersOut(currentNode.getTopicInNode(), filters)){
						currentNode.setVisibleNode();
						int x = (int) (radius * Math.cos(PI));
						int y = (int) (radius * Math.sin(PI));
						x += radius + DEPENDENCY_NODE_SPACING;
						y += radius + DEPENDENCY_NODE_SPACING;
						PI += 6.28 / goodNodes;
						Point location = new Point(x, y);
						currentNode.setNodePosition(x, y);
						currentNode.setLocation(location);
					} else {
						currentNode.setVisible(false);
					}
				}
				
				if(init){
					createTopicDependencyLinks(filters);
					addDependencyLinks();
				} else {
					updateDependencypackageLinks(filters);
				}
		}
		private int getGoodTopicNodes(ArrayList<Filter> filters){
//			int goodNodes = 0;
//			for(int i = 0; i < topicNodes.size(); i++){
//				if (topicNo)
//				if(!filtersOut(topicNodes.get(i).getTopicInNode(),
//						filters)){
//					goodNodes++;
//				}
//			}
			return topicNodes.size();
		}
		
		private boolean filterTopicPackages(String[] packages, PackageRepresentation currentPackage) {
			String name = currentPackage.getName();
			
			for (String item : packages) {
				if (item.equals(name)) return true;
			}
			
			return false;
		}
		
		private boolean filterTopicClasses(TopicMember[] classes,
				ClassRepresentation currentClass) {
			String name = currentClass.getName();
			
			for (TopicMember item : classes) { //TODO: +.java is just a hack,  do something proper
				if (item.name.equals(name+".java")) return true;
			}
			
			return false;
		}
		
		private void createTopicDependencyLinks(ArrayList<Filter> filters){
			ProjectRepresentation proj;
			try {
				proj = theView.getModeledProject();
			
				for(int i = 0; i < topicNodes.size(); i++){
					DVTopicNode currentNode = topicNodes.get(i);
					Topic topicInNode = currentNode.getTopicInNode();
					String[] packages = topicInNode.getDocumentNames();
					
					// just get the packages in the topics, get the links for those packages, and then 
					// discard the ones to other packages in the same topic

					// TODO, 2010: Why do we do the dependencies two different ways?! Are the weights comparable at all? should we just use 1 method?
					
					//if(!filtersOut( currentNode, filters)){
					
//					if (packages.length > 3) { //TODO: unhardcode the 3 comparison
//						for (String packageName : packages) {
//							HashMap<PackageRepresentation, Integer> dependencyMap =
//								proj.getPackage(packageName).getUsedPackages();
//							
//							Set<PackageRepresentation> keySet = dependencyMap.keySet();
//							Iterator<PackageRepresentation> packageIterator = keySet.iterator();
//							
//							while(packageIterator.hasNext()){
//								PackageRepresentation currentPackage = packageIterator.next();
//								
//							//	if(!filtersOut(currentPackage, filters)){
//								if (!filterTopicPackages(packages, currentPackage)) {
//									DVTopicNode targetNode = getNodeForPackageTopic(currentPackage);
//									if (targetNode != null) {
//										// Get the weight of the connection-to-be
//										int weigth = dependencyMap.get(currentPackage);
//									    
//										// First, check for a extant connection to add our weight to
//										PolylineConnection connection = getConnection(currentNode, targetNode);
//										
//										if (connection != null) { 
//											// If the connection exists, just add to it's weight
//											if (connection instanceof Link) {
//												Link link = (Link) connection;
//												link.setWeigth(link.getWeigth() + weigth);
//											}
//										} else {
//											// Otherwise, create a new connection
//											connection = linkSizedDependency(
//													currentNode, targetNode,
//													weigth, Layout.COLORED_AND_SIZED, Link.PACKAGE_LINK);
//											connections.add(connection);
//											
//											//System.out.println("Making package-based link between "+ currentNode.getName() + " and " + targetNode.getName());
//											
//											targetNode.setIncomingConnection(connection);
//											}
//									}
//								}
//							//	}
//							}
//						}
//					}
//					else  { // Do dependency links by classes instead
						TopicMember[] classes = LDATopics.getMap().getClassMap().topics[topicInNode.getNumber()-1].getDocuments();
						for (TopicMember classMember : classes) {
//							System.out.println("Class "+classMember.name+" links to:");
							String packageName = classMember.document.getPackage();
							String className = classMember.name.split(".java")[0];
							HashMap<ClassRepresentation, Integer> dependencyMap =
								proj.getClassRepresentationFor(className, classMember.document.getPackage()).getUsedClasses();
							
							Set<ClassRepresentation> keySet = dependencyMap.keySet();
							Iterator<ClassRepresentation> packageIterator = keySet.iterator();
							
							while(packageIterator.hasNext()){
								ClassRepresentation currentClass = packageIterator.next();
	//							System.out.println("     "+currentClass.getName());
								
							//	if(!filtersOut(currentPackage, filters)){
								if (!filterTopicClasses(classes, currentClass)) {
									DVTopicNode targetNode = getNodeForClassTopic(currentClass);
									if (targetNode != null) {
										// Get the weight of the connection-to-be
										int weigth = dependencyMap.get(currentClass);
										
										  
										// First, check for a extant connection to add our weight to
										PolylineConnection connection = getConnection(currentNode, targetNode);
										
										if (connection != null) { 
											// If the connection exists, just add to it's weight
											if (connection instanceof Link) {
												Link link = (Link) connection;
												link.setWeigth(link.getWeigth() + weigth);
											}
										} else {
											// Otherwise, create a new connection
											connection = linkSizedDependency(
													currentNode, targetNode,
													weigth, Layout.COLORED_AND_SIZED, Link.PACKAGE_LINK);
											connections.add(connection);
											
											//System.out.println("Making class-based link between "+ currentNode.getName() + " and " + targetNode.getName());
											
										
											targetNode.setIncomingConnection(connection);
										}
										
									}
								}
							//	}
							}
						}
					}
					//}
//				}
			} catch (ModelNotPreviouslyScheduled e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		public DVTopicNode getNodeForPackageTopic(PackageRepresentation packageInNode){
			for(int i = 0; i < topicNodes.size(); i++){
				DVTopicNode currentNode = topicNodes.get(i);
				if(currentNode.getTopicInNode().getDocumentByName(packageInNode.getName()) != null){
					return currentNode;
				}
			}
			return null;
		}
		
		public DVTopicNode getNodeForClassTopic(ClassRepresentation currentClass){
			for(int i = 0; i < topicNodes.size(); i++){
				DVTopicNode currentNode = topicNodes.get(i);
				// TODO: Check by more than name, check package too. could be class with same name in 2 packages
				if(LDATopics.getMap().getClassMap().topics[currentNode.getTopicInNode().getNumber()-1].getDocumentByName(currentClass.getName()+".java") != null){
					return currentNode;
				}
			}
			Log.printError("CircleLayout.getNodeForClass() " +
					"- No node associated with:" + currentClass.getName());
			return null;
		}
		
		public DVPackageNode getNodeForPackage(PackageRepresentation packageInNode){
			for(int i = 0; i < packageNodes.size(); i++){
				DVPackageNode currentNode = packageNodes.get(i);
				if(currentNode.getPackageInNode().equals(packageInNode)){
					return currentNode;
				}
			}
			return null;
		}
		
		private void createClassDependencyLinks(ArrayList<Filter> filters){
			for(int i = 0; i < classNodes.size(); i++){
				DVClassNode currentNode = classNodes.get(i);
				
				if(!filtersOut(currentNode.getClassRepresentation(), filters)){
					ClassRepresentation classInNode = currentNode.getClassRepresentation();
					HashMap<ClassRepresentation, Integer> dependencyMap =
						classInNode.getUsedClasses();
					
					Set<ClassRepresentation> keySet = dependencyMap.keySet();
					Iterator<ClassRepresentation> classIterator = keySet.iterator();
					
					while(classIterator.hasNext()){
						ClassRepresentation currentClass = classIterator.next();
						
						if(!filtersOut(currentClass, filters)){
							int weigth = dependencyMap.get(currentClass);
							PolylineConnection connection = linkSizedDependency(
									currentNode, getNodeForClass(currentClass), weigth,
									Layout.COLORED_AND_SIZED, Link.CLASS_LINK);
							connections.add(connection);
							
							getNodeForClass(currentClass).setIncomingConnection(connection);
						}
					}
				}
			}
		}
		
		private void createPackageDependencyLinks(ArrayList<Filter> filters){
			for(int i = 0; i < packageNodes.size(); i++){
				DVPackageNode currentNode = packageNodes.get(i);
				PackageRepresentation packageInNode = currentNode.getPackageInNode();
				
				if(!filtersOut(packageInNode, filters)){
					HashMap<PackageRepresentation, Integer> dependencyMap =
						packageInNode.getUsedPackages();
					
					Set<PackageRepresentation> keySet = dependencyMap.keySet();
					Iterator<PackageRepresentation> packageIterator = keySet.iterator();
					
					while(packageIterator.hasNext()){
						PackageRepresentation currentPackage = packageIterator.next();
						
						if(!filtersOut(currentPackage, filters)){
							int weigth = dependencyMap.get(currentPackage);
							PolylineConnection connection = linkSizedDependency(
									currentNode, getNodeForPackage(currentPackage),
									weigth, Layout.COLORED_AND_SIZED, Link.PACKAGE_LINK);
							connections.add(connection);
							
							getNodeForPackage(currentPackage).setIncomingConnection(connection);
						}
					}
				}
			}
		}
		
		private PolylineConnection getConnection(DVNode currentNode,
				DVNode to) {
			for (PolylineConnection con: connections)
				if (con instanceof Link) {
					Link link = (Link) con;
					if (link.getFrom().equals(currentNode) &&
							link.getTo().equals(to))
						return link;
				}
			return null;
		}

		private void addDependencyLinks(){
			for(int i = 0; i < connections.size(); i++){
				container.add(connections.get(i));
			}
		}
		
		public Rectangle getBounds(String dependencyKind, ArrayList<Filter> filters){
			int radius = computeRadius(dependencyKind, filters);
			double zoomingCoefficient = theView.getCurrentZoomingCoefficient();
			int xSpacing = (int)(DVNode.CLASS_NODE_WIDTH * zoomingCoefficient);
			
			if(dependencyKind.equals(ViewFacade.PACKAGE_DEPENDENCY)){
				xSpacing = (int) (DVNode.PACKAGE_NODE_WIDTH * zoomingCoefficient);
			}
			
			Rectangle bounds = new Rectangle(0, 0,
					2 * radius +
					xSpacing +
					2 * DEPENDENCY_NODE_SPACING,
					
					2 * radius +
					(int)(DVNode.NODE_HEIGHT * zoomingCoefficient)
					+ 2 * DEPENDENCY_NODE_SPACING);
			
			return bounds;
		}
		
		private int computeCircumference(String dependencyKind,
				ArrayList<Filter> filters){
			int circumference = 0;
			double zoomingCoefficient = theView.getCurrentZoomingCoefficient();
			
			try{
				int goodNodes = 0;
				
				if(dependencyKind.equals(ViewFacade.CLASS_DEPENDENCY)){
					// class dependency
					goodNodes = getGoodClassNodes(filters);
					circumference = (int) Math.ceil(goodNodes 
							* (DVNode.CLASS_NODE_WIDTH + 2 * DEPENDENCY_NODE_SPACING) 
							* zoomingCoefficient);
					
				} else if (dependencyKind.equals(LDAViewFacade.TOPIC_DEPENDENCY)) {
					// topic dependency
					goodNodes = getGoodTopicNodes(filters);
					circumference = (int) Math.ceil ((goodNodes
							* (Math.sqrt((DVNode.PACKAGE_NODE_WIDTH * DVNode.PACKAGE_NODE_WIDTH)
									+ (DVNode.NODE_HEIGHT * DVNode.NODE_HEIGHT)))
									+ DEPENDENCY_NODE_SPACING)
							* zoomingCoefficient);
				} else {
					// package dependency
					goodNodes = getGoodPackagesNodes(filters);
					circumference = (int) Math.ceil ((goodNodes
							* (Math.sqrt((DVNode.PACKAGE_NODE_WIDTH * DVNode.PACKAGE_NODE_WIDTH)
									+ (DVNode.NODE_HEIGHT * DVNode.NODE_HEIGHT)))
									+ DEPENDENCY_NODE_SPACING)
							* zoomingCoefficient);
				}
			}catch(Exception e){
				e.printStackTrace();
			}
			return circumference;
			
		}
		
		private int computeRadius(String dependencyKind,
				ArrayList<Filter> filters){
			return (int) (computeCircumference(dependencyKind, filters) / 6.28);
		}
		
		public ArrayList<PolylineConnection> getDependencyConnections(
				ArrayList<Filter> filters) {
			ArrayList<PolylineConnection> filteredConnections =
				new ArrayList<PolylineConnection>();
			
			for(int i = 0; i < connections.size(); i++){
				if (theView.getCurrentVisualizationDescription().equals(LDAViewFacade.TOPIC_DEPENDENCY)) {
					//topic dep view				
					DVTopicNode from =
						(DVTopicNode)connections.get(i).getSourceAnchor().getOwner();
					DVTopicNode to =
						(DVTopicNode)connections.get(i).getTargetAnchor().getOwner();
					//LDAMOD: no filter for topics
					//if(!filtersOut(from.getTopicInNode(), filters)
					//		&& !filtersOut(to.getPackageInNode(), filters)){
						filteredConnections.add(connections.get(i));
					//}
				}else if(theView.getCurrentVisualizationDescription()
						.equals(ViewFacade.CLASS_DEPENDENCY)){
					
					DVClassNode from =
						(DVClassNode)connections.get(i).getSourceAnchor().getOwner();
					DVClassNode to =
						(DVClassNode)connections.get(i).getTargetAnchor().getOwner();
					if(!filtersOut(from.getClassRepresentation(), filters)
							&& !filtersOut(to.getClassRepresentation(), filters)){
						filteredConnections.add(connections.get(i));
					}
				}else{
					
					DVPackageNode from =
						(DVPackageNode)connections.get(i).getSourceAnchor().getOwner();
					DVPackageNode to =
						(DVPackageNode)connections.get(i).getTargetAnchor().getOwner();
					if(!filtersOut(from.getPackageInNode(), filters)
							&& !filtersOut(to.getPackageInNode(), filters)){
						filteredConnections.add(connections.get(i));
					}
				}
			}
			
			return filteredConnections;
		}

	}
