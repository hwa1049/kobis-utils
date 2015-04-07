package org.kobic.kobis.main.services;

import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.kobic.kobis.file.excel.obj.XEmbryoSheetObj;
import org.kobic.kobis.main.vo.D1EmbryoVO;
import org.kobic.kobis.rule.Rule;
import org.kobic.kobis.util.Utils;

public class EmbryoServices extends AbstractKobisServices{
	private static Logger logger = Logger.getLogger(EmbryoServices.class);

	public EmbryoServices(String insCd, XSSFSheet sheet, SqlSessionFactory sessionFactory) {
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

				XEmbryoSheetObj sheetRecordObj = XEmbryoSheetObj.getNewInstance( dataRow );
				
				if( Utils.nullToEmpty( sheetRecordObj.getAccess_num() ).isEmpty() )	continue;

				D1EmbryoVO vo = new D1EmbryoVO( sheetRecordObj );
				
				Rule rule = new Rule( this.getInsCd() );
				rule.rule( vo );

				String accessionNumFromMapTab	= Utils.nullToEmpty( this.getKobisService().getAccessionNum( vo.getAccess_num(), this.getInsCd() ) );
				
				if( !accessionNumFromMapTab.isEmpty() ) {
					this.getKobisService().insertD1Embryo(vo, this.getInsCd());
				}else {
					this.getUnmapService().insertT2UnmappedEmbryo(vo);
				}

//				String accessionNumFromUnmapTab	= Utils.nullToEmpty( this.getUnmapService().getAccessionNum( vo.getAccess_num(), this.getInsCd() ) );
//
//				if( !accessionNumFromMapTab.isEmpty() && accessionNumFromUnmapTab.isEmpty() ) {
//					this.getKobisService().insertD1Embryo(vo);
//				}else if( accessionNumFromMapTab.isEmpty() && !accessionNumFromUnmapTab.isEmpty() ) {
//					this.getUnmapService().insertT2UnmappedEmbryo(sheetRecordObj);
//				}
				
				System.out.println( "("+totalCnt + "/" + (this.getSheet().getLastRowNum() -3) + ")");
				totalCnt++;
			}
		}
	}

}
