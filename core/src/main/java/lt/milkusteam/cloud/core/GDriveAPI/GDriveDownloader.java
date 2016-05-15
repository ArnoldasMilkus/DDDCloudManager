package lt.milkusteam.cloud.core.GDriveAPI;

import com.google.api.services.drive.Drive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by Vilintas Strielčiūnas on 2016-04-20.
 */
public class GDriveDownloader {
    private static final Logger LOGGER = LoggerFactory.getLogger(GDrive.class);
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
            LOGGER.error(ex.getMessage());
        }
    }
}
