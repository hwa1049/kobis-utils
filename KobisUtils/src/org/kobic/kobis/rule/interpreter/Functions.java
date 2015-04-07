package org.kobic.kobis.rule.interpreter;

import org.kobic.kobis.util.Utils;

public class Functions {
	public static String REPLACE(String[] list)  throws Exception{ 
		String ret = Utils.removeQuot(list[0], KeywordDictionary.DBL_QUOTATION ).trim().replace(
					Utils.removeQuot(list[1].trim(), KeywordDictionary.DBL_QUOTATION )
					,
					Utils.removeQuot(list[2].trim(), KeywordDictionary.DBL_QUOTATION)
				);
		
		return ret;
	}

	public static String SUBSTR(String[] list) throws Exception{
		return Utils.removeQuot(list[0].trim(), KeywordDictionary.DBL_QUOTATION).substring( 
				Integer.valueOf( Utils.removeQuot(list[1].trim(), KeywordDictionary.DBL_QUOTATION )  )
				, 
				Integer.valueOf(Utils.removeQuot(list[2].trim(), KeywordDictionary.DBL_QUOTATION ))
		);
	}

	public static String TRIM(String... list)  throws Exception{
		String ret  = Utils.removeQuot( list[0].trim(), KeywordDictionary.DBL_QUOTATION ).trim();
		return ret;
	}
	
	public static String CONTAINS(String... list)  throws Exception{
		String param1 = Utils.removeQuot( list[0].trim(), KeywordDictionary.DBL_QUOTATION ).trim();
		String param2 = Utils.removeQuot( list[1].trim(), KeywordDictionary.DBL_QUOTATION ).trim();

		if( param1.contains( param2 ) )	return "true";
		return "false";
	}
	
	public static String STARTWITH(String[] list) throws Exception{
		String param1 = Utils.removeQuot( list[0].trim(), KeywordDictionary.DBL_QUOTATION ).trim();
		String param2 = Utils.removeQuot( list[1].trim(), KeywordDictionary.DBL_QUOTATION ).trim();
		if( param1.toUpperCase().startsWith( param2.toUpperCase() ) )	return "true";
		return "false";
	}
	
	public static String CONCAT(String[] list) throws Exception {
		String ret="";
		for(String str:list) {
			ret += str.trim().replace("\"", "");
		}
		return KeywordDictionary.DBL_QUOTATION + ret + KeywordDictionary.DBL_QUOTATION;
	}
}