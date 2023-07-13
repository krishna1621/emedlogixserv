package com.emedlogix.service;

import com.emedlogix.entity.*;
import com.emedlogix.repository.*;
import generated.*;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

@Service
public class ExtractorServiceImpl implements ExtractorService {

    public static final Logger logger = LoggerFactory.getLogger(ExtractorServiceImpl.class);
    @Autowired
    ESCodeInfoRepository esCodeInfoRepository;
    @Autowired
    DBCodeDetailsRepository dbCodeDetailsRepository;
    @Autowired
    SectionRepository sectionRepository;
    @Autowired
    ChapterRepository chapterRepository;

    @Autowired
    NotesRepository notesRepository;

    private static List<Section> parseSection(DiagnosisType diagnosisType, String version, String icdRef, String chapterId, List<Section> sections) {
        List<JAXBElement<?>> inclusionTermOrSevenChrNoteOrSevenChrDef = diagnosisType.getInclusionTermOrSevenChrNoteOrSevenChrDef();
        for (int i = 0; i < inclusionTermOrSevenChrNoteOrSevenChrDef.size(); i++) {
            if (inclusionTermOrSevenChrNoteOrSevenChrDef.get(i).getValue() instanceof NoteType) {
                String classification = inclusionTermOrSevenChrNoteOrSevenChrDef.get(i).getName().getLocalPart();
                NoteType noteTypeVaule = (NoteType) inclusionTermOrSevenChrNoteOrSevenChrDef.get(i).getValue();
                sections.add(parseItems(noteTypeVaule, version, icdRef, chapterId, diagnosisType.getName().getContent().get(0).toString(), classification));
            } else {
                if (inclusionTermOrSevenChrNoteOrSevenChrDef.get(i).getValue() instanceof DiagnosisType) {
                    parseSection((DiagnosisType) inclusionTermOrSevenChrNoteOrSevenChrDef.get(i).getValue(), version, icdRef, chapterId, sections);
                }
            }
        }
        return sections;
    }

    private static Section parseItems(NoteType noteTypeVaule, String version, String icdRef, String chapterId, String code, String classification) {
        Section section = new Section();
        section.setCode(code.replace(".", ""));
        section.setVersion(version);
        section.setChapterId(chapterId);
        section.setIcdReference(icdRef);
        List<ContentType> contentTypes = noteTypeVaule.getNote();
        if (contentTypes != null && !contentTypes.isEmpty()) {
            for (ContentType contentType : contentTypes) {
                Iterator iter = contentType.getContent().listIterator();
                while (iter.hasNext()) {
                    String note = (String) iter.next();
                    Notes notes = new Notes();
                    notes.setClassification(classification);
                    notes.setNotes(note);
                    notes.setType("SECTION");
                    notes.setVersion(version);
                    notes.setId(UUID.randomUUID().toString());
                    notes.setSection(section);
                    section.getNotes().add(notes);
                }
            }
        }
        return section;
    }

    @Override
    public void doExtractCapterSectionXML() {
        String fileStr = "D:\\textfile\\icd10cm_tabular.xml";
        logger.info("Start Extracting Chapter Section from XML file:{}", fileStr);
        try {
            JAXBContext context = JAXBContext.newInstance(ICD10CMTabular.class);
            ICD10CMTabular tabular = (ICD10CMTabular) context.createUnmarshaller()
                    .unmarshal(new FileReader(fileStr));
            String version = tabular.getVersion().getContent().get(0).toString();
            String icdtitle = tabular.getIntroduction().getIntroSection().get(0).getTitle().getContent().get(0).toString();
            icdtitle = icdtitle.substring(icdtitle.indexOf(" "));
            //icd_id: tabular.getIntroduction().getIntroSection().get(0).getTitle().getContent().get(0);
            Iterator<ChapterType> tabIter = tabular.getChapter().listIterator();
            while (tabIter.hasNext()) {
                ChapterType chapterType = tabIter.next();
                Chapter chapter = new Chapter();
                chapter.setIcdReference(icdtitle);
                chapter.setId(chapterType.getName().getContent().get(0).toString());
                chapter.setDescription(chapterType.getDesc().getContent().get(0).toString());
                chapter.setVersion(version);
                List<SectionReference> sectionReferences = new ArrayList<>();
                ListIterator<JAXBElement<?>> NodeIter = chapterType.getInclusionTermOrSevenChrNoteOrSevenChrDef().listIterator();
                while (NodeIter.hasNext()) {
                    JAXBElement element = NodeIter.next();
                    if (element.getValue() instanceof NoteType) {
                        NoteType noteType = (NoteType) element.getValue();
                        Iterator<ContentType> contenIter = noteType.getNote().listIterator();
                        while (contenIter.hasNext()) {
                            ContentType contentType = contenIter.next();
                            Iterator iter = contentType.getContent().listIterator();
                            while (iter.hasNext()) {
                                String note = (String) iter.next();
                                Notes notes = new Notes();
                                notes.setClassification(element.getName().toString());
                                notes.setNotes(note);
                                notes.setType("CHAPTER");
                                notes.setVersion(version);
                                notes.setId(UUID.randomUUID().toString());
                                notes.setChapter(chapter);
                                chapter.getNotes().add(notes);
                            }
                        }
                    } else if (element.getValue() instanceof SectionType) {
                        List<Section> sections = new ArrayList<>();
                        String sectionRefId = ((SectionType) element.getValue()).getId();
                        SectionType sectionType = (SectionType) element.getValue();
                        if (sectionType.getDeactivatedOrDiag() != null && !sectionType.getDeactivatedOrDiag().isEmpty()) {
                            for (Object diagnosisTypeObj : sectionType.getDeactivatedOrDiag()) {
                                DiagnosisType diagnosisType = (DiagnosisType) diagnosisTypeObj;
                                parseSection(diagnosisType, chapter.getVersion(), chapter.getIcdReference(), chapter.getId(), sections);
                            }
                            //save section;
                            logger.info("Saving Section into DB: size {}", sections.size());
                            sectionRepository.saveAll(sections);
                            logger.info("Saved Section into DB: Successfully {}", sections.size());
                        } else {
                            //parseSection(sectionType.getInclusionTermOrSevenChrNoteOrSevenChrDef().listIterator()
                            //      , chapter.getVersion(), chapter.getIcdReference());
                        }
                    } else if (element.getValue() instanceof SectionIndexType) {
                        SectionReference sectionReference = new SectionReference();
                        SectionIndexType sectionIndexType = (SectionIndexType) element.getValue();
                        sectionReference.setId(sectionIndexType.getId());
                        sectionReference.setIcdReference(chapter.getIcdReference());
                        sectionReference.setNotes(sectionIndexType.getSectionRef().get(0).getValue());
                        sectionReference.setLast(sectionIndexType.getSectionRef().get(0).getLast());
                        sectionReference.setFirst(sectionIndexType.getSectionRef().get(0).getFirst());
                        sectionReference.setVersion(chapter.getVersion());
                        sectionReference.setChapterId(chapter.getId());
                        sectionReferences.add(sectionReference);
                    }
                }
                //save chapter
                logger.info("Saving Chanpter :{}", chapter.getId());
                chapterRepository.save(chapter);
            }
        } catch (Exception e) {
            //TODO cathc the right exception and log it
            logger.error(e.toString(), e);
        }
        logger.info("Chapter section from XML successfully extracted:");
    }

