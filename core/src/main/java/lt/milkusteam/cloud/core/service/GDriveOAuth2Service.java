package lt.milkusteam.cloud.core.service;

/**
 * Created by Vilintas Strielčiūnas on 2016-05-12.
 */
public interface GDriveOAuth2Service {

    boolean isLinked(String username);
    String getActivationURL();
    void unlinkUser(String username);
    void requestRefreshToken(String username, String code);
}
