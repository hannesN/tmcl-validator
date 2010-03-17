/**
 * Copyright: Copyright 2010 Topic Maps Lab, University of Leipzig. http://www.topicmapslab.de/    
 * License:   Apache License, Version 2.0 http://www.apache.org/licenses/LICENSE-2.0.html
 * 
 * @author Christian Haß
 */
package de.topicmapslab.tmclvalidator.tmapi;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.tmapi.core.Construct;
import org.tmapi.core.Name;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Scoped;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicMap;

import de.topicmapslab.tmclvalidator.TMCLValidatorException;
import de.topicmapslab.tmclvalidator.ValidationResult;
import de.topicmapslab.tmclvalidator.tmapi.constraint.IConstraint;
import de.topicmapslab.tmclvalidator.tmapi.constraint.ScopeRequiredConstraint;


/**
 * Validator for the scope required constraint.
 */
public class ScopeRequiredConstraintValidator extends AbstractTMAPIValidator {

	private static final String SCOPE_REQUIRED_CONSTRAINT = "http://psi.topicmaps.org/tmcl/scope-required-constraint";
	private static final String CONSTRAINT_TOPIC_TYPE = "http://psi.topicmaps.org/tmcl/constrained-topic-type";
		
	/**
	 * Constructor
	 * @param id - The validator ID.
	 */
	public ScopeRequiredConstraintValidator(String id, boolean useIdentifierInMessages) {
	    super(id, useIdentifierInMessages);
    }
	
	public void validate(TopicMap mergedTopicMap, Map<Construct, Set<ValidationResult>> invalidConstructs) throws TMCLValidatorException 
	{
		// get constrained types and corresponding constraints
		Map<Topic, Set<IConstraint> > topicAndConstraints = getTopicsAndConstraints(mergedTopicMap, CONSTRAINT_TOPIC_TYPE, SCOPE_REQUIRED_CONSTRAINT);

		for(Map.Entry<Topic, Set<IConstraint>> entry:topicAndConstraints.entrySet())
		{
			
			Topic topic = entry.getKey();

			// counter of scoped occurrences of a specific type with a specific scope
			Map<ScopeRequiredConstraint, Integer> cardinalityCounter = new HashMap<ScopeRequiredConstraint, Integer>();
			
			for(IConstraint constraint:entry.getValue())
			{
				ScopeRequiredConstraint scope_required_constraint = (ScopeRequiredConstraint)constraint;
				
				Set<Scoped> scopedObjects = new HashSet<Scoped>();
				
				// check names
				Set<Name> names = topic.getNames(scope_required_constraint.caracteristicType);
				
				if(!names.isEmpty())
				{
					for(Name name:names)
						scopedObjects.add(name);
					
				}else{
					
					// check occurrences
					Set<Occurrence> occurrences = topic.getOccurrences(scope_required_constraint.caracteristicType);

					if(!occurrences.isEmpty())
					{
						for(Occurrence occurrence:occurrences)
							scopedObjects.add(occurrence);
						
					}else throw new TMCLValidatorException("Validation of the scope required constraint for associations is not yet supported.");
				}
				
				for(Scoped scopedObject:scopedObjects)
				{
					// get set of themes
					Set<Topic> themes = scopedObject.getScope();
					
					if(themes.size() == 1)
					{
						if(themes.iterator().next().equals(scope_required_constraint.scopeInstance))
						{
							// increase cardinality counter
							int count = 0;
							if(cardinalityCounter.get(scope_required_constraint) != null) count = cardinalityCounter.get(scope_required_constraint);
							count++;
							cardinalityCounter.put(scope_required_constraint, count);
						}
					}
				}

			} // end of for(IConstraint constraint:entry.getValue())
			
			// check cardinality
			checkCardinality(invalidConstructs, entry.getValue(), topic, cardinalityCounter);

		} // end of for(Map.Entry<Topic, Set<IConstraint>> entry:topicAndConstraints.entrySet())
	}

	/**
	 * Checks the cardinality.
	 * @param invalidConstructs - Set of invalid constructs.
	 * @param constraints - Set of constraint wrapper.
	 * @param topic - The topic which is checked.
	 * @param cardinalityCounter - The cardinalities.
	 */
    private void checkCardinality(Map<Construct, Set<ValidationResult>> invalidConstructs, Set<IConstraint> constraints, Topic topic, Map<ScopeRequiredConstraint, Integer> cardinalityCounter) throws TMCLValidatorException {
	    
    	for(IConstraint constraint:constraints)
	    {
	    	ScopeRequiredConstraint scope_required_constraint = (ScopeRequiredConstraint)constraint;
	    	
	    	if (scope_required_constraint.cardMin != 0 || scope_required_constraint.cardMax != -1) 
	    	{

	    		int count = 0;
	    		if(cardinalityCounter.get(scope_required_constraint) != null) count = cardinalityCounter.get(scope_required_constraint);
	    		
	    		if (count < scope_required_constraint.cardMin) 
	    		{
	    			addInvalidConstruct(topic, "Number of caracteristics of type " + getBestName(scope_required_constraint.caracteristicType) + " having the scope " + getBestName(scope_required_constraint.scopeInstance) + "[" + count + "] is less then the specified minimum [" + scope_required_constraint.cardMin + "]", invalidConstructs);
	    		}

	    		if (scope_required_constraint.cardMax != -1 && count > scope_required_constraint.cardMax) 
	    		{
	    			addInvalidConstruct(topic, "Number of caracteristics of type " + getBestName(scope_required_constraint.caracteristicType) + " having the scope " + getBestName(scope_required_constraint.scopeInstance) + "[" + count + "] is bigger then the specified maximum [" + scope_required_constraint.cardMax + "]", invalidConstructs);
	    		}
	    	}
	    }
    }
	
	
		
}
