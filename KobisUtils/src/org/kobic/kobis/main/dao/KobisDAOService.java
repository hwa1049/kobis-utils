package org.kobic.kobis.main.dao;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.log4j.Logger;
import org.kobic.kobis.common.dao.CommonDAOService;
import org.kobic.kobis.main.mapper.KobisMapper;
import org.kobic.kobis.main.services.StructureServices;
import org.kobic.kobis.main.vo.D1CommonVO;
import org.kobic.kobis.taxon.mapper.TaxonMapper;
import org.kobic.kobis.taxon.vo.PhylogeneticTreeVO;
import org.kobic.kobis.util.Utils;
import org.kobic.kobis.file.excel.obj.internal.CultureObj;
import org.kobic.kobis.file.excel.obj.internal.DistPatentReferenceObj;
import org.kobic.kobis.file.excel.obj.internal.PatentObj;
import org.kobic.kobis.file.excel.obj.internal.ReferenceObj;
import org.kobic.kobis.file.excel.obj.internal.StoreObj;
import org.kobic.kobis.main.vo.D1BodyFluidVO;
import org.kobic.kobis.main.vo.D1CellStrainVO;
import org.kobic.kobis.main.vo.D1DnaRnaProteinDerivativesVO;
import org.kobic.kobis.main.vo.D1DnaSequenceVO;
import org.kobic.kobis.main.vo.D1EmbryoVO;
import org.kobic.kobis.main.vo.D1EtcVO;
import org.kobic.kobis.main.vo.D1ExpressionVO;
import org.kobic.kobis.main.vo.D1ExtractionVO;
import org.kobic.kobis.main.vo.D1IndividualVO;
import org.kobic.kobis.main.vo.D1ObservationVO;
import org.kobic.kobis.main.vo.D1OrganVO;
import org.kobic.kobis.main.vo.D1ProteinSequenceVO;
import org.kobic.kobis.main.vo.D1SeedVO;
import org.kobic.kobis.main.vo.D1SourceVO;
import org.kobic.kobis.main.vo.D1SpecimenVO;
import org.kobic.kobis.main.vo.D1StrainVO;
import org.kobic.kobis.main.vo.D1StructureVO;
import org.kobic.kobis.main.vo.DBCommonInterface;

public class KobisDAOService extends CommonDAOService implements KobisDAO{
	private static Logger logger = Logger.getLogger(KobisDAOService.class);

	public KobisDAOService(SqlSessionFactory sqlSessionFactory){
		super( sqlSessionFactory );
	}
    
	/**
	 * genus를 이용하여 환경부 분류체계를 조회하는 쿼리를 호출하는 메소드
	 * 
	 * @param genus 조회를 위한 genus 정보
	 * 
	 * @return genus로 조회된 분류체계 정보
	 */
	public List<PhylogeneticTreeVO> getPhylogeneticTreeByGenus( String genus) {
		List<PhylogeneticTreeVO> list = null;
		// autocommit is false
		SqlSession session = this.getSessionFactory().openSession();
		
		try {
			TaxonMapper taxonMapper = session.getMapper( TaxonMapper.class );
			list = taxonMapper.getPhylogeneticTreeByGenus(genus);
		}finally{
			session.close();
		}
		return list;
	}

    @Override
    public String getInstitutionId(String insCd) {
    	SqlSession session = this.getSessionFactory().openSession();
    	String result = null;
    	try {
    		KobisMapper kobisMapper = session.getMapper( KobisMapper.class );
    		result = kobisMapper.getInstitutionId(insCd);
	   	}finally{
	   		session.close();
	   	}
    	
    	return Utils.emptyToNull( result );
    }
    @Override
    public String getAccessionNum(String accession_num, String ins_cd) {
    	SqlSession session = this.getSessionFactory().openSession();
    	String result = null;
    	try {
    		KobisMapper kobisMapper = session.getMapper( KobisMapper.class );
    		Map<String, String> map = new HashMap<String, String>();
    		map.put("accession_num", accession_num);
    		map.put("ins_cd", ins_cd);

    		result = kobisMapper.getAccessionNum( map );
	   	}finally{
	   		session.close();
	   	}
    	
    	return Utils.emptyToNull( result );
    }
    @Override
    public int insertD1Common( D1CommonVO d1CommonVo, Map<String, String> crossTaxonMap ) {
    	SqlSession session = this.getSessionFactory().openSession( false );

    	KobisMapper kobisMapper = session.getMapper( KobisMapper.class );
    	TaxonMapper taxonMapper = session.getMapper( TaxonMapper.class );
    	int ret = 0;
    	try {
    		// T1_ClassificationSystemTable에서 입력된 환경부, ITIS, GBIF, NCBI의 분류체계로 코드가 존재하는지 조회
    		String tab_id = taxonMapper.getT1ClassificationSystemTable( crossTaxonMap );

    		if( Utils.nullToEmpty( tab_id ).isEmpty() ) {
    			// 만약 T1_ClassificationSystemTable에 값이 존재하지 않는 경우 테이블에 데이터 등록후 등록번호 가져옴
    			ret += taxonMapper.insertT1ClassificationSystemTable( crossTaxonMap );
    			tab_id = taxonMapper.getT1ClassificationSystemTable( crossTaxonMap );
    		}
    		if( !Utils.nullToEmpty( tab_id ).isEmpty() ) {
    			d1CommonVo.setCode( tab_id );

    			// 매핑된 분류코드를 record에 삽입한뒤 D1_Common 테이블에 입력
    			ret = kobisMapper.insertD1Common(d1CommonVo);

	    		if( !Utils.nullToEmpty( d1CommonVo.getSynonym().trim() ).isEmpty() ) {
	    			// 만약 record에 Synonym이 존재하면 E1_Synonym 테이블에 등록
	    			kobisMapper.insertE1Synonyms(d1CommonVo);
	    		}
    		}

    		session.commit();
    	}catch(Exception e) {
    		ret = 0;
    		session.rollback();
    		logger.error( e.getMessage() );
    	}finally{
    		session.close();
    	}
    	return ret;
    }
    
