package org.kobic.kobis.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;

import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.kobic.kobis.file.excel.obj.internal.ExcelWorksheetNameMap;
import org.kobic.kobis.main.dao.KobisDAOService;
import org.kobic.kobis.main.services.AbstractKobisServices;
import org.kobic.kobis.main.services.CommonServices;
import org.kobic.kobis.main.services.DnaRnaProteinDerivativesServices;
import org.kobic.kobis.mybatis.factory.MyBatisConnectionFactory;

public class ReadExcel{
	private static Logger logger = Logger.getLogger(ReadExcel.class); 

	private SqlSessionFactory sessionFactory;
	
	private String inFile;
	private String output;
	private String header;

	public ReadExcel() {
		this.inFile = null;
		this.output = null;
		this.header = null;

		this.sessionFactory = MyBatisConnectionFactory.getSqlSessionFactory();
	}

	public void run( String[] args ) {
		if( this.checkParameters(args) ) {
			try {
				this.readExcelFile( this.inFile, this.output, this.header );
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

	public boolean checkParameters( String[] args ) {
		for(int i=0; i<args.length-1; i+=2) {
			if( args[i].equals("-i") || args[i].equals("-I") )				this.inFile = args[i+1];
			else if( args[i].equals("-o") || args[i].equals("-O") )			this.output = args[i+1];
			else if( args[i].equals("-header") )							this.header = args[i+1];
			else {
				System.err.println("Your option is not valid : " + args[i]);
				ReadExcel.usage();
				return false;
			}
		}

		if( this.output == null ) {	this.output = ".";		}
		if( this.header == null ) {	this.header = "none";	}
		if( this.inFile == null ) {
			System.err.println("You did not input a excel file : " + inFile);
			ReadExcel.usage();
			return false;
		}else if( !new File(this.inFile).exists() ) {
			System.err.println("There are no a excel file : " + inFile);
			ReadExcel.usage();
			return false;
		}

		if( !new File(this.output).isDirectory() ) {
			System.err.println("Output you input is not a directory : " + output);
			ReadExcel.usage();
			return false;
		}
		
		System.out.println("Checking institution code....");
		KobisDAOService kobisService = new KobisDAOService( this.sessionFactory );
		if( kobisService.getInstitutionId(header) == null ) {
			System.err.println( header + "는 기관코드가 아닙니다." );
			return false;
		}
		System.out.println("Passed : check institution code....");
		
		return true;
	}

	public void readExcelFile( String filePath, String output, String header ) throws Exception{
		File exelFile = new File( filePath );

		InputStream inp = new FileInputStream( exelFile );

		@SuppressWarnings("resource")
		XSSFWorkbook workbook = new XSSFWorkbook( inp );

		// Loop for worksheet
		for(int i=0; i<workbook.getNumberOfSheets(); i++) {
			String sheetName = workbook.getSheetName(i);
			System.out.println("Worksheet : " + sheetName );
			logger.error( "Worksheet : " + sheetName );

			XSSFSheet sheet = workbook.getSheetAt( i );

			String className = ExcelWorksheetNameMap.getInstance().getClassNameFromSheetName( sheetName );
  
			if( className != null ) {
				@SuppressWarnings("rawtypes")
				Class c = Class.forName(className);

				@SuppressWarnings({ "unchecked", "rawtypes" })
				Constructor cons = c.getConstructor( new Class[]{String.class, XSSFSheet.class, SqlSessionFactory.class} );

				Object obj = cons.newInstance( header, sheet, this.sessionFactory );
				
				AbstractKobisServices ks = (AbstractKobisServices) obj;
				
				if( ks instanceof CommonServices)	continue;
//				if( ks instanceof IndividualServices )	continue;
//				if( ks instanceof CellStrainServices)	continue;
//				if( ks instanceof StrainServices)	continue;
////				if( ks instanceof DnaRnaProteinDerivativesServices) continue;
//				if( ks instanceof SourceServices ) continue;
//				if( ks instanceof ExtractServices ) continue;
////				else								continue;
//				if( ks instanceof DnaRnaProteinDerivativesServices )
				ks.readRecordsInSheet();
			}
		}

		inp.close();
	}

	public static void usage() {
		System.err.println("####################################");
		System.err.println("java ReadExel -i [input file] -o [output directory] -header header prefix");
	}

	public static void main( String[] args ) throws IOException {
//		String output = "/Users/lion/Desktop";
//		String inFile = "/Users/lion/Desktop/20150227_KOBIS_정보연계표준안_수정_ver9_한국식물추출물은행.xlsx";
//		String header = "cov";

//		-o /Users/lion/Desktop -i /Users/lion/git/kobis-utils/KobisUtils/sample/20150227_KOBIS_정보연계표준안_수정_ver9_한국식물추출물은행.xlsx -header INS00001

		String[][] params = new String[][]{
//				{"-o", "/Users/lion/Desktop", "-i", "/Users/lion/git/kobis-utils/KobisUtils/sample/KOBIS_정보연계표준안_수정_ver9_KCTC_Sample.xlsx", "-header", "INS00002"},
//				{"-o", "/Users/lion/Desktop", "-i", "/Users/lion/git/kobis-utils/KobisUtils/sample/KOBIS_정보연계표준안_수정_국가영장류센터_KOBIS수정본.xlsx", "-header", "INS00003"},
//				{"-o", "/Users/lion/Desktop", "-i", "/Users/lion/git/kobis-utils/KobisUtils/sample/KOBIS_정보연계표준안_유전자은행_KOBIS수정본.xlsx", "-header", "INS00004"},
//				{"-o", "/Users/lion/Desktop", "-i", "/Users/lion/git/kobis-utils/KobisUtils/sample/KOBIS_해외소재센터_201503 06 1차 데이터_IRMRC.xlsx", "-header", "INS00005"},
//				{"-o", "/Users/lion/Desktop", "-i", "/Users/lion/git/kobis-utils/KobisUtils/sample/KOBIS_해외소재센터_정보연계표준_20150403 2차 데이터_IRMRC.xlsx", "-header", "INS00005"},
				{"-o", "/Users/lion/Desktop", "-i", "/Users/lion/git/kobis-utils/KobisUtils/sample/20150227_KOBIS_정보연계표준안_수정_ver9_한국식물추출물은행.xlsx", "-header", "INS00001"}
		};

		for(int i=0; i<params.length; i++) {
			ReadExcel read = new ReadExcel();
			try {
				System.out.println( params[i][5] + " processing" );
				logger.error( params[i][5] + " processing" );
				read.run( params[i] );
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
}