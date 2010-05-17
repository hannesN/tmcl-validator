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
import org.tmapi.core.Reifiable;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicMap;
import org.tmapi.core.Typed;

import de.topicmapslab.tmclvalidator.TMCLValidatorException;
import de.topicmapslab.tmclvalidator.ValidationResult;
import de.topicmapslab.tmclvalidator.tmapi.constraint.IConstraint;
import de.topicmapslab.tmclvalidator.tmapi.constraint.TopicReifiesConstraint;
import de.topicmapslab.tmclvalidator.tmapi.utils.Utils;


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
		
		Map<Topic, Set<IConstraint> > typesAndConstraints = getConstructTypesAndConstraints(mergedTopicMap, CONSTRAINED_TOPIC_TYPE, TOPIC_REIFIER_CONSTRAINT);
		
		for(Map.Entry<Topic, Set<IConstraint>> entry:typesAndConstraints.entrySet())
		{
			for(Topic reifyingTopic:getTopics(entry.getKey())){

				// get reifierd topic
				Reifiable reifiedObject = reifyingTopic.getReified();
	
				// get type
				Topic type = null;
				
				if(reifiedObject != null)
				{
					
					if(!(reifiedObject instanceof Typed)){
						addInvalidConstruct(reifyingTopic, "Reified object is not 'typed'", invalidConstructs);
						continue;
					}
					
					type = ((Typed)reifiedObject).getType();
				}
				
				boolean reifiedObjectFound = false;
				
				for(IConstraint constraint:entry.getValue())
				{
					TopicReifiesConstraint topic_reifies_constraint = (TopicReifiesConstraint)constraint;
					
					// cannot reify constraint
					if(topic_reifies_constraint.cardMax == 0 && reifiedObject != null)
					{
						addInvalidConstruct(reifyingTopic, "Topics of type " + getBestName(entry.getKey()) + " are not allowed to reify.", invalidConstructs);
						
					}else{
						
						// must reify constraint
						if(topic_reifies_constraint.cardMin == 1)
						{
							if(reifiedObject == null)
							{
								addInvalidConstruct(reifyingTopic, "Topic musst reify an object of type " + getBestName(topic_reifies_constraint.statementType), invalidConstructs);
								
							}else if(!Utils.hasType((Typed)reifiedObject, topic_reifies_constraint.statementType))
							{
								addInvalidConstruct(reifyingTopic, "The construct reified by the topic musst be of type " + getBestName(topic_reifies_constraint.statementType), invalidConstructs);
							}
						}
						
						if(reifiedObject != null && type.equals(topic_reifies_constraint.statementType))
							reifiedObjectFound = true;
					}
				}
	
				if(reifiedObject != null && !reifiedObjectFound)
				{
					addInvalidConstruct(reifyingTopic, "Topic reifies a construct of an unallowed type (" + getBestName(((Typed)reifiedObject).getType()) + ").", invalidConstructs);
				}
			}
		}
		
	}
	
	
	
}
