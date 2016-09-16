package org.malnatij.svplugin.graph.links;

import org.eclipse.draw2d.ChopboxAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;

/**
 * This is an abstract anchor, a point in which a 
 * link is connected to a IFigure
 * @author Jacopo Malnati
 *
 */
public abstract class AbstractAnchor 
extends ChopboxAnchor{
	
	public AbstractAnchor(IFigure figure){
		super(figure);
	}
	
	/**
	 * Returns the point in which a link is connected
	 * to an IFigure
	 */
	public abstract Point getLocation(Point Reference);

}
