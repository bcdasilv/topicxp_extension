package org.malnatij.svplugin.graph.links;

import org.eclipse.draw2d.ChopboxAnchor;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.swt.graphics.Color;
import org.malnatij.svplugin.graph.nodes.Node;

public abstract class Link
extends PolylineConnection{
	
	public static final String PACKAGE_LINK = "package link";
	public static final String CLASS_LINK = "class link";
	protected Color currentColor;
	protected Node from;
	protected Node to;
	protected int weigth;
	
	protected void setToolTip(Node from, Node to, int weigth){
		String tip =
			"Dependency: " + from.getName() 
			+ " --(" + weigth + ")--> " + to.getName();
		
		setToolTip(new Label(tip));

		addMouseMotionListener(new MouseMotionTagger(this));
	}
	
	protected void setSources(Node from, Node to){
		this.from = from;
		this.to = to;
		
		ChopboxAnchor sourceAnchor = new ChopboxAnchor(from);
		ChopboxAnchor targetAnchor = new ChopboxAnchor(to);
		
		setSourceAnchor(sourceAnchor);
		setTargetAnchor(targetAnchor);
	}
	
	public void setOriginalColor(){
		setForegroundColor(currentColor);
	}

	public Node getFrom() {
		return from;
	}

	public Node getTo() {
		return to;
	}
	
	public int getWeigth(){
		return weigth;
	}
	
	public void setWeigth(int weigth) {
	    this.weigth = weigth;
	}

	public String toString() {
		return	"Dependency: " + from.getName() 
			+ " --(" + weigth + ")--> " + to.getName();
	}
}
