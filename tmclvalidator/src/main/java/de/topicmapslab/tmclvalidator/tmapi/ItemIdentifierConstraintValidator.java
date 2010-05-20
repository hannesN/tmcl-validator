/**
 * Copyright: Copyright 2010 Topic Maps Lab, University of Leipzig. http://www.topicmapslab.de/    
 * License:   Apache License, Version 2.0 http://www.apache.org/licenses/LICENSE-2.0.html
 * 
 * @author Christian Haß
 */
package de.topicmapslab.tmclvalidator.tmapi;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.tmapi.core.Construct;
import org.tmapi.core.Locator;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicMap;

import de.topicmapslab.tmclvalidator.TMCLValidatorException;
import de.topicmapslab.tmclvalidator.ValidationResult;
import de.topicmapslab.tmclvalidator.tmapi.constraint.IConstraint;
import de.topicmapslab.tmclvalidator.tmapi.constraint.ItemIdentifierConstraint;

/**
 * Validator for the item identifier constraint.
 */
public class ItemIdentifierConstraintValidator extends AbstractTMAPIValidator {

	private static final String ITEM_IDENTIFIER_CONSTRAINT = "http://psi.topicmaps.org/tmcl/item-identifier-constraint";
	private static final String CONSTRAINED_CONSTRUCT = "http://psi.topicmaps.org/tmcl/constrained-construct";

	/**
	 * Constructor
	 * @param id - The validator ID.
	 * @param useIdentifierInMessages - Forces the usage of identifier in result messages.
	 */
	public ItemIdentifierConstraintValidator(String id, boolean useIdentifierInMessages) {
	    super(id, useIdentifierInMessages);
    }
		
	public void validate(TopicMap mergedTopicMap, Map<Construct, Set<ValidationResult>> invalidConstructs) throws TMCLValidatorException
	{
		Map<IConstraint, Topic> constraintsAndTypes = getConstraintsAndTypes(mergedTopicMap, CONSTRAINED_CONSTRUCT, ITEM_IDENTIFIER_CONSTRAINT);
		
		for(Map.Entry<IConstraint, Topic> entry:constraintsAndTypes.entrySet()){
			
			String regExp = ((ItemIdentifierConstraint)entry.getKey()).regExp;
			int cardMin = ((ItemIdentifierConstraint)entry.getKey()).cardMin;
			int cardMax = ((ItemIdentifierConstraint)entry.getKey()).cardMax;
			
			// check topics
			checkItemIdentifier(new HashSet<Construct>(getTopics(entry.getValue())), regExp, cardMin, cardMax, invalidConstructs);
			
			// check names
			checkItemIdentifier(new HashSet<Construct>(getNames(entry.getValue())), regExp, cardMin, cardMax, invalidConstructs);
			
			// check occurrences
			checkItemIdentifier(new HashSet<Construct>(getOccurrences(entry.getValue())), regExp, cardMin, cardMax, invalidConstructs);
			
			// check associations
			checkItemIdentifier(new HashSet<Construct>(getAssociations(entry.getValue())), regExp, cardMin, cardMax, invalidConstructs);
			
			// check roles
			checkItemIdentifier(new HashSet<Construct>(getRoles(entry.getValue())), regExp, cardMin, cardMax, invalidConstructs);

		}
	}
	
	/**
	 * Validates each construct against the specified parameter
	 * @param constructs - Set of constructs
	 * @param regExp - The regular expression
	 * @param cardMin - Minimum cardinality
	 * @param cardMax - Maximum cardinality
	 * @param invalidConstructs - Result set
	 * @throws TMCLValidatorException
	 */
	private void checkItemIdentifier(Set<Construct> constructs, String regExp, int cardMin, int cardMax, Map<Construct, Set<ValidationResult>> invalidConstructs) throws TMCLValidatorException{
		
		for(Construct construct:constructs){
			
			Set<Locator> ii = construct.getItemIdentifiers();
			
			if(cardMin > ii.size())
				addInvalidConstruct(construct, "The construct has too few item identifiers [" + ii.size() + " of min " + cardMin + "]", invalidConstructs);
			
			if(cardMax != -1 && cardMax < ii.size())
				addInvalidConstruct(construct, "The construct has too many item identifiers [" + ii.size() + " of max " + cardMin + "]", invalidConstructs);
		
			// check regex
			for(Locator l:ii)
				if(!l.getReference().matches(regExp))
					addInvalidConstruct(construct, "The item identifier '" + l.getReference() +  "' doesn't match the regular expression '" + regExp + "'", invalidConstructs);
	
			
		}
		
	}
	
}
