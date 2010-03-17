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
import org.tmapi.core.Role;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicMap;
import org.tmapi.index.TypeInstanceIndex;

import de.topicmapslab.tmclvalidator.TMCLValidatorException;
import de.topicmapslab.tmclvalidator.ValidationResult;

/**
 * Validator for the type declarations.
 */
public class DeclarationConstraintsValidator extends AbstractTMAPIValidator {

	private static final String TOPIC_TYPE = "http://psi.topicmaps.org/tmcl/topic-type";
	private static final String NAME_TYPE = "http://psi.topicmaps.org/tmcl/name-type";
	private static final String OCCURRENCE_TYPE = "http://psi.topicmaps.org/tmcl/occurrence-type";
	private static final String ASSOCIATION_TYPE = "http://psi.topicmaps.org/tmcl/association-type";
	private static final String ROLE_TYPE = "http://psi.topicmaps.org/tmcl/role-type";
	
	private static final String SUBTYPE = "http://psi.topicmaps.org/iso13250/model/subtype";
	private static final String SUPERTYPE = "http://psi.topicmaps.org/iso13250/model/supertype";
	private static final String SUPERTYPE_SUBTYPE = "http://psi.topicmaps.org/iso13250/model/supertype-subtype";
	
	private static final String TMCL_PREFIX = "http://psi.topicmaps.org/tmcl/";
	private static final String DATAMODEL_PREFIX = "http://psi.topicmaps.org/iso13250/model/";
	
	/**
	 * Constructor
	 * @param id - The validator ID.
	 */
	public DeclarationConstraintsValidator(String id, boolean useIdentifierInMessages) {
	    super(id, useIdentifierInMessages);
    }
	
	public void validate(TopicMap mergedTopicMap, Map<Construct, Set<ValidationResult>> invalidConstructs) throws TMCLValidatorException 
	{
		TypeInstanceIndex typeInstanceIndex = mergedTopicMap.getIndex(TypeInstanceIndex.class);
		
		Topic topicType = mergedTopicMap.getTopicBySubjectIdentifier(mergedTopicMap.createLocator(TOPIC_TYPE));
		Topic nameType = mergedTopicMap.getTopicBySubjectIdentifier(mergedTopicMap.createLocator(NAME_TYPE));
		Topic occurrenceType = mergedTopicMap.getTopicBySubjectIdentifier(mergedTopicMap.createLocator(OCCURRENCE_TYPE));
		Topic associationType = mergedTopicMap.getTopicBySubjectIdentifier(mergedTopicMap.createLocator(ASSOCIATION_TYPE));
		Topic roleType = mergedTopicMap.getTopicBySubjectIdentifier(mergedTopicMap.createLocator(ROLE_TYPE));

		// check topic types
		for(Topic type:typeInstanceIndex.getTopicTypes())
		{
			if(!isTMCL(type) && !isDataModel(type))
			{
				if(!checkTopicType(type, topicType))
				{
					addInvalidConstruct(type, "Topic must be of type " + TOPIC_TYPE + ".", invalidConstructs);
				}
			}
		}
		
		// check name types
		for(Topic type:typeInstanceIndex.getNameTypes())
		{
			if(!isTMCL(type) && !isDataModel(type))
			{
				if(!checkTopicType(type, nameType))
				{
					addInvalidConstruct(type, "Topic must be of type " + NAME_TYPE + ".", invalidConstructs);
				}
			}
		}
		
		// check occurrence types
		for(Topic type:typeInstanceIndex.getOccurrenceTypes())
		{
			if(!isTMCL(type) && !isDataModel(type))
			{
				if(!checkTopicType(type, occurrenceType))
				{
					addInvalidConstruct(type, "Topic must be of type " + OCCURRENCE_TYPE + ".", invalidConstructs);
				}
			}
		}
		
		// check association types
		for(Topic type:typeInstanceIndex.getAssociationTypes())
		{
			if(!isTMCL(type) && !isDataModel(type))
			{
				if(!checkTopicType(type, associationType))
				{
					addInvalidConstruct(type, "Topic must be of type " + ASSOCIATION_TYPE + ".", invalidConstructs);
				}
			}
		}
		
		// check role types
		for(Topic type:typeInstanceIndex.getRoleTypes())
		{
			if(!isTMCL(type) && !isDataModel(type))
			{
				if(!checkTopicType(type, roleType))
				{
					addInvalidConstruct(type, "Topic must be of type " + ROLE_TYPE + ".", invalidConstructs);
				}
			}
		}

	}
	
	private boolean checkTopicType(Topic topic, Topic type)
	{
		if(type == null)
		{
			return false;
		}

		if(topic.getTypes().contains(type))
			return true;
		
		Topic subType = topic.getTopicMap().getTopicBySubjectIdentifier(topic.getTopicMap().createLocator(SUBTYPE));
		if(subType == null)
		{
			return false;
		}
		
		Topic superType = topic.getTopicMap().getTopicBySubjectIdentifier(topic.getTopicMap().createLocator(SUPERTYPE));
		if(superType == null)
		{
			return false;
		}
		
		Topic superTypeSubtype = topic.getTopicMap().getTopicBySubjectIdentifier(topic.getTopicMap().createLocator(SUPERTYPE_SUBTYPE));
		if(superTypeSubtype == null)
		{
			return false;
		}

		for(Role subtype:topic.getRolesPlayed(subType))
		{
			Association supertype_subtype = subtype.getParent();
			
			if(supertype_subtype.getType().equals(superTypeSubtype))
			{
				Set<Role> supertypes = supertype_subtype.getRoles(superType);
				
				for(Role supertype:supertypes)
				{
					if(checkTopicType(supertype.getPlayer(), type))
						return true;
				}
			}
		}

		return false;
	}
	
	private boolean isTMCL(Topic topic)
	{
		Set<org.tmapi.core.Locator> subjectIdentifiers = topic.getSubjectIdentifiers();
		
		if(subjectIdentifiers.isEmpty())
		{
			return false;
		}
		
		for(org.tmapi.core.Locator si:subjectIdentifiers)
		{
			if(si.toExternalForm().startsWith(TMCL_PREFIX))
			{
				return true;
			}
				
		}

		return false;
	}
	
	private boolean isDataModel(Topic topic)
	{
		Set<org.tmapi.core.Locator> subjectIdentifiers = topic.getSubjectIdentifiers();
		
		if(subjectIdentifiers.isEmpty())
		{
			return false;
		}
		
		for(org.tmapi.core.Locator si:subjectIdentifiers)
		{
			if(si.toExternalForm().startsWith(DATAMODEL_PREFIX))
			{
				return true;
			}
		}

		return false;
	}
	
	
}
