package org.kobic.kobis.rule.interpreter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.kobic.kobis.rule.dao.RuleDAOService;
import org.kobic.kobis.rule.vo.RuleParamObj;
import org.kobic.kobis.rule.vo.RuleQueryVO;
import org.kobic.kobis.util.Utils;

public class LexicalInterpreter {
	private static Logger logger = Logger.getLogger(LexicalInterpreter.class);

	private static LexicalInterpreter interpreter;
	
	public static LexicalInterpreter getInstance() {
		if( interpreter == null )	return new LexicalInterpreter();
		
		return interpreter;
	}

	public boolean interpret( RuleQueryVO vo, RuleParamObj param ) throws NoSuchMethodException, SecurityException, Exception {
		vo.setRule_condition( this.bindingParams(vo.getRule_condition(), param) );
		vo.setRule( this.bindingParams(vo.getRule(), param) );

		boolean ret = this.isTrueConditionStatement( vo.getRule_condition() );
		if( ret ) {
			this.processRuleStatement( vo.getRule(), param );
		}

		return true;
	}

	private void processRuleStatement( String statement, RuleParamObj param ) {
		String pattern = "set\\s+\\:([a-zA-Z0-9|\\_]*)\\s*\\=\\s*(.+)";
		Pattern p = Pattern.compile( pattern );
		Matcher m = p.matcher( statement );

		while( m.find() ) {
			String variable = m.group(1);

			String value = m.group(2).replace("\"", "").replace(";", "");

			param.addParam( variable, value);
		}
	}

	private List<Integer> getAllConjunctionsInConditionStatement(String condition) {
		StringTokenizer st = new StringTokenizer(condition);

		List<Integer> opList = new ArrayList<Integer>();
		while (st.hasMoreElements()) {
			String op = (String) st.nextElement();
			if( KeywordDictionary.getInstance().conjunctions.containsKey( op ) ) {
				int opcode = KeywordDictionary.getInstance().conjunctions.get(op);
				opList.add(opcode);
			}
		}
		return opList;
	}

	private boolean compare(String oper1, String oper2, int opcode) throws Exception{
		if( opcode == KeywordDictionary.EQUAL ) {
			return oper1.equals(oper2);
		}else if( opcode == KeywordDictionary.NOT_EQUAL ){
			return !oper1.equals(oper2);
		}else{
			throw new RuntimeException("String variable only compare equal or not-equal");
		}
	}

	private boolean compare(Boolean oper1, Boolean oper2, int opcode) throws Exception{
		if( opcode == KeywordDictionary.EQUAL ) {
			return oper1.equals(oper2);
		}else if( opcode == KeywordDictionary.NOT_EQUAL ){
			return !oper1.equals(oper2);
		}else{
			throw new RuntimeException("Boolean variable only compare equal or not-equal");
		}
	}
	
	private boolean compare(Double oper1, Double oper2, int opcode) throws Exception{
		if( opcode == KeywordDictionary.EQUAL ) {
			return oper1 == oper2;
		}else if( opcode == KeywordDictionary.NOT_EQUAL ){
			return oper1 != oper2;
		}else if( opcode == KeywordDictionary.LESS_THAN ){
			return oper1 < oper2;
		}else if( opcode == KeywordDictionary.GRATER_THAN ){
			return oper1 > oper2;
		}else if( opcode == KeywordDictionary.LESS_OR_EQUAL ){
			return oper1 <= oper2;
		}else if( opcode == KeywordDictionary.GRATER_OR_EQUAL ){
			return oper1 >= oper2;
		}
		return false;
	}
	
