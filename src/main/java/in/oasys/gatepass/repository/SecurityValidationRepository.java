package in.oasys.gatepass.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import in.oasys.gatepass.entity.SecurityValidation;

@Repository
public interface SecurityValidationRepository extends JpaRepository<SecurityValidation, Integer> {

}
