package edu.wm.LDATopics.LDA;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;

import edu.wm.LDATopics.LDA.documents.LDADocument;
import edu.wm.LDATopics.LDA.documents.LDAPackageDocument;

public class PackageTopicMap extends LDATopicMap {

	
	public PackageTopicMap(String project, LDAOptions options) {
		super(project, options);
	}

	public PackageTopicMap(String project, String basisModelName, LDAOptions options) {
		super(project, basisModelName, options);
	}

	@Override
	protected String getModelName() {
		return "packages";
	}
	
	/**
	 * Returns a list of documents for the Packages in a project.
	 * @param project
	 */
	public LDADocument[] getDocuments() {
		ArrayList<LDAPackageDocument> documentsList = new ArrayList<LDAPackageDocument>();
		try {
			IProject projectRep = ResourcesPlugin.getWorkspace().getRoot().getProject(project);
			IPackageFragment fragments[] = JavaCore.getJavaCore().create(projectRep).getPackageFragments();
		
			for (IPackageFragment fragment : fragments) { 
				// Checking if framgment exists because sometimes eclipse remembers old files...
				// Probably not necessary though, because it doesn't seem to remember old packages.
				if ((fragment.getCompilationUnits().length != 0) && (uriExists(fragment.getUnderlyingResource().getLocationURI()))) {
					LDAPackageDocument doc = new LDAPackageDocument(fragment);
					documentsList.add(doc);
			    	//System.out.println(doc.getName());
				}
			}
				
			
		} catch (JavaModelException e) {
			System.err.println("Error getting java code elements.");
			e.printStackTrace();
		}
		
		return (LDAPackageDocument[]) documentsList.toArray(new LDAPackageDocument[documentsList.size()]);		
	}
	

	
	private static boolean uriExists(URI uri) {
		try {
			uri.toURL().openStream();
		} catch (IOException Exception) {
			return false;
		}
		return true;
	}

}
