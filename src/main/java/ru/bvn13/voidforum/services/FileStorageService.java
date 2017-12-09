package ru.bvn13.voidforum.services;


import ru.bvn13.voidforum.error.NotFoundException;
import ru.bvn13.voidforum.models.StoredFile;
import ru.bvn13.voidforum.models.User;
import ru.bvn13.voidforum.models.support.HttpContentTypeSerializer;
import ru.bvn13.voidforum.repositories.StoredFileRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class FileStorageService {

    public static final Logger logger = LoggerFactory.getLogger(FileStorageService.class);

    private AppSetting appSetting;

    private StoredFileRepository repository;

    @Autowired
    private UserService userService;

    @Autowired
    public FileStorageService(StoredFileRepository repository, AppSetting appSetting) {
        this.repository = repository;
        this.appSetting = appSetting;
        logger.debug("== UPLOAD PATH == > "+appSetting.getStoragePath());
    }

    public StoredFile getFileById(Long id) {
        return repository.findById(id);
    }

    public StoredFile getFileByName(String fileName) {
        StoredFile file = null;

        file = repository.findByName(fileName);
        if (file == null) {
            if (fileName.matches("\\d+")) {
                file = this.getFileById(Long.valueOf(fileName));
            }
        }

        if (file == null) {
            throw new NotFoundException("File " + fileName + " is not found");
        }

        return file;
    }

    public void storeFile(User user, String filename, byte[] content) throws IOException {
        File storage = new File(appSetting.getStoragePath());
        if (!storage.exists()) {
            storage.mkdirs();
        }
        String separator = "";
        if (!appSetting.getStoragePath().endsWith("/")) {
            separator = "/";
        }
        String userFolder = appSetting.getStoragePath() + separator + user.getId() + "/";
        File userStorage = new File(userFolder);
        if (!userStorage.exists()) {
            userStorage.mkdirs();
        }

        String fullname = userFolder + filename;
        Path path = Paths.get(fullname);
        Files.write(path, content);

        File file = new File(fullname);

        StoredFile storedFile = new StoredFile();
        storedFile.setPath(path.toAbsolutePath().toString());
        storedFile.setUser(this.userService.currentUser());
        storedFile.setTitle(filename);
        storedFile.setName(filename);
        storedFile.setSize(file.length());

        this.repository.saveAndFlush(storedFile);
    }

    public byte[] getFileContentById(Long fileId) throws IOException {
        StoredFile storedFile = this.repository.findById(fileId);
        Path path = Paths.get(storedFile.getPath());
        return Files.readAllBytes(path);
    }

    public byte[] getFileContent(String fullname) throws IOException {
        Path path = Paths.get(fullname);
        return Files.readAllBytes(path);
    }

    public void deleteFileById(Long fileId) throws IOException {
        StoredFile storedFile = this.repository.findById(fileId);
        Path path = Paths.get(storedFile.getPath());
        // first delete info, second delete file
        // because file might be deleted already
        this.repository.delete(storedFile);
        Files.delete(path);
    }

    public String getContentType(String fileName) {
        return HttpContentTypeSerializer.getContentType(fileName);
    }


}
