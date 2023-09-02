package com.emedlogix.service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
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

import com.emedlogix.codes.ChapterType;
import com.emedlogix.codes.ContentType;
import com.emedlogix.codes.DiagnosisType;
import com.emedlogix.codes.ICD10CMTabular;
import com.emedlogix.codes.NoteType;
import com.emedlogix.codes.SectionIndexType;
import com.emedlogix.codes.SectionType;
import com.emedlogix.codes.VisualImpairmentType;
import com.emedlogix.entity.Category;
import com.emedlogix.entity.Chapter;
import com.emedlogix.entity.CodeDetails;
import com.emedlogix.entity.CodeInfo;
import com.emedlogix.entity.Drug;
import com.emedlogix.entity.DrugCode;
import com.emedlogix.entity.DrugHierarchy;
import com.emedlogix.entity.Eindex;
import com.emedlogix.entity.NeoPlasmCode;
import com.emedlogix.entity.Neoplasm;
import com.emedlogix.entity.NeoplasmHierarchy;
import com.emedlogix.entity.Notes;
import com.emedlogix.entity.Section;
import com.emedlogix.entity.SectionReference;
import com.emedlogix.entity.TermHierarchy;
import com.emedlogix.entity.VisRange;
import com.emedlogix.entity.VisualImpairment;
import com.emedlogix.index.ICD10CMIndex;
import com.emedlogix.index.MainTerm;
import com.emedlogix.index.Term;
import com.emedlogix.repository.ChapterRepository;
import com.emedlogix.repository.DBCodeDetailsRepository;
import com.emedlogix.repository.DrugCodeRepository;
import com.emedlogix.repository.DrugHierarchyRepository;
import com.emedlogix.repository.DrugRepository;
import com.emedlogix.repository.ESCodeInfoRepository;
import com.emedlogix.repository.EindexRepository;
import com.emedlogix.repository.NeoPlasmCodeRepository;
import com.emedlogix.repository.NeoPlasmRepository;
import com.emedlogix.repository.NeoplasmHierarchyRepository;
import com.emedlogix.repository.NotesRepository;
import com.emedlogix.repository.SectionRepository;
import com.emedlogix.repository.TermHierarchyRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.sun.org.apache.xerces.internal.dom.ElementNSImpl;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;

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
    
    @Autowired
    EindexRepository eindexRepository;
    
    @Autowired
    NeoPlasmRepository neoPlasmRepository;
    
    @Autowired
    NeoPlasmCodeRepository neoPlasmCodeRepository;
    
    @Autowired
    DrugRepository drugRepository;
    
    @Autowired
    DrugCodeRepository drugCodeRepository;

    @Autowired
    TermHierarchyRepository hierarchyRepository;

    @Autowired
    NeoplasmHierarchyRepository neoplasmHierarchyRepository;

    @Autowired
    DrugHierarchyRepository drugHierarchyRepository;

    private List<Section> parseSection(DiagnosisType diagnosisType, String version, String icdRef, String chapterId, List<Section> sections) throws JsonProcessingException {
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
                if(inclusionTermOrSevenChrNoteOrSevenChrDef.get(i).getValue() instanceof VisualImpairmentType) {
                    VisualImpairmentType visualImpairmentType = (VisualImpairmentType) inclusionTermOrSevenChrNoteOrSevenChrDef.get(i).getValue();
                    VisualImpairment visualImpairment = new VisualImpairment();
                    visualImpairmentType.getVisCategory().stream().forEach(
                            visCategory -> {
                                if(visCategory.getHeading() != null)visualImpairment.setCategoryHeading(
                                        ((ElementNSImpl) visCategory.getHeading()).getFirstChild().getNodeValue());
                                Category category = new Category();
                                visualImpairment.getCategoriesList().add(category);
                                if(visCategory.getValue() != null)category.setValue(((ElementNSImpl) visCategory.getValue()).getFirstChild().getNodeValue());
                                if(visCategory.getHeading() != null)visualImpairment.setCategoryHeading(((ElementNSImpl) visCategory.getHeading()).getFirstChild().getNodeValue());
                                visCategory.getVisRange().stream().forEach(visRange -> populateVisRange(visualImpairment, category, visRange));

                            }
                    );
                    ObjectWriter ow = new ObjectMapper().writer();
                    String json = ow.writeValueAsString(visualImpairment);
                    logger.info("JSON : {}", json);
                    sections.get(sections.size()-1).setVisImpair(json);
                }
            }
        }
        return sections;
    }

    private void populateVisRange(VisualImpairment visualImpairment, Category category, VisualImpairmentType.VisCategory.VisRange visRange){
        if(visRange.getHeading() != null)visualImpairment.setRangeHeading(((ElementNSImpl) visRange.getHeading()).getFirstChild().getNodeValue());
        if(visRange.getVisMin()!= null && visRange.getVisMin().getHeading() != null)visualImpairment.setMinHeading(((ElementNSImpl) visRange.getVisMin().getHeading()).getFirstChild().getNodeValue());
        if(visRange.getVisMax() != null && visRange.getVisMax().getHeading() != null)visualImpairment.setMaxHeading(((ElementNSImpl) visRange.getVisMax().getHeading()).getFirstChild().getNodeValue());
        VisRange range = new VisRange();
        if(visRange.getVisMax() != null && visRange.getVisMax().getValue() != null) range.setMax(((ElementNSImpl) visRange.getVisMax().getValue()).getFirstChild().getNodeValue());
        if(visRange.getVisMin() != null && visRange.getVisMin().getValue() != null) range.setMin(((ElementNSImpl) visRange.getVisMin().getValue()).getFirstChild().getNodeValue());
        category.getVisRangeList().add(range);
    }

    private Section parseItems(NoteType noteTypeVaule, String version, String icdRef, String chapterId, String code, String classification) {
        Section section = new Section();
        section.setCode(replaceDot(code));
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
        String fileStr = "icd10cm_tabular_2023.xml";
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
                        List<Section> sections = new LinkedList<>();
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
        String fileStr = "icd10cm_order_2023.txt";
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

	@Override
	public void doExtractNeoplasm() {//icd10cm_neoplasm_2023.xml,test_neoplasm.xml
		Object obj = parseXML("icd10cm_neoplasm_2023.xml", ICD10CMIndex.class);
		if(obj instanceof ICD10CMIndex) {
			ICD10CMIndex icd10CMIndex = (ICD10CMIndex)obj;
			icd10CMIndex.getLetter().stream().forEach(l -> {
				l.getMainTerm().stream().forEach(m -> {
					final Neoplasm neoplasmOne = populateNeoPlasmMainTerm(m);

					// store neoplasm hierarchy
					List<Integer> ids = new ArrayList<>();
					ids.add(neoplasmOne.getId());
					saveNeoplasmHierarchy(neoplasmOne.getId(),neoplasmOne.getId(),0);

					//store neoplasmcode
					List<NeoPlasmCode> neoplasmCodes = new ArrayList<>();
					m.getCell().stream().forEach(cell -> {
						cell.getContent().stream().forEach( code -> {
							populateNeoPlasmCode(neoplasmOne, neoplasmCodes, code);
						});
					});
					neoPlasmCodeRepository.saveAll(neoplasmCodes);

					if(!m.getTerm().isEmpty()) {
						parseNeoPlasmLevelTerm(m.getTerm(),ids);
					}
				});
			});
		}
	}

	private Neoplasm populateNeoPlasmMainTerm(MainTerm m) {
		Neoplasm neoplasm = new Neoplasm();
		neoplasm.setTitle(m.getTitle().getContent().get(0).toString());
		if(m.getTitle().getContent().size()>1) {
			neoplasm.setNemod(getNemodVal(m.getTitle().getContent().get(1)));
		}
		neoplasm.setSee(m.getSee());
		neoplasm.setSeealso(m.getSeeAlso());
		neoplasm.setIsmainterm(true);
		return neoPlasmRepository.save(neoplasm);
	}

	public Object parseXML(String fileName, Class<?> className) {
		try {
	        JAXBContext jaxbContext = JAXBContext.newInstance(className);

			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

			return jaxbUnmarshaller.unmarshal(new InputStreamReader(new ClassPathResource(fileName).getInputStream()));
		} catch (JAXBException | IOException e) {
			logger.error("doExtractICD10CMCodes error...", e.getMessage(),e.fillInStackTrace());
		}
		return new Object();
	}

	private void parseNeoPlasmLevelTerm(List<Term> termType, List<Integer> ids) {
		termType.forEach(a -> {
			final Neoplasm neoplasm = populateNeoPlasmLavelTerm(a);

			//store hierarchy
			if(a.getLevel() == ids.size()) {
				ids.add(neoplasm.getId());
			} else {
				for (int i=ids.size()-1; i>=a.getLevel(); i--) {
					ids.remove(i);
				}
				ids.add(neoplasm.getId());
			}
			int level = 0;
			for (int i=ids.size()-1; i>=0; i--) {
				saveNeoplasmHierarchy(ids.get(i),neoplasm.getId(),level);
				level++;
			}

			//store neoplasmcode
			List<NeoPlasmCode> neoplasmCodes = new ArrayList<>();
			a.getCell().forEach(c -> {
				c.getContent().stream().forEach( j -> {
					populateNeoPlasmCode(neoplasm, neoplasmCodes, j);
				});
			});
			neoPlasmCodeRepository.saveAll(neoplasmCodes);

			if(!a.getTerm().isEmpty()) {
				parseNeoPlasmLevelTerm(a.getTerm(),ids);
			}
		});
	}

	private void populateNeoPlasmCode(final Neoplasm neoplasmOne, List<NeoPlasmCode> neoplasmCodes, Object obj) {
		NeoPlasmCode neoPlasmCode = new NeoPlasmCode();
		neoPlasmCode.setNeoplasm_id(neoplasmOne.getId());
		neoPlasmCode.setCode(replaceDot(obj.toString()));
		neoplasmCodes.add(neoPlasmCode);
	}

	private Neoplasm populateNeoPlasmLavelTerm(Term a) {
		Neoplasm neoplasm = new Neoplasm();
		neoplasm.setTitle(a.getTitle().getContent().get(0).toString());
		if(a.getTitle().getContent().size()>1) {
			neoplasm.setNemod(getNemodVal(a.getTitle().getContent().get(1)));
		}
		neoplasm.setSee(a.getSee());
		neoplasm.setSeealso(a.getSeeAlso());
		neoplasm.setIsmainterm(false);
		return neoPlasmRepository.save(neoplasm);
	}

	@Override
	public void doExtractIndex() {//test_index.xml, icd10cm_index_2023.xml,icd10cm_eindex_2023.xml
        parseIndexesFile(parseXML("icd10cm_index_2023.xml",ICD10CMIndex.class));
		parseIndexesFile(parseXML("icd10cm_eindex_2023.xml",ICD10CMIndex.class));

	}

	private void parseIndexesFile(Object obj) {
		if(obj instanceof ICD10CMIndex) {
			ICD10CMIndex icd10CMIndex = (ICD10CMIndex)obj;
			icd10CMIndex.getLetter().stream().forEach(l -> {
				l.getMainTerm().stream().forEach(m -> {
					Eindex index = populateAndSaveEIndex(m);
					List<Integer> ids = new ArrayList<>();
					ids.add(index.getId());
					populateAndSaveHierarchy(index.getId(),index.getId(),0);
					if(!m.getTerm().isEmpty()) {
						parseEIndexLevelTerm(m.getTerm(),ids);
					}
				});
			});
		}
	}

	private Eindex populateAndSaveEIndex(MainTerm m) {
		Eindex eIndex = new Eindex();
		eIndex.setTitle(m.getTitle().getContent().get(0).toString());
		if(m.getTitle().getContent().size()>1) {
			eIndex.setNemod(getNemodVal(m.getTitle().getContent().get(1)));
		}
		eIndex.setCode(replaceDot(m.getCode()));
		eIndex.setSee(m.getSee());
		eIndex.setSeealso(m.getSeeAlso());
		eIndex.setSeecat(m.getSeecat());
		eIndex.setIsmainterm(true);
		return eindexRepository.save(eIndex);
	}

	private void populateAndSaveHierarchy(Integer parentId,Integer childId, Integer level) {
		TermHierarchy termHierarchy = new TermHierarchy();
		termHierarchy.setParentId(parentId);
		termHierarchy.setChildId(childId);
		termHierarchy.setLevel(level);
		hierarchyRepository.save(termHierarchy);
	}

	private void parseEIndexLevelTerm(List<Term> term, List<Integer> ids) {
		term.forEach(a -> {
			Eindex index = populateAndSaveEIndexLevelTerm(a);
			if(a.getLevel() == ids.size()) {
				ids.add(index.getId());
			} else {
				for (int i=ids.size()-1; i>=a.getLevel(); i--) {
					ids.remove(i);
				}
				ids.add(index.getId());
			}
			int level = 0;
			for (int i=ids.size()-1; i>=0; i--) {
				populateAndSaveHierarchy(ids.get(i),index.getId(),level);
				level++;
			}
			if(!a.getTerm().isEmpty()) {
				parseEIndexLevelTerm(a.getTerm(),ids);
			}

		});
	}

	private Eindex populateAndSaveEIndexLevelTerm(Term m) {
		Eindex eIndex = new Eindex();
		eIndex.setTitle(m.getTitle().getContent().get(0).toString());
		if(m.getTitle().getContent().size()>1) {
			eIndex.setNemod(getNemodVal(m.getTitle().getContent().get(1)));
		}
		eIndex.setCode(replaceDot(m.getCode()));
		eIndex.setSee(m.getSee());
		eIndex.setSeealso(m.getSeeAlso());
		eIndex.setSeecat(m.getSeecat());
		eIndex.setIsmainterm(false);
		return eindexRepository.save(eIndex);
	}

	@Override
	public void doExtractDrug() {//icd10cm_drug_2023.xml, test_drug.xml
		Object obj = parseXML("icd10cm_drug_2023.xml", ICD10CMIndex.class);
		if(obj instanceof ICD10CMIndex) {
			ICD10CMIndex icd10CMIndex = (ICD10CMIndex)obj;
			icd10CMIndex.getLetter().stream().forEach(l -> {
				l.getMainTerm().stream().forEach(m -> {
					final Drug drug = populateDrugMainTerm(m);

					//save drug hierarchy
					List<Integer> ids = new ArrayList<>();
					ids.add(drug.getId());
					saveDrugHierarchy(drug.getId(),drug.getId(),0);

					List<DrugCode> drugCodes = new ArrayList<>();
					m.getCell().stream().forEach(cell -> {
						cell.getContent().stream().forEach( code -> {
							populateDrugCode(drug, drugCodes, code);
						});
					});
					drugCodeRepository.saveAll(drugCodes);

					if(!m.getTerm().isEmpty()) {
						parseDrugLevelTerm(m.getTerm(), ids);
					}
				});
			});
		}
	}

	private Drug populateDrugMainTerm(MainTerm m) {
		Drug drug = new Drug();
		drug.setTitle(m.getTitle().getContent().get(0).toString());
		if(m.getTitle().getContent().size()>1) {
			drug.setNemod(getNemodVal(m.getTitle().getContent().get(1)));
		}
		drug.setSee(m.getSee());
		drug.setSeealso(m.getSeeAlso());
		drug.setIsmainterm(true);
		return drugRepository.save(drug);
	}

	private void populateDrugCode(final Drug drug, List<DrugCode> drugCodes, Object code) {
		DrugCode drugCode = new DrugCode();
		drugCode.setDrug_id(drug.getId());
		drugCode.setCode(replaceDot(code.toString()));
		drugCodes.add(drugCode);
	}

	private void parseDrugLevelTerm(List<Term> termType, List<Integer> ids) {
		termType.forEach(a -> {
			final Drug drug = populateDrugLevelTerm(a);

			//store drug hierarchy
			if(a.getLevel() == ids.size()) {
				ids.add(drug.getId());
			} else {
				for (int i=ids.size()-1; i>=a.getLevel(); i--) {
					ids.remove(i);
				}
				ids.add(drug.getId());
			}
			int level = 0;
			for (int i=ids.size()-1; i>=0; i--) {
				saveDrugHierarchy(ids.get(i),drug.getId(),level);
				level++;
			}

			//save drug codes
			List<DrugCode> drugCodes = new ArrayList<>();
			a.getCell().forEach(c -> {
				c.getContent().stream().forEach( j -> {
					populateDrugCode(drug, drugCodes, j);
				});
			});
			drugCodeRepository.saveAll(drugCodes);

			if(!a.getTerm().isEmpty()) {
				parseDrugLevelTerm(a.getTerm(), ids);
			}
		});
	}

	private Drug populateDrugLevelTerm(Term a) {
		Drug drug = new Drug();
		drug.setTitle(a.getTitle().getContent().get(0).toString());
		if(a.getTitle().getContent().size()>1) {
			drug.setNemod(getNemodVal(a.getTitle().getContent().get(1)));
		}
		drug.setSee(a.getSee());
		drug.setSeealso(a.getSeeAlso());
		drug.setIsmainterm(false);
		return drugRepository.save(drug);
	}

    private String replaceDot(String input) {
        if(input != null) {
            input = input.replace(".", "");
        }
        return input;
    }

    private String getNemodVal(Object obj) {
    	if (obj instanceof JAXBElement) {
    		JAXBElement element = (JAXBElement) obj;
			return element.getValue().toString();
		}
		return new String();
	}

	private void saveDrugHierarchy(Integer parentId,Integer childId, Integer level) {
		DrugHierarchy drugHierarchy = new DrugHierarchy();
		drugHierarchy.setParentId(parentId);
		drugHierarchy.setChildId(childId);
		drugHierarchy.setLevel(level);
		drugHierarchyRepository.save(drugHierarchy);
	}

	private void saveNeoplasmHierarchy(Integer parentId,Integer childId, Integer level) {
		NeoplasmHierarchy neoplasmHierarchy = new NeoplasmHierarchy();
		neoplasmHierarchy.setParentId(parentId);
		neoplasmHierarchy.setChildId(childId);
		neoplasmHierarchy.setLevel(level);
		neoplasmHierarchyRepository.save(neoplasmHierarchy);
	}
}


