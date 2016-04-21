package lt.milkusteam.cloud.web.controller;

import com.dropbox.core.DbxSessionStore;
import com.dropbox.core.DbxStandardSessionStore;
import lt.milkusteam.cloud.core.service.DbxAuthService;
import lt.milkusteam.cloud.core.service.DbxFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.OutputStream;
import java.security.Principal;

/**
 * Created by gediminas on 4/17/16.
 */
@Controller
@RequestMapping(value = "/dbx")
public class DbxFilesController {

    @Autowired
    DbxFileService dbxFileService;

    @Autowired
    DbxAuthService dbxAuthService;

    @RequestMapping(value = "/files")
    public String showFiles(Model model, Principal principal,
                            @RequestParam(name = "path", required = false) String path) {
        String username = principal.getName();
        boolean isLinked = false;
        if (dbxAuthService.isLinked(username)) {
            if (!dbxFileService.containsClient(username)) {
                dbxFileService.addClient(username);
            }

            model.addAttribute("files", dbxFileService.getFilesMetadata(username, path));
            model.addAttribute("folders", dbxFileService.getFoldersMetadata(username, path));
            isLinked = true;
        }
        model.addAttribute("dbxAuth", isLinked);
        model.addAttribute("currentPath", path);

        return "dbx-files";
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
                                @RequestParam("state") String state,
                                @RequestParam("code") String code) {
        dbxAuthService.finishAuth(principal.getName(), state, code);

        return "redirect:/dbx/files?path=";
    }

    @RequestMapping(value = "/auth-clear", method = RequestMethod.POST)
    public String clearDbxAuth(Principal principal) {
        dbxAuthService.undoAuth(principal.getName());
        if (dbxFileService.containsClient(principal.getName())) {
            dbxFileService.removeClient(principal.getName());

        }
        return "index";
    }

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public String uploadFile(Principal principal,
                             @RequestParam("path") String path,
                             @RequestParam("file") MultipartFile file) {
        try {
            if (file.getSize() > 100_000_000) {
                throw new RuntimeException("File size > 100_000_000 upload is not supported yet.");
            } else {
                dbxFileService.upload(principal.getName(), path + "/" + file.getOriginalFilename(), file.getInputStream());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "redirect:/dbx/files?path=" + path;
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public String createFolder(Principal principal,
                               @RequestParam("path") String path,
                               @RequestParam("name") String name) {

        dbxFileService.createFolder(principal.getName(), path + "/" + name);

        return "redirect:/dbx/files?path=" + path;
    }

    @RequestMapping(value = "/download", method = RequestMethod.GET)
    public void downloadFile(Principal principal, HttpServletResponse response, @RequestParam("path") String path) {
        System.out.println(" ********** DOWNLOAD = " + path);

        OutputStream stream = null;
        try {
            stream = response.getOutputStream();
            response.setContentType("application/force-download");
            response.setHeader("Content-Disposition", "attachment; filename=" + path.substring(path.lastIndexOf("/") + 1));
            dbxFileService.download(principal.getName(), path, stream);
            response.flushBuffer();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    public String deleteFile(Principal principal, @RequestParam("path") String path) {
        System.out.println(" ********** DELETE = " + path);
        dbxFileService.delete(principal.getName(), path);
        return "redirect:/dbx/files?path=" + path.substring(0, path.lastIndexOf("/"));
    }
}
