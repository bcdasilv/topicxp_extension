package org.malnatij.svplugin.graph.nodes;

import java.util.ArrayList;

import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.jdt.core.Flags;
import org.eclipse.swt.graphics.Color;
import org.malnatij.svplugin.filters.Filter;
import org.malnatij.svplugin.model.ClassRepresentation;
import org.malnatij.svplugin.util.Colors;
import org.malnatij.svplugin.views.XRayWorkbenchView;

/**
 * Node representing a CLASS in the dependency view layout
 * @author Jacopo Malnati
 *
 */
public class DVClassNode extends DVNode
implements IClassNode{
	private ClassRepresentation classInNode = null;

	public DVClassNode(ClassRepresentation currentClass,
			XRayWorkbenchView container) {
		super(container, new Dimension(CLASS_NODE_WIDTH, NODE_HEIGHT));
		classInNode = currentClass;
		setNodeLayout();
	    setBackgroundAndBorder();
	    setNodeLabel();
	    setNodeSize();
	    addToolTip();
	    addListeners();
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
	
	public boolean isLeaf(ArrayList<Filter> filters){
		return !classInNode.hasChildren(filters);
	}
	
	protected void addToolTip(){
		this.setToolTip(new Label(classInNode.toString()));
	}
	
	protected void setNodeLabel(){
		Label name = new Label(classInNode.getName());
	    add(name);
	}
	
	protected void setNodeSize(){
		setSize(CLASS_NODE_WIDTH, NODE_HEIGHT);
	}
	
	protected void addListeners(){
		this.addMouseListener(new MouseListener(){
			@SuppressWarnings("unused")
			public void mouseClicked(MouseEvent e) {}
			public void mouseDoubleClicked(MouseEvent arg0) {
				addToSelectedNodes(false);
				openAllSelectedNodes();
			}
			public void mousePressed(MouseEvent arg0) {
				addToSelectedNodes(true);
			}
			public void mouseReleased(MouseEvent arg0) {}
		});
	}
	
	public ClassRepresentation getClassRepresentation(){
		return classInNode;
	}
	
	protected void setBackgroundAndBorder(){
		border = new LineBorder(getDefaultBorderColor(), 1);
		setBorder(border);
		setBackgroundColor(getAppropriateNodeColor());
		setOpaque(true);
	}
	
	protected Color getAppropriateNodeColor() {
		Color theColor = null;
		int flag = classInNode.getFlag();
	
		if(Flags.isAbstract(flag)){
			theColor = Colors.ABSTRACT_COLOR;
		} else if (Flags.isInterface(flag)){
			theColor = Colors.INTERFACE_COLOR;
		} else {
			theColor = Colors.CONCRETE_COLOR;
		}
		return theColor;
	}

	public String getName() {
		return classInNode.getName();
	}

	@Override
	public boolean isRoot(ArrayList<Filter> filters) {
		return classInNode.isRoot(filters);
	}

}
