package edu.wm.LDATopics.LDA;

import java.util.ArrayList;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;

import edu.wm.LDATopics.LDATopics;
import edu.wm.LDATopics.LDA.documents.LDAClassDocument;
import edu.wm.LDATopics.LDA.documents.LDADocument;
import edu.wm.LDATopics.LDA.documents.LDAStringDocument;

public class QueryTopicMap extends LDATopicMap {
	String query;
	
	public QueryTopicMap(String project, LDAOptions options, String query) {
		super(project, options);
		
		this.options = (LDAOptions) options.clone();
		this.options.noThresholdOrCutoff = true;
		this.options.numberWords = 6;
		
		this.query = query;
	}
	
	public QueryTopicMap(String project, String basisModelName, LDAOptions options, String query) {
		super(project, basisModelName, options);
		
		this.options = (LDAOptions) options.clone();
		this.options.noThresholdOrCutoff = true;
		this.options.numberWords = 6;
		
		this.query = query;
	}
	
	@Override
	protected String getModelName() {
		return "userquery";
	}

	/**
	 * Returns a list of filenames of the .java files for a project.
	 * 
	 * @param project project name
	 * @return list of filenames
	 */
	protected LDADocument[] getDocuments() {
		ArrayList<LDADocument> documentsList = new ArrayList<LDADocument>();
		documentsList.add(new LDAStringDocument("query",query));
		documentsList.add(LDATopics.map.getClassMap().getDocuments()[0]);// need a dummy entry so it doesn't crash
		

		return (LDADocument[]) documentsList.toArray(new LDADocument[documentsList.size()]);		
	}

	/**
	 * Returns the probabilities of the user query with respect to each topic in an array.
	 * @return
	 */
	public Double[] getProbabilities() {
		Double[] probabilities = new Double[topics.length];
		
		for (Topic topic : topics)
			probabilities[topic.getNumber()-1] = topic.getDocumentByName("query").probability;
		
		return probabilities;
	}
}
