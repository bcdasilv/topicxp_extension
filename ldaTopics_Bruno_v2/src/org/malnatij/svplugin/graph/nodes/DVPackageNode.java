package org.malnatij.svplugin.graph.nodes;

import java.util.ArrayList;
import java.util.StringTokenizer;

import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.swt.graphics.Color;
import org.malnatij.svplugin.filters.Filter;
import org.malnatij.svplugin.model.PackageRepresentation;
import org.malnatij.svplugin.util.Colors;
import org.malnatij.svplugin.views.XRayWorkbenchView;

public class DVPackageNode extends DVNode {
	
	private PackageRepresentation packageInNode = null;

	public DVPackageNode(PackageRepresentation currentPackage,
			XRayWorkbenchView container) {
		super(container, new Dimension(PACKAGE_NODE_WIDTH, NODE_HEIGHT));
		packageInNode = currentPackage;
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
		return Colors.PACKAGE_COLOR;
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
			public void mouseClicked(MouseEvent e) {}
			public void mouseDoubleClicked(MouseEvent arg0) {
				addToSelectedNodes(false);
			}
			public void mousePressed(MouseEvent arg0) {
				addToSelectedNodes(true);
			}
			public void mouseReleased(MouseEvent arg0) {}
		});
	}

	@Override
	protected void addToolTip() {
		this.setToolTip(new Label(packageInNode.toString()));
	}

	@Override
	protected void setNodeLabel() {
		Label name = new Label(normalizeName(packageInNode.getName()));
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



}
