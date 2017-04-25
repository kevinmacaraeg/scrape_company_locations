package com.tlo.specialist.util;

import java.io.IOException;
import java.net.SocketTimeoutException;

import javax.net.ssl.SSLHandshakeException;

import org.apache.log4j.Logger;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
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
}
