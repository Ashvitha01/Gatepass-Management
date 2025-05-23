package in.oasys.gatepass.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import in.oasys.gatepass.entity.ConfigurationEntity;

@Repository
public interface ConfigureRepository extends JpaRepository<ConfigurationEntity, Integer> {

	Optional<ConfigurationEntity> findByKeyname(String keyname);

}
