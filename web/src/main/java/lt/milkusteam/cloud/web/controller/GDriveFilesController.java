package lt.milkusteam.cloud.web.controller;

import com.dropbox.core.InvalidAccessTokenException;
import lt.milkusteam.cloud.core.service.DbxAuthService;
import lt.milkusteam.cloud.core.service.DbxFileService;
import lt.milkusteam.cloud.core.service.GDriveFilesService;
import lt.milkusteam.cloud.core.service.GDriveOAuth2Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.security.Principal;

/**
 * Created by Vilintas Strielčiūnas 2016-04-20
 */
@Controller
public class GDriveFilesController {

    private static final Logger LOGGER = LoggerFactory.getLogger(GDriveFilesController.class);

    @Autowired
    GDriveFilesService gDriveFilesService;

    @Autowired
    DbxFileService dbxFileService;

    @Autowired
    DbxAuthService dbxAuthService;

    @Autowired
    GDriveOAuth2Service gDriveOAuthService;

    @RequestMapping(value = "/GDriveFiles", method = RequestMethod.GET)
    public String showFiles(Model model,
                            @RequestParam(name = "rootId", required = false) String folderId,
                            @RequestParam(name = "backId", required = false) String childId,
                            @RequestParam(name = "error", required = false) String error,
                            @RequestParam(name = "isTrashed", required = false) boolean isTrashed,
                            @RequestParam(name = "isOnlyPathChoose", required = false) boolean isOnlyPathChoose,
                            @RequestParam(name = "from", required = false) String dbxFilePath,
                            @RequestParam(name = "isUploading", required = false) boolean isUploading,
                            Principal principal) {
        int ind = 0, check;
        String username = principal.getName();
        boolean isLinked = false;
        boolean isError = false;
        model.addAttribute("isTrashed", isTrashed);
        model.addAttribute("isOnlyPathChoose", isOnlyPathChoose);
        model.addAttribute("dbxFilePath", dbxFilePath);
        model.addAttribute("isUploading", isUploading);
        if (error != null && !error.isEmpty()) {
            model.addAttribute("error", "GDrive.error." + error);

            isError = true;
            model.addAttribute("isError", isError);
            model.addAttribute("driveAuth", isLinked);
            return "GDriveFiles";
        }
        if (gDriveOAuthService.isLinked(username)) {
            if (!gDriveFilesService.containsClient(username, ind)) {
                check = gDriveFilesService.addClient(username);
                if (check < 0){
                    model.addAttribute("driveAuth", false);
                    return "redirect:/GDriveFiles/revokeToken";
                }
            }
            else if (gDriveFilesService.getEmail(username, ind) == null) {
                model.addAttribute("driveAuth", false);
                return "redirect:/GDriveFiles/revokeToken";
            }
            if (isTrashed) {
                model.addAttribute("files", gDriveFilesService.findAllInDirectory("", username, isTrashed, 0));
                model.addAttribute("curId", "root");
            } else if (folderId != null && !folderId.isEmpty()) {
                model.addAttribute("files", gDriveFilesService.findAllInDirectory(folderId, username, isTrashed, 0));
                model.addAttribute("curId", folderId);
            } else if (childId != null && !childId.isEmpty()) {
                folderId = gDriveFilesService.getIfChild(childId, username, 0);
                model.addAttribute("files", gDriveFilesService.findAllInDirectory(folderId, username, isTrashed, 0));
                model.addAttribute("curId", folderId);
            } else {
                model.addAttribute("files", gDriveFilesService.findAllInDirectory("root", username, isTrashed, 0));
                model.addAttribute("curId", "root");
            }
            isLinked = true;
        }
        model.addAttribute("driveAuth", isLinked);
        return "GDriveFiles";
    }

