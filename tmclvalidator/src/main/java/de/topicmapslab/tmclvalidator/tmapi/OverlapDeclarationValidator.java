/**
 * Copyright: Copyright 2010 Topic Maps Lab, University of Leipzig. http://www.topicmapslab.de/    
 * License:   Apache License, Version 2.0 http://www.apache.org/licenses/LICENSE-2.0.html
 * 
 * @author Christian Haß
 */
package de.topicmapslab.tmclvalidator.tmapi;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.tmapi.core.Association;
import org.tmapi.core.Construct;
import org.tmapi.core.Role;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicMap;
import org.tmapi.index.TypeInstanceIndex;

import de.topicmapslab.tmclvalidator.TMCLValidatorException;
import de.topicmapslab.tmclvalidator.ValidationResult;

/**
 * Validator for the overlap declaration.
 */
public class OverlapDeclarationValidator extends AbstractTMAPIValidator {

	private static final String SUBTYPE = "http://psi.topicmaps.org/iso13250/model/subtype";
	private static final String SUPERTYPE = "http://psi.topicmaps.org/iso13250/model/supertype";
	private static final String SUPERTYPE_SUBTYPE = "http://psi.topicmaps.org/iso13250/model/supertype-subtype";
	
	private static final String OVERLAP_DECLARATION = "http://psi.topicmaps.org/tmcl/overlap-declaration";
	private static final String OVERLAPS = "http://psi.topicmaps.org/tmcl/overlaps";
	
	private Set<Set<Topic>> cache;
	
	private TopicMap topicMap = null;
	private Topic superType = null;
	private Topic subType = null;
	private Topic superTypeSubType = null;
	
	/**
	 * Constructor
	 * @param id - The validator ID.
	 * @param useIdentifierInMessages - Forces the usage of identifier in result messages.
	 */
	public OverlapDeclarationValidator(String id, boolean useIdentifierInMessages) {
	    super(id, useIdentifierInMessages);
    	cache = new HashSet<Set<Topic>>();
	}
	
	public void validate(TopicMap mergedTopicMap, Map<Construct, Set<ValidationResult>> invalidConstructs) throws TMCLValidatorException 
	{
		// get overlay constraints
		this.topicMap = mergedTopicMap;
		
		Set<Set<Topic>> overlays = new HashSet<Set<Topic>>();
		
		TypeInstanceIndex typeInstanceIndex = getTypeInstanceIndex(mergedTopicMap);

		Topic constraintType = mergedTopicMap.getTopicBySubjectIdentifier(mergedTopicMap.createLocator(OVERLAP_DECLARATION));
		
		if(constraintType != null)
		{
			Collection<Topic> overlapDeclarations = typeInstanceIndex.getTopics(constraintType);
			
			for(Topic overlapDeclaration:overlapDeclarations)
			{
				Set<Topic> overlappingTypes = getOverlappingTypes(overlapDeclaration);
				
				if(overlappingTypes.size() != 2) 
					throw new TMCLValidatorException("Number of types specified by a overlap declaration is unequal two.");

				overlays.add(overlappingTypes);
			}
		}
		
		// get all topics
		Set<Topic> topics = mergedTopicMap.getTopics();
		
		for(Topic topic:topics)
		{
			if(topic.getTypes().size() > 1)
			{
				if(!isValid(topic, overlays))
					addInvalidConstruct(topic, "Topic has more then one type.", invalidConstructs);
			}
		}
		
	}
	
	/**
	 * Checks if a multi typed topic is valid according to the overlap constraint.
	 * @param topic - The topic
	 * @param overlays - Set of overplapping topic types.
	 * @return True in case of yes, otherwise false.
	 * @throws TMCLValidatorException
	 */
	private boolean isValid(Topic topic, Set<Set<Topic>> overlays) throws TMCLValidatorException
	{

		// check all combinations
		Topic[] types_array = topic.getTypes().toArray(new Topic[0]);
		
		for(int t1=0;t1<types_array.length;t1++)
		{
			for(int t2=t1+1;t2<types_array.length;t2++)
			{
				// first check if a overlap declaration exist
				if(overlayExist(types_array[t1], types_array[t2], overlays))
				{
										
				}else if(!isSuperTypeSubType(types_array[t1], types_array[t2])) // check if an supertype subtype relationship exists
				{
					return false;
				}
			}
		}
		
		return true;
	}
	
