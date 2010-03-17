/**
 * Copyright: Copyright 2010 Topic Maps Lab, University of Leipzig. http://www.topicmapslab.de/    
 * License:   Apache License, Version 2.0 http://www.apache.org/licenses/LICENSE-2.0.html
 * 
 * @author Christian Haß
 */
package de.topicmapslab.tmclvalidator.test.tmapi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;

import org.junit.Test;
import org.tmapi.core.TopicMapSystem;
import org.tmapi.core.TopicMapSystemFactory;
import org.tmapix.io.XTM20TopicMapReader;

public class ToyTest extends AbstractTest {

	private static String testMapFile_01 = "./src/test/resources/toy-test/toyTM.xtm";
	private static String testSchemaFile_01 = "./src/test/resources/toy-test/toytm_schema.xtm";
	
	private static String testMapFile_02 = "./src/test/resources/toy-test/toyTM.xtm";
	private static String testSchemaFile_02 = "./src/test/resources/toy-test/toytm_schema_with_schema_infos.xtm";
	
	@Test
    public final void testCase01() throws Exception {

		TopicMapSystemFactory factory = TopicMapSystemFactory.newInstance();
		TopicMapSystem system = factory.newTopicMapSystem();

		testMap = system.createTopicMap("test:test");
		assertNotNull(testMap);
		testSchema = system.createTopicMap("test:schema");
		assertNotNull(testSchema);
		
		XTM20TopicMapReader reader = new XTM20TopicMapReader(testMap, new File(testMapFile_01));
		reader.read();
		
		reader = new XTM20TopicMapReader(testSchema, new File(testSchemaFile_01));
		reader.read();
				
		this.results = runValidator();
		
		assertEquals(0, this.results.size());
		
				
    }
	
	@Test
    public final void testCase02() throws Exception {

		TopicMapSystemFactory factory = TopicMapSystemFactory.newInstance();
		TopicMapSystem system = factory.newTopicMapSystem();

		testMap = system.createTopicMap("test:test");
		assertNotNull(testMap);
		testSchema = system.createTopicMap("test:schema");
		assertNotNull(testSchema);
		
		XTM20TopicMapReader reader = new XTM20TopicMapReader(testMap, new File(testMapFile_02));
		reader.read();
		
		reader = new XTM20TopicMapReader(testSchema, new File(testSchemaFile_02));
		reader.read();
				
		this.results = runValidator();
		
		assertEquals(0, this.results.size());
		
				
    }
	
}
