package edu.wm.LDATopics.LDA;

import java.util.ArrayList;
import java.util.Arrays;

import edu.wm.LDATopics.LDA.documents.LDADocument;

/**
	 * A topic is a set of words and documents that belong to that topic with a certain probability.
	 * @author tcsava
	 *
	 */
public class Topic {
	// A topic is made up of a set of documents that belong to it to different degrees,
	// and a set of words that belong to it to different degrees.
	
	public ArrayList<TopicMember> documents = new ArrayList<TopicMember>();
	public ArrayList<TopicMember> words = new ArrayList<TopicMember>();

	public ArrayList<TopicMember> allProbabilities = new ArrayList<TopicMember>();

	
	public int number;
	public LDATopicMap map;
	
	public Topic(int number, LDATopicMap map) {
		this.number = number;
		this.map = map;
	}

	public String getName() {

		return "Topic #"+number;
	}
	
	public String getBestWords() {
		// Pick term(s) with most relevance, as defined by Kuhn:
		// rel(word, topic) = probability(term, topic) - 1/|topics| * sum of all p(word,topics)
		// For now, let's try the two most relevant terms:
		
		calculateRelevancy(); // TODO: do this once instead of every time getWords is called
		TopicMember bestWords[] = new TopicMember[5];
		for (TopicMember word : words) {
			boolean placed = false;
			for (int i = 0; i < bestWords.length; i++) {
				if ((((bestWords[i] == null) || (bestWords[i].relevance < word.relevance)) && !placed) &&
						(word.name.length() > 2)){ // also, filter out one-letter words
					bestWords[i] = word;
					placed = true;
				}
			}
		}
		
		String str = "[";
		for (TopicMember word : bestWords) { 
			if (word != null) {
				if (str != "[") str += ", ";
				str += word.toString();
			}
		}
		str += "]";

		return str;
	}
	
	/*
	 * Added by Bruno - 05/31/2012
	 * Check if the topic is within a document.
	 * Need to test if it makes sense!
	 * */
	public boolean isInDocument(LDADocument doc){
		String[] docNames = getDocumentNames();
		for (int i = 0; i < docNames.length; i++) {
			if (docNames[i].equals(doc.getName()))
				return true;
		}
		return false;
	}
	
	/*
	 * Comment added by Bruno - 03/23/2012
	 * Five most relevant documents for this topic. Why five?
	 *  */
	public TopicMember[] getBestDocuments() {
		TopicMember bestDocs[] = new TopicMember[5];
		for (TopicMember doc : documents) {
			boolean placed = false;
			for (int i = 0; i < bestDocs.length; i++) {
				if ((((bestDocs[i] == null) || (bestDocs[i].relevance < doc.relevance)) 
						&& !placed)){
					bestDocs[i] = doc;
					placed = true;
				}
			}
		}
		return bestDocs;
//		String str = "";
//		for (TopicMember member : bestDocs)
//			str += member.name + "\n";
//			
//		
//		return str;//Arrays.toString((Object[])bestDocs);
	}
	
	public String[] getDocumentNames() {
		ArrayList<String> docs = new ArrayList<String>();
		for (TopicMember doc : documents) {
			docs.add(doc.name);
		}
		return (String[]) docs.toArray(new String[docs.size()]);
	}
	
	public TopicMember[] getDocuments() {
		return documents.toArray(new TopicMember[documents.size()]);
	}
	
	public TopicMember getDocumentByName(String name) {
		for (TopicMember document : documents) {
			if (document.name.equals(name)) {
				return document;
			}
		}
		return null;
	}
	
	public TopicMember getDocumentByNameFromAllProbabilities(String name) {
		for (TopicMember document : allProbabilities) {
			if (document.name.equals(name)) {
				return document;
			}
		}
		return null;
	}
	
	public TopicMember getWordByName(String name) {
		for (TopicMember word : words) {
			if (word.name.equals(name)) {
				return word;
			}
		}
		return null;
	}
	
	public void calculateRelevancy() {
		for (TopicMember word : words) {
			word.calculateRelevancy(map.topics);
		}
	}

	public int getNumber() {
		return number;
	}

	public boolean documentIsInTopic(LDADocument document) {
		for (TopicMember member : documents) {
			if (member.document.equals(document)) {
				return true;
			}
		}
		return false;
	}
}