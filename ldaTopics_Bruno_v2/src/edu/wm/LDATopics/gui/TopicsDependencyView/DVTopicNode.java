package edu.wm.LDATopics.gui.TopicsDependencyView;

import java.awt.Container;
import java.util.ArrayList;
import java.util.StringTokenizer;

import org.eclipse.draw2d.Border;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.swt.graphics.Color;
import org.malnatij.svplugin.filters.Filter;
import org.malnatij.svplugin.graph.nodes.DVNode;
import org.malnatij.svplugin.graph.nodes.DVPackageNode;
import org.malnatij.svplugin.model.PackageRepresentation;
import org.malnatij.svplugin.util.Colors;
import org.malnatij.svplugin.views.XRayWorkbenchView;

import edu.wm.LDATopics.LDATopics;
import edu.wm.LDATopics.LDA.Topic;
import edu.wm.LDATopics.LDA.TopicMember;
import edu.wm.LDATopics.gui.TwoDimensionalView;

public class DVTopicNode extends DVNode {
	
	private Topic topicInNode = null;
	private TwoDimensionalView view;
	public DVTopicNode(Topic currentTopic,
			XRayWorkbenchView container) {
		super(container, new Dimension(PACKAGE_NODE_WIDTH, NODE_HEIGHT));
		topicInNode = currentTopic;
		view = (TwoDimensionalView) container;
		setNodeLayout();
	    setBackgroundAndBorder();
	    setNodeLabel();
	    setNodeSize();
	    addToolTip();
	    addListeners();
	}
	
	public void setHiddenNode(){
		//packageInNode.setHiddenEntity(true);
		if(incomingConnection != null){
			incomingConnection.setVisible(false);
			incomingConnection.repaint();
		}
		setVisible(false);
		repaint();
	}
	
	public void setVisibleNode(){
	//	packageInNode.setHiddenEntity(false);
		if(incomingConnection != null){
			incomingConnection.setVisible(true);
			incomingConnection.repaint();
		}
		setVisible(true);
		repaint();
	}
	
	public Topic getTopicInNode(){
		return topicInNode;
	}

	@Override
	protected Color getAppropriateNodeColor() {
		return Colors.TOPIC_COLOR;
	}

	@Override
	protected void setBackgroundAndBorder() {
		border = new LineBorder(getDefaultBorderColor(), 1);
		setBorder(border);
		setBackgroundColor(getAppropriateNodeColor());
		setOpaque(true);
	}
	
	public void setForegroundColor(Color border){
		super.setForegroundColor(border);
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
			//	addToSelectedNodes(false);
			}
			public void mousePressed(MouseEvent arg0) {
				//addToSelectedNodes(true);
			//	System.out.println("clicked topic:"+getTopicInNode().getName());
				// Transition to topic contents view
				view.viewTopicContents(getTopicInNode());
			}
			public void mouseReleased(MouseEvent arg0) {}
		});
	}

	@Override
	protected void addToolTip() {
		this.setToolTip(new Label(topicInNode.getName() + "\n" + topicInNode.getBestWords()));
	}

	@Override
	protected void setNodeLabel() {
		//TODO, 2010: put something else in here instead of name?
//		Label name = new Label(topicInNode.getName());
//		add(name);
		
		Label words = new Label(topicInNode.getBestWords());
		add(words);
			
		TopicMember[] docs= topicInNode.getBestDocuments();
		TopicMember[] classDocs= LDATopics.getMap().getClassMap().topics[topicInNode.getNumber()-1].getBestDocuments();
		int count = 0;
		for (TopicMember doc : docs) {
			if ((doc != null) && (count < 3)) {
				Label label = new Label(doc.name);
				add(label);
				count++;
			}
		}
		
		// If there weren't packages available, list some classes instead.
		for (TopicMember doc : classDocs) {
			if ((doc != null) && (count < 3)) {
				Label label = new Label(doc.name);
				add(label);
				count++;
			}
		}
//		if (docs[0] != null) {
//			Label doc1 = new Label(docs[0].name);
//			add(doc1);
//		}		
//		
//		if (docs[1] != null) {
//			Label doc2 = new Label(docs[1].name);
//			add(doc2);
//		}
//			
//		if (docs[2] != null) {
//			Label doc3 = new Label(docs[2].name);
//			add(doc3);
		//}
			
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
		return topicInNode.getName();
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
