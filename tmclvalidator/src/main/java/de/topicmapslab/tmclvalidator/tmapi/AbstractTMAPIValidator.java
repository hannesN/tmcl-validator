/**
 * Copyright: Copyright 2010 Topic Maps Lab, University of Leipzig. http://www.topicmapslab.de/    
 * License:   Apache License, Version 2.0 http://www.apache.org/licenses/LICENSE-2.0.html
 * 
 * @author Christian Haß
 */
package de.topicmapslab.tmclvalidator.tmapi;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmapi.core.Construct;
import org.tmapi.core.Locator;
import org.tmapi.core.Name;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicMap;
import org.tmapi.index.TypeInstanceIndex;

import de.topicmapslab.tmclvalidator.IConstraintValidator;
import de.topicmapslab.tmclvalidator.TMCLValidatorException;
import de.topicmapslab.tmclvalidator.ValidationResult;
import de.topicmapslab.tmclvalidator.tmapi.constraint.AbstractTopicTypeConstraint;
import de.topicmapslab.tmclvalidator.tmapi.constraint.AssociationRoleConstraint;
import de.topicmapslab.tmclvalidator.tmapi.constraint.IConstraint;
import de.topicmapslab.tmclvalidator.tmapi.constraint.OccurrenceDatatypeConstraint;
import de.topicmapslab.tmclvalidator.tmapi.constraint.RegularExpressionConstraint;
import de.topicmapslab.tmclvalidator.tmapi.constraint.ReifierConstraint;
import de.topicmapslab.tmclvalidator.tmapi.constraint.RoleCombinationConstraint;
import de.topicmapslab.tmclvalidator.tmapi.constraint.ScopeConstraint;
import de.topicmapslab.tmclvalidator.tmapi.constraint.ScopeRequiredConstraint;
import de.topicmapslab.tmclvalidator.tmapi.constraint.SubjectIdentifierConstraint;
import de.topicmapslab.tmclvalidator.tmapi.constraint.SubjectLocatorConstraint;
import de.topicmapslab.tmclvalidator.tmapi.constraint.TopicNameConstraint;
import de.topicmapslab.tmclvalidator.tmapi.constraint.TopicOccurrenceConstraint;
import de.topicmapslab.tmclvalidator.tmapi.constraint.TopicReifiesConstraint;
import de.topicmapslab.tmclvalidator.tmapi.constraint.TopicRoleConstraint;
import de.topicmapslab.tmclvalidator.tmapi.constraint.UniqueValueConstraint;
import de.topicmapslab.tmclvalidator.tmapi.utils.Utils;

/**
 * Abstract class which implements some common functionalities used by the validators. 
 */
public abstract class AbstractTMAPIValidator implements IConstraintValidator {

	/**
	 * The validator ID, i.e. the constraint ID.
	 */
	private boolean useIdentifierInMessages;
	
	protected static Logger logger = LoggerFactory.getLogger(AbstractTMAPIValidator.class);
	
	protected String id;
	
	protected static final String CARD_MIN = "http://psi.topicmaps.org/tmcl/card-min";
	protected static final String CARD_MAX = "http://psi.topicmaps.org/tmcl/card-max";

	private static final String ABSTRACT_TOPIC_TYPE_CONSTRAINT = "http://psi.topicmaps.org/tmcl/abstract-constraint";
	private static final String SUBJECT_IDENTIFIER_CONSTRAINT = "http://psi.topicmaps.org/tmcl/subject-identifier-constraint";
	private static final String SUBJECT_LOCATOR_CONSTRAINT = "http://psi.topicmaps.org/tmcl/subject-locator-constraint";
	private static final String TOPIC_NAME_CONSTRAINT = "http://psi.topicmaps.org/tmcl/topic-name-constraint";
	private static final String TOPIC_OCCURRENCE_CONSTRAINT = "http://psi.topicmaps.org/tmcl/topic-occurrence-constraint";
	private static final String TOPIC_ROLE_CONSTRAINT = "http://psi.topicmaps.org/tmcl/topic-role-constraint";
	private static final String SCOPE_CONSTRAINT = "http://psi.topicmaps.org/tmcl/scope-constraint";
	private static final String SCOPE_REQUIRED_CONSTRAINT = "http://psi.topicmaps.org/tmcl/scope-required-constraint";
	private static final String REIFIER_CONSTRAINT = "http://psi.topicmaps.org/tmcl/reifier-constraint";
	private static final String TOPIC_REIFIES_CONSTRAINT = "http://psi.topicmaps.org/tmcl/topic-reifies-constraint";
	private static final String ASSOCIATION_ROLE_CONSTRAINT = "http://psi.topicmaps.org/tmcl/association-role-constraint";
	private static final String ROLE_COMBINATION_CONSTRAINT = "http://psi.topicmaps.org/tmcl/role-combination-constraint";
	private static final String OCCURRENCE_DATA_TYPE_CONSTRAINT = "http://psi.topicmaps.org/tmcl/occurrence-datatype-constraint";
	private static final String UNIQUE_VALUE_CONSTRAINT = "http://psi.topicmaps.org/tmcl/unique-value-constraint";
	private static final String REGULAR_EXPRESSION_CONSTRAINT = "http://psi.topicmaps.org/tmcl/regular-expression-constraint";

