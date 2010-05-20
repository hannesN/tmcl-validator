/**
 * Copyright: Copyright 2010 Topic Maps Lab, University of Leipzig. http://www.topicmapslab.de/    
 * License:   Apache License, Version 2.0 http://www.apache.org/licenses/LICENSE-2.0.html
 * 
 * @author Christian Haß
 */
package de.topicmapslab.tmclvalidator.tmapi;


import java.util.HashSet;
import java.util.Set;

import org.tmapi.core.Topic;
import org.tmapi.core.TopicMap;

import de.topicmapslab.tmclvalidator.IConstraintValidator;
import de.topicmapslab.tmclvalidator.IConstraintValidatorFactory;

/**
 * Factory class which generates TMAPI based validators.
 */
public class TMAPIFactory implements IConstraintValidatorFactory {
		
	private boolean useIdentifierInMessages;
	
	/**
	 * Constructor
	 * @param useIdentifierInMessages - Forces the usage of identifier in result messages.
	 */
	public TMAPIFactory(boolean useIdentifierInMessages) {
		this.useIdentifierInMessages = useIdentifierInMessages;
	}
	
	public Set<IConstraintValidator> getConstraintValidators(TopicMap topicMap) {
	    
		// create empty set
		Set<IConstraintValidator> constraints = new HashSet<IConstraintValidator>();
		
		// add declaration constraint validator
		
		constraints.add(new DeclarationConstraintsValidator(DECLARATION_CONSTRAINT, this.useIdentifierInMessages));
		constraints.add(new OverlapDeclarationValidator(OVERLAY_DECLARATION, this.useIdentifierInMessages));
		
		// add overlay constraint validator
		
		// get constraint validator depending on schema
		for(int i=0;i<constraintIds.length;i++)
		{
			if(isConstraintUsed(topicMap, constraintIds[i])){
				constraints.add(getValidator(constraintIds[i]));
			}
		}
		
	    return constraints;
	}
	
	/**
	 * Check the schema whether a specific constraint is used or not.
	 * @param topicMap - The topic map.
	 * @param constraintId - The constraint ID which is to be checked.
	 * @return True in case the constraint is used, otherwise false.
	 */
	private boolean isConstraintUsed(TopicMap topicMap, String constraintId) {

		Topic constraint_type = topicMap.getTopicBySubjectIdentifier(topicMap.createLocator(constraintId));
		
		if(constraint_type != null) return true;
		return false;
	}
	
	/**
	 * Creates and returns a validator for a specific constraint.
	 * @param constraintId - The ID of the constraint.
	 * @return The validator object.
	 */
	private IConstraintValidator getValidator(String constraintId){
		
		if(constraintId == ABSTRACT_TOPIC_TYPE_CONSTRAINT) return new AbstractTopicTypeConstraintValidator(constraintId, this.useIdentifierInMessages);
		if(constraintId == SUBJECT_IDENTIFIER_CONSTRAINT) return new SubjectIdentifierConstraintValidator(constraintId, this.useIdentifierInMessages);
		if(constraintId == SUBJECT_LOCATOR_CONSTRAINT) return new SubjectLocatorConstraintValidator(constraintId, this.useIdentifierInMessages);
		if(constraintId == TOPIC_NAME_CONSTRAINT) return new TopicNameConstraintValidator(constraintId, this.useIdentifierInMessages);
		if(constraintId == TOPIC_OCCURRENCE_CONSTRAINT) return new TopicOccurranceConstraintValidator(constraintId, this.useIdentifierInMessages);
		if(constraintId == TOPIC_ROLE_CONSTRAINT) return new TopicRoleConstraintValidator(constraintId, this.useIdentifierInMessages);
		if(constraintId == SCOPE_CONSTRAINT) return new ScopeConstraintValidator(constraintId, this.useIdentifierInMessages);
		if(constraintId == SCOPE_REQUIRED_CONSTRAINT) return new ScopeRequiredConstraintValidator(constraintId, this.useIdentifierInMessages);
		if(constraintId == REIFIER_CONSTRAINT) return new ReifierConstraintValidator(constraintId, this.useIdentifierInMessages);
		if(constraintId == TOPIC_REIFIES_CONSTRAINT) return new TopicReifiesConstraintValidator(constraintId, this.useIdentifierInMessages);
		if(constraintId == ASSOCIATION_ROLE_CONSTRAINT) return new AssociationRoleConstraintValidator(constraintId, this.useIdentifierInMessages);
		if(constraintId == ROLE_COMBINATION_CONSTRAINT) return new RoleCombinationConstraintValidator(constraintId,this.useIdentifierInMessages);
		if(constraintId == OCCURRENCE_DATA_TYPE_CONSTRAINT) return new OccurrenceDatatypeConstraintValidator(constraintId, this.useIdentifierInMessages);
		if(constraintId == UNIQUE_VALUE_CONSTRAINT) return new UniqueValueConstraintValidator(constraintId, this.useIdentifierInMessages);
		if(constraintId == REGULAR_EXPRESSION_CONSTRAINT) return new RegularExpressionConstraintValidator(constraintId, this.useIdentifierInMessages);
		if(constraintId == VARIANT_NAME_CONSTRAINT) return new VariantNameConstraintValidator(constraintId, this.useIdentifierInMessages);
		if(constraintId == ITEM_IDENTIFIER_CONSTRAINT) return new ItemIdentifierConstraintValidator(constraintId, this.useIdentifierInMessages);
		
		
		return null;
	}
	
