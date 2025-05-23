package in.oasys.gatepass.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

//import in.oasys.gatepass.entity.GatePassRequestEntity.Gatepasstype;
import in.oasys.gatepass.entity.GatePassRequestEntity.Status;
import in.oasys.gatepass.entity.User;
import in.oasys.gatepass.entity.GatePassRequestEntity;

import java.util.Optional;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface GatePassRequestRepository extends JpaRepository<GatePassRequestEntity, Integer> {
	Optional<GatePassRequestEntity> findFirstByStudentid(User studentid);
	

	Optional<GatePassRequestEntity> findLastByStudentid(User studentid);

	Optional<GatePassRequestEntity> findByStudentidAndStatus(User studentid, Status status);

	List<GatePassRequestEntity> findByStudentidAndStatusNotIn(User studentid,
			List<GatePassRequestEntity.Status> statuses);

	Optional<GatePassRequestEntity> findFirstByStudentidAndStatusNotIn(User studentid,
			List<GatePassRequestEntity.Status> statuses);

	List<GatePassRequestEntity> findByStudentid(User studentid);

	Optional<GatePassRequestEntity> findByStudentidAndDate(User studentid, Date date);

	List<GatePassRequestEntity> findByStatus(Status status);

	List<GatePassRequestEntity> findByEventIdAndStatus(String eventId, Status status);

	boolean existsByStudentidAndStatus(User studentid, Status status);

	// fetch reports based on optional filters
	List<GatePassRequestEntity> findByDateBetweenAndReasonContaining(LocalDate startDate, LocalDate endDate,
			String reason);

	// Find all students who have completed exits and sort by count
	List<GatePassRequestEntity> findByStatusOrderByExittimeDesc(GatePassRequestEntity.Status status);

//	new 

	public Optional<GatePassRequestEntity> findTopByStudentidOrderByEntrytimeDesc(User studentid);

	Optional<GatePassRequestEntity> findTopByStudentidAndStatusOrderByEntrytimeDesc(User studentid,
			GatePassRequestEntity.Status status);

	Optional<GatePassRequestEntity> findTopByStudentidOrderByRequestIdDesc(User studentid);

	Optional<GatePassRequestEntity> findTopByStudentidOrderByCreatedAtDesc(User studentid);
	
	
	@Query(value="select * from gatepass_request_table where studentid= ? order by request_id desc limit 1;",nativeQuery = true)
	GatePassRequestEntity  findByStudentidOrderByRequestId(String studentid);

}
