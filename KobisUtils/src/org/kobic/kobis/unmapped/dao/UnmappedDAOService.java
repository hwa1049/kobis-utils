package org.kobic.kobis.unmapped.dao;

import java.util.HashMap;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.log4j.Logger;
import org.kobic.kobis.common.dao.CommonDAOService;
import org.kobic.kobis.file.excel.obj.XBodyFluidSheetObj;
import org.kobic.kobis.file.excel.obj.XCellStrainSheetObj;
import org.kobic.kobis.file.excel.obj.XDnaRnaProteinDerivativesSheetObj;
import org.kobic.kobis.file.excel.obj.XDnaSequenceSheetObj;
import org.kobic.kobis.file.excel.obj.XEmbryoSheetObj;
import org.kobic.kobis.file.excel.obj.XEtcSheetObj;
import org.kobic.kobis.file.excel.obj.XExpressionSheetObj;
import org.kobic.kobis.file.excel.obj.XExtractSheetObj;
import org.kobic.kobis.file.excel.obj.XIndividualSheetObj;
import org.kobic.kobis.file.excel.obj.XObservationSheetObj;
import org.kobic.kobis.file.excel.obj.XOrganSheetObj;
import org.kobic.kobis.file.excel.obj.XProteinSequenceSheetObj;
import org.kobic.kobis.file.excel.obj.XSeedSheetObj;
import org.kobic.kobis.file.excel.obj.XSourceSheetObj;
import org.kobic.kobis.file.excel.obj.XSpecimenSheetObj;
import org.kobic.kobis.file.excel.obj.XStrainSheetObj;
import org.kobic.kobis.file.excel.obj.XStructureSheetObj;
import org.kobic.kobis.main.vo.D1CommonVO;
import org.kobic.kobis.taxon.proc.TaxonProc;
import org.kobic.kobis.unmapped.mapper.UnmappedMapper;
import org.kobic.kobis.util.Utils;

public class UnmappedDAOService extends CommonDAOService implements UnmappedDAO{
	private static Logger logger = Logger.getLogger(UnmappedDAOService.class);

	public UnmappedDAOService(SqlSessionFactory sqlSessionFactory){
		super(sqlSessionFactory);
	}

    @Override
    public String getAccessionNum(String accession_num, String ins_cd) {
    	SqlSession session = this.getSessionFactory().openSession();
    	String result = null;
    	try {
    		UnmappedMapper unMappedMapper = session.getMapper( UnmappedMapper.class );
    		Map<String, String> map = new HashMap<String, String>();
    		map.put("accession_num", accession_num);
    		map.put("ins_cd", ins_cd);

    		result = unMappedMapper.getAccessionNum( map );
	   	}finally{
	   		session.close();
	   	}
    	
    	return Utils.emptyToNull( result );
    }
    
