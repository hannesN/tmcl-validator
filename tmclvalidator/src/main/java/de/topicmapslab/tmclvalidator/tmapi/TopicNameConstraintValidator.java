/**
 * Copyright: Copyright 2010 Topic Maps Lab, University of Leipzig. http://www.topicmapslab.de/    
 * License:   Apache License, Version 2.0 http://www.apache.org/licenses/LICENSE-2.0.html
 * 
 * @author Christian Haß
 */
package de.topicmapslab.tmclvalidator.tmapi;

import java.util.HashMap;
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
		Map<Topic, Set<IConstraint> > topicAndConstraints = getTopicsAndConstraints(mergedTopicMap, CONSTRAINT_TOPIC_TYPE, TOPIC_NAME_CONSTRAINT);
		
		for(Map.Entry<Topic, Set<IConstraint>> entry:topicAndConstraints.entrySet())
		{
			Topic topic = entry.getKey();

			Set<Name> names = topic.getNames();

			Map<TopicNameConstraint, Integer> cardinalityCounter = new HashMap<TopicNameConstraint, Integer>();

			for(Name name:names)
			{
				boolean nameTypeFound = false;
				
				// check each constraint
				for(IConstraint constraint:entry.getValue())
				{
					if(!(constraint instanceof TopicNameConstraint)) /// TODO extract this from here
						throw new TMCLValidatorException("Constraint is no Topic Name Constraint.");
					
					TopicNameConstraint name_constraint = (TopicNameConstraint)constraint;
					
					if(name.getType().equals(name_constraint.nameType))
					{
						nameTypeFound = true;
						
						// increase cardinality counter
						int count = 0;
						if(cardinalityCounter.get(name_constraint) != null) 
							count = cardinalityCounter.get(name_constraint);
						
						count++;
						cardinalityCounter.put(name_constraint, count);
					}
				}
				
				if(!nameTypeFound)
				{
					addInvalidConstruct(topic, "Unexspected name type (" + getBestName(name.getType()) + ")", invalidConstructs);
				}
			}
		
			// check cardinality
			checkCardinality(invalidConstructs, entry.getValue(), topic, cardinalityCounter);
			
		}
	}

	/**
	 * Checks the cardinality.
	 * @param invalidConstructs - Set of invalid constructs.
	 * @param constraints - Set of constraint wrapper.
	 * @param topic - The topic which is checked.
	 * @param cardinalityCounter - The cardinalities.
	 */
    private void checkCardinality(Map<Construct, Set<ValidationResult>> invalidConstructs, Set<IConstraint> constraints, Topic topic, Map<TopicNameConstraint, Integer> cardinalityCounter) throws TMCLValidatorException {
	    
    	for(IConstraint constraint:constraints)
	    {
	    	TopicNameConstraint name_constraint = (TopicNameConstraint)constraint;
	    	
	    	if (name_constraint.cardMin != 0 || name_constraint.cardMax != -1) 
	    	{
	    		int count = 0;
	    		if(cardinalityCounter.get(name_constraint) != null) count = cardinalityCounter.get(name_constraint);
	    		
	    		if (count < name_constraint.cardMin) 
	    		{
	    			addInvalidConstruct(topic, "Number of names of type " + getBestName(name_constraint.nameType) + " [" + count + "] is less then the specified minimum [" + name_constraint.cardMin + "]", invalidConstructs);
	    		}

	    		if (name_constraint.cardMax != -1 && count > name_constraint.cardMax) 
	    		{
	    			addInvalidConstruct(topic, "Number of names of type " + getBestName(name_constraint.nameType) + " [" + count + "] is bigger then the specified maximum [" + name_constraint.cardMax + "]", invalidConstructs);
	    		}
	    	}
	    }
    }
	
	

}
