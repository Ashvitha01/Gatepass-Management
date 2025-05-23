package in.oasys.gatepass.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import in.oasys.gatepass.dto.SecurityValidationDTO;
import in.oasys.gatepass.service.SecurityValidationService;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/securityvalidate")
public class SecurityValidationController {

	@Autowired
	private SecurityValidationService securityValidationService;

	@PostMapping("/validate-entry")
	public SecurityValidationDTO validateEntry(@RequestParam String studentId, @RequestParam String securityGuardId) {
		return securityValidationService.validateEntry(studentId, securityGuardId);
	}

	@PostMapping("/validate-exit")
	public SecurityValidationDTO validateExit(@RequestParam String studentId, @RequestParam String securityGuardId) {
		return securityValidationService.validateExit(studentId, securityGuardId);
	}
}
