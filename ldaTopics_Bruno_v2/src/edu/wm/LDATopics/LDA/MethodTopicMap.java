package edu.wm.LDATopics.LDA;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;

import edu.wm.LDATopics.LDA.documents.LDADocument;
import edu.wm.LDATopics.LDA.documents.LDAMethodDocument;

public class MethodTopicMap extends LDATopicMap {

	
	public MethodTopicMap(String project, LDAOptions options) {
		super(project, options);
		// TODO Auto-generated constructor stub
	}

	public MethodTopicMap(String project, String basisModelName, LDAOptions options) {
		super(project, basisModelName, options);
	}

	@Override
	protected String getModelName() {
		return "methods";
	}
	
	/**
	 * Returns a list of documents for the methods in a project.
	 * @param project
	 */
	protected LDADocument[] getDocuments() {
		ArrayList<LDAMethodDocument> documentsList = new ArrayList<LDAMethodDocument>();
		try {
			IProject projectRep = ResourcesPlugin.getWorkspace().getRoot().getProject(project);
			IPackageFragment fragments[] = JavaCore.getJavaCore().create(projectRep).getPackageFragments();
			
			for (IPackageFragment fragment : fragments) { 
				ICompilationUnit classes[];
				classes = fragment.getCompilationUnits();

				// or get the fragments and then their path
				for (ICompilationUnit clas : classes) {
					//IJavaElement elements[] = (IJavaElement[])new StandardJavaElementContentProvider().getElements(JavaCore.getJavaCore().create(projectRep));
					IType types[] = clas.getTypes();
					for (IType type : types) {
						IJavaElement elements[] = type.getMethods(); 
						for (IJavaElement element : elements) {
							if (element instanceof IMethod) {
								documentsList.add(new LDAMethodDocument((IMethod)element));
								//System.out.println(element.getElementName());
							}
						}
					}
				}
			}
			
		} catch (JavaModelException e) {
			System.err.println("Error getting java code elements.");
			e.printStackTrace();
		}
		
		return (LDAMethodDocument[]) documentsList.toArray(new LDAMethodDocument[documentsList.size()]);		
	}
	
	
	/**
	 * Calculates a cohesion metric for a topic map of methods from classes
	 * @param clas
	 * @return cohesiveness
	 */
	public Double getMWECohesion(String clas) {
		
		// preconditions are that this is an already loaded topic map at the method level
		if (topics == null)
			return -1.0;
//		File f = new File("/Users/tcsava/log");
//		PrintStream s = null;
//		try {
//			s = new PrintStream(f);
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		s.println(clas);
		int numMethods = 0;
		ArrayList<Double> WE = new ArrayList<Double>();
		
		// for each topic, calculate O()*D()
		for (Topic topic : topics) {
			// Equation 3:
			double o = 0; 
			for ( TopicMember member : topic.allProbabilities) {
				// if member is from class called clas
				// TODO, 2010: This is taking class to == compilation unit, which is really the .java file!! is this correct?
				// I think that's what the whole plugin does  though; good/bad?
//				s.println("\t\t"+((LDAMethodDocument)member.document).getClassFullName());
				if (((LDAMethodDocument)member.document).getClassFullName().equals(clas)) {
					o += member.probability; // add up the probabilities of all the methods
					numMethods++; // count the number of methods while we're at it
				}
			}
			
			// Equation 5:
			double d = 0;
			for (TopicMember member : topic.allProbabilities) {
				if (((LDAMethodDocument)member.document).getClassFullName().equals(clas)) {
					double q = member.probability / o;
					d += -q * Math.log(q);
				}
			}
			o = o / numMethods;
			d = d / Math.log(numMethods);
			WE.add(o*d);
		}
//		s.println(numMethods);
		if (numMethods == 0) return -2.0; // Class didn't exist or had no methods in the map
		
		// get the max and return it		
		Double max = Double.MIN_VALUE;
		for (Double i : WE) {
			if ( i > max ) max = i;
		}
		return max;
	}
}