    public boolean existE1Culture( Map<String, String> map ) {
    	String str = 
    			Utils.nullToEmpty( map.get("culture_medium_name") ).trim()
    			+ Utils.nullToEmpty( map.get("condition") ).trim() 
				+ Utils.nullToEmpty( map.get("succeed_dt") ).trim() 
				+ Utils.nullToEmpty( map.get("succeed_time") ).trim();

    	if( str.isEmpty() )	return false;

    	return true;
    }
    public Map<String, String> getE1Culture( DBCommonInterface sheet ) {
    	Map<String, String> map = new HashMap<String, String>();

		map.put("accession_num",	sheet.getAccess_num() );
		map.put("id",				sheet.getId() );
    	try {
    		Method getCulture = sheet.getClass().getMethod("getCulture" );

    		try {
    			Object cultureParam = getCulture.invoke( sheet );

    			if( cultureParam instanceof  CultureObj ) {
    				CultureObj param = (CultureObj)cultureParam;
    	    		map.put("culture_medium_name",	param.getCultureMediumName() );
    	    		map.put("condition",			param.getCondition() );
    	    		map.put("succeed_dt",			param.getSucceedDt() );
    	    		map.put("succeed_time",			param.getSucceedTime() );
    			}
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				// TODO Auto-generated catch block
				logger.error( e.getMessage() );
			}
    	}catch(NoSuchMethodException e) {
    		logger.error( e.getMessage() );
    	}

    	return map;
    }
    
    public boolean existE1Store( Map<String, String> map ) {
    	String str = Utils.nullToEmpty( map.get("store_no") ).trim() + Utils.nullToEmpty( map.get("store_place") ).trim();

    	if( str.isEmpty() )	return false;

    	return true;
    }
    public Map<String, String> getE1Store( DBCommonInterface sheet ) {
    	Map<String, String> map = new HashMap<String, String>();
    	
		map.put("accession_num",	sheet.getAccess_num() );
		map.put("id",				sheet.getId() );
    	try {
    		Method getStore = sheet.getClass().getMethod("getStore" );

    		try {
    			Object storeParam = getStore.invoke( sheet );

    			if( storeParam instanceof  StoreObj ) {
    				StoreObj param = (StoreObj)storeParam;
            		map.put("store_no",			param.getStoreNo() );
            		map.put("store_place",		param.getStorePlace() );
    			}
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				// TODO Auto-generated catch block
				logger.error( e.getMessage() );
			}
    	}catch(NoSuchMethodException e) {
    		logger.error( e.getMessage() );
    	}

    	return map;
    }
    
    public boolean existE1Reference( Map<String, String> map ) {
    	String str = Utils.nullToEmpty( map.get("reference")).trim();

    	if( str.isEmpty() )	return false;

    	return true;
    }
    public Map<String, String> getE1Reference( DBCommonInterface sheet ) {
    	Map<String, String> map = new HashMap<String, String>();

		map.put("accession_num",	sheet.getAccess_num() );
		map.put("id",				sheet.getId() );
    	try {
    		Method getExtra = sheet.getClass().getMethod("getExtra" );

    		try {
    			Object distPatentRefParam = getExtra.invoke( sheet );

    			if( distPatentRefParam instanceof  DistPatentReferenceObj ) {
        			DistPatentReferenceObj param = (DistPatentReferenceObj)distPatentRefParam;
            		map.put("reference",		param.getRef().getReference() );
    			}
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				// TODO Auto-generated catch block
				logger.error( e.getMessage() );
			}
    	}catch(NoSuchMethodException e) {
    		try {
				Method getPatent = sheet.getClass().getMethod("getReference" );
				
				try {
	    			Object refObj = getPatent.invoke( sheet );

	    			if( refObj instanceof  ReferenceObj ) {
	    				ReferenceObj param = (ReferenceObj)refObj;
	            		map.put("reference",		param.getReference() );
	            		map.put("accession_num",	sheet.getAccess_num() );
	            		map.put("id",				sheet.getId() );
	    			}
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
					// TODO Auto-generated catch block
					logger.error( ex.getMessage() );
				}
			} catch (NoSuchMethodException e1) {
				// TODO Auto-generated catch block
				logger.error( e1.getMessage() );
			} catch (SecurityException e1) {
				// TODO Auto-generated catch block
				logger.error( e1.getMessage() );
			}
    	}

    	return map;
    }

