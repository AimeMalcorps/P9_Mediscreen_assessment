package com.mediscreen.assessment.controllers;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.mediscreen.assessment.dto.PatientDTO;
import com.mediscreen.assessment.services.AssessmentService;

@RestController
public class AssessmentController {
	
	@Autowired
	AssessmentService assessmentService;
	
	@PostMapping("/assassment/patient/")
	public String getAllNotes(@RequestBody PatientDTO patientDTO) {
		return new JSONObject().put("assessment", assessmentService.generateAssessment(patientDTO)).toString();
	}

}
