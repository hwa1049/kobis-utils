package org.kobic.kobis.file.excel.obj;

import org.apache.ibatis.type.Alias;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.kobic.kobis.file.excel.obj.internal.AbstractSheetObj;
import org.kobic.kobis.file.excel.obj.internal.DistPatentReferenceObj;
import org.kobic.kobis.file.excel.obj.internal.StoreObj;

@Alias("D1_Organ")
public class XOrganSheetObj extends AbstractSheetObj implements DistPatentReferenceInterface{
	private String organType;
	private String disease;
	private StoreObj store;
	private DistPatentReferenceObj extra;

	public String getOrganType() {
		return organType;
	}

	public void setOrganType(String organType) {
		this.organType = organType;
	}

	public String getDisease() {
		return disease;
	}

	public void setDisease(String disease) {
		this.disease = disease;
	}

	public StoreObj getStore() {
		if( this.store == null )	this.store = new StoreObj();
		return store;
	}

	public void setStore(StoreObj store) {
		this.store = store;
	}

	public DistPatentReferenceObj getExtra() {
		if( this.extra == null )	this.extra = new DistPatentReferenceObj();
		return extra;
	}

	public void setExtra(DistPatentReferenceObj extra) {
		this.extra = extra;
	}
	
	public static XOrganSheetObj getNewInstance(XSSFRow row) {
		return new XOrganSheetObj().getInstance(row);
	}

	@Override
	public XOrganSheetObj getInstance( XSSFRow row ) {
		XOrganSheetObj obj = new XOrganSheetObj();
		for(int i=row.getFirstCellNum(); i<=row.getLastCellNum(); i++) {
			if( i == 0 )		obj.setAccess_num(						this.getVal(row.getCell(i) ) );
			else if( i == 1 )	obj.setOrganType(						this.getVal(row.getCell(i) ) );
			else if( i == 2 )	obj.getExtra().getDist().setDistYn(		this.getVal(row.getCell(i) ) );
			else if( i == 3 )	obj.getExtra().getDist().setDistUrl(	this.getVal(row.getCell(i) ) );
			else if( i == 4 )	obj.getExtra().getPatent().setParentNo(	this.getVal(row.getCell(i) ) );
			else if( i == 5 )	obj.getExtra().getPatent().setRegNo(	this.getVal(row.getCell(i) ) );
			else if( i == 6 )	obj.getExtra().getRef().setReference(	this.getVal(row.getCell(i) ) );
		}
		return obj;
	}

	@Override
	public String getPrintLine() {
		String line = this.getAccess_num() + ",";
		line += this.getOrganType() + ",";
		line += this.getDisease() + ",";
		line += this.getStore().getStorePlace() + ",";
		line += this.getStore().getStoreNo() + ",";
		line += this.getExtra().getDist().getDistYn() + ",";
		line += this.getExtra().getDist().getDistUrl() + ",";
		line += this.getExtra().getPatent().getParentNo() + ",";
		line += this.getExtra().getPatent().getRegNo() + ",";
		line += this.getExtra().getRef().getReference();
		
		return line;
	}
	@Override
	public String getDistYn() {
		// TODO Auto-generated method stub
		return this.extra.getDist().getDistYn();
	}
	@Override
	public String getDistUrl() {
		// TODO Auto-generated method stub
		return this.extra.getDist().getDistUrl();
	}
	@Override
	public String getParentNo() {
		// TODO Auto-generated method stub
		return this.extra.getPatent().getParentNo();
	}
	@Override
	public String getRegNo() {
		// TODO Auto-generated method stub
		return this.extra.getPatent().getRegNo();
	}
	@Override
	public String getReference() {
		// TODO Auto-generated method stub
		return this.extra.getRef().getReference();
	}
}
