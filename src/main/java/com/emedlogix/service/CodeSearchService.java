package com.emedlogix.service;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.emedlogix.entity.*;
import com.emedlogix.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import com.emedlogix.controller.CodeSearchController;
import org.springframework.web.bind.annotation.RequestParam;


@Service
public class CodeSearchService implements CodeSearchController {

    public static final Logger logger = LoggerFactory.getLogger(CodeSearchService.class);
    private static final String INDEX_NAME = "details";
    private static final String FIELD_NAME = "code";

    @Autowired
    ESCodeInfoRepository esCodeInfoRepository;

	@Autowired
	ESIndexLevelSearchRepository esIndexLevelSearchRepository;

	@Autowired
	ESIndexLoad esIndexLoad;

    @Autowired
    DBCodeDetailsRepository dbCodeDetailsRepository;
    @Autowired
    SectionRepository sectionRepository;
    @Autowired
    ChapterRepository chapterRepository;
    
    @Autowired
    NeoPlasmRepository neoPlasmRepository;

    @Autowired
    DrugRepository drugRepository;

    @Autowired
    EindexRepository eindexRepository;

    @Override
    public CodeInfo getCodeInfo(String code) {
        logger.info("Getting Code Information for:", code);
        CodeInfo codeInfo = esCodeInfoRepository.getByCode(code);
        return codeInfo;
    }

    public List<CodeInfo> getCodeInfoMatches(String code) {
        logger.info("Getting Code Information for code starts with:", code);
        List<CodeInfo> codeInfoList = new ArrayList<>();
        Iterable<CodeInfo> codeDetailsIterable = esCodeInfoRepository.findByCodeStartingWith(code);
        Iterator<CodeInfo> it = codeDetailsIterable.iterator();
        while (it.hasNext()) {
            CodeInfo codeInfo = it.next();
            codeInfoList.add(codeInfo);
        }
        logger.info("Got matching codes size:", codeInfoList.size());
        return codeInfoList;
    }

	@Override
	public List<CodeInfo> getCodeInfoDescription(String description) {
		logger.info("Getting Code information Details for Description:",description);
		List<CodeInfo> codeInfoList = new ArrayList<>();
		Iterable<CodeInfo> codeInfoIterable = esCodeInfoRepository.findByDescriptionContains(description);
		Iterator<CodeInfo> infoIterator = codeInfoIterable.iterator();
		while (infoIterator.hasNext()){
			CodeInfo codeInfo = infoIterator.next();
			codeInfoList.add(codeInfo);
		}
		logger.info("Got description size :",codeInfoList.size());
		return codeInfoList;
	}

    public CodeDetails getCodeInfoDetails(@PathVariable String code, @RequestParam String version){
        logger.info("Getting Code Information Details for code:", code);
        CodeDetails codeDetails = dbCodeDetailsRepository.findByCode(code);
        Section section = sectionRepository.findByCodeAndVersion(code,version);
        if(section != null) {
            codeDetails.setSection(section);
            chapterRepository.findById(section.getChapterId()).ifPresent(value -> {
                codeDetails.setChapter(value);
            });
        }
        //codeDetails.setChapter(.get());
        return codeDetails;
    }

	@Override
	public List<EindexVO> getEIndex(String code) {
		return eindexRepository.findMainTermBySearch(code).stream().map(m -> {
			return getParentChildHierarchy(m);
		}).collect(Collectors.toList());
	}

	@Override
	public List<Eindex> getIndexDetails(){
	/*	List<Map<String,Object>> allIndexData = eindexRepository.findAllIndexData();
		return allIndexData.stream().map(m -> {
			return populateEindexVO(m);
		}).collect(Collectors.toList());

	 */
		return eindexRepository.findAll();
	}

	@Override
	public List<MedicalCodeVO> getNeoPlasm(String code) {
		return neoPlasmRepository.findNeoplasmByCode(code).stream().map(m -> {
			return getDrugNeoplasmHierarchy(m,"neoplasm");
		}).collect(Collectors.toList());
	}

