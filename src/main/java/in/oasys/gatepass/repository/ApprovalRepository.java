package in.oasys.gatepass.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import in.oasys.gatepass.entity.Approval;
import in.oasys.gatepass.entity.Approval.Status;

@Repository
public interface ApprovalRepository extends JpaRepository<Approval, String> {
	List<Approval> findByStatus(Status status);
}
