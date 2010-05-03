
package de.topicmapslab.tmclvalidator.test.cxtm;

import java.io.File;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.tmapi.core.Construct;

import de.topicmapslab.tmclvalidator.ValidationResult;

public abstract class AbstractCXTMTest {

	protected String file;
	
    public AbstractCXTMTest(String file) {

    	this.file = file;
    }
    
    
    protected static Set<String> getTestFiles(String basePath){
    	
		Set<String> result = new HashSet<String>();
		
		File p = new File(basePath);
		
		if(!p.exists())
			throw new RuntimeException("Path does not exist.");
		
		System.out.println(p.getAbsolutePath());
		
		if(!p.isDirectory())
			throw new RuntimeException("Not a directory");
		
		File[] entries = p.listFiles();
		
		for(File entry:entries){

			if(entry.isFile())
				if(entry.getAbsolutePath().endsWith(".ctm"))
					result.add(entry.getAbsolutePath());
		}

		return result;
	}
    
    protected void plotInvalidConstructs(Map<Construct, Set<ValidationResult>> results){
    	
    	for(Map.Entry<Construct, Set<ValidationResult>>entry:results.entrySet()){
    		
    		System.out.println("\nInvalid Construct: " + entry.getKey().toString());
    		for(ValidationResult r:entry.getValue())
    			System.out.println(r.getConstraintId() + ": " + r.getMessage());
    		
    	}
    	
    }
   

	
}
