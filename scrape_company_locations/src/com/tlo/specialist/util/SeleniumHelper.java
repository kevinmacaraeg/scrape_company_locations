package com.tlo.specialist.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

public class SeleniumHelper {
	
	public static List<String> getDropdownTextOptions(WebElement dropdownElement) throws Exception {
		List<String> dropdownTextOptions = null;
		try {
			dropdownTextOptions = new ArrayList<String>();
			
			Select dropdown = new Select(dropdownElement);
			
			List<WebElement> dropdownOptionElements =  dropdown.getOptions();
			for (WebElement dropdownOptionElement : dropdownOptionElements) {
				dropdownTextOptions.add(dropdownOptionElement.getText());
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
		return dropdownTextOptions;
	}
	
	public static Map<String, String> getDropdownTextValueOptions(WebElement dropdownElement) throws Exception {
		Map<String, String> visibleTextValueMap = null;
		try {
			visibleTextValueMap = new HashMap<String, String>();
			
			Select dropdown = new Select(dropdownElement);
			
			List<WebElement> dropdownOptionElements =  dropdown.getOptions();
			for (WebElement dropdownOptionElement : dropdownOptionElements) {
				String value = dropdownOptionElement.getAttribute(Constants.HTML_ELEMENT_ATTR_VALUE);
				String visibleText = dropdownOptionElement.getText();
				
				if (!visibleTextValueMap.containsKey(visibleText)) {
					visibleTextValueMap.put(visibleText, value);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
		return visibleTextValueMap;
	}

}
