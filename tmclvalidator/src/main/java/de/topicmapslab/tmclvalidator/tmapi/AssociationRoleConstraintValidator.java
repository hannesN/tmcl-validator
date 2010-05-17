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
import org.tmapi.core.Role;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicMap;

import de.topicmapslab.tmclvalidator.TMCLValidatorException;
import de.topicmapslab.tmclvalidator.ValidationResult;
import de.topicmapslab.tmclvalidator.tmapi.constraint.AssociationRoleConstraint;
import de.topicmapslab.tmclvalidator.tmapi.constraint.IConstraint;


/**
 * Validator for the association role constraint.
 */
public class AssociationRoleConstraintValidator extends AbstractTMAPIValidator {


	private static final String ASSOCIATION_ROLE_CONSTRAINT = "http://psi.topicmaps.org/tmcl/association-role-constraint";
	private static final String CONSTRAINT_STATEMENT = "http://psi.topicmaps.org/tmcl/constrained-statement";
	
	
	/**
	 * Constructor
	 * @param id - The validator ID.
	 * @param useIdentifierInMessages - Forces the usage of identifier in result messages.
	 */
	public AssociationRoleConstraintValidator(String id, boolean useIdentifierInMessages) {
	    super(id, useIdentifierInMessages);
    }
	
	public void validate(TopicMap mergedTopicMap, Map<Construct, Set<ValidationResult>> invalidConstructs) throws TMCLValidatorException 
	{
		Map<IConstraint,Topic> constraintsAndTypes = getConstraintsAndTypes(mergedTopicMap, CONSTRAINT_STATEMENT, ASSOCIATION_ROLE_CONSTRAINT);
				
		for(Map.Entry<IConstraint, Topic> entry:constraintsAndTypes.entrySet()){
			
			// get association of that type
			Set<Association> associations = getAssociations(entry.getValue());
			
			Topic roleType = ((AssociationRoleConstraint)entry.getKey()).roleType;
			int cardMin = ((AssociationRoleConstraint)entry.getKey()).cardMin;
			int cardMax = ((AssociationRoleConstraint)entry.getKey()).cardMax;
			
			for(Association association:associations){
				
				Set<Role> roles = association.getRoles(roleType);

				if(cardMin > roles.size())
					addInvalidConstruct(association, "The association has too few roles of type " + getBestName(roleType) + " [" + roles.size() + " of min " + cardMin + "]", invalidConstructs);
				
				if(cardMax != -1 && cardMax < roles.size())
					addInvalidConstruct(association, "The association has too many roles of type " + getBestName(roleType) + " [" + roles.size() + " of max " + cardMin + "]", invalidConstructs);
				
			}
		}
	}
	
}