	/**
	 * Constructor
	 * @param id - The validator ID.
	 */
	public AbstractTMAPIValidator(String id, boolean useIdentifierInMessages) {
		this.id = id;
		this.useIdentifierInMessages = useIdentifierInMessages;
	}

	/**
	 * Adds an invalid topic map construct and the corresponding error message to the result set.
	 * @param construct - The invalid construct.
	 * @param message - The error message.
	 * @param invalidConstructs - The result set
	 */
    protected void addInvalidConstruct(Construct construct, String message, Map<Construct, Set<ValidationResult>> invalidConstructs) throws TMCLValidatorException {

		logger.info("Add invalid construct: " + message);
		
		Set<ValidationResult> tmp = invalidConstructs.get(construct);
		if (tmp == null)
			tmp = new HashSet<ValidationResult>();
		tmp.add(new ValidationResult(this.id, message));
		invalidConstructs.put(construct, tmp);

	}

	/**
	 * Creates a set of constrained topic map constructs and the corresponding constraints.
	 * @param topicMap - The topic map which should be evaluated.
	 * @param associationTypeSubjectLocator - The subject identifier which specifies the association type over which the constrained construct and the constraint instance is connected.
	 * @param constraintTypeSubjectLocator - The subject identifier of the constraint type.
	 * @return A map of types and the corresponding constraints.
	 * @throws TMCLValidatorException
	 */
	protected Map<Topic, Set<IConstraint> > getConstructTypesAndConstraints(TopicMap topicMap, String associationTypeSubjectLocator, String constraintTypeSubjectLocator) throws TMCLValidatorException {

		Map<Topic, Set<IConstraint> > result = new HashMap<Topic, Set<IConstraint>>();
		
		Topic associationType = topicMap.getTopicBySubjectIdentifier(topicMap.createLocator(associationTypeSubjectLocator));
		Topic constraintType = topicMap.getTopicBySubjectIdentifier(topicMap.createLocator(constraintTypeSubjectLocator));
		
		if (associationType == null)
			throw new TMCLValidatorException("Construct statement type is null.");
		
		if (constraintType == null)
			throw new TMCLValidatorException("Constraint type is null.");

		// get type instance index
		TypeInstanceIndex typeInstanceIndex = constraintType.getTopicMap().getIndex(TypeInstanceIndex.class);

		// get constraint instances
		Collection<Topic> constraints = typeInstanceIndex.getTopics(constraintType);

		for (Topic constraint : constraints) {
			// create constraint//-
			IConstraint newConstraint = getConstraint(constraintType);

			// get constraint parameter
			newConstraint.getParameter(constraint);

			// get statement
			Topic constrainedType = Utils.getCounterPlayer(constraint, associationType);

			// add constraint
			addConstraint(result, constrainedType, newConstraint);
		}
		
		return result;
	}
	
	/**
	 * Gets a list of topics and the corresponding constraints of a specific type.
	 * @param topicMap - The topic map.
	 * @param associationTypeSubjectLocator - The subject identifier which specifies the association type over which the constrained construct and the constraint instance is connected.
	 * @param constraintTypeSubjectLocator - The subject identifier of the constraint type.
	 * @return A map of topics and the corresponding constraints.
	 * @throws TMCLValidatorException
	 */
	protected Map<Topic, Set<IConstraint> >  getTopicsAndConstraints(TopicMap topicMap, String associationTypeSubjectLocator, String constraintTypeSubjectLocator) throws TMCLValidatorException{
		
		Map<Topic, Set<IConstraint> >  result = new HashMap<Topic, Set<IConstraint>>();
		
		// get types and constraints
		Map<Topic, Set<IConstraint> >  typesAndConstraints = getConstructTypesAndConstraints(topicMap, associationTypeSubjectLocator, constraintTypeSubjectLocator);
		
		if(typesAndConstraints.isEmpty())
			return result;

		// get all topics which are instances of those types
		Set<Topic> allTopics = getAllTopics(topicMap, typesAndConstraints.keySet());
						
		for(Topic topic:allTopics){
			
			Set<IConstraint> constraints = new HashSet<IConstraint>();
			
			for(Topic type:topic.getTypes()){
				if(typesAndConstraints.get(type) != null)
					constraints.addAll(typesAndConstraints.get(type));
			}
			
			result.put(topic, constraints);
		}
		
		return result;
	}
	
	/**
	 * Returns all topics witch have at least one of the types specified in a set.
	 * @param mergedTopicMap - The topic map.
	 * @param types - The set of topic types.
	 * @return A set of topics.
	 */
    private Set<Topic> getAllTopics(TopicMap mergedTopicMap, Set<Topic> types){

    	
    	if(types == null || types.isEmpty())
    		return Collections.emptySet();
    	
    	TypeInstanceIndex typeInstanceIndex = mergedTopicMap.getIndex(TypeInstanceIndex.class);
    	
    	Set<Topic> result = new HashSet<Topic>();
    	
    	for(Topic type:types){
    		Collection<Topic> topics = typeInstanceIndex.getTopics(type);
    		
    		for(Topic topic:topics)
    			result.add(topic);
    	}

    	return result;
    }