	@Override
	public int insertT2UnmappedCommon( D1CommonVO commonSheet ) {
    	SqlSession session = this.getSessionFactory().openSession( false );

    	int ret = 0;
    	try {
    		UnmappedMapper unmappedMapper = session.getMapper( UnmappedMapper.class );
    		unmappedMapper.insertT2UnmappedCommon(commonSheet);

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
	public int insertT2UnmappedEtc(XEtcSheetObj etcSheet) {
    	SqlSession session = this.getSessionFactory().openSession( false );

    	int ret = 0;
    	try {
    		UnmappedMapper unmappedMapper = session.getMapper( UnmappedMapper.class );
    		unmappedMapper.insertT2UnmappedEtc(etcSheet);

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
	public int insertT2UnmappedDnaSequence(XDnaSequenceSheetObj dnaSequenceSheet) {
    	SqlSession session = this.getSessionFactory().openSession( false );

    	int ret = 0;
    	try {
    		UnmappedMapper unmappedMapper = session.getMapper( UnmappedMapper.class );
    		unmappedMapper.insertT2UnmappedDnaSequence(dnaSequenceSheet);

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
	public int insertT2UnmappedStructure(XStructureSheetObj structureSheet) {
    	SqlSession session = this.getSessionFactory().openSession( false );

    	int ret = 0;
    	try {
    		UnmappedMapper unmappedMapper = session.getMapper( UnmappedMapper.class );
    		unmappedMapper.insertT2UnmappedStructure(structureSheet);

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
	public int insertT2UnmappedExpression(XExpressionSheetObj expressionSheet) {
    	SqlSession session = this.getSessionFactory().openSession( false );

    	int ret = 0;
    	try {
    		UnmappedMapper unmappedMapper = session.getMapper( UnmappedMapper.class );
    		unmappedMapper.insertT2UnmappedExpression(expressionSheet);

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
	public int insertT2UnmappedProteinSequence( XProteinSequenceSheetObj proteinSequenceSheet) {
    	SqlSession session = this.getSessionFactory().openSession( false );

    	int ret = 0;
    	try {
    		UnmappedMapper unmappedMapper = session.getMapper( UnmappedMapper.class );
    		unmappedMapper.insertT2UnmappedProteinSequence(proteinSequenceSheet);

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
	public int insertT2UnmappedStrain(XStrainSheetObj strainSheet) {
    	SqlSession session = this.getSessionFactory().openSession( false );

    	int ret = 0;
    	try {
    		UnmappedMapper unmappedMapper = session.getMapper( UnmappedMapper.class );
    		unmappedMapper.insertT2UnmappedStrain(strainSheet);

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
	public int insertT2UnmappedCellStrain(XCellStrainSheetObj cellStrainSheet) {
    	SqlSession session = this.getSessionFactory().openSession( false );

    	int ret = 0;
    	try {
    		UnmappedMapper unmappedMapper = session.getMapper( UnmappedMapper.class );
    		unmappedMapper.insertT2UnmappedCellStrain(cellStrainSheet);

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
	public int insertT2UnmappedBodyFluid(XBodyFluidSheetObj bodyFluidSheet) {
    	SqlSession session = this.getSessionFactory().openSession( false );

    	int ret = 0;
    	try {
    		UnmappedMapper unmappedMapper = session.getMapper( UnmappedMapper.class );
    		unmappedMapper.insertT2UnmappedBodyFluid(bodyFluidSheet);

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
	public int insertT2UnmappedExtraction(XExtractSheetObj extractionSheet) {
    	SqlSession session = this.getSessionFactory().openSession( false );

    	int ret = 0;
    	try {
    		UnmappedMapper unmappedMapper = session.getMapper( UnmappedMapper.class );
    		unmappedMapper.insertT2UnmappedExtraction(extractionSheet);

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
	public int insertT2UnmappedOrgan(XOrganSheetObj observationSheet) {
    	SqlSession session = this.getSessionFactory().openSession( false );

    	int ret = 0;
    	try {
    		UnmappedMapper unmappedMapper = session.getMapper( UnmappedMapper.class );
    		unmappedMapper.insertT2UnmappedOrgan(observationSheet);

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
	public int insertT2UnmappedObservation(XObservationSheetObj observationSheet) {
    	SqlSession session = this.getSessionFactory().openSession( false );

    	int ret = 0;
    	try {
    		UnmappedMapper unmappedMapper = session.getMapper( UnmappedMapper.class );
    		unmappedMapper.insertT2UnmappedObservation(observationSheet);

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
	public int insertT2UnmappedIndividual(XIndividualSheetObj individualSheet) {
    	SqlSession session = this.getSessionFactory().openSession( false );

    	int ret = 0;
    	try {
    		UnmappedMapper unmappedMapper = session.getMapper( UnmappedMapper.class );
    		unmappedMapper.insertT2UnmappedIndividual(individualSheet);

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
	public int insertT2UnmappedSpecimen(XSpecimenSheetObj specimenSheet) {
    	SqlSession session = this.getSessionFactory().openSession( false );

    	int ret = 0;
    	try {
    		UnmappedMapper unmappedMapper = session.getMapper( UnmappedMapper.class );
    		unmappedMapper.insertT2UnmappedSpecimen(specimenSheet);

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
	public int insertT2UnmappedSource(XSourceSheetObj sourceSheet) {
    	SqlSession session = this.getSessionFactory().openSession( false );

    	int ret = 0;
    	try {
    		UnmappedMapper unmappedMapper = session.getMapper( UnmappedMapper.class );
    		unmappedMapper.insertT2UnmappedSource(sourceSheet);

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
	public int insertT2UnmappedSeed(XSeedSheetObj seedSheet) {
    	SqlSession session = this.getSessionFactory().openSession( false );

    	int ret = 0;
    	try {
    		UnmappedMapper unmappedMapper = session.getMapper( UnmappedMapper.class );
    		unmappedMapper.insertT2UnmappedSeed(seedSheet);

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
	public int insertT2UnmappedEmbryo(XEmbryoSheetObj embryoSheet) {
    	SqlSession session = this.getSessionFactory().openSession( false );

    	int ret = 0;
    	try {
    		UnmappedMapper unmappedMapper = session.getMapper( UnmappedMapper.class );
    		unmappedMapper.insertT2UnmappedEmbryo(embryoSheet);

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
	public int insertT2UnmappedDnaRnaProteinDerivatives( XDnaRnaProteinDerivativesSheetObj dnaRnaProteinDerivativeSheet) {
    	SqlSession session = this.getSessionFactory().openSession( false );

    	int ret = 0;
    	try {
    		UnmappedMapper unmappedMapper = session.getMapper( UnmappedMapper.class );
    		unmappedMapper.insertT2UnmappedDnaRnaProteinDerivatives(dnaRnaProteinDerivativeSheet);

    		session.commit();
    	}catch(Exception e) {
    		ret = 0;
    		logger.error( e.getMessage() );
    		logger.error( "Accession is not exist in T2_UnmappedCommon  : " + dnaRnaProteinDerivativeSheet.getAccess_num() );
    		session.rollback();
    	}finally{
    		session.close();
    	}
    	return ret;
	}
}
