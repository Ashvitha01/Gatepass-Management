package in.oasys.gatepass.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import in.oasys.gatepass.entity.User;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
	Optional<User> findById(String userId);

	Optional<User> findByEmail(String email);

	Optional<User> findByUserId(String userId);
}
