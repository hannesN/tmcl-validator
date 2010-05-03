/**
 * Copyright: Copyright 2010 Topic Maps Lab, University of Leipzig. http://www.topicmapslab.de/    
 * License:   Apache License, Version 2.0 http://www.apache.org/licenses/LICENSE-2.0.html
 * 
 * @author Christian Haß
 */
package de.topicmapslab.tmclvalidator;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.tmapi.core.Construct;
import org.tmapi.core.TMAPIException;
import org.tmapi.core.TopicMap;

import de.topicmapslab.tmclvalidator.tmapi.TMAPIFactory;

/**
 * Main class of the TMCL validator
 * 
 */
public class TMCLValidator {

	/**
	 * The used factory.
	 */
	private IConstraintValidatorFactory factory;
	
	/**
	 * Constructor for using the default validator factory.
	 */
	public TMCLValidator() {
		factory = new TMAPIFactory(false);
	}
	
	/**
	 * Constructor for using the default validator factory.
	 * @param useIdentifierInMessages - Flag to force the usage of full identifier in error messages instead of names.
	 */
	public TMCLValidator(boolean useIdentifierInMessages) { // TODO if one more parameter is necessary, create a property handling
		factory = new TMAPIFactory(useIdentifierInMessages); 
	}

	/**
	 * Constructor for using a different factory, e.g. TMQL
	 * @param factory - The validator factory which should be used.
	 */
	public TMCLValidator(IConstraintValidatorFactory factory) {
		this.factory = factory;
	}
	
	/**
	 * Method to executed the validation of an topic maps against a schema.
	 * @param topicMap - The topic map which is to be validated. Note: The topic map need to contain the schema information.
	 * @return A set of invalid constructs and the corresponding constraint validator results. Returns an empty map if topic map is valid.
	 * @throws TMAPIException
	 */
	public Map<Construct, Set<ValidationResult>> validate(TopicMap topicMap) throws TMCLValidatorException{
		
		// create result set
		Map<Construct, Set<ValidationResult>> resultSet = new HashMap<Construct, Set<ValidationResult>>();
		resultSet.clear();
		
		// get validators
		Set<IConstraintValidator> validatorSet = factory.getConstraintValidators(topicMap);
		
		// call each validator
		Iterator<IConstraintValidator> it = validatorSet.iterator();
		
		while (it.hasNext()) 
		{
			 it.next().validate(topicMap, resultSet);
		}
		
		return resultSet;
	}
	
	/**
	 * Method to executed the validation of an topic maps against a schema.
	 * @param topicMap - The topic map which is to be validated.
	 * @param schema - The schema topic map. Note: This schema will be merge into the topic map unsing the mergeIn method of the used TMAPI implementation.	
	 * @return A set of invalid constructs and the corresponding constraint validator results. Returns an empty map if topic map is valid.
	 * @throws TMAPIException
	 */
	public Map<Construct, Set<ValidationResult>> validate(TopicMap topicMap, TopicMap schema) throws TMCLValidatorException{
		
		topicMap.mergeIn(schema);
		return validate(topicMap);
	}
	
}
