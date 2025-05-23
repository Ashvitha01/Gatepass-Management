package in.oasys.gatepass.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import in.oasys.gatepass.entity.BlacklistedStudents;
import in.oasys.gatepass.entity.User;

@Repository
public interface BlacklistedStudentRepository extends JpaRepository<BlacklistedStudents, Integer> {

	// Check if a student is blacklisted
	boolean existsByStudentid(User studentid);

	Optional<BlacklistedStudents> findByStudentid_UserId(String userId);

	Optional<BlacklistedStudents> findByStudentidAndStatus(User student, BlacklistedStudents.Status status);

	// Delete student from blacklist
	void deleteByStudentid_UserId(String userId);

}
