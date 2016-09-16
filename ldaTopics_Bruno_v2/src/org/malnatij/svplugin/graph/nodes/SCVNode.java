package org.malnatij.svplugin.graph.nodes;

import java.util.ArrayList;

import org.malnatij.svplugin.filters.Filter;
import org.malnatij.svplugin.model.ClassRepresentation;
import org.malnatij.svplugin.model.EntityRepresentation;
import org.malnatij.svplugin.views.XRayWorkbenchView;

/**
 * This class is an abstract description of a node containing a ClassRepresentation
 * @author Jacopo Malnati
 *
 */
public abstract class SCVNode extends Node
implements IClassNode{

	protected ClassRepresentation classInNode;
	
	public SCVNode(XRayWorkbenchView container, ClassRepresentation classToModel) {
		super(container);
		this.classInNode = classToModel;
	}

	public ClassRepresentation getClassRepresentation(){
		return classInNode;
	}
	
	public boolean isLeaf(ArrayList<Filter> filters){
		return !classInNode.hasChildren(filters);
	}

	public boolean isRoot(ArrayList<Filter> filters) {
		return classInNode.isRoot(filters);
	}
	
	public void setHiddenNode(){
		classInNode.setHiddenEntity(true);
		if(incomingConnection != null){
			incomingConnection.setVisible(false);
			incomingConnection.repaint();
		}
		setVisible(false);
		repaint();
	}
	
	public void setVisibleNode(){
		classInNode.setHiddenEntity(false);
		if(incomingConnection != null){
			incomingConnection.setVisible(true);
			incomingConnection.repaint();
		}
		setVisible(true);
		repaint();
	}
	
	public ArrayList<EntityRepresentation> getChildrenEntities(ArrayList<Filter> filters){
		ArrayList<EntityRepresentation> children  =
			new ArrayList<EntityRepresentation>();
		
		ArrayList<ClassRepresentation> childrenClasses  = classInNode.getChildren(filters);
		
		for(int i = 0; i < childrenClasses.size(); i++){
			children.add(childrenClasses.get(i));
		}
		
		return children;
	}
	
	public void addToHighlightNode(){
		viewContainer.addHighlightNode(this);
	}
}
