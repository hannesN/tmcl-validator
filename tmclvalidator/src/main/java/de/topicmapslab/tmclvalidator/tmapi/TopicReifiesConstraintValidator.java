/**
 * Copyright: Copyright 2010 Topic Maps Lab, University of Leipzig. http://www.topicmapslab.de/    
 * License:   Apache License, Version 2.0 http://www.apache.org/licenses/LICENSE-2.0.html
 * 
 * @author Christian Haß
 */
package de.topicmapslab.tmclvalidator.tmapi;

import java.util.Map;
import java.util.Set;

import org.tmapi.core.Association;
import org.tmapi.core.Construct;
import org.tmapi.core.Name;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Reifiable;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicMap;

import de.topicmapslab.tmclvalidator.TMCLValidatorException;
import de.topicmapslab.tmclvalidator.ValidationResult;
import de.topicmapslab.tmclvalidator.tmapi.constraint.IConstraint;
import de.topicmapslab.tmclvalidator.tmapi.constraint.TopicReifiesConstraint;


/**
 * Validator for the topic reifies constraint.
 */
public class TopicReifiesConstraintValidator extends AbstractTMAPIValidator {

	
	private static final String TOPIC_REIFIER_CONSTRAINT = "http://psi.topicmaps.org/tmcl/topic-reifies-constraint";
	private static final String CONSTRAINED_TOPIC_TYPE = "http://psi.topicmaps.org/tmcl/constrained-topic-type";
	
	/**
	 * Constructor
	 * @param id - The validator ID.
	 */
	public TopicReifiesConstraintValidator(String id, boolean useIdentifierInMessages) {
	    super(id, useIdentifierInMessages);
    }
	
	public void validate(TopicMap mergedTopicMap, Map<Construct, Set<ValidationResult>> invalidConstructs) throws TMCLValidatorException 
	{

		Map<Topic, Set<IConstraint> > topicAndConstraints = getTopicsAndConstraints(mergedTopicMap, CONSTRAINED_TOPIC_TYPE, TOPIC_REIFIER_CONSTRAINT);

		for(Map.Entry<Topic, Set<IConstraint>> entry:topicAndConstraints.entrySet())
		{
			Topic reifyingTopic = entry.getKey();

			// get reifierd topic
			Reifiable reifiedObject = reifyingTopic.getReified();

			// get type
			Topic type = null;
			
			if(reifiedObject != null)
			{
				if(reifiedObject instanceof Name)
					type = ((Name) reifiedObject).getType();
					
				if(reifiedObject instanceof Occurrence)
					type = ((Occurrence) reifiedObject).getType();

				if(reifiedObject instanceof Association)
					type = ((Association) reifiedObject).getType();

				if(type == null)
					throw new TMCLValidatorException("Reified topic is not a Name, a Occurrence or a Association.");

			}
			
			boolean reifiedObjectFound = false;
			
			for(IConstraint constraint:entry.getValue())
			{
				TopicReifiesConstraint topic_reifies_constraint = (TopicReifiesConstraint)constraint;
				
				// cannot reify constraint
				if(topic_reifies_constraint.cardMin == 0 && topic_reifies_constraint.cardMax == 0 && reifiedObject != null)
				{
					addInvalidConstruct(reifyingTopic, "Topic is reifier of (" + reifiedObject + ") but is not allowed to be a reifier.", invalidConstructs);
					
				}else{
					
					// must reify constraint
					if(topic_reifies_constraint.cardMin == 1 && topic_reifies_constraint.cardMax == 1)
					{
						if(reifiedObject == null)
						{
							addInvalidConstruct(reifyingTopic, "Topic musst reify an object of type " + getBestName(topic_reifies_constraint.topicType), invalidConstructs);
						}
						
						else if(!type.equals(topic_reifies_constraint.topicType))
						{
							addInvalidConstruct(reifyingTopic, "Topic musst reify an object of type " + getBestName(topic_reifies_constraint.topicType), invalidConstructs);
						}
					}
					
					if(reifiedObject != null && type.equals(topic_reifies_constraint.topicType))
						reifiedObjectFound = true;
				}
			}

			if(reifiedObject != null && !reifiedObjectFound)
			{
				addInvalidConstruct(reifyingTopic, "Topic reifies an unallowed object (" + reifiedObject + ").", invalidConstructs);
			}
		}
	}
	
	
	
}
