/**
 * 
 */
package de.topicmapslab.tmclvalidator.test.cxtm;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.tmapi.core.Construct;
import org.tmapi.core.TMAPIException;
import org.tmapi.core.TopicMap;
import org.tmapi.core.TopicMapSystem;
import org.tmapi.core.TopicMapSystemFactory;
import org.tmapix.io.CTMTopicMapReader;

import de.topicmapslab.tmclvalidator.TMCLValidator;
import de.topicmapslab.tmclvalidator.TMCLValidatorException;
import de.topicmapslab.tmclvalidator.ValidationResult;

@RunWith(Parameterized.class)
public class CXTMTestValid extends AbstractCXTMTest {

	public static final String BAD_SCHEMA = "src/test/resources/cxtm/tmcl/level-1/bad-schema";
	
	public static final String VALID = "src/test/resources/cxtm/tmcl/level-1/valid";
	
	

    public CXTMTestValid(String file) {
    	super(file);
    }

    @Test
    public void test() throws TMAPIException, IOException, TMCLValidatorException{
    	
    	System.out.println("Test " + this.file);
    	
		File f = new File(file);
		
        TopicMapSystemFactory factory = TopicMapSystemFactory.newInstance();
        TopicMapSystem system = factory.newTopicMapSystem();
        
        TopicMap testMap = system.createTopicMap("file:" + f.getAbsolutePath());
        
        CTMTopicMapReader reader = new CTMTopicMapReader(testMap, f);
        reader.read();
        
        TMCLValidator validator = new TMCLValidator();
		Map<Construct, Set<ValidationResult>> results = validator.validate(testMap);

		plotInvalidConstructs(results);
    	assertTrue(results.isEmpty());

    }
    
    
    @Parameters
    public static Collection<Object[]> data() {
    	
    	Collection<Object[]> data = new ArrayList<Object[]>();
    	
    	for(String f:getTestFiles(VALID)){
    		Object[] file = new Object[] { f };
    		data.add(file);
    	}

        return data;
    }
    
    
    
}
