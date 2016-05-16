package lt.milkusteam.cloud.core.dao;

import lt.milkusteam.cloud.core.model.User;
import lt.milkusteam.cloud.core.model.VerificationToken;

import java.util.Date;

public interface VerificationTokenDao  {

    VerificationToken findByToken(String token);

    VerificationToken findByUsername(String username);

    void save(VerificationToken verificationToken);
}
