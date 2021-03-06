package lt.milkusteam.cloud.web.controller;

import com.dropbox.core.DbxSessionStore;
import com.dropbox.core.DbxStandardSessionStore;
import com.dropbox.core.InvalidAccessTokenException;
import com.dropbox.core.v2.files.DeletedMetadata;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.FolderMetadata;
import lt.milkusteam.cloud.core.model.Pair;
import lt.milkusteam.cloud.core.service.DbxAuthService;
import lt.milkusteam.cloud.core.service.DbxFileService;
import lt.milkusteam.cloud.core.service.GDriveFilesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Principal;
import java.util.List;

/**
 * Created by gediminas on 4/17/16.
 */
@Controller
@RequestMapping(value = "/dbx")
public class DbxFilesController {

    private static final Logger LOGGER = LoggerFactory.getLogger(DbxFilesController.class);

    @Autowired
    DbxFileService dbxFileService;

    @Autowired
    DbxAuthService dbxAuthService;

    @Autowired
    GDriveFilesService gdFilesService;

    @RequestMapping(value = "/")
    public String checkDbxAuthentication(Principal principal, RedirectAttributes redirectAttributes) {
        String username = principal.getName();
        if (dbxFileService.containsClient(username) || dbxFileService.addClient(username)) {
            return "redirect:/dbx/files";
        } else {
            redirectAttributes.addFlashAttribute("error", 1);
            return "redirect:/dbx/error";
        }
    }

    @RequestMapping(value = "/files")
    public String showFiles(Model model, Principal principal,
                            @RequestParam(name = "path", required = false) String path,
                            @RequestParam(name = "from", required = false) String from,
                            RedirectAttributes redirectAttributes) {
        String username = principal.getName();
        if (path == null) {
            path = "";
        }
        if (from != null && !from.isEmpty()) {
            model.addAttribute("from", from);
        }
        try {
            if (dbxFileService.containsClient(username) || dbxFileService.addClient(username)) {
                Pair<List<FolderMetadata>, List<FileMetadata>> metadataPair =
                        dbxFileService.getMetadataPair(username, path);
                model.addAttribute("files", metadataPair.getRight());
                model.addAttribute("folders", metadataPair.getLeft());
                model.addAttribute("spaceUsage", dbxFileService.getStorageInfo(username));
            } else {
                return "redirect:/dbx/";
            }
            model.addAttribute("currentPath", path);
        } catch (InvalidAccessTokenException e) {
            redirectAttributes.addFlashAttribute("error", 2);
            clearUserDbxData(username);
            return "redirect:/dbx/error";
        }
        return "dbx-files";
    }

