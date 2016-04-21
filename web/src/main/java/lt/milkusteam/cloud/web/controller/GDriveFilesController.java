package lt.milkusteam.cloud.web.controller;

import lt.milkusteam.cloud.core.service.GDriveFilesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

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

    @RequestMapping(value = "/GDriveFiles", method = RequestMethod.GET)
    public String showFiles(Model model,
                            @RequestParam(name = "rootId", required = false) String folderId,
                            @RequestParam(name = "backId", required = false) String childId,
                            Principal principal) {
        String username = principal.getName();
        if (folderId != null && !folderId.isEmpty()) {
            model.addAttribute("files", files.findAllInDirectory(folderId, username));
            model.addAttribute("curId", folderId);
        }
        else if (childId != null && !childId.isEmpty()) {
            folderId = files.getIfChild(childId, username);
            model.addAttribute("files", files.findAllInDirectory(folderId, username));
            model.addAttribute("curId", folderId);
        }
        else {
            model.addAttribute("files", files.findAllInDirectory("root", username));
            model.addAttribute("curId", "root");
        }
        return "GDriveFiles";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/GDriveUpload")
    public String provideUploadInfo(Model model,
                                    @RequestParam(name = "parentId", required = true) String parentId) {
        if (!parentId.isEmpty()){
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
        InputStream input = null;
        String fileName = file.getOriginalFilename();
        System.out.println(file.getOriginalFilename());
        System.out.println(parentId);
        if (!parentId.isEmpty()){
            model.addAttribute("parentId", parentId);
        }

        if (!file.isEmpty()) {
            try {
                input = file.getInputStream();
                files.uploadFile(input, parentId, fileName, username, true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return "redirect:/GDriveFiles?rootId=" + parentId;
    }
}
