package org.kobic.kobis.main.services;

//import java.io.BufferedWriter;
//import java.io.FileWriter;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;

//import org.apache.commons.beanutils.BeanUtils;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.kobic.kobis.file.excel.obj.XCommonSheetObj;
import org.kobic.kobis.main.vo.D1CommonVO;
//import org.kobic.kobis.mybatis.db.vo.NameWithTaxonIdVO;
import org.kobic.kobis.rule.Rule;
//import org.kobic.kobis.util.Utils;
import org.kobic.kobis.taxon.proc.MultipleClassificationProc;

public class CommonServices extends AbstractKobisServices{
	private static Logger logger = Logger.getLogger(CommonServices.class);
	
//	private HashMap<String, XCommonSheetObj> mapped;
//	private HashMap<String, XCommonSheetObj> unmapped;

	public CommonServices( String insCd, XSSFSheet sheet, SqlSessionFactory sessionFactory ) {
		super( insCd, sheet, sessionFactory );
	}

//	private boolean findKobisCodeInPhylogeneticTree( String speciesType, String species, XCommonSheetObj commonSheetRecordObj, ExtraInfo extraInfo, List<PhylogeneticTreeVO> kobisPhylogeneListFromDB ) throws IntrospectionException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
//		String extraProperty = speciesType.replace(".", "");
//
//		for(Iterator<PhylogeneticTreeVO> iter=kobisPhylogeneListFromDB.iterator(); iter.hasNext();) {
//			PhylogeneticTreeVO ptvo = iter.next();
//
//			String extraPropertyValueFromDB = Utils.getValueByField( ptvo, extraProperty ).toString().split("\\|")[0];
//			String speciesFromDB = ptvo.getSpecies().split("\\|")[0].trim().toUpperCase();
//
//			if( species.trim().toUpperCase().equals( speciesFromDB.trim().toUpperCase() ) && extraPropertyValueFromDB.trim().toUpperCase().equals( extraInfo.extraValue(speciesType).trim().toUpperCase() ) ) {
//				commonSheetRecordObj.setKobisCode( ptvo.getCode() );
//
//				commonSheetRecordObj.setMessage( "" );
//				return true;
//			}
//		}
//		commonSheetRecordObj.setMessage( "Kobis에 종소명["+species+"]가 없거나 또는 엑셀파일에서 읽은 레코드의 종내 분류군명과 Kobis의 분류체계가 다릅니다.[" + speciesType.trim() + "][" + extraInfo.extraValue(speciesType).trim() + "]");
//		return false;
//	}
//
//	private boolean findKobisCodeInPhylogeneticTree( String genus, String species, XCommonSheetObj commonSheetRecordObj, List<PhylogeneticTreeVO> kobisPhylogeneListFromDB ) {
//		for(Iterator<PhylogeneticTreeVO> iter=kobisPhylogeneListFromDB.iterator(); iter.hasNext();) {
//			PhylogeneticTreeVO ptvo = iter.next();
//			String genusFromDB = ptvo.getGenus().split("\\|")[0].trim().toUpperCase();
//			String speciesFromDB = ptvo.getSpecies().split("\\|")[0].trim().toUpperCase();
//
//			if( genusFromDB.equals( genus.trim().toUpperCase() ) && speciesFromDB.equals( species.trim().toUpperCase() ) ) {
//				commonSheetRecordObj.setKobisCode( ptvo.getCode() );
//				
//				commonSheetRecordObj.setMessage( "" );
//				return true;
//			}
//		}
//		commonSheetRecordObj.setMessage( "Kobis에 속명["+genus+"]가 없거나 또는 종소명[" + species + "] 의 일치하는 정보가 없습니다." );
//		return false;
//	}
	
//	private String[] splitScientificNameByDetail( String scientificName ) {
//		String[] ret = null;
//
//		for(int i=0; i<splitWord.length; i++) {
//			if( scientificName.contains( splitWord[i] ) ) {
//				ret = scientificName.split( splitWord[i], -1 );
//				break;
//			}
//		}
//		return ret;
//	}

