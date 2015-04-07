package org.kobic.kobis.main.services;

import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.kobic.kobis.file.excel.obj.XIndividualSheetObj;
import org.kobic.kobis.main.vo.D1IndividualVO;
import org.kobic.kobis.rule.Rule;
import org.kobic.kobis.util.Utils;

public class IndividualServices extends AbstractKobisServices{
	private static Logger logger = Logger.getLogger(IndividualServices.class);

	public IndividualServices(String insCd, XSSFSheet sheet, SqlSessionFactory sessionFactory) {
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

				XIndividualSheetObj sheetRecordObj = XIndividualSheetObj.getNewInstance( dataRow );

				if( Utils.nullToEmpty( sheetRecordObj.getAccess_num() ).isEmpty() )	continue;
				
				D1IndividualVO vo = new D1IndividualVO( sheetRecordObj );
				
				Rule rule = new Rule( this.getInsCd() );
				rule.rule( vo );

				String accessionNumFromMapTab	= Utils.nullToEmpty( this.getKobisService().getAccessionNum( vo.getAccess_num(), this.getInsCd() ) );
				
				if( !accessionNumFromMapTab.isEmpty() ) {
					this.getKobisService().insertD1Individual(vo, this.getInsCd());
				}else {
					this.getUnmapService().insertT2UnmappedIndividual(vo);
				}

//				String accessionNumFromUnmapTab	= Utils.nullToEmpty( this.getUnmapService().getAccessionNum( vo.getAccess_num(), this.getInsCd() ) );
//
//				if( !accessionNumFromMapTab.isEmpty() && accessionNumFromUnmapTab.isEmpty() ) {
//					this.getKobisService().insertD1Individual(vo);
//				}else if( accessionNumFromMapTab.isEmpty() && !accessionNumFromUnmapTab.isEmpty() ) {
//					this.getUnmapService().insertT2UnmappedIndividual(sheetRecordObj);
//				}
				System.out.println( "("+totalCnt + "/" + (this.getSheet().getLastRowNum() -3) + ")");
				totalCnt++;
			}
		}
	}

}
