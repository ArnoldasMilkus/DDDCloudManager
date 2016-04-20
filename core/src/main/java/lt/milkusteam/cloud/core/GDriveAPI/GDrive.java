package lt.milkusteam.cloud.core.GDriveAPI;

/**
 * Created by Vilintas Strielčiūnas on 2016-04-14.
 */

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import javax.swing.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GDrive {
    /** Application name. */
    private static final String APPLICATION_NAME =
            "DDD Cloud Manager";
    private static final String MIME_FOLDER = "application/vnd.google-apps.folder";
    private static final String MIME_FILE = "application/vnd.google-apps.file";
    private static final String MIME_PHOTO = "application/vnd.google-apps.photo";


    private static final String DIR_FOR_DOWNLOADS = "Downloads";

    private static final java.io.File DATA_STORE_DIR = new java.io.File(
            System.getProperty("user.home"), ".credentials/drive-java-quickstart.json");
            //"D:/Info/Gdrive/.credentials/drive-java-quickstart.json");

    /** Global instance of the {@link FileDataStoreFactory}. */
    private static FileDataStoreFactory DATA_STORE_FACTORY;

    /** Global instance of the JSON factory. */
    private static final JsonFactory JSON_FACTORY =
            JacksonFactory.getDefaultInstance();

    /** Global instance of the HTTP transport. */
    private static HttpTransport HTTP_TRANSPORT;

    /** Global Drive API client. */
    //private static Drive driveService;

    /** Global instance of the scopes required by this quickstart.
     *
     * If modifying these scopes, delete your previously saved credentials
     * at ~/.credentials/drive-java-quickstart.json
     */
    private static final List<String> SCOPES =
            Arrays.asList(DriveScopes.DRIVE);

    static {
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Creates an authorized Credential object.
     * @return an authorized Credential object.
     * @throws IOException
     */
    public static Credential authorize() throws IOException {
        // Load client secrets.
        InputStream in =
                GDrive.class.getResourceAsStream("/client_secret.json");
        if (in == null) {
            System.out.println("Missing client secret.");
            System.out.println("Download and add it into resources/client_secret.json");
        }
       GoogleClientSecrets clientSecrets =
                GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
       GoogleAuthorizationCodeFlow flow =
                new GoogleAuthorizationCodeFlow.Builder(
                        HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                        .setDataStoreFactory(DATA_STORE_FACTORY)
                        .setAccessType("offline")
                        .build();

        Credential credential = new AuthorizationCodeInstalledApp(
                flow, new LocalServerReceiver()).authorize("user");
        System.out.println(
                "Credentials saved to " + DATA_STORE_DIR.getAbsolutePath());
        return credential;
    }

    /**
     * Build and return an authorized Drive client service.
     * @return an authorized Drive client service
     * @throws IOException
     */
    public static Drive getDriveService() throws IOException {
        Credential credential = authorize();
        return new Drive.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    public static void main(String[] args) throws IOException {
        // Build a new authorized API client service.
        Drive service = getDriveService();

        // Print the names and IDs for up to 10 files.
       /* FileList result = service.files().list()
                .setPrettyPrint(true)
                .setPageSize(50)
                .setFields("nextPageToken, files(id, name)")
                .execute();
        List<File> files = result.getFiles();
        if (files == null || files.size() == 0) {
            System.out.println("No files found.");
        } else {
            System.out.println("Files:");
            for (File file : files) {
                System.out.printf("%s (%s)\n", file.getName(), file.getId());
            }
        }
        File fileMetadata = new File();
        fileMetadata.setName("Project plan");

        fileMetadata.setParents()
        FileOfCloud cFile = new FileOfCloud();
        cFile.setgFile(fileMetadata);
        simpleUpload(service, cFile);*/
        String folderId = findFileId("Tests", MIME_FOLDER, service);
        if (folderId == null){
            folderId = createFolder("Tests", service);
        }
        else {
            File fileMetadata = new File();
            java.io.File fileToUpload = pickAFile(true);
            if (fileToUpload == null) {
                return;
            }
            fileMetadata.setName(fileToUpload.getName());
            System.out.println(fileToUpload.getName());
            System.out.println(fileToUpload.getName());
           // fileMetadata.setMimeType(MIME_FILE);
            List<String> parents = new ArrayList<String>(1);
            parents.add(folderId);
            fileMetadata.setParents(parents);

            FileContent mediaContent = new FileContent("", fileToUpload);
            File upFile = new GDriveUploader().simpleUpload(service, fileMetadata, mediaContent, false);
            System.out.println("\n-----Uploaded data------");
            System.out.println(upFile.toString());
            System.out.println("--------------------------");
            if (upFile != null) {
                System.out.println("File ID: " + upFile.getId());
                System.out.println(new GDriveDownloader().simpleDownload(service, HTTP_TRANSPORT, upFile.getId(), false));
                moveToTrashBin(service, upFile.getId());
            }
            else {
                System.out.println("Something went wrong with upload.");
            }
            String dFileID = findFileId("dff.png", "image/png", service);
            System.out.println(dFileID);
            System.out.println(new GDriveDownloader().simpleDownload(service, HTTP_TRANSPORT, dFileID, false));
        }
    }

    /** File chooser method
     * TODO merge in future with Gediminas method
     * @param open open file?
     * @return chosen file
     */
    public static java.io.File pickAFile(boolean open) {
        java.io.File res = null;
        JFileChooser cho = new JFileChooser(".");
        int app;
        if (open) {
            app = cho.showOpenDialog(null);
        }
        else {
            app = cho.showSaveDialog(null);
        }
        if (app == JFileChooser.APPROVE_OPTION) {
            res = cho.getSelectedFile();
        }
        return res;
    }

    public static String createFolder(String name, Drive service) {
        File fileMetadata = new File();
        fileMetadata.setName(name);
        fileMetadata.setMimeType(MIME_FOLDER);

        File file = null;
        try {
            file = service.files().create(fileMetadata)
                    .setFields("id")
                    .setFields("mimeType")
                    .execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Folder ID: " + file.getId());
        return file.getId();
    }
    public static String findFileId(String name, String mimeType, Drive service) {
        if (mimeType.isEmpty()) {
            mimeType = MIME_FILE;
        }
        String pageToken = null;
        do {
            FileList result = null;
            try {
                result = service.files().list()
                        /*.setQ("mimeType='image/jpeg'")*/
                        .setSpaces("drive")
                        .setFields("nextPageToken, files(id, name, mimeType)")
                        .setPageToken(pageToken)
                        .execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            for(File file: result.getFiles()) {
               /* System.out.println("Name " + file.getName());
                System.out.println("MimeType " + file.getMimeType());*/
                if (file.getName().equals(name) && file.getMimeType().equals(mimeType)) {
                    return file.getId();
                }
               // System.out.printf("Found file: %s (%s)\n", file.getName(), file.getId());
            }
            pageToken = result.getNextPageToken();
        } while (pageToken != null);
        return null;
    }
    public static boolean moveToTrashBin(Drive service, String fileId) {
        try {
            service.files().get(fileId).set("trashed", true).setFields("trashed").execute();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