	@Override
	public void readRecordsInSheet() throws NoSuchMethodException, SecurityException, Exception {
//		BufferedWriter bw = new BufferedWriter( new FileWriter("/Users/lion/Desktop/" + this.getInsCd() ) );

		if( this.getSheet().getLastRowNum() > 3 ) {
			int totalCnt = 0;
			int mappedCnt = 0;
			for( int j=3; j<=this.getSheet().getLastRowNum(); j++ ) {
				XSSFRow dataRow = this.getSheet().getRow(j);

				XCommonSheetObj commonSheetRecordObj = XCommonSheetObj.getNewInstance( dataRow );
				
//				String accession_num = Utils.nullToEmpty( this.getKobisService().getAccessionNum( commonSheetRecordObj.getAccess_num(), this.getInsCd() ) );
//				String unaccession_num = Utils.nullToEmpty( this.getUnmapService().getAccessionNum( commonSheetRecordObj.getAccess_num(), this.getInsCd() ) );
//
//				if( accession_num.isEmpty() && unaccession_num.isEmpty() ) {
					D1CommonVO d1CommonVo = new D1CommonVO( commonSheetRecordObj );
					d1CommonVo.setIns_cd( this.getInsCd() );
	
					// Rule 적용
					Rule rule = new Rule( d1CommonVo.getIns_cd() );
					rule.rule( d1CommonVo );
	
					String scientificName = d1CommonVo.getScientificName();
					
					MultipleClassificationProc classifyObj = new MultipleClassificationProc( this.getSessionFactory() );
	
					classifyObj.validate( scientificName );
	
					boolean canValidateToDatabase = classifyObj.updateDatabase( d1CommonVo, scientificName );
					if ( canValidateToDatabase ){	mappedCnt++;	}
//				}

				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
//				classifyObj.doProcessing( currentDataMappingStatus, scientificName );
//
//				boolean print = false;
//				String message = "";
//				if( kobicTaxons.size() > 1 ) 	{ message += "  KOBIC  : " + kobicTaxons.size();	print = true;}
//				if( ncbiTaxons.size() > 1 )		{ message += "  NCBI  : " + ncbiTaxons.size();		print = true;}
//				if( itisTaxons.size() > 1 )		{ message += "  ITIS  : " + itisTaxons.size();		print = true;}
//				if( gbifTaxons.size() > 1 )		{ message += "  GBIF  : " + gbifTaxons.size();		print = true;}
//
//				if( kobicTaxons.isEmpty() && ncbiTaxons.isEmpty() && itisTaxons.isEmpty() && gbifTaxons.isEmpty() ) {
//					// 한곳에도 매핑되는 경우가 없는 경우 var. f. ssp. susp. 등을 제거하고 조회해 본다.   Trichosa kirolowil var. japonica
//					String specific_detail = "";
//
//					String[] ref = this.splitScientificNameByDetail( scientificName );
//					if( ref != null && ref.length == 2 ) {
//						scientificName = ref[0].trim();
//						specific_detail = ref[1].trim();
//					}
//
//					kobicTaxons = this.getDao().getScientificNameFromKobicTaxonomy( scientificName );
//					ncbiTaxons = this.getDao().getScientificNameFromNcbiTaxonomy( scientificName );
//					itisTaxons = this.getDao().getScientificNameFromItisTaxonomy( scientificName );
//					gbifTaxons = this.getDao().getScientificNameFromGbifTaxonomy( scientificName );
//					
//					message = "";
//					if( kobicTaxons.size() > 1 ) 		{message += "  KOBIC  : " + kobicTaxons.size();	print = true;}
//					else if( ncbiTaxons.size() > 1 )	{message += "  NCBI  " + ncbiTaxons.size();		print = true;}
//					else if( itisTaxons.size() > 1)		{message += "  ITIS  " + itisTaxons.size();		print = true;}
//					else if( gbifTaxons.size() > 1 )	{message += "  GBIF  " + gbifTaxons.size();		print = true;}
//
//					// 만약 다시 여러개가 검출이 된다면 var. ssp. f. susp. 등을 참조하여 일치하는 정보를 불러온다
//					Map<String, String> map = new HashMap<String, String>();
//					map.put("scientific_name", scientificName);
//					map.put("detail", specific_detail);
//					kobicTaxons = this.getDao().getScientificNameFromKobicTaxonomyDetail( map );
//					if( kobicTaxons.size() < 2 )	print = false;
//				}else if( kobicTaxons.size() > 1 ) {
//					// 환경부 분류체계의 여러곳에 매핑되는 경우를 해결하기위해
//					if( scientificName.contains("var.") || scientificName.contains("for.") || scientificName.contains("f.") || scientificName.contains("ssp.") || scientificName.endsWith("susp.") ) {
//						;
//					}else {
//						kobicTaxons = this.getDao().getScientificNameFromKobicTaxonomyPure( scientificName );
//						if( kobicTaxons.size() < 2 )	print = false;
//					}
//				}
//
//				if( print )	{
//					d1CommonVo.setMessage( "[001] " + scientificName + "이 여러군데 매핑됩니다. " + message );
//					int result = this.getDao().insertUnmappedD1Common( d1CommonVo );
////					bw.write( d1CommonVo.getPrintLine() );
////					bw.newLine();
//				}else {
//					if( kobicTaxons.isEmpty() && ncbiTaxons.isEmpty() && itisTaxons.isEmpty() && gbifTaxons.isEmpty() ) {
//						d1CommonVo.setMessage( "[002] " + this.getDao().getInstitutionId(this.getInsCd()) + " " + scientificName + " 는 어디에도 매핑되지 않습니다." );
////						bw.write( d1CommonVo.getPrintLine() );
////						bw.newLine();
//						int result = this.getDao().insertUnmappedD1Common( d1CommonVo );
//					}else {
//						Map<String, String> map = new HashMap<String, String>();
//						if( kobicTaxons.size() > 0 )	map.put("KOBIS_CODE", kobicTaxons.get(0).getTax_id() );
//						if( ncbiTaxons.size() > 0 )		map.put("ncbi_tax_id", ncbiTaxons.get(0).getTax_id() );
//						if( itisTaxons.size() > 0 )		map.put("itis_tax_id", itisTaxons.get(0).getTax_id() );
//						if( gbifTaxons.size() > 0 )		map.put("gbif_tax_id", gbifTaxons.get(0).getTax_id() );
//						map.put("scientific_name", scientificName );
//	
//						String tab_id = Utils.nullToEmpty( this.getDao().getT1ClassificationSystemTable( map ) );
//
//			    		if( tab_id.isEmpty() ) {
//			    			int ret = this.getDao().insertT1ClassificationSystemTable( map );
//			    			tab_id = Utils.nullToEmpty( this.getDao().getT1ClassificationSystemTable( map ) );
//			    		}
//			    		d1CommonVo.setCode( tab_id );
//			    		this.getDao().insertMappedD1Common( d1CommonVo );
//					}
//				}

//				if( kobicTaxons.isEmpty() && ncbiTaxons.isEmpty() && itisTaxons.isEmpty() && gbifTaxons.isEmpty() ) {
//					d1CommonVo.setMessage( this.getDao().getInstitutionId(this.getInsCd()) + " " + scientificName + " 는 어디에도 매핑되지 않습니다." );
//					int result = this.getDao().insertUnmappedD1Common( d1CommonVo );
//				}else {
//					int result = this.getDao().insertMappedD1Common( d1CommonVo );
//				}
//
//				long f = System.currentTimeMillis();
//				
//				if( kobicTaxons.size() > 1 || ncbiTaxons.size() > 1 || itisTaxons.size() > 1 || gbifTaxons.size() > 1 ) {
//					String ln = "kobic(" + kobicTaxons.size() + "), ncbi(" + ncbiTaxons.size() + "), itis(" + itisTaxons.size()  + "), gbif(" +  gbifTaxons.size() + ")";
//					System.out.println( d1CommonVo.getAccess_num() + "  => " + scientificName + " " + ln);
//				}
//				
//				System.out.println( "      kobic(" + Utils.getTimeStamp(b, a) + "), ncbi(" + Utils.getTimeStamp(c, b) + "), itis(" + Utils.getTimeStamp(d, c) + "), gbif(" + Utils.getTimeStamp(e, d) +"), insert(" + Utils.getTimeStamp(f, e) +")" );
//
//				List<PhylogeneticTreeVO> kobisPhylogeneListFromDB = this.getDao().getPhylogeneticTreeByGenus( commonSheetRecordObj.getGenus() );
//
//				boolean ret = false;
//				if( kobisPhylogeneListFromDB.size() > 0 ) {
//					Rule rule = new Rule( this.getInsCd() );
//
//					rule.rule( commonSheetRecordObj );
//
//					String speciesType	= Utils.emptyToNull( commonSheetRecordObj.getIn_species_type() );
//					String genus		= commonSheetRecordObj.getGenus();
//					String species		= commonSheetRecordObj.getSpecies();
//					ExtraInfo extraInfo = null; 
//
//					if( speciesType != null ) {
////						 추출물은행 예외사항
//						if( this.getInsCd().equals("INS00001") ) {
//							String[] seperateSpeciesToSpecific = species.split( speciesType );
//
//							species = seperateSpeciesToSpecific[0];
//
//							extraInfo = new ExtraInfo( speciesType, seperateSpeciesToSpecific[1] );
//						}
//
//						ret = this.findKobisCodeInPhylogeneticTree(speciesType, species, commonSheetRecordObj, extraInfo, kobisPhylogeneListFromDB);
//					}else {
//						ret = this.findKobisCodeInPhylogeneticTree(genus, species, commonSheetRecordObj, kobisPhylogeneListFromDB);
//					}
//				}else {
//					commonSheetRecordObj.setMessage( "KOBIS 분류체계가 존재하지 않습니다. [" + commonSheetRecordObj.getGenus() + "]" );
//					ret = false;
//				}
//
//				if( ret ){
//					this.getDao();
//					this.mapped.put( commonSheetRecordObj.getAccess_num(), commonSheetRecordObj );
//					mappedCnt++;
//				}else{
//					this.unmapped.put( commonSheetRecordObj.getAccess_num(), commonSheetRecordObj );
//				}

//				System.out.println( String.format( "%20s", commonSheetRecordObj.getAccess_num()) + "  Processed " + String.format("%5s", ( j-3)) + " / " + (this.getSheet().getLastRowNum() - 3 ) + " isMapped : " + ret + " (" + commonSheetRecordObj.getMessage() + ")" );
//
				
				System.out.println( "("+totalCnt + "/" + this.getSheet().getLastRowNum() + ")");
				totalCnt++;
			}
//			bw.close();

			System.out.println( "=======================================================" );
			System.out.println( "== Total records : " + totalCnt);
			System.out.println( "== Mapped records : " + mappedCnt);
			System.out.println( "== Unmapped records : " + (totalCnt-mappedCnt) );
			System.out.println( "== Mapping ratio : " + ((double)mappedCnt/totalCnt) + "%");
			System.out.println( "=======================================================" );
		}

//		int totalRecord = 0;
//		int rightAssignment = 0;
//		// Loop for record
//		for( int j=3; j<=sheet.getLastRowNum(); j++ ) {
//			XSSFRow dataRow = sheet.getRow(j);
//			totalRecord++;
//			
//			CommonObj obj = CommonObj.getNewInstance( dataRow );
//
//			List<PhylogeneticTreeVO> list = session.selectList("Kobis.getPhylogeneticTreeByGenus", obj.getGenus() );
//
//			if( list == null ) {
//				// null
//				System.out.println("Genus [" + obj.getGenus() + "] is null ");
//			}else {
//				if( list.size() > 0 ) {
//					String species = obj.getSpecies().replace("var.", "").replace("  ", " ");
//					String[] divSpecies = species.split(" ");
//
//					int assignedCount = 0;
//					boolean isAssigned = false;
//					for(Iterator<PhylogeneticTreeVO> iter=list.iterator(); iter.hasNext();) {
//						PhylogeneticTreeVO vo = iter.next();
//
//						if( divSpecies.length > 1 ) {
//							String ssp = vo.getSsp().split("\\|")[0];
//							String var = vo.getVar().split("\\|")[0];
//							String f = vo.getF().split("\\|")[0];
//							
//							if( ssp.trim().toUpperCase().equals( divSpecies[1].trim().toUpperCase() ) ) {
////										System.out.println( "SSP >>>" + ssp + " " + divSpecies[1] );
//								isAssigned = true;
//								assignedCount++;
//								obj.setKobisCode( vo.getCode() );
//							}else if( var.trim().toUpperCase().equals( divSpecies[1].trim().toUpperCase() ) ) {
////										System.out.println( "VAR >>>" + ssp + " " + divSpecies[1] );
//								isAssigned = true;
//								assignedCount++;
//								obj.setKobisCode( vo.getCode() );
//							}else if( f.trim().toUpperCase().equals( divSpecies[1].trim().toUpperCase() ) ) {
////										System.out.println( "F >>>" + ssp + " " + divSpecies[1] );
//								isAssigned = true;
//								assignedCount++;
//								obj.setKobisCode( vo.getCode() );
//							}
//						}else {
//							String spieces = vo.getSpecies().split("\\|")[0].trim().toUpperCase();
//							String ssp = vo.getSsp().split("\\|")[0].replace("종소명없음", "").trim();
//							String var = vo.getVar().split("\\|")[0].replace("종소명없음", "").trim();
//							String f = vo.getF().split("\\|")[0].replace("종소명없음", "").trim();
//
//							if( obj.getSpecies().trim().toUpperCase().equals( spieces ) ) {
//								if( ssp.isEmpty() && var.isEmpty() && f.isEmpty() ) {
//									isAssigned = true;
//									assignedCount++;
//									obj.setKobisCode( vo.getCode() );
//								}
//							}else {
////										System.out.println( obj.getAccess_num() + "  " + obj.getSpecies().toUpperCase() + "  " + spieces );
//							}
//						}
//					}
//					
////							System.out.println( obj.getAccess_num() + " " + assignedCount );
//					if( isAssigned == false ) {
//						String line = obj.getPrintLine();
//						bwNoSpeciesMapping.write( line );
//						bwNoSpeciesMapping.newLine();
//					}else {
//						String line = obj.getPrintLine();
//						bwRightMappedResult.write( line );
//						bwRightMappedResult.newLine();
//						rightAssignment++;
//					}
//				}else {
//					// empty
//
//					String line = obj.getPrintLine();
//					bwNoGenusMapping.write( line );
//					bwNoGenusMapping.newLine();
//				}
//			}
//		}
//		System.out.println( sheetName + " => " + rightAssignment + "/" + totalRecord);
	}
}