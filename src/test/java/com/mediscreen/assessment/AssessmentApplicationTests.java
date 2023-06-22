package com.mediscreen.assessment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.mediscreen.assessment.controllers.AssessmentController;
import com.mediscreen.assessment.dto.AssessmentDTO;
import com.mediscreen.assessment.dto.NoteDTO;
import com.mediscreen.assessment.dto.PatientDTO;
import com.mediscreen.assessment.services.AssessmentService;

@SpringBootTest
@AutoConfigureMockMvc
class AssessmentApplicationTests {

	@Autowired
	private AssessmentService assessmentService;

	@Autowired
	private AssessmentController assessmentController;

	@Autowired
	public MockMvc mockMvc;

	public static final List<String> keyWords = Arrays.asList("Hémoglobine A1C", "Microalbumine", "Taille", "Poids",
			"Fumeur", "Anormal", "Cholestérol", "Vertige", "Rechute", "Réaction", "Anticorps");

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
		assertEquals("In Danger", assessmentService.assessmentMoreThan30(7));
		assertEquals("Early onset", assessmentService.assessmentMoreThan30(8));

	}

	@Test
	public void assessmentLessThan30() {

		assertEquals("None", assessmentService.assessmentLessThan30(0, 'M'));
		assertEquals("In Danger", assessmentService.assessmentLessThan30(3, 'M'));
		assertEquals("In Danger", assessmentService.assessmentLessThan30(4, 'M'));
		assertEquals("Early onset", assessmentService.assessmentLessThan30(5, 'M'));
		assertEquals("None", assessmentService.assessmentLessThan30(0, 'F'));
		assertEquals("In Danger", assessmentService.assessmentLessThan30(4, 'F'));
		assertEquals("In Danger", assessmentService.assessmentLessThan30(5, 'F'));
		assertEquals("In Danger", assessmentService.assessmentLessThan30(6, 'F'));
		assertEquals("Early onset", assessmentService.assessmentLessThan30(7, 'F'));
		assertEquals("Early onset", assessmentService.assessmentLessThan30(8, 'F'));

	}

	@Test
	public void generatePatientInfo() {

		PatientDTO patientDTO = new PatientDTO();
		patientDTO.setFamilly("MAYER");
		patientDTO.setGiven("John");
		patientDTO.setDob("1996-01-01");

		assertEquals("MAYER John (age 27)", assessmentService.generatePatientInfo(patientDTO));

		patientDTO.setDob("02/01/1996");

		assertEquals("MAYER John", assessmentService.generatePatientInfo(patientDTO));

	}

	@Test
	public void generateAssessment() {

		assertEquals(null, assessmentService.generateAssessment(null));

		PatientDTO patientDTO = new PatientDTO();
		patientDTO.setFamilly("MAYER");
		patientDTO.setGiven("John");
		patientDTO.setDob("1996-01-01");

		assertEquals(null, assessmentService.generateAssessment(patientDTO));
	}

	@Test
	public void getPatientByFamilly() {
		assertEquals(null, assessmentService.getPatientByFamilly(null));
	}

	@Test
	public void testControllers() throws Exception {

		PatientDTO patientDTO = new PatientDTO();
		patientDTO.setFamilly("LAURENT");
		patientDTO.setGiven("Barre");

		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
		ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
		String requestJson = ow.writeValueAsString(patientDTO);

		this.mockMvc.perform(post("/assessment/patient/").contentType(MediaType.APPLICATION_JSON).content(requestJson))
				.andExpect(status().isOk()).andReturn();
		assertEquals(null, assessmentController.getAllNotes(patientDTO));

		this.mockMvc.perform(get("/assessment/patient/" + patientDTO.getFamilly()).contentType(MediaType.APPLICATION_JSON).content(requestJson))
				.andExpect(status().isOk()).andReturn();
		assertEquals(null, assessmentController.getAllNotesByFamilly(patientDTO.getFamilly()));

	}

	@Test
	public void testDTOs() {
		NoteDTO note = new NoteDTO();
		note.setId(1);
		note.setNote("TEST");
		note.setPatientId(2);
		assertEquals(1, note.getId());
		assertEquals(2, note.getPatientId());
		assertEquals("TEST", note.getNote());

		AssessmentDTO assessment = new AssessmentDTO();
		assessment.setPatientInfo("Info");
		assessment.setResult("Result");
		assertEquals("Info", assessment.getPatientInfo());
		assertEquals("Result", assessment.getResult());

		PatientDTO patientDTO = new PatientDTO();
		patientDTO.setId(1);
		patientDTO.setFamilly("MAYER");
		patientDTO.setGiven("John");
		patientDTO.setDob("1996-01-01");
		patientDTO.setSex('M');
		patientDTO.setAddress("21 rue Moure");
		patientDTO.setPhone("06060606");
		assertEquals(1, patientDTO.getId());
		assertEquals("MAYER", patientDTO.getFamilly());
		assertEquals("John", patientDTO.getGiven());
		assertEquals("1996-01-01", patientDTO.getDob());
		assertEquals('M', patientDTO.getSex());
		assertEquals("21 rue Moure", patientDTO.getAddress());
		assertEquals("06060606", patientDTO.getPhone());
	}

}
