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
import de.topicmapslab.tmclvalidator.tmapi.constraint.IConstraint;
import de.topicmapslab.tmclvalidator.tmapi.constraint.RoleCombinationConstraint;


/**
 * Validator for the role combination constraint.
 */
public class RoleCombinationConstraintValidator extends AbstractTMAPIValidator {

	private static final String ROLE_COMBINATION_CONSTRAINT = "http://psi.topicmaps.org/tmcl/role-combination-constraint";
	private static final String CONSTRAINT_STATEMENT = "http://psi.topicmaps.org/tmcl/constrained-statement";

	/**
	 * Constructor
	 * @param id - The validator ID.
	 */
	public RoleCombinationConstraintValidator(String id, boolean useIdentifierInMessages) {
	    super(id, useIdentifierInMessages);
    }
	
	public void validate(TopicMap mergedTopicMap, Map<Construct, Set<ValidationResult>> invalidConstructs) throws TMCLValidatorException {
		
		// get constrained types and corresponding constraints
		Map<Topic, Set<IConstraint> > typesAndConstraints = getConstructTypesAndConstraints(mergedTopicMap, CONSTRAINT_STATEMENT, ROLE_COMBINATION_CONSTRAINT);

		for (Map.Entry<Topic, Set<IConstraint>> entry : typesAndConstraints.entrySet()) {

			Set<Association> associationInstances = getAssociations(entry.getKey());

			for (Association associationInstance : associationInstances) {
				
				// if a constraint exist for an association type -> for every role combination has to be one constraint defined

				// get roles
				Set<Role> roles = associationInstance.getRoles();
				
				Role[] roles_array = roles.toArray(new Role[0]);
				
				// check each combination

				for(int r1=0;r1<roles_array.length;r1++)
				{
					for(int r2=r1+1;r2<roles_array.length;r2++)
					{
						boolean combinationOk = false;

						if(!roles_array[r1].getType().equals(roles_array[r2].getType()))
						{
							// check of an constraint exist for this combination
							for (IConstraint constraint : entry.getValue()) 
							{
								RoleCombinationConstraint role_combination_constraint = (RoleCombinationConstraint)constraint;

								if((roles_array[r1].getType().equals(role_combination_constraint.roleType) 
										&& roles_array[r1].getPlayer().getTypes().contains(role_combination_constraint.topicType)
										&& roles_array[r2].getType().equals(role_combination_constraint.otherRoleType)
										&& roles_array[r2].getPlayer().getTypes().contains(role_combination_constraint.otherTopicType))
									|| (roles_array[r2].getType().equals(role_combination_constraint.roleType) 
									&& roles_array[r2].getPlayer().getTypes().contains(role_combination_constraint.topicType)
									&& roles_array[r1].getType().equals(role_combination_constraint.otherRoleType)
									&& roles_array[r1].getPlayer().getTypes().contains(role_combination_constraint.otherTopicType)))
								{
									combinationOk = true;
								}
							}
						}else combinationOk = true;
						
						if(!combinationOk)
						{
							addInvalidConstruct(associationInstance, "Association has an unallowed role combination: "
									+ getBestName(roles_array[r1].getPlayer())
									+ "(" + getBestName(roles_array[r1].getType()) + ") with "
									+ getBestName(roles_array[r2].getPlayer())
									+ "(" + getBestName(roles_array[r2].getType() )+ ")", invalidConstructs);
						}
					}
				}
			}
		}
	}
	

	

}
