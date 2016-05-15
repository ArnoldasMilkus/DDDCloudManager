package lt.milkusteam.cloud.web.controller;

import lt.milkusteam.cloud.core.service.GDriveFilesService;
import lt.milkusteam.cloud.core.service.GDriveOAuth2Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.security.Principal;

/**
 * Created by Vilintas 2016-04-20
 */
@Controller
public class GDriveFilesController {

    @Autowired
    GDriveFilesService files;

    @Autowired
    GDriveOAuth2Service authorization;

    @RequestMapping(value = "/GDriveFiles", method = RequestMethod.GET)
    public String showFiles(Model model,
                            @RequestParam(name = "rootId", required = false) String folderId,
                            @RequestParam(name = "backId", required = false) String childId,
                            @RequestParam(name = "error", required = false) String error,
                            @RequestParam(name = "isTrashed", required = false) boolean isTrashed,
                            Principal principal) {
        int id = 0;
        String username = principal.getName();
        boolean isLinked = false;
        boolean isError = false;
        model.addAttribute("isTrashed", isTrashed);
        if (error != null && !error.isEmpty()) {
            if (error.contains("access_denied")) {
                model.addAttribute("error", "GDrive.error.access_denied");
            }
            else {
                model.addAttribute("error", "GDrive.error.unknown");
            }
            isError = true;
            model.addAttribute("isError", isError);
            model.addAttribute("driveAuth", isLinked);
            return "GDriveFiles";
        }
        if (authorization.isLinked(username)) {
            if (!files.containsClient(username, 0)) {
                files.addClient(username);
            }
            if (isTrashed) {
                model.addAttribute("files", files.findAllInDirectory("", username, isTrashed));
                model.addAttribute("curId", "root");
            } else if (folderId != null && !folderId.isEmpty()) {
                model.addAttribute("files", files.findAllInDirectory(folderId, username, isTrashed));
                model.addAttribute("curId", folderId);
            } else if (childId != null && !childId.isEmpty()) {
                folderId = files.getIfChild(childId, username);
                model.addAttribute("files", files.findAllInDirectory(folderId, username, isTrashed));
                model.addAttribute("curId", folderId);
            } else {
                model.addAttribute("files", files.findAllInDirectory("root", username, isTrashed));
                model.addAttribute("curId", "root");
            }
            isLinked = true;
        }
        model.addAttribute("driveAuth", isLinked);
        return "GDriveFiles";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/GDriveUpload")
    public String provideUploadInfo(Model model,
                                    @RequestParam(name = "parentId", required = true) String parentId) {
        if (!parentId.isEmpty()) {
            model.addAttribute("parentId", parentId);
        }
        return "GDriveUpload";
    }

    @RequestMapping(method = RequestMethod.POST, value = "/GDriveUpload")
    public String handleFileUpload(@RequestParam(name = "file", required = true) MultipartFile file,
                                   @RequestParam(name = "parentId", required = true) String parentId,
                                   Model model,
                                   Principal principal) {
        String username = principal.getName();
        InputStream input;
        String fileName = file.getOriginalFilename();
        if (!parentId.isEmpty()) {
            model.addAttribute("parentId", parentId);
        }
        if (!file.isEmpty()) {
            try {
                input = file.getInputStream();
                files.uploadFile(input, parentId, fileName, username, false);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return "redirect:/GDriveFiles?rootId=" + parentId;
    }
    @RequestMapping(method = RequestMethod.GET, value = "/GDriveFiles/startAuth")
    public String calcAuthUrl(Principal principal) {
        String authorizationURL = authorization.getActivationURL();
        return "redirect:" + authorizationURL;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/GDriveFiles/finishAuth")
    public String askForToken(Principal principal,
                              @RequestParam(name = "error", required = false) String error,
                              @RequestParam(name = "code", required = false) String code){
        if (error != null && !error.isEmpty()) {
            return "redirect:/GDriveFiles?error=" + error;
        }
        if (code.isEmpty()) {
            return "redirect:/GDriveFiles?error=error";
        }
        authorization.requestRefreshToken(principal.getName(), code);
        return "redirect:/GDriveFiles";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/GDriveFiles/revokeToken")
    public String revokeToken(Principal principal){
        String username = principal.getName();
        if (authorization.isLinked(username)) {
            files.revokeToken(username, 0);
        }
        return "redirect:/GDriveFiles";
    }

    @RequestMapping(value = "/GDriveFiles/download", method = RequestMethod.GET)
    public void downloadFile(Principal principal, HttpServletResponse response, @RequestParam("fileId") String fileId) {
        String username = principal.getName();
        files.downloadToClient(response, fileId, username, 0);
    }

    @RequestMapping(value = "/GDriveFiles/delete", method = RequestMethod.GET)
    public String deleteFile(Principal principal, @RequestParam("fileId") String fileId,
                           @RequestParam(name = "parentId", required = false) String parentId,
                             @RequestParam(name = "isTrashed", required = false) boolean isTrashed) {
        isTrashed = !isTrashed;
        String username = principal.getName();
        files.fixTrashed(username, 0, isTrashed, fileId);
        if (parentId == null || parentId.isEmpty()) {
            parentId = "root";
        }
        if (isTrashed == false) {
            parentId = files.getIfChild(fileId, username);
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
        files.newFolder(username, 0, folderName, parentId);

        return "redirect:/GDriveFiles?rootId=" + parentId;
    }
}
