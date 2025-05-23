package in.oasys.gatepass.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import in.oasys.gatepass.dto.BlacklistedStudentDTO;
import in.oasys.gatepass.entity.BlacklistedStudents;
import in.oasys.gatepass.entity.User;
import in.oasys.gatepass.repository.BlacklistedStudentRepository;
import in.oasys.gatepass.repository.UserRepository;

@Service
public class BlacklistedStudentService {

	@Autowired
	private BlacklistedStudentRepository blacklistRepo;

	@Autowired
	private UserRepository userRepo;

	// Blacklist a student
	public BlacklistedStudentDTO blacklistStudent(String userId, String reason) {
		User student = userRepo.findById(userId).orElseThrow(() -> new IllegalArgumentException("Student not found"));

		if (blacklistRepo.existsByStudentid(student)) {
			throw new IllegalStateException("Student is already blacklisted.");
		}

		BlacklistedStudents blacklist = new BlacklistedStudents();
		blacklist.setStudentid(student);
		blacklist.setReason(reason);
		blacklist.setStatus(BlacklistedStudents.Status.BLACKLISTED);
		blacklist.setBlacklistedat(LocalDateTime.now());

		BlacklistedStudents saved = blacklistRepo.save(blacklist);
		return mapToDTO(saved);
	}

	// Check blacklist status
	public boolean isStudentBlacklisted(String userId) {
		return blacklistRepo.findByStudentid_UserId(userId).isPresent();
	}

	// Remove from blacklist
	@Transactional
	public String removeFromBlacklist(String userId) {
		if (!userRepo.existsById(userId)) {
			return "Student not found.";
		}

		Optional<BlacklistedStudents> entry = blacklistRepo.findByStudentid_UserId(userId);
		if (entry.isEmpty()) {
			return "Student is not in blacklist.";
		}

		blacklistRepo.deleteByStudentid_UserId(userId);
		return "Student removed from blacklist.";
	}

	// fetch all balcklisted studentds list
	public List<BlacklistedStudentDTO> getallblacklist() {
		return blacklistRepo.findAll().stream()
				.filter(student -> student.getStatus() == BlacklistedStudents.Status.BLACKLISTED).map(this::mapToDTO)
				.toList();
	}

	// Map Entity to DTO
	private BlacklistedStudentDTO mapToDTO(BlacklistedStudents entity) {
		BlacklistedStudentDTO dto = new BlacklistedStudentDTO();
		dto.setId(entity.getId());
		dto.setStudentId(entity.getStudentid().getUserId());
		dto.setReason(entity.getReason());
		dto.setStatus(entity.getStatus().toString());
		dto.setBlacklistedAt(entity.getBlacklistedat());
		return dto;
	}
}
