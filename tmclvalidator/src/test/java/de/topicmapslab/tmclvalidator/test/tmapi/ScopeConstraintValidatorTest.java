/**
 * Copyright: Copyright 2010 Topic Maps Lab, University of Leipzig. http://www.topicmapslab.de/    
 * License:   Apache License, Version 2.0 http://www.apache.org/licenses/LICENSE-2.0.html
 * 
 * @author Christian Haß
 */
package de.topicmapslab.tmclvalidator.test.tmapi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.tmapi.core.Association;
import org.tmapi.core.Construct;
import org.tmapi.core.Name;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Topic;
import org.tmapi.core.Typed;

public class ScopeConstraintValidatorTest extends AbstractTest {

	private static String testMapFile_01 = "./src/test/resources/scope-constraint/test_case_01_map.ctm";
	private static String testSchemaFile_01 = "./src/test/resources/scope-constraint/test_case_01_schema.ctm";
	
	private static String testMapFile_02 = "./src/test/resources/scope-constraint/test_case_02_map.ctm";
	private static String testSchemaFile_02 = "./src/test/resources/scope-constraint/test_case_02_schema.ctm";
	
	private static String testMapFile_03 = "./src/test/resources/scope-constraint/test_case_03_map.ctm";
	private static String testSchemaFile_03 = "./src/test/resources/scope-constraint/test_case_03_schema.ctm";
	
	private static String testMapFile_04 = "./src/test/resources/scope-constraint/test_case_04_map.ctm";
	private static String testSchemaFile_04 = "./src/test/resources/scope-constraint/test_case_04_schema.ctm";
	
	private static String testMapFile_05 = "./src/test/resources/scope-constraint/test_case_05_map.ctm";
	private static String testSchemaFile_05 = "./src/test/resources/scope-constraint/test_case_05_schema.ctm";
	
	private static String testMapFile_06 = "./src/test/resources/scope-constraint/test_case_06_map.ctm";
	private static String testSchemaFile_06 = "./src/test/resources/scope-constraint/test_case_06_schema.ctm";
	
	private static String testMapFile_07 = "./src/test/resources/scope-constraint/test_case_07_map.ctm";
	private static String testSchemaFile_07 = "./src/test/resources/scope-constraint/test_case_07_schema.ctm";
	
	private static String testMapFile_08 = "./src/test/resources/scope-constraint/test_case_08_map.ctm";
	private static String testSchemaFile_08 = "./src/test/resources/scope-constraint/test_case_08_schema.ctm";

	@Test
    public final void testCase01() throws Exception {

		this.results = runValidator(testMapFile_01, testSchemaFile_01);
		assertEquals(0, this.results.size());
    }
	
	@Test // no name scope
    public final void testCase02() throws Exception {

		checkNumberAndType(testMapFile_02, testSchemaFile_02, 1, "http://tmclvalidator.topicmapslab.de/familie_name");
    }
	
	@Test // wrong name scopes
    public final void testCase03() throws Exception {

		checkNumberAndType(testMapFile_03, testSchemaFile_03, 1, "http://tmclvalidator.topicmapslab.de/familie_name");
    }
	
	@Test // no occurrence scope
    public final void testCase04() throws Exception {

		checkNumberAndType(testMapFile_04, testSchemaFile_04, 1, "http://tmclvalidator.topicmapslab.de/description");
    }
	
	@Test // wrong occurrence scopes
    public final void testCase05() throws Exception {

		checkNumberAndType(testMapFile_05, testSchemaFile_05, 1, "http://tmclvalidator.topicmapslab.de/description");
    }
	
	@Test // no association scope
    public final void testCase06() throws Exception {

		checkNumberAndType(testMapFile_06, testSchemaFile_06, 1, "http://tmclvalidator.topicmapslab.de/lives_in");
    }
	
	@Test // wrong association scopes
    public final void testCase07() throws Exception {

		checkNumberAndType(testMapFile_07, testSchemaFile_07, 1, "http://tmclvalidator.topicmapslab.de/lives_in");
    }
	
	@Test // no name, occurrence and association scopes
    public final void testCase08() throws Exception {

		readMaps(testMapFile_08, testSchemaFile_08);
		
		Topic familie_name = testMap.getTopicBySubjectIdentifier(testMap.createLocator("http://tmclvalidator.topicmapslab.de/familie_name"));
		assertNotNull(familie_name);
		
		Topic description = testMap.getTopicBySubjectIdentifier(testMap.createLocator("http://tmclvalidator.topicmapslab.de/description"));
		assertNotNull(description);
		
		Topic lives_in = testMap.getTopicBySubjectIdentifier(testMap.createLocator("http://tmclvalidator.topicmapslab.de/lives_in"));
		assertNotNull(lives_in);
		
		this.results = runValidator();
		
		assertEquals(3, this.results.size());
		
		for(Construct construct:this.results.keySet())
		{
			Typed typed = (Typed)construct;
			assertNotNull(typed);
			
			if(typed instanceof Name)
			{
				assertTrue(familie_name.equals(typed.getType()));
				
			}else if(typed instanceof Occurrence)
			{
				assertTrue(description.equals(typed.getType()));
				
			}else if(typed instanceof Association)
			{
				assertTrue(lives_in.equals(typed.getType()));
				
			}else assertNotNull(null);
		}
    }

	
	
}
