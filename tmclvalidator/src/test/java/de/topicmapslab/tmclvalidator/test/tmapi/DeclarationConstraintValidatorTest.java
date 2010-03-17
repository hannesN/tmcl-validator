/**
 * Copyright: Copyright 2010 Topic Maps Lab, University of Leipzig. http://www.topicmapslab.de/    
 * License:   Apache License, Version 2.0 http://www.apache.org/licenses/LICENSE-2.0.html
 * 
 * @author Christian Haß
 */
package de.topicmapslab.tmclvalidator.test.tmapi;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class DeclarationConstraintValidatorTest extends AbstractTest {

	private static String testMapFile_01 = "./src/test/resources/declaration-constraint/test_case_01_map.ctm";
	private static String testSchemaFile_01 = "./src/test/resources/declaration-constraint/test_case_01_schema.ctm";
	
	private static String testMapFile_02 = "./src/test/resources/declaration-constraint/test_case_02_map.ctm";
	private static String testSchemaFile_02 = "./src/test/resources/declaration-constraint/test_case_02_schema.ctm";
	
	private static String testMapFile_03 = "./src/test/resources/declaration-constraint/test_case_03_map.ctm";
	private static String testSchemaFile_03 = "./src/test/resources/declaration-constraint/test_case_03_schema.ctm";
	
	private static String testMapFile_04 = "./src/test/resources/declaration-constraint/test_case_04_map.ctm";
	private static String testSchemaFile_04 = "./src/test/resources/declaration-constraint/test_case_04_schema.ctm";
	
	private static String testMapFile_05 = "./src/test/resources/declaration-constraint/test_case_05_map.ctm";
	private static String testSchemaFile_05 = "./src/test/resources/declaration-constraint/test_case_05_schema.ctm";
	
	private static String testMapFile_06 = "./src/test/resources/declaration-constraint/test_case_06_map.ctm";
	private static String testSchemaFile_06 = "./src/test/resources/declaration-constraint/test_case_06_schema.ctm";
	
	private static String testMapFile_07 = "./src/test/resources/declaration-constraint/test_case_07_map.ctm";
	private static String testSchemaFile_07 = "./src/test/resources/declaration-constraint/test_case_07_schema.ctm";

	@Test
    public final void testCase01() throws Exception {

		this.results = runValidator(testMapFile_01, testSchemaFile_01);
		assertEquals(0, this.results.size());
	}

	@Test // type no tmcl:topic-type
    public final void testCase02() throws Exception {

		checkForInvalidTopic(testMapFile_02, testSchemaFile_02, "http://tmclvalidator.topicmapslab.de/type_2");
    }
	
	@Test // name no tmcl:name-type
	public final void testCase03() throws Exception {

		checkForInvalidTopic(testMapFile_03, testSchemaFile_03, "http://tmclvalidator.topicmapslab.de/name_2");
    }
	
	@Test // occurrence no tmcl:occurrence-type
	public final void testCase04() throws Exception {

		checkForInvalidTopic(testMapFile_04, testSchemaFile_04, "http://tmclvalidator.topicmapslab.de/occurrence_2");
    }
	
	@Test // association no tmcl:association-type
	public final void testCase05() throws Exception {

		checkForInvalidTopic(testMapFile_05, testSchemaFile_05, "http://tmclvalidator.topicmapslab.de/association_2");
    }
	
	@Test // role no tmcl:role-type
	public final void testCase06() throws Exception {

		checkForInvalidTopic(testMapFile_06, testSchemaFile_06, "http://tmclvalidator.topicmapslab.de/role_2");
    }
	
	@Test // all used constructs not predefined in schema
	public final void testCase07() throws Exception {

		this.results = runValidator(testMapFile_07, testSchemaFile_07);
		assertEquals(5, this.results.size());
    }
	
	
	
	
	
}
