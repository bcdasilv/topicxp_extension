package org.malnatij.svplugin.views.actions;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import org.malnatij.svplugin.SVPluginActivator;
import org.malnatij.svplugin.XRay;

public class BrowserContentProvider extends XRay{
	private static String helpPath = "html" 
		+ System.getProperty("file.separator") + "regex.html";
	private static BrowserContentProvider theInstance;
	private static String regExHelp = "";
	
	private BrowserContentProvider(){
		acquireRegExHelp();
	}
	
	public static BrowserContentProvider getInstance(){
		if(theInstance == null){
			theInstance = new BrowserContentProvider();
		}
		return theInstance;
	}
	
	public String getRegExHelp(){
		return regExHelp;
	}
	
	@SuppressWarnings("deprecation")
	private void acquireRegExHelp(){
		StringBuffer contents = new StringBuffer();
		BufferedReader input = null;
		try {
			URL installURL = SVPluginActivator.getDefault().getDescriptor().getInstallURL();
			URL path = new URL(installURL, helpPath);
			input = new BufferedReader(new InputStreamReader(path.openStream()));
			String line = null;
			while ((line = input.readLine()) != null){
				contents.append(line);
				contents.append(System.getProperty("line.separator"));
			}
			regExHelp = contents.toString();
		} catch (Exception e){
			regExHelp = "Error while reaching " + helpPath + "\n" + e.getLocalizedMessage();
		}
		
	}

}
