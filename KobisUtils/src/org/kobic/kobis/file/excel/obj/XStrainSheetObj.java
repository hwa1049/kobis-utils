package org.kobic.kobis.file.excel.obj;

import org.apache.ibatis.type.Alias;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.kobic.kobis.file.excel.obj.internal.AbstractSheetObj;
import org.kobic.kobis.file.excel.obj.internal.CultureObj;
import org.kobic.kobis.file.excel.obj.internal.DistPatentReferenceObj;
import org.kobic.kobis.file.excel.obj.internal.StoreObj;

@Alias("D1_Strain")
public class XStrainSheetObj extends AbstractSheetObj{
	private String source;
	private String pathogenicYn;
	private String strainName;
	private CultureObj culture;
	private StoreObj store;
	private DistPatentReferenceObj extra;

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getPathogenicYn() {
		return pathogenicYn;
	}

	public void setPathogenicYn(String pathogenicYn) {
		this.pathogenicYn = pathogenicYn;
	}

	public String getStrainName() {
		return strainName;
	}

	public void setStrainName(String strainName) {
		this.strainName = strainName;
	}

	public CultureObj getCulture() {
		return culture;
	}

	public void setCulture(CultureObj culture) {
		this.culture = culture;
	}

	public StoreObj getStore() {
		return store;
	}

	public void setStore(StoreObj store) {
		this.store = store;
	}

	public DistPatentReferenceObj getExtra() {
		return extra;
	}

	public void setExtra(DistPatentReferenceObj extra) {
		this.extra = extra;
	}

	@Override
	public XStrainSheetObj getInstance( XSSFRow row ) {
		XStrainSheetObj obj = new XStrainSheetObj();
		for(int i=row.getFirstCellNum(); i<=row.getLastCellNum(); i++) {
			if( i == 0 )		obj.setAccess_num( row.getCell(i).toString() );
			else if( i == 1 )	obj.setSource( row.getCell(i).toString() );
			else if( i == 2 )	obj.setPathogenicYn( row.getCell(i).toString() );
			else if( i == 3 )	obj.setStrainName( row.getCell(i).toString() );
			else if( i == 4 )	obj.getCulture().setCultureMediumName( row.getCell(i).toString() );
			else if( i == 5 )	obj.getCulture().setCondition( row.getCell(i).toString() );
			else if( i == 6 )	obj.getCulture().setSucceedDt( row.getCell(i).toString() );
			else if( i == 7 )	obj.getCulture().setSucceedTime( row.getCell(i).toString() );
			else if( i == 8 )	obj.getExtra().getDist().setDistYn( row.getCell(i).toString() );
			else if( i == 9 )	obj.getExtra().getDist().setDistUrl( row.getCell(i).toString() );
			else if( i == 10 )	obj.getExtra().getPatent().setParentNo( row.getCell(i).toString() );
			else if( i == 11 )	obj.getExtra().getPatent().setRegNo( row.getCell(i).toString() );
			else if( i == 12 )	obj.getExtra().getRef().setReference( row.getCell(i).toString() );
		}
		return obj;
	}

	@Override
	public String getPrintLine() {
		String line = this.getAccess_num() + ",";
		line += this.getSource() + ",";
		line += this.getPathogenicYn() + ",";
		line += this.getStrainName() + ",";
		line += this.getCulture().getCultureMediumName() + ",";
		line += this.getCulture().getCondition() + ",";
		line += this.getCulture().getSucceedDt() + ",";
		line += this.getCulture().getSucceedTime() + ",";
		line += this.getStore().getStorePlace() + ",";
		line += this.getStore().getStoreNo() + ",";
		line += this.getExtra().getDist().getDistYn() + ",";
		line += this.getExtra().getDist().getDistUrl() + ",";
		line += this.getExtra().getPatent().getParentNo() + ",";
		line += this.getExtra().getPatent().getRegNo() + ",";
		line += this.getExtra().getRef().getReference();
		
		return line;
	}
}
