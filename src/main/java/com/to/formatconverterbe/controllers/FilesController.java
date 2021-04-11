package com.to.formatconverterbe.controllers;

import com.to.formatconverterbe.converters.CSVWriter;
import com.to.formatconverterbe.converters.JSONFlattener;
import com.to.formatconverterbe.fileReader.ResourceReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.to.formatconverterbe.messages.ResponseMessage;
import com.to.formatconverterbe.services.FilesStorageService;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

@CrossOrigin("*")
@Controller
@RequestMapping("/api")
public class FilesController {

    @Autowired
    FilesStorageService storageService;

    @PostMapping("/files/upload")
    public ResponseEntity<ResponseMessage> uploadFile(@RequestParam("file") MultipartFile file) {
        String message = "";

        try {
            storageService.save(file);
            message = "Uploaded the file successfully: " + file.getOriginalFilename();
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message));
        } catch (Exception e) {
            message = "Could not upload the file: " + file.getOriginalFilename() + "!";
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));
        }
    }


    @GetMapping("/file/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> getFile(@PathVariable("filename") String filename) {
        Resource file = storageService.load(filename);

        ResourceReader.asString(file);
        System.out.println(ResourceReader.asString(file));

        List<Map<String, String>> flatJson = JSONFlattener.parseJson(ResourceReader.asString(file));

        String csvString = CSVWriter.getCSV(flatJson);

        System.out.println(csvString);


        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }
}
