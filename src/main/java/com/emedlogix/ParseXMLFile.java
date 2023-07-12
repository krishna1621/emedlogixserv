/**
 * 
 */
package com.emedlogix;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import org.springframework.util.ResourceUtils;

import generated.ICD10CMIndex;
import generated.Term;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;

/**
 * @author viswa
 *
 */
public class ParseXMLFile {
	
	public ParseXMLFile() {
		parseIndex();
		//parseNeoPlasm();
	}

	private void parseIndex() {
		Object obj = parseXML("icd10cm_eindex_2023.xml",ICD10CMIndex.class);
		if(obj instanceof ICD10CMIndex) {
			ICD10CMIndex icd10CMIndex = (ICD10CMIndex)obj;
			icd10CMIndex.getLetter().stream().forEach(l -> {
				l.getMainTerm().stream().forEach(m -> {
					/*m.getTitle().getContent().forEach(r -> {
						if(r instanceof JAXBElement) {
							JAXBElement element = (JAXBElement)r;
							element.getName();							
						} else {
							System.out.println(r.toString());
						}
					});*/
					System.out.println("mainterm title ::"+m.getTitle().getContent().get(0).toString());
					System.out.println("mainterm code ::"+m.getCode());
					System.out.println("mainterm see ::"+m.getSee());
					System.out.println("mainterm seealso ::"+m.getSeeAlso());
					System.out.println("mainterm seecat ::"+m.getSeecat());
				});
			});
		}
	}
	
	private void parseNeoPlasm() {
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
			File file = ResourceUtils.getFile("classpath:"+fileName);		    
	        JAXBContext jaxbContext = JAXBContext.newInstance(className);

			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

			return jaxbUnmarshaller.unmarshal(file);
		} catch (JAXBException | FileNotFoundException e) {
			e.printStackTrace();
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
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new ParseXMLFile();
	}

}
