package org.malnatij.svplugin.views.handlers;

import java.util.ArrayList;

import org.eclipse.draw2d.PolylineConnection;
import org.malnatij.svplugin.graph.links.Link;
import org.malnatij.svplugin.layouts.CircleLayout;

public abstract class DependencyHandler 
extends PolymetricViewHandler {
	protected CircleLayout circleLayout = null;
	
	protected void resetAllNodesAndLinks(){
		deSelectNodes();
		deHilightNodes();
	}

	@Override
	public void showJustDependencyInRange(int[] range) {
		ArrayList<PolylineConnection> links =
			circleLayout.getDependencyConnections(filters);		
		//System.out.println("range "+range[0]+"--"+range[1]);
		for(int i = 0; i < links.size(); i++){
			Link currentLink = (Link) links.get(i);
			if(linkInRange(currentLink, range)){
				setVisibleLink(currentLink);	
			} else {
				//System.out.println("hiding 	Dependency: " + currentLink.getFrom().getName() 
		//	+ " --(" + currentLink.getWeigth() + ")--> " + currentLink.getTo().getName());
				setNotVisibleLink(currentLink);
			}
			currentLink.repaint();
		}
	}
	
	private boolean linkInRange(Link currentLink, int[] range){
		return currentLink.getWeigth() >= range[0] &&
		currentLink.getWeigth() <= range[1];
	}
	
	private void setVisibleLink(Link currentLink){
		currentLink.setVisible(true);
	}
	
	private void setNotVisibleLink(Link currentLink){
		currentLink.setVisible(false);
	}
	
	@Override
	public int getMaxDependencyWeigth() {
		int max = 0;
		ArrayList<PolylineConnection> links =
			circleLayout.getRawConnections();
		for(int i = 0; i < links.size(); i++){
			Link currentLink = (Link) links.get(i);
			if(currentLink.getWeigth() > max) 
				max = currentLink.getWeigth();
		}
		return max;
	}
	
}
