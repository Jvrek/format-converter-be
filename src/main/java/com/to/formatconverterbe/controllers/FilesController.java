package com.to.formatconverterbe.controllers;


import com.to.formatconverterbe.converters.*;
import com.to.formatconverterbe.fileReader.FileName;
import com.to.formatconverterbe.fileReader.ResourceReader;
import lombok.SneakyThrows;
import org.json.XML;
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
import java.io.UncheckedIOException;
import java.util.List;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

//@CrossOrigin("*")
@Controller
@RequestMapping("/api")
public class FilesController {

    final
    FilesStorageService storageService;

    public FilesController(FilesStorageService storageService) {
        this.storageService = storageService;
    }


    @SneakyThrows
    @PostMapping("v2/files/upload")
    public ResponseEntity<ResponseMessage> fileConversion(@RequestParam("file") MultipartFile file
            ,@RequestParam String converted) {
        String message;
        String fileExtension = FileName.getFileExtension(file.getOriginalFilename());
        String fileName = FileName.getFileNameWithoutExtension(file.getOriginalFilename());

        try {
            storageService.save(file);

        } catch (Exception e) {
            message = "Could not upload the file: " + file.getOriginalFilename() + "!";
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));
        }

        if(fileExtension.equals("json") && converted.equals("csv")){
        Resource fileStored = storageService.load(file.getOriginalFilename());

        List<Map<String, String>> flatJson = JSONFlattener.parseJson(ResourceReader.asString(fileStored));

        CSVConverter.writeToFile(CSVConverter.getCSV(flatJson),"uploads/" + fileName +".csv");
        message = fileName + ".csv";

        storageService.delete(file.getOriginalFilename());

        return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message));

        }

        if(fileExtension.equals("csv") && converted.equals("json")){

            Resource fileStored = storageService.load(file.getOriginalFilename());

            String jSONFROMCSV  = CsvToJsonConverter.csvTojson(ResourceReader.asString(fileStored));

            CSVConverter.writeToFile(jSONFROMCSV,"uploads/" + fileName + ".json");
            message = fileName + ".json";

            storageService.delete(file.getOriginalFilename());
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message));

        }

        if(fileExtension.equals("xml") && converted.equals("json")){

            Resource fileStored = storageService.load(file.getOriginalFilename());

            //String jSONFROMCSV  = CsvToJsonConverter.csvTojson(ResourceReader.asString(fileStored));



            String jsonFormated = XMLtoJSON.convertToJson(fileStored.getFile());

            CSVConverter.writeToFile(jsonFormated,"uploads/" + fileName + ".json");
            message = fileName + ".json";

            storageService.delete(file.getOriginalFilename());
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message));

        }

        if(fileExtension.equals("json") && converted.equals("xml")){
            Resource fileStored = storageService.load(file.getOriginalFilename());

            String xmlFile = JSONtoXML.toString(StringToJson.toJson(ResourceReader.asString(fileStored)));

            CSVConverter.writeToFile(xmlFile,"uploads/" + fileName +".xml");
            message = fileName + ".xml";

            System.out.println("converting");
            storageService.delete(file.getOriginalFilename());
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message));

        }

        return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage("Error during conversion"));
    }

    @GetMapping("/file/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> getFile(@PathVariable("filename") String filename){
        Resource file = storageService.load(filename);

        System.out.println(file.getFilename());

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"").body(file);

    }

    @GetMapping("/content/{filename:.+}")
    @ResponseBody
    public ResponseEntity<String> getString(@PathVariable("filename") String filename){
        Resource file = storageService.load(filename);

        try (Reader reader = new InputStreamReader(file.getInputStream(), UTF_8)) {
            String fileAsString = FileCopyUtils.copyToString(reader);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"").body(fileAsString);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
