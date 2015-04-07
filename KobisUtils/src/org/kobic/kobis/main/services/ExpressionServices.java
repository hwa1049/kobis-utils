package org.kobic.kobis.main.services;

import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.kobic.kobis.file.excel.obj.XExpressionSheetObj;
import org.kobic.kobis.main.vo.D1ExpressionVO;
import org.kobic.kobis.rule.Rule;
import org.kobic.kobis.util.Utils;

public class ExpressionServices extends AbstractKobisServices{
	private static Logger logger = Logger.getLogger(ExpressionServices.class);

	public ExpressionServices(String insCd, XSSFSheet sheet, SqlSessionFactory sessionFactory) {
		super(insCd, sheet, sessionFactory);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void readRecordsInSheet() throws NoSuchMethodException, SecurityException, Exception {
		// TODO Auto-generated method stub
		if( this.getSheet().getLastRowNum() > 3 ) {
			int totalCnt = 0;

			for( int j=3; j<=this.getSheet().getLastRowNum(); j++ ) {
				XSSFRow dataRow = this.getSheet().getRow(j);

				XExpressionSheetObj sheetRecordObj = XExpressionSheetObj.getNewInstance( dataRow );
				
				if( Utils.nullToEmpty( sheetRecordObj.getAccess_num() ).isEmpty() )	continue;

				D1ExpressionVO vo = new D1ExpressionVO( sheetRecordObj );
				
				Rule rule = new Rule( this.getInsCd() );
				rule.rule( vo );

				String accessionNumFromMapTab	= Utils.nullToEmpty( this.getKobisService().getAccessionNum( vo.getAccess_num(), this.getInsCd() ) );
				
				if( !accessionNumFromMapTab.isEmpty() ) {
					this.getKobisService().insertD1Expression(vo, this.getInsCd());
				}else {
					this.getUnmapService().insertT2UnmappedExpression(vo);
				}

//				String accessionNumFromUnmapTab	= Utils.nullToEmpty( this.getUnmapService().getAccessionNum( vo.getAccess_num(), this.getInsCd() ) );
//
//				if( !accessionNumFromMapTab.isEmpty() && accessionNumFromUnmapTab.isEmpty() ) {
//					this.getKobisService().insertD1Expression(vo);
//				}else if( accessionNumFromMapTab.isEmpty() && !accessionNumFromUnmapTab.isEmpty() ) {
//					this.getUnmapService().insertT2UnmappedExpression(sheetRecordObj);
//				}
				System.out.println( "("+totalCnt + "/" + (this.getSheet().getLastRowNum() -3) + ")");
				totalCnt++;
			}
		}
	}

}
