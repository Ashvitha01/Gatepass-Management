package in.oasys.gatepass.service;

import java.time.LocalDateTime;
import java.util.List;

import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Service;

import in.oasys.gatepass.dto.ApprovalDTO;
import in.oasys.gatepass.dto.GatePassRequestDTO;
import in.oasys.gatepass.dto.UserDTO;
import in.oasys.gatepass.entity.Approval;
import in.oasys.gatepass.entity.GatePassRequestEntity;
import in.oasys.gatepass.entity.NotificationEntity;
import in.oasys.gatepass.entity.User;
import in.oasys.gatepass.repository.ApprovalRepository;
import in.oasys.gatepass.repository.GatePassRequestRepository;
import in.oasys.gatepass.repository.NotificationRepository;

import jakarta.transaction.Transactional;

@Service
public class ApprovalService {

	@Autowired
	private ApprovalRepository approvalRepository;
	@Autowired
	private QRCodeGenerator qrCodeGenerator;
	@Autowired
	private GatePassRequestRepository gatePassRequestRepository;

	@Autowired
	private NotificationService notificationService;
	@Autowired
	private NotificationRepository notificationRepository;

	@Value("${staff.email}")
	private String staffEmail;

	// find the pending status
	public List<GatePassRequestDTO> findPendingByStatus() {
		return gatePassRequestRepository.findByStatus(GatePassRequestEntity.Status.PENDING).stream()
				.map(this::mapToGatePassDTO).collect(Collectors.toList());
	}

//approve the request gatepass
	@Transactional
	public ApprovalDTO approveRequest(User student, User staffid, String remarks) {
		GatePassRequestEntity request = gatePassRequestRepository.findByStudentid(student).stream()
				.filter(r -> r.getStatus() == GatePassRequestEntity.Status.PENDING).findFirst().orElseThrow(
						() -> new IllegalArgumentException("No request found for student ID: " + student.getUserId()));

		request.setStatus(GatePassRequestEntity.Status.APPROVED);
		gatePassRequestRepository.save(request);

		Approval approval = new Approval();

		approval.setApprovalId(UUID.randomUUID().toString());
		approval.setRequestId(request);
		approval.setStaffId(staffid);
		approval.setRemarks(remarks != null ? remarks : "Approved");
		approval.setApprovedAt(LocalDateTime.now());
		approval.setStatus(Approval.Status.APPROVED);

		approvalRepository.save(approval);

		String qrPath;
		try {
			qrPath = qrCodeGenerator.generateQRCode(student.getUserId());
			notificationService.sendApprovalNotification(request.getEmail(), qrPath);
		} catch (Exception e) {
			qrPath = "QR Generation Failed";
		}

		NotificationEntity notification = new NotificationEntity();
		notification.setMessage("Gate pass approved. QR Code path: " + qrPath);
		notification.setSent_at(LocalDateTime.now());
		notification.setStatus(NotificationEntity.Status.SENT);
		notification.setUserid(student);
		notificationRepository.save(notification);

		return mapToDTO(approval);
	}

//approve the emergency gatepass
	@Transactional
	public ApprovalDTO approveEmergencyRequest(User student, User securityUser, String remarks) {
		GatePassRequestEntity request = gatePassRequestRepository.findByStudentid(student).stream().findFirst()
				.orElseThrow(
						() -> new IllegalArgumentException("No request found for student ID: " + student.getUserId()));

		request.setStatus(GatePassRequestEntity.Status.APPROVED);
		gatePassRequestRepository.save(request);

		Approval approval = new Approval();
		approval.setApprovalId(UUID.randomUUID().toString());
		approval.setRequestId(request);
		approval.setStaffId(securityUser);
		approval.setRemarks(remarks != null ? remarks : "Emergency Approved");
		approval.setApprovedAt(LocalDateTime.now());
		approval.setStatus(Approval.Status.APPROVED);
		approvalRepository.save(approval);

		String qrPath;
		try {
			qrPath = qrCodeGenerator.generateQRCode(student.getUserId());

			// Send email with QR code attached
			notificationService.sendApprovalNotification(request.getEmail(), qrPath);

		} catch (Exception e) {
			qrPath = "QR Generation Failed";
			System.err.println("❌ Failed to send QR code email: " + e.getMessage());
		}

		NotificationEntity notification = new NotificationEntity();
		notification.setMessage("Emergency approval. QR: " + qrPath);
		notification.setSent_at(LocalDateTime.now());
		notification.setStatus(NotificationEntity.Status.SENT);
		notification.setUserid(student);
		notificationRepository.save(notification);

		return mapToDTO(approval);
	}

//reject the gatepass request
	public String rejectRequest(User student, String remarks) {
		List<GatePassRequestEntity> requests = gatePassRequestRepository.findByStudentid(student);
		boolean rejected = false;

		for (GatePassRequestEntity req : requests) {
			if (req.getStatus() == GatePassRequestEntity.Status.PENDING) {
				req.setStatus(GatePassRequestEntity.Status.REJECTED);
				gatePassRequestRepository.save(req);

				if (req.getEmail() != null) {
					notificationService.sendRejectionNotification(req.getEmail(), remarks);
				}

				NotificationEntity notification = new NotificationEntity();
				notification.setMessage("Gate pass rejected. Reason: " + remarks);
				notification.setSent_at(LocalDateTime.now());
				notification.setStatus(NotificationEntity.Status.SENT);
				notification.setUserid(student);
				notificationRepository.save(notification);

				rejected = true;
			}
		}

		return rejected ? "Gate pass rejected." : "No pending requests found to reject.";
	}

//bulk approve 
	@Transactional
	public String bulkApproveRequests(String eventId, User staffUser, String remarks) {
		List<GatePassRequestEntity> requests = gatePassRequestRepository.findByEventIdAndStatus(eventId,
				GatePassRequestEntity.Status.PENDING);

		if (requests.isEmpty()) {
			return "No pending requests for event.";
		}

		for (GatePassRequestEntity req : requests) {
			req.setStatus(GatePassRequestEntity.Status.APPROVED);
			gatePassRequestRepository.save(req);

			Approval approval = new Approval();
			approval.setApprovalId(UUID.randomUUID().toString());
			approval.setRequestId(req);
			approval.setStaffId(staffUser);
			approval.setRemarks(remarks != null ? remarks : "Bulk Approved");
			approval.setApprovedAt(LocalDateTime.now());
			approval.setStatus(Approval.Status.APPROVED);
			approvalRepository.save(approval);
		}

		try {
			String qrPath = qrCodeGenerator.generateQRCode("Event: " + eventId);
			for (GatePassRequestEntity req : requests) {
				notificationService.sendApprovalNotification(req.getEmail(), qrPath);
			}
			return "Bulk approved. QR Path: " + qrPath;
		} catch (Exception e) {
			return "Error generating bulk QR";
		}
	}

// fetch the history of approvals
	public List<ApprovalDTO> getApprovalHistory() {
		return approvalRepository.findAll().stream().map(this::mapToDTO).collect(Collectors.toList());
	}

