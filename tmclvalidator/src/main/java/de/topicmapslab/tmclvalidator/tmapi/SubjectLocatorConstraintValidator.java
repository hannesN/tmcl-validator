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
import org.tmapi.core.Locator;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicMap;

import de.topicmapslab.tmclvalidator.TMCLValidatorException;
import de.topicmapslab.tmclvalidator.ValidationResult;
import de.topicmapslab.tmclvalidator.tmapi.constraint.IConstraint;
import de.topicmapslab.tmclvalidator.tmapi.constraint.SubjectLocatorConstraint;


/**
 * Validator for the subject locator constraint.
 */
public class SubjectLocatorConstraintValidator  extends AbstractTMAPIValidator {

	private static final String SUBJECT_LOCATOR_CONSTRAINT = "http://psi.topicmaps.org/tmcl/subject-locator-constraint";
	private static final String CONSTRAINED_TOPIC_TYPE = "http://psi.topicmaps.org/tmcl/constrained-topic-type";
	
	/**
	 * Constructor
	 * @param id - The validator ID.
	 * @param useIdentifierInMessages - Forces the usage of identifier in result messages.
	 */
	public SubjectLocatorConstraintValidator(String id, boolean useIdentifierInMessages) {
	    super(id, useIdentifierInMessages);
    }
	
	
	public void validate(TopicMap mergedTopicMap, Map<Construct, Set<ValidationResult>> invalidConstructs) throws TMCLValidatorException
	{
		Map<IConstraint, Topic> constraintsAndTypes = getConstraintsAndTypes(mergedTopicMap, CONSTRAINED_TOPIC_TYPE, SUBJECT_LOCATOR_CONSTRAINT);
		
		for(Map.Entry<IConstraint, Topic> entry:constraintsAndTypes.entrySet()){
			
			String regExp = ((SubjectLocatorConstraint)entry.getKey()).regExp;
			int cardMin = ((SubjectLocatorConstraint)entry.getKey()).cardMin;
			int cardMax = ((SubjectLocatorConstraint)entry.getKey()).cardMax;
			
			for(Topic instance:getTopics(entry.getValue())){
				
				Set<Locator> sl = instance.getSubjectLocators();
				
				if(cardMin > sl.size())
					addInvalidConstruct(instance, "The topic has too few subject locator [" + sl.size() + " of min " + cardMin + "]", invalidConstructs);
				
				if(cardMax != -1 && cardMax < sl.size())
					addInvalidConstruct(instance, "The topic has too many subject locator [" + sl.size() + " of max " + cardMin + "]", invalidConstructs);
			
				// check regex
				for(Locator l:sl)
					if(!l.getReference().matches(regExp))
						addInvalidConstruct(instance, "The subject locator '" + l.getReference() +  "' doesn't match the regular expression '" + regExp + "'", invalidConstructs);
			}
		}
	}

}
