package lt.milkusteam.cloud.core.dao.repository;

import lt.milkusteam.cloud.core.model.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, String> {

    VerificationToken findByToken(String token);

    VerificationToken findByUsername(String username);
}