	private ApprovalDTO mapToDTO(Approval approval) {
		ApprovalDTO dto = new ApprovalDTO();
		dto.setApprovalId(approval.getApprovalId());

		// Map Request ID only (not entire object)
		dto.setRequestId(approval.getRequestId().getRequestId());

		// Map staff as UserDTO
		User staff = approval.getStaffId();
		UserDTO staffDto = new UserDTO();
		staffDto.setUserId(staff.getUserId());
		staffDto.setName(staff.getName());
		staffDto.setEmail(staff.getEmail());
		staffDto.setContactNumber(staff.getContactNumber());
		// Safely handle null Role
		if (staff.getRole() != null) {
			staffDto.setRole(staff.getRole().toString());
		} else {
			staffDto.setRole("No Role Assigned"); // Default or appropriate fallback message
		}
		dto.setStaffId(staffDto);
		dto.setRemarks(approval.getRemarks());
		dto.setStatus(approval.getStatus().toString());
		dto.setApprovedAt(approval.getApprovedAt());

		return dto;
	}

	private GatePassRequestDTO mapToGatePassDTO(GatePassRequestEntity entity) {
		GatePassRequestDTO dto = new GatePassRequestDTO();
		dto.setRequestId(entity.getRequestId());

		// Map User -> UserDTO
		User student = entity.getStudentid();
		UserDTO userDTO = new UserDTO();
		userDTO.setUserId(student.getUserId());
		userDTO.setName(student.getName());
		userDTO.setEmail(student.getEmail());
		userDTO.setContactNumber(student.getContactNumber());
		userDTO.setRole(student.getRole().toString());
		dto.setStudentid(userDTO);

		dto.setReason(entity.getReason());
		dto.setEmail(entity.getEmail());
		dto.setEventId(entity.getEventId());
		dto.setDate(entity.getDate());
		dto.setEntrytime(entity.getEntrytime());
		dto.setExittime(entity.getExittime());
		dto.setType(GatePassRequestDTO.Gatepasstype.valueOf(entity.getType().name()));

		dto.setQrcode(entity.getQrcode());
		dto.setStatus(entity.getStatus().toString());
		dto.setDocumentPath(entity.getDocumentPath());
		dto.setCreatedAt(entity.getCreatedAt());

		return dto;
	}

}
