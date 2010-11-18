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
import org.tmapi.core.Name;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicMap;

import de.topicmapslab.tmclvalidator.TMCLValidatorException;
import de.topicmapslab.tmclvalidator.ValidationResult;
import de.topicmapslab.tmclvalidator.tmapi.constraint.IConstraint;
import de.topicmapslab.tmclvalidator.tmapi.constraint.RegularExpressionConstraint;


/**
 * Validator for the regular expression constraint.
 */
public class RegularExpressionConstraintValidator extends AbstractTMAPIValidator {

	private static final String REGULAR_EXPRESSION_CONSTRAINT = "http://psi.topicmaps.org/tmcl/regular-expression-constraint";
	private static final String CONSTRAINT_STATEMENT = "http://psi.topicmaps.org/tmcl/constrained-statement";
	
	/**
	 * Constructor
	 * @param id - The validator ID.
	 * @param useIdentifierInMessages - Forces the usage of identifier in result messages.
	 */
	public RegularExpressionConstraintValidator(String id, boolean useIdentifierInMessages) {
	    super(id, useIdentifierInMessages);
    }
	
	public void validate(TopicMap mergedTopicMap, Map<Construct, Set<ValidationResult>> invalidConstructs) throws TMCLValidatorException 
	{
		Map<IConstraint,Topic> constraintsAndTypes = getConstraintsAndTypes(mergedTopicMap, CONSTRAINT_STATEMENT, REGULAR_EXPRESSION_CONSTRAINT);
		
		for(Map.Entry<IConstraint, Topic> entry:constraintsAndTypes.entrySet()){
			
			String regEx = ((RegularExpressionConstraint)entry.getKey()).regExp;
			
			// check names
			Set<Name> names = getNames(entry.getValue());
			
			for(Name name:names){
				if(!name.getValue().matches(regEx))
					addInvalidConstruct(name, "Name '" + name.getValue() + "' doesn't match the regular expression '" + regEx + "'.", invalidConstructs);
			}
			
			// check occurrences
			Set<Occurrence> occurrences = getOccurrences(entry.getValue());
			
			for(Occurrence occurrence:occurrences){
				if(!occurrence.getValue().matches(regEx))
					addInvalidConstruct(occurrence, "Occurrence doesn't match the regular expression '" + regEx + "'.", invalidConstructs);
			}
		}
	}
	
	
}
