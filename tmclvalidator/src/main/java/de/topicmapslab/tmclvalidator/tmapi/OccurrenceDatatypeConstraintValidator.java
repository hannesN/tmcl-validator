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
import org.tmapi.core.Occurrence;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicMap;

import de.topicmapslab.tmclvalidator.TMCLValidatorException;
import de.topicmapslab.tmclvalidator.ValidationResult;
import de.topicmapslab.tmclvalidator.tmapi.constraint.IConstraint;
import de.topicmapslab.tmclvalidator.tmapi.constraint.OccurrenceDatatypeConstraint;


/**
 * Validator for the occurrence datatype constraint.
 */
public class OccurrenceDatatypeConstraintValidator extends AbstractTMAPIValidator {

	private static final String OCCURRENCE_DATATYPE_CONSTRAINT = "http://psi.topicmaps.org/tmcl/occurrence-datatype-constraint";
	private static final String CONSTRAINT_STATEMENT = "http://psi.topicmaps.org/tmcl/constrained-statement";
	
	/**
	 * Constructor
	 * @param id - The validator ID.
	 * @param useIdentifierInMessages - Forces the usage of identifier in result messages.
	 */
	public OccurrenceDatatypeConstraintValidator(String id, boolean useIdentifierInMessages) {
	    super(id, useIdentifierInMessages);
    }
	
	public void validate(TopicMap mergedTopicMap, Map<Construct, Set<ValidationResult>> invalidConstructs) throws TMCLValidatorException 
	{
		Map<IConstraint,Topic> constraintsAndTypes = getConstraintsAndTypes(mergedTopicMap, CONSTRAINT_STATEMENT, OCCURRENCE_DATATYPE_CONSTRAINT);
				
		for(Map.Entry<IConstraint, Topic> entry:constraintsAndTypes.entrySet()){
			
			String datatype = ((OccurrenceDatatypeConstraint)entry.getKey()).datatype;
			
			if(datatype.equals("http://www.w3.org/2001/XMLSchema#anyType"))
				return;
			
			Set<Occurrence> occurrences = getOccurrences(entry.getValue());
			
			for(Occurrence occurrence:occurrences){
				if(!occurrence.getDatatype().getReference().equals(datatype))
					addInvalidConstruct(occurrence, "Occurrences of type " + getBestName(entry.getValue()) + " must have the datatype " + datatype + ".", invalidConstructs);
			}
		}
	}
	
}
