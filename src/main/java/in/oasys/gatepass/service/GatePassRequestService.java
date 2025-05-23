package in.oasys.gatepass.service;

import in.oasys.gatepass.dto.GatePassRequestDTO;
import in.oasys.gatepass.dto.UserDTO;
import in.oasys.gatepass.entity.BlacklistedStudents;
import in.oasys.gatepass.entity.GatePassRequestEntity;
import in.oasys.gatepass.entity.GatePassRequestEntity.Status;
import in.oasys.gatepass.entity.NotificationEntity;
import in.oasys.gatepass.entity.User;
import in.oasys.gatepass.repository.*;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

//import in.oasys.gatepass.entity.GatePassRequestEntity.Gatepasstype;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
@Log4j2
@Service
public class GatePassRequestService {

	@Autowired
	private GatePassRequestRepository gatePassRequestRepository;
	@Autowired
	private JavaMailSender mailSender;
	@Autowired
	private NotificationRepository notificationRepository;
	@Autowired
	private NotificationService notificationService;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private BlacklistedStudentRepository blacklistRepository;
	@Autowired
	private FileStorageService filestorageservice;

	@Value("${staff.email}")
	private String staffEmail;

	// Submit request for entry
	public GatePassRequestDTO submitRequestforEntry(GatePassRequestDTO dto, MultipartFile file) {
		if (dto.getStudentid() == null || dto.getStudentid().getUserId() == null) {
			throw new IllegalArgumentException("Student ID is required.");
		}

		User student = userRepository.findById(dto.getStudentid().getUserId())
				.orElseThrow(() -> new IllegalArgumentException("Student not found"));

		if (blacklistRepository.findByStudentidAndStatus(student, BlacklistedStudents.Status.BLACKLISTED).isPresent()) {
			throw new IllegalStateException("Student is blacklisted.");
		}

		// Check last gate pass must be COMPLETED
		GatePassRequestEntity latestRequest = gatePassRequestRepository
				.findByStudentidOrderByRequestId(student.getUserId());
		log.info("latestRequest ===========>>> ,{}",latestRequest);

		if (latestRequest!=null) {
			GatePassRequestEntity lastRequest = latestRequest;

			if (lastRequest.getType() == GatePassRequestEntity.Gatepasstype.ENTRY
					&& lastRequest.getStatus() != GatePassRequestEntity.Status.COMPLETED) {

				throw new IllegalStateException(
						"You must complete your previous gate pass before applying for a new entry.");
			}
		}

		// Save the file and get the document path
		String documentPath = filestorageservice.storeFile(file); // Save the file and get the document path

		// Proceed with new Entry Gate Pass request
		GatePassRequestEntity entity = new GatePassRequestEntity();
		entity.setStudentid(student);
		entity.setReason(dto.getReason());
		entity.setDate(dto.getDate());
		entity.setEventId(dto.getEventId());
		entity.setEmail(dto.getEmail());
		entity.setEntrytime(dto.getEntrytime());
		// Set the type as ENTRY
		entity.setType(GatePassRequestEntity.Gatepasstype.ENTRY);
		entity.setDocumentPath(documentPath);
		entity.setStatus(GatePassRequestEntity.Status.PENDING);

		GatePassRequestEntity saved = gatePassRequestRepository.save(entity);
		sendEmailNotification(saved, "New Entry Gate Pass Request");

		NotificationEntity notification = new NotificationEntity();
		notification.setMessage("Entry gate pass request submitted by: " + student.getUserId());
		notification.setSent_at(LocalDateTime.now());
		notification.setStatus(NotificationEntity.Status.SENT);
		notification.setUserid(student);
		notificationRepository.save(notification);

		return mapToDTO(saved);
	}

