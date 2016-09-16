package org.malnatij.svplugin.model.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.AssertStatement;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ConstructorInvocation;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.LabeledStatement;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.SwitchCase;
import org.eclipse.jdt.core.dom.SwitchStatement;
import org.eclipse.jdt.core.dom.SynchronizedStatement;
import org.eclipse.jdt.core.dom.TryStatement;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclarationStatement;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.WhileStatement;
import org.malnatij.svplugin.model.ClassRepresentation;
import org.malnatij.svplugin.model.ProjectRepresentation;
import org.malnatij.svplugin.util.Log;

public class DependencyBuilder
extends CoreModel
implements IProjectAnalyzer{
	// project to be parsed
	private IProject parsedProject = null;
	// project representation to be updated using info from parsing
	private ProjectRepresentation projectRepresentationToUpdate = null;
	// hashmap of ["package.class" -> classRepresentation]
	private HashMap<String, ClassRepresentation> allConcreteClasses =
		new HashMap<String, ClassRepresentation>();
	private IProgressMonitor monitor;

	public DependencyBuilder(IProject parsedProject,
			ProjectRepresentation projectRepresentationToUpdate, IProgressMonitor monitor){
		this.parsedProject = parsedProject;
		this.projectRepresentationToUpdate = projectRepresentationToUpdate;
		this.monitor = monitor;
		run();
	}

	public void run(){
		fillAllConcreteClasses();
		analyze();
	}

	public void fillAllConcreteClasses(){
		ArrayList<ClassRepresentation> classes = projectRepresentationToUpdate.getAllClasses();
		int size = classes.size();

		for(int i = 0; i < size; i++){
			ClassRepresentation currentClass = classes.get(i);
			// just handling classes internal to the project
			if(currentClass.isComplete()){
				String classRepresentationKey =
					currentClass.getPackageContainer().getName() +
					"." + currentClass.getName();

				allConcreteClasses.put(classRepresentationKey, currentClass);
				Log.printProjectParser("Added [" + classRepresentationKey +
						" -> " + currentClass.getName() + "]");
			}
		}
	}

	public void analyze(){
		IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();

		for(int i = 0; i < projects.length; i++){
			IProject currentProject = projects[i];
			if(currentProject.equals(parsedProject)
					&& currentProject.isOpen()){
				parseProject(currentProject);
				return;
			}

		}

		Log.printError("Unable to find: " + parsedProject.getName() + " while parsing");
	}

	private void parseProject(IProject project){
		String projectName = project.getName();
		Log.printProjectParser("Project: " + projectName + " : parsing");

		try {
			IResource[] content = project.members();
			for (int j = 0; j < content.length; j++){
				IResource currentContent = content[j];
				int currentType = currentContent.getType();
				switch(currentType){
				case IResource.FILE : fileHandler((IFile) currentContent);
				break;
				case IResource.FOLDER : folderHandler((IFolder) currentContent);
				break;
				default : unknownHandler(currentContent);
				}
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	public void fileHandler(IFile file){
		String fileExtension = file.getFileExtension();
		if(fileExtension != null && fileExtension.equalsIgnoreCase("java")){
			String fileNameAndExtension = file.getName();
			Log.printProjectParser("\n[JavaFile] " + fileNameAndExtension);
			IJavaElement element = JavaCore.create(file);
			ICompilationUnit compilationUnit = (ICompilationUnit) element;
			IType[] types;
			try {
				types = compilationUnit.getAllTypes();

				for (int i = 0; i < types.length; i++) {
					IType type = types[i];
					monitor.subTask("Analyzing dependencies for class: " + type.getElementName());
					parse(type);
					monitor.worked(1);
				}
			} catch (JavaModelException e) {
				Log.printError("Discarded (not available) file: " + file.getName());
				//e.printStackTrace();
			}

		}
	}

	public void unknownHandler(IResource unknownResource){
		Log.printProjectParser("[Unknown] " + unknownResource.getName());
	}

	public void folderHandler(IFolder folder){
		IResource[] folderContent;
		try {
			folderContent = folder.members();
			for(int i = 0; i < folderContent.length; i++){
				IResource currentResource = folderContent[i];
				int currentType = currentResource.getType();
				switch(currentType){
				case IResource.FILE : fileHandler((IFile) currentResource);
				break;
				case IResource.FOLDER : folderHandler((IFolder) currentResource);
				break;
				default : unknownHandler(currentResource);
				}
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}

	}

	@SuppressWarnings("unchecked")
	private void parse(IType currentClass){
		try{
			Log.printProjectParser("Going to parse: " + currentClass.getElementName());

			// creating the parser
			ASTParser parser = ASTParser.newParser(AST.JLS3);
			// get the bindings
			parser.setResolveBindings(true); 
			// parsing the source
			parser.setSource(currentClass.getCompilationUnit());
			// creating parse nodes
			CompilationUnit node = (CompilationUnit) parser.createAST(null);

			List typeList = node.types();

			for(int i = 0; i < typeList.size(); i++){
				TypeDeclaration currentType = (TypeDeclaration)typeList.get(i);
				// getting the method declaration within this class
				MethodDeclaration[] methods = currentType.getMethods();
				for(int j = 0; j < methods.length; j++){
					MethodDeclaration currentmethod = methods[j];
					// for every method, handle it
					processmethodDeclaration(currentmethod, currentClass);
				}
			}
		} catch (Exception e){
			//e.printStackTrace(); // nothing to process
		}
		
	}

	private void processmethodDeclaration(MethodDeclaration currentMethod, 
			IType currentClass){
		// getting the blocks inside the current method
		Block codeBlock = currentMethod.getBody();
		processBlock(codeBlock, currentClass);
	}

	@SuppressWarnings("unchecked")
	private void processBlock(Block codeBlock, IType currentClass){
		if(codeBlock != null){
			// if there is code..
			try{
				List statements = codeBlock.statements();
				if(statements != null){
					// if there are statements..
					for(int i = 0; i < statements.size(); i++){
						Statement currentStatement = (Statement) statements.get(i);
						processStatement(currentStatement, currentClass);
					}
				}

			} catch (Exception e){
				e.printStackTrace();
			}
		}
	}

	private void processStatement(Statement currentStatement, IType currentClass){
		if(currentStatement != null){
			switch(currentStatement.getNodeType()){
			case ASTNode.RETURN_STATEMENT: 
				processReturnStatement(
						(ReturnStatement) currentStatement, currentClass);
				break;

			case ASTNode.EXPRESSION_STATEMENT:
				processExpressionStatement(
						(ExpressionStatement) currentStatement, currentClass);
				break;

			case ASTNode.TRY_STATEMENT:
				processTryStatement((TryStatement) currentStatement, currentClass);
				break;

			case ASTNode.IF_STATEMENT:
				processIfStatement((IfStatement) currentStatement, currentClass);
				break;

			case ASTNode.FOR_STATEMENT:
				processForStatement((ForStatement) currentStatement, currentClass);
				break;

			case ASTNode.WHILE_STATEMENT:
				processWhileStatement(
						(WhileStatement) currentStatement, currentClass);
				break;

			case ASTNode.BLOCK:
				processBlock((Block) currentStatement, currentClass);
				break;

			case ASTNode.VARIABLE_DECLARATION_STATEMENT:
				processVariableDeclarationStatement(
						(VariableDeclarationStatement) currentStatement, currentClass);
				break;

			case ASTNode.SWITCH_STATEMENT:
				processSwitchStatement(
						(SwitchStatement) currentStatement, currentClass);
				break;

			case ASTNode.SYNCHRONIZED_STATEMENT:
				processSynchronizedStatement(
						(SynchronizedStatement) currentStatement, currentClass);
				break;

			case ASTNode.DO_STATEMENT:
				processDoStatement(
						(DoStatement) currentStatement, currentClass);
				break;

			case ASTNode.SWITCH_CASE:
				processSwitchCase((SwitchCase) currentStatement, currentClass);
				break;

			case ASTNode.ENHANCED_FOR_STATEMENT:
				processEnhancedForStatement(
						(EnhancedForStatement) currentStatement, currentClass);
				break;

			case ASTNode.ASSERT_STATEMENT:
				processAssertStatement(
						(AssertStatement) currentStatement, currentClass);
				break;

			case ASTNode.LABELED_STATEMENT:
				processLabeledStatement(
						(LabeledStatement) currentStatement, currentClass);
				break;

			case ASTNode.CONSTRUCTOR_INVOCATION:
				processConstructorInvocation(
						(ConstructorInvocation) currentStatement, currentClass);
				break;

			case ASTNode.TYPE_DECLARATION_STATEMENT:
				processTypeDeclarationStatement(
						(TypeDeclarationStatement) currentStatement, currentClass);
				break;

				// do nothing for the following statements/cases

			case ASTNode.SUPER_CONSTRUCTOR_INVOCATION: break;	
			case ASTNode.BREAK_STATEMENT: break;
			case ASTNode.EMPTY_STATEMENT: break;
			case ASTNode.THROW_STATEMENT: break;
			case ASTNode.CONTINUE_STATEMENT: break;

			default : System.err.println("\t" + currentStatement.getClass());
			}
		}
	}

	private void processTypeDeclarationStatement(TypeDeclarationStatement tds, IType currentClass){
		if(tds != null){
			ITypeBinding binding = tds.resolveBinding();
			processTypeBinding(binding, currentClass);
		}
	}

	private void processTypeBinding(ITypeBinding binding, IType currentClass){
		if(binding != null){
			String currentClassInfo =
				currentClass.getPackageFragment().getElementName() 
				+ "." + currentClass.getElementName();

			String declaringClassInfo =
				binding.getDeclaringClass().getPackage().getName()
				+ "." + binding.getDeclaringClass().getName();
			setUse(currentClassInfo, declaringClassInfo);
		}
	}

	@SuppressWarnings("unchecked")
	private void processConstructorInvocation(ConstructorInvocation ci, IType currentClass){
		if(ci != null){
			List expressions = ci.arguments();

			for(int i = 0; i < expressions.size(); i++){
				processExpression((Expression) expressions.get(i), currentClass);
			}

			IMethodBinding mb = ci.resolveConstructorBinding();
			processMethodBinding(mb, currentClass);

		}

	}

	private void processLabeledStatement(LabeledStatement ls, IType currentClass){
		if(ls != null){
			Statement body = ls.getBody();
			processStatement(body, currentClass);
		}
	}

	private void processEnhancedForStatement(EnhancedForStatement efs,
			IType currentClass){

		if(efs != null){
			Statement body = efs.getBody();
			Expression expression = efs.getExpression();

			processStatement(body, currentClass);
			processExpression(expression, currentClass);
		}
	}

	private void processDoStatement(DoStatement doStatement,
			IType currentClass){

		if(doStatement != null){
			Statement statement = doStatement.getBody();
			Expression doExpression = doStatement.getExpression();

			processStatement(statement, currentClass);
			processExpression(doExpression, currentClass);
		}
	}

	private void processWhileStatement(WhileStatement whileStatement,
			IType currentClass){

		if(whileStatement != null){
			Statement whileBody = whileStatement.getBody();
			Expression whileExpression = whileStatement.getExpression();

			processStatement(whileBody, currentClass);
			processExpression(whileExpression, currentClass);
		}
	}

	@SuppressWarnings("unchecked")
	private void processSwitchStatement(SwitchStatement switchStatement, IType currentClass){
		if(switchStatement != null){
			Expression switchExpression = switchStatement.getExpression();
			List switchStatements = switchStatement.statements();
			processExpression(switchExpression, currentClass);

			for(int i = 0; i < switchStatements.size(); i++){
				processStatement((Statement) switchStatements.get(i), currentClass);
			}
		}
	}

	private void processSwitchCase(SwitchCase switchCase, IType currentClass){
		Expression switchCaseExpression = switchCase.getExpression();
		processExpression(switchCaseExpression, currentClass);
	}

	@SuppressWarnings("unchecked")
	private void processForStatement(ForStatement forStatement, IType currentClass){
		if(forStatement != null){
			Statement forBody = forStatement.getBody();
			Expression forExpression = forStatement.getExpression();
			List initializerEpressions = forStatement.initializers();
			List updatersEpressions = forStatement.updaters();

			processStatement(forBody, currentClass);
			processExpression(forExpression, currentClass);

			for(int i = 0; i < initializerEpressions.size(); i++){
				processExpression((Expression)initializerEpressions.get(i), currentClass);
			}

			for(int i = 0; i < updatersEpressions.size(); i++){
				processExpression((Expression)updatersEpressions.get(i), currentClass);
			}
		}
	}

	private void processIfStatement(IfStatement ifStatement, IType currentClass){
		if(ifStatement != null){
			Expression ifExpression = ifStatement.getExpression();
			Statement elseStatement = ifStatement.getElseStatement();

			processExpression(ifExpression, currentClass);
			processStatement(elseStatement, currentClass);
		}
	}

	private void processExpressionStatement(ExpressionStatement currentExpression,
			IType currentClass){

		if(currentExpression != null){
			Expression expression = currentExpression.getExpression();
			if (expression != null){
				processExpression(expression, currentClass);
			}
		}	
	}

	private void processReturnStatement(ReturnStatement currentReturn, 
			IType currentClass){

		if(currentReturn != null){
			Expression expression = currentReturn.getExpression();
			if (expression != null){
				processExpression(expression, currentClass);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void processVariableDeclarationStatement(
			VariableDeclarationStatement currentVariableDeclaration, 
			IType currentClass){

		if(currentVariableDeclaration != null){
			List fragments = currentVariableDeclaration.fragments();

			for(int i = 0; i < fragments.size(); i++){
				processVariableDeclarationFragment(
						(VariableDeclarationFragment) fragments.get(i),
						currentClass);
			}
		}
	}

	private void processVariableDeclarationFragment(
			VariableDeclarationFragment fragment,
			IType currentClass){

		if(fragment != null){
			Expression initializer = fragment.getInitializer();
			processExpression(initializer, currentClass);
		}

	}

	@SuppressWarnings("unchecked")
	private void processTryStatement(TryStatement currentStatement,
			IType currentClass){

		if(currentStatement != null){
			Block tryBlock = currentStatement.getBody();
			List catchBlock = currentStatement.catchClauses();
			Block finallyBlock = currentStatement.getFinally();

			processBlock(tryBlock, currentClass);
			processBlock(finallyBlock, currentClass);

			for(int i = 0; i < catchBlock.size(); i++){
				processCatchClause((CatchClause) catchBlock.get(i), currentClass);
			}
		}
	}

	private void processCatchClause(CatchClause catchClause, IType currentClass){
		if(catchClause != null){
			Block catchBlock = catchClause.getBody();
			processBlock(catchBlock, currentClass);
		}
	}

	private void processSynchronizedStatement(
			SynchronizedStatement synchronizedStat, IType currentClass){

		if(synchronizedStat != null){
			Block synchronizedBlock = synchronizedStat.getBody();
			Expression syncExpression = synchronizedStat.getExpression();

			processBlock(synchronizedBlock, currentClass);
			processExpression(syncExpression, currentClass);
		}
	}

	private void processAssertStatement(AssertStatement as, IType currentClass){
		if(as != null){
			Expression expression = as.getExpression();
			Expression message = as.getMessage();

			processExpression(expression, currentClass);
			processExpression(message, currentClass);
		}
	}

	private void processExpression(Expression expression, IType currentClass){
		if(expression != null){
			if(expression instanceof MethodInvocation){
				MethodInvocation methodInv = (MethodInvocation)expression;

				IMethodBinding binding = methodInv.resolveMethodBinding();

				processMethodBinding(binding, currentClass);
			}
		}
	}

	private void processMethodBinding(IMethodBinding binding, IType currentClass){
		if(binding != null){
			String currentClassInfo =
				currentClass.getPackageFragment().getElementName() 
				+ "." + currentClass.getElementName();

			String declaringClassInfo =
				binding.getDeclaringClass().getPackage().getName()
				+ "." + binding.getDeclaringClass().getName();

			setUse(currentClassInfo, declaringClassInfo);
		}
	}

	private void setUse(String currentClassKey, String usedClassKey){
		ClassRepresentation currentClassRepresentation =
			allConcreteClasses.get(currentClassKey);

		ClassRepresentation declaringClassRepresentation =
			allConcreteClasses.get(usedClassKey);

		if(currentClassRepresentation == null){
			Log.printError("ProjectParser.setUse() not found: " + currentClassKey);
			return;
		}

		if(declaringClassRepresentation == null){
			Log.printProjectParser("external, discaring: " + usedClassKey);
			return;
		}

		Log.printProjectParser(currentClassRepresentation.getName() +
				" uses: " + declaringClassRepresentation.getName());

		currentClassRepresentation.addUsedClass(declaringClassRepresentation);
	}
}
