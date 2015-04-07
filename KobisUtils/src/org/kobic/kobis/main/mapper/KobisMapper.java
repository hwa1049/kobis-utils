package org.kobic.kobis.main.mapper;

import java.util.Map;

import org.kobic.kobis.main.vo.D1BodyFluidVO;
import org.kobic.kobis.main.vo.D1CellStrainVO;
import org.kobic.kobis.main.vo.D1CommonVO;
import org.kobic.kobis.main.vo.D1DnaRnaProteinDerivativesVO;
import org.kobic.kobis.main.vo.D1DnaSequenceVO;
import org.kobic.kobis.main.vo.D1EmbryoVO;
import org.kobic.kobis.main.vo.D1EtcVO;
import org.kobic.kobis.main.vo.D1ExpressionVO;
import org.kobic.kobis.main.vo.D1ExtractionVO;
import org.kobic.kobis.main.vo.D1IndividualVO;
import org.kobic.kobis.main.vo.D1ObservationVO;
import org.kobic.kobis.main.vo.D1OrganVO;
import org.kobic.kobis.main.vo.D1ProteinSequenceVO;
import org.kobic.kobis.main.vo.D1SeedVO;
import org.kobic.kobis.main.vo.D1SourceVO;
import org.kobic.kobis.main.vo.D1SpecimenVO;
import org.kobic.kobis.main.vo.D1StrainVO;
import org.kobic.kobis.main.vo.D1StructureVO;


public interface KobisMapper {
    public String getInstitutionId(String insCd);    
    public String getAccessionNum( Map<String, String> map );
    public int insertD1Common( D1CommonVO d1CommonVo );
    public int insertD1Organ( D1OrganVO observationSheet );
    public int insertD1Observation( D1ObservationVO observationSheet );
    public int insertD1Individual( D1IndividualVO individualSheet );
    public int insertD1Specimen( D1SpecimenVO specimenSheet );
    public int insertD1Source( D1SourceVO sourceSheet );
    public int insertD1Seed( D1SeedVO seedSheet );
    public int insertD1Embryo( D1EmbryoVO embryoSheet );
    public int insertD1DnaRnaProteinDerivatives( D1DnaRnaProteinDerivativesVO dnaRnaProteinDerivativeSheet );
    public int insertD1Strain( D1StrainVO strainSheet );
    public int insertD1CellStrain( D1CellStrainVO cellStrainSheet );
    public int insertD1BodyFluid( D1BodyFluidVO bodyFluidSheet );
    public int insertD1ProteinSequence( D1ProteinSequenceVO proteinSequenceSheet );
    public int insertD1Expression( D1ExpressionVO expressionSheet );
    public int insertD1Structure( D1StructureVO structureSheet );
    public int insertD1DnaSequence( D1DnaSequenceVO dnaSequenceSheet );
    public int insertD1Etc( D1EtcVO etcSheet);
    public int insertD1Extraction( D1ExtractionVO extractionSheet );
    public int insertE1Synonyms( D1CommonVO d1CommonVo );
    public int insertE1Culture( Map<String, String> cultureSheet );
    public int insertE1Store( Map<String, String> cultureSheet );
    public int insertE1Distribution( Map<String, String> cultureSheet );
    public int insertE1Reference( Map<String, String> cultureSheet );
    public int insertE1Patent( Map<String, String> cultureSheet );
    public int insertT2MappedCommon( D1CommonVO commonSheet );

	public String getNewObservationId( String access_num );
	public String getNewIndividualId( String access_num );
	public String getNewSpecimenId( String access_num );
	public String getNewOrganId( String access_num );
	public String getNewSourceId( String access_num );
	public String getNewSeedId( String access_num );
	public String getNewEmbryoId( String access_num );
	public String getNewDnaRnaProteinDerivativesId( String access_num );
	public String getNewStrainId( String access_num );
	public String getNewCellStrainId( String access_num );
	public String getNewBodyFluidId( String access_num );
	public String getNewProteinSequenceId( String access_num );
	public String getNewExpressionId( String access_num );
	public String getNewDnaSequenceId( String access_num );
	public String getNewEtcId( String access_num );
	public String getNewExtractionId( String access_num );
	public String getNewStructureId( String access_num );
	
	
	
	public int insertL3ExtendLink( Map<String, String> map );
}
