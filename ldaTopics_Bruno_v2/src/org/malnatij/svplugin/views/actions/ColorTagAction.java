package org.malnatij.svplugin.views.actions;

import java.util.ArrayList;

import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.malnatij.svplugin.util.Colors;
import org.malnatij.svplugin.views.XRayWorkbenchView;
import org.malnatij.svplugin.views.actions.menuitems.providers.ColorTagMenuItemProvider;
import org.malnatij.svplugin.views.actions.menuitems.providers.NormalizeBorderMenuItemProvider;
import org.malnatij.svplugin.views.actions.menuitems.providers.UntagMenuItemProvider;

public class ColorTagAction extends DropDownImageAction {

	public ColorTagAction(XRayWorkbenchView viewContainer) {
		super("Tag the current selected nodes with a color",
				viewContainer);
		
		setImageDescriptor(getImageDescriptor("colorBorder.gif"));

		createAndSetMenu();
	}
	
	private ArrayList<Color> getColorListFor(Color theColor){
		ArrayList<Color> c = new ArrayList<Color>();
		c.add(theColor);
		return c;
	}

	@Override
	protected void createAndSetMenu() {
		setMenuCreator(new IMenuCreator() {
			public Menu getMenu(Control parent) {
				Menu menu = new Menu(parent);
				
				new ColorTagMenuItemProvider(viewContainer, menu, "Yellow Tag",
						"yellow.gif", getColorListFor(Colors.YELLOW), true).getMenuItem();
				
				new ColorTagMenuItemProvider(viewContainer, menu, "Green Tag",
						"green.gif", getColorListFor(Colors.GREEN), true).getMenuItem();
				
				new ColorTagMenuItemProvider(viewContainer, menu, "Orange Tag",
						"orange.gif", getColorListFor(Colors.ORANGE), true).getMenuItem();
				
				new ColorTagMenuItemProvider(viewContainer, menu, "Magenta Tag",
						"magenta.gif", getColorListFor(Colors.MAGENTA), true).getMenuItem();
				
				new UntagMenuItemProvider(viewContainer, menu).getMenuItem();
				
			
				new MenuItem(menu, SWT.SEPARATOR);
				
				
				new NormalizeBorderMenuItemProvider(viewContainer, menu).getMenuItem();
				
				new ColorTagMenuItemProvider(viewContainer, menu, "Remove Yellow Tag",
						"noyellow.gif", getColorListFor(Colors.YELLOW), false).getMenuItem();
				
				new ColorTagMenuItemProvider(viewContainer, menu, "Remove Green Tag",
						"nogreen.gif", getColorListFor(Colors.GREEN), false).getMenuItem();
				
				new ColorTagMenuItemProvider(viewContainer, menu, "Remove Orange Tag",
						"noorange.gif", getColorListFor(Colors.ORANGE), false).getMenuItem();
				
				new ColorTagMenuItemProvider(viewContainer, menu, "Remove Magenta Tag",
						"nomagenta.gif", getColorListFor(Colors.MAGENTA), false).getMenuItem();
				
				
				return menu;
			}
			
			public Menu getMenu(Menu parent) {
				return null;
			}
			
			public void dispose() {

			}
		});
	}

}
