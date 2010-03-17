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

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.tmapi.core.Construct;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicMap;
import org.tmapi.core.TopicMapSystem;
import org.tmapi.core.TopicMapSystemFactory;
import org.tmapi.core.Typed;

import de.topicmapslab.tmcl_loader.TMCLLoader;
import de.topicmapslab.tmclvalidator.TMCLValidator;
import de.topicmapslab.tmclvalidator.ValidationResult;

public abstract class AbstractTest {

	protected TopicMap testMap = null;
	protected TopicMap testSchema = null;
	
	Map<Construct, Set<ValidationResult>> results;
	
	@Before
    public void setUp() throws Exception {

		TopicMapSystemFactory factory = TopicMapSystemFactory.newInstance();
		TopicMapSystem system = factory.newTopicMapSystem();

		testMap = system.createTopicMap("test:test");
		assertNotNull(testMap);
		testSchema = system.createTopicMap("test:schema");
		assertNotNull(testSchema);
		
		if(this.results == null)
			this.results = new HashMap<Construct, Set<ValidationResult>>();

		this.results.clear();
	}
	
	@After
    public void tearDown() throws Exception {
		
	}
	
	protected void readMaps(String mapFile, String schemaFile) throws Exception{
		
		TMCLLoader.readTMCLSchema(testMap, new File(mapFile));
		TMCLLoader.readTMCLSchema(testSchema, new File(schemaFile));
	}
	
	protected Map<Construct, Set<ValidationResult>> runValidator(String mapFile, String schemaFile) throws Exception{
		
		readMaps(mapFile, schemaFile);
		return runValidator();
	}
	
	protected Map<Construct, Set<ValidationResult>> runValidator() throws Exception{
	
		if(testSchema == null) throw new Exception("Schema is null!");
		if(testMap == null) throw new Exception("Topic map is null!");
		
		TMCLValidator validator = new TMCLValidator(testSchema); 
		Map<Construct, Set<ValidationResult>> results = validator.validate(testMap);
		
		return results;
	}
	
	protected void assertResultType(Set<Construct> results, Topic type)
	{
		for(Construct construct:results)
		{
			if(construct instanceof Typed)
			{
				Typed typed = (Typed)construct;
				assertTrue(type.equals(typed.getType()));
			}
		}
	}
	
	protected void checkNumberAndType(String mapFile, String schemaFile, int numInvalidConstructs, String invalidConstructsTypeSubjectIdentifier) throws Exception
	{
		readMaps(mapFile, schemaFile);

		Topic type = this.testMap.getTopicBySubjectIdentifier(this.testMap.createLocator(invalidConstructsTypeSubjectIdentifier));
		if(type == null) type = this.testSchema.getTopicBySubjectIdentifier(this.testSchema.createLocator(invalidConstructsTypeSubjectIdentifier));
		assertNotNull(type);
		
		this.results = runValidator();
		
		assertEquals(numInvalidConstructs, this.results.size());
		assertResultType(this.results.keySet(), type);
	}
	
	protected void checkForInvalidTopic(String mapFile, String schemaFile, String invalidConstructSubjectIdentifier) throws Exception
	{
		readMaps(mapFile, schemaFile);

		Topic invalidTopic = this.testMap.getTopicBySubjectIdentifier(this.testMap.createLocator(invalidConstructSubjectIdentifier));
		if(invalidTopic == null) invalidTopic = this.testSchema.getTopicBySubjectIdentifier(this.testSchema.createLocator(invalidConstructSubjectIdentifier));
		
		assertNotNull(invalidTopic);
		
		this.results = runValidator();

		assertEquals(1, this.results.size());
		assertTrue(this.results.keySet().iterator().next().equals(invalidTopic));
	}
	
}
