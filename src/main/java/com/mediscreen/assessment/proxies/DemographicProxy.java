package com.mediscreen.assessment.proxies;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.mediscreen.assessment.dto.PatientDTO;

@FeignClient(name = "demographic", url = "host.docker.internal:8080")
public interface DemographicProxy {
	
	@GetMapping(value = "/patient/search")
    public PatientDTO getPatientByName(@RequestBody String familly);

}
