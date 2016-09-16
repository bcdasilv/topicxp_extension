package edu.wm.LDATopics.LDA;

import jgibblda.*;

import java.io.*;
import java.lang.reflect.Array;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import org.apache.lucene.analysis.snowball.SnowballAnalyzer;
import org.apache.lucene.analysis.StopAnalyzer;
import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenStream;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IBuffer;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IOpenable;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;

import edu.wm.LDATopics.LDATopics;

import edu.wm.LDATopics.LDA.Topic;
import edu.wm.LDATopics.LDA.TopicMember;

import edu.wm.LDATopics.LDA.documents.LDAClassDocument;
import edu.wm.LDATopics.LDA.documents.LDADocument;
import edu.wm.LDATopics.LDA.documents.LDAMethodDocument;
import edu.wm.LDATopics.LDA.documents.LDAPackageDocument;
//TODO: use LDATopicMap for LDA stuff, this class for project/eclipse/etc stuff

/**
 * Responsible for representing and generating a set of topics.
 * @author tcsava
 *
 */
public class ProjectTopicMap {
	
	// Topics Maps
	ClassTopicMap classMap;
	PackageTopicMap packageMap;
	MethodTopicMap methodMap;
	
	// LDA Analysis Parameters
	private LDAOptions options = new LDAOptions();
	
	public String project; // root that all documents should somehow be children of. can really be a proj, a package, a class... or can it?
	
	/**
	 * Creates a new LDATopicMap for the source of project with name project,
	 * and loads up a topic map with the default settings.
	 * 
	 * @param project project name
	 */
	public ProjectTopicMap (String project) {
		this(project, new LDAOptions());
	}
	
	/**
	 * Creates a new LDATopicMap for the source of project with name project,
	 * and loads up a topic map with either the settings provided or the default settings.
	 * 
	 * @param project project name
	 * @param options options by which to generate LDA topic map
	 */
	public ProjectTopicMap (String project, LDAOptions newOptions) {
	    this.project = project;
	    
	    File folder = new File(getTopicDirectory());
	    if (!folder.exists()) folder.mkdirs();
	    
	    loadMetadata();
	    // TODO, 2010: No elegant path if data is somehow corrupt
	    
	    // Set up topic maps
	    classMap = new ClassTopicMap(project,options); // + settings?		
		packageMap = new PackageTopicMap(project, classMap.getModelName(), options);
		methodMap = new MethodTopicMap(project,  options);
		
		// Load the potentially new options, check if model is up to date, etc
	    setOptions(newOptions, true);
	}
	
	private boolean outOfDate() {
		IProject projectRep = ResourcesPlugin.getWorkspace().getRoot().getProject(project);
		long curModificationStamp = projectRep.getModificationStamp();
		
		return !(options.modificationStamp == curModificationStamp);
	}
	
	private boolean existsOnDisk() {
		// check all files that need to exist
		return new File(getTopicDirectory() + "metadata").exists() &&
				getClassMap().existsOnDisk() &&
				getPackageMap().existsOnDisk() &&
				getMethodMap().existsOnDisk();
	}

	public ClassTopicMap getClassMap() {
		//If it's not yet generated or it's out of date, update it.
//		if (!classMap.generated() || classMap.isOutOfDate())) 
//			classMap.updateTopicMap();
//			
		return classMap;			
	}
	
	public PackageTopicMap getPackageMap() {
		//If it's not yet generated or it's out of date, update it.
//		if (!packageMap.generated() || packageMap.isOutOfDate())) 
//			packageMap.updateTopicMap();

		return packageMap;	
	}

	public MethodTopicMap getMethodMap() {
		//If it's not yet generated or it's out of date, update it.
//		if (!methodMap.generated() || methodMap.isOutOfDate())) 
//			methodMap.updateTopicMap();
//		
		return methodMap;	
	}
	
	/**
	 * Returns a directory to store lda files in.
	 * 
	 * @return Directory for temp LDA files
	 */
	private String getTopicDirectory() {
		return LDATopics.getDefault().getStateLocation().append("/"+project+"/").toOSString();
	}	
	
	public void computeAllMWECohesion(IProgressMonitor monitor) {
		for (LDADocument LDAdoc : classMap.documents) {
			LDAClassDocument doc = (LDAClassDocument) LDAdoc;
			monitor.subTask(doc.getName());
			doc.MWECohesion = getMWECohesion(doc.getFullName());
			monitor.worked(1);
		}
	}
	
	public double getMWECohesion(String clas) {
		Double d = getMethodMap().getMWECohesion(clas);
		//System.out.println(d);
		return d;
	}
	
//	public void computeAllLCbCCohesion(IProgressMonitor monitor) {
//		for (LDADocument LDAdoc : classMap.documents) {
//			LDAClassDocument doc = (LDAClassDocument) LDAdoc;
//			monitor.subTask(doc.getName());
//			doc.LCbC = getLCbC(doc.getFullName());
//			monitor.worked(1);
//		}
//	}
	
