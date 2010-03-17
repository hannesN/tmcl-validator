/**
 * Copyright: Copyright 2010 Topic Maps Lab, University of Leipzig. http://www.topicmapslab.de/    
 * License:   Apache License, Version 2.0 http://www.apache.org/licenses/LICENSE-2.0.html
 * 
 * @author Christian Haß
 */
package de.topicmapslab.tmclvalidator.test.tmapi;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ReifierConstraintValidatorTest extends AbstractTest {

	private static String testMapFile_01 = "./src/test/resources/reifier-constraint/test_case_01_map.ctm";
	private static String testSchemaFile_01 = "./src/test/resources/reifier-constraint/test_case_01_schema.ctm";
	
	private static String testMapFile_02 = "./src/test/resources/reifier-constraint/test_case_02_map.ctm";
	private static String testSchemaFile_02 = "./src/test/resources/reifier-constraint/test_case_02_schema.ctm";
	
	private static String testMapFile_03 = "./src/test/resources/reifier-constraint/test_case_03_map.ctm";
	private static String testSchemaFile_03 = "./src/test/resources/reifier-constraint/test_case_03_schema.ctm";
	
	private static String testMapFile_04 = "./src/test/resources/reifier-constraint/test_case_04_map.ctm";
	private static String testSchemaFile_04 = "./src/test/resources/reifier-constraint/test_case_04_schema.ctm";
	
	private static String testMapFile_05 = "./src/test/resources/reifier-constraint/test_case_05_map.ctm";
	private static String testSchemaFile_05 = "./src/test/resources/reifier-constraint/test_case_05_schema.ctm";
	
	private static String testMapFile_06 = "./src/test/resources/reifier-constraint/test_case_06_map.ctm";
	private static String testSchemaFile_06 = "./src/test/resources/reifier-constraint/test_case_06_schema.ctm";
	
	private static String testMapFile_07 = "./src/test/resources/reifier-constraint/test_case_07_map.ctm";
	private static String testSchemaFile_07 = "./src/test/resources/reifier-constraint/test_case_07_schema.ctm";
	
	private static String testMapFile_08 = "./src/test/resources/reifier-constraint/test_case_08_map.ctm";
	private static String testSchemaFile_08 = "./src/test/resources/reifier-constraint/test_case_08_schema.ctm";
	
	private static String testMapFile_09 = "./src/test/resources/reifier-constraint/test_case_09_map.ctm";
	private static String testSchemaFile_09 = "./src/test/resources/reifier-constraint/test_case_09_schema.ctm";
	
	private static String testMapFile_10 = "./src/test/resources/reifier-constraint/test_case_10_map.ctm";
	private static String testSchemaFile_10 = "./src/test/resources/reifier-constraint/test_case_10_schema.ctm";

	//@Test
    public final void testCase01() throws Exception {

		this.results = runValidator(testMapFile_01, testSchemaFile_01);
		assertEquals(0, this.results.size());
    }
	
	//@Test // no must-have-name reifier
    public final void testCase02() throws Exception {

		checkNumberAndType(testMapFile_02, testSchemaFile_02, 1, "http://tmclvalidator.topicmapslab.de/must_have_name");
    }
	
	//@Test // cannot-have-name has an reifier
    public final void testCase03() throws Exception {

		checkNumberAndType(testMapFile_03, testSchemaFile_03, 1, "http://tmclvalidator.topicmapslab.de/cannot_have_name");
    }
	
	//@Test // may-have-name has an unallowed reifier
    public final void testCase04() throws Exception {

		checkNumberAndType(testMapFile_04, testSchemaFile_04, 1, "http://tmclvalidator.topicmapslab.de/may_have_name");
    }
	
	//@Test // no must_have_occurrence reifier
    public final void testCase05() throws Exception {

		checkNumberAndType(testMapFile_05, testSchemaFile_05, 1, "http://tmclvalidator.topicmapslab.de/must_have_occurrence");
    }
	
	//@Test // cannot_have_occurrence has an reifier
    public final void testCase06() throws Exception {

		checkNumberAndType(testMapFile_06, testSchemaFile_06, 1, "http://tmclvalidator.topicmapslab.de/cannot_have_occurrence");
    }
	
	//@Test // may_have_occurrence has an unallowed reifier
    public final void testCase07() throws Exception {

		checkNumberAndType(testMapFile_07, testSchemaFile_07, 1, "http://tmclvalidator.topicmapslab.de/may_have_occurrence");
    }
	
	//@Test // no must_have_association reifier
    public final void testCase08() throws Exception {

		checkNumberAndType(testMapFile_08, testSchemaFile_08, 1, "http://tmclvalidator.topicmapslab.de/must_have_association");
    }
	
	//@Test // cannot_have_association has an reifier
    public final void testCase09() throws Exception {

		checkNumberAndType(testMapFile_09, testSchemaFile_09, 1, "http://tmclvalidator.topicmapslab.de/cannot_have_association");
    }
	
	@Test // may_have_association has an unallowed reifier
    public final void testCase10() throws Exception {

		checkNumberAndType(testMapFile_10, testSchemaFile_10, 1, "http://tmclvalidator.topicmapslab.de/may_have_association");
    }
	
	
}
