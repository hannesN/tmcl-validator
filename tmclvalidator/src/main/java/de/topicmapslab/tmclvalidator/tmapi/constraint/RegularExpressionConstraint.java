/**
 * Copyright: Copyright 2010 Topic Maps Lab, University of Leipzig. http://www.topicmapslab.de/    
 * License:   Apache License, Version 2.0 http://www.apache.org/licenses/LICENSE-2.0.html
 * 
 * @author Christian Haß
 */
package de.topicmapslab.tmclvalidator.tmapi.constraint;

import org.tmapi.core.Topic;
import org.tmapi.core.TopicMap;

import de.topicmapslab.tmclvalidator.TMCLValidatorException;

public class RegularExpressionConstraint extends AbstractConstraint {

	public String regExp;
	
	public RegularExpressionConstraint()
	{
		this.regExp = ".*";
	}
	
	public void getParameter(Topic constraintInstance) throws TMCLValidatorException {
		
		TopicMap map = constraintInstance.getTopicMap();

		this.regExp = getOccurrenceValue(constraintInstance, map.getTopicBySubjectIdentifier(map.createLocator(REGEXP)));

	}
	
}