    @RequestMapping(value = "/trash")
    public String showDeletedFiles(Model model, Principal principal,
                                   @RequestParam(name = "path", required = false) String path,
                                   RedirectAttributes redirectAttributes) {
        if (path == null) {
            path = "";
        }
        String username = principal.getName();
        if (path == null) {
            path = "";
        }
        try {
            if (dbxFileService.containsClient(username)) {
                List<DeletedMetadata> metadataList =
                        dbxFileService.getAllDeletedMetadata(username);
                model.addAttribute("files", metadataList);
            }
            model.addAttribute("currentPath", path);
        } catch (InvalidAccessTokenException e) {
            redirectAttributes.addFlashAttribute("error", 2);
            clearUserDbxData(username);
            return "redirect:/dbx/error";
        }
        return "dbx-deleted-files";
    }


    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public String uploadFile(Principal principal,
                             @RequestParam("path") String path,
                             @RequestParam("file") MultipartFile file,
                             RedirectAttributes redirectAttributes) {
        try {
            if (file.getSize() > dbxFileService.CHUNK_SIZE) {
                dbxFileService.uploadBig(principal.getName(), path + "/" + file.getOriginalFilename(),
                        file.getInputStream(), file.getSize());
            } else {
                dbxFileService.uploadSmall(principal.getName(), path + "/" + file.getOriginalFilename(), file.getInputStream());
            }
            dbxFileService.updateStorageInfo(principal.getName());
        } catch (InvalidAccessTokenException e) {
            redirectAttributes.addFlashAttribute("error", 2);
            clearUserDbxData(principal.getName());
            return "redirect:/dbx/error";
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
        return "redirect:/dbx/files?path=" + path;
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public String createFolder(Principal principal,
                               @RequestParam("path") String path,
                               @RequestParam("name") String name,
                               RedirectAttributes redirectAttributes) {

        try {
            dbxFileService.createFolder(principal.getName(), path + "/" + name);
        } catch (InvalidAccessTokenException e) {
            redirectAttributes.addFlashAttribute("error", 2);
            clearUserDbxData(principal.getName());
            return "redirect:/dbx/error";
        }
        return "redirect:/dbx/files?path=" + path;
    }

    @RequestMapping(value = "/download", method = RequestMethod.GET)
    public void downloadFile(Principal principal, HttpServletResponse response, @RequestParam("path") String path) {
        OutputStream stream = null;
        try {
            stream = response.getOutputStream();
            response.setContentType(determineContentType(path));
            response.setHeader("Content-Disposition", "attachment; filename=\"" + (path.substring(path.lastIndexOf("/") + 1) + "\""));
            dbxFileService.download(principal.getName(), path, stream);
            response.flushBuffer();
        } catch (InvalidAccessTokenException e) {
            try {
                response.sendRedirect("/dbx/error");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            LOGGER.error(e.getMessage());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    public String deleteFile(Principal principal, @RequestParam("path") String path, RedirectAttributes redirectAttributes) {
        try {
            dbxFileService.delete(principal.getName(), path);
            dbxFileService.updateStorageInfo(principal.getName());
        } catch (InvalidAccessTokenException e) {
            redirectAttributes.addFlashAttribute("error", 2);
            clearUserDbxData(principal.getName());
            return "redirect:/dbx/error";
        }
        return "redirect:/dbx/files?path=" + path.substring(0, path.lastIndexOf("/"));
    }

    @RequestMapping(value = "/restore", method = RequestMethod.GET)
    public String restoreFile(Principal principal, @RequestParam("path") String path, RedirectAttributes redirectAttributes) {
        try {
            dbxFileService.restore(principal.getName(), path);
            dbxFileService.updateStorageInfo(principal.getName());
        } catch (InvalidAccessTokenException e) {
            redirectAttributes.addFlashAttribute("error", 2);
            clearUserDbxData(principal.getName());
            return "redirect:/dbx/error";
        }
        return "redirect:/dbx/files?path=" + path.substring(0, path.lastIndexOf("/"));
    }

    @RequestMapping(value = "/error", method = RequestMethod.GET)
    public String showError() {
        return "dbx-error";
    }

    @RequestMapping(value = "/auth-start", method = RequestMethod.POST)
    public String startDbxAuth(Principal principal) {
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpSession session = attr.getRequest().getSession(true);

        DbxSessionStore store = new DbxStandardSessionStore(session, "somethingsomething");
        String authUrl = dbxAuthService.startAuth(principal.getName(), store);

        return "redirect:" + authUrl;
    }

    @RequestMapping(value = "/auth-finish")
    public String finishDbxAuth(Principal principal,
                                @RequestParam(name = "state", required = false) String state,
                                @RequestParam(name = "code", required=false) String code) {
        if (state != null && !state.isEmpty() && code != null && !code.isEmpty()) {
            dbxAuthService.finishAuth(principal.getName(), state, code);
            dbxFileService.addClient(principal.getName());
            return "redirect:/dbx/";
        } else {
            return "redirect:/settings";
        }
    }

    @RequestMapping(value = "/auth-clear", method = RequestMethod.POST)
    public String clearDbxAuth(Principal principal) {
        clearUserDbxData(principal.getName());
        return "index";
    }

    public void clearUserDbxData(String username) {
        dbxAuthService.undoAuth(username);
        dbxFileService.removeClient(username);
    }

    @RequestMapping(value = "/copyFrom/gd", method = RequestMethod.POST)
    public String copyFileFromGDrive(Principal principal,
                                   @RequestParam(name = "from") String from,
                                   @RequestParam(name = "to", required = false) String to,
                                   RedirectAttributes redirectAttributes) {
        String username = principal.getName();
        if (to == null) {
            to = "";
        }
        try {
            LOGGER.info(principal.getName() + " copied file from Google Drive = " + from + " to Dropbox = " + to);
            InputStream is = gdFilesService.returnStream(principal.getName(), 0, from);
            String name = gdFilesService.getName(username, 0, from);
            long size = gdFilesService.getSize(username, 0, from);
            if (size < dbxFileService.CHUNK_SIZE) {
                dbxFileService.uploadSmall(username, to + "/" + name, is);
            } else {
                dbxFileService.uploadBig(username, to + "/" + name, is, size);
            }
            is.close();
        } catch (InvalidAccessTokenException e) {
            redirectAttributes.addFlashAttribute("error", 2);
            clearUserDbxData(principal.getName());
            return "redirect:/dbx/error";
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
        return "redirect:/dbx/files?path=" + to;
    }

    public String determineContentType(String path) {
        String result = null;
        String ext = path.substring(path.lastIndexOf(".")+1);
        switch (ext) {
            case "pdf":
                result = "application/pdf";
                break;
            case "mp3":
                result = "audio/mpeg";
                break;
            case "mkv":
                result = "video/x-matroska";
                break;
            case "avi":
                result = "video/x-msvideo";
                break;
            case "mpeg":
            case "mp4":
            case "webm":
                result = "video/" + ext;
                break;
            case "png":
            case "jpeg":
            case "bmp":
            case "gif":
                result = "image/" + ext;
                break;
            default:
                result = "application/force-download";
        }
        return result;
    }
}
