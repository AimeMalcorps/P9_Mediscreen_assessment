package com.mediscreen.assessment.proxies;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.mediscreen.assessment.dto.NoteDTO;

@FeignClient(name = "history-notes", url = "host.docker.internal:8082")
public interface HistoryNotesProxy {
	
	@GetMapping(value = "/patHistory/patient/{id}")
    public List<NoteDTO> getNotesByPatientId(@PathVariable Integer id);

}
