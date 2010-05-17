/**
 * Copyright: Copyright 2010 Topic Maps Lab, University of Leipzig. http://www.topicmapslab.de/    
 * License:   Apache License, Version 2.0 http://www.apache.org/licenses/LICENSE-2.0.html
 * 
 * @author Christian Haß
 */
package de.topicmapslab.tmclvalidator;


import java.util.Map;
import java.util.Set;

import org.tmapi.core.Construct;
import org.tmapi.core.TopicMap;

/**
 * Interface of an validator class.
 */
public interface IConstraintValidator{

	/**
	 * Validates a topic map which is merged with the used schema.
	 * @param topicMap - The topic map containing the schema.
	 * @param invalidConstructs - Reference to an result set which will be filled with invalid constructs.
	 */
	public void validate(TopicMap topicMap, Map<Construct, Set<ValidationResult>> invalidConstructs) throws TMCLValidatorException;
	
}
