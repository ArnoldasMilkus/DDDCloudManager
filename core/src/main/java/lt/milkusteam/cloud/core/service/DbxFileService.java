package lt.milkusteam.cloud.core.service;


import com.dropbox.core.DbxException;
import com.dropbox.core.InvalidAccessTokenException;
import com.dropbox.core.v2.files.DeletedMetadata;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.FolderMetadata;
import lt.milkusteam.cloud.core.model.Pair;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * Created by gediminas on 4/17/16.
 */
public interface DbxFileService {

    Pair<List<FolderMetadata>, List<FileMetadata>> getMetadataPair(String username, String path) throws DbxException;

    List<DeletedMetadata> getAllDeletedMetadata(String username) throws DbxException;

    boolean containsClient(String username);

    void removeClient(String username);

    boolean addClient(String username);

    void upload(String username, String dirPath, MultipartFile file) throws DbxException, IOException;

    void upload(String username, String fullPath, InputStream inputStream, long size) throws DbxException;

    void download(String username, String path, OutputStream outputStream) throws DbxException;

    InputStream getInputStream(String username, String path) throws InvalidAccessTokenException;

    void createFolder(String username, String path) throws DbxException;

    void delete(String username, String path) throws DbxException;

    void restore(String username, String path) throws DbxException;

    String getStorageInfo(String username) throws DbxException;

    void updateStorageInfo(String username) throws DbxException;

    String getAccountInfo(String username);
}
