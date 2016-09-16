/**
 * 
 */
package edu.wm.LDATopics.gui.TableView;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import edu.wm.LDATopics.LDA.Topic;
import edu.wm.LDATopics.LDA.TopicMember;

public class TableViewLabelProvider extends LabelProvider implements	ITableLabelProvider {


	/**
	 * @param table
	 */
	public TableViewLabelProvider() {
		
	}

	public Image getColumnImage(Object element, int column) {
		if ((column!=0)) return null;
		return null;
		//return NodeImageRegistry.getImage((JRipplesEIGNode) element);
	}
	
	public Image getImage(Object element) {
		return getColumnImage( element,0);		
	}
	
	public String getColumnText(Object obj, int index) {

		switch (index) {
		case 0:
			if (obj instanceof Topic)
				return ((Topic) obj).getName();
			if (obj instanceof TopicMember)
				return  ((TopicMember) obj).name;
			return "";
		case 1:
			if (obj instanceof Topic)
				return "-----------------";
			if (obj instanceof TopicMember)
				return  Double.toString(((TopicMember) obj).probability);
			if (obj instanceof String) 
				return (String) obj;
			return "";
		default:
			return "";
		
		}

	}
}