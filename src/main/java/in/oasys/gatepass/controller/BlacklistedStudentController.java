package in.oasys.gatepass.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import in.oasys.gatepass.dto.BlacklistedStudentDTO;
import in.oasys.gatepass.entity.BlacklistedStudents;
import in.oasys.gatepass.service.BlacklistedStudentService;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/blackliststudent")
public class BlacklistedStudentController {

	@Autowired
	private BlacklistedStudentService blacklistedStudentService;

	// Add student to blacklist
	@PostMapping("/add")
	public ResponseEntity<BlacklistedStudentDTO> blacklistStudent(@RequestParam String userId,
			@RequestParam String reason) {
		BlacklistedStudentDTO responseDTO = blacklistedStudentService.blacklistStudent(userId, reason);
		return ResponseEntity.ok(responseDTO);
	}

	// Check if a student is blacklisted
	@GetMapping("/checkblacklistedornot/{userId}")
	public ResponseEntity<Boolean> isStudentBlacklisted(@PathVariable("userId") String userId) {
		boolean isBlacklisted = blacklistedStudentService.isStudentBlacklisted(userId);
		return ResponseEntity.ok(isBlacklisted);
	}

	// get all blacklisted students
	@GetMapping("/getallblacklist")
	public List<BlacklistedStudentDTO> getallblacklist() {

		return blacklistedStudentService.getallblacklist();
	}

	// Remove student from blacklist
	@DeleteMapping("/remove/{userId}")
	public ResponseEntity<String> removeBlacklist(@PathVariable String userId) {
		String responseMessage = blacklistedStudentService.removeFromBlacklist(userId);
		return ResponseEntity.ok(responseMessage);
	}
}
