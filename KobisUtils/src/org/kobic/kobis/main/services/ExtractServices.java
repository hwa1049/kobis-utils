package org.kobic.kobis.main.services;

import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.kobic.kobis.file.excel.obj.XExtractSheetObj;
import org.kobic.kobis.main.vo.D1ExtractionVO;
import org.kobic.kobis.rule.Rule;
import org.kobic.kobis.util.Utils;

public class ExtractServices extends AbstractKobisServices{
	private static Logger logger = Logger.getLogger(ExtractServices.class);
	
	public ExtractServices(String insCd, XSSFSheet sheet, SqlSessionFactory sessionFactory) {
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

				XExtractSheetObj extractSheetRecordObj = XExtractSheetObj.getNewInstance( dataRow );
				
				if( Utils.nullToEmpty( extractSheetRecordObj.getAccess_num() ).isEmpty() )	continue;

				D1ExtractionVO d1ExtractVo = new D1ExtractionVO( extractSheetRecordObj );
				
				Rule rule = new Rule( this.getInsCd() );
				rule.rule( d1ExtractVo );

				String accessionNumFromMapTab	= Utils.nullToEmpty( this.getKobisService().getAccessionNum( d1ExtractVo.getAccess_num(), this.getInsCd() ) );
				
				if( !accessionNumFromMapTab.isEmpty() ) {
					this.getKobisService().insertD1Extraction( d1ExtractVo, this.getInsCd() );
				}else {
					this.getUnmapService().insertT2UnmappedExtraction( d1ExtractVo );
				}

//				String accessionNumFromUnmapTab	= Utils.nullToEmpty( this.getUnmapService().getAccessionNum( d1ExtractVo.getAccess_num(), this.getInsCd() ) );
//
//				if( !accessionNumFromMapTab.isEmpty() && accessionNumFromUnmapTab.isEmpty() ) {
//					this.getKobisService().insertD1Extraction( d1ExtractVo );
//				}else if( accessionNumFromMapTab.isEmpty() && !accessionNumFromUnmapTab.isEmpty() ) {
//					this.getUnmapService().insertT2UnmappedExtraction( extractSheetRecordObj );
//				}
				
				System.out.println( "("+totalCnt + "/" + (this.getSheet().getLastRowNum()-3) + ")");
				totalCnt++;
			}
			
			System.out.println( "=======================================================" );
			System.out.println( "== Total records : " + totalCnt);
			System.out.println( "=======================================================" );
		}
	}
}