	// definition of constraint ids, i.e. subject identifier
	private final String ABSTRACT_TOPIC_TYPE_CONSTRAINT = "http://psi.topicmaps.org/tmcl/abstract-constraint";
	private final String SUBJECT_IDENTIFIER_CONSTRAINT = "http://psi.topicmaps.org/tmcl/subject-identifier-constraint";
	private final String SUBJECT_LOCATOR_CONSTRAINT = "http://psi.topicmaps.org/tmcl/subject-locator-constraint";
	private final String TOPIC_NAME_CONSTRAINT = "http://psi.topicmaps.org/tmcl/topic-name-constraint";
	private final String TOPIC_OCCURRENCE_CONSTRAINT = "http://psi.topicmaps.org/tmcl/topic-occurrence-constraint";
	private final String TOPIC_ROLE_CONSTRAINT = "http://psi.topicmaps.org/tmcl/topic-role-constraint";
	private final String SCOPE_CONSTRAINT = "http://psi.topicmaps.org/tmcl/scope-constraint";
	private final String SCOPE_REQUIRED_CONSTRAINT = "http://psi.topicmaps.org/tmcl/scope-required-constraint";
	private final String REIFIER_CONSTRAINT = "http://psi.topicmaps.org/tmcl/reifier-constraint";
	private final String TOPIC_REIFIES_CONSTRAINT = "http://psi.topicmaps.org/tmcl/topic-reifies-constraint";
	private final String ASSOCIATION_ROLE_CONSTRAINT = "http://psi.topicmaps.org/tmcl/association-role-constraint";
	private final String ROLE_COMBINATION_CONSTRAINT = "http://psi.topicmaps.org/tmcl/role-combination-constraint";
	private final String OCCURRENCE_DATA_TYPE_CONSTRAINT = "http://psi.topicmaps.org/tmcl/occurrence-datatype-constraint";
	private final String UNIQUE_VALUE_CONSTRAINT = "http://psi.topicmaps.org/tmcl/unique-value-constraint";
	private final String REGULAR_EXPRESSION_CONSTRAINT = "http://psi.topicmaps.org/tmcl/regular-expression-constraint";
	
	private final String DECLARATION_CONSTRAINT = "http://psi.topicmaps.org/tmcl/declaration-constraint";
	private final String OVERLAY_DECLARATION = "http://psi.topicmaps.org/tmcl/overlap-declaration";
	private final String VARIANT_NAME_CONSTRAINT = "http://psi.topicmaps.org/tmcl/variant-name-constraint";
	private final String ITEM_IDENTIFIER_CONSTRAINT = "http://psi.topicmaps.org/tmcl/item-identifier-constraint";
	
	// define an array of constraint ids
	private final String[] constraintIds = { ABSTRACT_TOPIC_TYPE_CONSTRAINT,
												SUBJECT_IDENTIFIER_CONSTRAINT, 
												SUBJECT_LOCATOR_CONSTRAINT,
												TOPIC_NAME_CONSTRAINT,
												TOPIC_OCCURRENCE_CONSTRAINT,
												TOPIC_ROLE_CONSTRAINT,
												SCOPE_CONSTRAINT,
												SCOPE_REQUIRED_CONSTRAINT,
												REIFIER_CONSTRAINT,
												TOPIC_REIFIES_CONSTRAINT,
												ASSOCIATION_ROLE_CONSTRAINT,
												ROLE_COMBINATION_CONSTRAINT,
												OCCURRENCE_DATA_TYPE_CONSTRAINT,
												UNIQUE_VALUE_CONSTRAINT,
												REGULAR_EXPRESSION_CONSTRAINT,
												VARIANT_NAME_CONSTRAINT,
												ITEM_IDENTIFIER_CONSTRAINT};
	

}
