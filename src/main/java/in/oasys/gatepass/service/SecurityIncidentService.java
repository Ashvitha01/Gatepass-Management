package in.oasys.gatepass.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import in.oasys.gatepass.dto.SecurityIncidentDTO;
import in.oasys.gatepass.entity.GatePassRequestEntity;
import in.oasys.gatepass.entity.SecurityIncident;
import in.oasys.gatepass.entity.User;
import in.oasys.gatepass.repository.GatePassRequestRepository;
import in.oasys.gatepass.repository.SecurityIncidentRepository;
import in.oasys.gatepass.repository.UserRepository;

@Service
public class SecurityIncidentService {

	@Autowired
	private SecurityIncidentRepository securityIncidentRepository;

	@Autowired
	private GatePassRequestRepository gatePassRequestRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private NotificationService notificationService;

	// Report incident
	public SecurityIncidentDTO reportIncident(String studentid, String description, String securityGuardId) {
		// Check if the student exists in the database
		User student = userRepository.findById(studentid).orElseThrow(() -> new RuntimeException("Student not found"));

		// Check if there is already a pending incident for this student
		Optional<SecurityIncident> existingIncident = securityIncidentRepository.findByStudentidAndStatus(student,
				SecurityIncident.Status.PENDING);

		// If a pending incident exists, return an error message
		if (existingIncident.isPresent()) {
			throw new RuntimeException(
					"Student ID " + studentid + " is already in the security incident list with a PENDING status.");
		}

		// Check if the student has an approved gate pass
		boolean hasApprovedPass = gatePassRequestRepository.existsByStudentidAndStatus(student,
				GatePassRequestEntity.Status.APPROVED);

		if (hasApprovedPass) {
			throw new RuntimeException("Cannot report incident: Student has an approved gate pass.");
		}

		// Retrieve the security guard from the database
		User securityGuard = userRepository.findById(securityGuardId)
				.orElseThrow(() -> new RuntimeException("Security guard not found"));

		// Create and set the new incident
		SecurityIncident incident = new SecurityIncident();
		incident.setStudentid(student);
		incident.setDescription(description);
		incident.setReportedat(LocalDateTime.now());
		incident.setSecurityGuard(securityGuard);
		incident.setStatus(SecurityIncident.Status.PENDING);

		// Save the new incident
		SecurityIncident saved = securityIncidentRepository.save(incident);

		// Notify admin/staff
		String message = "Suspicious activity reported for Student ID: " + studentid + "\n" + "Description: "
				+ description + "\n" + "Reported by: " + securityGuard.getName();
		notificationService.notifyMultiple(message);

		// Return the saved incident as a DTO
		return mapToDTO(saved);
	}

	// Get all incidents
	public List<SecurityIncidentDTO> getAllIncidents() {
		return securityIncidentRepository.findAll().stream().map(this::mapToDTO).collect(Collectors.toList());
	}

	// Get incidents by student
	public List<SecurityIncidentDTO> getIncidentsByStudent(String studentId) {
		User student = userRepository.findById(studentId).orElseThrow(() -> new RuntimeException("Student not found"));
		return securityIncidentRepository.findByStudentid(student).stream().map(this::mapToDTO)
				.collect(Collectors.toList());
	}

	// Resolve incident
	public SecurityIncidentDTO resolveIncident(String studentId, String updatedDescription, String resolverId) {
		User student = userRepository.findById(studentId).orElseThrow(() -> new RuntimeException("Student not found"));

		User resolver = userRepository.findById(resolverId)
				.orElseThrow(() -> new RuntimeException("Resolver not found"));

		if (!(resolver.getRole().equals(User.Role.ADMIN) || resolver.getRole().equals(User.Role.STAFF))) {
			throw new RuntimeException("Unauthorized: Only Admin or Staff can resolve incidents");
		}

		SecurityIncident incident = securityIncidentRepository.findAllByStudentid(student)
				.orElseThrow(() -> new RuntimeException("Incident not found"));

		incident.setStatus(SecurityIncident.Status.RESOLVED);
		incident.setDescription(updatedDescription);
		incident.setReportedat(LocalDateTime.now());

		SecurityIncident updated = securityIncidentRepository.save(incident);

		String message = "Student ID " + studentId + " incident resolved by " + resolver.getName() + ".\n"
				+ "Updated Description: " + updatedDescription;
		notificationService.notifyMultiple(message);

		return mapToDTO(updated);
	}

	// Map entity to DTO
	private SecurityIncidentDTO mapToDTO(SecurityIncident incident) {
		SecurityIncidentDTO dto = new SecurityIncidentDTO();
		dto.setIncidentId(incident.getIncidentId());
		dto.setStudentId(incident.getStudentid().getUserId());
		dto.setSecurityGuardId(incident.getSecurityGuard().getUserId());
		dto.setDescription(incident.getDescription());
		dto.setReportedAt(incident.getReportedat());
		dto.setStatus(incident.getStatus().toString());
		return dto;
	}
}
