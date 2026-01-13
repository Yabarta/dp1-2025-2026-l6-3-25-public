package es.us.dp1.l6_3_24_25.Petris.user;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface UserRepository extends JpaRepository<User, Integer>{


	Optional<User> findByUsername(String username);

	Boolean existsByUsername(String username);

	Optional<User> findById(Integer id);

	@Query("SELECT u FROM User u WHERE u.authority.authority = :auth")
	Iterable<User> findAllByAuthority(String auth);

	Page<User> findAll(Pageable pageable);

	@Query("SELECT u FROM User u WHERE u.authority.authority = :auth")
	Page<User> findAllByAuthority(String auth, Pageable pageable);


}
