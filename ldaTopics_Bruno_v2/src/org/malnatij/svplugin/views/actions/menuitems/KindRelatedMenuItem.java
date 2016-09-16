package org.malnatij.svplugin.views.actions.menuitems;

import org.eclipse.swt.widgets.Menu;
import org.malnatij.svplugin.views.XRayWorkbenchView;

public abstract class KindRelatedMenuItem extends NameAndIconMenuItem{
	public static final String PARENTS = "parents";
	public static final String CHILDREN = "children";
	public static final String SIBLING = "sibling";
	public static final String LEAF = "leaf";
	public static final String ROOT = "root";
	public static final String BRANCH = "branch";
	public static final String OUTGOING_DEP = "outgoing dep";
	public static final String INTERFACE_LINK = "interface link";
	public static final String HIDE = "hide";
	public static final String SHOW_HIDDEN = "show hidden";
	public static final String ZOOM_IN = "zoom in";
	public static final String MANUAL_ZOOM = "manual zoom";
	public static final String ZOOM_OUT = "zoom out";
	public static final String FIT_SIZE = "fit size";
	public static final String ORIGINAL_SIZE = "original size";
	public static final String PACKAGE_IN = "package in";
	public static final String PACKAGE_OUT = "package out";
	public static final String INCOMING_DEP = "incoming dep";
	public static final String INCOMING_OUTGOING_DEP = "incoming outgoing dep";
	public static final String HIDE_DEP = "hide dep";

	protected String kind;

	public KindRelatedMenuItem(XRayWorkbenchView viewContainer, Menu menu,
			String menuName, String iconName, String kind) {
		super(viewContainer, menu, menuName, iconName);
		this.kind = kind;
	}



}
