package ru.bvn13.voidforum.controllers;

import ru.bvn13.voidforum.models.StoredFile;
import ru.bvn13.voidforum.services.FileStorageService;
import ru.bvn13.voidforum.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.io.IOException;

import static org.springframework.http.MediaType.APPLICATION_OCTET_STREAM;
import static org.springframework.util.MimeTypeUtils.APPLICATION_OCTET_STREAM_VALUE;


@Controller
@RequestMapping("/files")
public class StoredFileController {

    @Autowired
    private UserService userService;

    @Autowired
    private FileStorageService storageService;

    @GetMapping(value = "/{fileName:.+}", produces = APPLICATION_OCTET_STREAM_VALUE)
    @ExceptionHandler(value = FileNotFoundException.class)
    public @ResponseBody
    HttpEntity<byte[]> getFileById(@PathVariable String fileName, final HttpServletResponse response) throws IOException {

        StoredFile file = this.storageService.getFileByName(fileName);
        if (file == null) {
            response.sendError(404, String.format("File %s not found", fileName));
            return null;
        }
        byte[] content;
        try {
            content = this.storageService.getFileContent(file.getPath());
        } catch (IOException e) {
            e.printStackTrace();
            if (this.userService.isCurrentUserAdmin()) {
                response.sendError(404, String.format("File %s (%s) not found", file.getName(), file.getPath()));
            } else {
                response.sendError(404, String.format("File %s not found", file.getName()));
            }
            return null;
        }

        HttpHeaders header = new HttpHeaders();
        header.setContentType(APPLICATION_OCTET_STREAM);
        header.set("Content-Disposition", "inline; filename=" + file.getName());
        header.setContentLength(file.getSize());

        return new HttpEntity<byte[]>(content, header);
    }

}
