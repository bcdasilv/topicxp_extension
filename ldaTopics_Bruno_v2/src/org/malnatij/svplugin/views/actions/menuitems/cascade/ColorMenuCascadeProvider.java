package org.malnatij.svplugin.views.actions.menuitems.cascade;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.malnatij.svplugin.util.Colors;
import org.malnatij.svplugin.views.XRayWorkbenchView;
import org.malnatij.svplugin.views.actions.menuitems.MenuItemWithView;
import org.malnatij.svplugin.views.actions.menuitems.providers.SelectColorMenuItemProvider;
import org.eclipse.swt.graphics.Color;

public class ColorMenuCascadeProvider extends MenuItemWithView{

	public ColorMenuCascadeProvider(XRayWorkbenchView viewContainer, Menu menu) {
		super(viewContainer, menu);
	}

	@Override
	public MenuItem getMenuItem() {
		MenuItem colorMenu = new MenuItem(menu, SWT.CASCADE);
		colorMenu.setText("Select by Color Tag...");
		colorMenu.setImage(getImageDescriptor("colorBorder.gif").createImage());
		
		Menu subMenuTag = new Menu(menu.getShell(), SWT.DROP_DOWN);
		colorMenu.setMenu(subMenuTag);
		
		ArrayList<Color> yellowColor = new ArrayList<Color>();
		yellowColor.add(Colors.YELLOW);
		new SelectColorMenuItemProvider(viewContainer, subMenuTag,
				yellowColor, "Select Yellow Nodes", "yellow.gif", true).
				getMenuItem();
		
		ArrayList<Color> greenColor = new ArrayList<Color>();
		greenColor.add(Colors.GREEN);
		new SelectColorMenuItemProvider(viewContainer, subMenuTag,
				greenColor, "Select Green Nodes", "green.gif", true).
				getMenuItem();
		
		ArrayList<Color> orangeColor = new ArrayList<Color>();
		orangeColor.add(Colors.ORANGE);
		new SelectColorMenuItemProvider(viewContainer, subMenuTag,
				orangeColor, "Select Orange Nodes", "orange.gif", true).
				getMenuItem();
		
		ArrayList<Color> magentaColor = new ArrayList<Color>();
		magentaColor.add(Colors.MAGENTA);
		new SelectColorMenuItemProvider(viewContainer, subMenuTag,
				magentaColor, "Select Magenta Nodes", "magenta.gif", true).
				getMenuItem();
		
		ArrayList<Color> untaggedColors = new ArrayList<Color>();
		untaggedColors.add(Colors.BLACK);
		untaggedColors.add(Colors.GRAY);
		new SelectColorMenuItemProvider(viewContainer, subMenuTag,
				untaggedColors, "Select Untagged (Black/Gray) Nodes", "black.gif", true).
				getMenuItem();
		
		ArrayList<Color> allColor = new ArrayList<Color>();
		allColor.add(Colors.YELLOW);
		allColor.add(Colors.GREEN);
		allColor.add(Colors.ORANGE);
		allColor.add(Colors.MAGENTA);
		new SelectColorMenuItemProvider(viewContainer, subMenuTag,
				allColor, "Select (All) Colored Nodes", "colored.gif", true).
				getMenuItem();
		
		new MenuItem(subMenuTag, SWT.SEPARATOR);
		
		new SelectColorMenuItemProvider(viewContainer, subMenuTag,
				yellowColor, "Deselect Yellow Nodes", "noyellow.gif", false).
				getMenuItem();
		
		new SelectColorMenuItemProvider(viewContainer, subMenuTag,
				greenColor, "Deselect Green Nodes", "nogreen.gif", false).
				getMenuItem();
		
		new SelectColorMenuItemProvider(viewContainer, subMenuTag,
				orangeColor, "Deselect Orange Nodes", "noorange.gif", false).
				getMenuItem();
		
		new SelectColorMenuItemProvider(viewContainer, subMenuTag,
				magentaColor, "Deselect Magenta Nodes", "nomagenta.gif", false).
				getMenuItem();
		
		new SelectColorMenuItemProvider(viewContainer, subMenuTag,
				allColor, "Deselect Colored Nodes", "nocolored.gif", false).
				getMenuItem();
		
		return colorMenu;
	}

}