    public boolean existE1Patent( Map<String, String> map ) {
    	String str = Utils.nullToEmpty( map.get("patent_no") ).trim() + Utils.nullToEmpty( map.get("reg_no") ).trim();

    	if( str.isEmpty() )	return false;

    	return true;
    }
    
    public Map<String, String> getE1Patent( DBCommonInterface sheet ) {
    	Map<String, String> map = new HashMap<String, String>();
    	
		map.put("accession_num",	sheet.getAccess_num() );
		map.put("id",				sheet.getId() );
    	try {
    		Method getExtra = sheet.getClass().getMethod("getExtra" );

    		try {
    			Object distPatentRefParam = getExtra.invoke( sheet );

    			if( distPatentRefParam instanceof  DistPatentReferenceObj ) {
        			DistPatentReferenceObj param = (DistPatentReferenceObj)distPatentRefParam;
            		map.put("patent_no",		param.getPatent().getParentNo() );
            		map.put("reg_no",			param.getPatent().getRegNo() );
    			}
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				// TODO Auto-generated catch block
				logger.error( e.getMessage() );
			}
    	}catch(NoSuchMethodException e) {
    		try {
				Method getPatent = sheet.getClass().getMethod("getPatent" );
				
				try {
	    			Object patentObj = getPatent.invoke( sheet );

	    			if( patentObj instanceof  PatentObj ) {
	    				PatentObj param = (PatentObj)patentObj;
	            		map.put("patent_no",		param.getParentNo() );
	            		map.put("reg_no",			param.getRegNo() );
	            		map.put("accession_num",	sheet.getAccess_num() );
	            		map.put("id",				sheet.getId() );
	    			}
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
					// TODO Auto-generated catch block
					logger.error( ex.getMessage() );
				}
			} catch (NoSuchMethodException e1) {
				// TODO Auto-generated catch block
				logger.error( e1.getMessage() );
			} catch (SecurityException e1) {
				// TODO Auto-generated catch block
				logger.error( e1.getMessage() );
			}
    	}

    	return map;
    }
    
    public boolean existE1Dist( Map<String, String> map ) {
    	String str = Utils.nullToEmpty( map.get("dist_url") ).trim() + Utils.nullToEmpty( map.get("dist_yn") ).trim();

    	if( str.isEmpty() )	return false;

    	return true;
    }
   
    public Map<String, String> getE1Distribution( DBCommonInterface sheet ) {
    	Map<String, String> map = new HashMap<String, String>();

		map.put("accession_num",	sheet.getAccess_num() );
		map.put("id",				sheet.getId() );
    	try {
    		Method getExtra = sheet.getClass().getMethod("getExtra" );

    		try {
    			Object distPatentRefParam = getExtra.invoke( sheet );

    			if( distPatentRefParam instanceof  DistPatentReferenceObj ) {
        			DistPatentReferenceObj param = (DistPatentReferenceObj)distPatentRefParam;
	        		map.put("dist_url",			param.getDist().getDistUrl() );
	        		map.put("dist_yn",			param.getDist().getDistYn() );
    			}
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				// TODO Auto-generated catch block
				logger.error( e.getMessage() );
			}
    	}catch(NoSuchMethodException e) {
    		logger.error( e.getMessage() );
    	}

    	return map;
    }

    @Override
    public int insertD1Observation( D1ObservationVO observationSheet, String ins_cd ) {
    	SqlSession session = this.getSessionFactory().openSession( false );

    	int ret = 0;
    	try {
    		KobisMapper kobisMapper = session.getMapper( KobisMapper.class );
    		
    		String newObsId = kobisMapper.getNewObservationId( ins_cd );
    		observationSheet.setId( newObsId );
    		
			Map<String, String> map = new HashMap<String, String>();
    		map.put("access_num", observationSheet.getAccess_num() );
			map.put("id", newObsId );

			ret = kobisMapper.insertL3ExtendLink( map );
			if( ret > 0 ) {
	    		ret = kobisMapper.insertD1Observation( observationSheet );
	    		if( ret > 0 ) {
	    			Map<String, String> distMap = this.getE1Distribution( observationSheet );
	    			Map<String, String> patentMap = this.getE1Patent( observationSheet );
	    			Map<String, String> refMap = this.getE1Reference( observationSheet );;

	    			ret = kobisMapper.insertE1Distribution( distMap );
	    			ret = kobisMapper.insertE1Patent( patentMap );
	    			ret = kobisMapper.insertE1Reference(  refMap );
	    		}
			}

    		session.commit();
    	}catch(Exception e) {
    		ret = 0;
    		logger.error( e.getMessage() );
    		session.rollback();
    	}finally{
    		session.close();
    	}
    	return ret;    	
    }

