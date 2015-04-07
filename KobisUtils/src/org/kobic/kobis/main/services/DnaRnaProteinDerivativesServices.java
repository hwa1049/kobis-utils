package org.kobic.kobis.main.services;

import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.kobic.kobis.file.excel.obj.XDnaRnaProteinDerivativesSheetObj;
import org.kobic.kobis.main.vo.D1DnaRnaProteinDerivativesVO;
import org.kobic.kobis.rule.Rule;
import org.kobic.kobis.util.Utils;

public class DnaRnaProteinDerivativesServices extends AbstractKobisServices{
	private static Logger logger = Logger.getLogger(DnaRnaProteinDerivativesServices.class);

	public DnaRnaProteinDerivativesServices(String insCd, XSSFSheet sheet, SqlSessionFactory sessionFactory) {
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

				XDnaRnaProteinDerivativesSheetObj sheetRecordObj = XDnaRnaProteinDerivativesSheetObj.getNewInstance( dataRow );
				
				if( Utils.nullToEmpty( sheetRecordObj.getAccess_num() ).isEmpty() )	continue;

				D1DnaRnaProteinDerivativesVO d1DnaRnaProteinDerivativesnVo = new D1DnaRnaProteinDerivativesVO( sheetRecordObj );
				
				Rule rule = new Rule( this.getInsCd() );
				rule.rule( d1DnaRnaProteinDerivativesnVo );

				String accessionNumFromMapTab	= Utils.nullToEmpty( this.getKobisService().getAccessionNum( d1DnaRnaProteinDerivativesnVo.getAccess_num(), this.getInsCd() ) );
				
				if( !accessionNumFromMapTab.isEmpty() ) {
					this.getKobisService().insertD1DnaRnaProteinDerivatives(d1DnaRnaProteinDerivativesnVo, this.getInsCd());
				}else {
					this.getUnmapService().insertT2UnmappedDnaRnaProteinDerivatives( d1DnaRnaProteinDerivativesnVo );
				}
				
//				String accessionNumFromUnmapTab	= Utils.nullToEmpty( this.getUnmapService().getAccessionNum( d1DnaRnaProteinDerivativesnVo.getAccess_num(), this.getInsCd() ) );
//
//				if( !accessionNumFromMapTab.isEmpty() && accessionNumFromUnmapTab.isEmpty() ) {
//					this.getKobisService().insertD1DnaRnaProteinDerivatives(d1DnaRnaProteinDerivativesnVo);
//				}else if( accessionNumFromMapTab.isEmpty() && !accessionNumFromUnmapTab.isEmpty() ) {
//					this.getUnmapService().insertT2UnmappedDnaRnaProteinDerivatives(sheetRecordObj);
//				}
				
				System.out.println( "("+totalCnt + "/" + (this.getSheet().getLastRowNum() - 3) + ")");
				totalCnt++;
			}
		}
	}

}
