package lt.milkusteam.cloud.core.GDriveAPI;

import com.google.api.client.googleapis.media.MediaHttpUploader;
import com.google.api.client.googleapis.media.MediaHttpUploaderProgressListener;

import java.io.IOException;

/**
 * Created by Asus on 2016-04-20.
 */
public class GDriveUploadProgressListener implements MediaHttpUploaderProgressListener {

    private long allSize;

    @Override
    public void progressChanged(MediaHttpUploader uploader) throws IOException {
        switch (uploader.getUploadState()) {
            case INITIATION_STARTED:
                ProgressViewer.header2("Upload Initiation has started.");
                break;
            case INITIATION_COMPLETE:
                ProgressViewer.header2("Upload Initiation is Complete.");
                break;
            case MEDIA_IN_PROGRESS:
                ProgressViewer.header2("Upload is In Progress: "
                        + uploader.getNumBytesUploaded() + " bytes");
                break;
            case MEDIA_COMPLETE:
                ProgressViewer.header2("Upload is Complete!");
                break;
        }
    }
}
