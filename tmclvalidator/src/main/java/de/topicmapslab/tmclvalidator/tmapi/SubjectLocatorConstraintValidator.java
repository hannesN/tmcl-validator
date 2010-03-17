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
	 */
	public SubjectLocatorConstraintValidator(String id, boolean useIdentifierInMessages) {
	    super(id, useIdentifierInMessages);
    }
	
	
	public void validate(TopicMap mergedTopicMap, Map<Construct, Set<ValidationResult>> invalidConstructs) throws TMCLValidatorException
	{
		Map<Topic, Set<IConstraint> > topicAndConstraints = getTopicsAndConstraints(mergedTopicMap, CONSTRAINED_TOPIC_TYPE, SUBJECT_LOCATOR_CONSTRAINT);
		
		for(Map.Entry<Topic, Set<IConstraint>> entry:topicAndConstraints.entrySet())
		{
			Topic topic = entry.getKey();
			
			Set<Locator> subjectLocators = topic.getSubjectLocators();
			
			// check each constraint
			for(IConstraint constraint:entry.getValue())
			{
				if(!(constraint instanceof SubjectLocatorConstraint))
					throw new TMCLValidatorException("Constraint is no Subject Locator Constraint.");
				
				SubjectLocatorConstraint sl_constraint = (SubjectLocatorConstraint)constraint;
				
				if(subjectLocators.size() < sl_constraint.cardMin)
				{
					addInvalidConstruct(topic, "Topic has too few subject locator.", invalidConstructs);
				}
				
				if(sl_constraint.cardMax != -1 && subjectLocators.size() > sl_constraint.cardMin)
				{
					addInvalidConstruct(topic, "Topic has too many subject locator.", invalidConstructs);
				}
				
				// check regular expression
				for(Locator subjectIdentifier:subjectLocators)
				{
					if(!subjectIdentifier.getReference().matches(sl_constraint.regExp))
					{
						addInvalidConstruct(topic, "Subject locator '" + subjectIdentifier.getReference() + "' doesn't macht the reqular expression '" + sl_constraint.regExp + "'.", invalidConstructs);
					}
				}
			}
		}
	}

}
