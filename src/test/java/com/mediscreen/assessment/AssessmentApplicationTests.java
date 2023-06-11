package com.mediscreen.assessment;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.mediscreen.assessment.dto.NoteDTO;
import com.mediscreen.assessment.services.AssessmentService;

@SpringBootTest
class AssessmentApplicationTests {
	
	@Autowired
	private AssessmentService assessmentService;
	
	public static final List<String> keyWords = Arrays.asList("Hémoglobine A1C", "Microalbumine", "Taille", "Poids", "Fumeur",
			"Anormal", "Cholestérol", "Vertige", "Rechute", "Réaction", "Anticorps");

	@Test
	void contextLoads() {
	}
	
	@Test
	public void calculateKeyWordPresence() {
		List<NoteDTO> listNoteDTO = new ArrayList<NoteDTO>();
		
		NoteDTO noteDTO = new NoteDTO();
		noteDTO.setNote("Rechute du patient");
		listNoteDTO.add(noteDTO);
		
		assertEquals(1, assessmentService.calculateKeyWordPresence(listNoteDTO));
		
		noteDTO = new NoteDTO();
		noteDTO.setNote("Aucun mot cle");
		listNoteDTO.add(noteDTO);
		
		assertEquals(1, assessmentService.calculateKeyWordPresence(listNoteDTO));
		
		noteDTO = new NoteDTO();
		noteDTO.setNote("Anticorps mot cle");
		listNoteDTO.add(noteDTO);
		
		assertEquals(2, assessmentService.calculateKeyWordPresence(listNoteDTO));
	}
	
	@Test
	public void assessmentMoreThan30() {
		
		assertEquals("None", assessmentService.assessmentMoreThan30(0));
		assertEquals("Borderline", assessmentService.assessmentMoreThan30(2));
		assertEquals("In Danger", assessmentService.assessmentMoreThan30(6));
		assertEquals("Early onset", assessmentService.assessmentMoreThan30(8));
		
	}
	
	@Test
	public void assessmentLessThan30() {
		
		assertEquals("None", assessmentService.assessmentLessThan30(0, 'M'));
		assertEquals("In Danger", assessmentService.assessmentLessThan30(3, 'M'));
		assertEquals("Early onset", assessmentService.assessmentLessThan30(5, 'M'));
		assertEquals("In Danger", assessmentService.assessmentLessThan30(4, 'F'));
		assertEquals("Early onset", assessmentService.assessmentLessThan30(7, 'F'));
		
	}

}
