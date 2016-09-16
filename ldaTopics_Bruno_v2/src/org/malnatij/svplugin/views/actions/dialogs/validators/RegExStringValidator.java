package org.malnatij.svplugin.views.actions.dialogs.validators;

import java.util.regex.Pattern;

import org.malnatij.svplugin.views.actions.StringValidator;

public class RegExStringValidator extends StringValidator{

	public String isValid(String newText) {
		
		if(isEmptyText(newText)){
			return "Insert a non empty Regular Expression, please.";
		}
		
		try{
			Pattern.matches(newText, "");
		} catch (Exception e){
			return "Invalid Regular Expression, see the Help panel.";
		}
		
		return null;
	}

}
