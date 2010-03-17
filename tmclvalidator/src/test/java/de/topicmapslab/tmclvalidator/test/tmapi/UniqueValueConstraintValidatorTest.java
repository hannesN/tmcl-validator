/**
 * Copyright: Copyright 2010 Topic Maps Lab, University of Leipzig. http://www.topicmapslab.de/    
 * License:   Apache License, Version 2.0 http://www.apache.org/licenses/LICENSE-2.0.html
 * 
 * @author Christian Haß
 */
package de.topicmapslab.tmclvalidator.test.tmapi;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class UniqueValueConstraintValidatorTest extends AbstractTest {

	private static String testMapFile_01 = "./src/test/resources/unique-value-constraint/test_case_01_map.ctm";
	private static String testSchemaFile_01 = "./src/test/resources/unique-value-constraint/test_case_01_schema.ctm";
	
	private static String testMapFile_02 = "./src/test/resources/unique-value-constraint/test_case_02_map.ctm";
	private static String testSchemaFile_02 = "./src/test/resources/unique-value-constraint/test_case_02_schema.ctm";
	
	private static String testMapFile_03 = "./src/test/resources/unique-value-constraint/test_case_03_map.ctm";
	private static String testSchemaFile_03 = "./src/test/resources/unique-value-constraint/test_case_03_schema.ctm";
	
	private static String testMapFile_04 = "./src/test/resources/unique-value-constraint/test_case_04_map.ctm";
	private static String testSchemaFile_04 = "./src/test/resources/unique-value-constraint/test_case_04_schema.ctm";
	
	private static String testMapFile_05 = "./src/test/resources/unique-value-constraint/test_case_05_map.ctm";
	private static String testSchemaFile_05 = "./src/test/resources/unique-value-constraint/test_case_05_schema.ctm";

	@Test
    public final void testCase01() throws Exception {

		this.results = runValidator(testMapFile_01, testSchemaFile_01);
		assertEquals(0, this.results.size());
    }
	
	@Test // names not unique
	public final void testCase02() throws Exception {

		checkNumberAndType(testMapFile_02, testSchemaFile_02, 2, "http://tmclvalidator.topicmapslab.de/name_type_1");
    }
	
	@Test // no name of that type
	public final void testCase03() throws Exception {

		this.results = runValidator(testMapFile_03, testSchemaFile_03);
		assertEquals(0, this.results.size());
	}
	
	@Test // occurrences not unique
	public final void testCase04() throws Exception {

		checkNumberAndType(testMapFile_04, testSchemaFile_04, 2, "http://tmclvalidator.topicmapslab.de/occurrence_type_1");
    }
	
	@Test // no occurrences of that type
	public final void testCase05() throws Exception {

		this.results = runValidator(testMapFile_05, testSchemaFile_05);
		assertEquals(0, this.results.size());
	}

}