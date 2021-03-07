/**
 * 对公众平台发送给公众账号的消息加解密示例代码.
 * 
 * @copyright Copyright (c) 1998-2014 Tencent Inc.
 */

// ------------------------------------------------------------------------

package com.wechat.util;

import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * XMLParse class
 *
 * 提供提取消息格式中的密文及生成回复消息格式的接口.
 */
public class XMLParse {

	/**
	 * 提取出xml数据包中的加密消息
	 * @param xmltext 待提取的xml字符串
	 * @return 提取出的加密消息字符串
	 * @throws AesException 
	 */
	public static Object[] extract(String xmltext) throws AesException     {
		Object[] result = new Object[3];
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
			dbf.setFeature("http://xml.org/sax/features/external-general-entities", false);
			dbf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
			dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
			dbf.setXIncludeAware(false);
			dbf.setExpandEntityReferences(false);
			DocumentBuilder db = dbf.newDocumentBuilder();
			StringReader sr = new StringReader(xmltext);
			InputSource is = new InputSource(sr);
			Document document = db.parse(is);

			Element root = document.getDocumentElement();
			NodeList nodelist1 = root.getElementsByTagName("Encrypt");
			NodeList nodelist2 = root.getElementsByTagName("ToUserName");
			result[0] = 0;
			result[1] = nodelist1.item(0).getTextContent();
			result[2] = nodelist2.item(0).getTextContent();
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			throw new AesException(AesException.ParseXmlError);
		}
	}

	/**
	 * 提取出xml数据包中的消息
	 * @param xmltext 待提取的xml字符串
	 * @return 提取出的加密消息字符串
	 * @throws AesException
	 */
	public static Element extractTOXMLElement(String xmltext) throws AesException     {
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
			dbf.setFeature("http://xml.org/sax/features/external-general-entities", false);
			dbf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
			dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
			dbf.setXIncludeAware(false);
			dbf.setExpandEntityReferences(false);
			DocumentBuilder db = dbf.newDocumentBuilder();
			StringReader sr = new StringReader(xmltext);
			InputSource is = new InputSource(sr);
			Document document = db.parse(is);
			Element root = document.getDocumentElement();
//			NodeList nodelist1 = root.getElementsByTagName("Encrypt");
//			NodeList nodelist2 = root.getElementsByTagName("ToUserName");
//			result[0] = 0;
//			result[1] = nodelist1.item(0).getTextContent();
//			result[2] = nodelist2.item(0).getTextContent();
			return root;
		} catch (Exception e) {
			e.printStackTrace();
			throw new AesException(AesException.ParseXmlError);
		}
	}

	/**
	 * 生成xml消息
	 * @param encrypt 加密后的消息密文
	 * @param signature 安全签名
	 * @param timestamp 时间戳
	 * @param nonce 随机字符串
	 * @return 生成的xml字符串
	 */
	public static String generate(String encrypt, String signature, String timestamp, String nonce) {

		String format = "<xml>\n" + "<Encrypt><![CDATA[%1$s]]></Encrypt>\n"
				+ "<MsgSignature><![CDATA[%2$s]]></MsgSignature>\n"
				+ "<TimeStamp>%3$s</TimeStamp>\n" + "<Nonce><![CDATA[%4$s]]></Nonce>\n" + "</xml>";
		return String.format(format, encrypt, signature, timestamp, nonce);

	}


	/**
	 * /**
	 * 	 * 生成xml文本类消息
	 * @param toUserName 开发者微信号
	 * @param fromUserName 发送方帐号（一个OpenID）
	 * createTime 消息创建时间 （整型）
	 * @param msgType 消息类型，文本为text
	 * @param content 消息类型，文本为text
	 * @param msgId 消息id，64位整型
	 * @return 生成的xml字符串
	 */
	public static String generateTextMessage(String toUserName, String fromUserName, String msgType,String content,String msgId) {

		String format = "<xml>\n" + "<ToUserName><![CDATA[%1$s]]></ToUserName>\n"
				+ "<FromUserName><![CDATA[%2$s]]></FromUserName>\n"
				+ "<CreateTime>%3$s</CreateTime>\n"
				+ "<MsgType><![CDATA[%4$s]]></MsgType>\n"
				+ "<Content><![CDATA[%5$s]]></Content>\n"
				+ "<MsgId><![CDATA[%6$s]]></MsgId>\n"
				+ "</xml>";
		return String.format(format, toUserName, fromUserName, System.currentTimeMillis(), msgType,content,msgId);
	}

	/**
	 *
	 * @param toUserName
	 * @param fromUserName
	 * @param msgType
	 * @param event
	 * @return
	 */
	public static String generateEventMessage(String toUserName, String fromUserName, String msgType,String event) {

		String format = "<xml>\n" + "<ToUserName><![CDATA[%1$s]]></ToUserName>\n"
				+ "<FromUserName><![CDATA[%2$s]]></FromUserName>\n"
				+ "<CreateTime>%3$s</CreateTime>\n"
				+ "<MsgType><![CDATA[%4$s]]></MsgType>\n"
				+ "<Event><![CDATA[%5$s]]></Event>\n"
				+ "</xml>";
		return String.format(format, toUserName, fromUserName, System.currentTimeMillis(), msgType,event);
	}
}
