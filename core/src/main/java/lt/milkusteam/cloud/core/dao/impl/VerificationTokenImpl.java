package lt.milkusteam.cloud.core.dao.impl;

import lt.milkusteam.cloud.core.dao.VerificationTokenDao;
import lt.milkusteam.cloud.core.dao.repository.VerificationTokenRepository;
import lt.milkusteam.cloud.core.model.VerificationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * Created by Arnoldas on 2016-05-14.
 */
@Repository
public class VerificationTokenImpl implements VerificationTokenDao {

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;
    @Override
    public VerificationToken findByToken(String token){
    return verificationTokenRepository.findByToken(token);
    }
    @Override
    public VerificationToken findByUsername(String username){
    return  verificationTokenRepository.findByUsername(username);
    }
    @Override
    public void save(VerificationToken verificationToken){
    verificationTokenRepository.save(verificationToken);
    }
}
