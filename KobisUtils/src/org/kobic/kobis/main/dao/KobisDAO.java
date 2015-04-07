package org.kobic.kobis.main.dao;

import java.util.Map;

import org.kobic.kobis.main.vo.D1CommonVO;
import org.kobic.kobis.main.vo.D1BodyFluidVO;
import org.kobic.kobis.main.vo.D1CellStrainVO;
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

/**
 * KOBIS를 위해 외부기관으로 받은 데이터를 데이터베이스 반영하거나 조회하는 쿼리를 관장하는 Data Access Object
 * 
 * @author insoo078
 *
 */
public interface KobisDAO{
    public String getInstitutionId(String insCd);    
    public String getAccessionNum(String accession_num, String ins_cd);

    public int insertD1Common( D1CommonVO d1CommonVo, Map<String, String> crossTaxonMap );
    public int insertD1Organ( D1OrganVO observationSheet, String ins_cd );
    public int insertD1Observation( D1ObservationVO observationSheet, String ins_cd );
    public int insertD1Individual( D1IndividualVO individualSheet, String ins_cd );
    public int insertD1Specimen( D1SpecimenVO specimenSheet, String ins_cd );
    public int insertD1Source( D1SourceVO sourceSheet, String ins_cd );
    public int insertD1Seed( D1SeedVO seedSheet, String ins_cd );
    public int insertD1Embryo( D1EmbryoVO embryoSheet, String ins_cd );
    public int insertD1DnaRnaProteinDerivatives( D1DnaRnaProteinDerivativesVO dnaRnaProteinDerivativeSheet, String ins_cd );
    public int insertD1Strain( D1StrainVO strainSheet, String ins_cd );
    public int insertD1CellStrain( D1CellStrainVO cellStrainSheet, String ins_cd );
    public int insertD1BodyFluid( D1BodyFluidVO bodyFluidSheet, String ins_cd );
    public int insertD1ProteinSequence( D1ProteinSequenceVO proteinSequenceSheet, String ins_cd );
    public int insertD1Expression( D1ExpressionVO expressionSheet, String ins_cd );
    public int insertD1Structure( D1StructureVO structureSheet, String ins_cd );
    public int insertD1DnaSequence( D1DnaSequenceVO dnaSequenceSheet, String ins_cd );
    public int insertD1Etc( D1EtcVO etcSheet, String ins_cd );
    public int insertD1Extraction( D1ExtractionVO extractionSheet, String ins_cd );
    public int insertT2MappedCommon( D1CommonVO commonSheet );
}
