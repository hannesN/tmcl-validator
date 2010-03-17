/**
 * Copyright: Copyright 2010 Topic Maps Lab, University of Leipzig. http://www.topicmapslab.de/    
 * License:   Apache License, Version 2.0 http://www.apache.org/licenses/LICENSE-2.0.html
 * 
 * @author Christian Haß
 */
package de.topicmapslab.tmclvalidator.tmapi;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.tmapi.core.Association;
import org.tmapi.core.Construct;
import org.tmapi.core.Name;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Reifiable;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicMap;
import org.tmapi.index.TypeInstanceIndex;

import de.topicmapslab.tmclvalidator.TMCLValidatorException;
import de.topicmapslab.tmclvalidator.ValidationResult;
import de.topicmapslab.tmclvalidator.tmapi.constraint.IConstraint;
import de.topicmapslab.tmclvalidator.tmapi.constraint.ReifierConstraint;


/**
 * Validator for the reifier constraint.
 */
public class ReifierConstraintValidator extends AbstractTMAPIValidator {

	private static final String REIFIER_CONSTRAINT = "http://psi.topicmaps.org/tmcl/reifier-constraint";
	private static final String CONSTRAINT_STATEMENT = "http://psi.topicmaps.org/tmcl/constrained-statement";
		
	/**
	 * Constructor
	 * @param id - The validator ID.
	 */
	public ReifierConstraintValidator(String id, boolean useIdentifierInMessages) {
	    super(id, useIdentifierInMessages);
    }
	
	
	public void validate(TopicMap mergedTopicMap, Map<Construct, Set<ValidationResult>> invalidConstructs) throws TMCLValidatorException 
	{
		TypeInstanceIndex typeInstanceIndex = mergedTopicMap.getIndex(TypeInstanceIndex.class);
		
		// get constrained types and corresponding constraints
		Map<Topic, Set<IConstraint> > typesAndConstraints = getConstructTypesAndConstraints(mergedTopicMap, CONSTRAINT_STATEMENT, REIFIER_CONSTRAINT);
		
		for(Map.Entry<Topic, Set<IConstraint>> entry:typesAndConstraints.entrySet())
		{
			Collection<Reifiable> reifiableObjects = new HashSet<Reifiable>();
			
			// check associations
			Collection<Association> associations = typeInstanceIndex.getAssociations(entry.getKey());
			
			if(!associations.isEmpty())
			{
				for(Association association:associations)
					reifiableObjects.add(association);
				
			}else{
				
				// check occurrences
				Collection<Occurrence> occurrences = typeInstanceIndex.getOccurrences(entry.getKey());
				
				if(!occurrences.isEmpty())
				{
					for(Occurrence occurrence:occurrences)
						reifiableObjects.add(occurrence);
										
				}else{
					
					// check names
					Collection<Name> names = typeInstanceIndex.getNames(entry.getKey());
					
					if(!names.isEmpty())
					{
						for(Name name:names)
							reifiableObjects.add(name);
						
					}else{ 

						addInvalidConstruct(entry.getKey(), "The constrained topic type has no instances!", invalidConstructs);
					}
					
				}
			}
			
			for(Reifiable reifiableObject:reifiableObjects)
			{
				// get reifier
				Topic reifier = reifiableObject.getReifier();
				boolean reifierTypeFound = false;
				
				for(IConstraint constraint:entry.getValue())
				{
					ReifierConstraint reifier_constraint = (ReifierConstraint)constraint;
					
					if(reifier != null && reifier.getTypes().contains(reifier_constraint.allowedReifier))
					{
						reifierTypeFound = true;
						
						if(reifier_constraint.cardMin == 0 && reifier_constraint.cardMax == 0)
						{
							// cannot-have-reifier
							addInvalidConstruct(reifiableObject, "Object must not have an reifier of type " + getBestName(reifier_constraint.allowedReifier), invalidConstructs);
						}
					
					}else{
						
						if(reifier_constraint.cardMin == 1 && reifier_constraint.cardMax == 1)
						{
							// must-have-reifier
							addInvalidConstruct(reifiableObject, "Object must have an reifier of type " + getBestName(reifier_constraint.allowedReifier), invalidConstructs); 					
						}
					}
				}
				
				if(reifier != null && !reifierTypeFound)
				{
					addInvalidConstruct(reifiableObject, "Object have an unallowed reifier!", invalidConstructs);
				}
			}
		}
	}

	
}
