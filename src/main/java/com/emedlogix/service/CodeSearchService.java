package com.emedlogix.service;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import com.emedlogix.controller.CodeSearchController;
import com.emedlogix.entity.CodeDetails;
import com.emedlogix.entity.CodeInfo;
import com.emedlogix.entity.Eindex;
import com.emedlogix.entity.EindexVO;
import com.emedlogix.entity.MedicalCodeVO;
import com.emedlogix.entity.Section;
import com.emedlogix.repository.ChapterRepository;
import com.emedlogix.repository.DBCodeDetailsRepository;
import com.emedlogix.repository.DrugRepository;
import com.emedlogix.repository.ESCodeInfoRepository;
import com.emedlogix.repository.EindexRepository;
import com.emedlogix.repository.NeoPlasmRepository;
import com.emedlogix.repository.SectionRepository;


@Service
public class CodeSearchService implements CodeSearchController {

    public static final Logger logger = LoggerFactory.getLogger(CodeSearchService.class);
    private static final String INDEX_NAME = "details";
    private static final String FIELD_NAME = "code";

    @Autowired
    ESCodeInfoRepository esCodeInfoRepository;

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

    public CodeDetails getCodeInfoDetails(@PathVariable String code){
        logger.info("Getting Code Information Details for code:", code);
        CodeDetails codeDetails = dbCodeDetailsRepository.findByCode(code);
        Section section = sectionRepository.findByCode(code);
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
			return populateEindex(m);
		}).collect(Collectors.toList());
	}

	@Override
	public List<MedicalCodeVO> getNeoPlasm(String code) {
		return neoPlasmRepository.findNeoplasmByCode(code).stream().map(m -> {
			return populateMedicalCode(m);
		}).collect(Collectors.toList());
	}
	
	@Override
	public List<MedicalCodeVO> getDrug(String code) {
		return drugRepository.findDrugByCode(code).stream().map(m -> {
			return populateMedicalCode(m);
		}).collect(Collectors.toList());
	}

	private MedicalCodeVO populateMedicalCode(Map<String, Object> m) {
		MedicalCodeVO medicalCode = new MedicalCodeVO();
		medicalCode.setId(Integer.valueOf(String.valueOf(m.get("id"))));
		medicalCode.setTitle(String.valueOf(m.get("title")));
		medicalCode.setSee(String.valueOf(m.get("see")));
		medicalCode.setSeealso(String.valueOf(m.get("seealso")));
		medicalCode.setIsmainterm(Boolean.valueOf(String.valueOf(m.get("ismainterm"))));
		medicalCode.setCode(Arrays.asList(String.valueOf(m.get("code")).split(",")));
		return medicalCode;
	}

	private EindexVO populateEindex(Eindex eindex) {
		EindexVO eindexVo = new EindexVO();
		eindexVo.setId(eindex.getId());
		eindexVo.setTitle(eindex.getTitle());
		eindexVo.setSee(eindex.getSee());
		eindexVo.setSeealso(eindex.getSeealso());
		eindexVo.setIsmainterm(eindex.getIsmainterm());
		eindexVo.setCode(eindex.getCode());
		if(!eindex.getIsmainterm()) {
			eindexVo.setTermHierarchyList(eindexRepository.getParentChildList(eindex.getId()));
		}
		return eindexVo;
	}
}
