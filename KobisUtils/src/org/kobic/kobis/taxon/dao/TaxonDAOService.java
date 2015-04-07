package org.kobic.kobis.taxon.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.log4j.Logger;
import org.kobic.kobis.common.dao.CommonDAOService;
import org.kobic.kobis.rule.interpreter.LexicalInterpreter;
import org.kobic.kobis.taxon.mapper.TaxonMapper;
import org.kobic.kobis.taxon.vo.NameWithTaxonIdVO;
import org.kobic.kobis.taxon.vo.PhylogeneticTreeVO;
import org.kobic.kobis.util.Utils;

public class TaxonDAOService extends CommonDAOService implements TaxonDAO{
	private static Logger logger = Logger.getLogger(TaxonDAOService.class);

    public TaxonDAOService(SqlSessionFactory sqlSessionFactory){
        super(sqlSessionFactory);
    }
    
    @Override
    public List<PhylogeneticTreeVO> getPhylogeneticTreeByGenus( String genus) {
    	List<PhylogeneticTreeVO> list = null;
    	// autocommit is false
    	SqlSession session = this.getSessionFactory().openSession();

    	try {
    		TaxonMapper mapper = session.getMapper( TaxonMapper.class );
    		list = mapper.getPhylogeneticTreeByGenus(genus);
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
    		TaxonMapper mapper = session.getMapper( TaxonMapper.class );
    		result = mapper.getInstitutionId( insCd );
	   	}finally{
	   		session.close();
	   	}

    	return Utils.emptyToNull( result );
    }

    @Override
    public List<NameWithTaxonIdVO> getScientificNameFromNcbiTaxonomy(String scientfic_name) {
    	SqlSession session = this.getSessionFactory().openSession();
    	List<NameWithTaxonIdVO> result = null;
    	try {
    		TaxonMapper mapper = session.getMapper( TaxonMapper.class );
    		result = mapper.getScientificNameFromNcbiTaxonomy(scientfic_name);
	   	}finally{
	   		session.close();
	   	}   		

    	return result;
    }
    
    @Override
    public List<NameWithTaxonIdVO> getScientificNameFromGbifTaxonomy(String scientfic_name) {
    	SqlSession session = this.getSessionFactory().openSession();
    	List<NameWithTaxonIdVO> result = null;
    	
    	try {
    		TaxonMapper mapper = session.getMapper( TaxonMapper.class );
    		result = mapper.getScientificNameFromGbifTaxonomy(scientfic_name);
	   	}finally{
	   		session.close();
	   	}   	
    	
    	return result;
    }
    
    @Override
    public List<NameWithTaxonIdVO> getScientificNameFromItisTaxonomy(String scientfic_name) {
    	SqlSession session = this.getSessionFactory().openSession();
    	List<NameWithTaxonIdVO> result = null;
    	try {
    		TaxonMapper mapper = session.getMapper( TaxonMapper.class );
    		result = mapper.getScientificNameFromItisTaxonomy(scientfic_name);
    	}finally{
    		session.close();
    	}
    	
    	return result;
    }

    @Override
    public List<NameWithTaxonIdVO> getScientificNameFromKobicTaxonomy(String scientfic_name) {
    	SqlSession session = this.getSessionFactory().openSession();
    	List<NameWithTaxonIdVO> result = null;
    	try {
    		TaxonMapper mapper = session.getMapper( TaxonMapper.class );
    		result = mapper.getScientificNameFromKobicTaxonomy(scientfic_name);
    	}finally{
    		session.close();
    	}

    	return result;
    }

	@Override
	public List<PhylogeneticTreeVO> getPhylogeneticTree(Map<String, String> map) {
    	SqlSession session = this.getSessionFactory().openSession();
    	List<PhylogeneticTreeVO> result = null;
    	try {
    		TaxonMapper mapper = session.getMapper( TaxonMapper.class );
    		result = mapper.getPhylogeneticTree(map);
    	}finally{
    		session.close();
    	}

    	return result;
	}

	@Override
	public String getT1ClassificationSystemTable(Map<String, String> map) {
    	SqlSession session = this.getSessionFactory().openSession();
    	String result = null;
    	try {
    		TaxonMapper mapper = session.getMapper( TaxonMapper.class );
    		result = mapper.getT1ClassificationSystemTable(map);
    	}finally{
    		session.close();
    	}

    	return result;
	}

	@Override
	public int insertT1ClassificationSystemTable(Map<String, String> map) {
    	SqlSession session = this.getSessionFactory().openSession( false );

    	int ret = 0;
    	try {
    		TaxonMapper mapper = session.getMapper( TaxonMapper.class );
    		ret = mapper.insertT1ClassificationSystemTable(map);
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
