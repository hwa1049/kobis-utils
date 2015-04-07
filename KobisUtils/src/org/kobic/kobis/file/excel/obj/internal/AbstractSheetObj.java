package org.kobic.kobis.file.excel.obj.internal;

import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.kobic.kobis.util.Utils;

public abstract class AbstractSheetObj {
	private String access_num;

	public String getAccess_num() {
		return access_num;
	}

	public void setAccess_num(String access_num) {
		this.access_num = access_num;
	}
	
	public String getVal( XSSFCell cell ) {
		String ret = "";
		
		DataFormatter df = new DataFormatter();

//		if( cell != null ) ret = Utils.nullToEmpty( cell.toString() ).replace("\'", "\\\'");
		if( cell != null ) ret = Utils.nullToEmpty( df.formatCellValue(cell) ).replaceAll("'","\\\\'");
		
		return ret;
	}
	
	public abstract AbstractSheetObj getInstance(XSSFRow row);
	public abstract String getPrintLine();
}
