package com.emedlogix.service;


import com.emedlogix.controller.CodeSearchController;
import com.emedlogix.entity.CodeDetails;
import com.emedlogix.entity.CodeInfo;
import com.emedlogix.entity.Section;
import com.emedlogix.repository.ChapterRepository;
import com.emedlogix.repository.DBCodeDetailsRepository;
import com.emedlogix.repository.ESCodeInfoRepository;
import com.emedlogix.repository.SectionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


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

    @Override
    public List<CodeInfo> getAllInfo(String searchTerm) {
        List<CodeInfo> codeInfoList = new ArrayList<>();

        Iterable<CodeInfo> codeDetailsIterable = esCodeInfoRepository.findByCodeStartingWith(searchTerm);
        Iterator<CodeInfo> codeIterator = codeDetailsIterable.iterator();
        while (codeIterator.hasNext()) {
            CodeInfo codeInfo = codeIterator.next();
            codeInfoList.add(codeInfo);
        }

        Iterable<CodeInfo> descriptionIterable = esCodeInfoRepository.findByDescriptionContains(searchTerm);
        Iterator<CodeInfo> descriptionIterator = descriptionIterable.iterator();
        while (descriptionIterator.hasNext()) {
            CodeInfo codeInfo = descriptionIterator.next();
            if (!codeInfoList.contains(codeInfo)) {
                codeInfoList.add(codeInfo);
            }
        }

        logger.info("Got matching results size: " + codeInfoList.size());
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


}
