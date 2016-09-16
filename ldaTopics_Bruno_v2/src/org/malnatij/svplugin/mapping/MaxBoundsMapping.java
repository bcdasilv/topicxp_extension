package org.malnatij.svplugin.mapping;

import org.malnatij.svplugin.model.ProjectRepresentation;

public class MaxBoundsMapping extends ProjectDependentMapping{
	private double maxNodeWidth = -1;
	private double maxNodeHeight = -1;
	private double xScaling = -1;
	private double yScaling = -1;

	public MaxBoundsMapping(ProjectRepresentation theProject,
			int maxNodeWidth, int maxNodeHeight) {
		super(theProject);
		
		this.maxNodeWidth = maxNodeWidth;
		this.maxNodeHeight = maxNodeHeight;
		
		computeAndSetScaling();
	}
	
	// executed just once
	private void computeAndSetScaling(){
		int maxLOC = theProject.getMaxLOC();
		int maxNOM = theProject.getMaxNOM();

		xScaling = maxNodeWidth / maxNOM;
		yScaling = maxNodeHeight / maxLOC;
		
		//System.out.println("scalings are: " +xScaling + " " + yScaling);
	}

	public double getXScaling() {
		return xScaling;
	}

	public double getYScaling() {
		return yScaling;
	}

}
