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
import org.tmapi.core.Occurrence;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicMap;
import org.tmapi.index.TypeInstanceIndex;

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
	 */
	public OccurrenceDatatypeConstraintValidator(String id, boolean useIdentifierInMessages) {
	    super(id, useIdentifierInMessages);
    }
	
	public void validate(TopicMap mergedTopicMap, Map<Construct, Set<ValidationResult>> invalidConstructs) throws TMCLValidatorException 
	{
		TypeInstanceIndex typeInstanceIndex = mergedTopicMap.getIndex(TypeInstanceIndex.class);
	
		// get constrained types and corresponding constraints
		Map<Topic, Set<IConstraint> > typesAndConstraints = getConstructTypesAndConstraints(mergedTopicMap, CONSTRAINT_STATEMENT, OCCURRENCE_DATATYPE_CONSTRAINT);

		for(Map.Entry<Topic, Set<IConstraint>> entry:typesAndConstraints.entrySet())
		{
			if(entry.getValue().size() > 1)
				throw new TMCLValidatorException("Type " + getBestName(entry.getKey()) + " has more then one occurrence datatype constraints.");

			// get constraint
			OccurrenceDatatypeConstraint constraint = (OccurrenceDatatypeConstraint)entry.getValue().iterator().next();
			
			// check occurrences
			Collection<Occurrence> occurrences = typeInstanceIndex.getOccurrences(entry.getKey());
			
			for(Occurrence occurrence:occurrences)
			{
				if(!occurrence.getDatatype().getReference().equals(constraint.datatype))
					addInvalidConstruct(occurrence, "The Occurrence of type " + getBestName(entry.getKey()) + " must have the datatype " + constraint.datatype + ".", invalidConstructs);
			}
		}
	}
	
}
