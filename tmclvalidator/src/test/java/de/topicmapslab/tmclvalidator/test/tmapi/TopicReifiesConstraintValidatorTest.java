/**
 * Copyright: Copyright 2010 Topic Maps Lab, University of Leipzig. http://www.topicmapslab.de/    
 * License:   Apache License, Version 2.0 http://www.apache.org/licenses/LICENSE-2.0.html
 * 
 * @author Christian Haß
 */
package de.topicmapslab.tmclvalidator.test.tmapi;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TopicReifiesConstraintValidatorTest extends AbstractTest {

	private static String testMapFile_01 = "./src/test/resources/topic-reifies-constraint/test_case_01_map.ctm";
	private static String testSchemaFile_01 = "./src/test/resources/topic-reifies-constraint/test_case_01_schema.ctm";
	
	private static String testMapFile_02 = "./src/test/resources/topic-reifies-constraint/test_case_02_map.ctm";
	private static String testSchemaFile_02 = "./src/test/resources/topic-reifies-constraint/test_case_02_schema.ctm";
	
	private static String testMapFile_03 = "./src/test/resources/topic-reifies-constraint/test_case_03_map.ctm";
	private static String testSchemaFile_03 = "./src/test/resources/topic-reifies-constraint/test_case_03_schema.ctm";
	
	private static String testMapFile_04 = "./src/test/resources/topic-reifies-constraint/test_case_04_map.ctm";
	private static String testSchemaFile_04 = "./src/test/resources/topic-reifies-constraint/test_case_04_schema.ctm";
	
	private static String testMapFile_05 = "./src/test/resources/topic-reifies-constraint/test_case_05_map.ctm";
	private static String testSchemaFile_05 = "./src/test/resources/topic-reifies-constraint/test_case_05_schema.ctm";
	
	private static String testMapFile_06 = "./src/test/resources/topic-reifies-constraint/test_case_06_map.ctm";
	private static String testSchemaFile_06 = "./src/test/resources/topic-reifies-constraint/test_case_06_schema.ctm";
	
	private static String testMapFile_07 = "./src/test/resources/topic-reifies-constraint/test_case_07_map.ctm";
	private static String testSchemaFile_07 = "./src/test/resources/topic-reifies-constraint/test_case_07_schema.ctm";
	
	private static String testMapFile_08 = "./src/test/resources/topic-reifies-constraint/test_case_08_map.ctm";
	private static String testSchemaFile_08 = "./src/test/resources/topic-reifies-constraint/test_case_08_schema.ctm";
	
	private static String testMapFile_09 = "./src/test/resources/topic-reifies-constraint/test_case_09_map.ctm";
	private static String testSchemaFile_09 = "./src/test/resources/topic-reifies-constraint/test_case_09_schema.ctm";
	
	private static String testMapFile_10 = "./src/test/resources/topic-reifies-constraint/test_case_10_map.ctm";
	private static String testSchemaFile_10 = "./src/test/resources/topic-reifies-constraint/test_case_10_schema.ctm";
	
	private static String testMapFile_11 = "./src/test/resources/topic-reifies-constraint/test_case_11_map.ctm";
	private static String testSchemaFile_11 = "./src/test/resources/topic-reifies-constraint/test_case_11_schema.ctm";
	
	private static String testMapFile_12 = "./src/test/resources/topic-reifies-constraint/test_case_12_map.ctm";
	private static String testSchemaFile_12 = "./src/test/resources/topic-reifies-constraint/test_case_12_schema.ctm";
	
	private static String testMapFile_13 = "./src/test/resources/topic-reifies-constraint/test_case_13_map.ctm";
	private static String testSchemaFile_13 = "./src/test/resources/topic-reifies-constraint/test_case_13_schema.ctm";
	
	private static String testMapFile_14 = "./src/test/resources/topic-reifies-constraint/test_case_14_map.ctm";
	private static String testSchemaFile_14 = "./src/test/resources/topic-reifies-constraint/test_case_14_schema.ctm";
	
	@Test
    public final void testCase01() throws Exception {

		this.results = runValidator(testMapFile_01, testSchemaFile_01);
		assertEquals(0, this.results.size());
    }

	@Test // may name reifier don't reifies something
    public final void testCase02() throws Exception {

		this.results = runValidator(testMapFile_02, testSchemaFile_02);
		assertEquals(0, this.results.size());
    }

	@Test // may occurrence reifier don't reifies something
    public final void testCase03() throws Exception {

		this.results = runValidator(testMapFile_03, testSchemaFile_03);
		assertEquals(0, this.results.size());
    }

	@Test // may association reifier don't reifies something
    public final void testCase04() throws Exception {

		this.results = runValidator(testMapFile_04, testSchemaFile_04);
		assertEquals(0, this.results.size());
    }

	@Test // must name reifier don't reifies something
    public final void testCase05() throws Exception {

		checkForInvalidTopic(testMapFile_05, testSchemaFile_05, "http://tmclvalidator.topicmapslab.de/name_reifier_1");
    }
	
	@Test // must occurrence reifier don't reifies something
	public final void testCase06() throws Exception {

		checkForInvalidTopic(testMapFile_06, testSchemaFile_06, "http://tmclvalidator.topicmapslab.de/occurrence_reifier_1");
    }
	
	@Test // must association reifier don't reifies something
	public final void testCase07() throws Exception {

		checkForInvalidTopic(testMapFile_07, testSchemaFile_07, "http://tmclvalidator.topicmapslab.de/association_reifier_1");
    }

	@Test // cannot name reifier reifies something
	public final void testCase08() throws Exception {

		checkForInvalidTopic(testMapFile_08, testSchemaFile_08, "http://tmclvalidator.topicmapslab.de/name_reifier_3");
    }

	@Test // cannot occurrence reifier reifies something
	public final void testCase09() throws Exception {

		checkForInvalidTopic(testMapFile_09, testSchemaFile_09, "http://tmclvalidator.topicmapslab.de/occurrence_reifier_3");
    }

	@Test // cannot association reifier reifies something
	public final void testCase10() throws Exception {

		checkForInvalidTopic(testMapFile_10, testSchemaFile_10, "http://tmclvalidator.topicmapslab.de/association_reifier_3");
    }

	@Test // may name reifier reifies wrong type
	public final void testCase11() throws Exception {

		checkForInvalidTopic(testMapFile_11, testSchemaFile_11, "http://tmclvalidator.topicmapslab.de/name_reifier_2");
    }
	
	@Test // may occurrence reifier reifies wrong type
	public final void testCase12() throws Exception {

		checkForInvalidTopic(testMapFile_12, testSchemaFile_12, "http://tmclvalidator.topicmapslab.de/occurrence_reifier_2");
    }
	
	@Test // may association reifier reifies wrong type
	public final void testCase13() throws Exception {

		checkForInvalidTopic(testMapFile_13, testSchemaFile_13, "http://tmclvalidator.topicmapslab.de/association_reifier_2");
    }
	
	@Test
    public final void testCase14() throws Exception {

		this.results = runValidator(testMapFile_14, testSchemaFile_14);
		assertEquals(0, this.results.size());
    }
	
}
