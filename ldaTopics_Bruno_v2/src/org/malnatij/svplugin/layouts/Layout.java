package org.malnatij.svplugin.layouts;

import java.util.ArrayList;

import org.eclipse.draw2d.ChopboxAnchor;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.swt.graphics.Color;
import org.malnatij.svplugin.XRay;
import org.malnatij.svplugin.filters.Filter;
import org.malnatij.svplugin.graph.links.ColoredArrowLink;
import org.malnatij.svplugin.graph.links.SizedArrowLink;
import org.malnatij.svplugin.graph.nodes.Node;
import org.malnatij.svplugin.graph.nodes.SCVNode;
import org.malnatij.svplugin.model.ClassRepresentation;
import org.malnatij.svplugin.model.EntityRepresentation;
import org.malnatij.svplugin.util.Log;
import org.malnatij.svplugin.views.XRayWorkbenchView;

public abstract class Layout extends XRay {
	protected static final int H_SPACING_BETWEEN_NODES = 5;
	protected static final int V_SPACING_BETWEEN_NODES = 30;
	protected static final int DEPENDENCY_NODE_SPACING = 20*3; //LDAMOD:circle size
	public static final String COLORED_AND_SIZED = "colored";
	public static final String RED_AND_SIZED = "red";
	protected ArrayList<PolylineConnection> connections =
		new ArrayList<PolylineConnection>();
	protected XRayWorkbenchView theView = null;
	
	protected PolylineConnection setCenterConnectionBetween(Node from,
			Node to, Color color, int style){
	
		PolylineConnection c = new PolylineConnection();
		c.setForegroundColor(color);
		c.setLineStyle(style); // ie SWT.LINE_DASH
		setSources(from, to, c);
		
		return c;
	}
	
	public ArrayList<PolylineConnection> getRawConnections(){
		return connections;
	}
	
	protected PolylineConnection linkSizedDependency(
			Node from, Node to, int weigth, String linkColorKind,
			String linkKind){
		
		if(linkColorKind.equals(COLORED_AND_SIZED)){
			return new ColoredArrowLink(from, to, weigth, linkKind);
		} else if(linkColorKind.equals(RED_AND_SIZED)){
			return new SizedArrowLink(from, to, weigth);
		} else {
			Log.printError("No connection available for: " + linkColorKind);
			return null;
		}
	}
	
	private void setSources(Node from, Node to, PolylineConnection c){
		ChopboxAnchor sourceAnchor = new ChopboxAnchor(from);
		ChopboxAnchor targetAnchor = new ChopboxAnchor(to);
		
		c.setSourceAnchor(sourceAnchor);
		c.setTargetAnchor(targetAnchor);
	}

	/**
	 * @return true if the class MUST be filtered out
	 */
	protected boolean filtersOut(EntityRepresentation entityToFilter,
			ArrayList<Filter> filters){
		
		for(int i = 0; i < filters.size(); i++){
			//If even a single filter is positive, the class must be filtered out
			if(filters.get(i).matches(entityToFilter)){
				return true;
			}
		}
		
		return false;
		
	}
	
	protected ArrayList<ClassRepresentation> filterSCVNodeList(
			ArrayList<SCVNode> unFilteredList,
			ArrayList<Filter> filters){
		
		ArrayList<ClassRepresentation> unFilteredClassList = 
			new ArrayList<ClassRepresentation>();
		
		for(int i = 0; i < unFilteredList.size(); i++){
			unFilteredClassList.add(unFilteredList.get(i).getClassRepresentation());
		}
		
		return filterClassList(unFilteredClassList, filters);
		
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

}
