package org.kobic.kobis.taxon.proc;

import java.util.List;

import org.apache.log4j.Logger;
import org.kobic.kobis.taxon.vo.NameWithTaxonIdVO;

public class TaxonProc {
	private static Logger logger = Logger.getLogger(TaxonProc.class);

	private String name;
	private String currentStatus;
	private List<NameWithTaxonIdVO> list;
	
	public static final String CLS_TAB_KOBIC = "KOBIC";
	public static final String CLS_TAB_ITIS = "ITIS";
	public static final String CLS_TAB_GBIF = "GBIF";
	public static final String CLS_TAB_NCBI = "NCBI";

	public TaxonProc(String name, List<NameWithTaxonIdVO> list) {
		this.name = name;
		this.list = list;

		if( this.list == null )				this.currentStatus = null;
		else if( this.list.isEmpty() )		this.currentStatus = MultipleClassificationProc.NOTHING_TO_MAP_IN_ALL;
		else if( this.list.size() > 1 )		this.currentStatus = MultipleClassificationProc.MULTIPLE_MAPPING;
		else								this.currentStatus = MultipleClassificationProc.FINE_MAPPING;
	}
	
	public String getCurrentStatus() {
		return currentStatus;
	}
	public void setCurrentStatus(String currentStatus) {
		this.currentStatus = currentStatus;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<NameWithTaxonIdVO> getList() {
		return list;
	}
	public void setList(List<NameWithTaxonIdVO> list) {
		this.list = list;
	}
	public String getMessage() {
		return "  " + this.name + "  : " + this.list.size();
	}
	public boolean isStatus( String status ) {
		if( this.currentStatus.equals( status ) )	return true;
		return false;
	}
	public boolean isEmpty() {
		if( this.list == null )			return true;
		else if( this.list.isEmpty() )	return true;
		return false;
	}
	public String getTaxId() {
		if( this.list == null )			return null;
		else if( this.list.isEmpty() )	return null;
		
		return this.list.get(0).getTax_id();
	}
}
