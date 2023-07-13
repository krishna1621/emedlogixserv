package com.emedlogix.service;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import com.emedlogix.controller.CodeSearchController;
import com.emedlogix.entity.CodeDetails;
import com.emedlogix.entity.CodeInfo;
import com.emedlogix.entity.EIndex;
import com.emedlogix.entity.Section;
import com.emedlogix.repository.ChapterRepository;
import com.emedlogix.repository.DBCodeDetailsRepository;
import com.emedlogix.repository.DrugRepository;
import com.emedlogix.repository.EIndexRepository;
import com.emedlogix.repository.ESCodeInfoRepository;
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
    EIndexRepository eIndexRepository;
    
    @Autowired
    NeoPlasmRepository neoPlasmRepository;
    
    @Autowired
    DrugRepository drugRepository;

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
	public List<EIndex> getEIndex(String code, String filterBy) {
		return eIndexRepository.findMainTermBySearch(code,filterBy);
	}

	@Override
	public List<Map<String,Object>> getNeoPlasm(String code) {
		return neoPlasmRepository.findNeoplasmByCode(code);
	}
	
	@Override
	public List<Map<String,Object>> getDrug(String code) {
		return drugRepository.findDrugByCode(code);
	}
}
