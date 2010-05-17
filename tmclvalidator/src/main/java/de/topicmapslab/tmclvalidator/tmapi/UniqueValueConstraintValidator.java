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
	 * @param useIdentifierInMessages - Forces the usage of identifier in result messages.
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
			checkConstructOfUniqueness(new HashSet<Construct>(occurrences), invalidConstructs);

			// check names
			Collection<Name> names = typeInstanceIndex.getNames(entry.getKey());
			checkConstructOfUniqueness(new HashSet<Construct>(names), invalidConstructs);
			
		}
		
		literalIndex.close();
	}
	
	/**
	 * Checks if a number of constructs are unique with respect to their values.
	 * @param constructs - Set of constructs.
	 * @param invalidConstructs - The result set.
	 * @throws TMCLValidatorException
	 */
	private void checkConstructOfUniqueness(Set<Construct> constructs, Map<Construct, Set<ValidationResult>> invalidConstructs) throws TMCLValidatorException{
		
		Map<String, Construct> checkMap = new HashMap<String, Construct>();
		Map<String,Set<Construct>> invalidConstructMap = new HashMap<String, Set<Construct>>();
		
		for(Construct construct:constructs){
			
			String value;
			
			if(construct instanceof Occurrence)
				value = ((Occurrence)construct).getValue();
			else value = ((Name)construct).getValue();
			
			Construct prevConstruct = null;
			
			if((prevConstruct = checkMap.put(value, construct)) != null){
				// value is not unique
				
				Set<Construct> ununiqeConstructs = null;
				
				if((ununiqeConstructs = invalidConstructMap.get(value)) == null){
					ununiqeConstructs = new HashSet<Construct>();
					ununiqeConstructs.add(prevConstruct);
				}
				
				ununiqeConstructs.add(construct);
				invalidConstructMap.put(value, ununiqeConstructs);
			}
		}
		
		for(Map.Entry<String, Set<Construct>>entry:invalidConstructMap.entrySet()){

			for(Construct invalidConstruct:entry.getValue()){
				
				if(invalidConstruct instanceof Occurrence){
					addInvalidConstruct(invalidConstruct, "The value '" + entry.getKey() + "' of the occurrence violates the unique value constraint.", invalidConstructs);
				}else{
					addInvalidConstruct(invalidConstruct, "The value '" + entry.getKey() + "' of the name construct violates the unique value constraint.", invalidConstructs);
				}
			}
			
		}
		
	}
	
	
	
}