	@Override
	public int insertD1Individual( D1IndividualVO individualSheet, String ins_cd) {
		SqlSession session = this.getSessionFactory().openSession( false );

    	int ret = 0;
    	try {
    		KobisMapper kobisMapper = session.getMapper( KobisMapper.class );
    		
    		String newIndiId = kobisMapper.getNewIndividualId( ins_cd );
    		individualSheet.setId( newIndiId );
    		
			Map<String, String> map = new HashMap<String, String>();
    		map.put("access_num", individualSheet.getAccess_num() );
			map.put("id", newIndiId );

			ret = kobisMapper.insertL3ExtendLink( map );
			if( ret > 0 ) {
	    		ret = kobisMapper.insertD1Individual( individualSheet );
	    		
	    		if( ret > 0 ) {
	    			Map<String, String> distMap = this.getE1Distribution( individualSheet );
	    			Map<String, String> patentMap = this.getE1Patent( individualSheet );
	    			Map<String, String> refMap = this.getE1Reference( individualSheet );

	    			ret = kobisMapper.insertE1Distribution( distMap );
	    			ret = kobisMapper.insertE1Patent( patentMap );
	    			ret = kobisMapper.insertE1Reference(  refMap );
	    		}
			}

    		session.commit();
    	}catch(Exception e) {
    		ret = 0;
    		logger.error( e.getMessage() );
    		session.rollback();
    	}finally{
    		session.close();
    	}
    	return ret;
	}

	@Override
	public int insertD1Specimen(D1SpecimenVO specimenSheet, String ins_cd) {
		SqlSession session = this.getSessionFactory().openSession( false );

    	int ret = 0;
    	try {
    		KobisMapper kobisMapper = session.getMapper( KobisMapper.class );
    		
    		String newSpecimenId = kobisMapper.getNewSpecimenId( ins_cd );
    		specimenSheet.setId( newSpecimenId );
    		
			Map<String, String> map = new HashMap<String, String>();
    		map.put("access_num", specimenSheet.getAccess_num() );
			map.put("id", newSpecimenId );

			ret = kobisMapper.insertL3ExtendLink( map );
			if( ret > 0 ) {
	    		ret = kobisMapper.insertD1Specimen( specimenSheet );

	    		if( ret > 0 ) {
	    			Map<String, String> distMap = this.getE1Distribution( specimenSheet );
	    			Map<String, String> patentMap = this.getE1Patent( specimenSheet );
	    			Map<String, String> refMap = this.getE1Reference( specimenSheet );

	    			ret = kobisMapper.insertE1Distribution( distMap );
	    			ret = kobisMapper.insertE1Patent( patentMap );
	    			ret = kobisMapper.insertE1Reference(  refMap );
	    		}
			}

    		session.commit();
    	}catch(Exception e) {
    		ret = 0;
    		logger.error( e.getMessage() );
    		session.rollback();
    	}finally{
    		session.close();
    	}
    	return ret;
	}

	@Override
	public int insertD1Source(D1SourceVO sourceSheet, String ins_cd) {
		SqlSession session = this.getSessionFactory().openSession( false );

    	int ret = 0;
    	try {
    		KobisMapper kobisMapper = session.getMapper( KobisMapper.class );
    		
    		String newSourceId = kobisMapper.getNewSourceId( ins_cd );
    		sourceSheet.setId( newSourceId );
    		
			Map<String, String> map = new HashMap<String, String>();
    		map.put("access_num", sourceSheet.getAccess_num() );
			map.put("id", newSourceId );

			ret = kobisMapper.insertL3ExtendLink( map );
			if( ret > 0 ) {
	    		ret = kobisMapper.insertD1Source( sourceSheet );
	    		
	    		if( ret > 0 ) {
	    			Map<String, String> distMap = this.getE1Distribution( sourceSheet );
	    			Map<String, String> patentMap = this.getE1Patent( sourceSheet );
	    			Map<String, String> refMap = this.getE1Reference( sourceSheet );
	    			Map<String, String> storeMap = this.getE1Store( sourceSheet );

	    			ret = kobisMapper.insertE1Distribution( distMap );
	    			ret = kobisMapper.insertE1Patent( patentMap );
	    			ret = kobisMapper.insertE1Reference(  refMap );
	    			ret = kobisMapper.insertE1Store( storeMap );
	    		}
			}

    		session.commit();
    	}catch(Exception e) {
    		ret = 0;
    		logger.error( e.getMessage() );
    		session.rollback();
    	}finally{
    		session.close();
    	}
    	return ret;
	}

	@Override
	public int insertD1Seed(D1SeedVO seedSheet, String ins_cd) {
		SqlSession session = this.getSessionFactory().openSession( false );

    	int ret = 0;
    	try {
    		KobisMapper kobisMapper = session.getMapper( KobisMapper.class );
    		
    		String newSeedId = kobisMapper.getNewSeedId( ins_cd );
    		seedSheet.setId( newSeedId );
    		
			Map<String, String> map = new HashMap<String, String>();
    		map.put("access_num", seedSheet.getAccess_num() );
			map.put("id", newSeedId );

			ret = kobisMapper.insertL3ExtendLink( map );
			if( ret > 0 ) {
	    		ret = kobisMapper.insertD1Seed( seedSheet );
	
	    		if( ret > 0 ) {
	    			Map<String, String> distMap = this.getE1Distribution( seedSheet );
	    			Map<String, String> patentMap = this.getE1Patent( seedSheet );
	    			Map<String, String> refMap = this.getE1Reference( seedSheet );
	    			Map<String, String> storeMap = this.getE1Store( seedSheet );

	    			ret = kobisMapper.insertE1Distribution( distMap );
	    			ret = kobisMapper.insertE1Patent( patentMap );
	    			ret = kobisMapper.insertE1Reference(  refMap );
	    			ret = kobisMapper.insertE1Store( storeMap );
	    		}
			}

    		session.commit();
    	}catch(Exception e) {
    		ret = 0;
    		logger.error( e.getMessage() );
    		session.rollback();
    	}finally{
    		session.close();
    	}
    	return ret;
	}

