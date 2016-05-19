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
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


public class GDrive {

    private static final Logger LOGGER = LoggerFactory.getLogger(GDrive.class);
    private static final String APPLICATION_NAME = "DDD Cloud Manager";
    private static final String MIME_FOLDER = "application/vnd.google-apps.folder";
    private static final String REPLACE_FOLDER_TYPE_TO = "folder";
    private static final String CONFIG_FILE_PATH = "/client_secret.json";
    private static final String PROPERTIES_FILE_PATH = "/GDrive.properties";

    /** Global instance of the {@link FileDataStoreFactory}. */
    private static FileDataStoreFactory DATA_STORE_FACTORY;
    /** Global instance of the JSON factory. */
    private static final JsonFactory JSON_FACTORY =
            JacksonFactory.getDefaultInstance();
    /** Global instance of the HTTP transport. */
    private static HttpTransport HTTP_TRANSPORT;
    private static final Resource resource = new ClassPathResource(PROPERTIES_FILE_PATH);
    private static Properties properties = null;

    private String revokeTokenUrl;
    private Drive drive;
    private Credential credential;
    private java.io.File DATA_STORE_DIR;

    public GDrive initGDrive(String userId, String driveID, GoogleTokenResponse resp) {
        try {
            revokeTokenUrl = getProperties().getProperty("GDrive.revoke_url");
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            DATA_STORE_DIR = new java.io.File(".credentials/"+
                    userId +"/" + driveID );
            DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
            InputStream in = GDrive.class.getResourceAsStream(CONFIG_FILE_PATH);
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
        } catch (GeneralSecurityException e) {
            LOGGER.error(e.getMessage());
            return null;
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
        return this;
    }

    private static void prepareProperties() {
        if (properties != null) {
            return;
        }
        try {
            properties = PropertiesLoaderUtils.loadProperties(resource);
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
    }

    public static Properties getProperties() {
        if (properties == null) {
            prepareProperties();
        }
        return properties;
    }

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
                    .setFields("id, mimeType, parents")
                    .execute();
            return file.getId();
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
        return "";
    }

    public void setTrashed(String fileId, boolean isTrashed) {
        try {
            File fileMetadata = new File();
            fileMetadata.setTrashed(isTrashed);
            drive.files().update(fileId, fileMetadata).execute();
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
                LOGGER.error(e.getMessage());
            }
            if (result == null) {
                return null;
            }
            for(File file: result.getFiles()) {
                if (file.getMimeType().contains(REPLACE_FOLDER_TYPE_TO)) {
                    file.setMimeType(REPLACE_FOLDER_TYPE_TO);
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
            LOGGER.error(e.getMessage());
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
                            String.format(revokeTokenUrl, token)))
                    .execute();
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
    }
}
