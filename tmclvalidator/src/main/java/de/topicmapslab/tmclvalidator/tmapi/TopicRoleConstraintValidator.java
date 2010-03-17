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
import org.tmapi.core.Role;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicMap;

import de.topicmapslab.tmclvalidator.TMCLValidatorException;
import de.topicmapslab.tmclvalidator.ValidationResult;
import de.topicmapslab.tmclvalidator.tmapi.constraint.IConstraint;
import de.topicmapslab.tmclvalidator.tmapi.constraint.TopicRoleConstraint;


/**
 * Validator for the topic role constraint.
 */
public class TopicRoleConstraintValidator  extends AbstractTMAPIValidator {

	private static final String TOPIC_ROLE_CONSTRAINT = "http://psi.topicmaps.org/tmcl/topic-role-constraint";
	private static final String CONSTRAINT_TOPIC_TYPE = "http://psi.topicmaps.org/tmcl/constrained-topic-type";
	
	/**
	 * Constructor
	 * @param id - The validator ID.
	 */
	public TopicRoleConstraintValidator(String id, boolean useIdentifierInMessages) {
	    super(id, useIdentifierInMessages);
    }
		
	public void validate(TopicMap mergedTopicMap, Map<Construct, Set<ValidationResult>> invalidConstructs) throws TMCLValidatorException 
	{
		// get instances and constraints
		Map<Topic, Set<IConstraint> > topicsAndConstraints = getTopicsAndConstraints(mergedTopicMap, CONSTRAINT_TOPIC_TYPE, TOPIC_ROLE_CONSTRAINT);
		
		for(Map.Entry<Topic, Set<IConstraint>> entry:topicsAndConstraints.entrySet()){
			
			Topic topic = entry.getKey();

			Set<Role> rolesPlayed = topic.getRolesPlayed();
			Map<TopicRoleConstraint, Integer> cardinalityCounter = new HashMap<TopicRoleConstraint, Integer>();
			
			for(Role role:rolesPlayed)
			{
				boolean roleTypeFound = false;
				boolean associationTypeFound = false;
				
				for(IConstraint constraint:entry.getValue())
				{
					
					TopicRoleConstraint role_constraint = (TopicRoleConstraint)constraint;
					
					if(role.getType().equals(role_constraint.roleType))
					{
						roleTypeFound = true;
						// increase counter
						int count = 0;
						
						if(cardinalityCounter.get(role_constraint) != null) count = cardinalityCounter.get(role_constraint);
						count++;
						cardinalityCounter.put(role_constraint, count);
						
						// check for association type
						if(role.getParent().getType().equals(role_constraint.associationType)) associationTypeFound = true;
					}
				}
					
				if(!roleTypeFound)
				{
					addInvalidConstruct(topic, "Unexspected role type (" + getBestName(role.getType()) + ")", invalidConstructs);
					
				}else{
					
					// check association
					if(!associationTypeFound)
					{
						addInvalidConstruct(topic, "Topic playes the role " + getBestName(role.getType()) + " in an association of type " + getBestName(role.getParent().getType()) + " which is not allowed.", invalidConstructs);
					}
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
    private void checkCardinality(Map<Construct, Set<ValidationResult>> invalidConstructs, Set<IConstraint> constraints, Topic topic, Map<TopicRoleConstraint, Integer> cardinalityCounter) throws TMCLValidatorException {
	    
    	for(IConstraint constraint:constraints)
	    {
	    	TopicRoleConstraint role_constraint = (TopicRoleConstraint)constraint;
	    	
	    	int numRoles = 0;
	    	if(cardinalityCounter.get(role_constraint) != null) numRoles = cardinalityCounter.get(role_constraint);
	    	
	    	if(numRoles < role_constraint.cardMin)
	    	{
	    		addInvalidConstruct(topic, "Number of played roles of type " + getBestName(role_constraint.roleType) + " [" + numRoles + "] is less then the specified minimum [" + role_constraint.cardMin + "]", invalidConstructs);
	    	}
	    	
	    	if(role_constraint.cardMax != -1 && numRoles > role_constraint.cardMax)
	    	{
	    		addInvalidConstruct(topic, "Number of played roles of type " + getBestName(role_constraint.roleType) + " [" + numRoles + "] is bigger then the specified maximum [" + role_constraint.cardMax + "]", invalidConstructs);
	    	}
	    }
    }
	
   
	
}