	/**
	 * Creates and returns an constraint wrapper object according to the constraint type.
	 * @param constraintType - The constraint type.
	 * @return The constraint wrapper.
	 * @throws TMCLValidatorException
	 */
	private IConstraint getConstraint(Topic constraintType) throws TMCLValidatorException {
		if (constraintType.getSubjectIdentifiers().contains(constraintType.getTopicMap().createLocator(ABSTRACT_TOPIC_TYPE_CONSTRAINT)))
			return new AbstractTopicTypeConstraint();

		if (constraintType.getSubjectIdentifiers().contains(constraintType.getTopicMap().createLocator(SUBJECT_IDENTIFIER_CONSTRAINT)))
			return new SubjectIdentifierConstraint();

		if (constraintType.getSubjectIdentifiers().contains(constraintType.getTopicMap().createLocator(SUBJECT_LOCATOR_CONSTRAINT)))
			return new SubjectLocatorConstraint();

		if (constraintType.getSubjectIdentifiers().contains(constraintType.getTopicMap().createLocator(TOPIC_NAME_CONSTRAINT)))
			return new TopicNameConstraint();

		if (constraintType.getSubjectIdentifiers().contains(constraintType.getTopicMap().createLocator(TOPIC_OCCURRENCE_CONSTRAINT)))
			return new TopicOccurrenceConstraint();

		if (constraintType.getSubjectIdentifiers().contains(constraintType.getTopicMap().createLocator(TOPIC_ROLE_CONSTRAINT)))
			return new TopicRoleConstraint();

		if (constraintType.getSubjectIdentifiers().contains(constraintType.getTopicMap().createLocator(SCOPE_CONSTRAINT)))
			return new ScopeConstraint();

		if (constraintType.getSubjectIdentifiers().contains(constraintType.getTopicMap().createLocator(SCOPE_REQUIRED_CONSTRAINT)))
			return new ScopeRequiredConstraint();

		if (constraintType.getSubjectIdentifiers().contains(constraintType.getTopicMap().createLocator(REIFIER_CONSTRAINT)))
			return new ReifierConstraint();

		if (constraintType.getSubjectIdentifiers().contains(constraintType.getTopicMap().createLocator(TOPIC_REIFIES_CONSTRAINT)))
			return new TopicReifiesConstraint();

		if (constraintType.getSubjectIdentifiers().contains(constraintType.getTopicMap().createLocator(ASSOCIATION_ROLE_CONSTRAINT)))
			return new AssociationRoleConstraint();

		if (constraintType.getSubjectIdentifiers().contains(constraintType.getTopicMap().createLocator(ROLE_COMBINATION_CONSTRAINT)))
			return new RoleCombinationConstraint();

		if (constraintType.getSubjectIdentifiers().contains(constraintType.getTopicMap().createLocator(OCCURRENCE_DATA_TYPE_CONSTRAINT)))
			return new OccurrenceDatatypeConstraint();

		if (constraintType.getSubjectIdentifiers().contains(constraintType.getTopicMap().createLocator(UNIQUE_VALUE_CONSTRAINT)))
			return new UniqueValueConstraint();

		if (constraintType.getSubjectIdentifiers().contains(constraintType.getTopicMap().createLocator(REGULAR_EXPRESSION_CONSTRAINT)))
			return new RegularExpressionConstraint();

		throw new TMCLValidatorException("Unknown constraint type: " + constraintType);
	}

	/**
	 * Adds a new constraint wrapper object to the constraint list of an specific topic map construct.
	 * @param constraints - The construct.
	 * @param constrainedType - The constraint type.
	 * @param newConstraint - The constraint wrapper.
	 */
	private void addConstraint(Map<Topic, Set<IConstraint>> constraints, Topic constrainedType, IConstraint newConstraint) {

		Set<IConstraint> tmpConstraintSet = constraints.get(constrainedType);

		if (tmpConstraintSet == null)
			tmpConstraintSet = new HashSet<IConstraint>();
		tmpConstraintSet.add(newConstraint);

		constraints.put(constrainedType, tmpConstraintSet);
	}

	/**
	 * Tries to get a suitable name for a specific topic.
	 * Returns a name if available, otherwise one of the identifier.
	 * If the useIdentifierInMessages flag is set, always an identifier will be returned.
	 * @param topic - The topic.
	 * @return The name string.
	 */
	protected String getBestName(Topic topic) {

		if(!this.useIdentifierInMessages){
		
			Set<Name> names = topic.getNames();
	
			if (!names.isEmpty())
				return names.iterator().next().getValue();
		}

		Set<Locator> si = topic.getSubjectIdentifiers();
		if (!si.isEmpty())
			return si.iterator().next().getReference();

		Set<Locator> sl = topic.getSubjectLocators();
		if (!sl.isEmpty())
			return sl.iterator().next().getReference();

		Set<Locator> ii = topic.getItemIdentifiers();
		if (!ii.isEmpty())
			return ii.iterator().next().getReference();

		return topic.getId();
	}

		
}
