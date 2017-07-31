package com.tlo.specialist.util;

import java.util.Properties;

public class PropertiesFileHelper {
	
	private static Properties systemProperties;
	
	public static void setSystemProperties(Properties properties) {
		systemProperties = properties;
	}
	
	public static String getPropertyValue(String property) throws Exception {
		String propertyValue = null;
		try {
			propertyValue = systemProperties.getProperty(property).trim();
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
		return propertyValue;
	}
	
}
