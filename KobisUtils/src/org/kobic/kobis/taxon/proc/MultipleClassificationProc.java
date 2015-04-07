package org.kobic.kobis.taxon.proc;

import java.util.HashMap;
import java.util.Iterator;
//import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.log4j.Logger;
import org.kobic.kobis.main.dao.KobisDAOService;
import org.kobic.kobis.main.vo.D1CommonVO;
import org.kobic.kobis.taxon.dao.TaxonDAOService;
import org.kobic.kobis.unmapped.dao.UnmappedDAOService;

public class MultipleClassificationProc {
	private static Logger logger = Logger.getLogger(MultipleClassificationProc.class);

	private Map<String, TaxonProc> taxons;
	
	private String message;
	private String errorCode;

//	private static final String[] splitWord = new String[]{ "var.", "for.", "f.", "ssp.", "spp.", "susp." };

	public static final String FINE_MAPPING				= "000";
	public static final String MULTIPLE_MAPPING			= "001";
	public static final String NOTHING_TO_MAP_IN_ALL	= "002";
	public static final String ERROR_IN_DB				= "003";
	
	private KobisDAOService kobisService;
	private TaxonDAOService taxonService;
	private UnmappedDAOService unmappedService;

	public MultipleClassificationProc( SqlSessionFactory sessionFactory ) {
		this.message = "";
		this.errorCode = "";
		this.taxons = new HashMap<String, TaxonProc>();
		
		this.kobisService = new KobisDAOService( sessionFactory );
		this.taxonService = new TaxonDAOService( sessionFactory );
		this.unmappedService = new UnmappedDAOService( sessionFactory );
	}

	public String validate( String scientificName ){
		this.taxons.put( TaxonProc.CLS_TAB_KOBIC,	new TaxonProc( TaxonProc.CLS_TAB_KOBIC,	this.taxonService.getScientificNameFromKobicTaxonomy( scientificName ) ) );
		this.taxons.put( TaxonProc.CLS_TAB_NCBI,	new TaxonProc( TaxonProc.CLS_TAB_NCBI,	this.taxonService.getScientificNameFromNcbiTaxonomy( scientificName ) ) );
		this.taxons.put( TaxonProc.CLS_TAB_ITIS,	new TaxonProc( TaxonProc.CLS_TAB_ITIS,	this.taxonService.getScientificNameFromItisTaxonomy( scientificName ) ) );
		this.taxons.put( TaxonProc.CLS_TAB_GBIF,	new TaxonProc( TaxonProc.CLS_TAB_GBIF,	this.taxonService.getScientificNameFromGbifTaxonomy( scientificName ) ) );

		return this.errorCode1Check();
	}

	private String errorCode1Check( ) {
		int emptyTaxons = 0;
		for(Iterator<TaxonProc> iter = this.taxons.values().iterator(); iter.hasNext();) {
			TaxonProc taxon = iter.next();
			if( taxon.isStatus( MultipleClassificationProc.MULTIPLE_MAPPING) ) {
				// 어느 하나의 분류체계에서 2개이상 매핑되는 것이 나오면 들어온 레코드 데이터는 중복으로 처리한다
				this.message += taxon.getMessage();
				this.errorCode = MultipleClassificationProc.MULTIPLE_MAPPING;
			}else if( taxon.isStatus( MultipleClassificationProc.NOTHING_TO_MAP_IN_ALL ) )	{ emptyTaxons++;	}
		}

		if( !errorCode.equals( MultipleClassificationProc.MULTIPLE_MAPPING ) ) {
			// 모든 분류체계에서 검색이 안되는 레코드는 현재상태를 미매핑으로 처리한다
			if( emptyTaxons == this.taxons.size() )	this.errorCode = MultipleClassificationProc.NOTHING_TO_MAP_IN_ALL;
			else									this.errorCode = MultipleClassificationProc.FINE_MAPPING;
		}

		return this.errorCode;
	}

	public boolean updateDatabase( D1CommonVO d1CommonVo, String scientificName ) {
		if( this.errorCode.equals( MultipleClassificationProc.MULTIPLE_MAPPING ) )	{
			d1CommonVo.setMessage( "["+MultipleClassificationProc.MULTIPLE_MAPPING+"] " + scientificName + "이 여러군데 매핑됩니다. " + message );

			System.err.println( "unmap[001]");
			this.unmappedService.insertT2UnmappedCommon( d1CommonVo );

			return false;
		}else if( this.errorCode.equals( MultipleClassificationProc.NOTHING_TO_MAP_IN_ALL ) )	{
			d1CommonVo.setMessage( "["+MultipleClassificationProc.NOTHING_TO_MAP_IN_ALL+"] " + this.kobisService.getInstitutionId(d1CommonVo.getIns_cd()) + " " + scientificName + " 는 어디에도 매핑되지 않습니다." );
			
			System.err.println( "unmap[002]");
			this.unmappedService.insertT2UnmappedCommon( d1CommonVo );

			return false;
		}else {
			Map<String, String> map = new HashMap<String, String>();
			for(Iterator<TaxonProc> iter = this.taxons.values().iterator(); iter.hasNext();) {
				TaxonProc taxon = iter.next();
				if( taxon.getName().equals(TaxonProc.CLS_TAB_KOBIC ) && taxon.getCurrentStatus().equals( MultipleClassificationProc.FINE_MAPPING ) )	map.put("kobic_tax_id", taxon.getTaxId() );
				if( taxon.getName().equals(TaxonProc.CLS_TAB_NCBI ) && taxon.getCurrentStatus().equals( MultipleClassificationProc.FINE_MAPPING ) )		map.put("ncbi_tax_id", taxon.getTaxId() );
				if( taxon.getName().equals(TaxonProc.CLS_TAB_ITIS ) && taxon.getCurrentStatus().equals( MultipleClassificationProc.FINE_MAPPING ) )		map.put("itis_tax_id", taxon.getTaxId() );
				if( taxon.getName().equals(TaxonProc.CLS_TAB_GBIF ) && taxon.getCurrentStatus().equals( MultipleClassificationProc.FINE_MAPPING ) )		map.put("gbif_tax_id", taxon.getTaxId() );
			}
			map.put("scientific_name", scientificName );

			int ret = this.kobisService.insertD1Common( d1CommonVo, map );
			if( ret == 0 ) {
				d1CommonVo.setMessage( "["+MultipleClassificationProc.ERROR_IN_DB+"] " + this.kobisService.getInstitutionId(d1CommonVo.getIns_cd()) + " " + d1CommonVo.getAccess_num() + " 데이터베이스 반영중 오류 발생." );
				return false;
			}

    		return true;
		}
	}
}
