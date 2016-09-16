package org.malnatij.svplugin.graph.nodes;

import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.jdt.core.Flags;
import org.eclipse.swt.graphics.Color;
import org.malnatij.svplugin.mapping.Mapping;
import org.malnatij.svplugin.model.ClassRepresentation;
import org.malnatij.svplugin.util.Colors;
import org.malnatij.svplugin.views.XRayWorkbenchView;

public class InternalNode extends SCVNode {
	Mapping currentScaling;
	
	public InternalNode(ClassRepresentation classToModel,
			Mapping currentScaling, XRayWorkbenchView container){
		super(container, classToModel);
		this.currentScaling = currentScaling;
		
		@SuppressWarnings("unused")
		int fields = classToModel.getNumberOfFields();
		int loc = classToModel.getLinesOfCode();
		int methods = classToModel.getNumberOfMethods();
		
		// compute and set the default dimension
		setDefaultSize(new Dimension(
				(int) (methods * currentScaling.getXScaling()),
				(int) (loc * currentScaling.getYScaling())));
		
		// give the dimension to the node itself
		this.setSize(defaultSize);
		
		ToolbarLayout layout = new ToolbarLayout();
	    setLayoutManager(layout);
	    border = new LineBorder(getDefaultBorderColor(), 1);
	    setBorder(border);
	    setBackgroundColor(getAppropriateNodeColor());
	    setOpaque(true);
		
		add(new Label(classToModel.getName()));
		setToolTip(new Label(getClassRepresentation().toString()));
		
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

	public Color getAppropriateNodeColor(){
		Color theColor = null;
		int flag = classInNode.getFlag();
		if(Flags.isAbstract(flag)){
			if(classInNode.isInnerClass()){
				theColor = Colors.GRAY;
			} else {
				theColor = Colors.ABSTRACT_COLOR;
			}
			
		} else if (Flags.isInterface(flag)){
			if(classInNode.isInnerClass()){
				theColor = Colors.GRAY_LIGHT;
			} else {
				theColor = Colors.INTERFACE_COLOR;
			}
			
		} else {
			if(classInNode.isInnerClass()){
				theColor = Colors.GRAY_DARK;
			} else {
				theColor = Colors.CONCRETE_COLOR;
			}
		}
		return theColor;
	}
	
	public String toString(){
		return "InternalNode: " 
		+ getClassRepresentation().getName() 
		+ " x:" + x 
		+ " y:" + y;
	}

	public String getName() {
		return classInNode.getName();
	}

	@Override
	public Color getDefaultBorderColor() {
		return Colors.BLACK;
	}

}
