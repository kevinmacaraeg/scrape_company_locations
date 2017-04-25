package com.tlo.specialist.util;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

public class FileHelper {

	public static File constructFile(String filePath, String fileName) throws Exception {
		File file = null;
		try {
			
			file = new File(StringHelper.addSlashAtEnd(filePath) + fileName); 

		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
		return file;
	}
	
	public static void closeInputStream(InputStream inputStream) throws Exception {
		try {
			if (inputStream != null) {
				inputStream.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}
	
	public static void closeOutputStream(OutputStream outputStream) throws Exception {
		try {
			if (outputStream != null) {
				outputStream.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}
}
