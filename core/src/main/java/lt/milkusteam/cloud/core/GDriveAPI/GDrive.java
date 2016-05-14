package lt.milkusteam.cloud.core.GDriveAPI;

/**
 * Created by Vilintas Strielčiūnas on 2016-04-14.
 */

import com.google.api.client.auth.oauth2.*;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.*;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.*;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import lt.milkusteam.cloud.core.comparators.FilesSortComparator;
import sun.net.www.protocol.https.HttpsURLConnectionImpl;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

public class GDrive {
    /** Application name. */
    private static final String APPLICATION_NAME =
            "DDD Cloud Manager";
    private static final String MIME_FOLDER = "application/vnd.google-apps.folder";
    private static final String MIME_FILE = "application/vnd.google-apps.file";
    private static final String MIME_PHOTO = "application/vnd.google-apps.photo";

    private   Drive drive;
    private Credential credential;

    private java.io.File DATA_STORE_DIR; /*= new java.io.File(
            System.getProperty("user.home"), ".credentials/drive-java-quickstart.json");*/

    public GDrive(String userId, String driveID) {
        DATA_STORE_DIR = new java.io.File(System.getProperty("user.home"), ".credentials/"+
                userId +"/" + driveID );
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
//            DRIVE_SERVICE = getDriveService();
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(1);
        }
    }

    public GDrive(String userId, String driveID, GoogleTokenResponse resp) {
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            DATA_STORE_DIR = new java.io.File("", ".credentials/"+
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
                    .setFromTokenResponse(resp);
            drive = new Drive.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(1);
        }
    }

    public GDrive(String userId, String driveID, String token) {
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            DATA_STORE_DIR = new java.io.File("", ".credentials/"+
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
            t.printStackTrace();
            System.exit(1);
        }
    }

    public GDrive() {
    }

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

    /**
     * Creates an authorized Credential object.
     * @return an authorized Credential object.
     * @throws IOException
     */
//    private void authorize() throws IOException {
//        // Load client secrets.
//
//        InputStream in =
//                GDrive.class.getResourceAsStream("/client_secret.json");
//        if (in == null) {
//            System.out.println("Missing client secret.");
//            System.out.println("Download and add it into resources/client_secret.json");
//        }
//       GoogleClientSecrets clientSecrets =
//                GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
//
//        GoogleAuthorizationCodeRequestUrl reqUrl = new GoogleAuthorizationCodeRequestUrl("851556385458-s35qkeog0h3ph9pn7sbot0klvks3hpr5.apps.googleusercontent.com",
//                "http://rukykzuvi.ddns.net:8080/GDriveFiles", SCOPES);
//        String url = reqUrl.build();
//        // Build flow and trigger user authorization request.
//       /*GoogleAuthorizationCodeFlow flow =
//                new GoogleAuthorizationCodeFlow.Builder(
//                        HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
//                        .setDataStoreFactory(DATA_STORE_FACTORY)
//                        .setAccessType("online")
//                        .build();
//       Credential credential = new   Credential.Builder(BearerToken.authorizationHeaderAccessMethod()).setTransport(
//                 HTTP_TRANSPORT)
//                    .setJsonFactory(JSON_FACTORY)
//                    .setTokenServerUrl(
//                            new GenericUrl("http://rukykzuvi.ddns.net:8080/GDriveFiles"))
//                    .setClientAuthentication(new ClientParametersAuthentication("851556385458-s35qkeog0h3ph9pn7sbot0klvks3hpr5.apps.googleusercontent.com", "eO-rG_3ANrQz0TsLrYmkxDEi"))
//                    .build()
//                    /*.setFromTokenResponse(tokenResponse);
//        Credential credential = new AuthorizationCodeInstalledApp(
//                flow, new  LocalServerReceiver()).authorize("user");
//
//                System.out.println(
//                "Credentials saved to " + DATA_STORE_DIR.getAbsolutePath());*/
//        try {
//            if (Desktop.isDesktopSupported()) {
//                Desktop desktop = Desktop.getDesktop();
//                if (desktop.isSupported(Desktop.Action.BROWSE)) {
//                    System.out.println("Attempting to open that address in the default browser now...");
//                    desktop.browse(URI.create(url));
//                }
//            }
//        } catch (IOException e) {
//        } catch (InternalError e) {
//            // A bug in a JRE can cause Desktop.isDesktopSupported() to throw an
//            // InternalError rather than returning false. The error reads,
//            // "Can't connect to X11 window server using ':0.0' as the value of the
//            // DISPLAY variable." The exact error message may vary slightly.
//        }
//        return new Credential();
//    }


    /**
     * Build and return an authorized Drive client service.
     * @return an authorized Drive client service
     * @throws IOException
     */
