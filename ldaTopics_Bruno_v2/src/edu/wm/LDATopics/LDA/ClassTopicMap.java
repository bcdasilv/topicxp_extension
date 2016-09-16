package edu.wm.LDATopics.LDA;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Reader;
import java.io.StringReader;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Scanner;

import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.snowball.SnowballAnalyzer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;

import edu.wm.LDATopics.LDA.documents.LDAClassDocument;
import edu.wm.LDATopics.LDA.documents.LDADocument;

public class ClassTopicMap extends LDATopicMap {



	public ClassTopicMap(String project, LDAOptions options) {
		super(project, options);
	}
	
	public ClassTopicMap(String project, String basisModelName, LDAOptions options) {
		super(project, basisModelName, options);
	}
	
	@Override
	protected String getModelName() {
		return "classes";
	}

	/**
	 * Returns a list of filenames of the .java files for a project.
	 * 
	 * @param project project name
	 * @return list of filenames
	 */
	protected LDADocument[] getDocuments() {
		ArrayList<LDAClassDocument> documentsList = new ArrayList<LDAClassDocument>();
		try {
			IProject projectRep = ResourcesPlugin.getWorkspace().getRoot().getProject(project);
			IPackageFragment fragments[] = JavaCore.getJavaCore().create(projectRep).getPackageFragments();
		
			for (IPackageFragment fragment : fragments) { 
				ICompilationUnit units[];
				units = fragment.getCompilationUnits();
				
				// or get the fragments and then their path
				for (ICompilationUnit unit : units) {
					documentsList.add(new LDAClassDocument(unit));
					//System.out.println(ResourcesPlugin.getWorkspace().getRoot().getLocation().append(unit.getPath()).toOSString());
				}
			}
			
		} catch (JavaModelException e) {
			System.err.println("Error getting java code elements.");
			e.printStackTrace();
		}
		
		return (LDAClassDocument[]) documentsList.toArray(new LDAClassDocument[documentsList.size()]);		
	}
	
	/**
	 * Creates a .dat file containing all the documents in a given corpus.
	 * 
	 * ADDED BY BRUNO: 05/31/2012
	 * 
	 * REASON: remove license comments which normally appears at the 
	 * beginning of each file (before the package declaration).
	 * 
	 * ATTENTION: Need to review this method in projects with classes that 
	 * do not follow this pattern.
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
				
				Scanner scanner = new Scanner(reader);
				
				String newStrDocument = "";
				
				String line = scanner.nextLine();

				while(scanner.hasNextLine() && (!line.startsWith("package") || !line.endsWith(";")) ) {
					line = scanner.nextLine();
				}
				//This workaround only works in projects where the license comments appear 
				//before the package declaration.
				if(line.startsWith("package") && line.endsWith(";")){
					//newStrDocument += line; //unconmment here if the package declaration must be considered
					while(scanner.hasNextLine()){
						line = scanner.nextLine();
						newStrDocument += line;
					}
				}
				reader = new StringReader(newStrDocument);
				
				// Snowball makes datafiler -> datafil o.O is it supposed to? is that the stem?
				// Do preprocessing with Snowball
				TokenStream stream = new SnowballAnalyzer("English", getStopWords()).tokenStream("",reader);
				Token tok;
				String whitespace = "";
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
	
}