	//Added by Bruno
	public void computeAllLCbC(IProgressMonitor monitor) {
		try {
			PrintWriter pw = new PrintWriter(getTopicDirectory()+"LCbCcounting_"+project+".csv");
			pw.println("Component, LCbC, MWE");
			for (LDADocument LDAdoc : classMap.documents) {
				LDAClassDocument doc = (LDAClassDocument) LDAdoc;
				monitor.subTask(doc.getName());
				//doc.LCbC = getLCbC(doc.getFullName());
				//doc.LCbC = 0;
				Topic[] topics = getClassMap().topics;
				for (Topic topic : topics) {
					if (topic.isInDocument(LDAdoc))
							doc.addTopic(topic);//doc.LCbC++;
				}
				//System.out.println(doc.getFullName() + " LCbC = "+doc.getLCbC() + " Topics: "+doc.classDocTopicsToSting());
				pw.println(doc.getPackage()+"."+doc.getName()+","+doc.getLCbC()+","+doc.getMWECohesion());
				monitor.worked(1);
				
			}
			pw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
//	public int getLCbC(String clas) {
//		int lcbc = getMethodMap().getLCbC(clas);
//		//System.out.println(d);
//		return lcbc;
//	}
	
	/**
	 * Checks the modification stamp of the project at the time the last topic map
	 * was generated and compares it to the current modification stamp.
	 * 
	 * @return true if current map is current and present, otherwise false.
	 */
	public void loadMetadata() {
		try {
			options.loadMetadata( getTopicDirectory() + "metadata");
		} catch (FileNotFoundException e) {
			// No options to load, use default options
		//	options = (LDAOptions) defaultOptions.clone();
		}
		
	}
	
	public void saveMetadata() {
		// Get the project modification stamp of the project at the time of reading the docs
		IProject projectRep = ResourcesPlugin.getWorkspace().getRoot().getProject(project);
		long modificationStamp = projectRep.getModificationStamp();
	
		options.saveMetadata(modificationStamp, getTopicDirectory() + "metadata");
	}	
	
	public void exportToFile(String filename) throws IOException {
		// create .zip with all necessary LDA analysis files
		
		// All the files? Nah.
		//String files[] = {"model-final.others", "model-final.phi", "model-final.tassign", "model-final.theta", "model-final.twords", "metadata", "sourcefiles.dat", "wordmap.txt"};
		// Just the essential files
		// later, include some renderings of the visualizations or such?
		String files[] = {"model-final.theta", "model-final.twords", "metadata"};

		FileOutputStream zipFile = new FileOutputStream(filename);
		ZipOutputStream zip = new ZipOutputStream(zipFile);

		for (String file : files) {
			ZipEntry entry = new ZipEntry(file);
			FileInputStream fileStream = new FileInputStream(getTopicDirectory() + file);
			
			zip.putNextEntry(entry);
	        for (int c = fileStream.read(); c != -1; c = fileStream.read()) {
	        	zip.write(c);
	        }
			fileStream.close();
		}
		
		
		zip.close();
		zipFile.close();
	}
	
	
	public void importFromFile(String filename) throws IOException {
		// load back from an archived topic map
		
		// overwrite existing map? GUI should warn about that "this will overwrite your current map for project x, are you sure?"
		String files[] = {"model-final.theta", "model-final.twords", "metadata"};

		// extract files to current map directory, load them up.
		ZipFile zip = new ZipFile(filename);
		for (String file : files) {
			ZipEntry entry = zip.getEntry(file);
			InputStream entryIn = zip.getInputStream(entry);
		
			FileOutputStream entryOut = new FileOutputStream(getTopicDirectory() + file);		
			for (int c = entryIn.read(); c != -1; c = entryIn.read()) {
			       entryOut.write(c);
			}
		}
		
		loadTopicMaps();
	}
	
	public LDAOptions getOptions() {
		return options;
	}
	
	public boolean setOptions(LDAOptions newOptions, boolean forceReloadOrUpdate) {
		boolean updateTopics = false;
	//	boolean reloadTopics = false;
		boolean refilterTopics = false;
		
		// Check if we need to update topics or just reload them
		if (newOptions.alpha != options.alpha ||
			newOptions.beta != options.beta ||
			!Arrays.equals(newOptions.customStopWords,options.customStopWords) ||
			newOptions.numberIterations != options.numberIterations ||
			newOptions.numberTopics != options.numberTopics ||
			newOptions.numberWords != options.numberWords ||
			options.metadataIsInvalid ||
			outOfDate() ||  // TODO, 2010: Do we really want to update if user changes threshold and source code has changed?
			!existsOnDisk())
			updateTopics = true;
		
		if (newOptions.useThreshold != options.useThreshold ||
			newOptions.topicAssociationThreshold != options.topicAssociationThreshold ||
			newOptions.topicAssociationCutoff != options.topicAssociationCutoff)
			refilterTopics = true;
			
		
		options = newOptions;
		
		classMap.options = options;
		packageMap.options = options;
		if (methodMap != null)
			methodMap.options = options;
		
		saveMetadata();
		if (updateTopics)
			updateTopicMaps();
		else if (forceReloadOrUpdate) {
			loadTopicMaps();
		    
		} else if (refilterTopics) {
			classMap.filter(true);
			packageMap.filter(true);
			if (methodMap != null)
				methodMap.filter(true);
		}
	
		return updateTopics || forceReloadOrUpdate || refilterTopics;
	}
	
	private void loadTopicMaps() {
		try {

			// Remove any query
			// TODO, 2010: do we have to remove the query in this case?
			classMap.filterUsingQuery = false;
			if (LDATopics.twoDimensionalView != null)
				LDATopics.twoDimensionalView.updateToolbar();
			
			classMap.loadTopicMapInJob();
			packageMap.loadTopicMapInJob();
			if (methodMap != null){
				methodMap.loadTopicMapInJob();
			}
			
			// Compute Cohesion
			ComputeCohesionJob job = new ComputeCohesionJob();
			job.schedule();
			
			// Compute LCbC
			ComputeLCbCJob lcbcJob = new ComputeLCbCJob();
			lcbcJob.schedule();
			
		
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	
	public void updateTopicMaps() {

		// Remove any query
		classMap.filterUsingQuery = false;
		if (LDATopics.twoDimensionalView != null)
			LDATopics.twoDimensionalView.updateToolbar();
		
		// Update their settings if necessary
		classMap.updateTopicMap();
		packageMap.updateTopicMap();
		if (methodMap != null){
			methodMap.updateTopicMap();
		}
		
		// Compute Cohesion
		ComputeCohesionJob job = new ComputeCohesionJob();
		job.schedule();
		
		// Compute LCbC
		ComputeLCbCJob lcbcJob = new ComputeLCbCJob();
		lcbcJob.schedule();
	
	}

	
	/**
	 * For Snowball.
	 * 
	 * @return a list of java and english stop words
	 */
	private String[] getStopWords() {
		String[] JAVA_STOP_WORDS = { "public", "private", "protected",
				"interface", "abstract", "implements", "extends", "null",
				"new", "switch", "case", "default", "synchronized", "do", "if",
				"else", "break", "continue", "this", "assert", "for",
				
				"instanceof", "transient", "final", "static", "void", "catch",
				"try", "throws", "throw", "class", "finally", "return",
				"const", "native", "super", "while", "import", "package",
				"true", "false"};

		// these should be good stop words too, right? :
		String[] GENERIC_STOP_WORDS = {	"java", "boolean", "string", "get", "set", ""};
		
		HashSet<String> st = new HashSet<String>(Arrays.asList(StopAnalyzer.ENGLISH_STOP_WORDS));
		st.addAll(Arrays.asList(JAVA_STOP_WORDS));
		st.addAll(Arrays.asList(GENERIC_STOP_WORDS));
		st.addAll(Arrays.asList(options.customStopWords)); // Add any project-specific stop words the user has entered
				
		return st.toArray(new String[st.size()]);
	}
	
	
	
	public String toString() {
		return "";
	}
	
	
	
	// Job for computing all classes cohesion
	public class ComputeCohesionJob extends Job {
		String[] args;
		private boolean doNotSchedule;
		
		public ComputeCohesionJob() {
			super("Computing class cohesion metric");
			this.setPriority(Job.LONG);
			this.setRule(new LDATopicUpdateRule());
		}

		public IStatus run(IProgressMonitor monitor) {

			monitor.beginTask("Computing cohesion for ", classMap.documents.length);
			
			// Load in the new topic information
			computeAllMWECohesion(monitor);
			return Status.OK_STATUS;
		}
	}
	
	// Job for computing all classes Lack of Concern (Topic) based Cohesion
	public class ComputeLCbCJob extends Job {
		String[] args;
		private boolean doNotSchedule;
		
		public ComputeLCbCJob() {
			super("Computing class LCbC metric");
			this.setPriority(Job.LONG);
			this.setRule(new LDATopicUpdateRule());
		}

		public IStatus run(IProgressMonitor monitor) {

			monitor.beginTask("Computing LCbC for ", classMap.documents.length);
			
			// Load in the new topic information
			computeAllLCbC(monitor);
			return Status.OK_STATUS;
		}
	}
}