	@Override
	public int insertD1Embryo(D1EmbryoVO embryoSheet, String ins_cd) {
		SqlSession session = this.getSessionFactory().openSession( false );

    	int ret = 0;
    	try {
    		KobisMapper kobisMapper = session.getMapper( KobisMapper.class );
    		
    		String newEmbryoId = kobisMapper.getNewEmbryoId( ins_cd );
    		embryoSheet.setId( newEmbryoId );

			Map<String, String> map = new HashMap<String, String>();
    		map.put("access_num", embryoSheet.getAccess_num() );
			map.put("id", newEmbryoId );

			ret = kobisMapper.insertL3ExtendLink( map );
			if( ret > 0 ) {
	    		ret = kobisMapper.insertD1Embryo( embryoSheet );
	    		
	    		if( ret > 0 ) {
	    			Map<String, String> distMap = this.getE1Distribution( embryoSheet );
	    			Map<String, String> patentMap = this.getE1Patent( embryoSheet );
	    			Map<String, String> refMap = this.getE1Reference( embryoSheet );
	    			Map<String, String> storeMap = this.getE1Store( embryoSheet );

	    			ret = kobisMapper.insertE1Distribution( distMap );
	    			ret = kobisMapper.insertE1Patent( patentMap );
	    			ret = kobisMapper.insertE1Reference(  refMap );
	    			ret = kobisMapper.insertE1Store( storeMap );
	    		}
			}
    		
    		session.commit();
    	}catch(Exception e) {
    		ret = 0;
    		logger.error( e.getMessage() );
    		session.rollback();
    	}finally{
    		session.close();
    	}
    	return ret;
	}

	@Override
	public int insertD1DnaRnaProteinDerivatives( D1DnaRnaProteinDerivativesVO dnaRnaProteinDerivativeSheet, String ins_cd) {
		SqlSession session = this.getSessionFactory().openSession( false );

    	int ret = 0;
    	try {
    		KobisMapper kobisMapper = session.getMapper( KobisMapper.class );
    		
    		String newDnaRnaProteinDerivativesId = kobisMapper.getNewDnaRnaProteinDerivativesId( ins_cd );
    		dnaRnaProteinDerivativeSheet.setId( newDnaRnaProteinDerivativesId );
    		
			Map<String, String> map = new HashMap<String, String>();
    		map.put("access_num", dnaRnaProteinDerivativeSheet.getAccess_num() );
			map.put("id", newDnaRnaProteinDerivativesId );

			ret = kobisMapper.insertL3ExtendLink( map );
			if( ret > 0 ) {
				ret = kobisMapper.insertD1DnaRnaProteinDerivatives( dnaRnaProteinDerivativeSheet );
	
	    		if( ret > 0 ) {
	    			Map<String, String> distMap = this.getE1Distribution( dnaRnaProteinDerivativeSheet );
	    			Map<String, String> patentMap = this.getE1Patent( dnaRnaProteinDerivativeSheet );
	    			Map<String, String> refMap = this.getE1Reference( dnaRnaProteinDerivativeSheet );
	    			Map<String, String> storeMap = this.getE1Reference( dnaRnaProteinDerivativeSheet );
	
	    			if( this.existE1Dist(distMap) )		ret = kobisMapper.insertE1Distribution( distMap );
	    			if( this.existE1Patent(patentMap) ) ret = kobisMapper.insertE1Patent( patentMap );
	    			if( this.existE1Reference(refMap) )	ret = kobisMapper.insertE1Reference( refMap );
	    			if( this.existE1Store(storeMap) )	ret = kobisMapper.insertE1Store( storeMap );
	    		}
			}

    		session.commit();
    	}catch(Exception e) {
    		ret = 0;
    		logger.error( e.getMessage() );
    		session.rollback();
    	}finally{
    		session.close();
    	}
    	return ret;
	}

