package org.malnatij.svplugin.graph.links;

import org.eclipse.draw2d.PolygonDecoration;
import org.malnatij.svplugin.graph.nodes.Node;
import org.malnatij.svplugin.util.Colors;

public class SizedArrowLink extends Link {
	
	public SizedArrowLink(Node from, Node to, int weigth){		
		setSources(from, to);
		
		setLineWidth(weigth);
		
		setArrow(weigth);
		
		setToolTip(from, to, weigth);
		currentColor = Colors.SELECTED_COLOR;
		setForegroundColor(currentColor);
	}
	
	private void setArrow(int weigth){
		PolygonDecoration arrow = new PolygonDecoration();
		arrow.setLineWidth(weigth - 1);
		setTargetDecoration(arrow);
	}

}
