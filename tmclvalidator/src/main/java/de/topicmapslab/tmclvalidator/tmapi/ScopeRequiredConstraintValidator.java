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
import de.topicmapslab.tmclvalidator.tmapi.constraint.ScopeRequiredConstraint;


/**
 * Validator for the scope required constraint.
 */
public class ScopeRequiredConstraintValidator extends AbstractTMAPIValidator {

	private static final String SCOPE_REQUIRED_CONSTRAINT = "http://psi.topicmaps.org/tmcl/scope-required-constraint";
	private static final String CONSTRAINT_TOPIC_TYPE = "http://psi.topicmaps.org/tmcl/constrained-topic-type";
	
	/**
	 * Constructor
	 * @param id - The validator ID.
	 * @param useIdentifierInMessages - Forces the usage of identifier in result messages.
	 */
	public ScopeRequiredConstraintValidator(String id, boolean useIdentifierInMessages) {
	    super(id, useIdentifierInMessages);
    }
	
	public void validate(TopicMap mergedTopicMap, Map<Construct, Set<ValidationResult>> invalidConstructs) throws TMCLValidatorException 
	{
		Map<IConstraint, Topic> constraintsAndTypes = getConstraintsAndTypes(mergedTopicMap, CONSTRAINT_TOPIC_TYPE, SCOPE_REQUIRED_CONSTRAINT);
		
		for(Map.Entry<IConstraint, Topic> entry:constraintsAndTypes.entrySet()){
			
			Topic scopeInstance = ((ScopeRequiredConstraint)entry.getKey()).scopeInstance;
			Topic caracteristicType = ((ScopeRequiredConstraint)entry.getKey()).caracteristicType;
			int cardMin = ((ScopeRequiredConstraint)entry.getKey()).cardMin;
			int cardMax = ((ScopeRequiredConstraint)entry.getKey()).cardMax;
			
			Set<Topic> instances = getTopics(entry.getValue());
			
			for(Topic instance:instances){
				
				// check names
				Set<Name> names = getTopicNames(instance, caracteristicType);
				
				if(!names.isEmpty()){
					
					checkScope(new HashSet<Scoped>(names), scopeInstance, cardMin, cardMax, invalidConstructs, "names", instance);
					
				}else{
					
					// check occurrences
					Set<Occurrence> occurrences = getTopicOccurrences(instance, caracteristicType);
					
					if(!occurrences.isEmpty()){
						
						checkScope(new HashSet<Scoped>(occurrences), scopeInstance, cardMin, cardMax, invalidConstructs, "occurrences", instance);
						
					}else{
						
						// check associations
						Set<Association> associations = getTopicAssociations(instance, caracteristicType);
						
						if(!associations.isEmpty()){
							
							checkScope(new HashSet<Scoped>(associations), scopeInstance, cardMin, cardMax, invalidConstructs, "associations", instance);
							
						}else{
							
							if(cardMin > 0)
								addInvalidConstruct(instance, "Topic needs to have min " + cardMin + " names, occurrences or associations with a scope containing the theme " + getBestName(scopeInstance), invalidConstructs);
							
						}
						
					}
				}
			}
		}
	}
	
	/**
	 * Check if the scoped objects are valid.
	 * @param scopedObjects - Set of scoped objects.
	 * @param scopeInstance - The required scope.
	 * @param cardMin - Minimum cardinality.
	 * @param cardMax - Maximum cardinality.
	 * @param invalidConstructs - The result set.
	 * @param name - The construct name, Name, Occurrence, or Association.
	 * @param topic - The topic to which the scoped objects belong.
	 * @throws TMCLValidatorException
	 */
	private void checkScope(Set<Scoped> scopedObjects, Topic scopeInstance, int cardMin, int cardMax, Map<Construct, Set<ValidationResult>> invalidConstructs, String name, Topic topic) throws TMCLValidatorException{
		
		int count = 0;
		
		for(Scoped scopedObject:scopedObjects){
			
			Set<Topic> scope = scopedObject.getScope();
			for(Topic theme:scope)
				if(theme.equals(scopeInstance))
					count++;
		}
		
		if(cardMin > count)
			addInvalidConstruct(topic, "Topic has too few " + name + " with a scope having the theme " + getBestName(scopeInstance) + " [" + count + " of " + cardMin + "]", invalidConstructs);
		
		if(cardMax != -1 && cardMax < count)
			addInvalidConstruct(topic, "Topic has too many " + name + " with a scope having the theme " + getBestName(scopeInstance) + " [" + count + " of " + cardMax + "]", invalidConstructs);
	}
		

	
}
