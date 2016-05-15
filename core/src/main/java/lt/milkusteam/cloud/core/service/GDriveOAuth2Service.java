package lt.milkusteam.cloud.core.service;

import com.google.api.services.drive.model.File;

import java.io.InputStream;
import java.util.List;
import java.util.function.BooleanSupplier;

/**
 * Created by Vilintas on 2016-05-12.
 */
public interface GDriveOAuth2Service {

    boolean isLinked(String username);
    String getActivationURL();
    void unlinkUser(String username);
    void requestRefreshToken(String username, String code);
}