	// Submit request for exit
	public GatePassRequestDTO submitRequestforExit(GatePassRequestDTO dto, MultipartFile file) {
		if (dto.getStudentid() == null || dto.getStudentid().getUserId() == null) {
			throw new IllegalArgumentException("Student ID is required.");
		}

		User student = userRepository.findById(dto.getStudentid().getUserId())
				.orElseThrow(() -> new IllegalArgumentException("Student not found"));

		if (blacklistRepository.findByStudentidAndStatus(student, BlacklistedStudents.Status.BLACKLISTED).isPresent()) {
			throw new IllegalStateException("Student is blacklisted.");
		}

		// Check last gate pass must be VALIDATED
		GatePassRequestEntity latestRequest = gatePassRequestRepository
				.findByStudentidOrderByRequestId(student.getUserId());
		log.info("latestRequest ===========>>> ,{}",latestRequest);
		if (latestRequest==null) {
			throw new IllegalStateException(
					"No previous entry found. You must complete an entry gate pass before submitting an exit gate pass.");
		}

		GatePassRequestEntity lastRequest = latestRequest;
		if (lastRequest.getStatus() != GatePassRequestEntity.Status.VALIDATED) {
			throw new IllegalStateException("Your last gate pass must be validated before applying for an exit.");
		}

		// Save the file and get the document path
		String documentPath = filestorageservice.storeFile(file);

		// Proceed with new Exit Gate Pass request
		GatePassRequestEntity entity = new GatePassRequestEntity();
		entity.setStudentid(student);
		entity.setReason(dto.getReason());
		entity.setDate(dto.getDate());
		entity.setEventId(dto.getEventId());
		entity.setEmail(dto.getEmail());
		// Set the type as ENTRY
		entity.setType(GatePassRequestEntity.Gatepasstype.EXIT);
		entity.setExittime(dto.getExittime());
		entity.setDocumentPath(documentPath);
		entity.setStatus(GatePassRequestEntity.Status.PENDING);

		GatePassRequestEntity saved = gatePassRequestRepository.save(entity);
		sendEmailNotification(saved, "New Exit Gate Pass Request");

		NotificationEntity notification = new NotificationEntity();
		notification.setMessage("Exit gate pass request submitted by: " + student.getUserId());
		notification.setSent_at(LocalDateTime.now());
		notification.setStatus(NotificationEntity.Status.SENT);
		notification.setUserid(student);
		notificationRepository.save(notification);

		return mapToDTO(saved);
	}

	public GatePassRequestDTO updateRequestforentry(GatePassRequestDTO dto, MultipartFile file) {
		// Fetch the student based on userId
		User studentid = userRepository.findById(dto.getStudentid().getUserId())
				.orElseThrow(() -> new IllegalArgumentException("Student not found"));

		System.out.println("Student fetched: " + studentid);

		// Fetch the latest gate pass request by student ID
		return gatePassRequestRepository.findTopByStudentidOrderByCreatedAtDesc(studentid).map(existing -> {
			System.out.println("Found GatePassRequestEntity: " + existing);

			// Check if status is PENDING, only allow updates on PENDING status
			if (existing.getStatus() != GatePassRequestEntity.Status.PENDING) {
				throw new IllegalStateException("Only pending requests can be updated.");
			}

			if (existing.getType() != GatePassRequestEntity.Gatepasstype.ENTRY) {
				throw new IllegalStateException("Only ENTRY  typr requests can be updated ");
			}
			// Save the file and get the document path
			String documentPath = filestorageservice.storeFile(file); // Save the file and get the document path

			// Update the request fields with the new data from the DTO
			existing.setReason(dto.getReason());
			existing.setDocumentPath(dto.getDocumentPath());

			// Save the updated GatePassRequestEntity
			GatePassRequestEntity updated = gatePassRequestRepository.save(existing);
			System.out.println("Updated GatePassRequestEntity: " + updated);

			// Send email notification about the update
			sendEmailNotification(updated, "Gate Pass Request Updated");

			// Convert the updated entity to a DTO and return
			return mapToDTO(updated);
		}).orElseThrow(() -> new IllegalArgumentException("No gate pass request found for this student."));
	}

