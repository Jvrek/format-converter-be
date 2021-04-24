package com.to.formatconverterbe.controllers;

import com.to.formatconverterbe.converters.CSVConverter;
import com.to.formatconverterbe.converters.CsvToJsonConverter;
import com.to.formatconverterbe.converters.JSONFlattener;
import com.to.formatconverterbe.fileReader.FileName;
import com.to.formatconverterbe.fileReader.ResourceReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.to.formatconverterbe.messages.ResponseMessage;
import com.to.formatconverterbe.services.FilesStorageService;

import java.util.List;
import java.util.Map;

@CrossOrigin("*")
@Controller
@RequestMapping("/api")
public class FilesController {

    @Autowired
    FilesStorageService storageService;

//    @PostMapping("/files/upload")
//    public ResponseEntity<ResponseMessage> uploadFile(@RequestParam("file") MultipartFile file) {
//        String message = "";
//
//        try {
//            storageService.save(file);
//            message = "Uploaded the file successfully: " + file.getOriginalFilename();
//            return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message));
//        } catch (Exception e) {
//            message = "Could not upload the file: " + file.getOriginalFilename() + "!";
//            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));
//        }
//    }


    @PostMapping("v2/files/upload")
    public ResponseEntity<ResponseMessage> fileConversion(@RequestParam("file") MultipartFile file,@RequestParam String converted) {
        String message = "";
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

        return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message));

        }

        if(fileExtension.equals("csv") && converted.equals("json")){

            Resource fileStored = storageService.load(file.getOriginalFilename());

            String jSONFROMCSV  = CsvToJsonConverter.csvTojson(ResourceReader.asString(fileStored));

            CSVConverter.writeToFile(jSONFROMCSV,"uploads/" + fileName + ".json");
            message = fileName + ".json";

            return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message));

        }


        return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage("Error during conversion"));
    }

    @GetMapping("/file/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> getFile(@PathVariable("filename") String filename){
        Resource file = storageService.load(filename);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

//    @GetMapping("/file/{filename:.+}")
//    @ResponseBody
//    public ResponseEntity<Resource> getFile(@PathVariable("filename") String filename) {
//        Resource file = storageService.load(filename);
//        Resource file2 = storageService.load("student.csv");
//        ResourceReader.asString(file);
//        //System.out.println(ResourceReader.asString(file));
//
//        List<Map<String, String>> flatJson = JSONFlattener.parseJson(ResourceReader.asString(file));
//
//         String jSONFROMCSV  = CsvToJsonConverter.csvTojson(ResourceReader.asString(file2));
//
//        String csvString = CSVConverter.getCSV(flatJson);
//
//        CSVConverter.writeToFile(csvString,"uploads/text.csv");
//
//       // System.out.println(csvString);
//        System.out.println(jSONFROMCSV);
//
//
//        return ResponseEntity.ok()
//                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"").body(file);
//    }


}
