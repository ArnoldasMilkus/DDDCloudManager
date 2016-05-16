package lt.milkusteam.cloud.core.service.impl;

import com.google.api.client.auth.oauth2.TokenResponseException;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import lt.milkusteam.cloud.core.GDriveAPI.GDrive;
import lt.milkusteam.cloud.core.dao.GDriveTokenDAO;
import lt.milkusteam.cloud.core.model.GDriveToken;
import lt.milkusteam.cloud.core.service.GDriveOAuth2Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;

/**
 * Created by Vilintas Strielčiūnas on 2016-05-12
 */
@Service
public class GDriveOAuth2ServiceImpl implements GDriveOAuth2Service {

    private static final Logger LOGGER = LoggerFactory.getLogger(GDriveOAuth2ServiceImpl.class);

    @Autowired
    private GDriveTokenDAO gDriveTokenDAO;

    @Override
    public boolean isLinked(String username) {
        return gDriveTokenDAO.findByUsername(username) != null;
    }

    @Override
    public String getActivationURL() {
        Properties properties = GDrive.getProperties();
        return new GoogleAuthorizationCodeRequestUrl(properties.getProperty("GDrive.client_id"),
                properties.getProperty("GDrive.redirect_uri"),
                Arrays.asList(properties.getProperty("GDrive.scope")))
                .setState("/profile")
                .setAccessType("offline")
                .build();
    }

    @Override
    public void unlinkUser(String username) {
        gDriveTokenDAO.delete(username);
    }

    @Override
    public void requestRefreshToken(String username, String code) {
        Properties properties = GDrive.getProperties();
        try {
            GoogleTokenResponse response =
                    new GoogleAuthorizationCodeTokenRequest(new NetHttpTransport(), new JacksonFactory(),
                            properties.getProperty("GDrive.client_id"), properties.getProperty("GDrive.client_secret"),
                            code, properties.getProperty("GDrive.redirect_uri"))
                            .execute();
            if (response.getRefreshToken() != null && !response.getRefreshToken().isEmpty()) {
                GDriveToken token = new GDriveToken(username, response.getRefreshToken());
                gDriveTokenDAO.save(token);
            }
        } catch (TokenResponseException e) {
            if (e.getDetails() != null) {
                System.err.println("Error: " + e.getDetails().getError());
                if (e.getDetails().getErrorDescription() != null) {
                    LOGGER.error(e.getDetails().getErrorDescription());
                }
                if (e.getDetails().getErrorUri() != null) {
                    LOGGER.error(e.getDetails().getErrorUri());
                }
            } else {
                LOGGER.error(e.getMessage());
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
    }
}