	public GatePassRequestDTO updateRequestforexit(GatePassRequestDTO dto, MultipartFile file) {
		// Fetch the student based on userId
		User studentid = userRepository.findById(dto.getStudentid().getUserId())
				.orElseThrow(() -> new IllegalArgumentException("Student not found"));

		System.out.println("Student fetched: " + studentid);

		// Fetch the latest gate pass request by student ID
		return gatePassRequestRepository.findTopByStudentidOrderByCreatedAtDesc(studentid).map(existing -> {
			System.out.println("Found GatePassRequestEntity: " + existing);

			// Check if status is PENDING, only allow updates on PENDING status
			if (existing.getStatus() != GatePassRequestEntity.Status.PENDING) {
				throw new IllegalStateException("Only pending requests can be updated.");
			}

			if (existing.getType() != GatePassRequestEntity.Gatepasstype.EXIT) {
				throw new IllegalStateException("Only EXIT  typr requests can be updated ");
			}
			// Save the file and get the document path
			String documentPath = filestorageservice.storeFile(file);

			// Update the request fields with the new data from the DTO
			existing.setReason(dto.getReason());
			existing.setExittime(dto.getExittime()); // Allow exit time update
			existing.setDocumentPath(documentPath);

			// Save the updated GatePassRequestEntity
			GatePassRequestEntity updated = gatePassRequestRepository.save(existing);
			System.out.println("Updated GatePassRequestEntity: " + updated);

			// Send email notification about the update
			sendEmailNotification(updated, "Gate Pass Request Updated");

			// Convert the updated entity to a DTO and return
			return mapToDTO(updated);
		}).orElseThrow(() -> new IllegalArgumentException("No gate pass request found for this student."));
	}

//cancel for entry
	public String cancelRequestforentry(String studentId) {
		User student = userRepository.findById(studentId)
				.orElseThrow(() -> new IllegalArgumentException("Student not found"));

		GatePassRequestEntity latestRequest = gatePassRequestRepository.findTopByStudentidOrderByCreatedAtDesc(student)
				.orElseThrow(() -> new IllegalArgumentException("No gate pass request found for this student."));

		if (latestRequest.getStatus() != GatePassRequestEntity.Status.PENDING) {
			throw new IllegalStateException("Only pending requests can be cancelled.");
		}

		if (latestRequest.getType() != GatePassRequestEntity.Gatepasstype.ENTRY) {
			throw new IllegalStateException("Only ENTRY type requests can be cancelled.");
		}

		gatePassRequestRepository.delete(latestRequest);
		return "ENTRY type request cancelled successfully.";
	}

	// cancel for exit
	public String cancelRequestforexit(String studentId) {
		User studentid = userRepository.findById(studentId)
				.orElseThrow(() -> new IllegalArgumentException("Student not found"));

		GatePassRequestEntity latestRequest = gatePassRequestRepository
				.findTopByStudentidOrderByCreatedAtDesc(studentid)
				.orElseThrow(() -> new IllegalArgumentException("No gate pass request found for this student."));

		if (latestRequest.getStatus() != GatePassRequestEntity.Status.PENDING) {
			throw new IllegalStateException("Only pending requests can be cancelled.");
		}

		if (latestRequest.getType() != GatePassRequestEntity.Gatepasstype.EXIT) {
			throw new IllegalStateException("Only EXIT type requests can be cancelled.");
		}

		gatePassRequestRepository.delete(latestRequest);
		return "Request deleted successfully.";
	}

	public List<GatePassRequestDTO> getAllRequestsByStudent(String studentId) {
		// Fetch the student entity (User) by the studentId
		User studentid = userRepository.findById(studentId)
				.orElseThrow(() -> new IllegalArgumentException("Student not found"));

		// Fetch all gate pass requests for this student
		List<GatePassRequestEntity> requests = gatePassRequestRepository.findByStudentid(studentid);

		if (requests.isEmpty()) {
			throw new IllegalArgumentException("No requests found for student");
		}
		// Map all gate pass requests to DTOs and return them
		return requests.stream().map(this::mapToDTO) // Assuming mapToDTO method is implemented for mapping
				.collect(Collectors.toList());
	}

