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
import org.tmapi.core.Name;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicMap;
import org.tmapi.core.Variant;

import de.topicmapslab.tmclvalidator.TMCLValidatorException;
import de.topicmapslab.tmclvalidator.ValidationResult;
import de.topicmapslab.tmclvalidator.tmapi.constraint.IConstraint;
import de.topicmapslab.tmclvalidator.tmapi.constraint.VariantNameConstraint;

/**
 * Validator for the variant name constraint.
 */
public class VariantNameConstraintValidator extends AbstractTMAPIValidator {
	
	private static final String VARIANT_NAME_CONSTRAINT = "http://psi.topicmaps.org/tmcl/variant-name-constraint";
	private static final String CONSTRAINT_TOPIC_TYPE = "http://psi.topicmaps.org/tmcl/constrained-topic-type";
	
	/**
	 * Constructor
	 * @param id - The validator ID.
	 * @param useIdentifierInMessages - Forces the usage of identifier in result messages.
	 */
	public VariantNameConstraintValidator(String id, boolean useIdentifierInMessages) {
		super(id, useIdentifierInMessages);
	}
	
	public void validate(TopicMap mergedTopicMap, Map<Construct, Set<ValidationResult>> invalidConstructs) throws TMCLValidatorException {
	   
		Map<IConstraint,Topic> constraintsAndTypes = getConstraintsAndTypes(mergedTopicMap, CONSTRAINT_TOPIC_TYPE, VARIANT_NAME_CONSTRAINT);
		
		for(Map.Entry<IConstraint, Topic> entry:constraintsAndTypes.entrySet()){
			
			int cardMin = ((VariantNameConstraint)entry.getKey()).cardMin;
			int cardMax = ((VariantNameConstraint)entry.getKey()).cardMax;
			Topic nameType = ((VariantNameConstraint)entry.getKey()).nameType;
			Topic scopeTopic = ((VariantNameConstraint)entry.getKey()).scopeTopic;
			
			if(nameType == null)
				throw new TMCLValidatorException("No nametype found for variant name constraint.");
			
			if(scopeTopic == null)
				throw new TMCLValidatorException("No scope topic found for variant name constraint.");
			
			for(Topic instance:getTopics(entry.getValue())){
				
				for(Name name:getTopicNames(instance, nameType)){
					
					Set<Variant> variants = getScopedVariants(name, scopeTopic);
				
					if(cardMin > variants.size())
						addInvalidConstruct(instance, "The name of type " + getBestName(nameType) + " of the topic has too few variants with the theme " + getBestName(scopeTopic) + " [" + variants.size() + " of min " + cardMin + "]", invalidConstructs);
					
					if(cardMax != -1 && cardMax < variants.size())
						addInvalidConstruct(instance, "The name of type " + getBestName(nameType) + " of the topic has too many variants with the theme " + getBestName(scopeTopic) + " [" + variants.size() + " of max " + cardMax + "]", invalidConstructs);

				}
				
			}
			
		}
	}
	
	/**
	 * Returns the variants of a name which have a specific theme.
	 * @param name - The name.
	 * @param variantTheme - The theme.
	 * @return Set of variants. Can be empty but is never be null.
	 */
	private Set<Variant> getScopedVariants(Name name, Topic variantTheme){
		
		Set<Variant> result = new HashSet<Variant>();
		
		Set<Topic> nameScope = name.getScope();
		
		for(Variant variant:name.getVariants()){
			
			Set<Topic> scope = variant.getScope();
			
			if(scope.containsAll(nameScope) && scope.contains(variantTheme)){
				
				result.add(variant);
			}
			
		}
		
		return result;
		
	}
	

}