	@Override
	public int insertD1Strain(D1StrainVO strainSheet, String ins_cd) {
		SqlSession session = this.getSessionFactory().openSession( false );

    	int ret = 0;
    	try {
    		KobisMapper kobisMapper = session.getMapper( KobisMapper.class );
    		
    		String newStrainId = kobisMapper.getNewStrainId( ins_cd );
    		strainSheet.setId( newStrainId );
    		
			Map<String, String> map = new HashMap<String, String>();
    		map.put("access_num", strainSheet.getAccess_num() );
			map.put("id", newStrainId );

			ret = kobisMapper.insertL3ExtendLink( map );
			if( ret > 0 ) {
				ret = kobisMapper.insertD1Strain( strainSheet );
	
	    		if( ret > 0 ) {
	    			Map<String, String> distMap = this.getE1Distribution( strainSheet );
	    			Map<String, String> patentMap = this.getE1Patent( strainSheet );
	    			Map<String, String> refMap = this.getE1Reference( strainSheet );
	    			Map<String, String> cultureMap = this.getE1Culture( strainSheet );
	
	    			if( this.existE1Dist(distMap) )			ret = kobisMapper.insertE1Distribution( distMap );
	    			if( this.existE1Patent(patentMap) ) 	ret = kobisMapper.insertE1Patent( patentMap );
	    			if( this.existE1Reference(refMap) )		ret = kobisMapper.insertE1Reference( refMap );
	    			if( this.existE1Culture(cultureMap) )	ret = kobisMapper.insertE1Culture( refMap );
	    		}
			}

    		session.commit();
    	}catch(Exception e) {
    		ret = 0;
    		logger.error( e.getMessage() );
    		session.rollback();
    	}finally{
    		session.close();
    	}
    	return ret;
	}

	@Override
	public int insertD1CellStrain(D1CellStrainVO cellStrainSheet, String ins_cd) {
		SqlSession session = this.getSessionFactory().openSession( false );

    	int ret = 0;
    	try {
    		KobisMapper kobisMapper = session.getMapper( KobisMapper.class );
    		
    		String newCellStrainId = kobisMapper.getNewCellStrainId( ins_cd );
    		cellStrainSheet.setId( newCellStrainId );

			Map<String, String> map = new HashMap<String, String>();
    		map.put("access_num", cellStrainSheet.getAccess_num() );
			map.put("id", newCellStrainId );

			ret = kobisMapper.insertL3ExtendLink( map );
			if( ret > 0 ) {
				ret = kobisMapper.insertD1CellStrain( cellStrainSheet );
	
	    		if( ret > 0 ) {
	    			Map<String, String> distMap = this.getE1Distribution( cellStrainSheet );
	    			Map<String, String> patentMap = this.getE1Patent( cellStrainSheet );
	    			Map<String, String> refMap = this.getE1Reference( cellStrainSheet );
	    			Map<String, String> cultureMap = this.getE1Culture( cellStrainSheet );
	
	    			if( this.existE1Dist(distMap) )			ret = kobisMapper.insertE1Distribution( distMap );
	    			if( this.existE1Patent(patentMap) ) 	ret = kobisMapper.insertE1Patent( patentMap );
	    			if( this.existE1Reference(refMap) )		ret = kobisMapper.insertE1Reference( refMap );
	    			if( this.existE1Culture(cultureMap) )	ret = kobisMapper.insertE1Culture( refMap );
	    		}
			}

    		session.commit();
    	}catch(Exception e) {
    		ret = 0;
    		logger.error( e.getMessage() );
    		session.rollback();
    	}finally{
    		session.close();
    	}
    	return ret;
	}

	@Override
	public int insertD1BodyFluid(D1BodyFluidVO bodyFluidSheet, String ins_cd) {
		SqlSession session = this.getSessionFactory().openSession( false );

    	int ret = 0;
    	try {
    		KobisMapper kobisMapper = session.getMapper( KobisMapper.class );
    		
    		String newBodyFluidId = kobisMapper.getNewBodyFluidId( ins_cd );
    		bodyFluidSheet.setId( newBodyFluidId );
    		
			Map<String, String> map = new HashMap<String, String>();
    		map.put("access_num", bodyFluidSheet.getAccess_num() );
			map.put("id", newBodyFluidId );
			
			ret = kobisMapper.insertL3ExtendLink( map );
			if( ret > 0 ) {
	    		ret = kobisMapper.insertD1BodyFluid( bodyFluidSheet );
	
	    		if( ret > 0 ) {
	    			Map<String, String> distMap = this.getE1Distribution( bodyFluidSheet );
	    			Map<String, String> patentMap = this.getE1Patent( bodyFluidSheet );
	    			Map<String, String> refMap = this.getE1Reference( bodyFluidSheet );
	    			Map<String, String> cultureMap = this.getE1Culture( bodyFluidSheet );

	    			ret = kobisMapper.insertE1Distribution( distMap );
	    			ret = kobisMapper.insertE1Patent( patentMap );
	    			ret = kobisMapper.insertE1Reference(  refMap );
	    			ret = kobisMapper.insertE1Culture( cultureMap );
	    		}
			}

    		session.commit();
    	}catch(Exception e) {
    		ret = 0;
    		logger.error( e.getMessage() );
    		session.rollback();
    	}finally{
    		session.close();
    	}
    	return ret;
	}

