package edu.wm.LDATopics.gui.TopicContentsView;

import java.awt.Container;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.swt.graphics.Color;
import org.malnatij.svplugin.filters.Filter;
import org.malnatij.svplugin.graph.nodes.DVNode;
import org.malnatij.svplugin.model.PackageRepresentation;
import org.malnatij.svplugin.util.Colors;
import org.malnatij.svplugin.views.XRayWorkbenchView;

import edu.wm.LDATopics.gui.TwoDimensionalView;

public class TreePackageNode extends TreeNode {
	
	private PackageRepresentation packageInNode = null;
    TwoDimensionalView container;
	private Double codeSizeCache;
	
	public TreePackageNode(PackageRepresentation currentPackage,
			XRayWorkbenchView container) {
		super(container, new Dimension(PACKAGE_NODE_WIDTH, NODE_HEIGHT));
		packageInNode = currentPackage;
		this.container = (TwoDimensionalView) container;
		setNodeLayout();
	    setBackgroundAndBorder();
	    setNodeLabel();
	    setNodeSize();
	    addToolTip();
	    addListeners();
	}

	public void setHiddenNode(){
		packageInNode.setHiddenEntity(true);
		if(incomingConnection != null){
			incomingConnection.setVisible(false);
			incomingConnection.repaint();
		}
		setVisible(false);
		repaint();
	}
	
	public void setVisibleNode(){
		packageInNode.setHiddenEntity(false);
		if(incomingConnection != null){
			incomingConnection.setVisible(true);
			incomingConnection.repaint();
		}
		setVisible(true);
		repaint();
	}
	
	public PackageRepresentation getPackageInNode(){
		return packageInNode;
	}

	@Override
	protected Color getAppropriateNodeColor() {
		return Colors.GRAY_LIGHT;
	}

	@Override
	protected void setBackgroundAndBorder() {
		border = new LineBorder(getDefaultBorderColor(), 1);
		setBorder(border);
		setBackgroundColor(getAppropriateNodeColor());
		setOpaque(true);
	}

	@Override
	protected void setNodeSize() {
		setSize(PACKAGE_NODE_WIDTH, NODE_HEIGHT);
	}

	@Override
	protected void addListeners() {
		this.addMouseListener(new MouseListener(){
			@SuppressWarnings("unused")
			public void mouseClicked(MouseEvent e) {
			}
			public void mouseDoubleClicked(MouseEvent arg0) {
				addToSelectedNodes(false);
			}
			public void mousePressed(MouseEvent arg0) {
				if (arg0.button == 1)
					addToSelectedNodes(true);

				if (arg0.button == 3)
					container.showTopicDependency();
			}
			public void mouseReleased(MouseEvent arg0) {}
		});
	}

	@Override
	protected void addToolTip() {
		this.setToolTip(new Label("Package:" + packageInNode.getName()));
	}

	@Override
	protected void setNodeLabel() {
		Label name = new Label(packageInNode.getName());
		name.setLabelAlignment(1);
		name.setLocation(new Point(1,0));
		name.setSize(200, 15); //TODO: unhardcode the 200 and such
	    add(name);
	}
	
	private String normalizeName(String name){
		if(name == null || name.equals("")){
			return "(default package)";
		} else {
			StringTokenizer st = new StringTokenizer(name, ".");
			String lastToken = "";
			while(st.hasMoreTokens()){
				lastToken = st.nextToken();
			}
			return lastToken;
		}
	}

	public String getName() {
		return packageInNode.getName();
	}
	
	/**
	 * Packages, so far, are flat, without hierarchy
	 */
	@Override
	public boolean isRoot(ArrayList<Filter> filters) {
		return false;
	}
	@Override
	public boolean isLeaf(ArrayList<Filter> filters){
		return false;
	}

	public boolean containsPackage(String pack) {
//		if (this.packageInNode.getName().equals(pack)) return true; // this is the package
//		
//		List children = getChildren();
//		for (Object child : children) {
//			if (child instanceof TreePackageNode) {
//				if (((TreePackageNode)child).containsPackage(pack)) return true; // child contains package
//			}
//		}
//		return false;
		return (getPackageNode(pack) != null);
	}

	public TreePackageNode getPackageNode(String pack) {
		if (this.packageInNode.getName().equals(pack)) return this; // this is the package
		
		List children = getChildren();
		for (Object child : children) {
			if (child instanceof TreePackageNode) {
				TreePackageNode result = ((TreePackageNode)child).getPackageNode(pack);
				if (result != null) return result;
	
			}
		}
		return null;
	}

	public TreePackageNode getClosestParent(TreePackageNode newNode) {
		String name = newNode.getName();
		while (name.contains(".")) {
			name = name.substring(0, name.lastIndexOf("."));
			//System.out.println("looking for "+name);
			
			TreePackageNode parent = getPackageNode(name);
			if (parent != null) return parent;
		}
		return this; // default package
	}

	public String toString() {
		return toString("");
	}
	
	public String toString(String prefix) {
		String str = prefix + this.getName();
		
		List children = getChildren();
		for (Object child : children) {
			if (child instanceof TreeNode)
				str += "\n" + ((TreeNode)child).toString(prefix+"  ");
		}
		return str;
	}
	
	@Override
	public double getTreeNodeSize() {
		if (codeSizeCache == null)	{
			Double total = 0.0;
			
			List children = getChildren();
			for (Object child : children) {
				if (child instanceof TreeNode)
					total += ((TreeNode)child).getTreeNodeSize();
			}
			codeSizeCache = total;
		}

		return codeSizeCache;
	}
}
