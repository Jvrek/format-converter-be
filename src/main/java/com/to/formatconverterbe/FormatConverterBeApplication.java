package com.to.formatconverterbe;

import com.to.formatconverterbe.converters.CsvToJsonConverter;
import com.to.formatconverterbe.services.FilesStorageService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.Resource;

@SpringBootApplication
public class FormatConverterBeApplication implements CommandLineRunner {

    @Resource
    FilesStorageService storageService;

    public static void main(String[] args) {
        SpringApplication.run(FormatConverterBeApplication.class, args);
    }

    @Override
    public void run(String... arg) throws Exception {
        storageService.init();
    }




}