    @RequestMapping(method = RequestMethod.POST, value = "/GDriveUpload")
    public String handleFileUpload(@RequestParam(name = "file", required = true) MultipartFile file,
                                   @RequestParam(name = "parentId", required = true) String parentId,
                                   Model model,
                                   Principal principal) {
        String username = principal.getName();
        InputStream input = null;
        String fileName = file.getOriginalFilename();
        if (!parentId.isEmpty()) {
            model.addAttribute("parentId", parentId);
        }
        if (!file.isEmpty()) {
            try {
                input = file.getInputStream();
                gDriveFilesService.uploadFile(input, parentId, fileName, username, false, 0);
            } catch (IOException e) {
                LOGGER.error(e.getMessage());
            }
        }
        if (input != null) {
            try {
                input.close();
            } catch (IOException e) {
                LOGGER.error(e.getMessage());
            }
        }
        return "redirect:/GDriveFiles?rootId=" + parentId;
    }
    @RequestMapping(method = RequestMethod.GET, value = "/GDriveFiles/startAuth")
    public String calcAuthUrl() {
        String gDriveOAuthServiceURL = gDriveOAuthService.getActivationURL();
        return "redirect:" + gDriveOAuthServiceURL;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/GDriveFiles/finishAuth")
    public String askForToken(Principal principal,
                              @RequestParam(name = "error", required = false) String error,
                              @RequestParam(name = "code", required = false) String code){
        String errorCode = "";
        if (error != null && !error.isEmpty()) {
            return "redirect:/GDriveFiles?error=" + error;
        }
        if (code.isEmpty()) {
            return "redirect:/GDriveFiles?error=error";
        }
        errorCode = gDriveOAuthService.requestRefreshToken(principal.getName(), code);
        if (!errorCode.isEmpty()) {
            return "redirect:/GDriveFiles?error=" + errorCode;
        }
        return "redirect:/GDriveFiles";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/GDriveFiles/revokeToken")
    public String revokeToken(Principal principal){
        String username = principal.getName();
        if (gDriveOAuthService.isLinked(username)) {
            gDriveFilesService.revokeToken(username, 0);
            return "redirect:/settings";
        }
        return "redirect:/GDriveFiles";
    }

    @RequestMapping(value = "/GDriveFiles/download", method = RequestMethod.GET)
    public void downloadFile(Principal principal, HttpServletResponse response, @RequestParam("fileId") String fileId) {
        String username = principal.getName();
        gDriveFilesService.downloadToClient(response, fileId, username, 0);
    }

    @RequestMapping(value = "/GDriveFiles/delete", method = RequestMethod.GET)
    public String deleteFile(Principal principal, @RequestParam("fileId") String fileId,
                             @RequestParam(name = "parentId", required = false) String parentId,
                             @RequestParam(name = "isTrashed", required = false) boolean isTrashed) {
        isTrashed = !isTrashed;
        String username = principal.getName();
        gDriveFilesService.fixTrashed(username, 0, isTrashed, fileId);
        if (parentId == null || parentId.isEmpty()) {
            parentId = "root";
        }
        if (isTrashed == false) {
            parentId = gDriveFilesService.getIfChild(fileId, username, 0);
            isTrashed = !isTrashed;
        }

        return "redirect:/GDriveFiles?rootId=" + parentId + "&isTrashed=" + !isTrashed;
    }
    @RequestMapping(method = RequestMethod.POST, value = "/GDriveFiles/newFolder")
    public String newFolder(@RequestParam(name = "folderName", required = true) String folderName,
                            @RequestParam(name = "parentId", required = true) String parentId,
                            Principal principal) {
        String username = principal.getName();
        if (folderName == null || folderName.isEmpty()) {
            folderName = "New Folder";
        }
        if (parentId == null || parentId.isEmpty()) {
            parentId = "root";
        }
        gDriveFilesService.newFolder(username, 0, folderName, parentId);

        return "redirect:/GDriveFiles?rootId=" + parentId;
    }
    @RequestMapping(value = "/GDriveFiles/copyFromDropbox", method = RequestMethod.GET)
    public String copyFileToGDrive(Principal principal,
                                   @RequestParam(name = "from") String from,
                                   @RequestParam(name = "to", required = false) String to,
                                   RedirectAttributes redirectAttributes) {
        if (to == null || to.isEmpty()) {
            to = "root";
        }
        try {
            LOGGER.info(principal.getName() + " copied file from Dropbox = " + from + " to Google Drive = " + to);
            InputStream is = dbxFileService.getInputStream(principal.getName(), from);
            if (is == null) {
                LOGGER.error("Input stream is null");
                return "redirect:/GDriveFiles?error=multiTabs";
            }
            gDriveFilesService.uploadFile(is, to, from.substring(from.lastIndexOf("/") + 1), principal.getName(), true, 0);
            is.close();
        } catch (InvalidAccessTokenException e) {
            redirectAttributes.addFlashAttribute("error", 2);
            dbxAuthService.undoAuth(principal.getName());
            dbxFileService.removeClient(principal.getName());
            return "redirect:/dbx/error";
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
        return "redirect:/GDriveFiles?rootId=" + to;
    }
}
