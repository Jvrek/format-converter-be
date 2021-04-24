package com.to.formatconverterbe.fileReader;

public class FileName {

    public static String getFileExtension(String fileName){
        int index = fileName.lastIndexOf('.');
        if(index > 0) {
            String extension = fileName.substring(index + 1);
           return extension;
        }else
        {
            return null;
        }

    }

    public static String getFileNameWithoutExtension(String file) {
        String fileName = "";

        fileName = file.replaceFirst("[.][^.]+$", "");

        if(!fileName.isEmpty())
        return fileName;
        else
            return null;

    }
}
