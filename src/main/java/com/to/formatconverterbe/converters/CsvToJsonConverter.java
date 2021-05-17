package com.to.formatconverterbe.converters;

public class CsvToJsonConverter {


    public static String csvTojson(String content,String separator){

        StringBuilder sb=new StringBuilder("[\n");

        String csv = content;
        if(csv.contains("\"")){
            csv.replaceAll("\"","");
        }
        csv = csv.replaceAll("\\r","");
        String[] csvValues = csv.split("\n");
        String[] header = csvValues[0].split(separator);

        for(int i=1;i<csvValues.length;i++){
            sb.append("\t").append("{").append("\n");
            String[] tmp = csvValues[i].split(separator);
            for(int j=0;j<tmp.length;j++){
                sb.append("\t").append("\t\"").append(header[j]).append("\":\"").append(tmp[j]).append("\"");
                if(j<tmp.length-1){
                    sb.append(",\n");
                }else{
                    sb.append("\n");
                }
            }
            if(i<csvValues.length-1){
                sb.append("\t},\n");
            }
            else{
                sb.append("\t}\n");
            }
        }

        sb.append("]");

        return sb.toString();
    }

    public static  String csvTojson(String content){
        return csvTojson(content,",");
    }

}
