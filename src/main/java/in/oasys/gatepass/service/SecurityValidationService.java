package in.oasys.gatepass.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import in.oasys.gatepass.dto.SecurityValidationDTO;
import in.oasys.gatepass.entity.GatePassRequestEntity;
import in.oasys.gatepass.entity.SecurityValidation;
import in.oasys.gatepass.entity.User;
import in.oasys.gatepass.repository.GatePassRequestRepository;
import in.oasys.gatepass.repository.SecurityValidationRepository;
import in.oasys.gatepass.repository.UserRepository;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class SecurityValidationService {

	@Autowired
	private SecurityValidationRepository securityValidationRepository;

	@Autowired
	private GatePassRequestRepository gatePassRequestRepository;

	@Autowired
	private UserRepository userRepository;

	// Validate Entry
	public SecurityValidationDTO validateEntry(String studentId, String securityGuardId) {
		User student = userRepository.findById(studentId).orElseThrow(() -> new RuntimeException("Student not found"));

		GatePassRequestEntity gatePass = gatePassRequestRepository.findByStudentidOrderByRequestId(student.getUserId());
//				.orElseThrow(() -> new RuntimeException("Gate Pass Request not found"));
		log.info("gatepass======>{}",gatePass);
		if (gatePass.getRequestId()==null) {
			throw new IllegalStateException(
					"Gate Pass Request not found.");
		}
		LocalDateTime now = LocalDateTime.now();

		if (now.isAfter(gatePass.getEntrytime())) {
			throw new RuntimeException("Entry denied! Gate pass is expired or invalid.");
		}

		if (gatePass.getStatus() != GatePassRequestEntity.Status.APPROVED) {
			throw new RuntimeException("Gate pass request is not approved!");
		}
		if (gatePass.getType() != GatePassRequestEntity.Gatepasstype.ENTRY) {
			throw new RuntimeException("This is not valid for entry");
		}

		gatePass.setStatus(GatePassRequestEntity.Status.VALIDATED);

		User guard = userRepository.findById(securityGuardId)
				.orElseThrow(() -> new RuntimeException("Security guard not found"));

		SecurityValidation validation = new SecurityValidation();
		validation.setRequestId(gatePass);
		validation.setEntryTime(now);
		validation.setCreatedon(now);
		validation.setStatus(SecurityValidation.Status.VALIDATED);
		validation.setSecurityGuard(guard);

		SecurityValidation saved = securityValidationRepository.save(validation);

		return mapToDTO(saved);
	}

	// Validate Exit
	public SecurityValidationDTO validateExit(String studentId, String securityGuardId) {
		User studentid = userRepository.findById(studentId)
				.orElseThrow(() -> new RuntimeException("Student not found"));

		GatePassRequestEntity gatePass = gatePassRequestRepository.findByStudentidOrderByRequestId(studentid.getUserId());
	//			.orElseThrow(() -> new RuntimeException("Gate Pass Request not found"));
	  log.info("gatepass======>{}",gatePass);
		if (gatePass.getRequestId()==null) {
			throw new IllegalStateException(
					"Gate Pass Request not found.");
		}
		LocalDateTime now = LocalDateTime.now();
		

		if (now.isAfter(gatePass.getExittime())) {
			throw new RuntimeException("Exit denied! Gate pass is expired or invalid.");
		}

		if (gatePass.getStatus() != GatePassRequestEntity.Status.APPROVED) {
			throw new RuntimeException("Gate pass not validated for Exit.");
		}

		if (gatePass.getType() != GatePassRequestEntity.Gatepasstype.EXIT) {
			throw new RuntimeException("This is not valid for entry");
		}

		gatePass.setStatus(GatePassRequestEntity.Status.COMPLETED);

		User guard = userRepository.findById(securityGuardId)
				.orElseThrow(() -> new RuntimeException("Security guard not found"));

		SecurityValidation validation = new SecurityValidation();
		validation.setRequestId(gatePass);
		validation.setExitTime(now);
		validation.setCreatedon(now);
		validation.setStatus(SecurityValidation.Status.COMPLETED);
		validation.setSecurityGuard(guard);

		SecurityValidation saved = securityValidationRepository.save(validation);

		return mapToDTO(saved);
	}

	// Convert Entity to DTO
	private SecurityValidationDTO mapToDTO(SecurityValidation validation) {
		SecurityValidationDTO dto = new SecurityValidationDTO();
		dto.setValidationId(validation.getValidationid());
		dto.setRequestId(validation.getRequestId().getRequestId());
		dto.setSecurityGuardId(validation.getSecurityGuard().getUserId());
		dto.setEntryTime(validation.getEntryTime());
		dto.setExitTime(validation.getExitTime());
		dto.setCreatedOn(validation.getCreatedon());
		dto.setStatus(validation.getStatus().toString());
		return dto;
	}
}
