package edu.wm.LDATopics.gui.TopicContentsView;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.jdt.core.Flags;
import org.eclipse.swt.graphics.Color;
import org.malnatij.svplugin.filters.Filter;
import org.malnatij.svplugin.graph.nodes.DVNode;
import org.malnatij.svplugin.graph.nodes.IClassNode;
import org.malnatij.svplugin.model.ClassRepresentation;
import org.malnatij.svplugin.util.Colors;
import org.malnatij.svplugin.views.XRayWorkbenchView;

import edu.wm.LDATopics.LDATopics;
import edu.wm.LDATopics.LDA.Topic;
import edu.wm.LDATopics.LDA.TopicMember;
import edu.wm.LDATopics.LDA.documents.LDAClassDocument;
import edu.wm.LDATopics.LDA.documents.LDADocument;
import edu.wm.LDATopics.gui.TwoDimensionalView;

/**
 * Node representing a CLASS in the dependency view layout
 * @author Jacopo Malnati
 *
 */
public class TreeClassNode extends TreeNode
{
	private LDAClassDocument classInNode = null;
	 TwoDimensionalView container;
	private Integer codeSizeCache = null;
	 
	public TreeClassNode(LDAClassDocument currentClass,
			XRayWorkbenchView container) {
		super(container, new Dimension(CLASS_NODE_WIDTH, NODE_HEIGHT));
		classInNode = currentClass;
		this.container = (TwoDimensionalView) container;
		setNodeLayout();
	    setBackgroundAndBorder();
	    setNodeLabel();
	    setNodeSize();
	    addToolTip();
	    addListeners();
	}
	
	protected void setNodeLayout(){
		ToolbarLayout layout = new ToolbarLayout();
	    setLayoutManager(layout);
	}
	
	public void setHiddenNode(){
		//classInNode.setHiddenEntity(true);
		if(incomingConnection != null){
			incomingConnection.setVisible(false);
			incomingConnection.repaint();
		}
		setVisible(false);
		repaint();
	}
	
	public void setVisibleNode(){
		//classInNode.setHiddenEntity(false);
		if(incomingConnection != null){
			incomingConnection.setVisible(true);
			incomingConnection.repaint();
		}
		setVisible(true);
		repaint();
	}
	
	public boolean isLeaf(ArrayList<Filter> filters){
	//	return !classInNode.hasChildren(filters);
		return true; // classes are always leafs
	}
	
	protected void addToolTip(){
		// TODO, 2010: this fast enough? seems like it'd be a bit slow.
		String otherTopics = "";
		DecimalFormat round = new DecimalFormat("#.##");
		
		for (Topic topic : LDATopics.map.getClassMap().topics)
			if (topic.getNumber() != LDATopics.twoDimensionalView.getTopicShown()) {
				TopicMember doc = topic.getDocumentByName(classInNode.getName());
			
				if (topic.getDocumentByName(classInNode.getName()) != null)
					otherTopics += "\nProbability for topic "+topic.getBestWords()+": "+round.format(doc.probability);
			}
		String hellingers = "";
		if ((classInNode.hellingerDistance != -1.0) && LDATopics.map.getClassMap().filterUsingQuery)
			hellingers = "\nHellingers Distance from query: "+round.format(classInNode.hellingerDistance);
			
		// TODO, 2010: why does it use ldaclassdoc, not topic member? does this work properly when 1 class belongs to
		// multiple topics? or does it only show one probabilty for all the topics??
		this.setToolTip(new Label("Class: "+classInNode.toString()+"\nProbability for this topic: "+round.format(classInNode.probability)+
								otherTopics+"\nMWE Cohesion: "+round.format(classInNode.getMWECohesion())+		
		hellingers));
	}
	
	protected void setNodeLabel(){
		Label name = new Label(classInNode.getName());
	    add(name);
	}
	
	protected void setNodeSize(){
		setSize(CLASS_NODE_WIDTH, NODE_HEIGHT);
	}
	
	protected void addListeners(){
		this.addMouseListener(new MouseListener(){
			@SuppressWarnings("unused")
			public void mouseClicked(MouseEvent e) {}
			public void mouseDoubleClicked(MouseEvent arg0) {
			//	addToSelectedNodes(false);
				//openAllSelectedNodes();
				classInNode.openInEditor();
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
	
	public LDAClassDocument getClassRepresentation(){
		return classInNode;
	}
	
	protected void setBackgroundAndBorder(){
		border = new LineBorder(getDefaultBorderColor(), 1);
		setBorder(border);
		setBackgroundColor(getAppropriateNodeColor());
		setOpaque(true);
	}
	
	protected Color getAppropriateNodeColor() {
		Color theColor = null;
		Double mwe = classInNode.getMWECohesion();
		
		if (mwe < 0)
			mwe = 0.0;
		if (mwe > 1) 
			mwe = 1.0;
		Float mweF = new Float(mwe);
		java.awt.Color heatColor = new java.awt.Color(
				java.awt.Color.HSBtoRGB(.0f+.7f*mweF, .7f, .7f));
// TODO, 2010: make heatmapping an option?
		return  new Color(null, heatColor.getRed(), heatColor.getGreen(), heatColor.getBlue());
				// (int)(200*mwe), (int)(163*mwe), (int)(163*mwe));
		//		int flag = classInNode.getFlag();
//	
//		if(Flags.isAbstract(flag)){
//			theColor = Colors.ABSTRACT_COLOR;
//		} else if (Flags.isInterface(flag)){
//			theColor = Colors.INTERFACE_COLOR;
//		} else {
//			theColor = Colors.CONCRETE_COLOR;
//		}
		//return Colors.GRAY;
	}

	public String getName() {
		return classInNode.getName();
	}

	@Override
	public boolean isRoot(ArrayList<Filter> filters) {
		//return classInNode.isRoot(filters);
		return false;
	}

	@Override
	public double getTreeNodeSize() {
//		if (codeSizeCache == null)
//			codeSizeCache = classInNode.getSize();
//		return codeSizeCache;
		return Math.pow(classInNode.probability,2);
	}
	
	public String toString(String prefix) {
		return prefix + this.getName();
	}
	
	public String toString() {
		return this.getName();
	}

}
