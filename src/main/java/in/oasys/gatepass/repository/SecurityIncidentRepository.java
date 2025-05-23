package in.oasys.gatepass.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import in.oasys.gatepass.entity.SecurityIncident;
import in.oasys.gatepass.entity.User;

@Repository
public interface SecurityIncidentRepository extends JpaRepository<SecurityIncident, Integer> {

	List<SecurityIncident> findByStudentid(User studentid);

	Optional<SecurityIncident> findAllByStudentid(User studentid);

	Optional<SecurityIncident> findByStudentidAndStatus(User student, SecurityIncident.Status status);

}
