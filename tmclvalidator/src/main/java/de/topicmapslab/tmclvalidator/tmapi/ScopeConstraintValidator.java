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
import org.tmapi.core.Name;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Scoped;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicMap;

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
		Map<IConstraint, Topic> constraintsAndTypes = getConstraintsAndTypes(mergedTopicMap, CONSTRAINT_STATEMENT, SCOPE_CONSTRAINT);
		
		for(Map.Entry<IConstraint, Topic> entry:constraintsAndTypes.entrySet()){
		
			Topic scopeType = ((ScopeConstraint)entry.getKey()).allowedScope;
			int cardMin = ((ScopeConstraint)entry.getKey()).cardMin;
			int cardMax = ((ScopeConstraint)entry.getKey()).cardMax;
			
			/// TODO draft says 'or' so use if else instead?
			
			// check names
			for(Name name:getNames(entry.getValue()))
				checkScope(name, scopeType, cardMin, cardMax, invalidConstructs, "name");
						
			// check occurrences
			for(Occurrence occurrence:getOccurrences(entry.getValue()))
				checkScope(occurrence, scopeType, cardMin, cardMax, invalidConstructs, "occurrence");
			
			// check associations
			for(Association association:getAssociations(entry.getValue()))
				checkScope(association, scopeType, cardMin, cardMax, invalidConstructs, "association");
			
		}
	}

	private void checkScope(Scoped scopedObject, Topic scopeType, int cardMin, int cardMax, Map<Construct, Set<ValidationResult>> invalidConstructs, String name) throws TMCLValidatorException{
		
		Set<Topic> scope = scopedObject.getScope();
		
		int count = 0;
		
		for(Topic theme:scope)
			if(theme.getTypes().contains(scopeType))
				count++;
		
		if(cardMin > count)
			addInvalidConstruct(scopedObject, "The scope of the " + name + " contains to few topics of type " + getBestName(scopeType) + " [" + count + " of min " + cardMin + "]", invalidConstructs);
			
		if(cardMax != -1 && cardMax < count)
			addInvalidConstruct(scopedObject, "The scope of the " + name + " contains to many topics of type " + getBestName(scopeType) + " [" + count + " of max " + cardMin + "]", invalidConstructs);
	}
	
}
