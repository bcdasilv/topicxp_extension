package edu.wm.LDATopics.gui.TableView;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.part.ViewPart;

import edu.wm.LDATopics.LDATopics;
import edu.wm.LDATopics.LDA.LDATopicMap;
import edu.wm.LDATopics.gui.actions.ChangeLDAOptionsAction;
import edu.wm.LDATopics.gui.actions.RegenerateAnalysisTableViewAction;

public class TableView extends ViewPart {
	
	private TableViewer viewer;
	
	private TableColumn nameColumn;
	private TableColumn probabilityColumn;
	
	private TableViewContentProvider contentProvider;
	//ProjectTopicMap map;
	
	@Override
	public void createPartControl(Composite parent) {
		IProject projects[] = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		
		// TODO: Stage 3, let user select which project to work with, default to currently open one perhaps
		
		//map = new ProjectTopicMap("jEdit");
		//System.out.println(map);
		//System.out.println("MWE Cohesion for jEdit.java: "+map.getMWECohesion("=jEdit/<org.gjt.sp.jedit{jEdit.java[jEdit"));
		LDATopics.setProject("jEdit");
		
//		map = new LDATopicMap("JGibbLDA-v.1.0");
//		System.out.println("MWE Cohesion for Estimator.java: "+map.getMWECohesion("=JGibLDA-v.1.0/src<jgibblda{Estimator.java[Estimator"));
//		
		
		viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL
				| SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.VIRTUAL);
		
		contentProvider = new TableViewContentProvider(this);
		contentProvider.setElements(LDATopics.getMap().getPackageMap());
		contentProvider.refreshTable();
		viewer.setContentProvider(contentProvider);
		//viewer.setContentProvider(new TopicViewContentProvider(this));
		viewer.setLabelProvider(new TableViewLabelProvider());
		viewer.setUseHashlookup(true);
		
		//getSite().setSelectionProvider(viewer);
		
		final Table table = viewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		createColumns(table);
		
		table.setSortColumn(nameColumn);
		table.setSortDirection(SWT.DOWN);
		//this.createTableSorter();	
		
		// Add elements to the action bars
		IActionBars lBars = getViewSite().getActionBars();
		fillLocalToolBar(lBars.getToolBarManager());
	}

	private void createColumns(Table table) {
		nameColumn = new TableColumn(table, SWT.LEFT);
		nameColumn.setText("Filename");
		nameColumn.setWidth(100);

		probabilityColumn = new TableColumn(table, SWT.LEFT);
		probabilityColumn.setWidth(100);
		probabilityColumn.setText("Probability");
	}
	
	/**
	 * Adds the action to the toolbar.
	 * 
	 * @param pManager
	 *            The toolbar manager.
	 */
	private void fillLocalToolBar(IToolBarManager pManager)
	{
		// TODO: Stage 3, add all sorts of buttons and options and such:
		//   - regenerate analysis
		//   - switch to a different project
		//   - export/import
		//   - preferences: per-analysis (LDA settings) and for the whole plugin (follow focus, etc)
		
		//pManager.add(new CombinationalSearch());
		//pManager.add(new VisualizeAction(getSite()));

		pManager.add(new ChangeLDAOptionsAction());
		pManager.add(new RegenerateAnalysisTableViewAction(contentProvider));
	}
	
	public TableViewer getViewer() {
		return viewer;
	}
	
	
	@Override
	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		if (viewer.getControl().isDisposed()) return;
		viewer.getControl().setFocus();
	}
}
