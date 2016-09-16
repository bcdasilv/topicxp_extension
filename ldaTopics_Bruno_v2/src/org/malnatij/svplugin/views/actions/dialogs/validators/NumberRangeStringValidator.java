package org.malnatij.svplugin.views.actions.dialogs.validators;

import java.util.StringTokenizer;

import org.malnatij.svplugin.views.actions.StringValidator;

public class NumberRangeStringValidator 
extends StringValidator{

	/**
	 * the format is NUMBER-NUMBER
	 */
	public String isValid(String newText) {
		String error = "Please, enter a range in" +
		" the format: A-B " +
		"where A <= B and they are integers. ";
		
		newText = newText.trim();
		
		Integer from;
		Integer to;
		
		try{
			if(newText.equals("")){
				return null;
			} else {
				StringTokenizer st = new StringTokenizer(newText, "-");
				
				if(st.hasMoreTokens()){
					from = new Integer(st.nextToken());
					
					if(st.hasMoreTokens()){
						to = new Integer(st.nextToken());
						
						if(from.intValue() <= to.intValue()){
							return null;
						}
					}
				}
			}
		}catch(Exception e){
			return error;
		}
		
		return error;
	}

}
