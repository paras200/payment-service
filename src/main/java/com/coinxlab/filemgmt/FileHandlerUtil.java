package com.coinxlab.filemgmt;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class FileHandlerUtil {
	
	private static Log log = LogFactory.getLog(FileHandlerUtil.class.getName());

	public void writeFile(String text, String filename) throws IOException {
		log.info("write file " + filename);
		FileWriter fw = new FileWriter(new File(filename));
		fw.write(text);
		fw.close();
		log.info("write file complete for file " + filename);
	}
}
