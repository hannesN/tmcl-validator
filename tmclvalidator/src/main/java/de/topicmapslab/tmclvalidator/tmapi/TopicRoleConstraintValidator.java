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
	 * @param useIdentifierInMessages - Forces the usage of identifier in result messages.
	 */
	public TopicRoleConstraintValidator(String id, boolean useIdentifierInMessages) {
	    super(id, useIdentifierInMessages);
    }
		
	public void validate(TopicMap mergedTopicMap, Map<Construct, Set<ValidationResult>> invalidConstructs) throws TMCLValidatorException 
	{
		Map<IConstraint,Topic> constraintsAndTypes = getConstraintsAndTypes(mergedTopicMap, CONSTRAINT_TOPIC_TYPE, TOPIC_ROLE_CONSTRAINT);
		
		for(Map.Entry<IConstraint, Topic> entry:constraintsAndTypes.entrySet()){
		
			int cardMin = ((TopicRoleConstraint)entry.getKey()).cardMin;
			int cardMax = ((TopicRoleConstraint)entry.getKey()).cardMax;
			Topic roleType = ((TopicRoleConstraint)entry.getKey()).roleType;
			Topic associationType = ((TopicRoleConstraint)entry.getKey()).associationType;
			
			for(Topic instance:getTopics(entry.getValue())){
				
				Set<Role> roles = getTopicRolesByAssociationType(instance, roleType, associationType);
				
				if(cardMin > roles.size()){
					addInvalidConstruct(instance, "The topic has too few roles of type " + getBestName(roleType) + " playing in an association of type " + getBestName(associationType) + " [" + roles.size() + " of min " + cardMin + "]", invalidConstructs);
					continue;
				}
				
				if(cardMax != -1 && cardMax < roles.size()){
					addInvalidConstruct(instance, "The topic has too many roles of type " + getBestName(roleType) + " playing in an association of type " + getBestName(associationType) + " [" + roles.size() + " of max " + cardMin + "]", invalidConstructs);
					continue;
				}
			}
		}

	}
	
}
