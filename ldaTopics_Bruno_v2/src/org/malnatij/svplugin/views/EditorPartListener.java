package org.malnatij.svplugin.views;

import java.util.ArrayList;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.texteditor.ITextEditor;
import org.malnatij.svplugin.XRay;
import org.malnatij.svplugin.filters.Filter;
import org.malnatij.svplugin.model.ClassRepresentation;
import org.malnatij.svplugin.util.Log;


public class EditorPartListener extends XRay
implements IPartListener {

	private XRayWorkbenchView view;
	private ArrayList<Filter> filters;

	
	public EditorPartListener(XRayWorkbenchView view, ArrayList<Filter> filters){
		this.view = view;
		this.filters = filters;
	}

	
	public void partActivated(IWorkbenchPart part) {
		try{
			if(part instanceof ITextEditor) {
				ITextEditor editor = (ITextEditor) part;
				IEditorInput in = editor.getEditorInput();
				if (in instanceof IFileEditorInput) {
					IFileEditorInput editorInput = (IFileEditorInput) in;
					if (JavaCore.isJavaLikeFileName(editorInput.getFile().getName())) {
						Log.printEditorListener("Swich tab");
						ICompilationUnit unit =
							JavaCore.createCompilationUnitFrom(editorInput.getFile());
						IType type = unit.findPrimaryType();
						
						if (type != null) {
							String className = type.getElementName();
							Log.printEditorListener("switched on: " + className);
							String packageName =
								type.getPackageFragment().getElementName();
							ClassRepresentation theClass = 
								view.getModeledProject().
								getClassRepresentationFor(className, packageName);
							if(theClass != null && !theClass.isHiddenEntity()){
								view.centerViewOnClass(theClass);
								view.handleDependencyLinkRequest(theClass, filters);
							}
						}
					}
				}			
			}
		} catch (Exception e){
			//e.printStackTrace();
		}

	}
	

	public void partBroughtToTop(IWorkbenchPart part) {}

	public void partClosed(IWorkbenchPart part) {}

	public void partDeactivated(IWorkbenchPart part) {}

	public void partOpened(IWorkbenchPart part) {
		
	}

}
