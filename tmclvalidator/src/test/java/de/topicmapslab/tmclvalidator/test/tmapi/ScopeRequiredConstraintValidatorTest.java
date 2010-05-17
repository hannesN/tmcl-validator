/**
 * Copyright: Copyright 2010 Topic Maps Lab, University of Leipzig. http://www.topicmapslab.de/    
 * License:   Apache License, Version 2.0 http://www.apache.org/licenses/LICENSE-2.0.html
 * 
 * @author Christian Haß
 */
package de.topicmapslab.tmclvalidator.test.tmapi;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ScopeRequiredConstraintValidatorTest extends AbstractTest {

	private static String testMapFile_01 = "./src/test/resources/scope-required-constraint/test_case_01_map.ctm";
	private static String testSchemaFile_01 = "./src/test/resources/scope-required-constraint/test_case_01_schema.ctm";
	
	private static String testMapFile_02 = "./src/test/resources/scope-required-constraint/test_case_02_map.ctm";
	private static String testSchemaFile_02 = "./src/test/resources/scope-required-constraint/test_case_02_schema.ctm";
	
	private static String testMapFile_03 = "./src/test/resources/scope-required-constraint/test_case_03_map.ctm";
	private static String testSchemaFile_03 = "./src/test/resources/scope-required-constraint/test_case_03_schema.ctm";
	
	private static String testMapFile_04 = "./src/test/resources/scope-required-constraint/test_case_04_map.ctm";
	private static String testSchemaFile_04 = "./src/test/resources/scope-required-constraint/test_case_04_schema.ctm";
	
	private static String testMapFile_05 = "./src/test/resources/scope-required-constraint/test_case_05_map.ctm";
	private static String testSchemaFile_05 = "./src/test/resources/scope-required-constraint/test_case_05_schema.ctm";
	
	private static String testMapFile_06 = "./src/test/resources/scope-required-constraint/test_case_06_map.ctm";
	private static String testSchemaFile_06 = "./src/test/resources/scope-required-constraint/test_case_06_schema.ctm";
	
	private static String testMapFile_07 = "./src/test/resources/scope-required-constraint/test_case_07_map.ctm";
	private static String testSchemaFile_07 = "./src/test/resources/scope-required-constraint/test_case_07_schema.ctm";
	
	private static String testMapFile_08 = "./src/test/resources/scope-required-constraint/test_case_08_map.ctm";
	private static String testSchemaFile_08 = "./src/test/resources/scope-required-constraint/test_case_08_schema.ctm";
	
	private static String testMapFile_09 = "./src/test/resources/scope-required-constraint/test_case_09_map.ctm";
	private static String testSchemaFile_09 = "./src/test/resources/scope-required-constraint/test_case_09_schema.ctm";
	
	@Test
    public final void testCase01() throws Exception {

		this.results = runValidator(testMapFile_01, testSchemaFile_01);
		assertEquals(0, this.results.size());
    }
	
	@Test // has additional name and occurrence without scope
    public final void testCase02() throws Exception {

		this.results = runValidator(testMapFile_02, testSchemaFile_02);
		assertEquals(0, this.results.size());
    }
	
	@Test // name cardinallity to high  
	public final void testCase03() throws Exception {

		checkForInvalidTopic(testMapFile_03, testSchemaFile_03, "http://tmclvalidator.topicmapslab.de/instance_1");
    }
	
	
	@Test // name cardinallity to low
	public final void testCase04() throws Exception {

		checkForInvalidTopic(testMapFile_04, testSchemaFile_04, "http://tmclvalidator.topicmapslab.de/instance_1");
    }
	
	@Test // occurrence cardinality to high
	public final void testCase05() throws Exception {

		checkForInvalidTopic(testMapFile_05, testSchemaFile_05, "http://tmclvalidator.topicmapslab.de/instance_1");
    }
	
	@Test // occurrence cardinality to low
	public final void testCase06() throws Exception {

		checkForInvalidTopic(testMapFile_06, testSchemaFile_06, "http://tmclvalidator.topicmapslab.de/instance_1");
    }
	
	@Test
	public final void testCase07() throws Exception {

		checkForInvalidTopic(testMapFile_07, testSchemaFile_07, "http://tmclvalidator.topicmapslab.de/instance_1");
    }
	
	@Test // to many themes in occurrence scope
	public final void testCase08() throws Exception {

		checkForInvalidTopic(testMapFile_08, testSchemaFile_08, "http://tmclvalidator.topicmapslab.de/instance_1");
    }
	
	@Test // instance of multiple types
    public final void testCase09() throws Exception {

		this.results = runValidator(testMapFile_09, testSchemaFile_09);
		assertEquals(0, this.results.size());
    }
	
}
