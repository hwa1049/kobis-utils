package org.kobic.kobis.main.services;

import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.kobic.kobis.file.excel.obj.XCellStrainSheetObj;
import org.kobic.kobis.main.vo.D1CellStrainVO;
import org.kobic.kobis.rule.Rule;
import org.kobic.kobis.util.Utils;

public class CellStrainServices extends AbstractKobisServices{
	private static Logger logger = Logger.getLogger(CellStrainServices.class);

	public CellStrainServices(String insCd, XSSFSheet sheet, SqlSessionFactory sessionFactory) {
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

				XCellStrainSheetObj cellStrainSheetRecordObj = XCellStrainSheetObj.getNewInstance( dataRow );
				
				if( Utils.nullToEmpty( cellStrainSheetRecordObj.getAccess_num() ).isEmpty() )	continue;

				D1CellStrainVO d1CellStrainVo = new D1CellStrainVO( cellStrainSheetRecordObj );
				
				Rule rule = new Rule( this.getInsCd() );
				rule.rule( d1CellStrainVo );

				String accessionNumFromMapTab	= Utils.nullToEmpty( this.getKobisService().getAccessionNum( d1CellStrainVo.getAccess_num(), this.getInsCd() ) );
				
				if( !accessionNumFromMapTab.isEmpty() ) {
					this.getKobisService().insertD1CellStrain( d1CellStrainVo, this.getInsCd() );
				}else {
					this.getUnmapService().insertT2UnmappedCellStrain( d1CellStrainVo );
				}
				
//				String accessionNumFromUnmapTab	= Utils.nullToEmpty( this.getUnmapService().getAccessionNum( d1CellStrainVo.getAccess_num(), this.getInsCd() ) );
//
//				if( !accessionNumFromMapTab.isEmpty() && accessionNumFromUnmapTab.isEmpty() ) {
//					this.getKobisService().insertD1CellStrain( d1CellStrainVo );
//				}else if( accessionNumFromMapTab.isEmpty() && !accessionNumFromUnmapTab.isEmpty() ) {
//					this.getUnmapService().insertT2UnmappedCellStrain( cellStrainSheetRecordObj );
//				}
				
				System.out.println( "("+totalCnt + "/" + (this.getSheet().getLastRowNum() -3) + ")");
				totalCnt++;
			}
		}
	}
}
