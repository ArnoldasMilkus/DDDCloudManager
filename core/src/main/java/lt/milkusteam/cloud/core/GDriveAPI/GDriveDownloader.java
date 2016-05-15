package lt.milkusteam.cloud.core.GDriveAPI;

import com.google.api.client.googleapis.media.MediaHttpDownloader;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpTransport;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;

import javax.servlet.http.HttpServletResponse;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by Asus on 2016-04-20.
 */
public class GDriveDownloader {
    public String simpleDownload(Drive service, HttpTransport httpTransport, String fileId, boolean useDirectDownload) {
        String path = null;
        try {
            File dfile = service.files().get(fileId).execute();
            path = dfile.getName();
            if (path != null) {
                java.io.File file = new java.io.File(path);
                OutputStream outputStream = new FileOutputStream(file);

                MediaHttpDownloader downloader =
                        new MediaHttpDownloader(httpTransport, service.getRequestFactory().getInitializer());
                downloader.setDirectDownloadEnabled(useDirectDownload);
                downloader.setChunkSize(0x100000*10);
                downloader.setProgressListener(new GDriveDownloadProgressListener());
                downloader.download(new GenericUrl(service.files().get(fileId).buildHttpRequestUrl().toURL()), outputStream);
                outputStream.flush();
                outputStream.close();
            }
            else {
                System.out.println("Original file name is missing");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return path;
    }
    public void downloadToClient(Drive service, HttpServletResponse response, String fileId) {
        try {
            OutputStream stream = response.getOutputStream();
            response.setContentType(service.files().get(fileId).execute().getMimeType());
            String fileName = service.files().get(fileId).execute().getName();
            response.setContentLength(service.files().get(fileId).setFields("size").execute().getSize().intValue());
            response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
            service.files().get(fileId).executeMediaAndDownloadTo(stream);
            response.flushBuffer();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
