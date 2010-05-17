/**
 * Copyright: Copyright 2010 Topic Maps Lab, University of Leipzig. http://www.topicmapslab.de/    
 * License:   Apache License, Version 2.0 http://www.apache.org/licenses/LICENSE-2.0.html
 * 
 * @author Christian Haß
 */
package de.topicmapslab.tmclvalidator.test.tmapi;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TopicNameConstraintValidatorTest extends AbstractTest {

	private static String testMapFile_01 = "./src/test/resources/topic-name-constraint/test_case_01_map.ctm";
	private static String testSchemaFile_01 = "./src/test/resources/topic-name-constraint/test_case_01_schema.ctm";
	
	private static String testMapFile_02 = "./src/test/resources/topic-name-constraint/test_case_02_map.ctm";
	private static String testSchemaFile_02 = "./src/test/resources/topic-name-constraint/test_case_02_schema.ctm";
	
	private static String testMapFile_03 = "./src/test/resources/topic-name-constraint/test_case_03_map.ctm";
	private static String testSchemaFile_03 = "./src/test/resources/topic-name-constraint/test_case_03_schema.ctm";
	
	private static String testMapFile_04 = "./src/test/resources/topic-name-constraint/test_case_04_map.ctm";
	private static String testSchemaFile_04 = "./src/test/resources/topic-name-constraint/test_case_04_schema.ctm";
	
	@Test
    public final void testCase01() throws Exception {

		this.results = runValidator(testMapFile_01, testSchemaFile_01);
		assertEquals(0, this.results.size());
    }
	
	@Test // to few names
    public final void testCase02() throws Exception {

		checkForInvalidTopic(testMapFile_02, testSchemaFile_02, "http://tmclvalidator.topicmapslab.de/instance_1");
    }
	
	@Test // to many names
    public final void testCase03() throws Exception {

		checkForInvalidTopic(testMapFile_03, testSchemaFile_03, "http://tmclvalidator.topicmapslab.de/instance_1");
    }
	
	@Test
    public final void testCase04() throws Exception {

		this.results = runValidator(testMapFile_04, testSchemaFile_04);
		assertEquals(0, this.results.size());
    }
	
}
