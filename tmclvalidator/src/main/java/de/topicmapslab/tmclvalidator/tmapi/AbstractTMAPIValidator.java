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
import org.tmapi.core.Association;
import org.tmapi.core.Construct;
import org.tmapi.core.Locator;
import org.tmapi.core.Name;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Role;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicMap;
import org.tmapi.index.LiteralIndex;
import org.tmapi.index.TypeInstanceIndex;

import de.topicmapslab.tmclvalidator.IConstraintValidator;
import de.topicmapslab.tmclvalidator.TMCLValidatorException;
import de.topicmapslab.tmclvalidator.ValidationResult;
import de.topicmapslab.tmclvalidator.tmapi.constraint.AbstractTopicTypeConstraint;
import de.topicmapslab.tmclvalidator.tmapi.constraint.AssociationRoleConstraint;
import de.topicmapslab.tmclvalidator.tmapi.constraint.IConstraint;
import de.topicmapslab.tmclvalidator.tmapi.constraint.ItemIdentifierConstraint;
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
import de.topicmapslab.tmclvalidator.tmapi.constraint.VariantNameConstraint;
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
	private static final String VARIANT_NAME_CONSTRAINT = "http://psi.topicmaps.org/tmcl/variant-name-constraint";
	private static final String ITEM_IDENTIFIER_CONSTRAINT = "http://psi.topicmaps.org/tmcl/item-identifier-constraint";


	private Topic supertype_subtype;
	private Topic supertype;
	private Topic subtype;
	
	private TypeInstanceIndex typeInstanceIndex;
	private LiteralIndex literalIndex;
	
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
     * Returns constraints and related constrained types. 
     * @param topicMap - The topic map.
     * @param associationTypeSubjectLocator - The subject identifier which specifies the association type over which the constrained construct and the constraint instance is connected.
	 * @param constraintTypeSubjectLocator - The subject identifier of the constraint type.
     * @return Map containing the constraints and the related types.
     * @throws TMCLValidatorException
     */
    protected Map<IConstraint, Topic> getConstraintsAndTypes(TopicMap topicMap, String associationTypeSubjectLocator, String constraintTypeSubjectLocator) throws TMCLValidatorException{
    	
    	Topic associationType = topicMap.getTopicBySubjectIdentifier(topicMap.createLocator(associationTypeSubjectLocator));
    	Topic constraintType = topicMap.getTopicBySubjectIdentifier(topicMap.createLocator(constraintTypeSubjectLocator));
    	
    	if (associationType == null)
			throw new TMCLValidatorException("Construct statement type is null.");
		
		if (constraintType == null)
			throw new TMCLValidatorException("Constraint type is null.");
    	
		// get type instance index
		TypeInstanceIndex typeInstanceIndex = getTypeInstanceIndex(constraintType.getTopicMap());
			
		// get constraint instances
		Collection<Topic> constraints = typeInstanceIndex.getTopics(constraintType);
		
		// create result map
		
		Map<IConstraint, Topic> result = new HashMap<IConstraint, Topic>();
		
		for (Topic constraint : constraints) {
			// create constraint//-
			IConstraint newConstraint = getConstraint(constraintType);

			// get constraint parameter
			newConstraint.getParameter(constraint);

			// get statement
			Topic constrainedType = Utils.getCounterPlayer(constraint, associationType);

			result.put(newConstraint, constrainedType);
		}
		
		
    	return result;
    }

    
    /**
     * Returns occurrences of a specific type. 
     * @param type - The occurrence type.
     * @return A set of occurrences. May be empty but never null.
     */
    protected Set<Occurrence> getOccurrences(Topic type){
    	
    	Set<Occurrence> result = new HashSet<Occurrence>();
    	Set<Topic> transientTypes = getTransientTypes(type);
    	
    	TypeInstanceIndex typeInstanceIndex = getTypeInstanceIndex(type.getTopicMap());
    	
    	for(Topic transientType:transientTypes){
    		
    		Collection<Occurrence> occurrences = typeInstanceIndex.getOccurrences(transientType);
    		result.addAll(occurrences);
    	}
  	
    	return result;
    }
    
    /**
     * Returns names of a specific type. 
     * @param type - The name type.
     * @return A set of names. May be empty but never null.
     */
    protected Set<Name> getNames(Topic type){
    	
    	Set<Name> result = new HashSet<Name>();
    	Set<Topic> transientTypes = getTransientTypes(type);
    	
    	TypeInstanceIndex typeInstanceIndex = getTypeInstanceIndex(type.getTopicMap());
    	
    	for(Topic transientType:transientTypes){
    		
    		Collection<Name> names = typeInstanceIndex.getNames(transientType);
    		result.addAll(names);
    	}
  	
    	return result;
    }
    
    /**
     * Returns associations of a specific type. 
     * @param type - The association type.
     * @return A set of associations. May be empty but never null.
     */
    protected Set<Association> getAssociations(Topic type){
    	
    	Set<Association> result = new HashSet<Association>();
    	Set<Topic> transientTypes = getTransientTypes(type);
    	
    	TypeInstanceIndex typeInstanceIndex = getTypeInstanceIndex(type.getTopicMap());
    	
    	for(Topic transientType:transientTypes){
    		
    		Collection<Association> associations = typeInstanceIndex.getAssociations(transientType);
    		result.addAll(associations);
    	}
    	
    	return result;
    }
    
    /**
     * Returns topics of a specific type. 
     * @param type - The topic type.
     * @return A set of topics. May be empty but never null.
     */
    protected Set<Topic> getTopics(Topic type){
    	
    	Set<Topic> result = new HashSet<Topic>();
    	Set<Topic> transientTypes = getTransientTypes(type);
    	
    	TypeInstanceIndex typeInstanceIndex = getTypeInstanceIndex(type.getTopicMap());
    	
    	for(Topic transientType:transientTypes){
    		
    		Collection<Topic> topics = typeInstanceIndex.getTopics(transientType);
    		result.addAll(topics);
    	}
    	
    	return result;
    	
    }
    
    /**
     * Returns roles of a specific type. 
     * @param type - The topic type.
     * @return A set of topics. May be empty but never null.
     */
    protected Set<Role> getRoles(Topic type){
    	
    	Set<Role> result = new HashSet<Role>();
    	Set<Topic> transientTypes = getTransientTypes(type);
    	
    	TypeInstanceIndex typeInstanceIndex = getTypeInstanceIndex(type.getTopicMap());
    	
    	for(Topic transientType:transientTypes){
    		
    		Collection<Role> roles = typeInstanceIndex.getRoles(transientType);
    		result.addAll(roles);
    	}
    	
    	return result;
    	
    }
    
    
    /**
     * Returns topic names of a specific type. 
     * @param topic - The topic to which the names belong.
     * @param type - The name type.
     * @return Set of names. May be empty but never null.
     */
    protected Set<Name> getTopicNames(Topic topic, Topic type){
		
		Set<Name> names = topic.getNames();
		Set<Name> result = new HashSet<Name>();
		
		for(Name n:names)
			if(Utils.hasType(n, type))
				result.add(n);
		
		return result;
	}
	
    /**
     * Returns topic occurrences of a specific type. 
     * @param topic - The topic to which the occurrences belong.
     * @param type - The occurrence type.
     * @return Set of occurrences. May be empty but never null.
     */
	protected Set<Occurrence> getTopicOccurrences(Topic topic, Topic type){
	
		Set<Occurrence> occurrences = topic.getOccurrences();
		Set<Occurrence> result = new HashSet<Occurrence>();
		
		for(Occurrence o:occurrences)
			if(Utils.hasType(o, type))
				result.add(o);
		
		return result;
	}
	
	/**
	 * Returns topic roles of a specific type. 
	 * @param topic - The topic to which the roles belong.
	 * @param type - The role type.
	 * @return Set of roles. May be empty but never null.
	 */
	protected Set<Role> getTopicRoles(Topic topic, Topic type){
		
		Set<Role> roles = topic.getRolesPlayed();
		Set<Role> result = new HashSet<Role>();
		
		for(Role r:roles)
			if(Utils.hasType(r, type))
				result.add(r);
		
		return result;
	}
	
	/**
	 * Returns topic roles of a specific type which are part of a specific association type.
	 * @param topic - The topic to which the roles belong.
	 * @param roleType - The role type.
	 * @param associationType - The association type.
	 * @return Set of roles. May be empty but never null.
	 */
	protected Set<Role> getTopicRolesByAssociationType(Topic topic, Topic roleType, Topic associationType){
		
		Set<Role> roles = getTopicRoles(topic, roleType);
		Set<Role> result = new HashSet<Role>();
		
		for(Role r:roles)
			if(Utils.hasType(r.getParent(), associationType))
				result.add(r);
		
		return result;
	}
	
	/**
	 * Returns associations of a specific type belonging to a specific topic.
	 * @param topic - The topic to which the association belongs.
	 * @param type - The association type.
	 * @return Set of associations. May be empty but never null.
	 */
	protected Set<Association> getTopicAssociations(Topic topic, Topic type){

		Set<Association> all = new HashSet<Association>();
		Set<Association> result = new HashSet<Association>();
		
		for(Role r:topic.getRolesPlayed())
			all.add(r.getParent());
		
		for(Association a:all)
			if(Utils.hasType(a, type))
				result.add(a);
		
		return result;
		
	}
    
    
    /**
     * Returns all subtypes for the specific supertype, the supertype included, i.e. the whole hierarchy
     * @param superType
     * @return Set of topics.
     */
    private Set<Topic> getTransientTypes(Topic superType){
    	
    	Set<Topic> transientTypes = new HashSet<Topic>();
    	Set<Topic> allreadyCheckedTypes = new HashSet<Topic>();
    	
    	getAllSubtypes(superType, transientTypes, allreadyCheckedTypes);
    	
    	return transientTypes;
    }
    
    /**
     * Returns all subtypes of a topic.
     * @param superType - The supertype.
     * @param subTypes - Set for the subtypes.
     * @param allreadyCheckedTypes - Set of allready checked types.
     */
    private void getAllSubtypes(Topic superType, Set<Topic> subTypes, Set<Topic> allreadyCheckedTypes){
    	
    	subTypes.add(superType); // add this type
    	
    	if(allreadyCheckedTypes.contains(superType)) // return if already checked
    		return;
    	    	
    	allreadyCheckedTypes.add(superType);
    	
    	Set<Topic> subtypes = getSubtypes(superType);
    	
    	for(Topic subtype:subtypes)
    		if(!allreadyCheckedTypes.contains(subtype))
    			getAllSubtypes(subtype, subTypes, allreadyCheckedTypes);
    	
    	
    }
    
    /**
     * Returns the direct subtypes of a topic.
     * @param superType - The topic acting as supertype.
     * @return Set of topics.
     */
    @SuppressWarnings("unchecked")
    private Set<Topic> getSubtypes(Topic superType){

    	if(this.supertype_subtype == null)
    		this.supertype_subtype = superType.getTopicMap().getTopicBySubjectIdentifier(superType.getTopicMap().createLocator("http://psi.topicmaps.org/iso13250/model/supertype-subtype"));
    	
    	if(this.supertype_subtype == null)
    		return Collections.emptySet(); // no supertype subtype association exist
    	    	
    	
    	if(this.supertype == null)
    		this.supertype = superType.getTopicMap().getTopicBySubjectIdentifier(superType.getTopicMap().createLocator("http://psi.topicmaps.org/iso13250/model/supertype"));
    	
    	if(this.supertype == null)
    		return Collections.emptySet();
    	
    	if(this.subtype == null)
    		this.subtype = superType.getTopicMap().getTopicBySubjectIdentifier(superType.getTopicMap().createLocator("http://psi.topicmaps.org/iso13250/model/subtype"));
    	
    	if(this.subtype == null)
    		return Collections.emptySet();
    	
    	/// TODO
    	
    	Set<Topic> result = new HashSet<Topic>();
    	
    	Set<Role> supertypeRoles = superType.getRolesPlayed(this.supertype);
    	
    	for(Role r:supertypeRoles){
    		
    		if(r.getParent().getType().equals(this.supertype_subtype) && r.getParent().getRoles().size() == 2){
    			
    			for(Role r2:r.getParent().getRoles()){
    				
    				if(!r2.equals(r) && r2.getType().equals(this.subtype))
    					result.add(r2.getPlayer());
    			}
    		}
    	}
    	
    	return result;

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
		TypeInstanceIndex typeInstanceIndex = getTypeInstanceIndex(constraintType.getTopicMap());

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
		
		if (constraintType.getSubjectIdentifiers().contains(constraintType.getTopicMap().createLocator(VARIANT_NAME_CONSTRAINT)))
			return new VariantNameConstraint();
		
		if (constraintType.getSubjectIdentifiers().contains(constraintType.getTopicMap().createLocator(ITEM_IDENTIFIER_CONSTRAINT)))
			return new ItemIdentifierConstraint();

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

	protected TypeInstanceIndex getTypeInstanceIndex(TopicMap topicMap) {
		if (typeInstanceIndex == null) {
			typeInstanceIndex = topicMap.getIndex(TypeInstanceIndex.class);
			if (!typeInstanceIndex.isOpen()) {
				typeInstanceIndex.open();
			}
		}
		return typeInstanceIndex;
	}
	
	protected LiteralIndex getLiteralIndex(TopicMap topicMap) {
		if (literalIndex == null) {
			literalIndex = topicMap.getIndex(LiteralIndex.class);
			if (!literalIndex.isOpen()) {
				literalIndex.open();
			}
		}
		return literalIndex;
	}
}
