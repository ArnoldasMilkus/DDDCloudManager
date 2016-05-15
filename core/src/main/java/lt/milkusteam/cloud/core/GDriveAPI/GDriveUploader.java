package lt.milkusteam.cloud.core.GDriveAPI;

import com.google.api.client.googleapis.media.MediaHttpUploader;
import com.google.api.client.http.InputStreamContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Vilintas Strielčiūnas on 2016-04-20.
 */
public class GDriveUploader {
    private static final Logger LOGGER = LoggerFactory.getLogger(GDrive.class);

    public File simpleUploadStream(Drive service, File metadata, InputStream inStream, boolean useDirectUpload) {
        try {
            InputStreamContent astr = new InputStreamContent("", inStream);
            Drive.Files.Create create = service.files().create(metadata, astr);
            MediaHttpUploader uploader = create.getMediaHttpUploader();
            uploader.setDirectUploadEnabled(useDirectUpload);
            uploader.setProgressListener(new GDriveUploadProgressListener());
            return create.execute();
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
        return null;
    }
}
