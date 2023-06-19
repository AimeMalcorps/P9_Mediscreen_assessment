package com.mediscreen.assessment.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.mediscreen.assessment.dto.AssessmentDTO;
import com.mediscreen.assessment.dto.PatientDTO;
import com.mediscreen.assessment.services.AssessmentService;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
public class AssessmentController {
	
	@Autowired
	AssessmentService assessmentService;
	
	@PostMapping("/assessment/patient/")
	public AssessmentDTO getAllNotes(@RequestBody PatientDTO patientDTO) {
		return assessmentService.generateAssessment(patientDTO);
	}
	
	@GetMapping("assessment/patient/{familly}")
	public AssessmentDTO getAllNotesByFamilly(@PathVariable String familly) {
		return assessmentService.getPatientByFamilly(familly);
	}

}