	@Override
	public List<MedicalCodeVO> getNeoplasmDetails(){
	List<Map<String,Object>> allNeoplasmData = neoPlasmRepository.findAllNeoplasmData();
	return  allNeoplasmData.stream().map(m -> {
		return populateMedicalCode(m);
	}).collect(Collectors.toList());
	}
	@Override
	public List<MedicalCodeVO> getDrug(String code) {
		return drugRepository.findDrugByCode(code).stream().map(m -> {
			return getDrugNeoplasmHierarchy(m,"drug");
		}).collect(Collectors.toList());
	}
	public List<MedicalCodeVO> getDrugDetails(){
		List<Map<String,Object>> allDrugData = drugRepository.findAllDrugData();
		return allDrugData.stream().map(m -> {
			return populateMedicalCode(m);
		}).collect(Collectors.toList());
	}

	@Override
	public void getLevelTerms() {

		 esIndexLoad.loadIndexMainTerm();
	}

	@Override
	public EindexLevels getSearchTerm(String title) {

		return esIndexLevelSearchRepository.getByTitle(title);
	}


	private MedicalCodeVO getDrugNeoplasmHierarchy(Map<String, Object> m,String type) {		
		MedicalCodeVO resultMedicalCode = null;
		List<Map<String,Object>> resultMap = new ArrayList<>();
		if (type == "neoplasm") {
			resultMap = neoPlasmRepository.getParentChildList(Integer.valueOf(String.valueOf(m.get("id"))));
		} else if (type == "drug") {
			resultMap = drugRepository.getParentChildList(Integer.valueOf(String.valueOf(m.get("id"))));
		}
		for(int x = 0; x < resultMap.size(); x++) {
			if(resultMedicalCode == null) {
				resultMedicalCode = populateMedicalCode(m);
			} else {
				MedicalCodeVO medicalCode = populateMedicalCode(resultMap.get(x));
				medicalCode.setChild(resultMedicalCode);
				resultMedicalCode = medicalCode;
			}
		}
		if(resultMedicalCode == null) {
			resultMedicalCode = new MedicalCodeVO();
		}
		
		return resultMedicalCode;
	}

	private EindexVO getParentChildHierarchy(Eindex eindex) {
		EindexVO resultEindexVO = null;
		List<Map<String,Object>> resultMap = eindexRepository.getParentChildList(eindex.getId());
		for(int x = 0; x < resultMap.size(); x++) {
			if(resultEindexVO == null) {
				resultEindexVO = populateEindexVO(resultMap.get(x));
			} else {
				EindexVO eindexVO = populateEindexVO(resultMap.get(x));
				eindexVO.setChild(resultEindexVO);
				resultEindexVO = eindexVO;
			}
		}
		if(resultEindexVO == null) {
			resultEindexVO = new EindexVO();
		}
		return resultEindexVO;
	}

	private EindexVO populateEindexVO(Map<String,Object> map) {
		EindexVO eindexVo = new EindexVO();
		eindexVo.setId(Integer.parseInt(map.get("id").toString()));
		eindexVo.setTitle(String.valueOf(map.get("title")));
		eindexVo.setSee(String.valueOf(map.get("see")));
		eindexVo.setSeealso(String.valueOf(map.get("seealso")));
		eindexVo.setIsmainterm(Boolean.valueOf(map.get("ismainterm").toString()));
		eindexVo.setCode(String.valueOf(map.get("code")));
		eindexVo.setNemod(String.valueOf(map.get("nemod")));
		return eindexVo;
	}
	
	private MedicalCodeVO populateMedicalCode(Map<String, Object> m) {
		MedicalCodeVO medicalCode = new MedicalCodeVO();
		medicalCode.setId(Integer.valueOf(String.valueOf(m.get("id"))));
		medicalCode.setTitle(String.valueOf(m.get("title")));
		medicalCode.setSee(String.valueOf(m.get("see")));
		medicalCode.setSeealso(String.valueOf(m.get("seealso")));
		medicalCode.setIsmainterm(Boolean.valueOf(String.valueOf(m.get("ismainterm"))));
		medicalCode.setNemod(String.valueOf(m.get("nemod")));
		medicalCode.setCode(Arrays.asList(String.valueOf(m.get("code")).split(",")));
		return medicalCode;
	}
}
