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
import org.tmapi.core.Reifiable;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicMap;

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
	 * @param useIdentifierInMessages - Forces the usage of identifier in result messages.
	 */
	public ReifierConstraintValidator(String id, boolean useIdentifierInMessages) {
	    super(id, useIdentifierInMessages);
    }
		
	public void validate(TopicMap mergedTopicMap, Map<Construct, Set<ValidationResult>> invalidConstructs) throws TMCLValidatorException 
	{
		
		Map<IConstraint, Topic> constraintsAndTypes = getConstraintsAndTypes(mergedTopicMap, CONSTRAINT_STATEMENT, REIFIER_CONSTRAINT);
		
		for(Map.Entry<IConstraint, Topic> entry:constraintsAndTypes.entrySet()){
			
			Topic reifierType = ((ReifierConstraint)entry.getKey()).allowedReifier;
			int cardMin = ((ReifierConstraint)entry.getKey()).cardMin;
			int cardMax = ((ReifierConstraint)entry.getKey()).cardMax;
			
			
			// check associations
			for(Association association:getAssociations(entry.getValue()))
				checkConstruct((Reifiable)association, reifierType, cardMin, cardMax, "association", invalidConstructs);
						
			// check names
			for(Name name:getNames(entry.getValue()))
				checkConstruct((Reifiable)name, reifierType, cardMin, cardMax, "name", invalidConstructs);
			
			// check occurrences
			for(Occurrence occurrence:getOccurrences(entry.getValue()))
				checkConstruct((Reifiable)occurrence, reifierType, cardMin, cardMax, "occurrence", invalidConstructs);
			
		}
	}

	/**
	 * Checks if the reifiable construct is valid.
	 * @param reifiable - The reifiable construct.
	 * @param exspectedType - The supposed type.
	 * @param cardMin - Minimum cardinality.
	 * @param cardMax - Maximum cardinality.
	 * @param name - Name of the construct, Name, Occurrence or Association
	 * @param invalidConstructs - Result set.
	 * @throws TMCLValidatorException
	 */
	private void checkConstruct(Reifiable reifiable, Topic exspectedType, int cardMin, int cardMax, String name, Map<Construct, Set<ValidationResult>> invalidConstructs) throws TMCLValidatorException{
		
		Topic reifier = reifiable.getReifier();
		
		if(reifier == null){
			
			if(cardMin > 0){
				addInvalidConstruct(reifiable, "The " + name + " needs to have a reifier of type " + getBestName(exspectedType), invalidConstructs);
				return;
			}
			
		}else{
			
			if(cardMax < 1){
				addInvalidConstruct(reifiable, "The " + name + " musst not have a reifier", invalidConstructs);
				return;
			}
			
			Set<Topic> reifierTypes = reifier.getTypes();
			
			if(!reifierTypes.contains(exspectedType))
				addInvalidConstruct(reifiable, "The reifier of the " + name + " needs to be of type " + getBestName(exspectedType), invalidConstructs);
		}
	}
	
}


