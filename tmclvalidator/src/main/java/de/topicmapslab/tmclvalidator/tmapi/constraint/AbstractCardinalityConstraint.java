/**
 * Copyright: Copyright 2010 Topic Maps Lab, University of Leipzig. http://www.topicmapslab.de/    
 * License:   Apache License, Version 2.0 http://www.apache.org/licenses/LICENSE-2.0.html
 * 
 * @author Christian Haß
 */
package de.topicmapslab.tmclvalidator.tmapi.constraint;

import org.tmapi.core.Topic;

import de.topicmapslab.tmclvalidator.TMCLValidatorException;


public abstract class AbstractCardinalityConstraint extends AbstractConstraint {

	protected static final String CARD_MIN = "http://psi.topicmaps.org/tmcl/card-min";
	protected static final String CARD_MAX = "http://psi.topicmaps.org/tmcl/card-max";

	protected int getMinCardinality(Topic topic) throws TMCLValidatorException{
		
		Topic type = topic.getTopicMap().getTopicBySubjectIdentifier(topic.getTopicMap().createLocator(CARD_MIN));
		
		if(type == null)
			return 0;
		
		String value = getOccurrenceValue(topic, type);
		
		if(value.equals("")) return 0;
		if(value.equals("*"))
			throw new TMCLValidatorException("'*' is not an valid value for the min cardinality.");
		
		return Integer.parseInt(value);
		
	}
	
	protected int getMaxCardinality(Topic topic) throws TMCLValidatorException{
		
		Topic type = topic.getTopicMap().getTopicBySubjectIdentifier(topic.getTopicMap().createLocator(CARD_MAX));
		
		if(type == null)
			return -1;
		
		String value = getOccurrenceValue(topic, type);
		
		if(value.equals("")) return -1;
		if(value.equals("*")) return -1;
			
		return Integer.parseInt(value);
		
	}
}
