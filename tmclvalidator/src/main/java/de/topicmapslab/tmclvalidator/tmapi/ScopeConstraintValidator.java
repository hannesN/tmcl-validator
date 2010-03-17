/**
 * Copyright: Copyright 2010 Topic Maps Lab, University of Leipzig. http://www.topicmapslab.de/    
 * License:   Apache License, Version 2.0 http://www.apache.org/licenses/LICENSE-2.0.html
 * 
 * @author Christian Haß
 */
package de.topicmapslab.tmclvalidator.tmapi;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.tmapi.core.Association;
import org.tmapi.core.Construct;
import org.tmapi.core.Name;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Scoped;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicMap;
import org.tmapi.index.TypeInstanceIndex;

import de.topicmapslab.tmclvalidator.TMCLValidatorException;
import de.topicmapslab.tmclvalidator.ValidationResult;
import de.topicmapslab.tmclvalidator.tmapi.constraint.IConstraint;
import de.topicmapslab.tmclvalidator.tmapi.constraint.ScopeConstraint;


/**
 * Validator for the scope constraint.
 */
public class ScopeConstraintValidator extends AbstractTMAPIValidator {
	
	
	private static final String SCOPE_CONSTRAINT = "http://psi.topicmaps.org/tmcl/scope-constraint";
	private static final String CONSTRAINT_STATEMENT = "http://psi.topicmaps.org/tmcl/constrained-statement";
	
	/**
	 * Constructor
	 * @param id - The validator ID.
	 */
	public ScopeConstraintValidator(String id, boolean useIdentifierInMessages) {
	    super(id, useIdentifierInMessages);
    }
	
	
	public void validate(TopicMap mergedTopicMap, Map<Construct, Set<ValidationResult>> invalidConstructs) throws TMCLValidatorException 
	{
		TypeInstanceIndex typeInstanceIndex = mergedTopicMap.getIndex(TypeInstanceIndex.class);
		
		// get constrained types and corresponding constraints
		Map<Topic, Set<IConstraint> > typesAndConstraints = getConstructTypesAndConstraints(mergedTopicMap, CONSTRAINT_STATEMENT, SCOPE_CONSTRAINT);

		for(Map.Entry<Topic, Set<IConstraint>> entry:typesAndConstraints.entrySet())
		{
			Collection<Scoped> scopedObjects = new HashSet<Scoped>();
			
			// get instances of scoped types
			Collection<Association> associations = typeInstanceIndex.getAssociations(entry.getKey());
			
			if(!associations.isEmpty())
			{
				// check associations
				for(Association association:associations)
					scopedObjects.add(association);

			}else{
				// check occurrences
				Collection<Occurrence> occurrences = typeInstanceIndex.getOccurrences(entry.getKey());
				
				if(!occurrences.isEmpty())
				{
					for(Occurrence occurrence:occurrences)
						scopedObjects.add(occurrence);

				}else{
					// check names
					Collection<Name> names = typeInstanceIndex.getNames(entry.getKey());
					
					if(!names.isEmpty())
					{
						for(Name name:names)
							scopedObjects.add(name);
					}else{
						addInvalidConstruct(entry.getKey(), "The constrained topic type has no instances!", invalidConstructs); 
					}
				}
			}
			
			// check instances
			for(Scoped scopedObject:scopedObjects)
			{
				Set<Topic> scopeTopics = scopedObject.getScope();
				
				Map<ScopeConstraint, Integer> cardinalityCounter = new HashMap<ScopeConstraint, Integer>();
				
				for(Topic scopeTopic:scopeTopics)
				{
					boolean scopeTopicFound = false;
					
					for(IConstraint constraint:entry.getValue())
					{
						ScopeConstraint scope_constraint = (ScopeConstraint)constraint;
						
						if(scopeTopic.getTypes().contains(scope_constraint.allowedScope))
						{
							scopeTopicFound = true;
							
							int count = 0;
							if(cardinalityCounter.get(scope_constraint) != null) count = cardinalityCounter.get(scope_constraint);
							count++;
							cardinalityCounter.put(scope_constraint, count);
						}
					}
					
					if(!scopeTopicFound)
					{
						addInvalidConstruct(scopedObject, "Object has unallowed scope (" + getBestName(scopeTopic) + ")", invalidConstructs); 
					}
					
				}

				// check cardinality
				checkCardinality(invalidConstructs, entry.getValue(), scopedObject, cardinalityCounter);
			}
		}
	}

	/**
	 * Checks the cardinality.
	 * @param invalidConstructs - Set of invalid constructs.
	 * @param constraints - Set of constraints.
	 * @param scopedObject - The scoped object for which the cardinality is checked.
	 * @param cardinalityCounter - The cardinalities.
	 */
    private void checkCardinality(Map<Construct, Set<ValidationResult>> invalidConstructs, Set<IConstraint> constraints, Scoped scopedObject, Map<ScopeConstraint, Integer> cardinalityCounter) throws TMCLValidatorException {
	    
    	for(IConstraint constraint:constraints)
	    {
	    	
	    	ScopeConstraint scope_constraint = (ScopeConstraint)constraint;
	    	
	    	int count = 0;
	    	if(cardinalityCounter.get(scope_constraint) != null) count = cardinalityCounter.get(scope_constraint);
	    	
	    	if(count < scope_constraint.cardMin)
	    	{
	    		addInvalidConstruct(scopedObject, "Object has too few scope topics of type " + getBestName(scope_constraint.allowedScope), invalidConstructs); 
	    	}
	    	
	    	if(scope_constraint.cardMax != -1 && count > scope_constraint.cardMax)
	    	{
	    		addInvalidConstruct(scopedObject, "Object has too many scope topics of type " + getBestName(scope_constraint.allowedScope), invalidConstructs); 
	    	}
	    }
    }
	
	
}
