/**
 * Copyright: Copyright 2010 Topic Maps Lab, University of Leipzig. http://www.topicmapslab.de/    
 * License:   Apache License, Version 2.0 http://www.apache.org/licenses/LICENSE-2.0.html
 * 
 * @author Christian Haß
 */
package de.topicmapslab.tmclvalidator.tmapi;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.tmapi.core.Construct;
import org.tmapi.core.Name;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicMap;
import org.tmapi.index.LiteralIndex;
import org.tmapi.index.TypeInstanceIndex;

import de.topicmapslab.tmclvalidator.TMCLValidatorException;
import de.topicmapslab.tmclvalidator.ValidationResult;
import de.topicmapslab.tmclvalidator.tmapi.constraint.IConstraint;


/**
 * Validator for the unique value constraint.
 */
public class UniqueValueConstraintValidator extends AbstractTMAPIValidator {

	private static final String UNIQUE_VALUE_CONSTRAINT = "http://psi.topicmaps.org/tmcl/unique-value-constraint";
	private static final String CONSTRAINT_STATEMENT = "http://psi.topicmaps.org/tmcl/constrained-statement";
	
	/**
	 * Constructor
	 * @param id - The validator ID.
	 */
	public UniqueValueConstraintValidator(String id, boolean useIdentifierInMessages) {
	    super(id, useIdentifierInMessages);
    }
	
	public void validate(TopicMap mergedTopicMap, Map<Construct, Set<ValidationResult>> invalidConstructs) throws TMCLValidatorException 
	{
		TypeInstanceIndex typeInstanceIndex = mergedTopicMap.getIndex(TypeInstanceIndex.class);
		
		// get constrained types and corresponding constraints
		Map<Topic, Set<IConstraint> > typesAndConstraints = getConstructTypesAndConstraints(mergedTopicMap, CONSTRAINT_STATEMENT, UNIQUE_VALUE_CONSTRAINT);

		// get literal index
		LiteralIndex literalIndex = mergedTopicMap.getIndex(LiteralIndex.class);
		if(!literalIndex.isOpen()) literalIndex.open();
		
		for(Map.Entry<Topic, Set<IConstraint>> entry:typesAndConstraints.entrySet())
		{
			// check occurrences
			Collection<Occurrence> occurrences = typeInstanceIndex.getOccurrences(entry.getKey());
			
			for(Occurrence occurrence:occurrences)
			{
				Collection<Occurrence> occurrences_with_value = literalIndex.getOccurrences(occurrence.getValue());
				
				if(occurrences_with_value.size() > 1)
				{
					addInvalidConstruct(occurrence, "The Occurrence violates the unique value constraint.", invalidConstructs);
				}
			}

			// check names
			Collection<Name> names = typeInstanceIndex.getNames(entry.getKey());
			
			for(Name name:names)
			{
				Collection<Name> names_with_value = literalIndex.getNames(name.getValue());
				
				if(names_with_value.size() > 1)
				{
					addInvalidConstruct(name, "The Name violates the unique value constraint.", invalidConstructs);
				}
			}
		}
		
		literalIndex.close();
	}
	
	
	
}
