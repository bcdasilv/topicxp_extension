package edu.wm.LDATopics.LDA;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Locale;
import java.util.Scanner;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;

public class LDAOptions implements Cloneable {
	final int METADATA_FORMAT_VERSION = 6;
	
	
	public long modificationStamp = Long.MIN_VALUE;
	public int numberIterations = 1000;
	public int numberWords = 6;
	public int numberTopics = 4;
	public double alpha = 50 / numberTopics;
	public double beta = .1;
	
	public boolean noThresholdOrCutoff = false;
	public boolean useThreshold = true;
	public double topicAssociationThreshold = 0.4;
	public int topicAssociationCutoff = 30;
	
	public String customStopWords[] = {};


	public boolean metadataIsInvalid = false;

	
	/**
	 * Checks the modification stamp of the project at the time the last topic map
	 * was generated and compares it to the current modification stamp.
	 * 
	 * @return true if current map is current and present, otherwise false.
	 */
	public void loadMetadata(String file)  throws FileNotFoundException {
		// Load in various settings and other information about how the last analysis
		//  was done and/or how the next one should be done
		File stampFile = new File ( file);
		
		try {
			Scanner stampFileScanner = new Scanner(stampFile);
			stampFileScanner.useLocale(Locale.ENGLISH);
			
			int metadataVersion = stampFileScanner.nextInt();
			if (metadataVersion != METADATA_FORMAT_VERSION) {
				metadataIsInvalid  = true;
				throw new FileNotFoundException(); // old metadata version, we can't read it.
			}
			
			modificationStamp = stampFileScanner.nextLong();
			numberIterations = stampFileScanner.nextInt();
			numberWords = stampFileScanner.nextInt();
			numberTopics = stampFileScanner.nextInt();
			alpha = stampFileScanner.nextDouble();
			beta = stampFileScanner.nextDouble();
			
			useThreshold = stampFileScanner.nextBoolean();
			topicAssociationThreshold = stampFileScanner.nextDouble();
			topicAssociationCutoff = stampFileScanner.nextInt();
			
			String line = stampFileScanner.nextLine();
			customStopWords = line.split("\\s+");
			
			metadataIsInvalid = false;
			
			stampFileScanner.close();
		} catch (FileNotFoundException e) {
			// Metdata doesn't exist; mark as invalid
			metadataIsInvalid  = true;
//			modificationStamp = Long.MIN_VALUE;
//			numberIterations = 1000;
//			numberWords = 6;
//			numberTopics = 4;
//			alpha = 50 / numberTopics;
//			beta = .1;
//
//			useThreshold = true;
//			topicAssociationThreshold = 0.4;
//			topicAssociationCutoff = 30;
//			
//			customStopWords = new String[0];
//			saveMetadata();
			}
	}
	
	public void saveMetadata(long modificationStamp, String file) {
		
		// Save the modification stamp  and other metadata
		try {
			File stampFile = new File ( file );
			stampFile.createNewFile();
			PrintStream stampFileOut = new PrintStream(new FileOutputStream(stampFile));
			
			// File version number
			stampFileOut.println(METADATA_FORMAT_VERSION);
			// Project modification stamp
			stampFileOut.println(Long.toString(modificationStamp)); 
			// Number of iterations to run LDA
			stampFileOut.println(Integer.toString(numberIterations));
			// Number of words to output per topic
			stampFileOut.println(Integer.toString(numberWords));
			// Number of topics to generate
			stampFileOut.println(Integer.toString(numberTopics));
			// Alpha, LDA parameter
			stampFileOut.println(Double.toString(alpha));
			// Beta, LDA parameter
			stampFileOut.println(Double.toString(beta));
			
			// Topic association method selection and threshold/cutoff parameters
			stampFileOut.println(Boolean.toString(useThreshold));
			stampFileOut.println(Double.toString(topicAssociationThreshold));
			stampFileOut.println(Integer.toString(topicAssociationCutoff));
			
			// Custom stopwords to use in preprocessing
			for (int i = 0; i < customStopWords.length; i++) {
				stampFileOut.print(customStopWords[i] + " ");
			}
			stampFileOut.println(Double.toString(topicAssociationThreshold));
			stampFileOut.println("");
			
			stampFileOut.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getStopWordsAsString() {
		String words = "";
		for (int i = 0; i < customStopWords.length; i ++) {
			words += customStopWords[i];
			if (i < customStopWords.length-1)
				words += ",";
		}
		return words;
	}	
	
	@Override
	public Object clone()  {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			// Not gonna happen! This object is clonable
			e.printStackTrace();
			return null;
		}
	}
	
}