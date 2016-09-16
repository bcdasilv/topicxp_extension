package edu.wm.LDATopics.LDA.documents;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTRequestor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IBinding;

import edu.wm.LDATopics.LDA.ASTExplorerVisitorGranularityOfClasses;


public abstract class LDADocument {
	
	public double hellingerDistance = -1.0;
	
	/**
	 * Returns the display name of the document.
	 * 
	 * @return display name
	 */
	public String getName() {
		return "";
	}
	
	/**
	 * Returns a more unique identifier of the document.
	 * 
	 * @return display name
	 */
	public String getFullName() {
		return getName();
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
		return new StringReader(getContents());
	}
	
	
	/**
	 * Returns the contents of the document as a string.
	 * 
	 * @return Document contents.
	 */
	public String getContentsFromAST(ICompilationUnit classes[], IMethod method, IPackageFragment packge) {
		// Extract identifiers and comment text using the AST parser.
		//new ASTExplorerVisitorGranularityOfClasses().
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setProject(classes[0].getJavaProject());
		//parser.setResolveBindings(true);
		StringWriter strWriter =  new StringWriter();
		BufferedWriter out = new BufferedWriter(strWriter);
		parser.createASTs(classes, new String[0],new MyASTRequestor(out, method, packge), null);
		try {
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return strWriter.toString();
	}
	
	private class MyASTRequestor extends ASTRequestor {
			
			private BufferedWriter out;
			IMethod method;
			IPackageFragment packge;
			
			public MyASTRequestor(BufferedWriter out, IMethod method, IPackageFragment packge ) {
				this.out=out;
				this.method = method;
				this.packge = packge;
			}
			
			public void acceptAST(ICompilationUnit unit, CompilationUnit node) {
				try {
	
					// TreeItem treeItem=new TreeItem(treeControl,SWT.NONE);
					// treeItem.setText("node:"+node.toString());
					//if (Analyzer.this.monitor.isCanceled())	 throw new OperationCanceledException("Cancelled.");
					
					//if (monitor.isCanceled()) return;
					if (method != null)
						node.accept(new ASTExplorerVisitorGranularityOfClasses(null,out,method));
					else if (packge != null)
						node.accept(new ASTExplorerVisitorGranularityOfClasses(null,out,packge));
					else
						node.accept(new ASTExplorerVisitorGranularityOfClasses(null,out));
					
	
				} catch (Exception e) {
					ResourcesPlugin.getPlugin().getLog().log(new Status(IStatus.ERROR, "Method Locator", IStatus.ERROR, "Error while doing methods search", e));
				} finally {
					
				}
			}
	
			public void acceptBinding(String key, IBinding binding) {
				// Do nothing
			}
	}

	public String getPackage() {
		return "";
	}
}