	@Override
	public int insertD1ProteinSequence( D1ProteinSequenceVO proteinSequenceSheet, String ins_cd) {
		SqlSession session = this.getSessionFactory().openSession( false );

    	int ret = 0;
    	try {
    		KobisMapper kobisMapper = session.getMapper( KobisMapper.class );
    		
    		String newProteinSequenceId = kobisMapper.getNewProteinSequenceId( ins_cd );
    		proteinSequenceSheet.setId( newProteinSequenceId );
    		
			Map<String, String> map = new HashMap<String, String>();
    		map.put("access_num", proteinSequenceSheet.getAccess_num() );
			map.put("id", newProteinSequenceId );
			
			ret = kobisMapper.insertL3ExtendLink( map );
			if( ret > 0 ) {
	    		ret = kobisMapper.insertD1ProteinSequence( proteinSequenceSheet );
	
	    		if( ret > 0 ) {
	    			Map<String, String> patentMap = this.getE1Patent( proteinSequenceSheet );
	    			Map<String, String> refMap = this.getE1Reference( proteinSequenceSheet );

	    			ret = kobisMapper.insertE1Patent( patentMap );
	    			ret = kobisMapper.insertE1Reference(  refMap );
	    		}    		
			}
    		
    		session.commit();
    	}catch(Exception e) {
    		ret = 0;
    		logger.error( e.getMessage() );
    		session.rollback();
    	}finally{
    		session.close();
    	}
    	return ret;
	}

	@Override
	public int insertD1Expression(D1ExpressionVO expressionSheet, String ins_cd) {
		SqlSession session = this.getSessionFactory().openSession( false );

    	int ret = 0;
    	try {
    		KobisMapper kobisMapper = session.getMapper( KobisMapper.class );
    		
    		String newExpressionId = kobisMapper.getNewExpressionId( ins_cd );
    		expressionSheet.setId( newExpressionId );
    		
			Map<String, String> map = new HashMap<String, String>();
    		map.put("access_num", expressionSheet.getAccess_num() );
			map.put("id", newExpressionId );
			
			ret = kobisMapper.insertL3ExtendLink( map );
			if( ret > 0 ) {
	    		ret = kobisMapper.insertD1Expression( expressionSheet );
	
	    		if( ret > 0 ) {
	    			Map<String, String> patentMap = this.getE1Patent( expressionSheet );
	    			Map<String, String> refMap = this.getE1Reference( expressionSheet );

	    			ret = kobisMapper.insertE1Patent( patentMap );
	    			ret = kobisMapper.insertE1Reference(  refMap );
	    		}
			}

    		session.commit();
    	}catch(Exception e) {
    		ret = 0;
    		logger.error( e.getMessage() );
    		session.rollback();
    	}finally{
    		session.close();
    	}
    	return ret;
	}

	@Override
	public int insertD1Structure(D1StructureVO structureSheet, String ins_cd) {
		SqlSession session = this.getSessionFactory().openSession( false );

    	int ret = 0;
    	try {
    		KobisMapper kobisMapper = session.getMapper( KobisMapper.class );
    		
    		String newStructureId = kobisMapper.getNewStructureId( ins_cd );
    		structureSheet.setId( newStructureId );
    		
			Map<String, String> map = new HashMap<String, String>();
    		map.put("access_num", structureSheet.getAccess_num() );
			map.put("id", newStructureId );
			
			ret = kobisMapper.insertL3ExtendLink( map );
			if( ret > 0 ) {
	    		ret = kobisMapper.insertD1Structure( structureSheet );
	
	    		if( ret > 0 ) {
	    			Map<String, String> patentMap = this.getE1Patent( structureSheet );
	    			Map<String, String> refMap = this.getE1Reference( structureSheet );

	    			ret = kobisMapper.insertE1Patent( patentMap );
	    			ret = kobisMapper.insertE1Reference(  refMap );
	    		}   
			}

    		session.commit();
    	}catch(Exception e) {
    		ret = 0;
    		logger.error( e.getMessage() );
    		session.rollback();
    	}finally{
    		session.close();
    	}
    	return ret;
	}

	@Override
	public int insertD1DnaSequence(D1DnaSequenceVO dnaSequenceSheet, String ins_cd) {
		SqlSession session = this.getSessionFactory().openSession( false );

    	int ret = 0;
    	try {
    		KobisMapper kobisMapper = session.getMapper( KobisMapper.class );
    		
    		String newDnaSequenceId = kobisMapper.getNewDnaSequenceId( ins_cd );
    		dnaSequenceSheet.setId( newDnaSequenceId );
    		
			Map<String, String> map = new HashMap<String, String>();
    		map.put("access_num", dnaSequenceSheet.getAccess_num() );
			map.put("id", newDnaSequenceId );
			
			ret = kobisMapper.insertL3ExtendLink( map );
			if( ret > 0 ) {
	    		ret = kobisMapper.insertD1DnaSequence( dnaSequenceSheet );
	    		
	    		if( ret > 0 ) {
	    			Map<String, String> patentMap = this.getE1Patent( dnaSequenceSheet );
	    			Map<String, String> refMap = this.getE1Reference( dnaSequenceSheet );

	    			ret = kobisMapper.insertE1Patent( patentMap );
	    			ret = kobisMapper.insertE1Reference( refMap );
	    		}
			}

    		session.commit();
    	}catch(Exception e) {
    		ret = 0;
    		logger.error( e.getMessage() );
    		session.rollback();
    	}finally{
    		session.close();
    	}
    	return ret;
	}

