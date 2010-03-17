/**
 * Copyright: Copyright 2010 Topic Maps Lab, University of Leipzig. http://www.topicmapslab.de/    
 * License:   Apache License, Version 2.0 http://www.apache.org/licenses/LICENSE-2.0.html
 * 
 * @author Christian Haß
 */
package de.topicmapslab.tmclvalidator.tmapi.constraint;

import java.util.Set;

import org.tmapi.core.Occurrence;
import org.tmapi.core.Topic;

import de.topicmapslab.tmclvalidator.TMCLValidatorException;



public abstract class AbstractConstraint implements IConstraint {
	
	protected static final String REGEXP = "http://psi.topicmaps.org/tmcl/regexp";

	protected String getOccurrenceValue(Topic topic, Topic occurrenceType) throws TMCLValidatorException{
		
		Set<Occurrence> occurrences = topic.getOccurrences(occurrenceType);
		if(occurrences.size() > 1)
			throw new TMCLValidatorException("More then one occurrenc of type (" + occurrenceType + ") defined.");
		
		if(occurrences.isEmpty()){
			return "";
		}else{
			return occurrences.iterator().next().getValue();
		}
	}
}
