package edu.wm.LDATopics.LDA;

import edu.wm.LDATopics.LDA.documents.LDADocument;

/**
	 * Represents something that belongs to a topic with a certain probability. Either a word or a document. 
	 * @author tcsava
	 *
	 */
	public class TopicMember implements Comparable {
		public String fullName;
		public String name;
		public double probability;
		public double relevance;
		public LDADocument document;
		
		public TopicMember (String displayName, String name, double probability) {
			this.name = displayName;
			this.fullName = name;
			this.probability = probability;
		}
		
		public TopicMember (LDADocument document, double probability) {
			this.name = document.getName();
			this.fullName = document.getFullName();
			this.probability = probability;
			this.document = document;
			
		}
		
		public void calculateRelevancy(Topic topics[]) {
			// rel(word, topic) = probability(term, topic) - 1/|topics| * sum of all p(word,topics)
			int probabilitySum = 0;
			for (Topic topic : topics) {
				if (!topic.equals(this)) {
					TopicMember word = topic.getWordByName(name);
					if (word != null) 
						probabilitySum += word.probability;
				}
			}
			relevance = probability - (1/(topics.length-1)) * probabilitySum;
		}
		
		public TopicMember (String name, double probability) {
			this (name, name, probability);
		}
		
		public boolean equals(Object object) {
			if (!(object instanceof TopicMember)) return false;
			return this.document.equals(((TopicMember)object).document);
		}
		
		public String toString() {
			return name;
		}
		
		public int compareTo(Object o) {
			return -Double.compare(this.probability, ((TopicMember)o).probability);
		}
		
	}