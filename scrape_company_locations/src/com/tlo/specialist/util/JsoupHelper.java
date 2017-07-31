package com.tlo.specialist.util;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.net.ssl.SSLHandshakeException;

import org.apache.log4j.Logger;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

public class JsoupHelper {
	
	private static Logger logger = Logger.getLogger(JsoupHelper.class.getName());

	public static Document getWebsiteDocument(String websiteUrl) throws Exception {
		Document websiteDocument = null;
		try {
			int numberOfTriesToConnect = 0;
			while (numberOfTriesToConnect < 3) {
				try {
					websiteDocument = Jsoup.connect(websiteUrl).userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/47.0.2526.111 Safari/537.36").get();
					break;
				} catch (SocketTimeoutException e) {
					logger.info("Encountered connection time out while attempting to connect to " + websiteUrl + ". Number of attempts :: " + (numberOfTriesToConnect + 1));
				} catch (SSLHandshakeException e) {
					if (numberOfTriesToConnect >= 1) {
						throw new SSLHandshakeException(e.getMessage());
					} else {
						if (websiteUrl.contains("https")) {
							websiteUrl = websiteUrl.replace("https", "http");
						} else if (websiteUrl.contains("http")) {
							websiteUrl = websiteUrl.replace("http", "https");
						}
					}
				}
				numberOfTriesToConnect++;
			}
		} catch (IllegalArgumentException e) {
			logger.info("Encountered " + e.getClass().getName() + " while connecting to " + websiteUrl);
		} catch (HttpStatusException e) {
			logger.info("Encountered " + e.getClass().getName() + " while connecting to " + websiteUrl);
		} catch (IOException e) {
			logger.info("Encountered " + e.getClass().getName() + " while connecting to " + websiteUrl);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
		return websiteDocument;
	}
	
	public static String getAllTextsFromElementsAttribute(Elements elements, String attribute) throws Exception {
		StringBuilder textsFromAttribute = null;
		try {
			textsFromAttribute = new StringBuilder();
			for (Element element : elements) {
				textsFromAttribute.append(element.attr(attribute));
				textsFromAttribute.append(Constants.SPACE);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
		return StringHelper.replaceNullValue(textsFromAttribute.toString(), Constants.EMPTY_STRING);
	}
	
	public static Set<String> getElementsHrefAttributes(Elements elements) throws Exception {
		Set<String> hrefSet = null;
		try {
			hrefSet = new HashSet<String>();
			for (Element element : elements) {
				
				String link = element.attr(Constants.HTML_ELEMENT_ATTR_HREF);
				hrefSet.add(link);
			
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
		return hrefSet;
	}
	
	public static Set<String> getElementsAttributeValue(String attribute, Elements elements) throws Exception {
		Set<String> attributeValueSet = null;
		try {
			
			attributeValueSet = new HashSet<String>();
			for (Element element : elements) {
				
				String attributeValue = element.attr(attribute);
				attributeValueSet.add(attributeValue);
				
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
		return attributeValueSet;
	}
	
	public static List<String> getElementsTextToList(Elements elements) throws Exception {
		List<String> elementsTextList = null;
		try {
			
			elementsTextList = new ArrayList<String>();
			for (Element element : elements) {
				
				String text = element.text();
				elementsTextList.add(text);
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
		return elementsTextList;
	}
	
	public static Set<String> getElementsTextToSet(Elements elements) throws Exception {
		Set<String> elementsTextSet = null;
		try {
			elementsTextSet = new HashSet<String>();
			for (Element element : elements) {
				
				String text = element.text();
				elementsTextSet.add(text);
				
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
		return elementsTextSet;
	}
	
	public static Set<String> getElementsTextToSetBySplittingNodes(Elements elements, String splitNodesBy) throws Exception {
		Set<String> elementsTextSet = null;
		try {
			
			elementsTextSet = new HashSet<String>();
			
			for (Element element : elements) {
				
				String nodeTexts = getElementTextsByNode(element);
				nodeTexts = StringHelper.replaceEachNonBreakingSpaceWithSpace(nodeTexts);
				nodeTexts = nodeTexts.replace(Constants.REGEX_WHITESPACES, Constants.SPACE);
				nodeTexts = nodeTexts.replace(Constants.WINDOWS_NEW_LINE, Constants.SPACE);
				
				String[] nodeTextsSplit = nodeTexts.split(splitNodesBy);
				
				Set<String> elementTexts = new HashSet<String>();
				for (String textPerNode : nodeTextsSplit) {
					
					elementTexts.add(textPerNode.replaceAll(Constants.REGEX_HTML_TAG, Constants.EMPTY_STRING).replace(" and Fax", Constants.EMPTY_STRING).trim());
					
				}
				
				elementsTextSet.addAll(elementTexts);
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
		return elementsTextSet;
	}
	
	public static String getElementTextsByNode(Element element) throws Exception {
		StringBuilder nodeTextsBuilder = null;
		try {
			nodeTextsBuilder = new StringBuilder();
			
			for (Node node : element.childNodes()) {
				
				nodeTextsBuilder.append(node.toString());
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
		return nodeTextsBuilder.toString();
	}
	
}
