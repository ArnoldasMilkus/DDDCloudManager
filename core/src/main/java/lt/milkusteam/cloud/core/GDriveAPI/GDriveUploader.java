package lt.milkusteam.cloud.core.GDriveAPI;

import com.google.api.client.googleapis.media.MediaHttpUploader;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.InputStreamContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Asus on 2016-04-20.
 */
public class GDriveUploader {
    public File simpleUpload(Drive service, File metadata, FileContent content, boolean useDirectUpload) {
        try {
            //upFile = service.files().create(metadata, content).execute();
            Drive.Files.Create create = service.files().create(metadata, content);
            MediaHttpUploader uploader = create.getMediaHttpUploader();
            uploader.setDirectUploadEnabled(useDirectUpload);
            uploader.setChunkSize(0x100000*10);
            uploader.setProgressListener(new GDriveUploadProgressListener());
            return create.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public File simpleUploadStream(Drive service, File metadata, InputStream inStream, boolean useDirectUpload) {
        try {
            InputStreamContent astr = new InputStreamContent("", inStream);
            Drive.Files.Create create = service.files().create(metadata, astr);
            MediaHttpUploader uploader = create.getMediaHttpUploader();
            uploader.setDirectUploadEnabled(useDirectUpload);
            uploader.setProgressListener(new GDriveUploadProgressListener());
            return create.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
