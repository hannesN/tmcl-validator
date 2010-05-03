/**
 * Copyright: Copyright 2010 Topic Maps Lab, University of Leipzig. http://www.topicmapslab.de/    
 * License:   Apache License, Version 2.0 http://www.apache.org/licenses/LICENSE-2.0.html
 * 
 * @author Christian Haß
 */
package de.topicmapslab.tmclvalidator;

import java.util.Set;

import org.tmapi.core.TopicMap;

/**
 * Interface for an validator factory, which creates validator classes. 
 * 
 */
public interface IConstraintValidatorFactory {

	/**
	 * Returns a set of validators according to the topic map schema.
	 * @param topicMap - The topic maps containing the schema.
	 * @return Set of validator classes.
	 */
	public Set<IConstraintValidator> getConstraintValidators(TopicMap topicMap);
		
}
