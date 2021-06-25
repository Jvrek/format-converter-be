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
        sb.append("{\"").append(root.getNodeName()).append("\":");
        root.normalize();
        if(root.hasChildNodes()){
            int depth = 0;
            sb.append("{");
            getNode(root.getChildNodes(), depth, sb);
            sb.append("}");
        }
        sb.append("}");

        return sb.toString();
    }

    @SneakyThrows
    private static void getNode(NodeList nodeList, int depth, StringBuilder sb) {
        depth++;
        boolean open = false, endOfArray = false, isArray = false;
        if(depth==2 && nodeList.getLength()>2){
            sb.append("{");
            open = true;
        }
        int sizeOfArray = 0;
        for (int count = 0; count < nodeList.getLength(); count++) {
            Node tempNode = nodeList.item(count);
            if(tempNode.getNodeType()==1){
                String myName = "";
                myName = tempNode.getNodeName();
                if(count+2<nodeList.getLength()){
                    Node nextNode = nodeList.item(count+2);
                    String nextOne = nextNode.getNodeName();
                    if(myName.equalsIgnoreCase(nextOne)){
                        sizeOfArray++;
                        if(!isArray){
                            sb.append("\"").append(tempNode.getNodeName()).append("\":[");
                        }
                        isArray = true;
                    }
                    else if(sizeOfArray>0){
                        endOfArray = true;
                    }
                }
                else if(sizeOfArray>0){
                    endOfArray = true;
                }

                if (tempNode.hasAttributes()) {
                    open = handleNodeWithAttributes(depth, isArray, tempNode, sb);
                }

                else{
                    if(!isArray){
                        sb.append("\"").append(tempNode.getNodeName()).append("\"");
                    }

                    if(tempNode.hasChildNodes()){
                        try {
                            if (!tempNode.getNextSibling().getNextSibling().getNodeName().equals(tempNode.getNodeName())
                                    && !tempNode.getPreviousSibling().getPreviousSibling().getNodeName().equals(tempNode.getNodeName())) {
                                sb.append(":");
                            }
                        }catch (Exception e){
                            sb.append(":");
                        }
                        getNode(tempNode.getChildNodes(), depth, sb);
                    }
                }
            }

            if(tempNode.getNodeName().contains("#")){
                if(!tempNode.getTextContent().trim().isEmpty()){
                    sb.append("\"").append(tempNode.getTextContent()).append("\"");
                    if(tempNode.getParentNode().hasAttributes()){
                        sb.append("}");
                    }
                }
                continue;
            }

            if(endOfArray){
                sb.append("]");
                endOfArray = false;
                isArray = false;
                sizeOfArray = 0;
            }

            if(count<nodeList.getLength()-2){
                sb.append(",");
            }
            else if(count==nodeList.getLength()-2 && open){
                sb.append("}");
            }
        }
    }

    @SneakyThrows
    private static boolean handleNodeWithAttributes(int depth, boolean isArray, Node tempNode, StringBuilder sb) {
        NamedNodeMap nodeMap = tempNode.getAttributes();

        if(!isArray){
            sb.append("\"").append(tempNode.getNodeName()).append("\"");
        }
        System.out.println("{");
        for (int i = 0; i < nodeMap.getLength(); i++) {
            Node node = nodeMap.item(i);
            sb.append("\"").append(node.getNodeName()).append("\"");
            sb.append(":\"").append(node.getNodeValue()).append("\",");
        }
        sb.append("\""+"text"+"\"");
        if(tempNode.hasChildNodes()){
            sb.append(":");
        }
        getNode(tempNode.getChildNodes(), depth,sb);
        return true;
    }
}
