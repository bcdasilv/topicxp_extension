package edu.wm.LDATopics.LDA;

import jgibblda.*;

import java.io.*;
import java.lang.reflect.Array;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;

import edu.wm.LDATopics.LDATopics;

import edu.wm.LDATopics.LDA.Topic;
import edu.wm.LDATopics.LDA.TopicMember;

import edu.wm.LDATopics.LDA.documents.LDAClassDocument;
import edu.wm.LDATopics.LDA.documents.LDADocument;
import edu.wm.LDATopics.LDA.documents.LDAMethodDocument;
import edu.wm.LDATopics.LDA.documents.LDAPackageDocument;

/**
 * Responsible for representing and generating a set of topics.
 * @author tcsava
 *
 */
public abstract class LDATopicMap {
	final int METADATA_FORMAT_VERSION = 3;
	
	// LDA Analysis Parameters
	LDAOptions options;
	
	public double hellingerThreshold=0.4;
	
	public Topic topics[] = null;
	LDADocument documents[];
	
	boolean updateInProgress = false;
	boolean loadInProgress = false;
	
	public String project; // root that all documents should somehow be children of. can really be a proj, a package, a class... or can it?
	String basisModelName = ""; // existing topic map to measure this topic's documents by
	
	public boolean filterUsingQuery = false; // Should we filter documents using their hellinger distance from a query?
	
	/**
	 * Creates a new LDATopicMap for the source of project with name project,
	 * and loads up a topic map with either the last settings used or the default settings.
	 * 
	 * @param project project name
	 * @param path path to store LDA information in
	 */
	public LDATopicMap (String project, LDAOptions options) {
	   // TODO: should we still allow specifying path? not useful for us, might be useful as a library?
		this.project = project;
		this.options = options;
		
	    File folder = new File(getTopicDirectory());
	    if (!folder.exists()) folder.mkdirs();
	}
	
	
	public LDATopicMap(String project, String basisModelName, LDAOptions options)  {
		this.project = project;
		this.basisModelName = basisModelName;
		this.options = options;

	    File folder = new File(getTopicDirectory());
	    if (!folder.exists()) folder.mkdirs();
//		
//	    try {
//	    	loadTopicMapInJob();
//	    	// Don't count this load to prevent loads after updates
//	    	loadInProgress = false;
//	    } catch (FileNotFoundException e) {
//	    	// Couldn't load, so let's try generating the map
//	    	updateTopicMap();
//	    }
	}	
	
	protected abstract LDADocument[] getDocuments();
	protected abstract String getModelName();
	
	protected String getFullModelName() {
		String modelName = getModelName();
		if (basisModelName != "") modelName += "." + basisModelName;
		
		return modelName;
	}
	
