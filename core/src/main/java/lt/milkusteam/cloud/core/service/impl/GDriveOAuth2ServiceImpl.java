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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;

/**
 * Created by Vilintas on 2016-05-12
 */
@Service
public class GDriveOAuth2ServiceImpl implements GDriveOAuth2Service {

    private final static String CLIENT_ID = "851556385458-s35qkeog0h3ph9pn7sbot0klvks3hpr5.apps.googleusercontent.com";

    private final static String CLIENT_SECRET = "eO-rG_3ANrQz0TsLrYmkxDEi";

    private final static String REDIRECT_URL = "http://localhost:8080/GDriveFiles/finishAuth";

    private final static String SCOPE = "https://www.googleapis.com/auth/drive";

    @Autowired
    private GDriveTokenDAO GDriveTokenDAO;

    @Override
    public boolean isLinked(String username) {
        return GDriveTokenDAO.findByUsername(username) != null;
    }

    @Override
    public String getActivationURL() {
        return new GoogleAuthorizationCodeRequestUrl(CLIENT_ID, REDIRECT_URL, Arrays.asList(SCOPE))
                .setState("/profile")
                .setAccessType("offline")
                .build();
    }

    @Override
    public void unlinkUser(String username) {
        GDriveTokenDAO.delete(username);
    }

    @Override
    public void requestRefreshToken(String username, String code) {
        try {
            GoogleTokenResponse response =
                    new GoogleAuthorizationCodeTokenRequest(new NetHttpTransport(), new JacksonFactory(),
                            CLIENT_ID, CLIENT_SECRET,
                            code, REDIRECT_URL)
                            .execute();
            System.out.println("This access token: " + response.getAccessToken());
            System.out.println("This refresh token: " + response.getRefreshToken());
            if (response.getRefreshToken() != null && !response.getRefreshToken().isEmpty()) {
                GDriveToken token = new GDriveToken(username, response.getRefreshToken());
                GDriveTokenDAO.save(token);
            }
        } catch (TokenResponseException e) {
            if (e.getDetails() != null) {
                System.err.println("Error: " + e.getDetails().getError());
                if (e.getDetails().getErrorDescription() != null) {
                    System.err.println(e.getDetails().getErrorDescription());
                }
                if (e.getDetails().getErrorUri() != null) {
                    System.err.println(e.getDetails().getErrorUri());
                }
            } else {
                System.err.println(e.getMessage());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
