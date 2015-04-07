package org.kobic.kobis.main.services;

import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.kobic.kobis.file.excel.obj.XProteinSequenceSheetObj;
import org.kobic.kobis.main.vo.D1ProteinSequenceVO;
import org.kobic.kobis.rule.Rule;
import org.kobic.kobis.util.Utils;

public class ProteinSequenceServices extends AbstractKobisServices{
	private static Logger logger = Logger.getLogger(ProteinSequenceServices.class);

	public ProteinSequenceServices(String insCd, XSSFSheet sheet, SqlSessionFactory sessionFactory) {
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

				XProteinSequenceSheetObj sheetRecordObj = XProteinSequenceSheetObj.getNewInstance( dataRow );
				
				if( Utils.nullToEmpty( sheetRecordObj.getAccess_num() ).isEmpty() )	continue;

				D1ProteinSequenceVO vo = new D1ProteinSequenceVO( sheetRecordObj );
				
				Rule rule = new Rule( this.getInsCd() );
				rule.rule( vo );

				String accessionNumFromMapTab	= Utils.nullToEmpty( this.getKobisService().getAccessionNum( vo.getAccess_num(), this.getInsCd() ) );
				
				if( !accessionNumFromMapTab.isEmpty() ) {
					this.getKobisService().insertD1ProteinSequence( vo, this.getInsCd() );
				}else {
					this.getUnmapService().insertT2UnmappedProteinSequence( vo );
				}

//				String accessionNumFromUnmapTab	= Utils.nullToEmpty( this.getUnmapService().getAccessionNum( vo.getAccess_num(), this.getInsCd() ) );
//
//				if( !accessionNumFromMapTab.isEmpty() && accessionNumFromUnmapTab.isEmpty() ) {
//					this.getKobisService().insertD1ProteinSequence( vo );
//				}else if( accessionNumFromMapTab.isEmpty() && !accessionNumFromUnmapTab.isEmpty() ) {
//					this.getUnmapService().insertT2UnmappedProteinSequence( sheetRecordObj );
//				}
				System.out.println( "("+totalCnt + "/" + (this.getSheet().getLastRowNum() -3) + ")");
				totalCnt++;
			}
		}
	}

}
