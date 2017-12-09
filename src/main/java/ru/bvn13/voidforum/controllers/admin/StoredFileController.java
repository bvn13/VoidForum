package ru.bvn13.voidforum.controllers.admin;

import ru.bvn13.voidforum.error.NotFoundException;
import ru.bvn13.voidforum.forms.StoredFileForm;
import ru.bvn13.voidforum.models.StoredFile;
import ru.bvn13.voidforum.repositories.StoredFileRepository;
import ru.bvn13.voidforum.services.FileStorageService;
import ru.bvn13.voidforum.services.UserService;
import ru.bvn13.voidforum.utils.DTOUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Date;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller("adminUploadController")
@RequestMapping("admin/files")
public class StoredFileController {

    private static final int PAGE_SIZE = 20;

    @Autowired
    private FileStorageService storageService;

    @Autowired
    private StoredFileRepository storedFileRepository;

    @Autowired
    private UserService userService;

    @GetMapping("")
    public String index(@RequestParam(defaultValue = "0") int page, Model model) {
        Page<StoredFile> files = storedFileRepository.findAll(new PageRequest(page, PAGE_SIZE, Sort.Direction.DESC, "id"));

        model.addAttribute("totalPages", files.getTotalPages());
        model.addAttribute("page", page);
        model.addAttribute("files", files);

        return "admin/files/index";
    }

    @PostMapping("/upload") //new annotation since 4.3
    public String upload(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("uploadStatus", "Please select a file to upload");
            return "redirect:/admin/files/status";
        }

        String message = "";

        try {

            // Get the file and save it somewhere
            byte[] bytes = file.getBytes();

            this.storageService.storeFile(userService.currentUser(), file.getOriginalFilename(), bytes);

            message = "You successfully uploaded '" + file.getOriginalFilename() + "'";
            redirectAttributes.addFlashAttribute("uploadStatus", message);

        } catch (Exception e) {
            e.printStackTrace();
            message = "Internal server error occured";
            redirectAttributes.addFlashAttribute("uploadStatus", message);
        }

        return "redirect:/admin/files/status";
    }

    @GetMapping("/status")
    public String uploadStatus() {
        return "admin/files/status";
    }



    @GetMapping(value = "/{fileId:[\\d]+}/edit")
    public String editFileById(@PathVariable Long fileId, Model model) {
        Assert.notNull(fileId);
        StoredFile file = this.storageService.getFileById(fileId);
        if (file == null) {
            //response.sendError(404, String.format("File %s not found", fileId));
            throw new NotFoundException(String.format("File with id %s not found", fileId));
        }

        StoredFileForm fileForm = DTOUtil.map(file, StoredFileForm.class);

        model.addAttribute("file", file);
        model.addAttribute("fileForm", fileForm);

        return "admin/files/edit";
    }


    @PostMapping(value = "/{fileId:[\\d]+}")
    public String saveFile(@PathVariable Long fileId, @Valid StoredFileForm fileForm, Errors errors) {
        Assert.notNull(fileId);

        if (errors.hasErrors()) {
            return String.format("admin/files/%d/edit", fileId);
        }

        StoredFile storedFile = this.storedFileRepository.findById(fileId);
        DTOUtil.mapTo(fileForm, storedFile);
        storedFile.setUser(this.userService.currentUser());
        storedFile.setUpdatedAt(new Date());
        this.storedFileRepository.save(storedFile);

        return "redirect:/admin/files";
    }

    @RequestMapping(value = "{fileId:[0-9]+}/delete", method = {DELETE, POST})
    public String deletePost(@PathVariable Long fileId){
        try {
            this.storageService.deleteFileById(fileId);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "redirect:/admin/files";
    }

}