	public GatePassRequestDTO emergencyRequest(GatePassRequestDTO dto) {
		User studentid = userRepository.findById(dto.getStudentid().getUserId())
				.orElseThrow(() -> new IllegalArgumentException("Student not found"));

		// Check latest request's type and status
		Optional<GatePassRequestEntity> latestRequestOpt = gatePassRequestRepository
				.findTopByStudentidOrderByRequestIdDesc(studentid);

		GatePassRequestEntity latestRequest = latestRequestOpt.orElseThrow(
				() -> new IllegalStateException("No previous requests found. Emergency request not allowed."));

		if (latestRequest.getStatus() != GatePassRequestEntity.Status.VALIDATED
				|| latestRequest.getType() != GatePassRequestEntity.Gatepasstype.ENTRY) {
			throw new IllegalStateException(
					"Emergency request allowed only if your last request is a validated entry.");
		}

		GatePassRequestEntity entity = new GatePassRequestEntity();
		entity.setStudentid(studentid);
		entity.setReason(dto.getReason());
		entity.setEmail(dto.getEmail());
		entity.setExittime(dto.getExittime());
		entity.setStatus(GatePassRequestEntity.Status.PENDING);
		entity.setType(GatePassRequestEntity.Gatepasstype.EXIT);

		GatePassRequestEntity saved = gatePassRequestRepository.save(entity);

		notificationService.notifySecurityAndStaff(
				"Emergency request by: " + studentid.getName() + "\nReason: " + dto.getReason());

		NotificationEntity notification = new NotificationEntity();
		notification.setMessage("Emergency gate pass request submitted by " + studentid.getName());
		notification.setSent_at(LocalDateTime.now());
		notification.setStatus(NotificationEntity.Status.SENT);
		notification.setUserid(studentid);
		notificationRepository.save(notification);

		return mapToDTO(saved);
	}

	private void sendEmailNotification(GatePassRequestEntity request, String subject) {
		try {
			MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true);
			helper.setTo(staffEmail);
			helper.setSubject(subject);
			helper.setText("Gate Pass Request Details:\n\n" + "Student ID: " + request.getStudentid().getUserId() + "\n"
					+ "Reason: " + request.getReason() + "\n" + "Entry Time: " + request.getEntrytime() + "\n"
					+ "Exit Time: " + request.getExittime() + "\n" + "Status: " + request.getStatus());
			mailSender.send(message);
		} catch (Exception e) {
			System.out.println("Email notification failed: " + e.getMessage());
		}
	}

	private GatePassRequestDTO mapToDTO(GatePassRequestEntity entity) {
		GatePassRequestDTO dto = new GatePassRequestDTO();
		dto.setRequestId(entity.getRequestId());

		UserDTO userDTO = new UserDTO();
		userDTO.setUserId(entity.getStudentid().getUserId());
		userDTO.setName(entity.getStudentid().getName());
		userDTO.setEmail(entity.getStudentid().getEmail());
		userDTO.setContactNumber(entity.getStudentid().getContactNumber());
		userDTO.setRole(entity.getStudentid().getRole().toString());
		dto.setStudentid(userDTO);

		dto.setType(GatePassRequestDTO.Gatepasstype.valueOf(entity.getType().toString()));
		dto.setReason(entity.getReason());
		dto.setEntrytime(entity.getEntrytime());
		dto.setExittime(entity.getExittime());
		dto.setDocumentPath(entity.getDocumentPath());
		dto.setQrcode(entity.getQrcode());
		dto.setStatus(entity.getStatus().toString());
		dto.setCreatedAt(entity.getCreatedAt());

		return dto;
	}

}