    @Override
    public void doExtractOrderedCodes() {
        String fileStr = "D:\\textfile\\icd10cm_order.txt";
        logger.info("Start Extracting Ordered Codes from file {}", fileStr);
        Map<String, CodeDetails> codeDetailsMap = new HashMap<>();
        Map<String, CodeInfo> codeMap = new HashMap<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(new File(fileStr)));
            String line;
            List<String> lines = new ArrayList<>();
            while ((line = reader.readLine()) != null) {
                if (line.trim().length() > 0) {
                    CodeDetails details = parseCodeDetails(line);
                    codeDetailsMap.put(details.getCode(), details);
                    CodeInfo codeInfo = new CodeInfo();
                    codeInfo.setCode(details.getCode());
                    codeInfo.setDescription(details.getLongDescription());
                    codeMap.put(codeInfo.getCode(), codeInfo);
                }
            }
        } catch (IOException e) {
            logger.error(e.toString(), e);
        }
        doSaveCodesToES(codeMap);
        doSaveOrderedCodesToDB(codeDetailsMap);
        logger.info("Code details successfully extracted ordered codes {}", codeDetailsMap.size());

    }

    private void doSaveCodesToES(Map<String, CodeInfo> codeMap) {
        if (codeMap != null && !codeMap.isEmpty()) {
            logger.info("Total codes extracted {}", codeMap.entrySet().size());
            esCodeInfoRepository.saveAll(codeMap.values());
        }
        logger.info("Extractor Service Codes completed...");
    }

    private void doSaveOrderedCodesToDB(Map<String, CodeDetails> codeDetailsMap) {
        if (codeDetailsMap != null && !codeDetailsMap.isEmpty()) {
            logger.info("Total codes extracted {}", codeDetailsMap.entrySet().size());
            Iterator<Map.Entry<String, CodeDetails>> itr = codeDetailsMap.entrySet().iterator();
            ArrayList dataList = new ArrayList();
            int counter = 0;
            while (itr.hasNext()) {
                Map.Entry<String, CodeDetails> entry = itr.next();
                CodeDetails fetchCodeDetails = entry.getValue();
                dataList.add(fetchCodeDetails);
                counter++;
                if (dataList.size() % 2000 == 0) {
                    dbCodeDetailsRepository.saveAll(dataList);
                    dataList.clear();

                }
            }
            if (!dataList.isEmpty()) {
                dbCodeDetailsRepository.saveAll(dataList);
            }
        }
        logger.info("Extractor Service Ordered Codes completed...");
    }

    private CodeDetails parseCodeDetails(String input) {
        String[] tokens = input.split("[(?=\\s*$)]");
        CodeDetails codeDetails = new CodeDetails();
        int counter = 0;
        boolean skip = false;
        for (String token : tokens) {
            if (!token.isEmpty()) {
                if (counter == 3 && Character.isUpperCase(token.charAt(0)) && skip) {
                    if (token.length() > 2 && codeDetails.getShortDescription().startsWith(token.substring(0, 2))) {
                        counter++;
                    }
                }
                switch (counter) {
                    case 0:
                        counter++;
                        break;
                    case 1:
                        codeDetails.setCode(token);
                        counter++;
                        break;
                    case 2:
                        codeDetails.setBillable((token.equals("1")));
                        counter++;
                        break;
                    case 3:
                        codeDetails.setShortDescription((concateDescription(codeDetails.getShortDescription(), token)).trim());
                        skip = true;
                        //counter++;
                        break;
                    case 4:
                        codeDetails.setLongDescription((concateDescription(codeDetails.getLongDescription(), token)).trim());
                        //counter++;
                        break;
                }
            } else {
                if (codeDetails.getShortDescription() != null && codeDetails.getShortDescription().length() > 0 && counter == 3) {
                    counter++;
                }
            }
        }
        return codeDetails;
    }

    private String concateDescription(String previous, String current) {
        return (previous == null ? "" : previous) + " " + current.trim();
    }
}


