package com.dougfii.android.core.update;

import com.dougfii.android.core.utils.Utils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by momo on 15/10/31.
 */
public class UpdateXmlService {
    private UpdateVersion version = new UpdateVersion();

    public UpdateVersion parse(InputStream stream) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance(); // 实例化一个文档构建器工厂
            DocumentBuilder builder = factory.newDocumentBuilder(); // 通过文档构建器工厂获取一个文档构建器
            Document document = builder.parse(stream);// 通过文档通过文档构建器构建一个文档实例
            Element root = document.getDocumentElement();// 获取XML文件根节点
            NodeList nodes = root.getChildNodes(); // 获得所有子节点

            for (int j = 0; j < nodes.getLength(); j++) {
                // 遍历子节点
                Node node = nodes.item(j);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element childElement = (Element) node;

                    if ("code".equals(childElement.getNodeName())) //版本号
                    {
                        version.setCode(Utils.toInteger(childElement.getFirstChild().getNodeValue()));
                    } else if ("version".equals(childElement.getNodeName())) //版本名称
                    {
                        version.setVersion(childElement.getFirstChild().getNodeValue());
                    } else if (("name".equals(childElement.getNodeName()))) //软件名称
                    {
                        version.setName(childElement.getFirstChild().getNodeValue());
                    } else if (("url".equals(childElement.getNodeName()))) //下载地址
                    {
                        version.setUrl(childElement.getFirstChild().getNodeValue());
                    } else if (("description".equals(childElement.getNodeName()))) //描述信息
                    {
                        version.setDescription(childElement.getFirstChild().getNodeValue());
                    }
                }
            }
        } catch (Exception e) {
            //
        }

        return version;
    }
}