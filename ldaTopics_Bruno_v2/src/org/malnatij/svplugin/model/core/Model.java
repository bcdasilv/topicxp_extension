package org.malnatij.svplugin.model.core;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.malnatij.svplugin.model.ProjectRepresentation;

import edu.wm.LDATopics.LDA.LDATopicUpdateRule;

/**
 * The current Model modeling the currently selected project
 * @author Jacopo Malnati
 *
 */
public class Model extends Job {
	private ModelExtractor modelExtractor;
	private IProject wantedProject;
	
	/**
	 * Constructor
	 * @param wantedProject the wanted project to analyze
	 * @param message the message to show to the user
	 */
	public Model(IProject wantedProject, String message){
		//super("[X-Ray core] " + message);
		super("Creating dependency model with X-Ray.");
		// Make sure this doesn't run before our LDA code is done!
		this.setRule(new LDATopicUpdateRule());
		
		this.wantedProject = wantedProject;
		setUpJob();
	}
	
	/**
	 * The job will be long, and provide feedbacks to the user
	 */
	public void setUpJob(){
		setUser(false);
		setPriority(LONG);
	}
	
	@Override
	/**
	 * The behavior of the Job, triggered by schedule().
	 * schedule() must be explicitly called after creating the model
	 */
	protected IStatus run(IProgressMonitor monitor) {
		int classesInProject = TicksPredictor.getNrOfClasses(wantedProject);
		monitor.beginTask("", 3 * classesInProject);
		extractModel(monitor);
		return Status.OK_STATUS;
	}
	
	/**
	 * Extract the model from the sources (starts the pipe and
	 * filter architecture that will extract the model, link the
	 * hierarchies and catch the dependencies) 
	 * @param monitor 
	 * @param classesInProject 
	 */
	private void extractModel(IProgressMonitor monitor){
		try{
			modelExtractor = new ModelExtractor(wantedProject, monitor);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * @return the project representation, modeled by the model
	 * @throws ModelNotPreviouslyScheduled if the model was not previously scheduled
	 */
	public ProjectRepresentation getModeledProject()
	throws ModelNotPreviouslyScheduled{
		
		if(modelExtractor != null){
			return modelExtractor.getProject();
		} else {
			throw new ModelNotPreviouslyScheduled();
		}	
	}

}