	@Override
	public int insertD1Etc(D1EtcVO etcSheet, String ins_cd) {
		SqlSession session = this.getSessionFactory().openSession( false );

    	int ret = 0;
    	try {
    		KobisMapper kobisMapper = session.getMapper( KobisMapper.class );
    		
    		String newEtcId = kobisMapper.getNewEtcId( ins_cd );
    		etcSheet.setId( newEtcId );
    		
			Map<String, String> map = new HashMap<String, String>();
    		map.put("access_num", etcSheet.getAccess_num() );
			map.put("id", newEtcId );
			
			ret = kobisMapper.insertL3ExtendLink( map );
			if( ret > 0 ) {
	    		ret = kobisMapper.insertD1Etc(etcSheet);
	    		if( ret > 0 ) {
	    			Map<String, String> distMap = this.getE1Distribution( etcSheet );
	    			Map<String, String> patentMap = this.getE1Patent( etcSheet );
	    			Map<String, String> refMap = this.getE1Reference( etcSheet );

	    			ret = kobisMapper.insertE1Distribution( distMap );
	    			ret = kobisMapper.insertE1Patent( patentMap );
	    			ret = kobisMapper.insertE1Reference(  refMap );
	    		}
			}

    		ret = kobisMapper.insertD1Etc( etcSheet );
    		session.commit();
    	}catch(Exception e) {
    		ret = 0;
    		logger.error( e.getMessage() );
    		session.rollback();
    	}finally{
    		session.close();
    	}
    	return ret;
	}

	@Override
	public int insertD1Extraction( D1ExtractionVO extractionSheet, String ins_cd ) {
		SqlSession session = this.getSessionFactory().openSession( false );

    	int ret = 0;
    	try {
    		KobisMapper kobisMapper = session.getMapper( KobisMapper.class );

			String newExtractId = kobisMapper.getNewExtractionId( ins_cd );
    		extractionSheet.setId( newExtractId );

			Map<String, String> map = new HashMap<String, String>();
    		map.put("access_num", extractionSheet.getAccess_num() );
			map.put("id", newExtractId );

			ret = kobisMapper.insertL3ExtendLink( map );
			if( ret > 0 ) {
	    		ret = kobisMapper.insertD1Extraction( extractionSheet );
	
	    		if( ret > 0 ) {
	    			Map<String, String> distMap = this.getE1Distribution( extractionSheet );
	    			Map<String, String> patentMap = this.getE1Patent( extractionSheet );
	    			Map<String, String> refMap = this.getE1Reference( extractionSheet );
	
	    			if( this.existE1Dist(distMap) )		ret = kobisMapper.insertE1Distribution( distMap );
	    			if( this.existE1Patent(patentMap) ) ret = kobisMapper.insertE1Patent( patentMap );
	    			if( this.existE1Reference(refMap) )	ret = kobisMapper.insertE1Reference( refMap );
	    		}
			}

    		session.commit();
    	}catch(Exception e) {
    		ret = 0;
    		logger.error( e.getMessage() );
    		session.rollback();
    	}finally{
    		session.close();
    	}
    	return ret;
	}

	@Override
	public int insertT2MappedCommon(D1CommonVO commonSheet) {
		SqlSession session = this.getSessionFactory().openSession( false );

    	int ret = 0;
    	try {
    		KobisMapper kobisMapper = session.getMapper( KobisMapper.class );
    		ret = kobisMapper.insertT2MappedCommon( commonSheet );
    		session.commit();
    	}catch(Exception e) {
    		ret = 0;
    		logger.error( e.getMessage() );
    		session.rollback();
    	}finally{
    		session.close();
    	}
    	return ret;
	}

	@Override
	public int insertD1Organ(D1OrganVO organSheet, String ins_cd) {
		// TODO Auto-generated method stub
		SqlSession session = this.getSessionFactory().openSession( false );

    	int ret = 0;
    	try {
    		KobisMapper kobisMapper = session.getMapper( KobisMapper.class );
    		
    		String newOrganId = kobisMapper.getNewOrganId( ins_cd );
    		organSheet.setId( newOrganId );
    		
			Map<String, String> map = new HashMap<String, String>();
    		map.put("access_num", organSheet.getAccess_num() );
			map.put("id", newOrganId );
			
			ret = kobisMapper.insertL3ExtendLink( map );
			if( ret > 0 ) {
	    		ret = kobisMapper.insertD1Organ( organSheet );
	    		
	    		if( ret > 0 ) {
	    			Map<String, String> distMap = this.getE1Distribution( organSheet );
	    			Map<String, String> patentMap = this.getE1Patent( organSheet );
	    			Map<String, String> refMap = this.getE1Reference( organSheet );
	    			Map<String, String> storeMap = this.getE1Store( organSheet );

	    			ret = kobisMapper.insertE1Distribution( distMap );
	    			ret = kobisMapper.insertE1Patent( patentMap );
	    			ret = kobisMapper.insertE1Reference(  refMap );
	    			ret = kobisMapper.insertE1Store( storeMap );
	    		}
			}

    		session.commit();
    	}catch(Exception e) {
    		ret = 0;
    		logger.error( e.getMessage() );
    		session.rollback();
    	}finally{
    		session.close();
    	}
    	return ret;
	}
}