//    private Drive getDriveService() throws IOException {
//        Credential credential = authorize();
//        return new Drive.Builder(
//                HTTP_TRANSPORT, JSON_FACTORY, credential)
//                .setApplicationName(APPLICATION_NAME)
//                .build();
//    }
//
//    private Drive getDriveServiceByUsername(String userName) throws IOException {
//        Credential credential = authorize();
//        return new Drive.Builder(
//                HTTP_TRANSPORT, JSON_FACTORY, credential)
//                .setApplicationName(APPLICATION_NAME)
//                .build();
//    }

    public static void main(String[] args) throws IOException {
        // Build a new authorized API client service.
//        GDrive drv = new GDrive("vilstr3", "drive001");
////        Drive service = drv.getDriveService();
//
//        // Print the names and IDs for up to 10 files.
//       /* FileList result = service.files().list()
//                .setPrettyPrint(true)
//                .setPageSize(50)
//                .setFields("nextPageToken, files(id, name)")
//                .execute();
//        List<File> files = result.getFiles();
//        if (files == null || files.size() == 0) {
//            System.out.println("No files found.");
//        } else {
//            System.out.println("Files:");
//            for (File file : files) {
//                System.out.printf("%s (%s)\n", file.getName(), file.getId());
//            }
//        }
//        File fileMetadata = new File();
//        fileMetadata.setName("Project plan");
//
//        fileMetadata.setParents()
//        FileOfCloud cFile = new FileOfCloud();
//        cFile.setgFile(fileMetadata);
//        simpleUpload(service, cFile);*/
//        String folderId = drv.findFileId("Tests", MIME_FOLDER, service);
//        if (folderId == null){
//            folderId = drv.createFolder("Tests", service);
//        }
//        else {
//            File fileMetadata = new File();
//            java.io.File fileToUpload = pickAFile(true);
//            if (fileToUpload == null) {
//                return;
//            }
//            fileMetadata.setName(fileToUpload.getName());
//            System.out.println(fileToUpload.getName());
//            System.out.println(fileToUpload.getName());
//           // fileMetadata.setMimeType(MIME_FILE);
//            List<String> parents = new ArrayList<String>(1);
//            parents.add(folderId);
//            fileMetadata.setParents(parents);
//
//            FileContent mediaContent = new FileContent("", fileToUpload);
//            File upFile = new GDriveUploader().simpleUpload(service, fileMetadata, mediaContent, false);
//            System.out.println("\n-----Uploaded data------");
//            System.out.println(upFile.toString());
//            System.out.println("--------------------------");
//            if (upFile != null) {
//                System.out.println("File ID: " + upFile.getId());
//                System.out.println(new GDriveDownloader().simpleDownload(service, HTTP_TRANSPORT, upFile.getId(), false));
//                moveToTrashBin(service, upFile.getId());
//            }
//            else {
//                System.out.println("Something went wrong with upload.");
//            }
//            String dFileID = drv.findFileId("dff.png", "image/png", service);
//            System.out.println(dFileID);
//            System.out.println(new GDriveDownloader().simpleDownload(service, HTTP_TRANSPORT, dFileID, false));
//            drv.getListByParentId("root");
//            //System.out.println(service.about().);
//        }
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

    public String createFolder(String name, Drive service) {
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
    public String findFileId(String name, String mimeType, Drive service) {
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

    public List<File> getListByParentId(String parentId) {
        List <File> list = new ArrayList<>();
        StringBuilder build = new StringBuilder();
        build.append("trashed=false and '");
        build.append(parentId);
        build.append("' in parents");
        if (parentId.isEmpty()) {
            build.setLength(0);
            build.append("trashed=false");
        }
        String pageToken = null;
        do {
            FileList result = null;
            try {
                result = drive.files().list()
                        .setQ(build.toString())
                        .setSpaces("drive")
                        .setFields("nextPageToken, files(name, id, parents, mimeType)")
                        .setPageToken(pageToken)
                        .execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            for(File file: result.getFiles()) {
                /*System.out.printf("Found file: %s (%s)\n",
                        file.getName(), file.getId());*/
                if (file.getMimeType().contains("folder")) {
                    file.setMimeType("folder");
                }
                list.add(file);
            }
            pageToken = result.getNextPageToken();
        } while (pageToken != null);
        /*for (File file: list) {
            System.out.printf("From list %s %s %s %s\n", file.getName(), file.getId(), file.getMimeType(), file.getParents().toString());
        }*/
        list.sort(new FilesSortComparator());
        return list;
    }

    public String getParentId(String fileId) {
        String parentId = null;
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

    public void requestAccessToken(String code) {
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
