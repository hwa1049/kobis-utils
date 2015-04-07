package org.kobic.kobis.rule.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.log4j.Logger;
import org.kobic.kobis.common.dao.CommonDAOService;
import org.kobic.kobis.rule.mapper.RuleMapper;
import org.kobic.kobis.rule.vo.RuleQueryVO;

public class RuleDAOService extends CommonDAOService implements RuleDAO{
	private static Logger logger = Logger.getLogger(RuleDAOService.class);

    public RuleDAOService(SqlSessionFactory sqlSessionFactory){
        super( sqlSessionFactory );
    }
    
    @Override
	public List<RuleQueryVO> getRulesByInsId( String ins_id, String className ) {
    	List<RuleQueryVO> list = null;
    	// autocommit is false
    	SqlSession session = this.getSessionFactory().openSession( false );

    	RuleMapper ruleMapper = session.getMapper( RuleMapper.class );

    	Map<String, String> map = new HashMap<String, String>();
    	map.put( "ins_cd", ins_id );
    	map.put( "className", className);

    	try {
    		 list = ruleMapper.getRulesByInsId( map );
    	}catch(Exception e) {
    		logger.error( e.getMessage() );
    	}finally{
    		session.close();
    	}
    	return list;
    }
}
