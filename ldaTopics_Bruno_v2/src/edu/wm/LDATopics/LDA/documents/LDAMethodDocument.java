package edu.wm.LDATopics.LDA.documents;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;

public class LDAMethodDocument extends LDADocument {

	IMethod method;
	
	public LDAMethodDocument(IMethod method) {
		this.method = method;
	}
	
	public String getClassFullName() {
		return method.getParent().getParent().getHandleIdentifier();
	}
	
	/**
	 * Returns the display name of the document.
	 * 
	 * @return display name
	 */
	public String getName() {
		return method.getElementName();
	}
	
	public String getFullName() {
		return method.getHandleIdentifier();
	}
	
	/**
	 * Returns the contents of the document as a string.
	 * 
	 * @return Document contents.
	 */
	public String getContents() {
		ICompilationUnit units[] = {(ICompilationUnit)method.getCompilationUnit()};
		return getContentsFromAST(units, method, null);
		
//		try {
//			return method.getSource();
//		} catch (JavaModelException e) {
//			e.printStackTrace();
//		}
//		return "";
	}
}
