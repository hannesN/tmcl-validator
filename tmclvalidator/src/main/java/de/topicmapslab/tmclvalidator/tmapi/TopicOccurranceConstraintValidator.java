/**
 * Copyright: Copyright 2010 Topic Maps Lab, University of Leipzig. http://www.topicmapslab.de/    
 * License:   Apache License, Version 2.0 http://www.apache.org/licenses/LICENSE-2.0.html
 * 
 * @author Christian Haß
 */
package de.topicmapslab.tmclvalidator.tmapi;

import java.util.Map;
import java.util.Set;

import org.tmapi.core.Construct;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicMap;

import de.topicmapslab.tmclvalidator.TMCLValidatorException;
import de.topicmapslab.tmclvalidator.ValidationResult;
import de.topicmapslab.tmclvalidator.tmapi.constraint.IConstraint;
import de.topicmapslab.tmclvalidator.tmapi.constraint.TopicOccurrenceConstraint;


/**
 * Validator for the topic occurrence constraint.
 */
public class TopicOccurranceConstraintValidator extends AbstractTMAPIValidator {

	private static final String TOPIC_OCCURRENCE_CONSTRAINT = "http://psi.topicmaps.org/tmcl/topic-occurrence-constraint";
	private static final String CONSTRAINT_TOPIC_TYPE = "http://psi.topicmaps.org/tmcl/constrained-topic-type";
	
	/**
	 * Constructor
	 * @param id - The validator ID.
	 * @param useIdentifierInMessages - Forces the usage of identifier in result messages.
	 */
	public TopicOccurranceConstraintValidator(String id, boolean useIdentifierInMessages) {
	    super(id, useIdentifierInMessages);
    }

	public void validate(TopicMap mergedTopicMap, Map<Construct, Set<ValidationResult>> invalidConstructs) throws TMCLValidatorException{
		
		Map<IConstraint,Topic> constraintsAndTypes = getConstraintsAndTypes(mergedTopicMap, CONSTRAINT_TOPIC_TYPE, TOPIC_OCCURRENCE_CONSTRAINT);
		
		for(Map.Entry<IConstraint, Topic> entry:constraintsAndTypes.entrySet()){
			
			int cardMin = ((TopicOccurrenceConstraint)entry.getKey()).cardMin;
			int cardMax = ((TopicOccurrenceConstraint)entry.getKey()).cardMax;
			Topic occurrenceType = ((TopicOccurrenceConstraint)entry.getKey()).occurrenceType;
			
			for(Topic instance:getTopics(entry.getValue())){
				
				Set<Occurrence> occurrences = getTopicOccurrences(instance, occurrenceType);
				
				if(cardMin > occurrences.size())
					addInvalidConstruct(instance, "The topic has too few occurrences of type " + getBestName(occurrenceType) + " [" + occurrences.size() + " of min " + cardMin + "]", invalidConstructs);
				
				if(cardMax != -1 && cardMax < occurrences.size())
					addInvalidConstruct(instance, "The topic has too many occurrences of type " + getBestName(occurrenceType) + " [" + occurrences.size() + " of max " + cardMin + "]", invalidConstructs);
				
			}
		}
		
	}


}
