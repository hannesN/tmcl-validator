/**
 * 
 */
package de.topicmapslab.tmcl_loader;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.tmapi.core.TopicMap;
import org.tmapix.io.CTMTopicMapReader;

/**
 * @author Hannes Niederhausen
 * 
 */
public class TMCLLoader {

	public static void readTMCLSchema(TopicMap topicMap, String filename) throws Exception {
		readTMCLSchema(topicMap, new File(filename));
	}

	public static void readTMCLSchema(TopicMap topicMap, File file) throws Exception {
		readTMCLSchema(topicMap, new FileInputStream(file));
	}

	public static void readTMCLSchema(TopicMap topicMap, InputStream is) throws Exception {
		InputStream templateStream = TMCLLoader.class.getClassLoader().getResourceAsStream("META-INF/templates.ctm");

		StringBuffer template_prefixes = new StringBuffer();
		StringBuffer template = new StringBuffer();
		StringBuffer prefixes = new StringBuffer();
		StringBuffer rest = new StringBuffer();

		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is) );
		
		// split input stream into CTM prefixes and rest
		
		while (bufferedReader.ready()) {
			String line = bufferedReader.readLine();
			if (isCTMPrefix(line)) {
				prefixes.append(line);
				prefixes.append("\n");
			}else{
				
//				if(!line.startsWith("#"))
//				{
					rest.append(line);
					rest.append("\n");
//				}
			}
		}
		
		// get template content without CTM prefix lines
		
		bufferedReader = new BufferedReader(new InputStreamReader(templateStream));
		while (bufferedReader.ready()) {
			String line = bufferedReader.readLine();
			
//			template.append(line);
//			template.append("\n");
			
			if (isCTMPrefix(line))
			{
				template_prefixes.append(line);
				template_prefixes.append("\n");
			}else{
				
				if(!line.startsWith("#"))
				{
					template.append(line);
					template.append("\n");
				}
			}
		}
		
		// rebuild file content
		
		StringBuffer full = new StringBuffer();
		
		full.append(prefixes);
		full.append(template_prefixes);
		full.append(template);
		full.append(rest);
		
		//System.out.println(full);
		
		CTMTopicMapReader reader = new CTMTopicMapReader(topicMap, new ByteArrayInputStream(full.toString().getBytes()), "http://tmclloader.topicmapslab.de/");
		reader.read();
		
	}
	
	
	private static boolean isCTMPrefix(String line) {
		return ( (line.startsWith("%prefix")) 
				|| (line.startsWith("%encoding"))
				|| (line.startsWith("%include"))
				|| (line.startsWith("%version")) );
	}
}
