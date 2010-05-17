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
import org.tmapi.core.Name;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicMap;

import de.topicmapslab.tmclvalidator.TMCLValidatorException;
import de.topicmapslab.tmclvalidator.ValidationResult;
import de.topicmapslab.tmclvalidator.tmapi.constraint.IConstraint;
import de.topicmapslab.tmclvalidator.tmapi.constraint.TopicNameConstraint;

/**
 * Validator for the topic name constraint.
 */
public class TopicNameConstraintValidator extends AbstractTMAPIValidator {

	// constants

	private static final String TOPIC_NAME_CONSTRAINT = "http://psi.topicmaps.org/tmcl/topic-name-constraint";
	private static final String CONSTRAINT_TOPIC_TYPE = "http://psi.topicmaps.org/tmcl/constrained-topic-type";

	/**
	 * Constructor
	 * @param id - The validator ID.
	 */
	public TopicNameConstraintValidator(String id, boolean useIdentifierInMessages) {
	    super(id, useIdentifierInMessages);
    }

	public void validate(TopicMap mergedTopicMap, Map<Construct, Set<ValidationResult>> invalidConstructs) throws TMCLValidatorException 
	{
		Map<IConstraint,Topic> constraintsAndTypes = getConstraintsAndTypes(mergedTopicMap, CONSTRAINT_TOPIC_TYPE, TOPIC_NAME_CONSTRAINT);
		
		for(Map.Entry<IConstraint, Topic> entry:constraintsAndTypes.entrySet()){
			
			int cardMin = ((TopicNameConstraint)entry.getKey()).cardMin;
			int cardMax = ((TopicNameConstraint)entry.getKey()).cardMax;
			Topic nameType = ((TopicNameConstraint)entry.getKey()).nameType;
			
			for(Topic instance:getTopics(entry.getValue())){
				
				Set<Name> names = getTopicNames(instance, nameType);
				
				if(cardMin > names.size())
					addInvalidConstruct(instance, "The topic has too few names of type " + getBestName(nameType) + " [" + names.size() + " of min " + cardMin + "]", invalidConstructs);
				
				if(cardMax != -1 && cardMax < names.size())
					addInvalidConstruct(instance, "The topic has too many names of type " + getBestName(nameType) + " [" + names.size() + " of max " + cardMin + "]", invalidConstructs);
				
			}
		}
	}

}
