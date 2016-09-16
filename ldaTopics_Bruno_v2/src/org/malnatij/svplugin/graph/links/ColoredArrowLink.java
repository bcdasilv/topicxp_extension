package org.malnatij.svplugin.graph.links;

import org.eclipse.draw2d.PolygonDecoration;
import org.eclipse.swt.graphics.Color;
import org.malnatij.svplugin.graph.nodes.Node;

public class ColoredArrowLink extends Link {
	private String kind;
	
	public ColoredArrowLink(Node from, Node to,
			int weigth, String kind){	
		
		this.kind = kind;
		this.weigth = weigth;
		
		setSources(from, to);
		
		setToolTip(from, to, weigth);
		
		setForegroundColor(setArrowColorAndSize(weigth));
	}
	
	public void setWeigth(int weigth) {
		this.weigth = weigth;
		
		
		setToolTip(from, to, weigth);
		setForegroundColor(setArrowColorAndSize(weigth));
		
	
	}
	
	
	private void setArrow(int weigth){
		PolygonDecoration arrow = new PolygonDecoration();
		arrow.setLineWidth((int)Math.log(weigth));
		setTargetDecoration(arrow);
	}
	
	private int normalizeColorIndex(int index){
		if (index < 0){
			return 0;
		}
		
		return index;
	}
	 
	private Color setArrowColorAndSize(int weigth){
		int coefficient;
		int red = 255;
		
		if(kind.equals(Link.CLASS_LINK)){
			coefficient = 6;
		} else {
			coefficient = 2;
		}
		
		int commonBase = normalizeColorIndex(200 - weigth * coefficient);
		
		Color color = new Color(null, red, commonBase, commonBase);
		
		setArrow(weigth);
		setLineWidth((int)Math.log(weigth * 4));
		
		currentColor = color;
		return color;
	}

}
