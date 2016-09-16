package edu.wm.LDATopics.LDA.documents;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Scanner;

import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.snowball.SnowballAnalyzer;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaModelException;
import org.malnatij.svplugin.model.ClassRepresentation;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.ui.PartInitException;

import edu.wm.LDATopics.LDA.Topic;
import edu.wm.LDATopics.LDA.TopicMember;

public class LDAClassDocument extends LDADocument {

	ICompilationUnit classUnit;
	public double probability;
	public double MWECohesion;
	//public int LCbC;
	private ArrayList<Topic> topics;  
	
	public LDAClassDocument (ICompilationUnit classUnit) {
		this.classUnit = classUnit;
		topics = new ArrayList<Topic>();
	}
	
	public void addTopic(Topic topic){
		topics.add(topic);
	}
	
	public String classDocTopicsToSting(){
		String topicList="";
		for (Topic topic : topics) {
			topicList += topic.getName() + ", ";
		}
		return topicList;
	}
	
	/**
	 * Returns the display name of the document.
	 * 
	 * @return display name
	 */
	public String getName() {
		return classUnit.getElementName();
	}
	
	public String getFullName() {
		return classUnit.getHandleIdentifier();
	}

	public String getPackage() {
		return classUnit.getParent().getElementName();
	}
	
	public int getSize() {
		try {
			return classUnit.getSource().length();
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}
	}
	
	public String toString() {
		return getName();
	}
	
	public double getMWECohesion() {
		return MWECohesion;
	}
	
	public int getLCbC() {
		return topics.size();
	}
	
	public boolean equals(Object otherDocument) {
		if (otherDocument instanceof ClassRepresentation) {
			System.out.println(((ClassRepresentation)otherDocument).getName() + "vs" +getName() );
			System.out.println(((ClassRepresentation)otherDocument).getPackageContainer().getName() + "vs" +getPackage() );
			
			if (((ClassRepresentation)otherDocument).getName().equals(getName()) &&
				((ClassRepresentation)otherDocument).getPackageContainer().getName().equals(getPackage()))
				return true;
		}
		if (!(otherDocument instanceof LDAClassDocument)) return false;
		return classUnit.equals(((LDAClassDocument)otherDocument).classUnit);
	}
	
	/**
	 * Returns the contents of the document as a string.
	 * 
	 * @return Document contents.
	 */
	public String getContents() {
		/*// Extract identifiers and comment text using the AST parser.
		ICompilationUnit units[] = {classUnit};
		return getContentsFromAST(units, null, null);*/
		// Old method; just asked eclipse for the source code, but that didn't help with extracting identifiers
		try {
			return classUnit.getSource();
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	public void openInEditor() {
//		IFile fileToBeOpened;
//		try {
//			// This line's probably way convoluted... Works though.
//			fileToBeOpened = classUnit.getCorrespondingResource().getWorkspace().getRoot().getFile(classUnit.getPath());
//			
//			IEditorInput editorInput = new FileEditorInput(fileToBeOpened);
//			IWorkbenchWindow window=PlatformUI.getWorkbench().getActiveWorkbenchWindow();
//			IWorkbenchPage page = window.getActivePage();
//			page.openEditor(editorInput, org.eclipse.jdt.ui.javaeditor);
//		} catch (JavaModelException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (PartInitException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		try {
			JavaUI.openInEditor(classUnit.getPrimaryElement());
		} catch (PartInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
