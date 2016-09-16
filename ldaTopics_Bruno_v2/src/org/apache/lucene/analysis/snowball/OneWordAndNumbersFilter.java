package org.apache.lucene.analysis.snowball;

import java.io.IOException;
import java.text.DecimalFormatSymbols;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;

/*
 *  Created by Bruno - June/2014
 *	Filter for removing one word tokens and numbers in source code	
 */

public class OneWordAndNumbersFilter extends TokenFilter {
	
	  	//private LinkedList<Token> listOfTokens = new LinkedList<Token>();
	  	
	  	
		public OneWordAndNumbersFilter(TokenStream incomingStreamOfTokens) {
		    super(incomingStreamOfTokens);
		  }

		public final Token next() throws IOException {
			//if (listOfTokens.size()>0) return listOfTokens.removeFirst();

			Token token = input.next();

//			if ((token == null) && (listOfTokens.size()==0))
//				return null;
		    if (token == null)
		        return null;			

			String text = token.termText();
			if ( (text.length() >= 2) && !(isStringNumeric(text)) )
				return token;

//			if (listOfTokens.size()<2) 
//				listOfTokens.clear(); 

			return this.next();
		}
	
		/*
		 * Solution extracted from StackOverFlow post:
		 * http://stackoverflow.com/questions/1102891/how-to-check-if-a-string-is-a-numeric-type-in-java 
		 * */
		private boolean isStringNumeric( String str )
		{
		    DecimalFormatSymbols currentLocaleSymbols = DecimalFormatSymbols.getInstance();
		    char localeMinusSign = currentLocaleSymbols.getMinusSign();

		    if ( !Character.isDigit( str.charAt( 0 ) ) && str.charAt( 0 ) != localeMinusSign ) return false;

		    boolean isDecimalSeparatorFound = false;
		    char localeDecimalSeparator = currentLocaleSymbols.getDecimalSeparator();

		    for ( char c : str.substring( 1 ).toCharArray() )
		    {
		        if ( !Character.isDigit( c ) )
		        {
		            if ( c == localeDecimalSeparator && !isDecimalSeparatorFound )
		            {
		                isDecimalSeparatorFound = true;
		                continue;
		            }
		            return false;
		        }
		    }
		    return true;
		}
}
