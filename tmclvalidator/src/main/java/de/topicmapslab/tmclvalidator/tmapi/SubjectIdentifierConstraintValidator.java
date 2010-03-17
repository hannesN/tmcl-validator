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
				
		// get constrained types and corresponding constraints
		Map<Topic, Set<IConstraint> > topicAndConstraints = getTopicsAndConstraints(mergedTopicMap, CONSTRAINED_TOPIC_TYPE, SUBJECT_IDENTIFIER_CONSTRAINT);

		for(Map.Entry<Topic, Set<IConstraint>> entry:topicAndConstraints.entrySet())
		{
			Topic topic = entry.getKey();

			Set<Locator> subjectIdentifiers = topic.getSubjectIdentifiers();
				
			// check each constraint
			for(IConstraint constraint:entry.getValue())
			{
				if(!(constraint instanceof SubjectIdentifierConstraint))
					throw new TMCLValidatorException("Constraint is no Subject Identifier Constraint.");
				
				SubjectIdentifierConstraint si_constraint = (SubjectIdentifierConstraint)constraint;
				
				if(subjectIdentifiers.size() < si_constraint.cardMin)
				{
					addInvalidConstruct(topic, "Topic has too few subject identifier", invalidConstructs);
				}
				
				if(si_constraint.cardMax != -1 && subjectIdentifiers.size() > si_constraint.cardMin)
				{
					addInvalidConstruct(topic, "Topic has too many subject identifier", invalidConstructs);
				}
				
				// check regular expression
				for(Locator subjectIdentifier:subjectIdentifiers)
				{
					if(!subjectIdentifier.getReference().matches(si_constraint.regExp))
					{
						addInvalidConstruct(topic, "Subject identifier '" + subjectIdentifier.getReference() + "' doesn't macht the reqular expression '" + si_constraint.regExp + "'.", invalidConstructs);
					}
				}
			}
		}
	}
	
	
	
}
