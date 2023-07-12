package com.emedlogix.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import com.emedlogix.entity.Chapter;
import com.emedlogix.entity.CodeDetails;
import com.emedlogix.entity.CodeInfo;
import com.emedlogix.entity.EIndex;
import com.emedlogix.entity.Notes;
import com.emedlogix.entity.Section;
import com.emedlogix.entity.SectionReference;
import com.emedlogix.repository.ChapterRepository;
import com.emedlogix.repository.DBCodeDetailsRepository;
import com.emedlogix.repository.EIndexRepository;
import com.emedlogix.repository.NotesRepository;
import com.emedlogix.repository.SectionRepository;

import generated.ChapterType;
import generated.ContentType;
import generated.DiagnosisType;
import generated.ICD10CMIndex;
import generated.ICD10CMTabular;
import generated.NoteType;
import generated.SectionIndexType;
import generated.SectionType;
import generated.Term;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;

@Service
public class ExtractorServiceImpl implements ExtractorService {

    public static final Logger logger = LoggerFactory.getLogger(ExtractorServiceImpl.class);
    //@Autowired
    //ESCodeInfoRepository esCodeInfoRepository;
    @Autowired
    DBCodeDetailsRepository dbCodeDetailsRepository;
    @Autowired
    SectionRepository sectionRepository;
    @Autowired
    ChapterRepository chapterRepository;

    @Autowired
    NotesRepository notesRepository;
    
    @Autowired
    EIndexRepository eIndexRepository;

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
        String fileStr = "/Users/mnachiappan/Documents/Development/clarit/icd10cm_tabular_2023.xml";
        logger.info("Start Extracting Chapter Section from XML file:{}", fileStr);
        try {
            JAXBContext context = JAXBContext.newInstance(ICD10CMTabular.class);
            ICD10CMTabular tabular = (ICD10CMTabular) context.createUnmarshaller()
                    .unmarshal(new FileReader(ResourceUtils.getFile("classpath:icd10cm_tabular_2023.xml")));
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
        String fileStr = "/Users/mnachiappan/Documents/Development/clarit/icd10cm_order_2023.txt";
        logger.info("Start Extracting Ordered Codes from file {}", fileStr);
        Map<String, CodeDetails> codeDetailsMap = new HashMap<>();
        Map<String, CodeInfo> codeMap = new HashMap<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(ResourceUtils.getFile("classpath:icd10cm_order_2023.txt")));
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
            //esCodeInfoRepository.saveAll(codeMap.values());
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

	@Override
	public void doExtractNeoplasm() {		
		Object obj = parseXML("test_neoplasm.xml", ICD10CMIndex.class);
		if(obj instanceof ICD10CMIndex) {
			ICD10CMIndex icd10CMIndex = (ICD10CMIndex)obj;
			icd10CMIndex.getLetter().stream().forEach(l -> {
				l.getMainTerm().stream().forEach(m -> {					
					System.out.println("mainterm title ::"+m.getTitle().getContent().get(0).toString());
					
					m.getCell().stream().forEach(f -> {
						f.getContent().stream().forEach( j -> {
							System.out.println("mainterm cell ::"+j.toString());
						});
					});
					
					if(!m.getTerm().isEmpty()) {
						parseNeoPlasmLevelTerm(m.getTerm());
					}
				});
			});
		}	
	}
	
	public Object parseXML(String fileName, Class className) {
		try {
			//File file = ResourceUtils.getFile("classpath:"+fileName);		    
	        JAXBContext jaxbContext = JAXBContext.newInstance(className);

			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

			return jaxbUnmarshaller.unmarshal(new InputStreamReader(new ClassPathResource("classpath:"+fileName).getInputStream()));
		} catch (JAXBException | IOException e) {
			logger.error("doExtractICD10CMCodes error...", e.getMessage(),e.fillInStackTrace());
		}
		return new Object();
	}
	
	private void parseNeoPlasmLevelTerm(List<Term> termType) {
		termType.forEach(a -> {
			System.out.println("levelterm title :: "+a.getTitle().getContent().get(0).toString());
			System.out.println("levelterm level :: "+a.getLevel());
			a.getCell().forEach(c -> {
				c.getContent().stream().forEach( j -> {
					System.out.println("levelterm cell ::"+j.toString());
				});
			});
			if(!a.getTerm().isEmpty()) {
				parseNeoPlasmLevelTerm(a.getTerm());
			}
		});
	}

	@Override
	public void doExtractIndex() {
		Object obj = parseXML("icd10cm_eindex_2023.xml",ICD10CMIndex.class);
		if(obj instanceof ICD10CMIndex) {
			ICD10CMIndex icd10CMIndex = (ICD10CMIndex)obj;
			List<EIndex> eIndexArray = new ArrayList<>();
			icd10CMIndex.getLetter().stream().forEach(l -> {
				l.getMainTerm().stream().forEach(m -> {
					EIndex eIndex = new EIndex();
					eIndex.setTitle(m.getTitle().getContent().get(0).toString());
					eIndex.setCode(m.getCode());
					eIndex.setSee(m.getSee());
					eIndex.setSeealso(m.getSeeAlso());
					eIndex.setSeecat(m.getSeecat());
					eIndex.setIsmainterm(true);
					eIndexArray.add(eIndex);				
				});
				eIndexRepository.saveAll(eIndexArray);
			});
		}
	}
}


