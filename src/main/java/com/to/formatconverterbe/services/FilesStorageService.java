package com.to.formatconverterbe.services;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;
import com.to.formatconverterbe.interfaces.IFilesStorageService;

@Service
public class FilesStorageService implements IFilesStorageService {

    private final Path root = Paths.get("uploads");

    @Override
    public void init() {
        try {
            if(!Files.exists(root)){
                Files.createDirectory(root);
            };
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize main folder for upload!");
        }
    }

    @Override
    public void save(MultipartFile file) {
        try {
            if(!Files.exists(root)){
                Files.createDirectory(root);
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize folder for upload!");
        }
        try {
            Files.copy(file.getInputStream(), root.resolve(file.getOriginalFilename()));
        } catch (Exception e) {
            throw new RuntimeException("Could not store the file. Error: " + e.getMessage());
        }
    }

    @Override
    public Resource load(String filename) {
        if (Files.exists(root)) {
            try {
                Path file = root.resolve(filename);
                Resource resource = new UrlResource(file.toUri());
                if (resource.exists() || resource.isReadable()) {
                    return resource;
                } else {
                    throw new RuntimeException("Could not read the file!");
                }
            } catch (MalformedURLException e) {
                throw new RuntimeException("Error: " + e.getMessage());

            }
        }else {
            throw new RuntimeException("Could not read the file path!");
        }
    }

    //@Override
    //public void deleteAll() {
        //FileSystemUtils.deleteRecursively(root.toFile());
    //}

}
