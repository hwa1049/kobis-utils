-- 부처별 보유현황
select 
	(select ins_name from X2_InstitutionCode where ins_cd=A.ins_cd) ins_nm
	, ins_cd
	, count(*) cnt
from D1_Common A, (
	select distinct accession_num from (
		-- 분류체계 검색
		select 
			A.accession_num
		from D1_Common A, (
			select tab_id from T1_ClassificationSystemTable
			where match(scientific_name) against('sativa' in boolean mode)
			union all
			select tab_id from T1_ClassificationSystemTable
			where kobic_tax_id in (
				select tax_id from X1_KobicTaxonomyKorName
				where match(kor_name) against('sativa' in boolean mode)
			)
		) B
		where A.tab_id=B.tab_id

		union all

		-- 이름으로 검색
		select
			accession_num
		from D1_Common
		where match(common_name, kor_name, line_name, variety_name, institution, category_1, category_2, category_3, keywords, family, genus, subgenus, species, in_species_type, in_species_name, taxonomy)
		against('sativa' in boolean mode)
	) KK
) AA
where A.accession_num=AA.accession_num
group by A.ins_cd;


-- 분류체계에서 조회
select type, sum(cnt) cnt from (
	select 
		BB.type, IF(AA.type is not null, 1, 0) cnt
	from (
		select type, tax_id from (
			select 'KOBIC' type, tax_id from X1_KobicTaxonomyName
			where match(name_txt) against('"Allomyrina dichotoma"' in boolean mode)
			union all
			select 'KOBIC' type, tax_id from X1_KobicTaxonomyKorName
			where match(kor_name) against('"Allomyrina dichotoma"' in boolean mode)
		) KK
		group by type, tax_id
		union all
		select 'NCBI' type, tax_id from X1_NcbiTaxonomyName
		where match(name_txt) against('"Allomyrina dichotoma"' in boolean mode)
		union all
		select 'GBIF' type, tax_id from X1_GbifTaxonomyName
		where match(name_txt) against('"Allomyrina dichotoma"' in boolean mode)
		union all
		select 'ITIS' type, tax_id from X1_ItisTaxonomyName
		where match(name_txt) against('"Allomyrina dichotoma"' in boolean mode)
	) AA right outer join ( select 'KOBIC' type union all select 'NCBI' type union all select 'GBIF' type union all select 'ITIS' ) BB
	on AA.type=BB.type
) KK
group by type
order by cnt desc;

-- 유전자 정보로 검색
select
	A.tax_id
	, A.GeneID
	, A.Symbol
	, A.LocusTag
	, A.Synonyms
	, A.description
	, (select count(*) from T1_ClassificationSystemTable where ncbi_tax_id=A.tax_id) count
from G1_GeneInfo A
where match(symbol, locustag, synonyms, chromosome, description) against('SC35')
order by count desc
limit 5;

-- 부처별 카운트 테이블
select
	sum(d1_spec_cnt) d1_spec_cnt
	, sum(d1_obs_cnt) d1_obs_cnt
	, sum(d1_ind_cnt) d1_ind_cnt
	, sum(d1_org_cnt) d1_org_cnt
	, sum(d1_sou_cnt) d1_sou_cnt
	, sum(d1_see_cnt) d1_see_cnt
	, sum(d1_emb_cnt) d1_emb_cnt
	, sum(d1_drpd_cnt) d1_drpd_cnt
	, sum(d1_strain_cnt) d1_strain_cnt
	, sum(d1_cell_cnt) d1_cell_cnt
	, sum(d1_body_cnt) d1_body_cnt
	, sum(d1_protseq_cnt) d1_protseq_cnt
	, sum(d1_exp_cnt) d1_exp_cnt
	, sum(d1_str_cnt) d1_str_cnt
	, sum(d1_dnaseq_cnt) d1_dnaseq_cnt
	, sum(d1_etc_cnt) d1_etc_cnt
	, sum(d1_ext_cnt) d1_ext_cnt
from (
	select
		A.accession_num
		, (select count(*) from D1_Specimen where accession_num=A.accession_num) d1_spec_cnt
		, (select count(*) from D1_Observation where accession_num=A.accession_num) d1_obs_cnt
		, (select count(*) from D1_Individual where accession_num=A.accession_num) d1_ind_cnt
		, (select count(*) from D1_Organ where accession_num=A.accession_num) d1_org_cnt
		, (select count(*) from D1_Source where accession_num=A.accession_num) d1_sou_cnt
		, (select count(*) from D1_Seed where accession_num=A.accession_num) d1_see_cnt
		, (select count(*) from D1_Embryo where accession_num=A.accession_num) d1_emb_cnt
		, (select count(*) from D1_DNA_RNA_Protein_Derivatives where accession_num=A.accession_num) d1_drpd_cnt
		, (select count(*) from D1_Strain where accession_num=A.accession_num) d1_strain_cnt
		, (select count(*) from D1_CellStrain where accession_num=A.accession_num) d1_cell_cnt
		, (select count(*) from D1_BodyFluid where accession_num=A.accession_num) d1_body_cnt
		, (select count(*) from D1_ProteinSequence where accession_num=A.accession_num) d1_protseq_cnt
		, (select count(*) from D1_Expression where accession_num=A.accession_num) d1_exp_cnt
		, (select count(*) from D1_Structure where accession_num=A.accession_num) d1_str_cnt
		, (select count(*) from D1_Dna_Sequence where accession_num=A.accession_num) d1_dnaseq_cnt
		, (select count(*) from D1_Etc where accession_num=A.accession_num) d1_etc_cnt
		, (select count(*) from D1_Extraction where accession_num=A.accession_num) d1_ext_cnt
	from D1_Common A, (
		select distinct accession_num from (
			-- 분류체계 검색
			select 
				A.accession_num
			from D1_Common A, (
				select tab_id from T1_ClassificationSystemTable
				where match(scientific_name) against('sativa' in boolean mode)
				union all
				select tab_id from T1_ClassificationSystemTable
				where kobic_tax_id in (
					select tax_id from X1_KobicTaxonomyKorName
					where match(kor_name) against('sativa' in boolean mode)
				)
			) B
			where A.tab_id=B.tab_id
			union all
			-- 이름으로 검색
			select
				accession_num
			from D1_Common
			where match(common_name, kor_name, line_name, variety_name, institution, category_1, category_2, category_3, keywords, family, genus, subgenus, species, in_species_type, in_species_name, taxonomy)
			against('sativa' in boolean mode)
		) KK
	) AA
	where A.accession_num=AA.accession_num
	and A.ins_cd='INS00002'
) AA;

-- 각 부처별 상세 리스트
select
	A.accession_num
from D1_Common A, (
	select distinct accession_num from (
		-- 분류체계 검색
		select 
			A.accession_num
		from D1_Common A, (
			select tab_id from T1_ClassificationSystemTable
			where match(scientific_name) against('sativa' in boolean mode)
			union all
			select tab_id from T1_ClassificationSystemTable
			where kobic_tax_id in (
				select tax_id from X1_KobicTaxonomyKorName
				where match(kor_name) against('sativa' in boolean mode)
			)
		) B
		where A.tab_id=B.tab_id
		union all
		-- 이름으로 검색
		select
			accession_num
		from D1_Common
		where match(common_name, kor_name, line_name, variety_name, institution, category_1, category_2, category_3, keywords, family, genus, subgenus, species, in_species_type, in_species_name, taxonomy)
		against('sativa' in boolean mode)
	) KK
) AA
where A.accession_num=AA.accession_num
and A.ins_cd='INS00002';