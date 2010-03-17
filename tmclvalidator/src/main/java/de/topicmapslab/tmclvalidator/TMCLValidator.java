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
	 * The used topic map schema.
	 */
	private final TopicMap schema;
	
	/**
	 * Constructor for using the default validator factory.
	 * @param schema - The topic maps schema.
	 */
	public TMCLValidator(TopicMap schema) {
		this.schema = schema;
		factory = new TMAPIFactory(false);
	}
	
	/**
	 * Constructor for using the default validator factory.
	 * @param schema - The topic maps schema.
	 * @param useIdentifierInMessages - Flag to force the usage of full identifier in error messages instead of names.
	 */
	public TMCLValidator(TopicMap schema, boolean useIdentifierInMessages) {
		this.schema = schema;
		factory = new TMAPIFactory(useIdentifierInMessages); 
	}

	/**
	 * Constructor for using a different factory, e.g. TMQL
	 * @param schema - The topic maps schema.
	 * @param factory - The validator factory which should be used.
	 */
	public TMCLValidator(TopicMap schema, IConstraintValidatorFactory factory) {
		this.schema = schema;
		this.factory = factory;
	}

	/**
	 * Method to executed the validation of an specific topic maps construct.
	 * Note: For the moment, always the whole topic maps will be validated.
	 * @param construct - The topic map construct which will be validated.
	 * @return A set of invalid constructs and the corresponding constraint validator results. Returns an empty map if topic map is valid.
	 * @throws TMAPIException 
	 */
	public Map<Construct, Set<ValidationResult>> validate(Construct construct) throws TMCLValidatorException, TMAPIException {

		// create merged topic map
	
		TopicMap mergedTopicMap = merge(construct);

		// create result set
		Map<Construct, Set<ValidationResult>> resultSet = new HashMap<Construct, Set<ValidationResult>>();
		resultSet.clear();
		
		// get validators
		Set<IConstraintValidator> validatorSet = factory.getConstraintValidators(this.schema);

		// call each validator
		Iterator<IConstraintValidator> it = validatorSet.iterator();

		while (it.hasNext()) 
		{
			 it.next().validate(mergedTopicMap, resultSet);
		}
		
		return resultSet;
	}

	/**
	 * Merges the construct topic map with the schema.
	 * @param construct - The to validating construct.
	 * @return A merged topic map. 
	 */
	private TopicMap merge(Construct construct) {
		
		TopicMap map = construct.getTopicMap();
		
		map.mergeIn(this.schema);

		return map;
	}
	
}