	/**
	 * Returns a directory to store lda files in.
	 * 
	 * @return Directory for temp LDA files
	 */
	String getTopicDirectory() {
		return LDATopics.getDefault().getStateLocation().append("/"+project+"/").toOSString();
	}
	
	
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
			// No options, so use the defaults
		}
		
	}
	
	public void saveMetadata() {
		// Get the project modification stamp of the project at the time of reading the docs
		IProject projectRep = ResourcesPlugin.getWorkspace().getRoot().getProject(project);
		long modificationStamp = projectRep.getModificationStamp();
	
		options.saveMetadata(modificationStamp, getTopicDirectory() + "metadata");
	}	
	
	
	/**
	 * Calls JGibbLDA to generate a new Topic Map using the current settings.
	 */
	public void updateTopicMap() {
		if (basisModelName != "") {
			updateTopicMapFromExistingTopics();
			return;
		}
		
		// Load metadata if it's been saved; information about last LDA settings to use and such
		//loadMetadata();


		
		// then use jgibblda to generate LDA topics with
		String args[] = {"-est","-dir", getTopicDirectory(),"-dfile", getModelName(), "-model", getModelName(),
						 "-alpha", Double.toString(options.alpha), "-beta", Double.toString(options.beta),
						 "-ntopics", Integer.toString(options.numberTopics), "-twords", Integer.toString(options.numberWords),
						 "-niters", Integer.toString(options.numberIterations), "-savestep", Integer.toString(options.numberIterations)};
		Job generateTopics = new GenerateTopicsJob(args,options.numberIterations, getModelName());
		generateTopics.schedule();
		
		
		// And load the topics into our data structures
		loadTopicMapInJobWithoutCheck();
	}

	
	/**
	 * Calls JGibbLDA to generate a new Topic Map using the current settings.
	 * Regenerates the document->topic mapping using the existing topics.
	 */
	public void updateTopicMapFromExistingTopics() {
		// Load metadata if it's been saved; information about last LDA settings to use and such
		//loadMetadata();


		
		// then use jgibblda to generate LDA topics with
		String args[] = {"-inf","-dir", getTopicDirectory(), "-model", basisModelName,
						 "-dfile", getModelName(),
						 "-twords", Integer.toString(options.numberWords),
						 "-niters", Integer.toString(20)};
						// was numberIterations;  locked niters to 20 for restimation
		Job generateTopics = new GenerateTopicsJob(args, 20, getModelName());
		generateTopics.schedule();
		
		// And load the topics into our data structures
		loadTopicMapInJobWithoutCheck();
	}
	
	
	/**
	 * Uses updateTopicMapFromExistingTopics to compute topic probabilities for a use query
	 * (which is treated as any other document), then loads in the results and computes
	 * Hellinger distances between the user query and the documents in this topic map.
	 */
	public void userQuery(String query) {		
		filterUsingQuery = true; //TODO, 2010: ever need to set this to false?
		
		// Run the query, get the probabilities
		QueryTopicMap queryTopicMap = new QueryTopicMap(this.project, this.getFullModelName(), this.options, query);
		queryTopicMap.updateTopicMapFromExistingTopics();
		
		// And compute distanes between the query and all our documents
		ComputeDistanceJob distanceJob = new ComputeDistanceJob(queryTopicMap);
		distanceJob.schedule();			
	}
	
	
	/**
	 * Returns the probabilities of doc with respect to each topic in an array
	 * @param doc
	 * @return
	 */
	private Double[] getProbabilities(LDADocument doc) {
		Double[] probabilities = new Double[topics.length];
		
		//TODO, 2010: make getDocumentByName use full names instead of names? names may not be unique!!
		for (Topic topic : topics)
			probabilities[topic.getNumber()-1] = topic.getDocumentByNameFromAllProbabilities(doc.getName()).probability;
		
		return probabilities;
	}


	public double hellingerDistance(Double probability1[], Double probability2[]) {
		Double sum = 0.0;
		
		for (int i = 0; i < probability1.length; i++)
			sum += Math.pow(Math.sqrt(probability1[i]) - Math.sqrt(probability2[i]),2);
		
		return Math.sqrt(sum);
	}
	
	public boolean existsOnDisk() {
		String modelName = getFullModelName();
		
		return new File(getTopicDirectory()+modelName+".theta").exists() &&
			new File(getTopicDirectory()+modelName+".twords").exists();
	}	
	
	public void loadTopicMapInJob() throws FileNotFoundException {
		
		if (!existsOnDisk()) {
//			System.out.println("No topic map was available to be loaded for model '"+getModelName()+"'.");
			throw new FileNotFoundException();
		}
		
		Job loadTopics = new LoadTopicsJob(getModelName());
		loadTopics.schedule();
	}
	
	public void loadTopicMapInJobWithoutCheck() {
		Job loadTopics = new LoadTopicsJob(getModelName());
		loadTopics.schedule();
	}
	
	/**
	 * Reads in the last generated topic map for the current project and settings.
	 * @throws FileNotFoundException 
	 */
	public void loadTopicMap() throws FileNotFoundException {
		// Load metadata if it's been saved; information about last LDA settings used and such
		loadMetadata();
		String modelName = getFullModelName();
		
		if (!new File(getTopicDirectory()+modelName+".theta").exists()) {
//			System.out.println("No topic map was available to be loaded for model '"+getModelName()+"'.");
			throw new FileNotFoundException();
		}

		// Reset topics and such
		this.documents = getDocuments();
		topics = null;
		

		// Read in document probabilities for each topic
		Scanner scanTheta = new Scanner(new FileInputStream(getTopicDirectory()+modelName+".theta"));
		scanTheta.useLocale(Locale.ENGLISH);
		
		for (LDADocument doc : documents) {
			if (!scanTheta.hasNextLine()) throw new FileNotFoundException();; // Invalid input
			
			String line = scanTheta.nextLine();
			String topicProbabilities[] = line.split("\\s+");
			for (int i = 0; i < topicProbabilities.length; i++) {
				// Init topics array
				if ((topics == null) || (topics.length != topicProbabilities.length)) {
					topics = new Topic[topicProbabilities.length];
					
					for (int j = 0; j < topics.length; j++) {
						topics[j] = new Topic((j+1), this);
					}
				}
				//new Path(doc).lastSegment()
					
				topics[i].documents.add(new TopicMember(doc, Double.valueOf(topicProbabilities[i])));
			}
		}
		
		scanTheta.close();
		
		if (topics == null) throw new FileNotFoundException();;  // Invalid data!
		
		for ( Topic topic : topics)
			Collections.sort(topic.documents);	
		
		// Read in top words in each topic
		Scanner scanTwords = new Scanner(new FileInputStream(getTopicDirectory()+modelName+".twords"));
		scanTwords.useLocale(Locale.ENGLISH);
		
		if (!options.noThresholdOrCutoff) { // this is a hack, but it keeps us from reading twords if this is a queryTopicMap
			for (int i = 0; i < topics.length; i++) {
				scanTwords.nextLine();
				for (int j = 0; j < options.numberWords; j++) {
					//System.out.println(scanTwords.next() + ":"+ scanTwords.nextDouble());
					topics[i].words.add(new TopicMember(scanTwords.next(), scanTwords.nextDouble()));
				
				}
				scanTwords.nextLine();
			}
		}
		
		scanTwords.close(); // TODO, 2010: Didn't close; did that cause issue on XP??
		
		// Save all probabilities for computing MWE cohesion, hellinger distance
		for (Topic topic : topics)
			topic.allProbabilities = (ArrayList<TopicMember>) topic.documents.clone();

		filter(false);
	}
	
	public void filter(boolean refilter) {
		// TODO, 2010: detect this automatically?
		// If filtering's been done before, we need to copy documents back over
		for (Topic topic : topics)
			topic.documents = (ArrayList<TopicMember>) topic.allProbabilities.clone();

		if (filterUsingQuery)
			filterUsingQuery();
			
		if (!options.noThresholdOrCutoff) {
			if (options.useThreshold)
				limitTopicsPerDocumentWithThreshold(options.topicAssociationThreshold);
			else // use cutoff
				limitTopicsPerDocumentWithCutoff(options.topicAssociationCutoff);
		}
	}
	
	
	private void filterUsingQuery() {
		for (LDADocument doc : documents) {		
			for (Topic topic : topics) {
				TopicMember document = topic.getDocumentByName(doc.getName());
				if (doc.hellingerDistance > hellingerThreshold) {
					topic.documents.remove(document);
				}
			}
		}
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
		
		loadTopicMapInJob();
	}
	
	
	/**
	 * For Snowball.
	 * 
	 * @return a list of java and english stop words
	 */
	protected String[] getStopWords() {
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
	
	
	/**
	 * Creates a datafile in the LDA directory containing the words in the current list of documents.
	 */
	public void createDatafile() {				
		// If project LDA temp dir doesn't exist, create it.
		File directory = new File(getTopicDirectory());
		if (!directory.exists()) directory.mkdir();
		
		// Then create a .dat file in the directory with the words to be analyzed
		//  from all of the source files
		createDatafile(getTopicDirectory()+getModelName(), documents);
	}
	
	
	/**
	 * Creates a .dat file containing all the documents in a given corpus.
	 * 
	 * @param fileName Location to save datafile.
	 * @param documents List of filenames to save to datafile.
	 */
	public void createDatafile(String fileName, LDADocument documents[]) {
		FileOutputStream dataFile;
		try {
			dataFile = new FileOutputStream(fileName);
			PrintStream out = new PrintStream(dataFile);
			out.println(documents.length);
		
			for (LDADocument document : documents) {
				out.print(document.getFullName()+ " ="); //oops, this doesn't actually belong there. useful for debugging though.
				//Scanner scan = new Scanner(new FileInputStream(document));
				
				Reader reader = document.getReader();
								
				// Snowball makes datafiler -> datafil o.O is it supposed to? is that the stem?
				// Do preprocessing with Snowball
				TokenStream stream = new SnowballAnalyzer("English", getStopWords()).tokenStream("",reader);
				Token tok;
				String whitespace = "";
				//Bruno: I modified here as in the ClassTopicMap class.
				while ((tok = stream.next()) != null) {
					String text = tok.termText();
					if ( (text.length() >= 2) && !(isStringNumeric(text)) ){
						out.print(whitespace+tok.termText());
						whitespace = " ";
					}
				}
				out.print("\n");
			}
			
			out.close();
			dataFile.close();
		} catch (FileNotFoundException e) {
			System.err.println("File not found: "+fileName);
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("Error closing: "+fileName);
			e.printStackTrace();
		}		
	}
	
	//Added by Bruno - 06/27/2014
	/*
	 * Solution extracted from StackOverFlow post:
	 * http://stackoverflow.com/questions/1102891/how-to-check-if-a-string-is-a-numeric-type-in-java 
	 * */
	private boolean isStringNumeric( String str )
	{
	    DecimalFormatSymbols currentLocaleSymbols = DecimalFormatSymbols.getInstance();
	    char localeMinusSign = currentLocaleSymbols.getMinusSign();

	    if ( !Character.isDigit( str.charAt( 0 ) ) && str.charAt( 0 ) != localeMinusSign ) return false;

	    boolean isDecimalSeparatorFound = false;
	    char localeDecimalSeparator = currentLocaleSymbols.getDecimalSeparator();

	    for ( char c : str.substring( 1 ).toCharArray() )
	    {
	        if ( !Character.isDigit( c ) )
	        {
	            if ( c == localeDecimalSeparator && !isDecimalSeparatorFound )
	            {
	                isDecimalSeparatorFound = true;
	                continue;
	            }
	            return false;
	        }
	    }
	    return true;
	}	

	
	/**
	 * Limits documents to only being in one topic at a time, the one that it best fits in.
	 * 
	 * @param limit Number of topics to limit a document to.
	 */
	public void limitTopicsPerDocument(int limit) {
		// assume limit = 1 for now
		int currentDoc = 0;
		int bestTopic;
		
		if (topics == null) return;
		
		for (LDADocument doc : documents) {
			int currentTopic = 0;
			double bestProbability = -1;
			bestTopic = -1;
		
			for (Topic topic : topics) {
				TopicMember document = topic.getDocumentByName(doc.getName());
				if (document.probability > bestProbability) {
					bestTopic = currentTopic;
					bestProbability = document.probability;
				}
				currentTopic++;
			}
			
			currentTopic = 0;
			for (Topic topic : topics) {
				if (currentTopic != bestTopic) {
					TopicMember toRemove = topic.getDocumentByName(doc.getName());
					
					if (toRemove != null) {
						topic.documents.remove(toRemove);
					}
				}
				currentTopic++;
			}
			
			currentDoc++;
		}
	}
	
	
	/**
	 * Limits documents to only being in a topic if the associated probability is above a threshold
	 * 
	 * @param threshold Threshold required
	 */
	public void limitTopicsPerDocumentWithThreshold(double threshold) {
		
		if (topics == null) return;
		
		for (LDADocument doc : documents) {		
			for (Topic topic : topics) {
				TopicMember document = topic.getDocumentByName(doc.getName());
				if ((document != null) && (document.probability < threshold)) {
					topic.documents.remove(document);
				}
			}
		}
	}

	
	/**
	 * Limits number of documents that can be associated with one topic to a specific number.
	 * 
	 * @param cutoff Cuttoff point.
	 */
	public void limitTopicsPerDocumentWithCutoff(int cutoff) {
		
		if (topics == null) return;
		
		for (Topic topic : topics) {
			if (topic.documents.size() <= cutoff)
				continue; // Our work is done for us! No need to cut any thing off.
			
			// Otherwise, documents are already sorted so we just need to cut off the bottom size-cutoff documents
		    int totalDocuments = topic.documents.size();
		    for (int i = cutoff; i < totalDocuments; i++)
		    	topic.documents.remove(cutoff);
		    
		}
	}

	
	public String toString() {
		String str = ""; 
		int num = 1;
		
		for (Topic topic : topics) {
			str += topic.getName() + ":\n";
			str += "words:\n";
			for (TopicMember word : topic.words) {
				str += "\t" + word.name + "\t" + word.probability + "\n"; 
			}
			
			str += "\ndocs:\n";
			for (TopicMember document: topic.documents) {
				str += "\t" + document.name + "\t" + document.probability + "\n";
			}
			str += "\n";
			num++;
		}
		
		return str;
	}
	
	// Job for generating topic map in
	public class GenerateTopicsJob extends Job {
		String[] args;
		int numberIterations;
		private String modelName;
		boolean doNotSchedule = false;
		
		public GenerateTopicsJob(String[] args, int numberIterations, String modelName) {
			super("Extracting Topics from "+modelName+ "");
			this.args = args;
			this.modelName = modelName;
			this.setPriority(Job.LONG);
			this.setRule(new LDATopicUpdateRule());
			if (modelName.equals("classes"))
				this.setUser(true);
			else
				this.setUser(false);
			this.numberIterations = numberIterations;

			// Only schedule one update at a time!
			if (updateInProgress)
				doNotSchedule = true;
			else
				updateInProgress = true;
		}
		
		@Override
		public boolean shouldSchedule() {
			return !doNotSchedule && super.shouldSchedule();
		}

		public IStatus run(IProgressMonitor monitor) {
			monitor.beginTask("", numberIterations);
			
			monitor.subTask("Extracting words from "+modelName+"...");
			// Get the documents we're analyzing
			documents = getDocuments();
			
			// Make sure we have a .dat file for the project,
			createDatafile();
			
			monitor.subTask("Extracting topics...");
			
			PrintStream newOut = null;
			try {
				newOut = new ProgressStream(monitor, new File(getTopicDirectory()+getFullModelName()+".log"));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return Status.CANCEL_STATUS;
			}
			PrintStream oldOut = System.out;
			System.setOut(newOut);
			// Do the work
			LDA.main(args);
			System.setOut(oldOut);
			
			// JGibbLDA always calls the model model-final, so we need to rename the files
				
				monitor.subTask("Moving LDA files.");
			File files[] = new File(getTopicDirectory()).listFiles(new FilenameFilter() {
				public boolean accept(File dir, String name) {
					if (name.startsWith("model-final")) return true;
					else return false;
				}});
			for (File file : files) {
				File destination = new File(file.getParent() + "/"+getFullModelName() + file.getName().split("model-final")[1]);
				destination.delete();
				Boolean success = file.renameTo(destination);
				if (!success)
					System.out.println("LDA Topics ERROR: failed to rename "+file.toString()+ " to:"+file.getParent() + "/"+getModelName() + file.getName().split("model-final")[1]);
			}
			
			// TODO: remove files we don't care to read?
			
			// Save metadata about how the topic map was generated
			monitor.subTask("Saving LDA metadata.");
			saveMetadata();

			updateInProgress = false;
			return Status.OK_STATUS;
		   }
		
		// All writes to this print stream are copied to two print streams
		public class ProgressStream extends PrintStream {
		    IProgressMonitor monitor;
		    
		    public ProgressStream(IProgressMonitor monitor, File logFile) throws FileNotFoundException {
		    	super(new FileOutputStream(logFile));
		        //super(out);
		        this.monitor = monitor;
		    }
		    
		    public void write(byte buf[], int off, int len) {
		        try {
		            super.write(buf, off, len);
		        } catch (Exception e) {
		        }
		        
		        // And indicate some progress
		        for (byte b : buf)
		        	if (b == '\n')
		        	monitor.worked(1);
		    }
		    public void flush() {
		        super.flush();
		    }
		}
	}
	
	
	// Job for loading the topic map into our data structures
	public class LoadTopicsJob extends Job {
		String[] args;
		private boolean doNotSchedule;
		
		public LoadTopicsJob(String modelName) {
			super("Loading topic map for "+ modelName);
			//this.args = args;
			this.setPriority(Job.LONG);
			this.setRule(new LDATopicUpdateRule());
			// Only schedule one load at a time!
			if (loadInProgress)
				doNotSchedule = true;
			else
				loadInProgress = true;
		}
		
		@Override
		public boolean shouldSchedule() {
			return !doNotSchedule && super.shouldSchedule();
		}

		public IStatus run(IProgressMonitor monitor) {

			monitor.beginTask("Loading topics...", monitor.UNKNOWN);
			
			// Load in the new topic information
			try {
				loadTopicMap();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			loadInProgress = false;
			return Status.OK_STATUS;
		}
	}
	
	public class ComputeDistanceJob extends Job {
		QueryTopicMap queryTopicMap;
		
		public ComputeDistanceJob(QueryTopicMap queryMap) {
			super("Computing Hellinger Distances...");
			this.queryTopicMap = queryMap;
			setRule(new LDATopicUpdateRule());
			
		}
		
		@Override
		protected IStatus run(IProgressMonitor monitor) {
			Double probabilities[] = queryTopicMap.getProbabilities();
			boolean allSame = true;
			
			for (int i = 0; i < (probabilities.length-1); i++)
				if (!probabilities[i].equals(probabilities[i+1]))
					allSame = false;
			if (!probabilities[0].equals(probabilities[probabilities.length-1]))
				allSame = false;
			
			if (allSame) {
				// TODO, 2010: message box telling the user about this
				filterUsingQuery = false;
				LDATopics.twoDimensionalView.updateToolbar();
				return Status.OK_STATUS;
			}
				
			// Now, compute Hellinger distances for each class and store them
			for (LDADocument doc : documents) {
				doc.hellingerDistance = hellingerDistance(probabilities, getProbabilities(doc));
			}
			
			filter(true);

			return Status.OK_STATUS;
		}
	}
	
}
