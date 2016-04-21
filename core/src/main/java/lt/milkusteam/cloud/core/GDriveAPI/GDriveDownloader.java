package lt.milkusteam.cloud.core.GDriveAPI;

import com.google.api.client.googleapis.media.MediaHttpDownloader;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpTransport;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;

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
    /*public String downloadToClient(Drive service, HttpTransport httpTransport, String fileId, boolean useDirectDownload) {
        HttpServletResponse response = new HttpServletResponseWrapper().setHeader("Content-Disposition", "attachment; filename=datafile.xls");
        response.setContentType("application/vnd.ms-excel");
        OutputStream outStream = response.getOutputStream();

        byte[] buf = new byte[4096];
        int len = -1;

//Write the file contents to the servlet response
//Using a buffer of 4kb (configurable). This can be
//optimized based on web server and app server
//properties
        while ((len = inStream.read(buf)) != -1) {
            outStream.write(buf, 0, len);
        }

        outStream.flush();
        outStream.close();
    }*/
}
