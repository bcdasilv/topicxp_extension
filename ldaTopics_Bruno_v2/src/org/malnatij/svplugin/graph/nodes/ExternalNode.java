package org.malnatij.svplugin.graph.nodes;

import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.swt.graphics.Color;
import org.malnatij.svplugin.model.ClassRepresentation;
import org.malnatij.svplugin.util.Colors;
import org.malnatij.svplugin.views.XRayWorkbenchView;

public class ExternalNode extends SCVNode {
	
	public ExternalNode(ClassRepresentation classToModel,
			XRayWorkbenchView container){
		super(container, classToModel);
		setDefaultSize(new Dimension(SIZE_TRESHOLD, SIZE_TRESHOLD));
		
		ToolbarLayout layout = new ToolbarLayout();
	    setLayoutManager(layout);
	    
	    border = new LineBorder(getAppropriateBorderColor(), 1);
	    setBorder(border);
	    setBackgroundColor(getAppropriateNodeColor());
	    setOpaque(true);
	    
	    setToolTip(new Label(getClassRepresentation().toString()));
	    
	    setSize(defaultSize);
	    
	    this.addMouseListener(new MouseListener(){
			@SuppressWarnings("unused")
			public void mouseClicked(MouseEvent e) {}
			public void mouseDoubleClicked(MouseEvent arg0) {}
			public void mousePressed(MouseEvent arg0) {
				addToSelectedNodes(true);
			}
			public void mouseReleased(MouseEvent arg0) {}
		});
	}
	
	public Color getAppropriateBorderColor(){
		return Colors.GRAY;
	}

	public Color getAppropriateNodeColor() {
		if(classInNode.isInterface()){
			return Colors.INTERFACE_COLOR;
		}
		return Colors.EXTERNAL_COLOR;
	}

	public String getName() {
		return classInNode.getName();
	}

	@Override
	public Color getDefaultBorderColor() {
		return Colors.GRAY;
	}

}
