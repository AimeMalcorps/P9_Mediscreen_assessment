package com.mediscreen.assessment.services;

import java.time.LocalDate;
import java.time.Period;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.mediscreen.assessment.dto.NoteDTO;
import com.mediscreen.assessment.dto.PatientDTO;
import com.mediscreen.assessment.proxies.HistoryNotesProxy;

@Service
public class AssessmentService {

	@Autowired
	HistoryNotesProxy historyNotesProxy;

	private Logger logger = LoggerFactory.getLogger(AssessmentService.class);

	public static final List<String> keyWords = Arrays.asList("Hémoglobine A1C", "Microalbumine", "Taille", "Poids", "Fumeur",
			"Anormal", "Cholestérol", "Vertige", "Rechute", "Réaction", "Anticorps");

	public String generateAssessment(PatientDTO patientDTO) {
		
		String risque = "None";
		try {
			List<NoteDTO> patientNotes = historyNotesProxy.getNotesByPatientId(patientDTO.getId());
			
			if (patientNotes != null) {
				int keyWordOccurency = calculateKeyWordPresence(patientNotes);
				logger.info("keyWordOccurency = " + keyWordOccurency);
				// Aucun risque de diabete
				if (keyWordOccurency == 0) {
					return risque;
					
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
			return risque;
		} catch(Exception e) {
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

}
