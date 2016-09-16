package org.malnatij.svplugin.graph.nodes;

import java.util.ArrayList;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.swt.graphics.Color;

import org.malnatij.svplugin.filters.Filter;
import org.malnatij.svplugin.model.EntityRepresentation;
import org.malnatij.svplugin.util.Colors;
import org.malnatij.svplugin.views.XRayWorkbenchView;

/**
 * This node is a general node, it might be internal or external.
 * It has borders and it models a class (internal or external).
 * @author Jacopo Malnati
 *
 */
public abstract class Node extends Figure {
	public static int NORMAL_BORDER_SIZE = 1;
	public static int SELECTED_BORDER_SIZE = 2;
	public static int SELECTED_COLORED_BORDER_SIZE = 4;
	
	public int x = -1;
	public int y = -1;
	protected Dimension defaultSize = null;
	private Color borderColor =  null;
	protected PolylineConnection incomingConnection;
	protected XRayWorkbenchView viewContainer;
	protected static final int SIZE_TRESHOLD = 10;
	protected LineBorder border;
	
	public Node(XRayWorkbenchView container){
		this.viewContainer = container;
		this.borderColor = getDefaultBorderColor();
	}
	
	public void setNodePosition(int x, int y){
		this.x = x;
		this.y = y;
	}
	
	public abstract Color getDefaultBorderColor();
	
	/**
	 * If it is a black node (not colored) select it with a RED COLOR
	 * otherwise just increase the size of the border
	 */
	public void setSelectedBorderColor() {
		if (borderColor.equals(getDefaultBorderColor())
				|| borderColor.equals(Colors.HIGHLIGHT_COLOR)) {
			border.setColor(Colors.SELECTED_COLOR);
			border.setWidth(SELECTED_BORDER_SIZE);
		} else {
			border.setWidth(SELECTED_COLORED_BORDER_SIZE);
		}
		this.repaint();
	}
	
	public PolylineConnection getIncomingConnection(){
		return incomingConnection;
	}
	
	public abstract boolean isLeaf(ArrayList<Filter> filters);
	
	public abstract boolean isRoot(ArrayList<Filter> filters);
	
	public abstract String getName();
	
	protected abstract Color getAppropriateNodeColor();
	
	public abstract void setHiddenNode();
	
	public abstract void setVisibleNode();

	public abstract ArrayList<EntityRepresentation> 
	getChildrenEntities(ArrayList<Filter> filters);
	
	/* Every node has a default dimension computed on different metrics.
	 * This dimension could be altered by zooming factors.
	 */
	protected void setDefaultSize(Dimension defaultSize){
		if(defaultSize.height < SIZE_TRESHOLD){
			defaultSize.height = SIZE_TRESHOLD;
		}
		if(defaultSize.width < SIZE_TRESHOLD){
			defaultSize.width = SIZE_TRESHOLD;
		}
		// initialize the default size and the zoomed size (not zoomed yet)
		this.defaultSize = defaultSize;

	}
	
	public void zoomSize(double currentCoefficient){
		try{
			int width = (int)Math.ceil(this.defaultSize.width * currentCoefficient);
			int height = (int)Math.ceil(this.defaultSize.height * currentCoefficient);
			setSize(width, height);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void setHighlightBorderColor(){
		if(borderColor.equals(getDefaultBorderColor())){
			border.setColor(Colors.HIGHLIGHT_COLOR);
			border.setWidth(SELECTED_BORDER_SIZE);
		} else {
			border.setWidth(SELECTED_COLORED_BORDER_SIZE);
		}
		
		this.repaint();
	}
	
	public void setOriginalSize(){
		setSize(defaultSize);
	}
	
	public void removeBorderTag(){
		this.borderColor = getDefaultBorderColor();
		border.setColor(borderColor);
		border.setWidth(NORMAL_BORDER_SIZE);
		this.repaint();
		if(viewContainer.isSelected(this)){
			setSelectedBorderColor();
		}
	}

	public void setColoredBorder(Color borderColor) {
		this.borderColor = borderColor;
		border.setColor(borderColor);
		border.setWidth(SELECTED_BORDER_SIZE);
		this.repaint();
		setSelectedBorderColor();
	}
	
	public boolean isSelected(){
		return viewContainer.isSelected(this);
	}
	
	public Color getBorderColor() {
		return borderColor;
	}
	
	public boolean isTagged(Color color){
		return color.equals(borderColor);
	}

	public void setNormalBorderColor(){
		border.setColor(borderColor);
		if(borderColor.equals(getDefaultBorderColor())){
			border.setWidth(NORMAL_BORDER_SIZE);
		} else {
			border.setWidth(SELECTED_BORDER_SIZE);
		}
		this.repaint();
	}
	
	public void setIncomingConnection(PolylineConnection connection){
		incomingConnection = connection;
	}

	public Color getColorPreviousToHighlight(){
		return border.getColor();
	}
	
	public int getWidthPreviousToHighlight(){
		return border.getWidth();
	}
	
	// after link mouse-over
	public void setColorAfterHighlight(Color old, int size){
		border.setColor(old);
		border.setWidth(size);
		repaint();
	}
	
	protected void addToSelectedNodes(boolean deselectDuplicate){
		viewContainer.addSelectedNode(this, deselectDuplicate, true);
	}
	
	protected void openAllSelectedNodes(){
		viewContainer.openAllNodes();
	}
}
