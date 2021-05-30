package com.to.formatconverterbe.converters;

import lombok.SneakyThrows;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

public class XMLtoJSON {

    @SneakyThrows
    public static String convertToJson(File file){

        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        StringBuilder sb = new StringBuilder();
        Document doc = db.parse(file);
        Element root = doc.getDocumentElement();
        //System.out.println("{\""+root.getNodeName()+"\":");
        sb.append("{\"").append(root.getNodeName()).append("\":");
        root.normalize();
        if(root.hasChildNodes()){
            int depth = 0;
            // System.out.println("{");
            sb.append("{");
            printResult(root.getChildNodes(), depth, sb);
            sb.append("}");
            // System.out.println("}");

        }
        //System.out.println("}");
        sb.append("}");

        System.out.println(sb.toString());
        return sb.toString();
    }


    private static void printResult(NodeList nodeList, int depth, StringBuilder sb) {
        depth++;
        boolean open = false, endOfArray = false, isArray = false;
        if(depth==2 && nodeList.getLength()>2){
            //System.out.println("{");
            sb.append("{");
            open = true;
        }
        int sizeOfArray = 0;
        for (int count = 0; count < nodeList.getLength(); count++) {
            Node tempNode = nodeList.item(count);
            if(tempNode.getNodeType()==1){
                //similar elements - start
                String myName = "";
                myName = tempNode.getNodeName();
                if(count+2<nodeList.getLength()){
                    Node nextNode = nodeList.item(count+2);
                    String nextOne = nextNode.getNodeName();
                    if(myName.equalsIgnoreCase(nextOne)){
                        sizeOfArray++;
                        if(!isArray){
                            //System.out.print("\""+tempNode.getNodeName()+"\":[");
                            sb.append("\"").append(tempNode.getNodeName()).append("\":[");
                        }
                        isArray = true;
                    }
                    else if(sizeOfArray>0){//means atleast 2 items are same
                        endOfArray = true;
                    }
                }
                else if(sizeOfArray>0){
                    endOfArray = true;
                }


                if (tempNode.hasAttributes()) {
                    open = handleNodeWithAttributes(depth, isArray, tempNode, sb);
                }

                //else if only element node without attributes
                else{
                    if(!isArray){
                        // System.out.print("\""+tempNode.getNodeName()+"\"");
                        sb.append("\"").append(tempNode.getNodeName()).append("\"");
                    }


                    if(tempNode.hasChildNodes()){

                        if(tempNode.getFirstChild().getNodeValue().isBlank())
                            // System.out.print(":");
                            sb.append(":");
                        printResult(tempNode.getChildNodes(), depth,sb);
                    }
                }
            }

            if(tempNode.getNodeName().contains("#")){
                if(!tempNode.getTextContent().trim().isEmpty()){
                    //System.out.print("\""+tempNode.getTextContent()+"\"");
                    sb.append("\"").append(tempNode.getTextContent()).append("\"");
                    if(tempNode.getParentNode().hasAttributes()){
                        //  System.out.println("}");
                        sb.append("}");
                    }
                }
                continue;
            }

            if(endOfArray){
                // System.out.print("]");
                sb.append("]");
                endOfArray = false;
                isArray = false;
                sizeOfArray = 0;
            }

            if(count<nodeList.getLength()-2){
                // System.out.println(",");
                sb.append(",");
            }
            else if(count==nodeList.getLength()-2 && open){

                //System.out.println("}");
                sb.append("}");
            }
        }
    }

    private static boolean handleNodeWithAttributes(int depth, boolean isArray,
                                                    Node tempNode, StringBuilder sb) {
        // get attributes names and values
        NamedNodeMap nodeMap = tempNode.getAttributes();

        if(!isArray){
            //System.out.println("\""+tempNode.getNodeName()+"\"");
            sb.append("\"").append(tempNode.getNodeName()).append("\"");

        }
        System.out.println("{");
        for (int i = 0; i < nodeMap.getLength(); i++) {
            Node node = nodeMap.item(i);
            // System.out.print("\"" + node.getNodeName()+"\"");
            sb.append("\"").append(node.getNodeName()).append("\"");
            //System.out.println(":\"" + node.getNodeValue()+"\",");
            sb.append(":\"").append(node.getNodeValue()).append("\",");
        }
        //System.out.print("\""+"text"+"\"");
        sb.append("\""+"text"+"\"");
        if(tempNode.hasChildNodes()){
            // System.out.print(":");
            sb.append(":");
        }
        printResult(tempNode.getChildNodes(), depth,sb);
        return true;
    }


}
