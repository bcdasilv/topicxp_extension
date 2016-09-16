package org.malnatij.svplugin.graph.links;

import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseMotionListener;
import org.eclipse.swt.graphics.Color;
import org.malnatij.svplugin.XRay;
import org.malnatij.svplugin.graph.nodes.Node;
import org.malnatij.svplugin.util.Colors;

public class MouseMotionTagger extends XRay
implements MouseMotionListener{
	private Link parent;
	private Color fromColor;
	private Color toColor;
	private int fromW;
	private int toW;
	
	public MouseMotionTagger(Link parent){
		this.parent = parent;
	}

	public void mouseDragged(MouseEvent me) {}

	public void mouseEntered(MouseEvent me) {
		parent.setForegroundColor(Colors.HIGHLIGHT_COLOR);
		Node from = parent.getFrom();
		Node to = parent.getTo();

		//System.out.println("Entering "+from.getName() + "-->" +to.getName());
		fromColor = from.getColorPreviousToHighlight();
		toColor = to.getColorPreviousToHighlight();
		fromW = from.getWidthPreviousToHighlight();
		toW = to.getWidthPreviousToHighlight();
		from.setHighlightBorderColor();
		to.setHighlightBorderColor();
	}

	public void mouseExited(MouseEvent me) {
		parent.setOriginalColor();
		// IT incorectly remembers which color, so just set it to the default
		parent.getFrom().setColorAfterHighlight(parent.getFrom().getDefaultBorderColor(), 1);
		parent.getTo().setColorAfterHighlight(parent.getFrom().getDefaultBorderColor(), 1);
	}

	public void mouseHover(MouseEvent me) {}

	public void mouseMoved(MouseEvent me) {}

}
