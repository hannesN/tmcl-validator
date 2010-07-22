/**
 * Copyright: Copyright 2010 Topic Maps Lab, University of Leipzig. http://www.topicmapslab.de/    
 * License:   Apache License, Version 2.0 http://www.apache.org/licenses/LICENSE-2.0.html
 * 
 * @author Christian Haß
 */
package de.topicmapslab.tmclvalidator.tmapi;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.tmapi.core.Construct;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicMap;
import org.tmapi.index.TypeInstanceIndex;

import de.topicmapslab.tmclvalidator.TMCLValidatorException;
import de.topicmapslab.tmclvalidator.ValidationResult;
import de.topicmapslab.tmclvalidator.tmapi.constraint.IConstraint;

/**
 * Validator for the abstract topic type constraint.
 */
public class AbstractTopicTypeConstraintValidator  extends AbstractTMAPIValidator {

	private static final String ABSTRACT_CONSTRAINT = "http://psi.topicmaps.org/tmcl/abstract-constraint";
	private static final String CONSTRAINED_TOPIC_TYPE = "http://psi.topicmaps.org/tmcl/constrained-topic-type";
	
	/**
	 * Constructor
	 * @param id - The validator ID.
	 * @param useIdentifierInMessages - Forces the usage of identifier in result messages.
	 */
	public AbstractTopicTypeConstraintValidator(String id, boolean useIdentifierInMessages) {
	    super(id, useIdentifierInMessages);
    }

	public void validate(TopicMap mergedTopicMap, Map<Construct, Set<ValidationResult>> invalidConstructs) throws TMCLValidatorException
	{
		TypeInstanceIndex typeInstanceIndex = getTypeInstanceIndex(mergedTopicMap);
		
		Map<IConstraint, Topic> constraintsAndTypes = getConstraintsAndTypes(mergedTopicMap, CONSTRAINED_TOPIC_TYPE, ABSTRACT_CONSTRAINT);
		
		// check each type if instances exist
		
		for(Map.Entry<IConstraint, Topic> entry:constraintsAndTypes.entrySet()){
			
			Collection<Topic> instances = typeInstanceIndex.getTopics(entry.getValue());
			
			if(!instances.isEmpty())
			{
				addInvalidConstruct(entry.getValue(), "The topic has to be abstract.", invalidConstructs);
			}
		}

	}

}
