package com.mediscreen.assessment.services;

import java.time.LocalDate;
import java.time.Period;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mediscreen.assessment.dto.AssessmentDTO;
import com.mediscreen.assessment.dto.NoteDTO;
import com.mediscreen.assessment.dto.PatientDTO;
import com.mediscreen.assessment.proxies.DemographicProxy;
import com.mediscreen.assessment.proxies.HistoryNotesProxy;

@Service
public class AssessmentService {

	@Autowired
	HistoryNotesProxy historyNotesProxy;
	
	@Autowired
	DemographicProxy demogrphicProxy;

	private Logger logger = LoggerFactory.getLogger(AssessmentService.class);

	public static final List<String> keyWords = Arrays.asList("Hémoglobine A1C", "Microalbumine", "Taille", "Poids", "Fumeur",
			"Anormal", "Cholestérol", "Vertige", "Rechute", "Réaction", "Anticorps");

	
	public AssessmentDTO getPatientByFamilly(String familly) {
		PatientDTO patientDTO = new PatientDTO();
		
		try {
			patientDTO = demogrphicProxy.getPatientByName(familly);
			return generateAssessment(patientDTO);
		} catch (Exception e) {
			return null;
		}
	}

	public AssessmentDTO generateAssessment(PatientDTO patientDTO) {
		
		AssessmentDTO assessmentDTO = new AssessmentDTO();
		
		String risque = "None";
		
		if (patientDTO != null) {
			
			try { 
				
				assessmentDTO.setPatientInfo(generatePatientInfo(patientDTO));
				
				List<NoteDTO> patientNotes = historyNotesProxy.getNotesByPatientId(patientDTO.getId());
				
				if (patientNotes != null) {
					int keyWordOccurency = calculateKeyWordPresence(patientNotes);
					logger.info("keyWordOccurency = " + keyWordOccurency);
					// Aucun risque de diabete
					if (keyWordOccurency == 0) {
						assessmentDTO.setResult(risque);
						return assessmentDTO;
					} else {
						// si le patient a plus de 30 ans
						if (Period.between(LocalDate.parse(patientDTO.getDob()), LocalDate.now()).getYears() > 30) {
							logger.info("PLUS de 30 ans");
							risque = assessmentMoreThan30(keyWordOccurency);
						// moins de 30 ans
						} else {
							logger.info("MOINS de 30 ans");
							risque = assessmentLessThan30(keyWordOccurency, patientDTO.getSex());
						}
					}
				}
				
				assessmentDTO.setResult(risque);
				return assessmentDTO;
				
			} catch(Exception e) {
				return null;
			}
		} else {
			return null;
		}
	}
	
	// Calcul le risque pour un patient de moins de 30 ans
	public String assessmentMoreThan30(int keyWordOccurency) {
		
		String risque = "None";
		
		if (keyWordOccurency >= 2 && keyWordOccurency <= 5) {
			risque = "Borderline";
		} else if (keyWordOccurency == 6 || keyWordOccurency == 7) {
			risque = "In Danger";
		} else if (keyWordOccurency >= 8) {
			risque = "Early onset";
		}
		
		logger.info(risque + " - Plus de 30 ans");
		
		return risque;
	}
	
	// Calcul le risque pour un patient de moins de 30 ans
	public String  assessmentLessThan30(int keyWordOccurency, char sex) {
		
		String risque = "None";
		
		if (sex == 'M') {
			if (keyWordOccurency == 3 || keyWordOccurency == 4) {
				risque = "In Danger";
			} else if (keyWordOccurency >= 5) {
				risque = "Early onset";
			}
		}
		
		if (sex == 'F') {
			if (keyWordOccurency >= 4 && keyWordOccurency <= 6) {
				risque = "In Danger";
			} else if (keyWordOccurency >= 7) {
				risque = "Early onset";
			}
		}
		
		logger.info(risque + " - Moins de 30 ans");
		
		return risque;
	}
	
	public int calculateKeyWordPresence(List<NoteDTO> patientNotes) {
		
		int keyWordOccurency = 0;
		
		for (NoteDTO noteDTO : patientNotes) {
			for (String word : keyWords) {
				if (noteDTO.getNote().toUpperCase().contains(word.toUpperCase())) {
					keyWordOccurency ++;
				}
			}
		}
		return keyWordOccurency;
	}
	
	public String generatePatientInfo(PatientDTO patientDTO) {
		
		try {
			int age = Period.between(LocalDate.parse(patientDTO.getDob()), LocalDate.now()).getYears();
			return patientDTO.getFamilly() + " " + patientDTO.getGiven() +
					" (age " + age + ")";
		} catch (Exception e) {
			return patientDTO.getFamilly() + " " + patientDTO.getGiven();
		}
	}

}
