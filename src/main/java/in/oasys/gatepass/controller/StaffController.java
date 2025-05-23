package in.oasys.gatepass.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import in.oasys.gatepass.dto.ApprovalDTO;
import in.oasys.gatepass.dto.GatePassRequestDTO;

import in.oasys.gatepass.entity.User;
import in.oasys.gatepass.service.ApprovalService;
import jakarta.mail.MessagingException;

@CrossOrigin("*")
@RestController
@RequestMapping("/api")
public class StaffController {

	@Autowired
	private ApprovalService approvalService;

	// Approve a single student's request
	@PostMapping("/staff/approve/{staffId}/{remarks}")
	public ResponseEntity<ApprovalDTO> approveRequest(@PathVariable String staffId,
			@PathVariable(required = false) String remarks, @RequestBody User student) {

		User staffUser = new User();
		staffUser.setUserId(staffId);

		ApprovalDTO result = approvalService.approveRequest(student, staffUser, remarks);
		return ResponseEntity.ok(result);
	}

	// Reject a request
	@PostMapping("/staff/reject/{reason}")
	public ResponseEntity<String> rejectRequest(@PathVariable String reason, @RequestBody User student) {

		String result = approvalService.rejectRequest(student, reason);
		return ResponseEntity.ok(result);
	}

	// Bulk Approve requests for a given event
	@PostMapping("/staff/bulk-approve/{eventId}/{staffId}/{remarks}")
	public ResponseEntity<String> bulkApproveRequests(@PathVariable String eventId, @PathVariable String staffId,
			@PathVariable(required = false) String remarks) {

		User staffUser = new User();
		staffUser.setUserId(staffId);

		String result = approvalService.bulkApproveRequests(eventId, staffUser, remarks);
		return ResponseEntity.ok(result);
	}

	// View approval history (using DTOs)
	@GetMapping("/staff/history")
	public ResponseEntity<List<ApprovalDTO>> getApprovalHistory() {
		return ResponseEntity.ok(approvalService.getApprovalHistory());
	}

	// View pending gate pass requests (raw entity, or convert to DTO if needed)
	@GetMapping("/staff/pending")
	public ResponseEntity<List<GatePassRequestDTO>> getPendingGatePassRequests() {
		List<GatePassRequestDTO> pendingRequests = approvalService.findPendingByStatus();
		return ResponseEntity.ok(pendingRequests);
	}

	// Approve Emergency Request by Security
	@PostMapping("/security/approve-emergency/{securityId}/{remarks}")
	public ResponseEntity<ApprovalDTO> approveEmergencyRequest(@PathVariable String securityId,
			@PathVariable(required = false) String remarks, @RequestBody User student) throws MessagingException {

		User securityUser = new User();
		securityUser.setUserId(securityId);

		ApprovalDTO result = approvalService.approveEmergencyRequest(student, securityUser, remarks);
		return ResponseEntity.ok(result);
	}
}
