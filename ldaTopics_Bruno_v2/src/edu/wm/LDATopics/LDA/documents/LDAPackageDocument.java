package edu.wm.LDATopics.LDA.documents;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaModelException;

public class LDAPackageDocument extends LDADocument {

	IPackageFragment pack;
	
	public LDAPackageDocument (IPackageFragment pack) {
		this.pack = pack;
	}
	
	/**
	 * Returns the display name of the document.
	 * 
	 * @return display name
	 */
	public String getName() {
		if (pack.isDefaultPackage())
			return "Default";
		else
			return pack.getElementName();
	}
	
	public String getFullName() {
		return pack.getHandleIdentifier();
	}
	
	/**
	 * Returns the contents of the document as a string.
	 * 
	 * @return Document contents.
	 */
	public String getContents() {
		try {
			//System.out.println("package: "+pack.getElementName());
			//for (ICompilationUnit unit : pack.getCompilationUnits()) 
			//	System.out.println("\t"+ unit.getElementName());
			
			return getContentsFromAST(pack.getCompilationUnits(), null, pack);
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
		return "";
		/*
		try {
			String source = "";
			for (ICompilationUnit cu : pack.getCompilationUnits()) {
				source += cu.getSource();
			}
			return source;
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
		return "";*/
	}
}