	/**
	 * Gets the overlapping types specified by an overlap constraint.
	 * @param overlapDeclaration - The constraint.
	 * @return - Set of types.
	 */
	private Set<Topic> getOverlappingTypes(Topic overlapDeclaration){
		
		Set<Topic> topics = new HashSet<Topic>();
		
		Set<Role> roles = overlapDeclaration.getRolesPlayed();

		for(Role role:roles)
		{

			if(role.getParent().getType().equals(overlapDeclaration.getTopicMap().getTopicBySubjectIdentifier(overlapDeclaration.getTopicMap().createLocator(OVERLAPS))))
			{
				Set<Role> allRoles = role.getParent().getRoles();
				
				for(Role associationRole:allRoles)
				{
					if(!associationRole.getPlayer().equals(overlapDeclaration))
					{
						topics.add(associationRole.getPlayer());
					}
				}
			}
		}
		
		return topics;
	}
	
	/**
	 * Checks if two types are allowed to overlap.
	 * @param type1 - Type one.
	 * @param type2 - Type two.
	 * @param overlays - Defined overlays.
	 * @return True in case of yes, otherwise false.
	 */
	private boolean overlayExist(Topic type1, Topic type2, Set<Set<Topic>> overlays)
	{
		for(Set<Topic> set:overlays)
		{
			if(set.contains(type1) && set.contains(type2))
			{
				return true;
			}
		}
		
		return false;
	}

	/**
	 * Checks if two types are supertype-subtype related.
	 * @param type1 - Type one.
	 * @param type2 - Type two.
	 * @return True in case of yes, otherwise false.
	 * @throws TMCLValidatorException
	 */
 	private boolean isSuperTypeSubType(Topic type1, Topic type2) throws TMCLValidatorException
	{
		if(isSupertype(type1, type2) || isSupertype(type2, type1))
		{
			Set<Topic> tmp = new HashSet<Topic>();
			tmp.add(type1);
			tmp.add(type2);
			this.cache.add(tmp);
			return true;
		}
		
		return false;
	}
	
 	/**
 	 * Checks if a topic is supertype of an other topic.
 	 * @param supertype - The topic supposed to be the supertype.
 	 * @param subtype - The topic supposed to be the subtype.
 	 * @return True in case of yes, otherwise false.
 	 * @throws TMCLValidatorException
 	 */
	private boolean isSupertype(Topic supertype, Topic subtype) throws TMCLValidatorException
	{
		
		if(isCached(supertype, subtype)) // try to find the two types in cache 
		{
			return true;
		}
		
		if(getSuperType() == null)
			return false;

		if(getSubType() == null)
			return false;

		if(getSuperTypeSubType() == null)
			return false;
		

		// get subtype roles
		for(Role subtypeRole:subtype.getRolesPlayed(this.subType))
		{
			Association supertype_subtype = subtypeRole.getParent();
			
			// check association type
			if(supertype_subtype.getType().equals(this.superTypeSubType))
			{
				// iterate through supertype player
				for(Role supertypeRole:supertype_subtype.getRoles(this.superType))
				{
					// check counterplayer
					if(supertypeRole.getPlayer().equals(supertype))
						return true;

					// call recursive
					if(isSupertype(supertype, supertypeRole.getPlayer()))
					{
						return true;
					}
				}
			}
		}

		return false;
	}

	/// TODO improve caching
	private boolean isCached(Topic type1, Topic type2)
	{
		
		for(Set<Topic> set:cache)
		{
			if(set.contains(type1) && set.contains(type2))
			{
				return true;
			}
		}
		return false;
	}
	
	private Topic getSuperType() throws TMCLValidatorException
	{
		if(this.topicMap == null)
			throw new TMCLValidatorException("Topic Map is null.");
		
		if(this.superType == null)
		{
			this.superType = this.topicMap.getTopicBySubjectIdentifier(this.topicMap.createLocator(SUPERTYPE));
		}
		
		return this.superType;
	}
	
	private Topic getSubType() throws TMCLValidatorException
	{
		if(this.topicMap == null)
			throw new TMCLValidatorException("Topic Map is null.");
		
		if(this.subType == null)
		{
			this.subType = this.topicMap.getTopicBySubjectIdentifier(this.topicMap.createLocator(SUBTYPE));
		}
		
		return this.subType;
	}
	
	private Topic getSuperTypeSubType() throws TMCLValidatorException
	{
		if(this.topicMap == null)
			throw new TMCLValidatorException("Topic Map is null.");
		
		if(this.superTypeSubType == null)
		{
			this.superTypeSubType = this.topicMap.getTopicBySubjectIdentifier(this.topicMap.createLocator(SUPERTYPE_SUBTYPE));
		}
		
		return this.superTypeSubType;
	}
	
}
