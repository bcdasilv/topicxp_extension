package org.malnatij.svplugin.graph.nodes;

import java.util.ArrayList;

import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.swt.graphics.Color;
import org.malnatij.svplugin.filters.Filter;
import org.malnatij.svplugin.model.EntityRepresentation;
import org.malnatij.svplugin.util.Colors;
import org.malnatij.svplugin.views.XRayWorkbenchView;

/**
 * Dependency View nodes have fixed sizes and are used in the dependency view
 */
public abstract class DVNode extends Node{
	public static final int NODE_HEIGHT = (int) 70; //LDAMOD: topic box size. //TODO: move this to our package
	public static final int CLASS_NODE_WIDTH = 40;
	public static final int PACKAGE_NODE_WIDTH = 80+90;
	
	public DVNode(XRayWorkbenchView container, Dimension defaultSize){
		super(container);
		this.defaultSize = defaultSize;
	}
	
	protected void setNodeLayout(){
		ToolbarLayout layout = new ToolbarLayout();
	    setLayoutManager(layout);
	}
	
	public ArrayList<EntityRepresentation> getChildrenEntities(ArrayList<Filter> filters){
		ArrayList<EntityRepresentation> children  =
			new ArrayList<EntityRepresentation>();
		// in a circular layout deleting a superclass should not delete its children
		return children;
	}
	
	@Override
	public Color getDefaultBorderColor() {
		return Colors.BLACK;
	}
	
	protected abstract Color getAppropriateNodeColor();
	
	protected abstract void setBackgroundAndBorder();
	
	protected abstract void setNodeSize();
	
	protected abstract void addToolTip();
	
	protected abstract void addListeners();
	
	protected abstract void setNodeLabel();

}
