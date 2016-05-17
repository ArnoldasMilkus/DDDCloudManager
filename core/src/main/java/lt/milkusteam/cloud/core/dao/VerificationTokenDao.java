package lt.milkusteam.cloud.core.dao;

import lt.milkusteam.cloud.core.model.VerificationToken;

public interface VerificationTokenDao  {

    VerificationToken findByToken(String token);

    VerificationToken findByUsername(String username);

    void save(VerificationToken verificationToken);
}
