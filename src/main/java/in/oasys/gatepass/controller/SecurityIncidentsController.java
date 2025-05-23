package in.oasys.gatepass.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import in.oasys.gatepass.dto.SecurityIncidentDTO;
import in.oasys.gatepass.service.SecurityIncidentService;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/securityincident")
public class SecurityIncidentsController {

	@Autowired
	private SecurityIncidentService securityIncidentService;

	// Report an incident
	@PostMapping("/report")
	public SecurityIncidentDTO reportIncident(@RequestParam String studentid, @RequestParam String description,
			@RequestParam String securityGuardId) {
		return securityIncidentService.reportIncident(studentid, description, securityGuardId);
	}

	// Get incidents by student
	@GetMapping("/student/{studentId}")
	public List<SecurityIncidentDTO> getIncidentsByStudent(@PathVariable String studentId) {
		return securityIncidentService.getIncidentsByStudent(studentId);
	}

	// Fetch all incidents
	@GetMapping("/getallincident")
	public List<SecurityIncidentDTO> fetchAllIncidents() {
		return securityIncidentService.getAllIncidents();
	}

	// Resolve an incident
	@PutMapping("/staff/resolve/{studentId}")
	public SecurityIncidentDTO resolveIncident(@PathVariable String studentId, @RequestParam String description,
			@RequestParam String resolverId) {
		return securityIncidentService.resolveIncident(studentId, description, resolverId);
	}
}
