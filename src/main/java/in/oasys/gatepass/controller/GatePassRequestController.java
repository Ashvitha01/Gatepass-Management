package in.oasys.gatepass.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import in.oasys.gatepass.dto.GatePassRequestDTO;
import in.oasys.gatepass.service.GatePassRequestService;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/gatepass")
public class GatePassRequestController {

	@Autowired
	private final GatePassRequestService gatePassRequestService;

	public GatePassRequestController(GatePassRequestService gatePassRequestService) {
		this.gatePassRequestService = gatePassRequestService;
	}

	// Submit Gate Pass Request for entry
	@PostMapping(value = "/submitforentry", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<GatePassRequestDTO> submitRequestforEntry(@RequestPart("request") GatePassRequestDTO request,
			@RequestPart("file") MultipartFile file) {
		GatePassRequestDTO savedRequest = gatePassRequestService.submitRequestforEntry(request, file);
		return ResponseEntity.ok(savedRequest);
	}

	// Submit gatepass request for exit

	@PostMapping(value = "/submitforexit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public GatePassRequestDTO submitRequestforExit(@RequestPart("exitrequest") GatePassRequestDTO exitrequest,
			@RequestPart("file") MultipartFile file) {

		return gatePassRequestService.submitRequestforExit(exitrequest, file);
	}
	// Update Gate Pass Request

	@PutMapping(value = "/updateforentry", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<GatePassRequestDTO> updateRequestforentry(@RequestPart("request") GatePassRequestDTO request,
			@RequestPart("file") MultipartFile file) {
		GatePassRequestDTO updatedRequest = gatePassRequestService.updateRequestforentry(request, file);
		return ResponseEntity.ok(updatedRequest);
	}

	@PutMapping(value = "/updateforexit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<GatePassRequestDTO> updateRequestforexit(@RequestPart("request") GatePassRequestDTO request,
			@RequestPart("file") MultipartFile file) {
		GatePassRequestDTO updatedRequest = gatePassRequestService.updateRequestforexit(request, file);
		return ResponseEntity.ok(updatedRequest);
	}

	// Get Gate Pass Requests for a Specific Student
	@GetMapping("/status/{studentId}")

	public ResponseEntity<List<GatePassRequestDTO>> getAllRequestsByStudent(@PathVariable String studentId) {
		try {
			List<GatePassRequestDTO> requests = gatePassRequestService.getAllRequestsByStudent(studentId);
			return ResponseEntity.ok(requests);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}
	}

	// Cancel Gate Pass Request
	@DeleteMapping("/cancelforentry/{studentId}")
	public ResponseEntity<String> cancelRequestforentry(@PathVariable("studentId") String studentId) {
		String response = gatePassRequestService.cancelRequestforentry(studentId);
		return ResponseEntity.ok(response);
	}

	// Cancel Gate Pass Request
	@DeleteMapping("/cancelforexit/{studentId}")
	public ResponseEntity<String> cancelRequestforexit(@PathVariable("studentId") String studentId) {
		String response = gatePassRequestService.cancelRequestforexit(studentId);
		return ResponseEntity.ok(response);
	}

	// Emergency Gate Pass Request
	@PostMapping("/emergencyrequest")
	public ResponseEntity<GatePassRequestDTO> createEmergencyGatePassRequest(@RequestBody GatePassRequestDTO request) {
		GatePassRequestDTO emergencyRequest = gatePassRequestService.emergencyRequest(request);
		return ResponseEntity.ok(emergencyRequest);
	}
}
