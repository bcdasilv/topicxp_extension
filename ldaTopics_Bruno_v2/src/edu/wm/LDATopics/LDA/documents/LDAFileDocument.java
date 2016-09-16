package edu.wm.LDATopics.LDA.documents;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.io.StringReader;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

public class LDAFileDocument extends LDADocument {

	IPath path;
	
	public LDAFileDocument (String path) {
		this.path = new Path(path);
	}
	
	/**
	 * Returns the display name of the document.
	 * 
	 * @return display name
	 */
	public String getName() {
		return path.lastSegment();
	}
	
	public String getFullName() {
		return path.toPortableString();
	}
	
	/**
	 * Returns the contents of the document as a string.
	 * 
	 * @return Document contents.
	 */
	public String getContents() {
		return "";
	}
	
	/**
	 * Returns a reader for the document's contents.
	 * 
	 * @return reader for document
	 */
	public Reader getReader() {
		try {
			return new FileReader(new File(path.toOSString()));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
}