	private boolean isTrueConditionStatement( String condition ) throws Exception{
		String[] operations = condition.split("AND|OR");

		List<Integer> opList = this.getAllConjunctionsInConditionStatement(condition);
		List<Boolean> booleanCompare = new ArrayList<Boolean>();

		boolean ret = true;
		for(int i=0; i<operations.length; i++) {
			KeywordDictionary.getInstance();

//			String pattern = "([a-zA-Z0-9|\"|\\.|\\s]*)\\s+([" + KeywordDictionary.getOperatorString() + "]+)\\s+([a-zA-Z0-9|\"|\\.|\\s]*)";
			String pattern = "(.*)\\s+([" + KeywordDictionary.getOperatorString() + "]+)\\s+(.*)";
			
			Pattern p = Pattern.compile( pattern );
			Matcher m = p.matcher( operations[i] );
			
			if( m.find() ) {
				if( m.groupCount() != 3 ) throw new RuntimeException( "잘못된 문장입니다. [oprand] operator [operand] : " + operations[i] );
				String oper1 = m.group(1).trim();
				String oper2 = m.group(3).trim();
				int opcode = Integer.valueOf( KeywordDictionary.getInstance().operators.get( m.group(2) ) );

				if( Utils.isCharacterSequence(oper1, KeywordDictionary.DBL_QUOTATION) && Utils.isCharacterSequence(oper2, KeywordDictionary.DBL_QUOTATION) )
					booleanCompare.add( this.compare(oper1, oper2, opcode) );
				else if( Utils.isNumeric( oper1 ) && Utils.isNumeric( oper2 ) )
					booleanCompare.add( this.compare( Double.valueOf( oper1 ), Double.valueOf( oper2 ), opcode) );
				else if( Utils.isBoolean(oper1) && Utils.isBoolean(oper2) )
					booleanCompare.add( this.compare( Boolean.valueOf(oper1), Boolean.valueOf( oper2 ), opcode) );
				else
					throw new RuntimeException("비교할 수 없는 문장입니다. 문자열끼리 비교 또는 숫자끼리 비교만 가능합니다. " + operations[i] );
			}
		}

		if( opList.size() > 0 ) {
			for(int i=0; i<opList.size(); i++) {
				int opcode = opList.get(i);
				if( i == 0 )	ret = true & booleanCompare.get(i);
				else{
					if( opcode == KeywordDictionary.AND )	ret &= booleanCompare.get(i);
					else									ret |= booleanCompare.get(i);
				}
			}
		}else {
			ret = booleanCompare.get(0);
		}

		return ret;
	}

	private String[] functionParameters(String str) {
		String[] div = str.trim().split(",");
		
		return div;
	}

	private String functionTranslate( String statement ) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
//		String pattern = "([a-zA-Z0-9]+)\\(([\"|\\'|\\s|a-zA-Z0-9|\\,|\\.|\\-]*)\\)";
		String pattern = "([a-zA-Z0-9]+)\\((.*)\\)";
		Pattern p = Pattern.compile( pattern );
		Matcher m = p.matcher( statement );

		while( m.find() ) {
			String methodName= m.group(1);
			String[] params = this.functionParameters( m.group(2) );

			Method method = Functions.class.getMethod( methodName, new Class[]{String[].class} );
			Object result = method.invoke( methodName, new Object[]{params} );
			
			if( result instanceof String )	{
				if( result.toString().toLowerCase().equals("true") || result.toString().toLowerCase().equals("false") )	
					result = result.toString();
				else
					result = KeywordDictionary.DBL_QUOTATION + result.toString() + KeywordDictionary.DBL_QUOTATION ;
			}
			statement = statement.replace( m.group(), result.toString() );
		}

		return statement;
	}

	private String variableTranslate(String statement, RuleParamObj param) {
		String pattern = "\\$\\{\\s*[a-z|A-Z|\\_|0-9]+\\s*\\}";
		Pattern p = Pattern.compile( pattern );
		Matcher m = p.matcher( statement );

		String condition = statement;
		while( m.find() ) {
			String oldStr = m.group();
			String removeBrace = m.group().replace("$",  "").replace("{", "").replace("}", "");
			String newStr = Utils.wrap( Utils.nullToEmpty( param.getParam(removeBrace) ), KeywordDictionary.DBL_QUOTATION );
			
			condition = condition.replace( oldStr, newStr );
		}
		condition = condition.replace( KeywordDictionary.SNGL_QUOTATION, KeywordDictionary.DBL_QUOTATION );
		
		return condition;
	}

	private String bindingParams( String statement, RuleParamObj param ) {
		String variableTranslatedStatement = this.variableTranslate( statement, param );
		String fulctionTranslatedStatement;
		try {
			fulctionTranslatedStatement = this.functionTranslate(variableTranslatedStatement);

			return fulctionTranslatedStatement;
		} catch (NoSuchMethodException | SecurityException
				| IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
}
