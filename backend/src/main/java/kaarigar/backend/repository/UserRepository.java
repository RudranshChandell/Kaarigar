package kaarigar.backend.repository;

import kaarigar.backend.enums.Role;
import kaarigar.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByName(String name);

    List<User> findByRole(Role role);

    Optional<User> findByEmailIgnoreCase(String email);

    // Count users by role (used in analytics)
    long countByRole(Role role);
}

