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
import de.topicmapslab.tmclvalidator.tmapi.constraint.SubjectIdentifierConstraint;


/**
 * Validator for the subject identifier constraint.
 */
public class SubjectIdentifierConstraintValidator extends AbstractTMAPIValidator  {

	private static final String SUBJECT_IDENTIFIER_CONSTRAINT = "http://psi.topicmaps.org/tmcl/subject-identifier-constraint";
	private static final String CONSTRAINED_TOPIC_TYPE = "http://psi.topicmaps.org/tmcl/constrained-topic-type";

	/**
	 * Constructor
	 * @param id - The validator ID.
	 */
	public SubjectIdentifierConstraintValidator(String id, boolean useIdentifierInMessages) {
	    super(id, useIdentifierInMessages);
    }
		
	public void validate(TopicMap mergedTopicMap, Map<Construct, Set<ValidationResult>> invalidConstructs) throws TMCLValidatorException
	{
		Map<IConstraint, Topic> constraintsAndTypes = getConstraintsAndTypes(mergedTopicMap, CONSTRAINED_TOPIC_TYPE, SUBJECT_IDENTIFIER_CONSTRAINT);
		
		for(Map.Entry<IConstraint, Topic> entry:constraintsAndTypes.entrySet()){
			
			String regExp = ((SubjectIdentifierConstraint)entry.getKey()).regExp;
			int cardMin = ((SubjectIdentifierConstraint)entry.getKey()).cardMin;
			int cardMax = ((SubjectIdentifierConstraint)entry.getKey()).cardMax;
			
			for(Topic instance:getTopics(entry.getValue())){
				
				Set<Locator> si = instance.getSubjectIdentifiers();
				
				if(cardMin > si.size())
					addInvalidConstruct(instance, "The topic has too few subject identifiers [" + si.size() + " of min " + cardMin + "]", invalidConstructs);
				
				if(cardMax != -1 && cardMax < si.size())
					addInvalidConstruct(instance, "The topic has too many subject identifiers [" + si.size() + " of max " + cardMin + "]", invalidConstructs);
			
				// check regex
				for(Locator l:si)
					if(!l.getReference().matches(regExp))
						addInvalidConstruct(instance, "The subject identifier '" + l.getReference() +  "' doesn't match the regular expression '" + regExp + "'", invalidConstructs);
			}
		}
	}
	
}
