package lt.milkusteam.cloud.core.GDriveAPI;

/**
 * Created by Vilintas Strielčiūnas on 2016-04-14.
 */

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.CredentialRefreshListener;
import com.google.api.client.auth.oauth2.DataStoreCredentialRefreshListener;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import lt.milkusteam.cloud.core.comparators.FilesSortComparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class GDrive {
    private static final Logger LOGGER = LoggerFactory.getLogger(GDrive.class);
    /** Application name. */
    private static final String APPLICATION_NAME =
            "DDD Cloud Manager";
    private static final String MIME_FOLDER = "application/vnd.google-apps.folder";
    private static final String MIME_FILE = "application/vnd.google-apps.file";

    private   Drive drive;
    private Credential credential;

    private java.io.File DATA_STORE_DIR;

    public GDrive(String userId, String driveID, GoogleTokenResponse resp) {
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            DATA_STORE_DIR = new java.io.File(".credentials/"+
                    userId +"/" + driveID );
            DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
            InputStream in = GDrive.class.getResourceAsStream("/client_secret.json");
            if (in == null) {
                LOGGER.error("Missing client secret.");
                LOGGER.error("Download and add it into resources/client_secret.json");
            }
            GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
            CredentialRefreshListener list = new DataStoreCredentialRefreshListener(userId, DATA_STORE_FACTORY);
            credential = new GoogleCredential.Builder().setTransport(HTTP_TRANSPORT)
                    .setJsonFactory(JSON_FACTORY)
                    .setClientSecrets(clientSecrets)
                    .addRefreshListener(list)
                    .build()
                    .setFromTokenResponse(resp);
            drive = new Drive.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
        } catch (Throwable t) {
            LOGGER.error(t.getMessage());
            System.exit(1);
        }
    }

    public GDrive(String userId, String driveID, String token) {
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            DATA_STORE_DIR = new java.io.File(".credentials/"+
                    userId +"/" + driveID );
            DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
            InputStream in = GDrive.class.getResourceAsStream("/client_secret.json");
            if (in == null) {
                System.out.println("Missing client secret.");
                System.out.println("Download and add it into resources/client_secret.json");
            }
            GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
            CredentialRefreshListener list = new DataStoreCredentialRefreshListener(userId, DATA_STORE_FACTORY);
           credential = new GoogleCredential.Builder().setTransport(HTTP_TRANSPORT)
                    .setJsonFactory(JSON_FACTORY)
                    .setClientSecrets(clientSecrets)
                    .addRefreshListener(list)
                    .build()
                    .setRefreshToken(token);
            drive = new Drive.Builder(
                    HTTP_TRANSPORT, JSON_FACTORY, credential)
                    .setApplicationName(APPLICATION_NAME)
                    .build();
        } catch (Throwable t) {
            LOGGER.error(t.getMessage());
            System.exit(1);
        }
    }

    /** Global instance of the {@link FileDataStoreFactory}. */
    private static FileDataStoreFactory DATA_STORE_FACTORY;

    /** Global instance of the JSON factory. */
    private static final JsonFactory JSON_FACTORY =
            JacksonFactory.getDefaultInstance();

    /** Global instance of the HTTP transport. */
    private static HttpTransport HTTP_TRANSPORT;

    public String createFolder(String name, String parentId) {
        File fileMetadata = new File();
        List<String> parents = new ArrayList<>();
        parents.add(parentId);
        fileMetadata.setParents(parents);
        fileMetadata.setName(name);
        fileMetadata.setMimeType(MIME_FOLDER);
        File file;
        try {
            file = drive.files().create(fileMetadata)
                    .setFields("id")
                    .setFields("mimeType, parents")
                    .execute();
            LOGGER.info("Folder ID: " + file.getId());
            return file.getId();
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
        return "";
    }
    public String findFileId(String name, String mimeType, Drive service) {
        if (mimeType.isEmpty()) {
            mimeType = MIME_FILE;
        }
        String pageToken = null;
        do {
            FileList result = null;
            try {
                result = service.files().list()
                        .setSpaces("drive")
                        .setFields("nextPageToken, files(id, name, mimeType)")
                        .setPageToken(pageToken)
                        .execute();
            } catch (IOException e) {
                LOGGER.error(e.getMessage());
            }
            for(File file: result.getFiles()) {
                if (file.getName().equals(name) && file.getMimeType().equals(mimeType)) {
                    return file.getId();
                }
            }
            pageToken = result.getNextPageToken();
        } while (pageToken != null);
        return null;
    }
    public void setTrashed(String fileId, boolean isTrashed) {
        try {
            File fileMetadata = new File();
            fileMetadata.setTrashed(isTrashed);
            System.out.println("File is trashed " + drive.files().update(fileId, fileMetadata).execute().getTrashed());
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
    }

    public List<File> getListByParentId(String parentId, boolean isTrashed) {
        List <File> list = new ArrayList<>();
        StringBuilder build = new StringBuilder();
        build.append("trashed=" +isTrashed + " and '");
        build.append(parentId);
        build.append("' in parents");
        if (parentId == null || parentId.isEmpty()) {
            build.setLength(0);
            build.append("trashed="+isTrashed);
        }
        String pageToken = null;
        do {
            FileList result = null;
            try {
                result = drive.files().list()
                        .setQ(build.toString())
                        .setSpaces("drive")
                        .setFields("nextPageToken, files(name, id, parents, mimeType, size)")
                        .setPageToken(pageToken)
                        .execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            for(File file: result.getFiles()) {
                if (file.getMimeType().contains("folder")) {
                    file.setMimeType("folder");
                }
                if (file.getSize() != null) {
                    file.setSize(file.getSize() / 1024);
                }
                list.add(file);
            }
            pageToken = result.getNextPageToken();
        } while (pageToken != null);
        list.sort(new FilesSortComparator());
        return list;
    }

    public String getParentId(String fileId) {
        String parentId = "root";
        List<String> list = null;
        try {
            list = drive.files().get(fileId).setFields("parents").execute().getParents();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (list != null && !list.isEmpty()) {
            parentId = list.get(list.size()-1);
        }
        return parentId;
    }

    public Drive getDrive() {
        return drive;
    }

    public void revokeToken(String token) {
        try {
            HttpResponse revokeResponse = HTTP_TRANSPORT.createRequestFactory()
                    .buildGetRequest(new GenericUrl(
                            String.format("https://accounts.google.com/o/oauth2/revoke?token=%s", token)))
                    .execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
