package org.malnatij.svplugin.graph.links;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;

public class DownAnchor
extends AbstractAnchor{
	
	public DownAnchor(IFigure figure){
		super(figure);
	}
	
	
	public Point getLocation(Point Reference){
		Rectangle owner = getBox();
		
		int x = owner.x + (owner.width / 2);
		int y = owner.y + owner.height;
		
		return new Point(x, y);
	}

}
