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
	 */
	public TopicOccurranceConstraintValidator(String id, boolean useIdentifierInMessages) {
	    super(id, useIdentifierInMessages);
    }

	public void validate(TopicMap mergedTopicMap, Map<Construct, Set<ValidationResult>> invalidConstructs) throws TMCLValidatorException 
	{
		Map<Topic, Set<IConstraint> > topicAndConstraints = getTopicsAndConstraints(mergedTopicMap, CONSTRAINT_TOPIC_TYPE, TOPIC_OCCURRENCE_CONSTRAINT);

		for(Map.Entry<Topic, Set<IConstraint>> entry:topicAndConstraints.entrySet())
		{
			
			Topic topic = entry.getKey();

			Set<Occurrence> occurrences = topic.getOccurrences();

			Map<TopicOccurrenceConstraint, Integer> cardinalityCounter = new HashMap<TopicOccurrenceConstraint, Integer>();
					
			for(Occurrence occurrence:occurrences)
			{
				boolean occurrenceTypeFound = false;
				
				// check each constraint
				for(IConstraint constraint:entry.getValue())
				{
					TopicOccurrenceConstraint occurrance_constraint = (TopicOccurrenceConstraint)constraint;
					
					if(occurrence.getType().equals(occurrance_constraint.occurrenceType))
					{
						occurrenceTypeFound = true;
						// increase cardinality counter
						int count = 0;
						if(cardinalityCounter.get(occurrance_constraint) != null) count = cardinalityCounter.get(occurrance_constraint);
						count++;
						cardinalityCounter.put(occurrance_constraint, count);
					}
				}
				
				if(!occurrenceTypeFound)
				{
					addInvalidConstruct(topic, "Unexspected occurrence type (" + getBestName(occurrence.getType()) + ")", invalidConstructs);
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
    private void checkCardinality(Map<Construct, Set<ValidationResult>> invalidConstructs, Set<IConstraint> constraints, Topic topic, Map<TopicOccurrenceConstraint, Integer> cardinalityCounter) throws TMCLValidatorException {
	    
    	for(IConstraint constraint:constraints)
	    {
	    	TopicOccurrenceConstraint occurrence_constraint = (TopicOccurrenceConstraint)constraint;
	    	
	    	if (occurrence_constraint.cardMin != 0 || occurrence_constraint.cardMax != -1) 
	    	{
	    		int count = 0;
	    		if(cardinalityCounter.get(occurrence_constraint) != null) count = cardinalityCounter.get(occurrence_constraint);
	    		
	    		if (count < occurrence_constraint.cardMin) 
	    		{
	    			addInvalidConstruct(topic, "Number of occurrences of type " + getBestName(occurrence_constraint.occurrenceType) + " [" + count + "] is less then the specified minimum [" + occurrence_constraint.cardMin + "]", invalidConstructs);
	    		}

	    		if (occurrence_constraint.cardMax != -1 && count > occurrence_constraint.cardMax) 
	    		{
	    			addInvalidConstruct(topic, "Number of occurrences of type " + getBestName(occurrence_constraint.occurrenceType) + " [" + count + "] is bigger then the specified maximum [" + occurrence_constraint.cardMax + "]", invalidConstructs);
	    		}
	    	}
	    }
    }
	

}
