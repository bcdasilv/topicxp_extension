package edu.wm.LDATopics.LDA.documents;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;

public class LDAStringDocument extends LDADocument {

	String name;
	String query;
	
	public LDAStringDocument(String name, String query) {
		this.name = name;
		this.query = query;
	}

	@Override
	public String getFullName() {
		// TODO Auto-generated method stub
		return name;
	}
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return name;
	}
	
	/**
	 * Returns the contents of the document as a string.
	 * 
	 * @return Document contents.
	 */
	public String getContents() {
		return query;
	}
}
