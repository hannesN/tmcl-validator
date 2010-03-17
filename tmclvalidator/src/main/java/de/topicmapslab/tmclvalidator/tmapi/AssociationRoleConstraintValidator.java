/**
 * Copyright: Copyright 2010 Topic Maps Lab, University of Leipzig. http://www.topicmapslab.de/    
 * License:   Apache License, Version 2.0 http://www.apache.org/licenses/LICENSE-2.0.html
 * 
 * @author Christian Haß
 */
package de.topicmapslab.tmclvalidator.tmapi;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.tmapi.core.Association;
import org.tmapi.core.Construct;
import org.tmapi.core.Role;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicMap;
import org.tmapi.index.TypeInstanceIndex;

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
	 */
	public AssociationRoleConstraintValidator(String id, boolean useIdentifierInMessages) {
	    super(id, useIdentifierInMessages);
    }
	
	public void validate(TopicMap mergedTopicMap, Map<Construct, Set<ValidationResult>> invalidConstructs) throws TMCLValidatorException 
	{
		TypeInstanceIndex typeInstanceIndex = mergedTopicMap.getIndex(TypeInstanceIndex.class);
		
		// get constrained types and corresponding constraints
		Map<Topic, Set<IConstraint> > typesAndConstraints = getConstructTypesAndConstraints(mergedTopicMap, CONSTRAINT_STATEMENT, ASSOCIATION_ROLE_CONSTRAINT);

		for(Map.Entry<Topic, Set<IConstraint>> entry:typesAndConstraints.entrySet())
		{
			
			Collection<Association> associationInstances = typeInstanceIndex.getAssociations(entry.getKey());
			
			for(Association associationInstance:associationInstances)
			{
				Set<Role> roles = associationInstance.getRoles();
				
				Map<AssociationRoleConstraint, Integer> cardinalityCounter = new HashMap<AssociationRoleConstraint, Integer>();
				
				for(Role role:roles)
				{
				
					boolean roleFound = false;
					
					for(IConstraint constraint:entry.getValue())
					{
						AssociationRoleConstraint association_role_constraint = (AssociationRoleConstraint)constraint;
						
						if(role.getType().equals(association_role_constraint.roleType))
						{
							roleFound = true;
							// increate cardinality counter
							int count = 0;
							if(cardinalityCounter.get(constraint) != null) count = cardinalityCounter.get(constraint);
							count++;
							cardinalityCounter.put(association_role_constraint, count);
						}
					}
					
					if(!roleFound)
						addInvalidConstruct(associationInstance, "Association has an unexspected role type (" + getBestName(role.getType()) + ")", invalidConstructs);

				}
				
				// check cardinality
				checkCardinality(invalidConstructs, entry.getValue(), associationInstance, cardinalityCounter);
			}
		}
	}

	/**
	 * Checks the cardinality.
	 * @param invalidConstructs - Set of invalid constructs.
	 * @param constraints - Set of constraint wrapper.
	 * @param associationInstance - The association instance for witch the cardinallity is checked.
	 * @param cardinalityCounter - The cardinalities.
	 */
    private void checkCardinality(Map<Construct, Set<ValidationResult>> invalidConstructs, Set<IConstraint> constraints, Association associationInstance, Map<AssociationRoleConstraint, Integer> cardinalityCounter) throws TMCLValidatorException {
	    
    	for(IConstraint constraint:constraints)
	    {
	    	AssociationRoleConstraint association_role_constraint = (AssociationRoleConstraint)constraint;
	    	
	    	int numRoles = 0;
	    	if(cardinalityCounter.get(constraint) != null) numRoles = cardinalityCounter.get(constraint);
	    	
	    	if(numRoles < association_role_constraint.cardMin)
	    	{
	    		addInvalidConstruct(associationInstance, "Number of roles of type " + getBestName(association_role_constraint.roleType) + " [" + numRoles + "] is less then the specified minimum [" + association_role_constraint.cardMin + "]", invalidConstructs);
	    	}
	    	
	    	if(association_role_constraint.cardMax != -1 && numRoles > association_role_constraint.cardMax)
	    	{
	    		addInvalidConstruct(associationInstance, "Number of roles of type " + getBestName(association_role_constraint.roleType) + " [" + numRoles + "] is bigger then the specified maximum [" + association_role_constraint.cardMax + "]", invalidConstructs);
	    	}
	    }
    }
	
}
