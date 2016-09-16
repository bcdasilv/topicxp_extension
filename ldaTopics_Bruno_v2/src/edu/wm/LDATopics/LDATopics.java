package edu.wm.LDATopics;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import edu.wm.LDATopics.LDA.LDAOptions;
import edu.wm.LDATopics.LDA.ProjectTopicMap;
import edu.wm.LDATopics.gui.TwoDimensionalView;


/**
 * The activator class controls the plug-in life cycle
 */
public class LDATopics extends AbstractUIPlugin {
	
	// The following ids and class names must match the ids and
	// names defined in the plugin.xml file *exactly*.  Don't
	// forget to update them when plugin.xml is modified.
	
	private static final String NAME_RESOURCE_CLASS = 
		"edu.wm.LDATopics.LDAResources";
	
	// The plug-in ID
	public static final String PLUGIN_ID = "edu.wm.topicxp";

	// The shared instance
	private static LDATopics plugin;

	// Resource bundle.
	private ResourceBundle resourceBundle;
	
	// The map of the project we're currently analyzing.
	public static ProjectTopicMap map;
	
	public static TwoDimensionalView twoDimensionalView;

	
	
	/**
	 * The constructor
	 */
	public LDATopics() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		
		try
		{
			resourceBundle = ResourceBundle.getBundle(NAME_RESOURCE_CLASS);
		}
		catch (MissingResourceException e)
		{
			//ProblemManager.reportException(new Exception(
			System.err.println("Missing Resource file: " + NAME_RESOURCE_CLASS);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static LDATopics getDefault() {
		return plugin;
	}

	/**
	 * Returns the string from the plugin's resource bundle, or 'key' if not
	 * found.
	 * 
	 * @param pKey
	 *            The key to use for the property lookup
	 * @return A string representing the resource.
	 */
	public static String getResourceString(String pKey)
	{
		final ResourceBundle lBundle = LDATopics.getDefault()
				.getResourceBundle();
		try
		{
			return (lBundle != null) ? lBundle.getString(pKey) : pKey;
		}
		catch (MissingResourceException lException)
		{
			return pKey;
		}
	}
	
	/**
	 * @return The plugin's resource bundle
	 */
	public ResourceBundle getResourceBundle()
	{
		return resourceBundle;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

	public static ProjectTopicMap getMap() {
		return map;
	}
	

	public static void setProject(IProject projectToScan) {
		setProject(projectToScan.getName());	
	}
	
	public static void setProject(String projectToScan) {
		setProject(projectToScan, new LDAOptions());	
	}
	
	public static void setProject(IProject projectToScan, LDAOptions options) {
		setProject(projectToScan.getName(),options);
	}
	
	public static void setProject(String projectToScan, LDAOptions options) {
		map = new ProjectTopicMap(projectToScan, options);	
	}
	
	
	/** 
	 * Initializes a preference store with default preference values 
	 * for this plug-in.
	 */
	protected void initializeDefaultPreferences(IPreferenceStore store) {
		store.setDefault("helpShown", false);
//		Color color= Display.getDefault().getSystemColor(DEFAULT_HIGHLIGHT);
//		PreferenceConverter.setDefault(store,  HIGHLIGHT_PREFERENCE, color.getRGB());

	}
	
}
